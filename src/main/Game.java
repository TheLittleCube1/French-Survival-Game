package main;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import display.Display;
import environment.DayNightCycler;
import input.KeyManager;
import inventory.Inventory;
import states.GameState;
import states.SettingState;
import states.State;

public class Game implements Runnable {

	private Display display;
	public int width, height;
	public String title;

	private boolean running = false;
	private Thread thread;
	
	public static int idealFrameRate = 50;
	public static int averageFrameRate = idealFrameRate;
	
	public static final int NINE_AM = (int) (0 * idealFrameRate);
	public static final int SIX_PM = (int) (0.375 * DayNightCycler.DAY_SECONDS * idealFrameRate);
	public static final int NINE_PM = (int) (0.5 * DayNightCycler.DAY_SECONDS * idealFrameRate);
	public static final int SIX_AM = (int) (0.875 * DayNightCycler.DAY_SECONDS * idealFrameRate);
	public static int frameCount = NINE_AM;

	private BufferStrategy bs;
	public Graphics2D g;

	public GameState gameState = new GameState(this);
	public SettingState settingState = new SettingState(this);
	
	private KeyManager keyManager;

	public static BufferedImage piecesImage, analysisIcon, whitePerspectiveIcon, blackPerspectiveIcon, settingsIcon, territoryMapEnabledIcon, territoryMapDisabledIcon;
	public static List<Image> images = new ArrayList<Image>();
	
	public Game(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
		keyManager = new KeyManager();
	}

	private void init() {
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		
		prepareImages();
		State.setState(gameState);
		gameState.initialize();
		settingState.initialize();
		Inventory.initializeInventory();
		
	}

	public void tick() {

		if (State.getState() != null) {
			State.getState().tick(g);
		}

	}

	public void render() {
		
		bs = display.getCanvas().getBufferStrategy();
		if (bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}

		g = (Graphics2D) bs.getDrawGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		if (State.getState() != null) {
			State.getState().render(g);
		}

		bs.show();
		g.dispose();
	}

	public void run() {

		init();
		
		double timePerTick = 1e9 / idealFrameRate;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0;

		while (running) {
			now = System.nanoTime();
			timePerTick = ((1e9f / idealFrameRate) + (1e9f / averageFrameRate)) / 2f;
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if (delta >= 1) {
				render();
				tick();
				frameCount++;
				delta = 0;
				ticks++;
			}

			if (timer >= 1e9) {
				timer = 0;
				averageFrameRate = (int) ((averageFrameRate * 3.0 + ticks) / 4);
				System.out.println(ticks + " FPS");
				ticks = 0;
			}
		}

		stop();

	}

	public synchronized void start() {
		if (running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Display getDisplay() {
		return display;
	}

	public Graphics2D getGraphics2D() {
		return g;
	}

	public static void prepareImages() {
		
	}
	
	public KeyManager getKeyManager() {
		return keyManager;
	}

}