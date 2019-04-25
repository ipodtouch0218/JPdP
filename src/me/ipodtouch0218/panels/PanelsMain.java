package me.ipodtouch0218.panels;

import me.ipodtouch0218.java2dengine.GameEngine;
import me.ipodtouch0218.java2dengine.display.GameWindow;
import me.ipodtouch0218.panels.objects.ObjPonBoard;

public class PanelsMain {

	public static void main(String[] args) {
		new PanelsMain();
	}
	
	////////
	public PanelsMain() {
		init();
	}
	
	private void init() {
		GameWindow.setWindowName("Panel de Pon");
		GameWindow.setWindowSize(1280,720);
//		GameWindow.setScaleSize(2, 2);
		GameEngine.setMaxFPS(60);
		GameEngine.start();
		
		GameEngine.addGameObject(new ObjPonBoard(), 32, 64);
		
	}
	
}
