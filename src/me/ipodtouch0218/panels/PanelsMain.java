package me.ipodtouch0218.panels;

import me.ipodtouch0218.java2dengine.GameEngine;
import me.ipodtouch0218.java2dengine.display.GameWindow;
import me.ipodtouch0218.panels.objects.ObjPonBoard;

public class PanelsMain {

	public static boolean renderColor = false;
	
	public static void main(String[] args) {
		new PanelsMain();
	}
	
	////////
	public PanelsMain() {
		init();
	}
	
	private void init() {
		GameWindow.setWindowName("JPdP");
		GameWindow.setWindowSize(256,448);
		GameWindow.setScaleSize(1, 1);
		GameEngine.setMaxFPS(60);
		GameEngine.start();
		GameWindow.center();
		
		GameEngine.addGameObject(new ObjPonBoard(6, 12), 32, 32);
		
	}
	
}
