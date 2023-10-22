package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import algorithms.Toolbox;
import inventory.Inventory;
import inventory.Item;
import inventory.ItemStack;
import main.Game;
import main.Launcher;
import player.Player;
import states.GameState;

public class ItemCollectionNotification {
	
	public static String name = "";
	public static int remainingFrames = 3 * Game.idealFrameRate;
	public static double y = 0;
	
	public static void newNotification(int itemID) {
		y = 0;
		remainingFrames = 3 * Game.idealFrameRate;
		name = "+1 " + Item.IDToName[itemID];
	}
	
	public static void newNotification(int itemID, int count) {
		y = 0;
		remainingFrames = 3 * Game.idealFrameRate;
		name = "+" + count + " " + Inventory.IDPluralName(itemID, count);
	}
	
	public static void newNotification(ArrayList<ItemStack> items) {
		y = 0;
		remainingFrames = 3 * Game.idealFrameRate;
		name = "";
		for (int i = 0; i < items.size(); i++) {
			name += "+" + items.get(i);
			if (i != items.size() - 1) name += ", ";
		}
	}
	
	public static void tick() {
		remainingFrames--;
		boolean noPlayerHealthBar = Player.playerHealthBar.opacity == 0;
		if (noPlayerHealthBar) {
			y += (38.5 - y) / 8;
		} else {
			y += (52.5 - y) / 8;
		}
	}
	
	public static void render(Graphics2D g) {
		if (remainingFrames <= 0 || name == null) return;
		g.setColor(Color.DARK_GRAY);
		Toolbox.setFontSize(g, (int) (16 * GameState.zoom));
		Toolbox.setAlign(Toolbox.ALIGN_CENTER, Toolbox.ALIGN_CENTER);
		Toolbox.drawText(g, name, Launcher.WIDTH / 2, (int) (Launcher.HEIGHT / 2 - (10 + y) * GameState.zoom));
	}
	
	public static void print() {
		System.out.println(name);
	}
	
}
