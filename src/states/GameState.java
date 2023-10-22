package states;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;

import algorithms.Toolbox;
import creature.Creature;
import creature.MushroomMonster;
import creature.Rabbit;
import creature.Snail;
import display.ImageLoader;
import entities.Campfire;
import entities.Crafting;
import entities.CreatureControl;
import entities.Entity;
import entities.EntityControl;
import entities.Hagrid;
import entities.ItemCollectionNotification;
import entities.Obstacle;
import entities.ObstacleControl;
import entities.ObstacleScreenYComparator;
import entities.TallGrass;
import entities.Tree;
import entities.WheatPatch;
import environment.EnvironmentManager;
import input.KeyManager;
import inventory.InventoryUI;
import inventory.Item;
import main.Game;
import main.Launcher;
import player.CharacterGraphics;
import player.Player;
import player.PlayerMovement;
import tiles.Tiles;

public class GameState extends State {

	public static double zoom = 1.0;

	public static int tileWidth = (int) (Launcher.WIDTH * 0.1953125), tileHeight = tileWidth / 2,
			halfTileWidth = tileHeight, halfTileHeight = tileHeight / 2;
	public static final double TILE_HEIGHT_TO_WIDTH_RATIO = (double) tileHeight / tileWidth;

	public static boolean debug = false;
	public static Obstacle nearestHarvestableObstacle = null;
	public static Creature nearestHarvestableCreature = null;
	public static Campfire nearestCampfire = null;

	public GameState(Game game) {
		super(game);
	}

	@Override
	public void initialize() {
		Obstacle.obstacles.sort(new ObstacleScreenYComparator());
		Tiles.initializeTiles();
	}

	@Override
	public void tick(Graphics2D g) {

		EntityControl.update();
		ObstacleControl.update();
		CreatureControl.update();
		CreatureControl.tick();

		PlayerMovement.updateDelta();
		Launcher.game.getKeyManager().tick();
		nearestHarvestableObstacle = Obstacle.nearestHarvestableEntity();
		nearestCampfire = Obstacle.nearestCampfire();
		nearestHarvestableCreature = Creature.nearestHarvestableCreature();
		ItemCollectionNotification.tick();

		InventoryUI.setInventoryIcons();
		Campfire.setInventoryIcons();
		Crafting.setInventoryIcons();

		for (Entity e : Entity.entities) {
			e.tick();
		}

	}

