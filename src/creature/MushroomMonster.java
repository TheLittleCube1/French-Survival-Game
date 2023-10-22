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
import player.CharacterGraphics;
import player.Player;
import states.GameState;
import survival.HealthBar;
import tiles.Tiles;

public class MushroomMonster extends Creature {

	public static BufferedImage[] MUSHROOM_IDLE_LEFT = new BufferedImage[4], MUSHROOM_IDLE_RIGHT = new BufferedImage[4];
	public static BufferedImage[] MUSHROOM_RUN_LEFT = new BufferedImage[8], MUSHROOM_RUN_RIGHT = new BufferedImage[8];
	public static BufferedImage[] MUSHROOM_DIE_LEFT = new BufferedImage[4], MUSHROOM_DIE_RIGHT = new BufferedImage[4];
	public static BufferedImage[] MUSHROOM_ATTACK_LEFT = new BufferedImage[8], MUSHROOM_ATTACK_RIGHT = new BufferedImage[8];

	public static double speed = 1.2;
	public static int attackStart = -1;
	
	public static final double ATTACK_CYCLE_SECONDS = 2.0;

	public static final int STATE_IDLE = 0, STATE_RUNNING = 1, STATE_ATTACK = 2, STATE_DEAD = -1;
	public static final int DIRECTION_LEFT = -1, DIRECTION_RIGHT = 1;

	public int state = STATE_IDLE;
	public boolean facingRight = true;
	public double[] directionVector = { 1, 0 };

