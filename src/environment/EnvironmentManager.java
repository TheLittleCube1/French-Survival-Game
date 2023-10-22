package environment;

import java.awt.Graphics2D;

public class EnvironmentManager {
	
	public static void createEnvironment(Graphics2D g) {
		DayNightCycler.renderTint(g);
		DayNightCycler.renderTime(g);
	}
	
}
