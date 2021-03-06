package me.ipodtouch0218.panels.objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import me.ipodtouch0218.java2dengine.GameEngine;
import me.ipodtouch0218.java2dengine.object.GameObject;
import me.ipodtouch0218.panels.BlockType;
import me.ipodtouch0218.panels.PanelsMain;

public class ObjFallingBlocks extends GameObject {

	private static double fallingSpeed = 24; //blocks per second
	private ObjPonBoard board;
	private int rowX, chain;
	
	private BlockType[] blocks;
	
	public ObjFallingBlocks(ObjPonBoard board, int rowX, int startingY, int chain) {
		this.board = board;
		this.rowX = rowX;
		this.chain = chain;
		
		ArrayList<BlockType> blockList = new ArrayList<>();
		for (int y = startingY; y >= 0; y--) {
			BlockType block = board.blocks[rowX][y];
			if (block == null) { break; }
			blockList.add(block);
			board.blocks[rowX][y] = null;
		}
		blocks = blockList.toArray(new BlockType[]{});
		
		x = (board.getX() + (rowX * ObjPonBoard.blockScale));
		y = (board.getY() + ((startingY) * ObjPonBoard.blockScale) - board.boardScroll);
	}
	
	///
	
	@Override
	public void tick(double delta) {
		
		y += fallingSpeed*ObjPonBoard.blockScale*delta;
		
		if (hitGround()) {
			int toppos = board.getTopBlockPos(rowX);
			for (int i = 0; i < blocks.length; i++) {
				board.blocks[rowX][toppos - i - 1] = blocks[i];
			}
			HashSet<Point> points = new HashSet<>();
			for (int i = 0; i < blocks.length; i++) {
				points.add(new Point(rowX, toppos-i-1));
			}
			board.findAndClearMatches(chain, points.toArray(new Point[]{}));
			
			GameEngine.removeGameObject(this);
		}
		
	}
	
	private boolean hitGround() {
		return y >= (board.getY() + ((board.getTopBlockPos(rowX)-1) * ObjPonBoard.blockScale) - board.boardScroll);
	}
	
	///
	
	@Override
	public void render(Graphics2D g) {
		for (int i = 0; i < blocks.length; i++) {
			
			if (PanelsMain.renderColor) {
				g.setColor(blocks[i].color());
				g.fillRect((int) (x+1), (int) (y - (i * ObjPonBoard.blockScale)), ObjPonBoard.blockScale-2, ObjPonBoard.blockScale-2);
			} else {
				g.drawImage(blocks[i].sprite().getImage(), (int) x, (int) (y - (i * ObjPonBoard.blockScale)), ObjPonBoard.blockScale, ObjPonBoard.blockScale, null);
			}
		}
	}
}
