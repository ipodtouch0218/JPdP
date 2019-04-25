package me.ipodtouch0218.panels.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

import me.ipodtouch0218.java2dengine.input.InputHandler;
import me.ipodtouch0218.java2dengine.object.GameObject;
import me.ipodtouch0218.panels.BlockType;
import me.ipodtouch0218.panels.util.MiscUtils;

public class ObjPonBoard extends GameObject {

	private static final int blockScale = 32;
	private static final int matchMinSize = 3;

	private BlockType[][] blocks = new BlockType[6][13];
	private double[] rowGravityTimer = new double[6];
	
	private double boardScroll = 0;
	private double scrollSpeed = 0.1; //measured in blocks per second
	private boolean scroll = true;
	private boolean manualScrolling = false;
	
	private int cursorX = 2;
	private int cursorY = 5;
	
	private boolean leftPressed, rightPressed, upPressed, downPressed, rotPressed, scrollPressed;
	
	public ObjPonBoard() {
		
		//TODO: generate board without matches
		
		for (int x = 0; x < blocks.length; x++) {
			for (int y = blocks[x].length - 1; y > 6; y--) {
				blocks[x][y] = BlockType.randomBlock();
			}
		}
		
	}
	
	private void prefillBoard() {
		//Boards start with 30 tiles, none matching, 
	}
	
	////
	
	@Override
	public void tick(double delta) {

		handleControls();
		handleScrolling(delta);
		handleGravity(delta);
		
	}
	
	private void handleControls() {
		if (manualScrolling) { return; }
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
		if (pressed && !scrollPressed) {
			manualScrolling = true;
		}
		scrollPressed = pressed;
		
		cursorX = MiscUtils.limit(cursorX, 0, 4);
		cursorY = MiscUtils.limit(cursorY, 1, 11);
		
		pressed = InputHandler.isKeyPressed(KeyEvent.VK_A);
		if (pressed && !rotPressed && !moved) {
			swapBlocks(); 
		}
		rotPressed = pressed;
	}
	
	private void handleGravity(double delta) {
		for (int i = 0; i < rowGravityTimer.length; i++) {
			if (rowGravityTimer[i] > 0) {
				rowGravityTimer[i] -= delta;
			}
		}
	}
	
	private void swapBlocks() {
		if ((rowGravityTimer[cursorX] > 0 && blocks[cursorX][cursorY+1] == null) ||
			(rowGravityTimer[cursorX+1] > 0 && blocks[cursorX+1][cursorY+1] == null)) 
				{ return; }
		
		BlockType temp = blocks[cursorX][cursorY];
		blocks[cursorX][cursorY] = blocks[cursorX + 1][cursorY];
		blocks[cursorX + 1][cursorY] = temp;
		
		findAndClearMatches(cursorX, cursorY);
		findAndClearMatches(cursorX+1, cursorY);
	}
	
	private void handleScrolling(double delta) {
		if (!scroll && !manualScrolling) { return; }
		if (blocksAtTop()) { 
			manualScrolling = false;
			return; 
		}
		
		if (manualScrolling) {
			boardScroll += 8*blockScale*delta;
		} else {
			boardScroll += scrollSpeed*blockScale*delta;
		}
		if (boardScroll >= blockScale) {
			moveBlocksUp();
			boardScroll %= blockScale;
			manualScrolling = false;
		}
	}
	
	private void moveBlocksUp() {
		if (blocksAtTop()) { return; }
		
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
		g.setClip((int) x, (int) y-2, blockScale*blocks.length, blockScale*(blocks[0].length-1)+2);
		
		for (int xBlock = 0; xBlock < blocks.length; xBlock++) {
			for (int yBlock = blocks[xBlock].length - 1; yBlock >= 0; yBlock--) {
				
				BlockType block = blocks[xBlock][yBlock];
				if (block == null) { continue; }
				
				g.setColor(block.color());
				if (yBlock >= blocks[xBlock].length - 1) {
					g.setColor(block.color().darker().darker());
				}
				
				g.fillRect((int) (x + (xBlock * blockScale) + 1), (int) (y + (yBlock * blockScale) - boardScroll + 1), blockScale-2, blockScale-2);
			}
		}
	}
	
	private void renderCursor(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawRect((int) (x + (blockScale * cursorX-1)), (int) (y + (blockScale * cursorY-1) - boardScroll), (blockScale*2)+1, blockScale+1);
	}
	
	////
	
	private void checkForGravity() {
		for (int x = 0; x < blocks.length; x++) {
			for (int y = 0; y < blocks.length; y++) {
				
			}
		}
	}
	
	////
	
	
	//TODO: "swapper matches" to combine nearby matches (two 3 lengths = one 6 length)
	private void findAndClearMatches(int x, int y) {
		getMatches(x,y).forEach(p -> blocks[p.x][p.y] = null);
		
		checkForGravity();
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
	
	
}
