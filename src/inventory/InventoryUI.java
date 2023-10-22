package inventory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import algorithms.Toolbox;
import main.Launcher;

public class InventoryUI {

	public static boolean showInventory = false;

	public static BufferedImage[] ITEM_ICONS = new BufferedImage[Item.IDToName.length];
	
	public static int[] inventoryIcons = new int[20];
	
	public static final int INVENTORY_PANEL_LEFT = (int) (Launcher.WIDTH * 0.6), INVENTORY_PANEL_TOP = (int) (Launcher.HEIGHT * 0.11565836298932385);
	public static final int INVENTORY_PANEL_WIDTH = (int) (Launcher.WIDTH * 0.35), INVENTORY_PANEL_HEIGHT = Launcher.HEIGHT - 2 * INVENTORY_PANEL_TOP;
	public static final int INVENTORY_PANEL_ARC_DIAMETER = (int) (Launcher.WIDTH * 0.025);
	
	public static final int INFORMATION_PANEL_LEFT = Launcher.WIDTH - (INVENTORY_PANEL_LEFT + INVENTORY_PANEL_WIDTH);
	public static final int INFORMATION_PANEL_TOP = INVENTORY_PANEL_TOP;
	public static final int INFORMATION_PANEL_WIDTH = INVENTORY_PANEL_WIDTH;
	public static final int INFORMATION_PANEL_HEIGHT = INVENTORY_PANEL_HEIGHT;
	public static final int INFORMATION_PANEL_ARC_DIAMETER = INVENTORY_PANEL_ARC_DIAMETER;

	public static final int iconsPerRow = 4, padding = INVENTORY_PANEL_WIDTH / 20;
	public static final int iconSideLength = (INVENTORY_PANEL_WIDTH - (iconsPerRow + 1) * padding) / iconsPerRow;
	
	public static int selectedInventorySpace = -1;
	
	public static void renderInventory(Graphics2D g) {
		
		renderInventoryPanel(g);
		renderInformationPanel(g);
		
		if (showInventory) {
			g.setColor(Color.BLACK);
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 20);
			Toolbox.drawText(g, "Stockage", Launcher.WIDTH / 2, INVENTORY_PANEL_TOP / 2);
		}
		
	}
	
	public static void closeInventory() {
		showInventory = false;
		selectedInventorySpace = -1;
	}
	
	public static void renderInformationPanel(Graphics2D g) {
		
		if (!showInventory) {
			return;
		}
		
		int baseColor = 140;
		
		g.setColor(new Color(baseColor, baseColor, baseColor));
		g.fillRoundRect(INFORMATION_PANEL_LEFT, INFORMATION_PANEL_TOP, INFORMATION_PANEL_WIDTH, INFORMATION_PANEL_HEIGHT, INFORMATION_PANEL_ARC_DIAMETER, INFORMATION_PANEL_ARC_DIAMETER);
		
		if (selectedInventorySpace == -1 || inventoryIcons[selectedInventorySpace] == -1) {
			g.setColor(new Color(baseColor - 60, baseColor - 60, baseColor - 60));
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 16);
			Toolbox.drawText(g, "Il n'y a rien ici...", INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2, (int) (Launcher.HEIGHT * 0.35));
		} else {
			int itemID = inventoryIcons[selectedInventorySpace];
			g.setColor(new Color(40, 40, 40));
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
			Toolbox.setFontSize(g, 18);
			Toolbox.drawText(g, Item.IDToName[itemID], INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2, (int) (Launcher.HEIGHT * 0.17));
			double scale = 0.6;
			Toolbox.drawCenteredImage(g, ITEM_ICONS[itemID], INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2, (int) (Launcher.HEIGHT * 0.38), (int) (INFORMATION_PANEL_WIDTH * scale), (int) (INFORMATION_PANEL_WIDTH * scale));
			Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_TOP);
			Toolbox.setFontSize(g, 14);
			Toolbox.drawBoundedText(g, Item.IDToDescription[itemID], INFORMATION_PANEL_LEFT + 20, (int) (Launcher.HEIGHT * 0.6), INFORMATION_PANEL_WIDTH - 40);
			
			if (Item.usable[itemID]) {
				g.setColor(new Color(baseColor - 15, baseColor - 15, baseColor - 15));
				g.fillRect(INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2 - 40, INFORMATION_PANEL_TOP + INFORMATION_PANEL_HEIGHT - 50, 80, 40);
				g.setColor(new Color(60, 60, 60));
				Toolbox.setFontSize(g, 16);
				Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
				Toolbox.drawText(g, Item.verb[itemID], INFORMATION_PANEL_LEFT + INFORMATION_PANEL_WIDTH / 2, INFORMATION_PANEL_TOP + INFORMATION_PANEL_HEIGHT - 30);
			}
			
		}
		
	}

	public static void renderInventoryPanel(Graphics2D g) {
		
		if (!showInventory) {
			return;
		}

		int baseColor = 140;

		g.setColor(new Color(baseColor, baseColor, baseColor));
		g.fillRoundRect(INVENTORY_PANEL_LEFT, INVENTORY_PANEL_TOP, INVENTORY_PANEL_WIDTH, INVENTORY_PANEL_HEIGHT, INVENTORY_PANEL_ARC_DIAMETER, INVENTORY_PANEL_ARC_DIAMETER);

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 4; x++) {
				
				int cornerX = INVENTORY_PANEL_LEFT + padding * (x + 1) + iconSideLength * x;
				int cornerY = INVENTORY_PANEL_TOP + padding * (y + 1) + iconSideLength * y;
				
				g.setColor(new Color(baseColor + 5, baseColor + 5, baseColor + 5));
				if (y * 4 + x == selectedInventorySpace) {
					g.setColor(new Color(baseColor - 20, baseColor - 20, baseColor - 20));
				}
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
				
				if (inventoryIcons[iconNumber] == -1) {
					complete = true;
					break;
				}
				
				int cornerX = INVENTORY_PANEL_LEFT + padding * (x + 1) + iconSideLength * x;
				int cornerY = INVENTORY_PANEL_TOP + padding * (y + 1) + iconSideLength * y;
				
				g.drawImage(ITEM_ICONS[inventoryIcons[iconNumber]], cornerX, cornerY, iconSideLength, iconSideLength, null);
				
				Toolbox.setAlign(Toolbox.ALIGN_RIGHT, Toolbox.ALIGN_BOTTOM);
				Toolbox.drawText(g, "" + Inventory.inventory[inventoryIcons[iconNumber]].quantity, cornerX + iconSideLength * 19 / 20, cornerY + iconSideLength * 19 / 20);
				
			}
			if (complete) break;
		}
		
	}
	
	public static void setInventoryIcons() {
		for (int i = 0; i < 20; i++) {
			inventoryIcons[i] = -1;
		}
		int index = 0;
		for (ItemStack itemStack : Inventory.inventory) {
			if (itemStack.quantity == 0) {
				continue;
			}
			inventoryIcons[index] = itemStack.itemID;
			index++;
		}
	}

}
