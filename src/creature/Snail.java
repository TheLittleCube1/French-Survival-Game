package creature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import algorithms.Toolbox;
import entities.CreatureControl;
import entities.EntityControl;
import inventory.Inventory;
import inventory.Item;
import main.Launcher;
import states.GameState;
import survival.HealthBar;

public class Snail extends Creature {

	public static BufferedImage SNAIL_LEFT, SNAIL_RIGHT;
	public boolean facingRight;

	public Snail(double x, double y) {
		super(x, y);
		super.healthBar = new HealthBar(20, Double.POSITIVE_INFINITY, this);
		IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.045);
		IMAGE_HEIGHT = IMAGE_WIDTH;
		facingRight = Math.random() < 0.5;
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		Toolbox.drawCenteredImage(g, facingRight ? SNAIL_RIGHT : SNAIL_LEFT, positionOnScreen[0],
				(int) ((topY() + bottomY()) / 2), (int) (IMAGE_WIDTH * GameState.zoom),
				(int) (IMAGE_HEIGHT * GameState.zoom));
		healthBar.render(g);
	}

	@Override
	public void tick() {

		healthBar.tick();

	}

	@Override
	public void die() {
		deathTime = System.nanoTime();
		harvest();
	}

	@Override
	public void harvest() {
		Inventory.addBundle(Item.ID_SNAIL, 1);
		CreatureControl.remove(this);
		EntityControl.remove(this);
	}

	@Override
	public boolean inHarvestRange() {
		return Toolbox.pixelsFromPlayer(x, y) < 50;
	}

	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (IMAGE_HEIGHT * 0.7 * GameState.zoom);
	}

	@Override
	public int bottomY() {
		return (int) (topY() + IMAGE_HEIGHT * GameState.zoom);
	}

}
