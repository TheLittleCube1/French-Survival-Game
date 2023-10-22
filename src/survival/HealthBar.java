package survival;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import algorithms.Toolbox;
import creature.Creature;
import creature.MushroomMonster;
import creature.Rabbit;
import main.Game;
import main.Launcher;
import player.CharacterGraphics;
import player.Player;
import states.GameState;

public class HealthBar {

	public double health, maxHealth;
	public double regenSeconds;

	public Creature creature = null;

	public float opacity = 0.0f;

	public HealthBar(double maxHealth, double regenSeconds, Creature creature) {
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.regenSeconds = regenSeconds;
		this.creature = creature;
	}

	public void tick() {
		double fullRegenerationTicks = regenSeconds * Game.averageFrameRate;
		double regenerationPerTick = maxHealth / fullRegenerationTicks;
		if (health >= maxHealth || health <= 0) {
			opacity = Math.max(0.0f, opacity - 1.0f / Game.averageFrameRate);
		} else {
			health = Math.min(maxHealth, health + regenerationPerTick);
		}
	}

	public void render(Graphics2D g) {
		
		double yDisplacement;
		if (creature == null) {
			yDisplacement = 0;
		} else if (creature instanceof Rabbit) {
			yDisplacement = creature.IMAGE_HEIGHT * 0.9;
		} else if (creature instanceof MushroomMonster) {
			yDisplacement = creature.IMAGE_HEIGHT * 0.77;
		}  else {
			yDisplacement = creature.IMAGE_HEIGHT;
		}
		
		if (creature == null) {
			int width = (int) (50 * GameState.zoom), height = (int) (6 * GameState.zoom);
			g.setColor(new Color(1.0f, 0.3f, 0.3f, opacity));
			g.fillRect(Launcher.WIDTH / 2 - width / 2,
					(int) (Toolbox.yOnScreen(Player.playerX, Player.playerY)
							- CharacterGraphics.playerRenderHeight * 1.2 * GameState.zoom),
					(int) (width * health / maxHealth), height);
			g.setColor(new Color(0, 0, 0, opacity / 3.0f));
			g.setStroke(new BasicStroke(1));
			g.drawRect(Launcher.WIDTH / 2 - width / 2, (int) (Toolbox.yOnScreen(Player.playerX, Player.playerY)
					- CharacterGraphics.playerRenderHeight * 1.2 * GameState.zoom), width, height);
			return;
		}
		
		else {
			int width = (int) (35 * GameState.zoom), height = (int) (4 * GameState.zoom);
			g.setColor(new Color(1.0f, 0.3f, 0.3f, opacity));
			int[] positionOnScreen = Toolbox.positionOnScreen(creature.x, creature.y);
			g.fillRect(positionOnScreen[0] - width / 2,
					(int) (positionOnScreen[1] - yDisplacement * GameState.zoom),
					(int) (width * health / maxHealth), height);
			g.setColor(new Color(0, 0, 0, opacity / 3.0f));
			g.drawRect(positionOnScreen[0] - width / 2,
					(int) (positionOnScreen[1] - yDisplacement * GameState.zoom), width, height);
		}
	}

	public void takeHit(double damage) {
		health -= damage;
		if (health <= 0) {
			die();
		} else {
			opacity = 1.0f;
		}
	}

	public void die() {
		if (creature != null) {
			creature.die();
		} else {
			System.out.println("Player died");
		}
	}

}
