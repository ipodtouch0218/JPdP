package me.ipodtouch0218.panels.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import me.ipodtouch0218.java2dengine.GameEngine;
import me.ipodtouch0218.java2dengine.input.InputHandler;
import me.ipodtouch0218.java2dengine.object.GameObject;
import me.ipodtouch0218.panels.BlockType;
import me.ipodtouch0218.panels.util.MiscUtils;

public class ObjPonBoard extends GameObject {

	public static final int blockScale = 32;
	private static final int matchMinSize = 3;

	private int width = 6, height = 12;
	
	BlockType[][] blocks;
	double[] rowGravityTimer;
	
	double boardScroll = 0;
	private double scrollSpeed = 0.1; //measured in blocks per second
	private boolean scroll = true, manualScrolling = false, matching = false;
	private double scrollDelay = 0;
	
	private int cursorX = (width/2)-1;
	private int cursorY = height/2;
	
	private boolean swapping;
	private double swappingTimer;
	
	private boolean leftPressed, rightPressed, upPressed, downPressed, rotPressed, scrollPressed;
	
	public ObjPonBoard(int width, int height) {
		this.width = width;
		this.height = height;
		
		blocks = new BlockType[width][height+1];
		rowGravityTimer = new double[width];
		
		cursorX = (width/2)-1;
		cursorY = height/2;
		
		//TODO: generate board without matches
		
		fillBoard();
		
		
	}
	
	private void fillBoard() {
		//fill lowest 6
		for (int x = 0; x < width; x++) {
			blocks[x][height] = BlockType.randomBlock();
		}
		
		//Boards start with 30 tiles, none touching
		for (int count = 0; count < 30; count++) {
			
			int x = 0, y = 0;
			while (y > (height/2)) {
				x = (int) (Math.random()*blocks.length);
				y = getTopBlockPos(x)-1;
			}
			BlockType random = null;
			while (true) {
				random = BlockType.randomBlock();
				
				if (y < height-1 && blocks[x][y+1] == random) { continue; } //matches block below
				if (x > 0 && blocks[x-1][y] == random) { continue; } //matches block on the left
				if (x < width-1 && blocks[x+1][y] == random) { continue; } //matches block on the right
				break;
			}
			blocks[x][y] = random;
		}
	}
	
	////
	
	@Override
	public void tick(double delta) {
		handleSwapping(delta);
		handleControls();
		handleScrolling(delta);
		handleGravity(delta);
	}
	
	private void handleSwapping(double delta) {
		if (!swapping) { return; }
		swappingTimer -= delta;
		if (swappingTimer < 0) {
			BlockType temp = blocks[cursorX][cursorY];
			blocks[cursorX][cursorY] = blocks[cursorX + 1][cursorY];
			blocks[cursorX + 1][cursorY] = temp;
			
			findAndClearMatches(cursorX, cursorY);
			findAndClearMatches(cursorX+1, cursorY);
			swapping = false;
		}
	}
	
	
		
