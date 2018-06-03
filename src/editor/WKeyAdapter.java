package editor;

import java.awt.event.*;

/**
 * Данный класс обслуживает события ввода из клавиатуры.
 * @author Александр Подхалюзин
 * @version 1.0
 */

public class WKeyAdapter extends KeyAdapter{
	TextComponent comp;
	public WKeyAdapter(TextComponent comp)
	{
		this.comp = comp;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		if (!e.isControlDown())
			comp.model.insertKey(e.getKeyChar());
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isShiftDown())
		{
			comp.model.setIsShift(true);
		}
		if (e.getKeyCode()!=KeyEvent.VK_UNDEFINED)
		{
			if (e.getKeyCode()== KeyEvent.VK_V && e.isControlDown())
				comp.model.changeCursor(KeyEvent.VK_PASTE);
			else if (e.getKeyCode()== KeyEvent.VK_C && e.isControlDown())
				comp.model.changeCursor(KeyEvent.VK_COPY);
			else if (e.getKeyCode()== KeyEvent.VK_X && e.isControlDown())
				comp.model.changeCursor(KeyEvent.VK_CUT);
			else
				comp.model.changeCursor(e.getKeyCode());
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (!e.isShiftDown())
		{
			comp.model.setIsShift(false);
			comp.model.dragged =false;
		}
	}
}
