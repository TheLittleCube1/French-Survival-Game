package player;

import java.awt.Graphics2D;

import algorithms.Toolbox;
import entities.Entity;
import entities.ItemCollectionNotification;
import main.Game;
import survival.HealthBar;

public class Player extends Entity {

	public static final int STATE_IDLE = 0,
							STATE_RUNNING = 1,
							STATE_DEAD = -1;

	public static final int ACTION_NONE = 0,
							ACTION_SLASH = 1;

	public static final int DIRECTION_NONE = 0,
							DIRECTION_LEFT = -1,
							DIRECTION_RIGHT = 1,
							DIRECTION_DOWN = -1,
							DIRECTION_UP = 1;

	public static boolean facingRight = false;
	public static HealthBar playerHealthBar = new HealthBar(100, 20, null);
	
	public static double playerX = 0, playerY = 0.5;
	
	public static int state = 1, action = ACTION_NONE;
	public static int[] movementDirection = new int[] { DIRECTION_NONE, DIRECTION_NONE };
	public static int slashImageIndex = 0;
	
	public static int deathFrame = -1;
	
	public static Player player = new Player(playerX, playerY);
	
	public Player(double x, double y) {
		super(x, y);
	}

	public void tick() {
		x = playerX;
		y = playerY;
		if (action == ACTION_SLASH && Game.frameCount % 4 == 0) {
			slashImageIndex++;
			if (slashImageIndex == 2) {
//				Creature.slash();
			} else if (slashImageIndex == 5) {
				slashImageIndex = 0;
				action = ACTION_NONE;
			}
		}
		playerHealthBar.tick();
		if (playerHealthBar.health <= 0) {
			state = STATE_DEAD;
			if (deathFrame == -1) {
				deathFrame = Game.frameCount;
			}
		}
	}
	
	public void render(Graphics2D g) {
		ItemCollectionNotification.render(g);
		CharacterGraphics.renderPlayer(g);
	}
	
	@Override
	public void drawDebug(Graphics2D g) {
		Toolbox.drawPosition(playerX, playerY, g);
	}

}
