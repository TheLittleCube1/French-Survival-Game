package input;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

import states.GameState;

public class MouseWheelManager extends JFrame implements MouseWheelListener {
	private static final long serialVersionUID = -5761437823052236407L;

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//GameState.zoom = Toolbox.constrain(GameState.zoom - e.getPreciseWheelRotation() * 0.01, 0.65, 1.6);
		GameState.zoom = GameState.zoom - e.getPreciseWheelRotation() * 0.01;
	}
	
}
