package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import entities.ObstacleFetcher;
import input.MouseManager;
import states.GameState;
import states.SettingState;

public class Launcher {
	
	public static final int WIDTH = 1000, HEIGHT = (int) (WIDTH * 0.5625);
	public static Game game;
	public static GameState gameState;
	public static SettingState settingState;
	public static Font gameFont;
	
	public static MouseManager mouseManager;

	public static void main(String[] args){
		
		GameState.loadImages();
		ObstacleFetcher.fetchEntities();
		
		game = new Game("Minecraft Java Edition", WIDTH, HEIGHT);
		game.start();
		gameState = game.gameState;
		settingState = game.settingState;
		
		try {
			gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("FOT-Rodin Pro DB.otf")).deriveFont(30f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("FOT-Rodin Pro DB.otf")).deriveFont(30f));
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
		
	}

}