package environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import algorithms.Toolbox;
import main.Game;
import main.Launcher;

public class DayNightCycler {

	public static final int DAY_SECONDS = 300;
	public static final float BLUE_FACTOR = 0.2f;
	public static final float MAXIMUM_OPACITY = 0.63f;

	public static final int CYCLE_LENGTH = DAY_SECONDS * Game.idealFrameRate;
	
	public static final float NINE_AM = 0f / 24f,
							  NOON = 3f / 24,
							  SIX_PM = 9f / 24f,
							  NINE_PM = 12f / 24f,
							  MIDNIGHT = 15f / 24f,
							  SIX_AM = 21f / 24f;

	public static final int TIME_MORNING = 1, TIME_AFTERNOON = 2, TIME_NIGHT = 3;

	public static int getGeneralTime() {
		float fraction = fraction();
		if (fraction > SIX_AM || fraction < NOON) {
			return TIME_MORNING;
		} else if (fraction < SIX_PM) {
			return TIME_AFTERNOON;
		} else {
			return TIME_NIGHT;
		}
	}

	public static float fraction() {
		return (Game.frameCount % CYCLE_LENGTH) / (float) CYCLE_LENGTH;
	}

	public static void renderTime(Graphics2D g) {
		Toolbox.setAlign(Toolbox.ALIGN_RIGHT, Toolbox.ALIGN_TOP);
		g.setColor(Color.BLACK);
		Toolbox.setFontSize(g, 16);
		Toolbox.drawText(g, getRoughTime(), Launcher.WIDTH - 10, 10);
	}

	public static String getRoughTime() {
		float fraction = (Game.frameCount % CYCLE_LENGTH) / (float) CYCLE_LENGTH;
		float hoursPast9 = fraction * 24;
		int wholeHoursPast9 = (int) hoursPast9;
		int actualHour = (wholeHoursPast9 + 9) % 12;
		actualHour = (actualHour == 0) ? 12 : actualHour;
		int minutes = (int) ((hoursPast9 - wholeHoursPast9) * 60) / 5 * 5;
		boolean PM = (fraction >= 0.125 && fraction < 0.625);
		return actualHour + ":" + (minutes < 10 ? "0" : "") + minutes + (PM ? " PM" : " AM");
	}

	public static String getExactTime() {
		float fraction = (Game.frameCount % CYCLE_LENGTH) / (float) CYCLE_LENGTH;
		float hoursPast9 = fraction * 24;
		int wholeHoursPast9 = (int) hoursPast9;
		int actualHour = (wholeHoursPast9 + 9) % 12;
		actualHour = (actualHour == 0) ? 12 : actualHour;
		int minutes = (int) ((hoursPast9 - wholeHoursPast9) * 60);
		boolean PM = (fraction >= 0.125 && fraction < 0.625);
		return actualHour + ":" + (minutes < 10 ? "0" : "") + minutes + (PM ? " PM" : " AM");
	}

	public static void renderTint(Graphics2D g) { // based on tick count

		float fraction = (Game.frameCount % CYCLE_LENGTH) / (float) CYCLE_LENGTH;
		if (fraction <= SIX_PM) {
			// 9 am to 7 pm (Day)
			return;
		} else if (fraction <= NINE_PM) {
			// 7 pm to 9 pm (Sunset)
			float opacity = (float) Toolbox.map(fraction, SIX_PM, NINE_PM, 0, MAXIMUM_OPACITY);
			g.setColor(new Color(0, 0, BLUE_FACTOR, opacity));
		} else if (fraction <= SIX_AM) {
			// 9 pm to 7 am (Night)
			g.setColor(new Color(0, 0, BLUE_FACTOR, MAXIMUM_OPACITY));
		} else {
			// 7 am to 9 pm (Sunrise)
			float opacity = (float) Toolbox.map(fraction, SIX_AM, 1.0f, MAXIMUM_OPACITY, 0);
			g.setColor(new Color(0, 0, BLUE_FACTOR, opacity));
		}

		Rectangle area = new Rectangle();
		area.setSize(Launcher.WIDTH, Launcher.HEIGHT);

		g.fill(area);
	}

}