	public MushroomMonster(double x, double y) {
		super(x, y);
		super.healthBar = new HealthBar(45, Double.POSITIVE_INFINITY, this);
		IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.12);
		IMAGE_HEIGHT = IMAGE_WIDTH;
		facingRight = Math.random() < 0.5;
	}

	@Override
	public void tick() {
		
		healthBar.tick();

		if (state == STATE_DEAD) {
			return;
		}
		
		if (Player.state == Player.STATE_DEAD) {
			state = STATE_IDLE;
			return;
		}

		if (Toolbox.getTile(x, y) == Tiles.TILE_TYPE_WATER) {
			speed = Math.max(0.4, speed - 0.02);
		} else {
			speed = Math.min(1.2, speed + 0.02);
		}
		
		if ((state == STATE_ATTACK && (Game.frameCount - attackStart) % (ATTACK_CYCLE_SECONDS * Game.averageFrameRate) < Game.averageFrameRate) || state != STATE_ATTACK) {
			// State update
			int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
			int dX = positionOnScreen[0] - Launcher.WIDTH / 2;
			int dY = positionOnScreen[1] - (Launcher.HEIGHT / 2 + CharacterGraphics.playerYDisplacement);
			
			int targetX = dX < 0 ? Launcher.WIDTH / 2 - 50 : Launcher.WIDTH / 2 + 50;
			int targetY = Launcher.HEIGHT / 2 + CharacterGraphics.playerYDisplacement;
			int displacementX = positionOnScreen[0] - targetX;
			int displacementY = positionOnScreen[1] - targetY;
			
			double targetDistance = Math.sqrt(displacementX * displacementX + displacementY * displacementY);
			double distance = Math.sqrt(dX * dX + dY * dY);
			
			if (distance > Launcher.WIDTH * 0.6) {
	
				// Distance far away -> Idle
				state = STATE_IDLE;
	
			} else if (distance < 70 && Math.abs(dY) < 30) {
	
				// Distance less than 70 pixels from target, y coordinate is right -> Attack mode
				state = STATE_ATTACK;
				if (attackStart == -1) {
					attackStart = Game.frameCount;
				}
	
				if (dX < 0) {
					facingRight = true;
				} else {
					facingRight = false;
				}
	
			} else if (distance < Launcher.WIDTH * 0.4) {
	
				// Distance just right -> Run towards player
				state = STATE_RUNNING;
				if (displacementX < 0) {
					facingRight = true;
				} else {
					facingRight = false;
				}
	
				double[] orthoDirection = { -(double) displacementX / targetDistance, -(double) displacementY / targetDistance };
				directionVector[0] = 0.894427191 * orthoDirection[0] + 0.894427191 * orthoDirection[1];
				directionVector[1] = 0.447213595 * orthoDirection[0] - 0.447213595 * orthoDirection[1];
				double length = Math
						.sqrt(directionVector[0] * directionVector[0] + directionVector[1] * directionVector[1]);
				directionVector[0] /= length;
				directionVector[1] /= length;
	
			}
		}
		
		if (state != STATE_ATTACK) {
			attackStart = -1;
		}

		// Position update
		if (state == STATE_RUNNING) {
			x += speed / Game.averageFrameRate * directionVector[0];
			y += speed / Game.averageFrameRate * directionVector[1];
		}

	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		if (state == STATE_IDLE || Player.state == Player.STATE_DEAD) {
			double cycleSeconds = 0.8, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int[] imageIndex = new int[] { 0, 1, 2, 3 };
			int cycleIndex = (int) ((Game.frameCount % cycleFrames) / (cycleFrames / imageIndex.length));
			Toolbox.drawCenteredImage(g,
					facingRight ? MUSHROOM_IDLE_RIGHT[imageIndex[cycleIndex]]
							: MUSHROOM_IDLE_LEFT[imageIndex[cycleIndex]],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		} else if (state == STATE_RUNNING) {
			double cycleSeconds = 0.6, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int[] imageIndex = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
			int cycleIndex = (int) ((Game.frameCount % cycleFrames) / (cycleFrames / imageIndex.length));
			Toolbox.drawCenteredImage(g,
					facingRight ? MUSHROOM_RUN_RIGHT[imageIndex[cycleIndex]]
							: MUSHROOM_RUN_LEFT[imageIndex[cycleIndex]],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		} else if (state == STATE_DEAD) {
			int index = (int) Math.min(3, (System.nanoTime() - deathTime) / 2e8);
			Toolbox.drawCenteredImage(g, facingRight ? MUSHROOM_DIE_RIGHT[index] : MUSHROOM_DIE_LEFT[index],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
		} else if (state == STATE_ATTACK) {
			double cycleSeconds = ATTACK_CYCLE_SECONDS, cycleFrames = cycleSeconds * Game.averageFrameRate;
			int[] imageIndex = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7 };
			int cycleIndex = (int) (((Game.frameCount - attackStart) % cycleFrames) / (cycleFrames / imageIndex.length));
			Toolbox.drawCenteredImage(g,
					facingRight ? MUSHROOM_ATTACK_RIGHT[imageIndex[cycleIndex]]
							: MUSHROOM_ATTACK_LEFT[imageIndex[cycleIndex]],
					positionOnScreen[0], (int) (positionOnScreen[1] - IMAGE_HEIGHT / 2 * GameState.zoom),
					(int) (IMAGE_WIDTH * GameState.zoom), (int) (IMAGE_HEIGHT * GameState.zoom));
			if ((Game.frameCount - attackStart) % cycleFrames == cycleFrames - 5) {
				hit();
			}
		}
		healthBar.render(g);
	}
	
	public void hit() {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		int dX = positionOnScreen[0] - Launcher.WIDTH / 2;
		int dY = positionOnScreen[1] - (Launcher.HEIGHT / 2 + CharacterGraphics.playerYDisplacement);
		int distance = (int) Math.sqrt(dX * dX + dY * dY);
		if ((dX < 0 ^ !facingRight) && Math.abs(dY) < 30 && distance < 95) {
			Player.playerHealthBar.takeHit(35);
		}
	}

	@Override
	public void die() {
		state = STATE_DEAD;
		deathTime = System.nanoTime();
	}

	@Override
	public void harvest() {
		Inventory.addBundle(Item.ID_MUSHROOM, 3);
		CreatureControl.remove(this);
		EntityControl.remove(this);
	}

	@Override
	public boolean inHarvestRange() {
		return Toolbox.pixelsFromPlayer(x, y) < 80;
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
