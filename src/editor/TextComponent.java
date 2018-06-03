package editor;


import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;

/**
 * Компонента для отображения текста.
 * Используется для отрисовки текущего экрана.
 * @author Александр Подхалюзин
 * @version 1.0
 */

public class TextComponent extends Panel
{
	final static long serialVersionUID=1;

	/**
	 * Ссылка на родительское окно, для связи с полосами прокрутки,
	 * которые в нем находятся.
	 */
	WindowView app;
	/**
	 * Основные параметры компоненты.
	 */
	public final int height=15;
	public final int width=9;
	public final int left = 5;
	public final int top = 12;
	public int numLines=25;
	public int numTokens=72;
	/**
	 * Ссылка на класс, оперерующий с изменениями в тексте.
	 */
	public Model model;
	/**
	 * Шрифты.
	 */
	private Font f;
	private Font fCursor;
	private final int fontSize =15;
	/**
	 * Переменные двойной буферизации.
	 */
	private Graphics buffGraphics;
	private Image buffImage;
	private boolean buff=true;

	/**
	 * Конструктор. Определяет основные параметры компоненты.
	 * Устанавливает связь с родительским окном.
	 * @param xsize Ширина компоненты.
	 * @param ysize Высота компоненты.
	 * @param app Родительское окно.
	 */
	public TextComponent(int xsize,int ysize,WindowView app)
	{
		this.app =app;

		setSize(new Dimension(xsize,ysize));
		setBackground(Color.black);
		setVisible(true);
		/**
		 * Добавление адаптеров для прослушивания событий.
		 */
		addMouseListener(new WMouseAdapter(this));
		addMouseMotionListener(new WMouseAdapter(this));
		addMouseWheelListener(new WMouseAdapter(this));
		addKeyListener(new WKeyAdapter(this));
		/**
		 * Для возможоности использовать табуляцию.
		 */
		setFocusTraversalKeysEnabled(false);

		numLines=(ysize-2*top)/height;
		numTokens=(xsize-2*left)/width;
		model = new Model(this);
		/**
		 * Создание шрифтов
		 */
		f=new Font("Monospaced",Font.PLAIN, fontSize);
		Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		fCursor = new Font("Monospaced",Font.PLAIN, fontSize);
		fCursor = fCursor.deriveFont(map);
		setFont(f);
	}
	public void paint(Graphics g)
	{
		/**
		 * Изменения положений ползунков на полосах прокрутки.
		 */
		app.sb.setMinimum(0);
		app.sb.setMaximum(Math.max(0,model.getLinesSize()+1-numLines));
		app.sb.setValue(model.getPosY());
		app.sbb.setMinimum(0);
		app.sbb.setMaximum(Math.max(0,model.getMaxX()+1-numTokens));
		app.sbb.setValue(model.getPosX());
		/**
		 * Установка переменных буферезации. Просходит только один раз.
		 */
		if (buff)
		{
			buffImage = createImage(getSize().width,getSize().height);
			buffGraphics = buffImage.getGraphics();
			buffGraphics.setColor(Color.black);
			buffGraphics.fillRect(0, 0,getSize().width, getSize().height);
			buff=false;
		}
		/**
		 * Отрисовка экрана.
		 */
		buffGraphics.setColor(Color.black);
		buffGraphics.fillRect(0, 0, getSize().width, getSize().height);
		for (int i=0; i<numLines; ++i)
			for (int j=0; j<numTokens; ++j)
			{
				switch (model.fonts[i][j])
				{
				case '0': //Основной шрифт
					buffGraphics.setColor(Color.white);
					break;
				case '1': //Подсветка ключевых слов
					buffGraphics.setColor(Color.cyan);
					break;
				case '2': //Подсветка комментариев
					buffGraphics.setColor(Color.green);
					break;
				case '3': //Шрифт курсора
					buffGraphics.setColor(Color.red);
                    buffGraphics.drawLine(j*width+left,(i)*height+top+3,j*width+left,(i-1)*height+top+3);
					break;
				case '4': //Шрифт выделенной области
					buffGraphics.setColor(Color.blue);
					buffGraphics.fillRect((j)*width+left, (i)*height, width, height);
					buffGraphics.setColor(Color.white);
					break;
				case '5': //Если курсор часть выделенной области                              
                    buffGraphics.setColor(Color.blue);
					buffGraphics.fillRect((j)*width+left, (i)*height, width, height);
                    buffGraphics.setColor(Color.red);
                    buffGraphics.drawLine(j*width+left,(i)*height+top+3,j*width+left,(i-1)*height+top+3);
					break;
				case '6': //Курсор при нажатой клавише Ins
					buffGraphics.setColor(Color.red);
					buffGraphics.fillRect((j)*width+left, (i)*height, width, height);
					buffGraphics.setColor(Color.white);
					break;
				case '7': //Подсветка строковых и символьных литералов
					buffGraphics.setColor(Color.magenta);
					break;
				}
				String s="";
				s+=model.lines[i][j];
				buffGraphics.drawString(s, j*width+left, (i)*height+top);
			}
		g.drawImage(buffImage,0,0,this);
	}
	/**
	 * Метод для сглаживания перерисовки.
	 */
	public void update(Graphics g)
	{
		paint(g);
	}
}
