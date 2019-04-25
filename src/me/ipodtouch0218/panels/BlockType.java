package me.ipodtouch0218.panels;

import java.awt.Color;

public enum BlockType {

	RED(0xF80800),
	GREEN(0x08F800),
	LIGHT_BLUE(0x10F8F8),
	YELLOW(0xF8F808),
	MAGENTA(0xF810F8),
	BLUE(0x0080F8),
	METAL(0xC0C0C0),
	
	GARBAGE_NORMAL(0xF8A800),
	GARBAGE_METAL(0x606060);
	
	private Color color;
	BlockType(int color) {
		this.color = new Color(color);
	}
	
	public Color color() { return color; }

	public static BlockType randomBlock() {
		return values()[(int) (Math.random()*6)];
	}
	
}
