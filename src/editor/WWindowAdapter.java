package editor;

import java.awt.event.*;

/**
 * ������ ����� ����������� ������� ������ �� ���������.
 * @author ��������� ����������
 * @version 1.0
 */

public class WWindowAdapter extends WindowAdapter{
	public WWindowAdapter() {
	}
	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}  	
}
