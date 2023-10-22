package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import algorithms.LineSegmentIntersection;
import algorithms.Point;
import algorithms.Toolbox;
import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import main.Launcher;
import player.PlayerMovement;
import states.GameState;

public class Tree extends Obstacle {

	public static BufferedImage[] TREES = new BufferedImage[2];
	public BufferedImage TREE;
	public int IMAGE_WIDTH, IMAGE_HEIGHT;
	
	public int species = -1;
	public static final double[] probabilities = { 0.8, 0.2 };
	
	public boolean harvestWaited = true;
	public double phase, cycleLength = 120 * Game.idealFrameRate, harvestableWindowFraction = 0.3;
	
	public static int harvestIconWidth = 70, harvestIconHeight = (int) (harvestIconWidth * 0.65);

	public Tree(double x, double y) {
		super(x, y, 0);
		this.x = x;
		this.y = y;
		this.harvestDistance = 60;
		
		double s = 0.08;
		this.boundary.add(new double[] { x - s, y - s });
		this.boundary.add(new double[] { x + s, y + s });
		
		harvestable = true;
		showHarvestIcon = true;

		harvestReloadSeconds = 120;
		harvestReloadNanoseconds = harvestReloadSeconds * (int) 1e9;
		lastHarvest = 0L;
		phase = Math.random();
		
		double r = Math.random(), total = 0;
		for (int i = 0; i < TREES.length - 1; i++) {
			total += probabilities[i];
			if (r < total) {
				species = i;
				break;
			}
		}
		if (species == -1) {
			species = TREES.length - 1;
		}
		TREE = TREES[species];
		
		int pixelsWidth = TREE.getWidth(), pixelsHeight = TREE.getHeight();
		IMAGE_WIDTH = (int) (pixelsWidth * 0.003278 * Launcher.WIDTH);
		IMAGE_HEIGHT = (int) (pixelsHeight * 0.003278 * Launcher.WIDTH);
		
	}
	
	@Override
	public void drawHarvestIcon(Graphics2D g, boolean highlight) {
		int[] position = Toolbox.positionOnScreen(x, y);
		int screenX = position[0], screenY = position[1];
		if (highlight) {
			g.drawImage(Obstacle.HIGHLIGHTED_HARVEST_ICON, (int) (screenX - harvestIconWidth / 2 * GameState.zoom), (int) (screenY - (285 + 12 * Math.sin(Game.frameCount / 10.0)) * GameState.zoom), (int) (harvestIconWidth * GameState.zoom), (int) (harvestIconHeight * GameState.zoom), null);
		} else {
			g.drawImage(Obstacle.HARVEST_ICON, (int) (screenX - harvestIconWidth / 2 * GameState.zoom), (int) (screenY - (285 + 12 * Math.sin(Game.frameCount / 10.0)) * GameState.zoom), (int) (harvestIconWidth * GameState.zoom), (int) (harvestIconHeight * GameState.zoom), null);
		}
	}
	
	@Override
	public void interact() {
		
	}
	
	@Override
	public void harvest() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Item.ID_ACORN, 1));
		if (Math.random() < 0.2) {
			items.add(new ItemStack(Item.ID_BRANCH, 1));
		}
		Inventory.addItemStacks(items);
		
		readyForHarvest = false;
		harvestWaited = false;
		lastHarvest = System.nanoTime();
	}

	@Override
	public boolean collision(double fromX, double fromY, double toX, double toY) {

		Point playerStart = new Point(fromX, fromY), playerEnd = new Point(toX, toY);
		Point boundaryStart = new Point(boundary.get(0)[0], boundary.get(0)[1]), boundaryEnd = new Point(boundary.get(1)[0], boundary.get(1)[1]);
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
			int[] nextPosition = Toolbox.positionOnScreen(boundary.get((i + 1) % boundary.size())[0], boundary.get((i + 1) % boundary.size())[1]);
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2));
			g.drawLine(position[0], position[1], nextPosition[0], nextPosition[1]);
		}
		Toolbox.drawPosition(x, y, g);
	}
	
	@Override
	public boolean inFrontOf(int screenY) {
		int yOnScreen = Toolbox.yOnScreen(boundary.get(0)[0], boundary.get(0)[1]);
		if (yOnScreen >= screenY) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void tick() {
		if (System.nanoTime() - lastHarvest > harvestReloadNanoseconds) {
			harvestWaited = true;
		}
		phase += 1 / cycleLength;
		if (phase > 1) {
			phase--;
		}
		if (phase < harvestableWindowFraction && harvestWaited) {
			readyForHarvest = true;
		} else {
			readyForHarvest = false;
		}
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		g.drawImage(TREE, positionOnScreen[0] - (int) (IMAGE_WIDTH * 0.515 * GameState.zoom),
				positionOnScreen[1] - (int) (IMAGE_HEIGHT * GameState.zoom),
				(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom), null);
	}
	
	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (IMAGE_HEIGHT * GameState.zoom);
	}
	
	@Override
	public int bottomY() {
		return Toolbox.yOnScreen(x, y);
	}
	
	@Override
	public String toString() {
		return "Tree";
	}

}