	private void handleControls() {
		if (manualScrolling) { return; }
		if (swapping) { return; }
		boolean pressed = false, moved = false;
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_LEFT);
		if (pressed && !leftPressed) {
			cursorX -= 1;
			moved = true;
		}
		leftPressed = pressed;
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_RIGHT);
		if (pressed && !rightPressed) {
			cursorX += 1;
			moved = true;
		}
		rightPressed = pressed;
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_UP);
		if (pressed && !upPressed) {
			cursorY -= 1;
			moved = true;
		}
		upPressed = pressed;
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_DOWN);
		if (pressed && !downPressed) {
			cursorY += 1;
			moved = true;
		}
		downPressed = pressed;
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_ENTER);
		if (pressed && !scrollPressed && scrollDelay <= 0) {
			manualScrolling = true;
		}
		scrollPressed = pressed;
		
		cursorX = MiscUtils.limit(cursorX, 0, 4);
		cursorY = MiscUtils.limit(cursorY, (blocksAtTop() ? 0 : 1), 11);
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_A);
		if (pressed && !rotPressed && !moved) {
			swapBlocks(); 
		}
		rotPressed = pressed;
	}
	
	private void handleGravity(double delta) {
		for (int i = 0; i < rowGravityTimer.length; i++) {
			boolean timerstarted = false;
			if (rowGravityTimer[i] > 0) {
				rowGravityTimer[i] -= delta;
				timerstarted = true;
			} else {
				int startingY = checkForGravity(i);
				if (startingY != -1) {
					rowGravityTimer[i] = 0.1;
				}
			}
			
			//start blocks falling
			if (rowGravityTimer[i] < 0 && timerstarted) {
				GameEngine.addGameObject(new ObjFallingBlocks(this, i, checkForGravity(i)));
			}
		}
	}
	
	private int checkForGravity(int x) {
		for (int y = blocks[x].length-2; y >= 0; y--) {
			if (blocks[x][y] != null && blocks[x][y+1] == null) {
				//this block needs to fall
				return y;
			}
		}
		return -1;
	}
	
	
	private void swapBlocks() {
		if (swapping) { return; }
		if (blocks[cursorX][cursorY] == null && blocks[cursorX+1][cursorY] == null) { return; }
		if ((rowGravityTimer[cursorX] != 0 && blocks[cursorX][cursorY+1] == null) ||
			(rowGravityTimer[cursorX+1] != 0 && blocks[cursorX+1][cursorY+1] == null)) 
				{ return; }
		
		swapping = true;
		swappingTimer = 0.05;
	}
	
	private void handleScrolling(double delta) {
		if (matching) { return; }
		if (scrollDelay > 0) { scrollDelay-=delta; return; }
		if (!scroll && !manualScrolling) { return; }
		if (blocksAtTop()) { 
			manualScrolling = false;
			return; 
		}
		
		if (boardScroll < blockScale) {
			if (manualScrolling) {
				boardScroll += 8*blockScale*delta;
			} else {
				boardScroll += scrollSpeed*blockScale*delta;
			}
		}
		if (boardScroll >= blockScale) {
			boolean resetscroll = moveBlocksUp();
			if (resetscroll) {
				boardScroll %= blockScale;
			} else {
				boardScroll = blockScale;
			}
			manualScrolling = false;
		}
	}
	
	private boolean moveBlocksUp() {
		if (blocksAtTop()) { return false; }
		
		for (int xBlock = 0; xBlock < blocks.length; xBlock++) {
			boolean moved = false;
			BlockType below = null;
			for (int yBlock = blocks[xBlock].length - 1; yBlock >= 0; yBlock--) {
				if (below == null && moved) { continue; }
				
				BlockType toBeReplaced = blocks[xBlock][yBlock];
				blocks[xBlock][yBlock] = below;
				below = toBeReplaced; 
				
				moved = true;
			}
		}
		
		addNewBlockRow();
		
		for (int x = 0; x < blocks.length; x++) {
			findAndClearMatches(x, blocks[x].length-2);
		}
		
		cursorY -= 1;
		return true;
	}
	
	
	private void addNewBlockRow() {
		for (int i = 0; i < blocks.length; i++) {
			blocks[i][blocks[i].length-1] = BlockType.randomBlock();
			//TODO: make into a "next" queue list for multiplayer syncing.
		}
	}
	
	private boolean blocksAtTop() {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i][0] != null) { return true; }
		}
		return false;
	}
	
	////
	
	@Override
	public void render(Graphics2D g) {
		renderBlocks(g);
		renderCursor(g);
	}
	
	private void renderBlocks(Graphics2D g) {
		g.setClip((int) x-1, (int) y-2, blockScale*blocks.length+2, blockScale*(blocks[0].length-1)+2);
		
		for (int xBlock = 0; xBlock < blocks.length; xBlock++) {
			for (int yBlock = blocks[xBlock].length - 1; yBlock >= 0; yBlock--) {
				
				
				BlockType block = blocks[xBlock][yBlock];
				if (block == null) { continue; }
				
				g.setColor(block.color());
				if (yBlock >= blocks[xBlock].length - 1) {
					g.setColor(block.color().darker().darker());
				}
				
				double swapOffset = 0;
				if (swapping && yBlock == cursorY && ((xBlock == cursorX) || (xBlock == cursorX+1))) { 
					swapOffset = ((1-(swappingTimer/0.05))*blockScale);
					if (xBlock == cursorX+1) { swapOffset *= -1; }
				}
				
				g.fillRect((int) (x + (xBlock * blockScale) + 1 + swapOffset), (int) (y + (yBlock * blockScale) - boardScroll + 1), blockScale-2, blockScale-2);
			}
		}
	}
	
	private void renderCursor(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawRect((int) (x + (blockScale * cursorX-1)), (int) (y + (blockScale * cursorY-1) - boardScroll), (blockScale*2)+1, blockScale+1);
	}
	
	////
	
	
	//TODO: "swapper matches" to combine nearby matches (two 3 lengths = one 6 length)
	public void findAndClearMatches(int x, int y) {
		HashSet<Point> matches = getMatches(x,y);
		if (matches.isEmpty()) { return; }
		matches.forEach(p -> {
			blocks[p.x][p.y] = null;
		});
		scrollDelay = (matches.size()-matchMinSize)+1;
	}
	
	private HashSet<Point> getMatches(int x, int y) {
		HashSet<Point> finalMatches = new HashSet<>();
		
		BlockType baseBlock = blocks[x][y];
		if (baseBlock == null) { return finalMatches; }
		
		HashSet<Point> verticalMatches = adjacentMatches(x,y,0,1); //up matches
		verticalMatches.addAll(adjacentMatches(x,y,0,-1)); //down matches

		HashSet<Point> horizontalMatches = adjacentMatches(x,y,1,0); //right matches
		horizontalMatches.addAll(adjacentMatches(x,y,-1,0)); //left matches
		
		if (verticalMatches.size()+1 >= matchMinSize) {
			finalMatches.addAll(verticalMatches);
		}
		if (horizontalMatches.size()+1 >= matchMinSize) {
			finalMatches.addAll(horizontalMatches);
		}
		if (!finalMatches.isEmpty()) {
			finalMatches.add(new Point(x,y));
		}
		return finalMatches;
	}
	private HashSet<Point> adjacentMatches(int startx, int starty, int xstep, int ystep) {
		BlockType baseBlock = blocks[startx][starty];
		if (baseBlock == null) { return null; }
		HashSet<Point> matching = new HashSet<>();
		
		int i = 1;
		boolean matches = true;
		while (matches) {
			int newx = startx + (i*xstep);
			int newy = starty + (i*ystep);
			
			if (newx < 0 || newx >= blocks.length) { break; }
			if (newy < 0 || newy >= blocks[newx].length-1) { break; }
			
			matches = blocks[newx][newy] == baseBlock;
			if (matches) {
				matching.add(new Point(newx, newy));
			}
			i++;
		}
		return matching;
	}

	/// 
	
	public int getTopBlockPos(int row) {
		int exists = blocks[row].length-1;
		for (int y = blocks[row].length - 1; y >=0; y--) {
			if (blocks[row][y] == null) { break; }
			exists = y;
		}
		return exists;
	}
	
	
}
