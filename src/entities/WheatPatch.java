package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import algorithms.Toolbox;
import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import player.Player;
import states.GameState;
import tiles.Tiles;

public class WheatPatch extends Obstacle {

	public static final int IMAGE_WIDTH = GameState.tileWidth, IMAGE_HEIGHT = GameState.tileHeight;
	public static BufferedImage[] WHEAT_PATCH = new BufferedImage[2];
	
	public static final double MATURE_SECONDS = 20;
	public static final int STATE_SAPLING = 0, STATE_MATURE = 1;
	public int state = STATE_SAPLING;
	
	public int plantedFrame;

	public static int harvestIconWidth = 50, harvestIconHeight = (int) (harvestIconWidth * 0.65);

	public WheatPatch(double x, double y) {
		super(x, y, 0);
		this.x = x;
		this.y = y;
		this.harvestDistance = 60;

		harvestable = true;
		showHarvestIcon = true;
		
		int iX = (int) Math.round(x), iY = (int) Math.round(y);
		Tiles.addWheat(iX, iY, this);
		plantedFrame = Game.frameCount;
	}
	
	public static void plant() {
		int x = (int) Math.round(Player.playerX);
		int y = (int) Math.round(Player.playerY);
		if (Inventory.quantity(Item.ID_WHEAT_SEED) > 0 && Toolbox.currentTile() == Tiles.TILE_DIRT && Tiles.getWheatPatch(x, y) == null) {
			new WheatPatch(x, y);
			Inventory.removeItem(Item.ID_WHEAT_SEED);
		}
	}

	@Override
	public void drawHarvestIcon(Graphics2D g, boolean highlight) {
		
	}

	@Override
	public void interact() {

	}

	@Override
	public void harvest() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Item.ID_WHEAT, 1));
		Inventory.addItemStacks(items);
		EntityControl.remove(this);
		ObstacleControl.remove(this);
		Tiles.removeWheat((int) x, (int) y);
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
		if (Game.frameCount - plantedFrame > MATURE_SECONDS * Game.averageFrameRate) {
			state = STATE_MATURE;
		}
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		Toolbox.drawCenteredImage(g, state == STATE_SAPLING ? WHEAT_PATCH[0] : WHEAT_PATCH[1], positionOnScreen[0], positionOnScreen[1], (int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
	}

	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (IMAGE_HEIGHT * 0.5 * GameState.zoom);
	}

	@Override
	public int bottomY() {
		return Toolbox.yOnScreen(x, y) + (int) (IMAGE_HEIGHT * 0.5 * GameState.zoom);
	}

	@Override
	public String toString() {
		return "Wheat Patch";
	}

}
