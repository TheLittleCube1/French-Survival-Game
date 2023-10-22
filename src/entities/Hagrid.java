package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import algorithms.LineSegmentIntersection;
import algorithms.Point;
import algorithms.Toolbox;
import inventory.Inventory;
import inventory.InventoryUI;
import inventory.Item;
import main.Game;
import main.Launcher;
import player.PlayerMovement;
import states.GameState;

public class Hagrid extends Obstacle {

	public static final int IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.13), IMAGE_HEIGHT = IMAGE_WIDTH;
	public static BufferedImage[] HAGRID_IDLE_LEFT = new BufferedImage[4], HAGRID_IDLE_RIGHT = new BufferedImage[4];
	public static BufferedImage[] HAGRID_SPEAK_LEFT = new BufferedImage[4], HAGRID_SPEAK_RIGHT = new BufferedImage[4];
	public static boolean interacting = false;
	public static int lastInteraction = -1;
	public static int framesPerTick = 15;

	public static Hagrid Hagrid;

	public static String[] messages = {  };
	public static int mission;
	public static int messageIndex = 0;

	public Hagrid(double x, double y) {
		super(x, y, 1);
		this.x = x;
		this.y = y;

		harvestable = false;
		readyForHarvest = false;

		Hagrid = this;

		double left = 0.25, right = 0.15;
		this.boundary.add(new double[] { x - left, y - left });
		this.boundary.add(new double[] { x + right, y + right });
		
		shuffleMission();
	}
	
	public static void shuffleMission() {
		mission = (int) (Math.random() * Item.mission.length);
		System.out.println("New mission: " + mission);
	}

	@Override
	public void interact() {

		double orthoDistance = orthoDistance();
		if (orthoDistance < 70) {
			if (interacting) {
				interacting = false;
				lastInteraction = -1;
			} else {
				interacting = true;
				lastInteraction = Game.frameCount / framesPerTick;
			}
			InventoryUI.showInventory = false;
			InventoryUI.selectedInventorySpace = -1;
			Crafting.showCraftingUI = false;
			GameState.nearestCampfire.clear();
			Campfire.selectedCampfire = null;
		} else {
			interacting = false;
			lastInteraction = -1;
		}

	}

	@Override
	public void harvest() {
	}

	@Override
	public void drawHarvestIcon(Graphics2D g, boolean highlight) {
	}

	@Override
	public boolean collision(double fromX, double fromY, double toX, double toY) {

		Point playerStart = new Point(fromX, fromY), playerEnd = new Point(toX, toY);
		Point boundaryStart = new Point(boundary.get(0)[0], boundary.get(0)[1]),
				boundaryEnd = new Point(boundary.get(1)[0], boundary.get(1)[1]);
		if (!LineSegmentIntersection.intersect(playerStart, playerEnd, boundaryStart, boundaryEnd)) {
			return false;
		}

		if (PlayerMovement.requestID >= 4) {
			PlayerMovement.velocityX = PlayerMovement.perFrameWhole * (+0.500);
			PlayerMovement.velocityY = PlayerMovement.perFrameWhole * (+0.500);
		} else if (PlayerMovement.requestID <= -4) {
			PlayerMovement.velocityX = PlayerMovement.perFrameWhole * (-0.500);
			PlayerMovement.velocityY = PlayerMovement.perFrameWhole * (-0.500);
		} else {
			PlayerMovement.velocityX = 0;
			PlayerMovement.velocityY = 0;
		}

		return true;
	}

	@Override
	public void drawBoundary(Graphics2D g) {
		for (int i = 0; i < boundary.size(); i++) {
			int[] position = Toolbox.positionOnScreen(boundary.get(i)[0], boundary.get(i)[1]);
			int[] nextPosition = Toolbox.positionOnScreen(boundary.get((i + 1) % boundary.size())[0],
					boundary.get((i + 1) % boundary.size())[1]);
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2));
			g.drawLine(position[0], position[1], nextPosition[0], nextPosition[1]);
		}
		Toolbox.drawPosition(x, y, g);
	}

	@Override
	public boolean inFrontOf(int screenY) {
		int yOnScreen = Toolbox.yOnScreen(x, y);
		if (yOnScreen > screenY) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		if (Inventory.quantity(Item.mission[mission]) >= 1) {
			messages = new String[] { "Ah, merci!", Item.missionStatements[mission] };
		} else {
			messages = new String[] { "Bonjour!", Item.missionStatements[mission] };
		}
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		int ticks = Game.frameCount / framesPerTick;
		if (interacting) {
			boolean talking = ticks - lastInteraction < Game.averageFrameRate / framesPerTick * 2;
			if (talking) {
				g.drawImage(HAGRID_SPEAK_LEFT[(ticks - lastInteraction + 1) % 4],
						positionOnScreen[0] - (int) (IMAGE_WIDTH * 0.55 * GameState.zoom),
						positionOnScreen[1] - (int) (IMAGE_HEIGHT * 0.85 * GameState.zoom),
						(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom), null);
			} else {
				g.drawImage(HAGRID_SPEAK_LEFT[0],
						positionOnScreen[0] - (int) (IMAGE_WIDTH * 0.55 * GameState.zoom),
						positionOnScreen[1] - (int) (IMAGE_HEIGHT * 0.85 * GameState.zoom),
						(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom), null);
			}
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_BOTTOM);
			Toolbox.setFontSize(g, (int) (14 * GameState.zoom));
			g.setColor(new Color(40, 40, 40));
			Toolbox.drawText(g, messages[messageIndex], positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_WIDTH * 0.88 * GameState.zoom));
		} else {
			g.drawImage(HAGRID_IDLE_LEFT[ticks % 4], positionOnScreen[0] - (int) (IMAGE_WIDTH * 0.55 * GameState.zoom),
					positionOnScreen[1] - (int) (IMAGE_HEIGHT * 0.85 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom), null);
		}
		if (!interacting) {
			double distance = Toolbox.pixelsFromPlayer(Hagrid.x, Hagrid.y);
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_BOTTOM);
			Toolbox.setFontSize(g, 14);
			g.setColor(new Color(40, 40, 40, (int) Toolbox.constrain(Toolbox.map(distance, 100, 170, 0, 255), 0, 255)));
			Toolbox.drawText(g, "Hagrid", positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_WIDTH * 0.88 * GameState.zoom));
		}
	}

	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (0.85 * IMAGE_HEIGHT * GameState.zoom);
	}

	@Override
	public int bottomY() {
		return Toolbox.yOnScreen(x, y);
	}

	@Override
	public String toString() {
		return "NPC";
	}

}
