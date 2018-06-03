package editor;

import java.awt.event.*;

/**
 * Данный класс обслуживает события мышки.
 * @author Александр Подхалюзин
 * @version 1.0
 */

public class WMouseAdapter extends MouseAdapter{
	TextComponent comp;
	public WMouseAdapter(TextComponent comp)
	{
		this.comp = comp;
	}
	@Override
	public void mousePressed(MouseEvent me) {
		int x=(me.getX()- comp.left)/ comp.width;
		int y=(me.getY()- comp.top+ comp.height-1)/ comp.height;
		if (x>=0 && x< comp.numTokens && y>=0 && y< comp.numLines)
		{
			if (!comp.model.dragged)
			{
				comp.model.setCursor(3,x, y);
                comp.model.currentXpos=comp.model.getPosX()+comp.model.getCursorPosX();
                comp.model.dragged =true;
			}
			else
			{
				comp.model.setCursor(0,x, y);
                comp.model.currentXpos=comp.model.getPosX()+comp.model.getCursorPosX();
            }
		}
	}
	@Override
	public void mouseReleased(MouseEvent me) {
		int x=(me.getX()- comp.left)/ comp.width;
		int y=(me.getY()- comp.top+ comp.height-1)/ comp.height;
		if (x>=0 && x< comp.numTokens && y>=0 && y< comp.numLines)
		{
			comp.model.dragged =false;
            comp.model.setCursor(2,x, y);
            comp.model.currentXpos=comp.model.getPosX()+comp.model.getCursorPosX();
        }
	}
	@Override
	public void mouseDragged(MouseEvent me) {
		int x=(me.getX()- comp.left)/ comp.width;
		int y=(me.getY()- comp.top+ comp.height-1)/ comp.height;
		if (x+ comp.model.getPosX()>=0 && y+ comp.model.getPosY()>=0)
		{
            comp.model.setCursor(2,x, y);
            comp.model.currentXpos=comp.model.getPosX()+comp.model.getCursorPosX();
        }
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		comp.model.setPosY(comp.model.getPosY()+e.getUnitsToScroll());
		comp.model.getArea(comp.model.lines, comp.model.fonts);
		comp.repaint();
	}
}
