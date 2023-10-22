package entities;

import java.util.ArrayList;
import java.util.List;

public class EntityControl {
	
	private static List<Entity> toRemove = new ArrayList<Entity>(), toAdd = new ArrayList<Entity>();
	
	public static void update() {
		for (Entity e : toRemove) {
			Entity.entities.remove(e);
		}
		for (Entity e : toAdd) {
			Entity.entities.add(e);
		}
		toRemove.clear();
		toAdd.clear();
	}
	
	public static void add(Entity e) {
		toAdd.add(e);
	}
	
	public static void remove(Entity e) {
		toRemove.add(e);
	}
	
}
