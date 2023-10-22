package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import algorithms.Toolbox;
import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import main.Launcher;
import states.GameState;

public class TallGrass extends Obstacle {

	public static final int IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.08),
			IMAGE_HEIGHT = (int) (IMAGE_WIDTH * 0.9210526315);
	public static BufferedImage TULIP;

	public boolean harvestWaited = true;
	public double phase, cycleLength = 120 * Game.idealFrameRate, harvestableWindowFraction = 0.2;

	public static int harvestIconWidth = 50, harvestIconHeight = (int) (harvestIconWidth * 0.65);

	public TallGrass(double x, double y) {
		super(x, y, 0);
		this.x = x;
		this.y = y;
		this.harvestDistance = 60;

		harvestable = true;
		showHarvestIcon = true;

		harvestReloadSeconds = 120;
		harvestReloadNanoseconds = harvestReloadSeconds * 1000000000;
		lastHarvest = 0L;
		phase = Math.random();

	}

	@Override
	public void drawHarvestIcon(Graphics2D g, boolean highlight) {
		int[] position = Toolbox.positionOnScreen(x, y);
		int screenX = position[0], screenY = position[1];
		if (highlight) {
			g.drawImage(Obstacle.HIGHLIGHTED_HARVEST_ICON, (int) (screenX - harvestIconWidth / 2 * GameState.zoom),
					(int) (screenY - (130 + 12 * Math.sin(Game.frameCount / 10.0)) * GameState.zoom),
					(int) (harvestIconWidth * GameState.zoom), (int) (harvestIconHeight * GameState.zoom), null);
		} else {
			g.drawImage(Obstacle.HARVEST_ICON, (int) (screenX - harvestIconWidth / 2 * GameState.zoom),
					(int) (screenY - (130 + 12 * Math.sin(Game.frameCount / 10.0)) * GameState.zoom),
					(int) (harvestIconWidth * GameState.zoom), (int) (harvestIconHeight * GameState.zoom), null);
		}
	}

	@Override
	public void interact() {

	}

	@Override
	public void harvest() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Item.ID_WHEAT_SEED, 2 + (int) (2 * Math.random()))); // 2 or 3 wheat seeds
		Inventory.addItemStacks(items);

		readyForHarvest = false;
		harvestWaited = false;
		lastHarvest = System.nanoTime();
	}

	@Override
	public boolean collision(double fromX, double fromY, double toX, double toY) {
		return false;
	}

	@Override
	public void drawBoundary(Graphics2D g) {
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
		g.drawImage(TULIP, positionOnScreen[0] - (int) (IMAGE_WIDTH * 0.515 * GameState.zoom),
				positionOnScreen[1] - (int) (IMAGE_HEIGHT * GameState.zoom), (int) (IMAGE_WIDTH * GameState.zoom),
				(int) (IMAGE_HEIGHT * GameState.zoom), null);
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
