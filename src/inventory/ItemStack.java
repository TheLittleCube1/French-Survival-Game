package inventory;

public class ItemStack {

	public int itemID, quantity = 0;

	public ItemStack(int itemID) {
		this.itemID = itemID;
	}

	public ItemStack(int itemID, int quantity) {
		this.itemID = itemID;
		this.quantity = quantity;
	}

	public void add(int count) {
		quantity += count;
	}

	public void remove(int count) {
		if (quantity >= count) {
			quantity -= count;
		} else {
			System.out.println("Not enough (" + quantity + " out of " + count + ")");
		}
	}

	public void print() {
		if (quantity > 0) {
			System.out.println(quantity + "x " + Item.IDToName[itemID]);
		}
	}
	
	public String toString() {
		return quantity + " " + Inventory.IDPluralName(itemID, quantity);
	}

}
