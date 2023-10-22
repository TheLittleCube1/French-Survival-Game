package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import algorithms.Toolbox;
import main.Launcher;
import player.CharacterGraphics;
import states.GameState;

public abstract class Obstacle extends Entity {
	
	public static final int OBSTACLE_TREE = 0, OBSTACLE_CAMPFIRE = 1, OBSTACLE_TALL_GRASS = 2;
	
	public static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	public static BufferedImage HARVEST_ICON, HIGHLIGHTED_HARVEST_ICON;
	
	public double x, y, orthoY;
	public ArrayList<double[]> boundary = new ArrayList<double[]>();
	public boolean harvestable, readyForHarvest, showHarvestIcon;
	
	public long lastHarvest;
	public long harvestReloadSeconds, harvestReloadNanoseconds;
	
	public int harvestDistance;
	
	public Obstacle(double x, double y, double type) {
		super(x, y);
		ObstacleControl.add(this);
		orthoY = Toolbox.screenYFromOrigin(x, y);
	}
	
	public static Obstacle nearestHarvestableEntity() {
		float minimumDistance = 1e9f;
		Obstacle bestEntity = null;
		for (int i = 0; i < Obstacle.obstacles.size(); i++) {
			Obstacle e = Obstacle.obstacles.get(i);
			if (!e.harvestable || !e.readyForHarvest) {
				continue;
			}
			int[] positionOnScreen = Toolbox.positionOnScreen(e.x, e.y);
			int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2;
			int displacementY = (int) (positionOnScreen[1] - Launcher.HEIGHT / 2 - CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
			float distance = displacementX * displacementX + displacementY * displacementY;
			if (distance < minimumDistance) {
				bestEntity = e;
				minimumDistance = distance;
			}
		}
		return bestEntity;
	}
	
	public static Campfire nearestCampfire() {
		float minimumDistance = 1e9f;
		Campfire nearestCampfire = null;
		for (int i = 0; i < Obstacle.obstacles.size(); i++) {
			Obstacle e = Obstacle.obstacles.get(i);
			if (!e.toString().equals("Campfire")) {
				continue;
			}
			int[] positionOnScreen = Toolbox.positionOnScreen(e.x, e.y);
			int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2;
			int displacementY = (int) (positionOnScreen[1] - Launcher.HEIGHT / 2 - CharacterGraphics.playerRenderHeight / 2 * GameState.zoom);
			float distance = displacementX * displacementX + displacementY * displacementY;
			if (distance < minimumDistance) {
				nearestCampfire = (Campfire) e;
				minimumDistance = distance;
				
			}
		}
		return nearestCampfire;
	}
	
	public boolean inHarvestRange() {
		return orthoDistance() <= harvestDistance;
	}
	
	public double orthoDistance() {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		int displacementX = Launcher.WIDTH / 2 - positionOnScreen[0];
		int displacementY = Launcher.HEIGHT / 2 + (int) (CharacterGraphics.playerRenderHeight / 2 * GameState.zoom) - positionOnScreen[1];
		return Math.sqrt(displacementX * displacementX + displacementY * displacementY);
	}
	
	public void drawDebug(Graphics2D g) {
		drawBoundary(g);
	}
	
	public abstract void drawHarvestIcon(Graphics2D g, boolean highlight);
	
	public abstract void interact();
	public abstract void harvest();
	
	public abstract boolean inFrontOf(int screenY);
	
	public abstract boolean collision(double fromX, double fromY, double toX, double toY);
	public abstract void drawBoundary(Graphics2D g);
	
	public abstract int topY();
	public abstract int bottomY();
	
	public abstract void tick();
	public abstract void render(Graphics2D g);
	
	public abstract String toString();
	
}