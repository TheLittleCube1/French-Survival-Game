package entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithms.Toolbox;
import data.CraftingRecipe;
import inventory.Inventory;
import inventory.InventoryUI;
import inventory.Item;
import inventory.ItemStack;
import main.Launcher;

public class Crafting {
	
	public static boolean showCraftingUI = false;
	public static int[] inventoryIcons = new int[20];
	public static List<CraftingRecipe> recipeSuggestions = new ArrayList<CraftingRecipe>();

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

	public static final int iconsPerRow = 4, padding = INVENTORY_PANEL_WIDTH / 20;
	public static final int iconSideLength = (INVENTORY_PANEL_WIDTH - (iconsPerRow + 1) * padding) / iconsPerRow;

	public static List<ItemStack> resources = new ArrayList<ItemStack>();
	public static Map<Integer, Integer> squareMap = new HashMap<Integer, Integer>();
	public static CraftingRecipe selectedRecipeSuggestion = null;

	public static CraftingRecipe craftingRecipe = null;
	public static int quantitySelected = 1;
	
	public static void initializeMenu() {
		quantitySelected = 1;
		craftingRecipe = null;
		selectedRecipeSuggestion = null;
		resources.clear();
		squareMap.clear();
	}

	public static void renderCraftingUI(Graphics2D g) {

		renderInventoryPanel(g);
		renderCraftingPanel(g);
		
		if (showCraftingUI) {
			g.setColor(Color.BLACK);
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 20);
			Toolbox.drawText(g, "Crafting", Launcher.WIDTH / 2, INVENTORY_PANEL_TOP / 2);
		}

	}

	public static void renderCraftingPanel(Graphics2D g) {
		if (!showCraftingUI) {
			return;
		}

		int baseColor = 140;

		g.setColor(new Color(baseColor, baseColor, baseColor));
		g.fillRoundRect(INFORMATION_PANEL_LEFT, INFORMATION_PANEL_TOP, INFORMATION_PANEL_WIDTH,
				INFORMATION_PANEL_HEIGHT, INFORMATION_PANEL_ARC_DIAMETER, INFORMATION_PANEL_ARC_DIAMETER);

		Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
		g.setColor(new Color(40, 40, 40));
		Toolbox.setFontSize(g, 18);
		Toolbox.drawText(g, "Resources", INFORMATION_PANEL_LEFT + padding, INFORMATION_PANEL_TOP + padding);

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

			if (x < resources.size()) {
				g.drawImage(InventoryUI.ITEM_ICONS[resources.get(x).itemID], cornerX, cornerY, squareLength,
						squareLength, null);
				g.setColor(Color.BLACK);
				Toolbox.drawText(g, "" + resources.get(x).quantity, cornerX + squareLength - 2,
						cornerY + squareLength - 2);
			}

		}

		Toolbox.setAlign(Toolbox.ALIGN_LEFT, Toolbox.ALIGN_TOP);
		g.setColor(new Color(40, 40, 40));
		Toolbox.setFontSize(g, 18);
		Toolbox.drawText(g, "Blueprints", INFORMATION_PANEL_LEFT + padding, (int) (Launcher.HEIGHT * 0.33));

		recipeSuggestions = recipeSuggestions();

		for (int i = 0; i < recipeSuggestions.size(); i++) {
			CraftingRecipe suggestion = recipeSuggestions.get(i);
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
			if (selectedRecipeSuggestion == recipeSuggestions.get(i)) {
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

			// Resources Required
			for (int ingredientBox = 0; ingredientBox < suggestion.resources.size(); ingredientBox++) {

				int cornerX = (int) (INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH - padding * 1.3
						- ingredientSquareLength - ingredientSquareLength * 1.25 * ingredientBox) + 2;
				int cornerY = (int) (Launcher.HEIGHT * 0.4 + i * ingredientSquareLength * spacing
						- (ingredientSquareLength - 15) / 2);
				ItemStack stack = suggestion.resources.get(suggestion.resources.size() - ingredientBox - 1);
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
						InventoryUI.ITEM_ICONS[suggestion.resources
								.get(suggestion.resources.size() - ingredientBox - 1).itemID],
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
			Toolbox.drawText(g, "Make", INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2,
					(int) (Launcher.HEIGHT * 0.79) + 20);

			Toolbox.setFontSize(g, 16);
			int textCenterX = (INFORMATION_PANEL_LEFT * 2 + 3 * INFORMATION_PANEL_WIDTH / 2 + 50) / 2;
			int textCenterY = (int) (Launcher.HEIGHT * 0.79) + 20;
			int maximum = selectedRecipeSuggestion.maximumCopies();
			Toolbox.drawText(g, "" + quantitySelected, textCenterX, textCenterY);
			g.setColor(new Color(160, 160, 160));
			if (quantitySelected != 1) {
				g.fillPolygon(new int[] { textCenterX - 27, textCenterX - 18, textCenterX - 18 },
						new int[] { textCenterY, textCenterY - 7, textCenterY + 7 }, 3);
			}
			if (quantitySelected != maximum) {
				g.fillPolygon(new int[] { textCenterX + 27, textCenterX + 18, textCenterX + 18 },
						new int[] { textCenterY, textCenterY - 7, textCenterY + 7 }, 3);
			}

		}

	}

	public static void renderInventoryPanel(Graphics2D g) {
		if (!showCraftingUI) {
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
					remaining -= resources.get(squareMap.get(itemID)).quantity;
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
			countInPot = resources.get(squareMap.get(itemID)).quantity;
		}
		if (Inventory.inventory[itemID].quantity == countInPot) {
			System.out.println("Not enough");
			return;
		}
		if (!squareMap.containsKey(itemID)) {
			if (resources.size() == 5) {
				System.out.println("Ingredient slots full");
				return;
			}
			resources.add(new ItemStack(itemID, 1));
			squareMap.put(itemID, resources.size() - 1);
		} else {
			ItemStack stack = resources.get(squareMap.get(itemID));
			stack.add(1);
		}
		System.out.println(ingredientString() + "\n");
	}

	public static void removeFromSquareRequest(int x) {
		if (resources.size() <= x) {
			System.out.println("Empty square");
			return;
		}
		int remaining = --resources.get(x).quantity;
		if (remaining == 0) {
			resources.remove(x);
			recalculateSquareMap();
		}
	}

	public static String ingredientString() {
		String str = "Crafting Stump (";
		for (int i = 0; i < resources.size(); i++) {
			ItemStack stack = resources.get(i);
			str += stack + ((i == resources.size() - 1) ? "" : ", ");
		}
		return str + ")";
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
		for (int x = 0; x < resources.size(); x++) {
			int itemID = resources.get(x).itemID;
			squareMap.put(itemID, x);
		}
	}

	public static List<CraftingRecipe> recipeSuggestions() {
		List<CraftingRecipe> suggestions = new ArrayList<CraftingRecipe>();
		for (CraftingRecipe recipe : CraftingRecipe.craftingRecipes) {
			boolean potentialSuggestion = true;
			for (ItemStack stack : resources) {
				if (!recipe.containsItemID[stack.itemID]) {
					potentialSuggestion = false;
					break;
				}
			}
			if (potentialSuggestion) {
				suggestions.add(recipe);
			} else {
				if (selectedRecipeSuggestion == recipe) {
					selectedRecipeSuggestion = null;
				}
			}
		}
		return suggestions;
	}

	@Override
	public String toString() {
		return "Crafting";
	}

}
