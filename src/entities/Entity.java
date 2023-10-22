package entities;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import algorithms.Toolbox;
import player.Player;

public abstract class Entity implements Comparable<Entity> {
	
	public double x, y;
	
	public static List<Entity> entities = new ArrayList<Entity>();
	
	public Entity(double x, double y) {
		this.x = x;
		this.y = y;
		
		EntityControl.add(this);
	}
	
	public int compareTo(Entity other) {
		int thisY = Toolbox.yOnScreen(x, y), otherY = Toolbox.yOnScreen(other.x, other.y);
		if (thisY != otherY) {
			return thisY - otherY;
		} else {
			boolean thisIsPlayer = this instanceof Player;
			boolean otherIsPlayer = other instanceof Player;
			if (thisIsPlayer) {
				return Player.movementDirection[1];
			} else if (otherIsPlayer) {
				return -Player.movementDirection[1];
			} else {
				return 0;
			}
		}
	}
	
	public abstract void tick();
	public abstract void render(Graphics2D g);
	public abstract void drawDebug(Graphics2D g);
	
}
