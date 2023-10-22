package entities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ObstacleFetcher {
	
	public static void fetchEntities() {
		File entityFile = new File("Obstacles.txt");
		try {

			Scanner s = new Scanner(entityFile);
			while (s.hasNextLine()) {
				double x = s.nextDouble(), y = s.nextDouble();
				int type = s.nextInt();
				if (type == Obstacle.OBSTACLE_TREE) {
					new Tree(x, y);
				} else if (type == Obstacle.OBSTACLE_CAMPFIRE) {
					new Campfire(x, y);
				} else if (type == Obstacle.OBSTACLE_TALL_GRASS) {
					new TallGrass(x, y);
				}
			}
			
			s.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		new Hagrid(-0.5, -2.5);
	}
	
}
