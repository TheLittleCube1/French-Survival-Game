package data;

import java.util.ArrayList;
import java.util.List;

import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;

public class CraftingRecipe {
	
	public static final CraftingRecipe[] craftingRecipes = {
			new CraftingRecipe(Item.ID_ROASTED_ACORN, new int[][] {{Item.ID_BRANCH, 1}}, 5.0),
			new CraftingRecipe(Item.ID_BRANCH, new int[][] {{Item.ID_ROASTED_ACORN, 1}, {Item.ID_BRANCH, 2}}, 5.0),
	};
	
	public List<ItemStack> resources = new ArrayList<ItemStack>();
	public int productItemID;
	public boolean[] containsItemID = new boolean[Item.IDToName.length];
	
	public int frames;
	
	public CraftingRecipe(int productItemID, int[][] ingredients, double seconds) {
		// new CookingRecipe(Inventory.ID_ROASTED_ACORN, new int[][] {{Inventory.ID_ACORN, 1}});
		for (int[] stack : ingredients) {
			this.resources.add(new ItemStack(stack[0], stack[1]));
			containsItemID[stack[0]] = true;
		}
		this.productItemID = productItemID;
		this.frames = (int) (seconds * Game.idealFrameRate);
	}
	
	public boolean enough() {
		for (ItemStack stack : resources) {
			int itemID = stack.itemID, quantity = stack.quantity;
			if (Inventory.inventory[itemID].quantity < quantity) {
				return false;
			}
		}
		return true;
	}
	
	public int maximumCopies() {
		int minimum = Integer.MAX_VALUE;
		for (ItemStack stack : resources) {
			int itemID = stack.itemID, quantity = stack.quantity;
			minimum = Math.min(minimum, Inventory.inventory[itemID].quantity / quantity);
		}
		return minimum;
	}
	
	public void make(int count) {
		Inventory.addBundle(productItemID, count);
		for (ItemStack stack : resources) {
			Inventory.removeBundle(stack.itemID, stack.quantity);
		}
	}
	
}
