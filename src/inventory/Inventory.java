package inventory;

import java.util.ArrayList;

import entities.ItemCollectionNotification;

public class Inventory {

	public static ItemStack[] inventory = new ItemStack[Item.IDToName.length];

	public static void initializeInventory() {
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = new ItemStack(i);
		}
	}
	
	public static int quantity(int itemID) {
		return inventory[itemID].quantity;
	}

	public static void print() {
		for (ItemStack stack : inventory) {
			stack.print();
		}
		System.out.println();
	}

	public static void addItem(int itemID) {
		inventory[itemID].add(1);
		ItemCollectionNotification.newNotification(itemID);
	}

	public static void addBundle(int itemID, int count) {
		inventory[itemID].add(count);
		ItemCollectionNotification.newNotification(itemID, count);
	}

	public static void addItemStacks(ArrayList<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			inventory[stack.itemID].add(stack.quantity);
		}
		ItemCollectionNotification.newNotification(stacks);
	}

	public static void removeItem(int itemID) {
		inventory[itemID].remove(1);
	}

	public static void removeBundle(int itemID, int count) {
		inventory[itemID].remove(count);
	}

	public static String IDPluralName(int itemID, int count) {
		if (count == 1)
			return Item.IDToName[itemID];
		else
			return Item.IDToNamePlural[itemID];
	}

}
