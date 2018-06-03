package editor;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * Данный класс определяет основное окно.
 * В нем также определяется метод main.
 * @author Александр Подхалюзин
 * @version 1.0
 */

public class WindowView extends Frame
implements AdjustmentListener{
	final static long serialVersionUID=1;
	public TextComponent text;
	public FileDialog fileDialog;
	public Scrollbar sb, sbb;
    /**
	 * Этот метод фиксирует события изменения полос прокрутки.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
			if (e.getAdjustable()==sb)
			{
			text.model.setPosY(e.getValue());
			text.requestFocus();
			text.model.getArea(text.model.lines, text.model.fonts);
			text.repaint();
			}
			else
			{
				text.model.setPosX(e.getValue());
				text.requestFocus();
				text.model.getArea(text.model.lines, text.model.fonts);
				text.repaint();
			}
		}
	/**
	 * Конструктор.
	 * Создает две полосы прокрутки, область для ввода текста и меню.
	 */
	public WindowView()
	{
		setLayout(new BorderLayout());
		this.text = new TextComponent(780,556,this);
		this.add(this.text,BorderLayout.CENTER);
		this.text.requestFocus();
        sb = new Scrollbar(Scrollbar.VERTICAL,0,1,0,0);
		add(sb,BorderLayout.EAST);
		sb.setVisible(true);
		sb.addAdjustmentListener(this);
		sbb = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,0);
		add(sbb,BorderLayout.SOUTH);
		sbb.setVisible(true);
		sbb.repaint();
		sbb.validate();
		sbb.addAdjustmentListener(this);
		addWindowListener(new WWindowAdapter());

		/**
		 * Создание меню.
		 */
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);
		Menu file = new Menu("File");
		MenuItem[] itemFile = new MenuItem[6];
		file.add(itemFile[0]=new MenuItem("New"));
		file.add(itemFile[1]=new MenuItem("Open File..."));
		file.add(itemFile[2]=new MenuItem("Save"));
		file.add(itemFile[3]=new MenuItem("Save As..."));
		file.add(itemFile[4]=new MenuItem("-"));
		file.add(itemFile[5]=new MenuItem("Quit"));
		menuBar.add(file);
		Menu edit = new Menu("Edit");
		MenuItem[] itemEdit = new MenuItem[5];
		edit.add(itemEdit[0]=new MenuItem("Cut"));
		edit.add(itemEdit[1]=new MenuItem("Copy"));
		edit.add(itemEdit[2]=new MenuItem("Paste"));
		edit.add(itemEdit[3]=new MenuItem("-"));
		edit.add(itemEdit[4]=new MenuItem("Select All"));
		menuBar.add(edit);
		WMenuHandler handler = new WMenuHandler(this);
		for (int i=0; i<6; ++i) itemFile[i].addActionListener(handler);
		for (int i=0; i<5; ++i) itemEdit[i].addActionListener(handler);
	}
	/**
	 * Метод main. Создает окно.
	 * @param args параметры отркрытия, в них нет необходимости
	 */
	public static void main(String[] args) {
		WindowView app=new WindowView();

		/**
		 * Установка параметров окна.
		 */
		app.setSize(new Dimension(800,600));
		app.setMaximumSize(new Dimension(100,100));
		app.setResizable(false);
		app.setTitle("TextEditor");
		app.setVisible(true);
		app.setBackground(Color.gray);
		app.setLocation(50, 50);
        app.text.requestFocus();
    }
}
