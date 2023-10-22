package data;

import java.util.ArrayList;
import java.util.List;

import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;

public class CookingRecipe {
	
	public static final CookingRecipe[] cookingRecipes = {
			new CookingRecipe(Item.ID_ROASTED_ACORN, new int[][] {{Item.ID_ACORN, 1}}, 5.0),
			new CookingRecipe(Item.ID_COOKED_RABBIT, new int[][] {{Item.ID_RAW_RABBIT, 1}}, 40.0),
			new CookingRecipe(Item.ID_ESCARGOT, new int[][] {{Item.ID_SNAIL, 1}}, 15.0),
			new CookingRecipe(Item.ID_LOAF_OF_BREAD, new int[][] {{Item.ID_WHEAT, 8}}, 25.0),
			new CookingRecipe(Item.ID_CROISSANT, new int[][] {{Item.ID_WHEAT, 6}}, 25.0),
			new CookingRecipe(Item.ID_MUSHROOM_STEW, new int[][] {{Item.ID_MUSHROOM, 5}, {Item.ID_RAW_RABBIT, 1}}, 25.0),
	};
	
	public List<ItemStack> ingredients = new ArrayList<ItemStack>();
	public int productItemID;
	public boolean[] containsItemID = new boolean[Item.IDToName.length];
	
	public int frames;
	
	public CookingRecipe(int productItemID, int[][] ingredients, double seconds) {
		for (int[] stack : ingredients) {
			this.ingredients.add(new ItemStack(stack[0], stack[1]));
			containsItemID[stack[0]] = true;
		}
		this.productItemID = productItemID;
		this.frames = (int) (seconds * Game.idealFrameRate);
	}
	
	public int quantity(int itemID) {
		for (ItemStack stack : ingredients) {
			if (stack.itemID == itemID) {
				return stack.quantity;
			}
		}
		return 0;
	}
	
	public boolean enough() {
		for (ItemStack stack : ingredients) {
			int itemID = stack.itemID, quantity = stack.quantity;
			if (Inventory.inventory[itemID].quantity < quantity) {
				return false;
			}
		}
		return true;
	}
	
	public int maximumCopies() {
		int minimum = Integer.MAX_VALUE;
		for (ItemStack stack : ingredients) {
			int itemID = stack.itemID, quantity = stack.quantity;
			minimum = Math.min(minimum, Inventory.inventory[itemID].quantity / quantity);
		}
		return minimum;
	}
	
	public void make(int count) {
		Inventory.addBundle(productItemID, count);
		for (ItemStack stack : ingredients) {
			Inventory.removeBundle(stack.itemID, stack.quantity);
		}
	}
	
}
