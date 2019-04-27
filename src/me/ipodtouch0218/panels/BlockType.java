package me.ipodtouch0218.panels;

import java.awt.Color;
import java.util.Arrays;

import me.ipodtouch0218.java2dengine.display.sprite.GameSprite;
import me.ipodtouch0218.java2dengine.display.sprite.SpriteSheet;

public enum BlockType {
	
	RED(0xF80800, 0),
	GREEN(0x08F800, 1),
	LIGHT_BLUE(0x10F8F8, 2),
	YELLOW(0xF8F808, 3),
	MAGENTA(0xF810F8, 4),
	BLUE(0x0080F8, 5),
	METAL(0xC0C0C0, -1),
	
	GARBAGE_NORMAL(0xF8A800, -1),
	GARBAGE_METAL(0x606060, -1);
	
	static {
		SpriteSheet blocksheet = new SpriteSheet("blocks.png", 32, 32);
		Arrays.stream(values()).filter(bt -> bt.row >= 0).forEach(bt -> bt.sprite = blocksheet.getSprite(bt.row, 0));
		blocksheet.close();
	}
	private int row;
	private Color color;
	private GameSprite sprite;
	BlockType(int color, int row) {
		this.color = new Color(color);
		this.row = row;
	}
	
	public Color color() { return color; }
	public GameSprite sprite() { return sprite; }

	public static BlockType randomBlock() {
		return values()[(int) (Math.random()*5)];
	}
	
}
