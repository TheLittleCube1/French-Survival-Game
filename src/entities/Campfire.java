package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.Toolbox;
import data.CookingRecipe;
import inventory.Inventory;
import inventory.InventoryUI;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import main.Launcher;
import player.PlayerMovement;
import states.GameState;

public class Campfire extends Obstacle {

	public static final int IMAGE_WIDTH = (int) (Launcher.WIDTH * 0.2), IMAGE_HEIGHT = (int) (IMAGE_WIDTH * 1.05);
	public static BufferedImage[] CAMPFIRE = new BufferedImage[7];
	public static boolean showCookingUI = false;
	public static Campfire selectedCampfire = null;
	public static int cookingMenuPage = 0;

	public static int[] inventoryIcons = new int[20];

	public static final int INVENTORY_PANEL_LEFT = (int) (Launcher.WIDTH * 0.6),
			INVENTORY_PANEL_TOP = (int) (Launcher.HEIGHT * 0.11565836298932385);
	public static final int INVENTORY_PANEL_WIDTH = (int) (Launcher.WIDTH * 0.35),
			INVENTORY_PANEL_HEIGHT = Launcher.HEIGHT - 2 * INVENTORY_PANEL_TOP;
	public static final int INVENTORY_PANEL_ARC_DIAMETER = (int) (Launcher.WIDTH * 0.025);

	public static final int INFORMATION_PANEL_LEFT = Launcher.WIDTH - (INVENTORY_PANEL_LEFT + INVENTORY_PANEL_WIDTH);
	public static final int INFORMATION_PANEL_TOP = INVENTORY_PANEL_TOP;
	public static final int INFORMATION_PANEL_WIDTH = INVENTORY_PANEL_WIDTH;
	public static final int INFORMATION_PANEL_HEIGHT = INVENTORY_PANEL_HEIGHT;
	public static final int INFORMATION_PANEL_ARC_DIAMETER = INVENTORY_PANEL_ARC_DIAMETER;
	public static final int ingredientSquareLength = 30;
	public static final double spacing = 1.4;
	public static final double scale = 0.35;

	public static final int iconsPerRow = 4, padding = INVENTORY_PANEL_WIDTH / 20;
	public static final int iconSideLength = (INVENTORY_PANEL_WIDTH - (iconsPerRow + 1) * padding) / iconsPerRow;

	public static List<ItemStack> ingredients = new ArrayList<ItemStack>();
	public static Map<Integer, Integer> squareMap = new HashMap<Integer, Integer>();
	public static CookingRecipe selectedRecipeSuggestion = null;

	public boolean cookingInProgress = false;
	public CookingRecipe cookingRecipe = null;
	public int cookingQuantityQueued = 1;
	public int cookingQuantityFinished = 0;
	public int lastFrame;

	public Campfire(double x, double y) {
		super(x, y, 1);
		this.x = x;
		this.y = y;
		this.harvestDistance = 70;
		
		harvestable = true;
		readyForHarvest = false;
	}
	
	public void clear() {
		cookingQuantityQueued = 1;
		cookingQuantityFinished = 0;
		cookingRecipe = null;
		cookingInProgress = false;
		selectedRecipeSuggestion = null;
		ingredients.clear();
		squareMap.clear();
	}

	@Override
	public void interact() {
		
		double orthoDistance = orthoDistance();
		if (orthoDistance < 70) {
			showCookingUI = !showCookingUI;
			InventoryUI.showInventory = false;
			InventoryUI.selectedInventorySpace = -1;
			Crafting.showCraftingUI = false;
			if (showCookingUI) {
				selectedCampfire = this;
			} else {
				selectedCampfire = null;
			}
		} else {
			showCookingUI = false;
			selectedCampfire = null;
		}

		clear();

	}

	@Override
	public void harvest() {
		cookingRecipe.make(cookingQuantityFinished);
		if (cookingQuantityFinished == cookingQuantityQueued) {
			cookingInProgress = false;
			cookingRecipe = null;
			cookingQuantityQueued = 1;
			cookingQuantityFinished = 0;
		} else {
			cookingQuantityQueued -= cookingQuantityFinished;
			cookingQuantityFinished = 0;
		}
	}

