package player;

import algorithms.Toolbox;
import entities.Campfire;
import entities.Hagrid;
import entities.Obstacle;
import main.Game;
import main.Launcher;
import tiles.Tiles;

public class PlayerMovement {

	public static double speed = 1.0, adjustmentFactor = 1.2;
	public static double perFrameWhole = 2.0 / Game.idealFrameRate * speed;
	public static double velocityX = 0, velocityY = 0;

	public static int requestID = 0;
	public static final int REQUEST_UP = 2, REQUEST_DOWN = 3;
	public static final int REQUEST_RIGHT = 4, REQUEST_RIGHT_UP = 5, REQUEST_RIGHT_DOWN = 6;
	public static final int REQUEST_LEFT = -4, REQUEST_LEFT_UP = -5, REQUEST_LEFT_DOWN = -6;

	public static void collision(double fromX, double fromY, double toX, double toY) {
		for (int i = 0; i < Obstacle.obstacles.size(); i++) {
			Obstacle o = Obstacle.obstacles.get(i);
			if (o.collision(fromX, fromY, toX, toY)) {
				break;
			}
		}

		// Automatically close campfire when far away
		if (Campfire.selectedCampfire != null) {
			double orthoDistance = Campfire.selectedCampfire.orthoDistance();
			if (orthoDistance > 130) {
				Campfire.showCookingUI = false;
				Campfire.selectedCampfire = null;
			}
		}

		// Automatically stop interaction with Hagrid when far away
		if (Hagrid.interacting) {
			double orthoDistance = Hagrid.Hagrid.orthoDistance();
			if (orthoDistance > Launcher.WIDTH * 0.6) {
				Hagrid.interacting = false;
				Hagrid.lastInteraction = -1;
			}
		}

	}

	public static void updateDelta() {
		if (Toolbox.currentTile() == Tiles.TILE_TYPE_WATER) {
			speed = Math.max(0.4, speed - 0.02);
		} else {
			speed = Math.min(1.0, speed + 0.02);
		}
		perFrameWhole = 2.0 / Game.averageFrameRate * speed;
	}

	public static void moveLeftRequest() {
		velocityX = perFrameWhole * (-0.500);
		velocityY = perFrameWhole * (-0.500);
		requestID = REQUEST_LEFT;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveRightRequest() {
		velocityX = perFrameWhole * (+0.500);
		velocityY = perFrameWhole * (+0.500);
		requestID = REQUEST_RIGHT;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveDownRequest() {
		velocityX = perFrameWhole * (+1.000);
		velocityY = perFrameWhole * (-1.000);
		requestID = REQUEST_DOWN;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveUpRequest() {
		velocityX = perFrameWhole * (-1.000);
		velocityY = perFrameWhole * (+1.000);
		requestID = REQUEST_UP;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveRightUpRequest() {
		velocityX = perFrameWhole * (-0.356) * adjustmentFactor;
		velocityY = perFrameWhole * (+1.061) * adjustmentFactor;
		requestID = REQUEST_RIGHT_UP;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveLeftUpRequest() {
		velocityX = perFrameWhole * (-1.061) * adjustmentFactor;
		velocityY = perFrameWhole * (+0.356) * adjustmentFactor;
		requestID = REQUEST_LEFT_UP;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveLeftDownRequest() {
		velocityX = perFrameWhole * (+0.356) * adjustmentFactor;
		velocityY = perFrameWhole * (-1.061) * adjustmentFactor;
		requestID = REQUEST_LEFT_DOWN;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

	public static void moveRightDownRequest() {
		velocityX = perFrameWhole * (+1.061) * adjustmentFactor;
		velocityY = perFrameWhole * (-0.356) * adjustmentFactor;
		requestID = REQUEST_RIGHT_DOWN;
		collision(Player.playerX, Player.playerY, Player.playerX + velocityX, Player.playerY + velocityY);
	}

}