	@Override
	public void render(Graphics2D g) {

		renderGround(g);

		Collections.sort(Entity.entities);
		for (Entity e : Entity.entities) {
			if (Toolbox.pixelsFromCenter(e.x, e.y) > Launcher.WIDTH * 2) {
				continue;
			}
			e.render(g);
			if (debug) {
				e.drawDebug(g);
			}
			if (e instanceof Obstacle) {
				Obstacle o = (Obstacle) e;
				if (o.showHarvestIcon && o.readyForHarvest) {
					o.drawHarvestIcon(g, nearestHarvestableObstacle == o && o.inHarvestRange());
				}
			}

		}

		EnvironmentManager.createEnvironment(g);
		InventoryUI.renderInventory(g);
		Campfire.renderCookingUI(g);
		Crafting.renderCraftingUI(g);
		Player.playerHealthBar.render(g);

		if (KeyManager.queueHarvest) {
			KeyManager.harvest();
			KeyManager.queueHarvest = false;
		}
		
		if (debug) {
			g.setColor(new Color(255, 255, 255));
			Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
			Toolbox.drawText(g, Creature.creatures.size() + " creatures in list", 10, 10);
		}
		
		if (Player.state == Player.STATE_DEAD) {
			Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_BOTTOM);
			Toolbox.setFontSize(g, 16);
			g.setColor(Color.BLACK);
			Toolbox.drawText(g, "(Cliquez pour renaître)", 12, Launcher.HEIGHT - 12);
		}

	}

	public void renderGround(Graphics2D g) {
		int halfWidth = (int) (halfTileWidth * zoom), halfHeight = (int) (halfTileHeight * zoom);
		int t = (int) Math.ceil((0.51311 * Launcher.WIDTH) / halfWidth + 0.5);
		int nearestX = (int) Math.round(Player.playerX), nearestY = (int) Math.round(Player.playerY);
		double fractionX = Player.playerX - nearestX, fractionY = Player.playerY - nearestY;
		int shiftX = -(int) ((fractionX + fractionY) * halfWidth);
		int shiftY = -(int) ((fractionX - fractionY) * halfHeight);
		for (int xDisplacement = t; xDisplacement >= -t; xDisplacement--) {
			for (int yDisplacement = t; yDisplacement >= -t; yDisplacement--) {
				int tileX = (xDisplacement + yDisplacement) * halfWidth;
				int tileY = (xDisplacement - yDisplacement) * halfHeight;
				int tileLeft = Launcher.WIDTH / 2 + tileX - halfWidth + shiftX;
				int tileRight = tileLeft + halfWidth + halfWidth;
				int tileTop = Launcher.HEIGHT / 2 + tileY - halfHeight + shiftY
						+ (int) (CharacterGraphics.playerRenderHeight / 2 * zoom);
				int tileBottom = tileTop + halfHeight + halfHeight;
				if (tileLeft > Launcher.WIDTH || tileRight < 0 || tileTop > Launcher.HEIGHT || tileBottom < 0) {
					continue;
				}

				int tile = Tiles.fetchGroundTexture(nearestX + xDisplacement, nearestY + yDisplacement);
				if (tile == Tiles.TILE_GRASS_1) {
					g.drawImage(Tiles.TEXTURE_GRASS_1, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
				} else if (tile == Tiles.TILE_GRASS_2) {
					g.drawImage(Tiles.TEXTURE_GRASS_2, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
				} else if (tile == Tiles.TILE_GRASS_3) {
					g.drawImage(Tiles.TEXTURE_GRASS_3, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
				} else if (tile == Tiles.TILE_DIRT) {
					g.drawImage(Tiles.TEXTURE_DIRT, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
				} else if (tile == Tiles.TILE_WATER) {
					g.drawImage(Tiles.TEXTURE_WATER_BACKGROUND, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);

					float minimum = 0.25f;
					float frequency = 20.0f;
					float value1 = (float) Toolbox.map(Math.sin(Game.frameCount / frequency), -1, 1, minimum, 1);
					float value2 = (float) Toolbox.map(Math.sin(Game.frameCount / frequency), -1, 1, 1, minimum);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value1));
					g.drawImage(Tiles.TEXTURE_WATER_RIPPLE_1, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, value2));
					g.drawImage(Tiles.TEXTURE_WATER_RIPPLE_2, tileLeft, tileTop, (int) (tileWidth * zoom),
							(int) (tileHeight * zoom), null);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}
		}
	}

	public static void loadImages() {

		// Tiles
		Tiles.TEXTURE_GRASS_1 = ImageLoader.loadImage("/textures/Tiles/Grass 1.png");
		Tiles.TEXTURE_GRASS_2 = ImageLoader.loadImage("/textures/Tiles/Grass 2.png");
		Tiles.TEXTURE_GRASS_3 = ImageLoader.loadImage("/textures/Tiles/Grass 3.png");
		Tiles.TEXTURE_WATER_BACKGROUND = ImageLoader.loadImage("/textures/Tiles/Water Background.png");
		Tiles.TEXTURE_WATER_RIPPLE_1 = ImageLoader.loadImage("/textures/Tiles/Water Ripple 1.png");
		Tiles.TEXTURE_WATER_RIPPLE_2 = ImageLoader.loadImage("/textures/Tiles/Water Ripple 2.png");
		Tiles.TEXTURE_DIRT = ImageLoader.loadImage("/textures/Tiles/Dirt 1.png");

		// Wheat Patch
		WheatPatch.WHEAT_PATCH[0] = ImageLoader.loadImage("/textures/Entities/Wheat Patch/Wheat Patch 0.png");
		WheatPatch.WHEAT_PATCH[1] = ImageLoader.loadImage("/textures/Entities/Wheat Patch/Wheat Patch 1.png");

		// Tree
		for (int i = 0; i <= 1; i++) {
			Tree.TREES[i] = ImageLoader.loadImage("/textures/Entities/Tree/Tree " + i + ".png");
		}

		// Campfire
		for (int i = 0; i <= 6; i++) {
			Campfire.CAMPFIRE[i] = ImageLoader.loadImage("/textures/Entities/Campfire/Campfire " + i + ".png");
		}

		// Tulip
		TallGrass.TULIP = ImageLoader.loadImage("/textures/Entities/Tulip.png");

		// Player
		for (int i = 0; i <= 3; i++) {
			CharacterGraphics.IDLE_RIGHT[i] = ImageLoader.loadImage("/textures/Character/Idle/Idle " + i + ".png");
			CharacterGraphics.IDLE_LEFT[i] = Toolbox.flipImage(CharacterGraphics.IDLE_RIGHT[i]);
		}
		for (int i = 0; i <= 5; i++) {
			CharacterGraphics.RUN_RIGHT[i] = ImageLoader.loadImage("/textures/Character/Run/Run " + i + ".png");
			CharacterGraphics.RUN_LEFT[i] = Toolbox.flipImage(CharacterGraphics.RUN_RIGHT[i]);
		}
		for (int i = 0; i <= 4; i++) {
			CharacterGraphics.SLASH_RIGHT[i] = ImageLoader.loadImage("/textures/Character/Slash/Slash " + i + ".png");
			CharacterGraphics.SLASH_LEFT[i] = Toolbox.flipImage(CharacterGraphics.SLASH_RIGHT[i]);
		}
		for (int i = 0; i <= 6; i++) {
			CharacterGraphics.DIE_RIGHT[i] = ImageLoader.loadImage("/textures/Character/Death/Death " + i + ".png");
			CharacterGraphics.DIE_LEFT[i] = Toolbox.flipImage(CharacterGraphics.DIE_RIGHT[i]);
		}

		// Hagrid
		for (int i = 0; i <= 3; i++) {
			Hagrid.HAGRID_IDLE_RIGHT[i] = ImageLoader.loadImage("/textures/Entities/Hagrid/Idle/Idle " + i + ".png");
			Hagrid.HAGRID_IDLE_LEFT[i] = Toolbox.flipImage(Hagrid.HAGRID_IDLE_RIGHT[i]);
		}
		for (int i = 0; i <= 3; i++) {
			Hagrid.HAGRID_SPEAK_RIGHT[i] = ImageLoader.loadImage("/textures/Entities/Hagrid/Speak/Speak " + i + ".png");
			Hagrid.HAGRID_SPEAK_LEFT[i] = Toolbox.flipImage(Hagrid.HAGRID_SPEAK_RIGHT[i]);
		}

		// Item Icons
		InventoryUI.ITEM_ICONS[Item.ID_ACORN] = ImageLoader.loadImage("/textures/Items/Acorn.png");
		InventoryUI.ITEM_ICONS[Item.ID_BRANCH] = ImageLoader.loadImage("/textures/Items/Branch.png");
		InventoryUI.ITEM_ICONS[Item.ID_ROASTED_ACORN] = ImageLoader.loadImage("/textures/Items/Roasted Acorn.png");
		InventoryUI.ITEM_ICONS[Item.ID_RAW_RABBIT] = ImageLoader.loadImage("/textures/Items/Raw Rabbit.png");
		InventoryUI.ITEM_ICONS[Item.ID_COOKED_RABBIT] = ImageLoader.loadImage("/textures/Items/Cooked Rabbit.png");
		InventoryUI.ITEM_ICONS[Item.ID_MUSHROOM] = ImageLoader.loadImage("/textures/Items/Raw Mushroom.png");
		InventoryUI.ITEM_ICONS[Item.ID_WHEAT_SEED] = ImageLoader.loadImage("/textures/Items/Wheat Seed.png");
		InventoryUI.ITEM_ICONS[Item.ID_WHEAT] = ImageLoader.loadImage("/textures/Items/Wheat.png");
		InventoryUI.ITEM_ICONS[Item.ID_SNAIL] = ImageLoader.loadImage("/textures/Items/Snail.png");
		InventoryUI.ITEM_ICONS[Item.ID_ESCARGOT] = ImageLoader.loadImage("/textures/Items/Escargot.png");
		InventoryUI.ITEM_ICONS[Item.ID_LOAF_OF_BREAD] = ImageLoader.loadImage("/textures/Items/Loaf of Bread.png");
		InventoryUI.ITEM_ICONS[Item.ID_CROISSANT] = ImageLoader.loadImage("/textures/Items/Croissant.png");
		InventoryUI.ITEM_ICONS[Item.ID_MUSHROOM_STEW] = ImageLoader.loadImage("/textures/Items/Mushroom Stew.png");

		// Harvest Icons
		Obstacle.HARVEST_ICON = ImageLoader.loadImage("/textures/Misc/Harvest Icon.png");
		Obstacle.HIGHLIGHTED_HARVEST_ICON = ImageLoader.loadImage("/textures/Misc/Highlighted Harvest Icon.png");

		// Rabbit
		for (int i = 0; i <= 2; i++) {
			Rabbit.RABBIT_IDLE_RIGHT[i] = ImageLoader.loadImage("/textures/Creatures/Rabbit/Rabbit Idle " + i + ".png");
			Rabbit.RABBIT_IDLE_LEFT[i] = Toolbox.flipImage(Rabbit.RABBIT_IDLE_RIGHT[i]);
		}
		for (int i = 0; i <= 5; i++) {
			Rabbit.RABBIT_RUNNING_RIGHT[i] = ImageLoader
					.loadImage("/textures/Creatures/Rabbit/Rabbit Running " + i + ".png");
			Rabbit.RABBIT_RUNNING_LEFT[i] = Toolbox.flipImage(Rabbit.RABBIT_RUNNING_RIGHT[i]);
		}
		for (int i = 0; i <= 2; i++) {
			Rabbit.RABBIT_DEAD_RIGHT[i] = ImageLoader.loadImage("/textures/Creatures/Rabbit/Rabbit Dead " + i + ".png");
			Rabbit.RABBIT_DEAD_LEFT[i] = Toolbox.flipImage(Rabbit.RABBIT_RUNNING_RIGHT[i]);
		}

		// Mushroom Monster
		for (int i = 0; i <= 3; i++) {
			MushroomMonster.MUSHROOM_IDLE_RIGHT[i] = ImageLoader
					.loadImage("/textures/Creatures/Mushroom Monster/Idle/Idle " + i + ".png");
			MushroomMonster.MUSHROOM_IDLE_LEFT[i] = Toolbox.flipImage(MushroomMonster.MUSHROOM_IDLE_RIGHT[i]);
		}
		for (int i = 0; i <= 7; i++) {
			MushroomMonster.MUSHROOM_RUN_RIGHT[i] = ImageLoader
					.loadImage("/textures/Creatures/Mushroom Monster/Run/Run " + i + ".png");
			MushroomMonster.MUSHROOM_RUN_LEFT[i] = Toolbox.flipImage(MushroomMonster.MUSHROOM_RUN_RIGHT[i]);
		}
		for (int i = 0; i <= 3; i++) {
			MushroomMonster.MUSHROOM_DIE_RIGHT[i] = ImageLoader
					.loadImage("/textures/Creatures/Mushroom Monster/Death/Death " + i + ".png");
			MushroomMonster.MUSHROOM_DIE_LEFT[i] = Toolbox.flipImage(MushroomMonster.MUSHROOM_DIE_RIGHT[i]);
		}
		for (int i = 0; i <= 7; i++) {
			MushroomMonster.MUSHROOM_ATTACK_RIGHT[i] = ImageLoader
					.loadImage("/textures/Creatures/Mushroom Monster/Attack/Attack " + i + ".png");
			MushroomMonster.MUSHROOM_ATTACK_LEFT[i] = Toolbox.flipImage(MushroomMonster.MUSHROOM_ATTACK_RIGHT[i]);
		}

		// Snail
		Snail.SNAIL_LEFT = ImageLoader.loadImage("/textures/Creatures/Snail.png");
		Snail.SNAIL_RIGHT = Toolbox.flipImage(Snail.SNAIL_LEFT);

	}

	public String toString() {
		return "Game State";
	}

}