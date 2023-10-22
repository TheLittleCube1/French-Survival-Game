package creature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import algorithms.Toolbox;
import entities.CreatureControl;
import entities.EntityControl;
import inventory.Inventory;
import inventory.Item;
import main.Game;
import main.Launcher;
import states.GameState;
import survival.HealthBar;
import tiles.Tiles;

public class Rabbit extends Creature {

	public static BufferedImage[] RABBIT_IDLE_LEFT = new BufferedImage[3], RABBIT_IDLE_RIGHT = new BufferedImage[3];
	public static BufferedImage[] RABBIT_RUNNING_LEFT = new BufferedImage[6], RABBIT_RUNNING_RIGHT = new BufferedImage[6];
	public static BufferedImage[] RABBIT_DEAD_LEFT = new BufferedImage[4], RABBIT_DEAD_RIGHT = new BufferedImage[4];
	public static double speed = 1;

	public static final int STATE_IDLE = 0, STATE_RUNNING = 1, STATE_DEAD = -1;
	public static final int DIRECTION_LEFT = -1, DIRECTION_RIGHT = 1, DIRECTION_DOWN = -1, DIRECTION_UP = 1;

	public int state = STATE_RUNNING;
	public int[] movementDirection = new int[] { DIRECTION_LEFT, DIRECTION_UP };

	public Rabbit(double x, double y) {
		super(x, y);
		super.healthBar = new HealthBar(20, Double.POSITIVE_INFINITY, this);
		IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.04);
		IMAGE_HEIGHT = IMAGE_WIDTH;
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		if (state == STATE_IDLE) {
			double cycleSeconds = 6.5, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int[] imageIndex = new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 };
			int cycleIndex = (int) ((Game.frameCount % cycleFrames) / (cycleFrames / imageIndex.length));
			Toolbox.drawCenteredImage(g, movementDirection[0] == DIRECTION_RIGHT ? RABBIT_IDLE_RIGHT[imageIndex[cycleIndex]] : RABBIT_IDLE_LEFT[imageIndex[cycleIndex]], positionOnScreen[0],
					(int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		} else if (state == STATE_RUNNING) {
			double cycleSeconds = 0.5, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int[] imageIndex = new int[] { 0, 1, 2, 3, 4, 5 };
			int cycleIndex = (int) ((Game.frameCount % cycleFrames) / (cycleFrames / imageIndex.length));
			Toolbox.drawCenteredImage(g,
					(movementDirection[0] == DIRECTION_LEFT) ? RABBIT_RUNNING_LEFT[imageIndex[cycleIndex]]
							: RABBIT_RUNNING_RIGHT[imageIndex[cycleIndex]],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		} else if (state == STATE_DEAD) {
			int index = (int) Math.min((System.nanoTime() - deathTime) / 1.5e8, 2);
			Toolbox.drawCenteredImage(g,
					(movementDirection[0] == DIRECTION_LEFT) ? RABBIT_DEAD_LEFT[index]
							: RABBIT_DEAD_RIGHT[index],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		}
		healthBar.render(g);
	}

	@Override
	public void tick() {
		
		healthBar.tick();
		
		if (state == STATE_DEAD) {
			return;
		}
		
		if (Toolbox.getTile(x, y) == Tiles.TILE_TYPE_WATER) {
			speed = Math.max(0.4, speed - 0.02);
		} else {
			speed = Math.min(1.0, speed + 0.02);
		}

		// State update
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2,
				displacementY = positionOnScreen[1] - Launcher.HEIGHT / 2;
		double distance = Math.sqrt(displacementX * displacementX + displacementY * displacementY);
		if (state == STATE_RUNNING && distance < Launcher.WIDTH * 0.45) {
			state = STATE_RUNNING;
			if (displacementX < 0) {
				movementDirection[0] = DIRECTION_LEFT;
			} else {
				movementDirection[0] = DIRECTION_RIGHT;
			}
			if (displacementY < 0) {
				movementDirection[1] = DIRECTION_UP;
			} else {
				movementDirection[1] = DIRECTION_DOWN;
			}
		} else if (state == STATE_RUNNING && distance >= Launcher.WIDTH * 0.45) {
			state = STATE_IDLE;
		} else if (state == STATE_IDLE && distance <= Launcher.WIDTH * 0.3) {
			state = STATE_RUNNING;
			if (displacementX < 0) {
				movementDirection[0] = DIRECTION_LEFT;
			} else {
				movementDirection[0] = DIRECTION_RIGHT;
			}
			if (displacementY < 0) {
				movementDirection[1] = DIRECTION_UP;
			} else {
				movementDirection[1] = DIRECTION_DOWN;
			}
		}
		
		//state = STATE_IDLE;

		// Position update
		if (state == STATE_RUNNING) {
			double cycleSeconds = 0.4, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int cycleIndex = (int) ((Game.frameCount % cycleFrames) / (cycleFrames / 6));
			
			double factor = 0.1;
			
			// Horizontal direction
			if (movementDirection[0] == DIRECTION_RIGHT) {
				if (cycleIndex >= 1 && cycleIndex <= 3) {
					x += speed / Game.averageFrameRate;
					y += speed / Game.averageFrameRate;
				} else {
					x += speed / Game.averageFrameRate * factor;
					y += speed / Game.averageFrameRate * factor;
				}
			} else {
				if (cycleIndex >= 1 && cycleIndex <= 3) {
					x -= speed / Game.averageFrameRate;
					y -= speed / Game.averageFrameRate;
				} else {
					x -= speed / Game.averageFrameRate * factor;
					y -= speed / Game.averageFrameRate * factor;
				}
			}
			
			// Vertical direction
			if (movementDirection[1] == DIRECTION_UP) {
				if (cycleIndex >= 1 && cycleIndex <= 3) {
					x -= speed / Game.averageFrameRate;
					y += speed / Game.averageFrameRate;
				} else {
					x -= speed / Game.averageFrameRate * factor;
					y += speed / Game.averageFrameRate * factor;
				}
			} else {
				if (cycleIndex >= 1 && cycleIndex <= 3) {
					x += speed / Game.averageFrameRate;
					y -= speed / Game.averageFrameRate;
				} else {
					x += speed / Game.averageFrameRate * factor;
					y -= speed / Game.averageFrameRate * factor;
				}
			}
		}

	}
	
	@Override
	public void die() {
		state = STATE_DEAD;
		deathTime = System.nanoTime();
	}

	@Override
	public void harvest() {
		Inventory.addBundle(Item.ID_RAW_RABBIT, 1);
		CreatureControl.remove(this);
		EntityControl.remove(this);
	}

	@Override
	public boolean inHarvestRange() {
		return Toolbox.pixelsFromPlayer(x, y) < 50;
	}

	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (IMAGE_HEIGHT * GameState.zoom);
	}

	@Override
	public int bottomY() {
		return Toolbox.yOnScreen(x, y);
	}

}
