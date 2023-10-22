package entities;

import java.util.ArrayList;
import java.util.List;

import algorithms.Toolbox;
import creature.Creature;
import creature.MushroomMonster;
import creature.Rabbit;
import creature.Snail;
import environment.DayNightCycler;
import main.Game;
import main.Launcher;
import player.Player;

public class CreatureControl {
	
	private static List<Creature> toRemove = new ArrayList<Creature>(), toAdd = new ArrayList<Creature>();
	
	public static void tick() {
		
		for (Creature c : Creature.creatures) {
			if (Toolbox.pixelsFromCenter(c.x, c.y) > Launcher.WIDTH * 2.5) {
				toRemove.add(c);
			}
		}
		
		if (Math.random() < 1.0 / (Game.averageFrameRate * 3)) {
			double angle = Math.random() * 2 * Math.PI;
			double distance = 6 + Math.random() * 3;
			double dX = distance * Math.cos(angle);
			double dY = distance * Math.sin(angle);
			
			double r = Math.random();
			double x = Player.playerX + dX;
			double y = Player.playerY + dY;
			if (DayNightCycler.getGeneralTime() != DayNightCycler.TIME_NIGHT) {
				if (r < 0.5) {
					new Rabbit(x, y);
				} else if (r < 0.75) {
					new Snail(x, y);
				} else {
					new MushroomMonster(x, y);
				}
			} else {
				if (r < 0.3) {
					new Rabbit(x, y);
				} else if (r < 0.6) {
					new Snail(x, y);
				} else {
					new MushroomMonster(x, y);
				}
			}
		}
	}
	
	public static void update() {
		for (Creature e : toRemove) {
			Creature.creatures.remove(e);
			Entity.entities.remove(e);
		}
		for (Creature e : toAdd) {
			Creature.creatures.add(e);
		}
		toRemove.clear();
		toAdd.clear();
	}
	
	public static void add(Creature e) {
		toAdd.add(e);
	}
	
	public static void remove(Creature e) {
		toRemove.add(e);
	}
	
}
