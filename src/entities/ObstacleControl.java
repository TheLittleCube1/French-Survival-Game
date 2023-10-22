package entities;

import java.util.ArrayList;
import java.util.List;

public class ObstacleControl {
	
	private static List<Obstacle> toRemove = new ArrayList<Obstacle>(), toAdd = new ArrayList<Obstacle>();
	
	public static void update() {
		for (Obstacle o : toRemove) {
			Obstacle.obstacles.remove(o);
		}
		for (Obstacle o : toAdd) {
			Obstacle.obstacles.add(o);
		}
		toRemove.clear();
		toAdd.clear();
	}
	
	public static void add(Obstacle o) {
		toAdd.add(o);
	}
	
	public static void remove(Obstacle o) {
		toRemove.add(o);
	}
	
}
