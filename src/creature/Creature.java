package creature;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import algorithms.Toolbox;
import entities.CreatureControl;
import entities.Entity;
import main.Launcher;
import player.CharacterGraphics;
import player.Player;
import survival.HealthBar;

public abstract class Creature extends Entity {
	
	public static List<Creature> creatures = new ArrayList<Creature>();
	
	public HealthBar healthBar = new HealthBar(100, Double.POSITIVE_INFINITY, this);
	public int yOnScreen;
	public int IMAGE_WIDTH, IMAGE_HEIGHT;
	
	public long deathTime = -1;
	
	public Creature(double x, double y) {
		super(x, y);
		CreatureControl.add(this);
	}
	
	public static void slash() {
		for (Creature creature : creatures) {
			int displacementX = Toolbox.xOnScreen(creature.x, creature.y) - Launcher.WIDTH / 2;
			int distance = creature.pixelDistance();
			if (distance < 70) {
				System.out.println("In range: " + creature);
				if (displacementX < 0 ^ Player.facingRight) {
					creature.takeHit(10);
				}
			} else {
				System.out.println("Not in range, distance = " + distance);
			}
		}
	}
	
	public void takeHit(double hitPoints) {
		if (healthBar.health <= 0) {
			return;
		}
		healthBar.takeHit(hitPoints);
	}
	
	public static void tickCreatures() {
		for (Creature creature : creatures) {
			creature.tick();
			creature.healthBar.tick();
			creature.yOnScreen = creature.yOnScreen();
		}
	}
	
	public static Creature nearestHarvestableCreature() {
		Creature nearestHarvestableCreature = null;
		int smallestDistance = Integer.MAX_VALUE;
		for (Creature creature : creatures) {
			int pixelsFromPlayer = Toolbox.pixelsFromCenter(creature.x, creature.y);
			if (creature.healthBar.health <= 0 && pixelsFromPlayer < smallestDistance) {
				nearestHarvestableCreature = creature;
				smallestDistance = pixelsFromPlayer;
			}
		}
		return nearestHarvestableCreature;
	}
	
	public int yOnScreen() {
		return Toolbox.yOnScreen(x, y);
	}
	
	public void drawDebug(Graphics2D g) {
		Toolbox.drawPosition(x, y, g);
		Toolbox.drawText(g, "" + pixelDistance(), Toolbox.xOnScreen(x, y), Toolbox.yOnScreen(x, y));
	}
	
	public int pixelDistance() {
		int[] positionOnScreen = Toolbox.positionOnScreen(x, y);
		int displacementX = positionOnScreen[0] - Launcher.WIDTH / 2;
		int displacementY = positionOnScreen[1] - (Launcher.HEIGHT / 2 + CharacterGraphics.playerYDisplacement);
		return (int) Math.sqrt(displacementX * displacementX + displacementY * displacementY);
	}
	
	public abstract void tick();
	public abstract void render(Graphics2D g);
	
	public abstract void die();
	public abstract void harvest();
	public abstract boolean inHarvestRange();
	
	public abstract int topY();
	public abstract int bottomY();
	
}
