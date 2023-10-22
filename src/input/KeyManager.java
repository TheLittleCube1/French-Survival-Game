package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import algorithms.Toolbox;
import creature.Creature;
import entities.Campfire;
import entities.Crafting;
import entities.Hagrid;
import entities.Obstacle;
import entities.WheatPatch;
import inventory.Inventory;
import inventory.InventoryUI;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import player.Player;
import player.PlayerMovement;
import states.GameState;
import tiles.Tiles;

public class KeyManager implements KeyListener {

	boolean[] held = new boolean[1000];
	
	public static boolean queueHarvest = false;

	public KeyManager() {

	}

	public void tick() {
		
		if (Player.state == Player.STATE_DEAD) {
			return;
		}
		
		if (Player.action != Player.ACTION_SLASH) {
			Player.state = 0;
			boolean left = held[65] || held[37], up = held[87] || held[38], right = held[68] || held[39],
					down = held[83] || held[40];
			if (right && up) {
				PlayerMovement.moveRightUpRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_RIGHT, Player.DIRECTION_UP };
				Player.facingRight = true;
			} else if (!right && !left && up) {
				PlayerMovement.moveUpRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_NONE, Player.DIRECTION_UP };
			} else if (left && up) {
				PlayerMovement.moveLeftUpRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_LEFT, Player.DIRECTION_UP };
				Player.facingRight = false;
			} else if (left && !up && !down) {
				PlayerMovement.moveLeftRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_LEFT, Player.DIRECTION_NONE };
				Player.facingRight = false;
			} else if (left && down) {
				PlayerMovement.moveLeftDownRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_LEFT, Player.DIRECTION_DOWN };
				Player.facingRight = false;
			} else if (!left && !right && down) {
				PlayerMovement.moveDownRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_NONE, Player.DIRECTION_DOWN };
			} else if (right && down) {
				PlayerMovement.moveRightDownRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_RIGHT, Player.DIRECTION_DOWN };
				Player.facingRight = true;
			} else if (right && !up && !down) {
				PlayerMovement.moveRightRequest();
				Player.state = Player.STATE_RUNNING;
				Player.movementDirection = new int[] { Player.DIRECTION_RIGHT, Player.DIRECTION_NONE };
				Player.facingRight = true;
			} else {
				PlayerMovement.velocityX = 0;
				PlayerMovement.velocityY = 0;
				if (Player.facingRight) {
					Player.state = Player.STATE_IDLE;
					Player.movementDirection = new int[] { Player.DIRECTION_RIGHT, Player.DIRECTION_NONE };
				} else {
					Player.state = Player.STATE_IDLE;
					Player.movementDirection = new int[] { Player.DIRECTION_LEFT, Player.DIRECTION_NONE };
				}
			}
			Player.playerX += PlayerMovement.velocityX;
			Player.playerY += PlayerMovement.velocityY;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		held[keyCode] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		if (Player.state == Player.STATE_DEAD) {
			return;
		}
		
		int keyCode = e.getKeyCode();
		held[keyCode] = false;
		
		char c = e.getKeyChar();
		if (c == 'e') {
			InventoryUI.showInventory = !InventoryUI.showInventory;
			Crafting.showCraftingUI = false;
			Campfire.showCookingUI = false;
			Campfire.selectedCampfire = null;
		} else if (c == 'c') {
			queueHarvest = true;
		} else if (c == 'f') {
			
			if (Hagrid.interacting) {
				if (Inventory.quantity(Item.mission[Hagrid.mission]) >= 1 && Hagrid.messageIndex == 0) {
					Hagrid.shuffleMission();
					Inventory.removeItem(Item.mission[Hagrid.mission]);
				}
				Hagrid.messageIndex++;
				Hagrid.lastInteraction = Game.frameCount / Hagrid.framesPerTick;
				if (Hagrid.messageIndex >= Hagrid.messages.length) {
					Hagrid.interacting = false;
					Hagrid.messageIndex = 0;
				}
				return;
			}
			
			double campfireDistance, NPCDistance;
			if (GameState.nearestCampfire == null || GameState.nearestCampfire.cookingInProgress) {
				campfireDistance = 1e18;
			} else {
				campfireDistance = Toolbox.pixelsFromPlayer(GameState.nearestCampfire.x, GameState.nearestCampfire.y);
			}
			NPCDistance = Toolbox.pixelsFromPlayer(Hagrid.Hagrid.x, Hagrid.Hagrid.y);
			if (campfireDistance < NPCDistance) {
				GameState.nearestCampfire.interact();
			} else {
				Hagrid.Hagrid.interact();
			}
		} else if (c == 'z') {
			GameState.zoom = 1.0;
			return;
		} else if (c == '1') {
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
			for (int ID = 0; ID < Inventory.inventory.length; ID++) {
				stacks.add(new ItemStack(ID, 1));
			}
			Inventory.addItemStacks(stacks);
		} else if (c == 'b') {
			GameState.debug = !GameState.debug;
		} else if (c == 'g') {
			WheatPatch.plant();
		} else if (c == 'h') {
			Player.playerHealthBar.takeHit(20);
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	public static void harvest() {
		
		WheatPatch currentWheatPatch = Tiles.getWheatPatch((int) Math.round(Player.playerX), (int) Math.round(Player.playerY));
		if (currentWheatPatch != null && currentWheatPatch.state == WheatPatch.STATE_MATURE) {
			currentWheatPatch.harvest();
			return;
		}
		
		Obstacle nearestEntity = GameState.nearestHarvestableObstacle;
		Creature nearestCreature = GameState.nearestHarvestableCreature;
		if (nearestEntity == null) {
			if (nearestCreature == null) {
				// Nothing to harvest
				return;
			} else {
				// Harvest animal
				if (nearestCreature.inHarvestRange()) {
					nearestCreature.harvest();
				}
			}
		} else {
			if (nearestCreature == null) {
				// Harvest entity
				if (nearestEntity.inHarvestRange()) {
					nearestEntity.harvest();
				}
			} else {
				int entityDist = Toolbox.pixelsFromPlayer(nearestEntity.x, nearestEntity.y);
				int creatureDist = Toolbox.pixelsFromPlayer(nearestCreature.x, nearestCreature.y);
				if (entityDist < creatureDist) {
					// Harvest entity
					if (nearestEntity.inHarvestRange()) {
						nearestEntity.harvest();
					}
				} else {
					// Harvest animal
					if (nearestCreature.inHarvestRange()) {
						nearestCreature.harvest();
					}
				}
			}
		}
	}

}
