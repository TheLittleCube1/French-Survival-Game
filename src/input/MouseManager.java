package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import creature.Creature;
import data.CookingRecipe;
import entities.Campfire;
import entities.Crafting;
import entities.CreatureControl;
import entities.Entity;
import entities.EntityControl;
import inventory.Inventory;
import inventory.InventoryUI;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import main.Launcher;
import player.Player;
import survival.HealthBar;

public class MouseManager extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = -8994576182964289862L;
	
	public static long lastClick = System.nanoTime(), lastAttack = -1;

	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (Player.state == Player.STATE_DEAD) {
			respawn();
			return;
		}
		
		int mouseX = e.getX();
		int mouseY = e.getY();
		InventoryUI.setInventoryIcons();
		Campfire.setInventoryIcons();
		
		if (System.nanoTime() - lastClick < 3.5e8 && System.nanoTime() - lastAttack > 7e8) {
			// Double-click --> Slash
			Player.action = Player.ACTION_SLASH;
			Creature.slash();
			System.out.println(Creature.creatures.size());
			lastAttack = System.nanoTime();
		}
		
		// INVENTORY
		if (InventoryUI.showInventory) {
			// Selecting an item in the inventory
			for (int y = 0; y < 5; y++) {
				for (int x = 0; x < 4; x++) {
					int topLeftX = InventoryUI.INVENTORY_PANEL_LEFT + InventoryUI.padding * (x + 1) + InventoryUI.iconSideLength * x;
					int topLeftY = InventoryUI.INVENTORY_PANEL_TOP + InventoryUI.padding * (y + 1) + InventoryUI.iconSideLength * y;
					int bottomRightX = topLeftX + InventoryUI.iconSideLength;
					int bottomRightY = topLeftY + InventoryUI.iconSideLength;
					if (mouseX >= topLeftX && mouseX <= bottomRightX && mouseY >= topLeftY && mouseY <= bottomRightY) {
						if (InventoryUI.selectedInventorySpace == 4 * y + x) {
							InventoryUI.selectedInventorySpace = -1;
						} else {
							InventoryUI.selectedInventorySpace = 4 * y + x;
						}
						return;
					}
				}
			}
			
			// Using item request
			if (Math.abs(InventoryUI.INFORMATION_PANEL_LEFT + InventoryUI.INFORMATION_PANEL_WIDTH / 2 - mouseX) < 40) {
				if (Math.abs(InventoryUI.INFORMATION_PANEL_TOP + InventoryUI.INFORMATION_PANEL_HEIGHT - 30 - mouseY) < 20) {
					int itemID = InventoryUI.inventoryIcons[InventoryUI.selectedInventorySpace];
					Item.useItem(itemID);
				}
			} else {
				InventoryUI.selectedInventorySpace = -1;
			}
			
		}
		
		// COOKING
		if (Campfire.showCookingUI && mouseX <= Launcher.WIDTH / 2 && mouseY > Launcher.HEIGHT * 0.3 && mouseY < Launcher.HEIGHT * 0.76) {
			// Selecting a suggested cooking recipe
			for (int i = 0; i < 5; i++) {
				if (mouseX >= Campfire.INFORMATION_PANEL_LEFT + Campfire.padding && mouseX <= Campfire.INFORMATION_PANEL_LEFT + Campfire.INFORMATION_PANEL_WIDTH - Campfire.padding && mouseY >= (int) (Launcher.HEIGHT * 0.4 + i * Campfire.ingredientSquareLength * Campfire.spacing - 10) && mouseY <= (int) (Launcher.HEIGHT * 0.4 + i * Campfire.ingredientSquareLength * Campfire.spacing - 10) + (int) (Campfire.ingredientSquareLength * 1.28)) {
					if (CookingRecipe.cookingRecipes[Campfire.cookingMenuPage * 5 + i].enough()) {
						if (Campfire.selectedRecipeSuggestion == CookingRecipe.cookingRecipes[Campfire.cookingMenuPage * 5 + i]) {
							Campfire.selectedRecipeSuggestion = null;
							Campfire.ingredients.clear();
						} else {
							Campfire.selectedRecipeSuggestion = CookingRecipe.cookingRecipes[Campfire.cookingMenuPage * 5 + i];
							Campfire.ingredients.clear();
							for (ItemStack suggestionStack : Campfire.selectedRecipeSuggestion.ingredients) {
								Campfire.ingredients.add(new ItemStack(suggestionStack.itemID, suggestionStack.quantity));
							}
							Campfire.recalculateSquareMap();
							Campfire.selectedCampfire.cookingInProgress = false;
							Campfire.selectedCampfire.cookingRecipe = Campfire.selectedRecipeSuggestion;
							Campfire.selectedCampfire.cookingQuantityQueued = 1;
						}
					}
					return;
				}
			}
		} else if (Campfire.showCookingUI && Campfire.selectedRecipeSuggestion != null && mouseX >= Campfire.INFORMATION_PANEL_LEFT + Campfire.INFORMATION_PANEL_WIDTH / 2 - 50 && mouseX <= Campfire.INFORMATION_PANEL_LEFT + Campfire.INFORMATION_PANEL_WIDTH / 2 + 50 && mouseY > Launcher.HEIGHT * 0.79 && mouseY < Launcher.HEIGHT * 0.79 + 40) {
			// Pressing the "Cook" button
			Campfire.selectedCampfire.cookingInProgress = true;
			Campfire.showCookingUI = false;
			Campfire.selectedCampfire.lastFrame = Game.frameCount;
		} else if (Campfire.showCookingUI && Campfire.selectedRecipeSuggestion != null && mouseX >= (Campfire.INFORMATION_PANEL_LEFT * 2 + 3 * Campfire.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 - 27 && mouseX <= (Campfire.INFORMATION_PANEL_LEFT * 2 + 3 * Campfire.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 - 18 && mouseY >= (int) (Launcher.HEIGHT * 0.79) + 13 && mouseY <= (int) (Launcher.HEIGHT * 0.79) + 27) {
			// Decrease quantity button
			if (Campfire.selectedCampfire.cookingQuantityQueued != 1) {
				Campfire.selectedCampfire.cookingQuantityQueued--;
				for (ItemStack stack : Campfire.ingredients) {
					int itemID = stack.itemID;
					int unitQuantity = Campfire.selectedRecipeSuggestion.quantity(itemID);
					Campfire.setIngredientCount(itemID, unitQuantity * Campfire.selectedCampfire.cookingQuantityQueued);
				}
			}
		} else if (Campfire.showCookingUI && Campfire.selectedRecipeSuggestion != null && mouseX >= (Campfire.INFORMATION_PANEL_LEFT * 2 + 3 * Campfire.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 + 18 && mouseX <= (Campfire.INFORMATION_PANEL_LEFT * 2 + 3 * Campfire.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 + 27 && mouseY >= (int) (Launcher.HEIGHT * 0.79) + 13 && mouseY <= (int) (Launcher.HEIGHT * 0.79) + 27) {
			// Increase quantity button
			if (Campfire.selectedCampfire.cookingQuantityQueued != Campfire.selectedRecipeSuggestion.maximumCopies()) {
				Campfire.selectedCampfire.cookingQuantityQueued++;
				for (ItemStack stack : Campfire.ingredients) {
					int itemID = stack.itemID;
					int unitQuantity = Campfire.selectedRecipeSuggestion.quantity(itemID);
					Campfire.setIngredientCount(itemID, unitQuantity * Campfire.selectedCampfire.cookingQuantityQueued);
				}
			}
		} else if (Campfire.showCookingUI && Campfire.selectedRecipeSuggestion != null && mouseX >= Campfire.INFORMATION_PANEL_LEFT + 153 && mouseX <= Campfire.INFORMATION_PANEL_LEFT + 162 && mouseY >= (int) (Launcher.HEIGHT * 0.33) - 1 && mouseY <= (int) (Launcher.HEIGHT * 0.33) + 13) {
			// Decrease page count button
			if (Campfire.cookingMenuPage != 0) {
				Campfire.cookingMenuPage--;
			}
		} else if (Campfire.showCookingUI && Campfire.selectedRecipeSuggestion != null && mouseX >= 248 && mouseX <= 257 && mouseY >= (int) (Launcher.HEIGHT * 0.33) - 1 && mouseY <= (int) (Launcher.HEIGHT * 0.33) + 13) {
			// Increase page count button
			if (Campfire.cookingMenuPage <= CookingRecipe.cookingRecipes.length / 5) {
				Campfire.cookingMenuPage++;
			}
		}
		
		// CRAFTING
		else if (Crafting.showCraftingUI && mouseX > Launcher.WIDTH / 2) {
			// Adding an ingredient from inventory panel to crafting panel
			for (int y = 0; y < 5; y++) {
				for (int x = 0; x < 4; x++) {
					int topLeftX = Crafting.INVENTORY_PANEL_LEFT + Crafting.padding * (x + 1) + Crafting.iconSideLength * x;
					int topLeftY = Crafting.INVENTORY_PANEL_TOP + Crafting.padding * (y + 1) + Crafting.iconSideLength * y;
					if (mouseX >= topLeftX && mouseX <= topLeftX + Crafting.iconSideLength && mouseY >= topLeftY && mouseY <= topLeftY + Crafting.iconSideLength) {
						Crafting.addIngredientRequest(Crafting.inventoryIcons[4 * y + x]);
						return;
					}
				}
			}
		} else if (Crafting.showCraftingUI && mouseX <= Launcher.WIDTH / 2 && mouseY <= Launcher.HEIGHT * 0.3) {
			// Deleting an ingredient from cooking panel
			int cookingPadding = Launcher.WIDTH / 52;
			int squareLength = (Crafting.INFORMATION_PANEL_WIDTH - 6 * cookingPadding - 20) / 5;
			for (int x = 0; x < 5; x++) {
				
				int cornerX = Crafting.INFORMATION_PANEL_LEFT + cookingPadding * (x + 1) + squareLength * x + 10;
				int cornerY = (int) (Launcher.HEIGHT * 0.202);
				
				if (mouseX >= cornerX && mouseX <= cornerX + squareLength && mouseY >= cornerY && mouseY <= cornerY + squareLength) {
					Crafting.removeFromSquareRequest(x);
					return;
				}
				
			}
		} else if (Crafting.showCraftingUI && mouseX <= Launcher.WIDTH / 2 && mouseY > Launcher.HEIGHT * 0.3 && mouseY < Launcher.HEIGHT * 0.76) {
			// Selecting a suggested crafting recipe
			for (int i = 0; i < Crafting.recipeSuggestions.size(); i++) {
				if (mouseX >= Crafting.INFORMATION_PANEL_LEFT + Crafting.padding && mouseX <= Crafting.INFORMATION_PANEL_LEFT + Crafting.INFORMATION_PANEL_WIDTH - Crafting.padding && mouseY >= (int) (Launcher.HEIGHT * 0.4 + i * Crafting.ingredientSquareLength * Crafting.spacing - 10) && mouseY <= (int) (Launcher.HEIGHT * 0.4 + i * Crafting.ingredientSquareLength * Crafting.spacing - 10) + (int) (Crafting.ingredientSquareLength * 1.28)) {
					if (Crafting.recipeSuggestions.get(i).enough()) {
						if (Crafting.selectedRecipeSuggestion == Crafting.recipeSuggestions.get(i)) {
							Crafting.selectedRecipeSuggestion = null;
						} else {
							Crafting.selectedRecipeSuggestion = Crafting.recipeSuggestions.get(i);
							Crafting.resources.clear();
							for (ItemStack suggestionStack : Crafting.selectedRecipeSuggestion.resources) {
								Crafting.resources.add(new ItemStack(suggestionStack.itemID, suggestionStack.quantity));
							}
							Crafting.recalculateSquareMap();
							Crafting.craftingRecipe = Crafting.selectedRecipeSuggestion;
							Crafting.quantitySelected = 1;
						}
					}
					return;
				}
			}
		} else if (Crafting.showCraftingUI && Crafting.selectedRecipeSuggestion != null && mouseX >= Crafting.INFORMATION_PANEL_LEFT + Crafting.INFORMATION_PANEL_WIDTH / 2 - 50 && mouseX <= Crafting.INFORMATION_PANEL_LEFT + Crafting.INFORMATION_PANEL_WIDTH / 2 + 50 && mouseY > Launcher.HEIGHT * 0.79 && mouseY < Launcher.HEIGHT * 0.79 + 40) {
			// Pressing the "Make" button
			Crafting.showCraftingUI = false;
			Crafting.selectedRecipeSuggestion.make(Crafting.quantitySelected);
		} else if (Crafting.showCraftingUI && Crafting.selectedRecipeSuggestion != null && mouseX >= (Crafting.INFORMATION_PANEL_LEFT * 2 + 3 * Crafting.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 - 27 && mouseX <= (Crafting.INFORMATION_PANEL_LEFT * 2 + 3 * Crafting.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 - 18 && mouseY >= (int) (Launcher.HEIGHT * 0.79) + 13 && mouseY <= (int) (Launcher.HEIGHT * 0.79) + 27) {
			// Decrease quantity button
			if (Crafting.quantitySelected != 1) {
				Crafting.quantitySelected--;
			}
		} else if (Crafting.showCraftingUI && Crafting.selectedRecipeSuggestion != null && mouseX >= (Crafting.INFORMATION_PANEL_LEFT * 2 + 3 * Crafting.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 + 18 && mouseX <= (Crafting.INFORMATION_PANEL_LEFT * 2 + 3 * Crafting.INFORMATION_PANEL_WIDTH / 2 + 50) / 2 + 27 && mouseY >= (int) (Launcher.HEIGHT * 0.79) + 13 && mouseY <= (int) (Launcher.HEIGHT * 0.79) + 27) {
			// Increase quantity button
			if (Crafting.quantitySelected != Crafting.selectedRecipeSuggestion.maximumCopies()) {
				Crafting.quantitySelected++;
			}
		}
		
		lastClick = System.nanoTime();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	public static void respawn() {
		for (Entity e : Entity.entities) {
			if (e instanceof Creature) {
				EntityControl.remove(e);
			}
		}
		for (Creature c : Creature.creatures) {
			CreatureControl.remove(c);
		}
		Inventory.initializeInventory();
		Player.facingRight = false;
		Player.playerHealthBar = new HealthBar(100, 20, null);
		Player.playerX = 0;
		Player.playerY = 0.5;
		Player.state = 1;
		Player.action = Player.ACTION_NONE;
		Player.movementDirection = new int[] { Player.DIRECTION_NONE, Player.DIRECTION_NONE };
		Game.frameCount = 0;
	}
	
}
