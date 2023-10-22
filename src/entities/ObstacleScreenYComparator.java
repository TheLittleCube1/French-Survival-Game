package entities;

import java.util.Comparator;

public class ObstacleScreenYComparator implements Comparator<Obstacle> {
	
	@Override
	public int compare(Obstacle o1, Obstacle o2) {
		return (int) (o1.orthoY - o2.orthoY);
	}
	
}