	public static void renderCookingUI(Graphics2D g) {

		renderInventoryPanel(g);
		renderCookingPanel(g);
		
		if (showCookingUI) {
			g.setColor(Color.BLACK);
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 20);
			Toolbox.drawText(g, "Feu de Camp", Launcher.WIDTH / 2, INVENTORY_PANEL_TOP / 2);
		}

	}

	public static void renderCookingPanel(Graphics2D g) {
		if (!showCookingUI) {
			return;
		}

		int baseColor = 140;

		g.setColor(new Color(baseColor, baseColor, baseColor));
		g.fillRoundRect(INFORMATION_PANEL_LEFT, INFORMATION_PANEL_TOP, INFORMATION_PANEL_WIDTH,
				INFORMATION_PANEL_HEIGHT, INFORMATION_PANEL_ARC_DIAMETER, INFORMATION_PANEL_ARC_DIAMETER);

		Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
		g.setColor(new Color(40, 40, 40));
		Toolbox.setFontSize(g, 18);
		Toolbox.drawText(g, "Ingrédients", INFORMATION_PANEL_LEFT + padding, INFORMATION_PANEL_TOP + padding);

		int cookingPadding = Launcher.WIDTH / 52;
		int squareLength = (INFORMATION_PANEL_WIDTH - 6 * cookingPadding - 20) / 5;

		Toolbox.setAlign(Toolbox.ALIGN_RIGHT, Toolbox.ALIGN_BOTTOM);
		Toolbox.setFontSize(g, 12);
		for (int x = 0; x < 5; x++) {

			int cornerX = INFORMATION_PANEL_LEFT + cookingPadding * (x + 1) + squareLength * x + 10;
			int cornerY = (int) (Launcher.HEIGHT * 0.202);

			g.setColor(new Color(baseColor + 5, baseColor + 5, baseColor + 5));
			g.fillRect(cornerX, cornerY, squareLength, squareLength);

			g.setColor(new Color(baseColor + 10, baseColor + 10, baseColor + 10));
			g.setStroke(new BasicStroke(2));
			g.drawRect(cornerX, cornerY, squareLength, squareLength);

			if (x < ingredients.size()) {
				g.drawImage(InventoryUI.ITEM_ICONS[ingredients.get(x).itemID], cornerX, cornerY, squareLength,
						squareLength, null);
				g.setColor(Color.BLACK);
				Toolbox.drawText(g, "" + ingredients.get(x).quantity, cornerX + squareLength - 2,
						cornerY + squareLength - 2);
			}

		}

		Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
		g.setColor(new Color(40, 40, 40));
		Toolbox.setFontSize(g, 18);
		Toolbox.drawText(g, "Livre des Recettes", INFORMATION_PANEL_LEFT + padding, (int) (Launcher.HEIGHT * 0.33));

		for (int i = 5 * cookingMenuPage; i < CookingRecipe.cookingRecipes.length && i < 5 * (cookingMenuPage + 1); i++) {
			CookingRecipe suggestion = CookingRecipe.cookingRecipes[i];
			Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
			Toolbox.setFontSize(g, 15);

			// Recipe Suggestion Rectangle
			if (suggestion.enough()) {
				g.setColor(new Color(baseColor - 7, baseColor - 7, baseColor - 7));
			} else {
				g.setColor(new Color(110, 110, 110));
			}
			g.fillRoundRect(INFORMATION_PANEL_LEFT + padding,
					(int) (Launcher.HEIGHT * 0.4 + i * ingredientSquareLength * spacing - 10),
					INFORMATION_PANEL_WIDTH - (int) (2 * padding), (int) (ingredientSquareLength * 1.28), 3, 3);

			// Draw Outline If Selected
			if (selectedRecipeSuggestion == CookingRecipe.cookingRecipes[i]) {
				g.setColor(new Color(80, 80, 80));
				g.drawRoundRect(INFORMATION_PANEL_LEFT + padding,
						(int) (Launcher.HEIGHT * 0.4 + i * ingredientSquareLength * spacing - 10),
						INFORMATION_PANEL_WIDTH - (int) (2 * padding), (int) (ingredientSquareLength * 1.28), 3, 3);
			}

			// Label
			g.setColor(new Color(60, 60, 60));
			Toolbox.drawText(g, Item.IDToName[suggestion.productItemID],
					INFORMATION_PANEL_LEFT + (int) (padding * 1.3),
					(int) (Launcher.HEIGHT * 0.403 + i * ingredientSquareLength * spacing));

			// Ingredients Required
			for (int ingredientBox = 0; ingredientBox < suggestion.ingredients.size(); ingredientBox++) {

				int cornerX = (int) (INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH - padding * 1.3
						- ingredientSquareLength - ingredientSquareLength * 1.25 * ingredientBox) + 2;
				int cornerY = (int) (Launcher.HEIGHT * 0.4 + i * ingredientSquareLength * spacing
						- (ingredientSquareLength - 15) / 2);
				ItemStack stack = suggestion.ingredients.get(suggestion.ingredients.size() - ingredientBox - 1);
				int countRequired = stack.quantity;
				if (!suggestion.enough()) {
					g.setColor(new Color(120, 120, 120));
					g.fillRect(cornerX, cornerY, ingredientSquareLength, ingredientSquareLength);
					g.setColor(new Color(135, 135, 135));
					g.setStroke(new BasicStroke(2));
					g.drawRect(cornerX, cornerY, ingredientSquareLength, ingredientSquareLength);
				} else {
					g.setColor(new Color(baseColor + 5, baseColor + 5, baseColor + 5));
					g.fillRect(cornerX, cornerY, ingredientSquareLength, ingredientSquareLength);
					g.setColor(new Color(baseColor + 10, baseColor + 10, baseColor + 10));
					g.setStroke(new BasicStroke(2));
					g.drawRect(cornerX, cornerY, ingredientSquareLength, ingredientSquareLength);
				}

				g.drawImage(
						InventoryUI.ITEM_ICONS[suggestion.ingredients
								.get(suggestion.ingredients.size() - ingredientBox - 1).itemID],
						cornerX, cornerY, ingredientSquareLength, ingredientSquareLength, null);
				Toolbox.setAlign(Toolbox.ALIGN_RIGHT, Toolbox.ALIGN_BOTTOM);
				Toolbox.setFontSize(g, 10);
				g.setColor(Color.BLACK);
				Toolbox.drawText(g, "" + countRequired, cornerX + ingredientSquareLength - 1,
						cornerY + ingredientSquareLength - 1);

			}
		}

		if (selectedRecipeSuggestion != null) {

			g.setColor(new Color(baseColor - 10, baseColor - 10, baseColor - 10));
			g.fillRoundRect(INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2 - 50, (int) (Launcher.HEIGHT * 0.79),
					100, 40, 3, 3);
			g.setColor(new Color(60, 60, 60));
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 18);
			Toolbox.drawText(g, "Cuisiner", INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2,
					(int) (Launcher.HEIGHT * 0.79) + 20);

		}

	}

	public static void renderInventoryPanel(Graphics2D g) {
		if (!showCookingUI) {
			return;
		}

		int baseColor = 140;

		g.setColor(new Color(baseColor, baseColor, baseColor));
		g.fillRoundRect(INVENTORY_PANEL_LEFT, INVENTORY_PANEL_TOP, INVENTORY_PANEL_WIDTH, INVENTORY_PANEL_HEIGHT,
				INVENTORY_PANEL_ARC_DIAMETER, INVENTORY_PANEL_ARC_DIAMETER);

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 4; x++) {

				int cornerX = INVENTORY_PANEL_LEFT + padding * (x + 1) + iconSideLength * x;
				int cornerY = INVENTORY_PANEL_TOP + padding * (y + 1) + iconSideLength * y;

				g.setColor(new Color(baseColor + 5, baseColor + 5, baseColor + 5));
				g.fillRect(cornerX, cornerY, iconSideLength, iconSideLength);

				g.setColor(new Color(baseColor + 10, baseColor + 10, baseColor + 10));
				g.setStroke(new BasicStroke(3));
				g.drawRect(cornerX, cornerY, iconSideLength, iconSideLength);

			}
		}

		boolean complete = false;
		g.setColor(Color.BLACK);
		Toolbox.setFontSize(g, 18);
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 4; x++) {

				int iconNumber = 4 * y + x;
				int itemID = inventoryIcons[iconNumber];

				if (itemID == -1) {
					complete = true;
					break;
				}

				int cornerX = INVENTORY_PANEL_LEFT + padding * (x + 1) + iconSideLength * x;
				int cornerY = INVENTORY_PANEL_TOP + padding * (y + 1) + iconSideLength * y;

				g.drawImage(InventoryUI.ITEM_ICONS[itemID], cornerX, cornerY, iconSideLength, iconSideLength, null);

				Toolbox.setAlign(Toolbox.ALIGN_RIGHT, Toolbox.ALIGN_BOTTOM);

				int remaining = Inventory.inventory[itemID].quantity;
				if (squareMap.containsKey(itemID)) {
					remaining -= getIngredientCount(squareMap.get(itemID));
				}
				Toolbox.drawText(g, "" + remaining, cornerX + iconSideLength * 19 / 20,
						cornerY + iconSideLength * 19 / 20);

			}
			if (complete)
				break;
		}
	}

	public static void addIngredientRequest(int itemID) {
		if (itemID == -1) {
			return;
		}
		int countInPot = 0;
		if (squareMap.containsKey(itemID)) {
			countInPot = ingredients.get(squareMap.get(itemID)).quantity;
		}
		if (Inventory.inventory[itemID].quantity == countInPot) {
			System.out.println("Not enough");
			return;
		}
		if (!squareMap.containsKey(itemID)) {
			if (ingredients.size() == 5) {
				System.out.println("Ingredient slots full");
				return;
			}
			ingredients.add(new ItemStack(itemID, 1));
			squareMap.put(itemID, ingredients.size() - 1);
		} else {
			ItemStack stack = ingredients.get(squareMap.get(itemID));
			stack.add(1);
		}
		System.out.println(ingredientString() + "\n");
	}

	public static void removeFromSquareRequest(int x) {
		if (ingredients.size() <= x) {
			System.out.println("Empty square");
			return;
		}
		int remaining = --ingredients.get(x).quantity;
		if (remaining == 0) {
			ingredients.remove(x);
			recalculateSquareMap();
		}
	}

	@Override
	public void drawHarvestIcon(Graphics2D g, boolean highlight) {

	}

	@Override
	public boolean collision(double fromX, double fromY, double toX, double toY) {
		double heightToWidthRatio = 0.6;
		int[] positionOnScreen = Toolbox.positionOnScreen(toX, toY);
		int[] campfirePosition = Toolbox.positionOnScreen(x, y);
		int displacementX = positionOnScreen[0] - campfirePosition[0];
		int displacementY = positionOnScreen[1] - campfirePosition[1];
		double width = 105 * GameState.zoom / 2, height = width * heightToWidthRatio;
		if (displacementX * displacementX / (width * width) + displacementY * displacementY / (height * height) < 1) {
			PlayerMovement.velocityX = 0;
			PlayerMovement.velocityY = 0;
			return true;
		}
		return false;
	}
	
	@Override
	public void drawBoundary(Graphics2D g) {
		double heightToWidthRatio = 0.6;
		int WIDTH = (int) (105 * GameState.zoom);
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(2));
		g.drawOval((int) (positionOnScreen[0] - WIDTH / 2), (int) (positionOnScreen[1] - WIDTH * heightToWidthRatio / 2), (int) (WIDTH), (int) (WIDTH * heightToWidthRatio));
		Toolbox.drawPosition(x, y, g);
	}
	
	@Override
	public boolean inFrontOf(int screenY) {
		int yOnScreen = Toolbox.yOnScreen(x, y);
		if (yOnScreen >= screenY) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		if (cookingInProgress) {
			if (Game.frameCount - lastFrame > cookingRecipe.frames) {
				if (cookingQuantityFinished < cookingQuantityQueued) {
					cookingQuantityFinished++;
					if (cookingQuantityFinished < cookingQuantityQueued) lastFrame = Game.frameCount;
				}
			}
		}
		if (cookingQuantityFinished >= 1) {
			readyForHarvest = true;
		}
	}

	@Override
	public void render(Graphics2D g) {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		g.drawImage(CAMPFIRE[(Game.frameCount / 5) % 7],
				positionOnScreen[0] - (int) (IMAGE_WIDTH * scale * GameState.zoom),
				positionOnScreen[1] - (int) (1.67 * scale * IMAGE_HEIGHT * GameState.zoom),
				(int) (IMAGE_WIDTH * 2 * scale * GameState.zoom), (int) (IMAGE_HEIGHT * 2 * scale * GameState.zoom),
				null);
		if (cookingInProgress) {
			int innerRadius = (int) (23 * GameState.zoom), outerRadius = (int) (innerRadius * 1.2);
			double fraction = (double) (Game.frameCount - lastFrame) / cookingRecipe.frames;
			g.setColor(new Color(255, 59, 59));
			g.fillArc(positionOnScreen[0] - outerRadius,
					(int) (positionOnScreen[1] - outerRadius - 100 * GameState.zoom), 2 * outerRadius, 2 * outerRadius,
					90, (int) (360 * fraction));
			g.setColor(new Color(240, 240, 240));
			g.fillOval(positionOnScreen[0] - innerRadius,
					(int) (positionOnScreen[1] - innerRadius - 100 * GameState.zoom), 2 * innerRadius, 2 * innerRadius);
			Toolbox.drawCenteredImage(g, InventoryUI.ITEM_ICONS[cookingRecipe.productItemID], positionOnScreen[0],
					(int) (positionOnScreen[1] - 100 * GameState.zoom), (int) (1.5 * innerRadius),
					(int) (1.5 * innerRadius));
			Toolbox.setFontSize(g, 13);
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			g.setColor(Color.BLACK);
			Toolbox.drawText(g, "" + cookingQuantityFinished + " / " + cookingQuantityQueued, positionOnScreen[0],
					(int) (positionOnScreen[1] + 38 * GameState.zoom));
		}
	}
	
	@Override
	public int topY() {
		return Toolbox.yOnScreen(x, y) - (int) (1.67 * scale * IMAGE_HEIGHT * GameState.zoom);
	}
	
	@Override
	public int bottomY() {
		return Toolbox.yOnScreen(x, y) + (int) (0.33 * scale * IMAGE_HEIGHT * GameState.zoom);
	}

	public static String ingredientString() {
		String str = "Campfire (";
		for (int i = 0; i < ingredients.size(); i++) {
			ItemStack stack = ingredients.get(i);
			str += stack + ((i == ingredients.size() - 1) ? "" : ", ");
		}
		return str + ")";
	}
	
	public static void setIngredientCount(int itemID, int quantity) {
		for (ItemStack stack : ingredients) {
			if (stack.itemID == itemID) {
				stack.quantity = quantity;
			}
		}
	}
	
	public static int getIngredientCount(int itemID) {
		for (ItemStack stack : ingredients) {
			if (stack.itemID == itemID) {
				return stack.quantity;
			}
		}
		return 0;
	}

	public static void setInventoryIcons() {
		for (int i = 0; i < 20; i++) {
			inventoryIcons[i] = -1;
		}
		int index = 0;
		for (ItemStack itemStack : Inventory.inventory) {
			if (itemStack.quantity == 0 || !Item.COOKABLE[itemStack.itemID]) {
				continue;
			}
			inventoryIcons[index] = itemStack.itemID;
			index++;
		}
	}

	public static void recalculateSquareMap() {
		squareMap.clear();
		for (int x = 0; x < ingredients.size(); x++) {
			int itemID = ingredients.get(x).itemID;
			squareMap.put(itemID, x);
		}
	}

	@Override
	public String toString() {
		return "Campfire";
	}

}
