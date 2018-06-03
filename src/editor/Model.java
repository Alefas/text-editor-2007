package editor;

import java.util.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.awt.event.*;

/**
 * Класс моделирующий работу с текстом.
 * @author Александр Подхалюзин
 * @version 1.0
 */
public class Model {
	/**
	 * Текст хранится здесь.
	 * Второй массив используется для хранения вида шрифта.
	 * Но обновляется он каждое перерисовывание экрана после изменений, поэтому
	 */
	private ArrayList <StringBuffer> textLines;
	private ArrayList <StringBuffer> textFonts;
	public int getLinesSize()
	{
		return textLines.size();
	}
	/**
	 * Ссылка к компоненте.
	 */
	private TextComponent comp;
	/**
	 * Конструктор. Определяет начальные значения всевозможных параметров.
	 * @param comp ссылка к компоненте
	 */
	public Model(TextComponent comp)
	{
		signs = new Vector <Integer>();
		signs.add(0);
		clip = comp.getToolkit().getSystemClipboard();
		keys=javaWords;
		file=null;
		textLines = new ArrayList <StringBuffer>(0);
		textFonts = new ArrayList <StringBuffer>(0);
		textLines.add(new StringBuffer("\n"));
		textFonts.add(new StringBuffer("0"));
		posX=0;
		posY=0;
		cursorPosX=0;
		cursorPosY=0;
		this.comp = comp;
		lines = new char[comp.numLines][comp.numTokens];
		fonts = new char[comp.numLines][comp.numTokens];
		getArea(lines,fonts);
        currentXpos = 0;
    }
	/**
	 * Текущий экран.
	 */
	public char lines[][], fonts[][];
	/**
	 * Число сиволов в длиннейшей строке.
	 * Это число не уменьшается.
	 * Это значит, если длиннейшая строка уменьшается по длине,
	 * то эта переменная не изменится.
	 */
	private int maxX;
	public int getMaxX() {
		return maxX;
	}
	/**
	 * Текущая позиция экрана.
	 */
	private int posX,posY;
	public int getPosX() {
		return posX;
	}
	public int getPosY() {
		return posY;
	}
	public void setPosY(int posY) {
		if (posY>=0 && posY<textLines.size()-comp.numLines+1)
			this.posY = posY;
		else if (posY<0)
			this.posY=0;
		else
			this.posY= Math.max(0,textLines.size()-comp.numLines);
	}
	public void setPosX(int posX) {
		this.posX = posX;
	}
	/**
	 * Текущее положение курсора.
	 */
	private int cursorPosX,cursorPosY;
	public int getCursorPosX() {
		return cursorPosX;
	}
	public int getCursorPosY() {
		return cursorPosY;
	}
	public void setCursorPosX(int cursorPosX) {
		this.cursorPosX = cursorPosX;
	}
	public void setCursorPosY(int cursorPosY) {
		this.cursorPosY = cursorPosY;
	}
    public int currentXpos;
    /**
	 * Текущий открытый файл.
	 * Если документ новый, то null.
	 */
	public String file;
	/**
	 * Ключевые слова Java.
	 * Второй массив используется для представления первого.
	 * Например, для возможности добать сюда ключевые слова
	 * другого языка.
	 */
	private String[] javaWords={"abstract", "boolean", "break", "byte", "case", "catch",
			"char", "class", "const", "continue", "default", "do", "double", "else",
			"extends", "final", "finally", "float", "for", "goto", "if", "implements",
			"import", "instanceof", "int", "interface", "long", "native", "new", "package",
			"private", "protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
			"void", "volatile", "while"};
	private String[] keys;
	/**
	 * Буфер обмена.
	 */
	private Clipboard clip;

	/**
	 * Выделенная область текста.
	 */
	private int draggedFirstY=0;
	private int draggedFirstX=0;
	private int draggedSecondY=0;
	private int draggedSecondX=0;
	public boolean dragged =false;
	/**
	 * Проверяет выделен ли кусок текст.
	 * Это означет, что позиции начала выделения и
	 * конца выделения совпадают или нет.
	 * @return Выделен ли текст.
	 */
	public boolean draggedTrue()
	{
        return draggedFirstY != draggedSecondY || !(draggedSecondX == draggedFirstX);
	}
	/**
	 * Снимает выделение с текста.
	 */
	public void noDragged()
	{
		dragged =false;
		draggedFirstY=0;
		draggedFirstX=0;
		draggedSecondY=0;
		draggedSecondX=0;
	}
	/**
	 * Некоторое число индикаторов.
	 * Индикаторы нажатых клавиш Ins и Shift.
	 * Индикатор очистки выделенной области.
	 */
	private boolean isClearing=false;
	private boolean isIns=false;
	private boolean isShift=false;
	/**
	 * Создание текущего образа экрана.
	 * @param lines сюда возврацается текст
	 * @param fonts сюда возвращаются шрифты
	 */
	public void getArea(char[][] lines, char[][] fonts)
	{
		for (int i=posY; i<posY+lines.length && i< textLines.size(); ++i) resetFont(i);
		for (int i=posY; i<lines.length+posY; ++i)
			for (int j=posX; j<lines[0].length+posX; ++j)
			{
				if (i>= textLines.size() || i>= textFonts.size() || j>= textLines.get(i).length() || j>= textFonts.get(i).length() ||
						textLines.get(i).charAt(j)=='\n' || textLines.get(i).charAt(j) =='\t' || textLines.get(i).charAt(j) ==(char)0)
				{
					lines[i-posY][j-posX]=' ';
					if (i>= textLines.size() || i>= textFonts.size() || j>= textLines.get(i).length() || j>= textFonts.get(i).length())
						fonts[i-posY][j-posX]='0';
					else
						fonts[i-posY][j-posX]= textFonts.get(i).charAt(j);
				}
				else
				{
					lines[i-posY][j-posX]= textLines.get(i).charAt(j);
					fonts[i-posY][j-posX]= textFonts.get(i).charAt(j);
				}
				/**
				 * Изменения шрифта, если эта точка была выделена.
				 */
				if ((i-draggedSecondY)*(i-draggedFirstY)<0) fonts[i-posY][j-posX]='4';
				if ((i-draggedSecondY)==0 && (draggedSecondY>draggedFirstY) && j<draggedSecondX)
					fonts[i-posY][j-posX]='4';
				if ((i-draggedSecondY)==0 && (draggedSecondY<draggedFirstY) && j>=draggedSecondX)
					fonts[i-posY][j-posX]='4';
				if ((i-draggedFirstY)==0 && (draggedSecondY>draggedFirstY) && j>=draggedFirstX)
					fonts[i-posY][j-posX]='4';
				if ((i-draggedFirstY)==0 && (draggedSecondY<draggedFirstY) && j<draggedFirstX)
					fonts[i-posY][j-posX]='4';
				if (draggedSecondY==draggedFirstY && i==draggedFirstY &&
						j>=Math.min(draggedFirstX, draggedSecondX) &&
						j<Math.max(draggedFirstX, draggedSecondX))
					fonts[i-posY][j-posX]='4';
			}
		if (cursorPosY-posY>=0 && cursorPosY-posY< comp.numLines
				&& cursorPosX-posX>=0 && cursorPosX-posX< comp.numTokens)
		if (isIns)
			fonts[cursorPosY-posY][cursorPosX-posX]='6';
		else if (fonts[cursorPosY-posY][cursorPosX-posX]=='4')
			fonts[cursorPosY-posY][cursorPosX-posX]='5';
		else
			fonts[cursorPosY-posY][cursorPosX-posX]='3';
	}

	/**
	 * Изменяет положение курсора в связи с произошедшим событием.
	 * В осноном сюда входят навигационные клавишы.
	 * Также сюда входят вставка копирование и вырезание.
	 * В каждом из случаев изменение положения курсора
	 * происходит банальным перебором текущего, с последующим
	 * вызовом метода SetCursor().
	 * @param x Индекс события.
	 */
	public void changeCursor(int x)
	{
		switch (x)
		{
		case KeyEvent.VK_DOWN:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,currentXpos-posX,cursorPosY-posY);
				dragged =true;
			}
			if (isShift && dragged)
			{
				setCursor(2,currentXpos-posX,cursorPosY-posY+1);
			}
			else
				setCursor(0,currentXpos-posX,cursorPosY-posY+1);
			break;
		case KeyEvent.VK_UP:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,currentXpos-posX,cursorPosY-posY);
				dragged =true;
			}
			if (isShift && dragged)
			{
				if (cursorPosY>0)
					setCursor(2,currentXpos-posX,cursorPosY-posY-1);
			}
			else
			{
			if (cursorPosY>0)
				setCursor(0,currentXpos-posX,cursorPosY-posY-1);
			}
			break;
		case KeyEvent.VK_LEFT:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
                currentXpos=cursorPosX;
                setCursor(3,cursorPosX-posX,cursorPosY-posY);
				dragged =true;
			}
			if (isShift && dragged)
			{
				if (cursorPosX>0)
                {
                    currentXpos=cursorPosX-1;
                    setCursor(2,cursorPosX-posX-1,cursorPosY-posY);
                }
                    else if (cursorPosY>0)
                {
                        currentXpos=textLines.get(posY+cursorPosY-posY-1).length()-1;
                        setCursor(2, textLines.get(posY+cursorPosY-posY-1).length()-posX,cursorPosY-posY-1);
                }
            }
			else
			{
				if (cursorPosX>0)
                {
                    currentXpos=cursorPosX-1;
                    setCursor(0,cursorPosX-posX-1,cursorPosY-posY);
                }
                else if (cursorPosY>0)
                {
                     currentXpos=textLines.get(posY+cursorPosY-posY-1).length()-1;
                    setCursor(0, textLines.get(posY+cursorPosY-posY-1).length()-posX,cursorPosY-posY-1);
                }
            }
			break;
		case KeyEvent.VK_RIGHT:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
                currentXpos=cursorPosX;
                setCursor(3,cursorPosX-posX,cursorPosY-posY);
				dragged =true;
			}
			if (isShift && dragged)
			{
				int t1=cursorPosX-posX;
                currentXpos=cursorPosX+1;
                setCursor(2,cursorPosX-posX+1,cursorPosY-posY);
				if (t1==draggedSecondX && textLines.get(cursorPosY).charAt(cursorPosX)=='\t')
                {
                    currentXpos=cursorPosX+4;
                    setCursor(2,cursorPosX-posX+4,cursorPosY-posY);
                }
                if (t1==draggedSecondX && cursorPosY< textLines.size()-1)
                {
                    currentXpos=0;
                    setCursor(2,-posX,cursorPosY-posY+1);
                }
            }
			else
			{
                int t=posX+cursorPosX-posX;
                currentXpos=cursorPosX+1;
                setCursor(0,cursorPosX-posX+1,cursorPosY-posY);
                if (t==posX+cursorPosX-posX && textLines.get(cursorPosY).charAt(cursorPosX)=='\t')
                {
                        currentXpos=cursorPosX+4;
                    setCursor(0,cursorPosX-posX+4,cursorPosY-posY);
                }
                if (t==posX+cursorPosX-posX && cursorPosY< textLines.size()-1)
                {
                    currentXpos=0;
                    setCursor(0,-posX,cursorPosY-posY+1);
                }
            }
			break;
		case KeyEvent.VK_END:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,cursorPosX-posX,cursorPosY-posY);
				setCursor(2,cursorPosX-posX,cursorPosY-posY);
				dragged =true;
			}
			if (isShift && dragged)
			{
                currentXpos=textLines.get(posY+cursorPosY-posY).length()-1;
                setCursor(2, textLines.get(posY+cursorPosY-posY).length()-posX,cursorPosY-posY);
			}
			else
            {
                currentXpos=textLines.get(posY+cursorPosY-posY).length()-1;
            setCursor(0, textLines.get(posY+cursorPosY-posY).length()-posX,cursorPosY-posY);
            }
            break;
		case KeyEvent.VK_HOME:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,cursorPosX-posX,cursorPosY-posY);
				setCursor(2,cursorPosX-posX,cursorPosY-posY);
				dragged =true;
			}
			else if (isShift && dragged)
			{
                currentXpos=0;
                setCursor(2,-posX,cursorPosY-posY);
			}
			else
            {
                currentXpos=0;
            setCursor(0,-posX,cursorPosY-posY);
            }
            break;
		case KeyEvent.VK_PAGE_DOWN:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,cursorPosX-posX,cursorPosY-posY);
				setCursor(2,cursorPosX-posX,cursorPosY-posY);
				dragged =true;
			}
			else if (isShift && dragged)
			{
				setCursor(2,currentXpos-posX,cursorPosY-posY+ comp.numLines);
			}
			else
			setCursor(0,currentXpos-posX,cursorPosY-posY+ comp.numLines);
			break;
		case KeyEvent.VK_PAGE_UP:
			if (!isShift && draggedTrue())
				noDragged();
			if (isShift && !dragged)
			{
				setCursor(3,currentXpos-posX,cursorPosY-posY);
				setCursor(2,currentXpos-posX,cursorPosY-posY);
				dragged =true;
			}
			else if (isShift && dragged)
			{
				if (cursorPosY- comp.numLines>=0)
					setCursor(2,currentXpos-posX,cursorPosY-posY- comp.numLines);
				else
					setCursor(2,currentXpos-posX,-posY);
			}
			else
			{
			if (cursorPosY- comp.numLines>=0)
				setCursor(0,currentXpos-posX,cursorPosY-posY- comp.numLines);
			else
				setCursor(0,currentXpos-posX,-posY);
			}
			break;
		case KeyEvent.VK_PASTE:
			paste();
			break;
		case KeyEvent.VK_COPY:
			copy();
			break;
		case KeyEvent.VK_CUT:
			cut();
			break;
		case KeyEvent.VK_INSERT:
			isIns=!isIns;
			comp.repaint();
			break;
		}
		getArea(lines,fonts);
	}
	/**
	 * Устанавливает курсор в необходимое положение, в соответствии с
	 * правилами положения курсора, который не может находится
	 * в позиции, где нет текста.
	 * Также при изменении param, дает возможность изменять
	 * начальное и конечное положения выделенной области.
	 * @param param определяет что надо изменить
	 * @param x положение в строке относитльно текущего экрана
	 * @param y строка относительно текущего экрана
	 */
	public void setCursor(int param,int x,int y)
	{
		int curX=x+posX;
		int curY=y+posY;
		if (curY>= textLines.size())
		{
			curY= textLines.size()-1;
		}
		while (curX>= textLines.get(curY).length() || textLines.get(curY).charAt(curX)==0) curX--;
		if (curX>=posX && curX<posX+ comp.numTokens
				&& curY>=posY && curY<posY+ comp.numLines)
		{
			cursorPosX=curX;
			cursorPosY=curY;
		}
		else
		{
			if (curY>=posY && curY<posY+ comp.numLines)
			{
				if (curX<posX)
					posX=Math.max(curX- comp.numTokens/5,0);
				else
					posX=curX- comp.numTokens+1;
				cursorPosX=curX;
				cursorPosY=curY;
			}
			else if (curX>=posX && curX<posX+ comp.numTokens)
			{
				if (curY<posY)
					posY=curY;
				else
					posY=curY- comp.numLines+1;
				cursorPosX=curX;
				cursorPosY=curY;
			}
			else
			{
				if (curX<posX)
					posX=Math.max(curX- comp.numTokens/5,0);
				else
					posX=curX- comp.numTokens+1;
				posY=curY;
				cursorPosX=curX;
				cursorPosY=curY;
			}
		}
		switch (param)
		{
		case 1:
			draggedFirstX=curX;
			draggedFirstY=curY;
			break;
		case 2:
			draggedSecondX=curX;
			draggedSecondY=curY;
			break;
		case 3:
			draggedFirstX=curX;
			draggedFirstY=curY;
			draggedSecondX=curX;
			draggedSecondY=curY;
			break;
		}
		getArea(lines,fonts);
		comp.repaint();
	}
	/**
	 * Проверяет, является ли символ верным, чтобы слово стоящее
	 * за ним подсвечивалось как ключевое
	 * @param z символ перед (или после) ключевым словом
	 * @return подсвечивать ли это ключевое слово
	 */
	public boolean isGood(char z)
	{
        return !(z >= 'a' && z <= 'z') && (z < 'A' || z > 'Z') && !(z == '_' || z == '$');
    }
	/**
	 * Данный метод обновляет шрифты соответствующей строки.
	 * Это происходит быстро. Так как все необходимые расчеты
	 * для подсветки многострочных комментариев делаются заранее.
	 * Алгоритм прост. Происходит проход по строке несколько раз.
	 * Сначала определяются ключевые слова. При необходимости они будут
	 * перекрашены в дальнейшем. Затем перекрашиваем остальные случаи.
	 * Перекраска по принципу конечного автомата. Красим в
	 * зависимости от конечного числа состояний (покраска ',
	 * покраска ", покраска комментариев,отсутсвие покраски),
	 * и переводит по простой логике в следующее состояние.
	 * Для определения начального состояния, используется массив
	 * signs. По нему мы в обратном направлении ищем первый ненулевой
	 * элемент. Единица означает, что состояние покраски комментариев.
	 * Во всех остальных случаях, начальное состояние нулевое.
	 * @param line строка в тексте
	 */
	public void resetFont(int line)
	{
		if (line< textFonts.size())
		{
			textFonts.remove(line);
			StringBuffer set = new StringBuffer();
			for (int i=0; i< textLines.get(line).length(); ++i) set.append('0');
			/**
			 * Подсветка ключевых слов.
			 */
            for (String key : keys) {
                String z = textLines.get(line).toString();
                int start = 0;
                while (true) {
                    int ind = z.indexOf(key, start);
                    if (ind == -1) break;
                    start = ind + key.length();
                    if ((ind == 0 || isGood(z.charAt(ind - 1))) && (isGood(z.charAt(ind + key.length()))))
                        for (int j = ind; j < ind + key.length(); ++j) {
                            set.setCharAt(j, '1');
                        }
                }
            }
			/**
			 * Теперь запускаем конечный автомат.
			 */
			StringBuffer strlits=new StringBuffer(textLines.get(line));
			for (int i=0; i< textLines.get(line).length(); ++i) if (strlits.charAt(i)==0)
				strlits.setCharAt(i, 't');
			int h=line-1;
			while (h>=0 && signs.get(h).equals(Integer.valueOf(0)))
			{
				h--;
			}
			/**
			 * Переменная отражающая текущее состояние автомата.
			 * Сама реализация проходит банальным перебором
			 * случаев и просмотра текущего символа.
			 */
			int state;
			if (h>=0) state= signs.get(h);
			else state=0;
			if (state<0) state=0;
			for (int i=0; i<strlits.length(); ++i)
			{
				switch (state)
				{
				case 0:
					if (strlits.charAt(i)=='\'')
					{
						state=2;
						set.setCharAt(i, '7');
					}
					else if (strlits.charAt(i)=='\"')
					{
						state=3;
						set.setCharAt(i, '7');
					}
					else if (strlits.charAt(i)=='/' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='*')
					{
						state=1;
						set.setCharAt(i, '2');
					}
					else if (strlits.charAt(i)=='/' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='/')
					{
						state=4;
						set.setCharAt(i, '2');
					}
					break;
				case 1:
					if (strlits.charAt(i)=='*' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='/')
					{
						state=0;
						set.setCharAt(i, '2');
						set.setCharAt(i+1, '2');
						i++;
					}
					else
					{
						set.setCharAt(i, '2');
					}
					break;
				case 2:
					if (strlits.charAt(i)=='\'')
					{
						state=0;
						set.setCharAt(i, '7');
					}
					else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='\'')
					{
						set.setCharAt(i, '7');
						set.setCharAt(i+1, '7');
						i++;
					}
					else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='\\')
					{
						set.setCharAt(i, '7');
						set.setCharAt(i+1, '7');
						i++;
					}
					else
					{
						set.setCharAt(i, '7');
					}
					break;
				case 3:
					if (strlits.charAt(i)=='\"')
					{
						state=0;
						set.setCharAt(i, '7');
					}
					else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='\"')
					{
						set.setCharAt(i, '7');
						set.setCharAt(i+1, '7');
						i++;
					}
					else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
							strlits.charAt(i+1)=='\\')
					{
						set.setCharAt(i, '7');
						set.setCharAt(i+1, '7');
						i++;
					}
					else
					{
						set.setCharAt(i, '7');
					}
					break;
				case 4:
					set.setCharAt(i, '2');
					break;
				}
			}
			textFonts.add(line, set);
		}
	}
	public Vector <Integer> signs;
	/**
	 * Обновляет массив signs.
	 * Стратегия такая же, как в предыдущем методе
	 * только без обновления массива шрифтов.
	 * @param line  номер строки
	 */
	public void resetLists(int line)
	{
		StringBuffer strlits=new StringBuffer(textLines.get(line));
		int state=0;
		int initstate=0;
		for (int i=0; i<strlits.length(); ++i)
		{
			switch (state)
			{
			case -1:
			case 0:
				if (strlits.charAt(i)=='\'')
				{
					state=2;
				}
				else if (strlits.charAt(i)=='\"')
				{
					state=3;
				}
				else if (strlits.charAt(i)=='/' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='*')
				{
					initstate=1;
					state=1;
				}
				else if (strlits.charAt(i)=='/' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='/')
				{
					signs.set(line, state);
					return;
				}
				break;
			case 1:
				if (strlits.charAt(i)=='*' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='/')
				{
					state=0-initstate;
					i++;
				}
				break;
			case 2:
				if (strlits.charAt(i)=='\'')
				{
					state=0-initstate;
				}
				else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='\'')
				{
					i++;
				}
				else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='\\')
				{
					i++;
				}
				break;
			case 3:
				if (strlits.charAt(i)=='\"')
				{
					state=0-initstate;
				}
				else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='\"')
				{
					i++;
				}
				else if (strlits.charAt(i)=='\\' && i<strlits.length()-1 &&
						strlits.charAt(i+1)=='\\')
				{
					i++;
				}
				break;
			}
		}
		if (state!=1 && state!=-1)
        {
            if (strlits.indexOf("*/")==-1)
                state=0;
            else
                state=-1;
        }
        signs.set(line, state);
	}
	/**
	 * Вставляет символ в текст.
	 * @param c вставляемый символ
	 */
	public void insertKey(char c)
	{
		if (!isClearing && draggedTrue())
		{
			clear();
			if (c==KeyEvent.VK_BACK_SPACE
					|| c==KeyEvent.VK_DELETE) return;
		}
        if (isIns && c==KeyEvent.VK_ENTER)
		{
            currentXpos=0;
            setCursor(0,-posX,cursorPosY-posY+1);
		}
        else if (c==KeyEvent.VK_ENTER)
		{
			signs.add(cursorPosY+1, 0);
			textLines.add(cursorPosY+1,
					new StringBuffer(textLines.get(cursorPosY).substring(cursorPosX)));
			textLines.set(cursorPosY,
					new StringBuffer(textLines.get(cursorPosY).substring(0, cursorPosX)));
			textFonts.add(new StringBuffer(""));
				textLines.get(cursorPosY).append('\n');
			resetLists(cursorPosY+1);
			resetLists(cursorPosY);
            currentXpos=0;
            setCursor(0,-posX,cursorPosY-posY+1);
		}
		else if (c==KeyEvent.VK_TAB)
		{
			for (int i=0; i<3; ++i)
				textLines.get(cursorPosY).insert(cursorPosX-posX, (char) 0);
			textLines.get(cursorPosY).insert(cursorPosX-posX, '\t');
			resetLists(cursorPosY);
            currentXpos=cursorPosX+4;
            setCursor(0,cursorPosX-posX+4,cursorPosY-posY);
		}
		else if (c==KeyEvent.VK_BACK_SPACE)
		{
			if (cursorPosX>0)
			{
				if (textLines.get(cursorPosY).charAt(cursorPosX-1)==0)
				{
					textLines.get(cursorPosY).delete(cursorPosX-4, cursorPosX);
					resetLists(cursorPosY);
                    currentXpos=cursorPosX-4;
                    setCursor(0,cursorPosX-4,cursorPosY-posY);
				}
				else
				{
					textLines.get(cursorPosY).deleteCharAt(cursorPosX-1);
					resetLists(cursorPosY);
                    currentXpos=cursorPosX-1;
                    setCursor(0,cursorPosX-1,cursorPosY-posY);
				}
			}
			else if (cursorPosY>0)
			{
				signs.remove(cursorPosY);
				int tok= textLines.get(cursorPosY-1).length()-1;
				textLines.get(cursorPosY-1).deleteCharAt(textLines.get(cursorPosY-1).length()-1);
				textLines.get(cursorPosY-1).append(textLines.get(cursorPosY));
				textLines.remove(cursorPosY);
				resetLists(cursorPosY-1);
                currentXpos=tok;
                setCursor(0,tok-posX,cursorPosY-posY-1);
			}
		}
		else if (c==KeyEvent.VK_DELETE)
		{
			if (textLines.get(cursorPosY).charAt(cursorPosX)=='\t')
			{
				textLines.get(cursorPosY).delete(cursorPosX, cursorPosX+4);
				resetLists(cursorPosY);
                currentXpos=cursorPosX;
                setCursor(0,cursorPosX,cursorPosY-posY);
			}
			else if (textLines.get(cursorPosY).charAt(cursorPosX)!='\n')
			{
				textLines.get(cursorPosY).deleteCharAt(cursorPosX);
				resetLists(cursorPosY);
                currentXpos=cursorPosX;
                setCursor(0,cursorPosX,cursorPosY-posY);
			}
			else if(cursorPosY< textLines.size()-1)
			{
				signs.remove(cursorPosY+1);
				textLines.get(cursorPosY).deleteCharAt(textLines.get(cursorPosY).length()-1);
				textLines.get(cursorPosY).append(textLines.get(cursorPosY+1));
				textLines.remove(cursorPosY+1);
				resetLists(cursorPosY);
                currentXpos=cursorPosX;
                setCursor(0,cursorPosX-posX,cursorPosY-posY);
			}
		}
		else if (isIns && textLines.get(cursorPosY).charAt(cursorPosX)!='\n')
		{
			textLines.get(cursorPosY).setCharAt(cursorPosX,c);
			resetLists(cursorPosY);
            currentXpos=cursorPosX+1;
            setCursor(0,cursorPosX-posX+1,cursorPosY-posY);
		}
		else
		{
			textLines.get(cursorPosY).insert(cursorPosX,c);
			resetLists(cursorPosY);
            currentXpos=cursorPosX+1;
            setCursor(0,cursorPosX-posX+1,cursorPosY-posY);
		}
		draggedFirstY=0;
		draggedFirstX=0;
		draggedSecondY=0;
		draggedSecondX=0;
		getArea(lines,fonts);
		if (maxX< textLines.get(cursorPosY).length()) maxX= textLines.get(cursorPosY).length();
	}
	/**
	 * Открытие файла.
	 * @param s имя файла
	 */
	public void openFile(String s)
	{
		try
		{
			maxX=0;
            currentXpos=0;
            FileReader fr = new FileReader(s);
			BufferedReader br = new BufferedReader(fr);
            file=s;
            textLines =new ArrayList<StringBuffer>(0);
			textFonts =new ArrayList<StringBuffer>(0);
			signs = new Vector <Integer>();
			while (true)
			{
				StringBuffer string=new StringBuffer(br.readLine());
				if (string==null) break;
				for (int i=0; i<string.length(); ++i)
					if (string.charAt(i)=='\t')
					{
						for (int j=0; j<3; ++j)
						{
							string.insert(i+1, (char) 0);
							++i;
						}
					}
				string.append('\n');
				textLines.add(new StringBuffer(string));
				if (string.length()>maxX) maxX=string.length();
				StringBuffer str = new StringBuffer();
				for (int i=0; i<string.length(); ++i) str.append('0');
				signs.add(0);
				resetLists(signs.size()-1);
				textFonts.add(new StringBuffer(str));
			}
			/**
			 * В случае, если файл пустой.
			 */
			if (textLines.size()==0)
			{
				signs.add(0);
				StringBuffer buff;
				buff = new StringBuffer("\n");
				textLines.add(buff);
				buff = new StringBuffer("0");
				textFonts.add(buff);
			}
		}
		catch (Exception e)
		{
			/**
			 * Нет необходимости что-либо делать.
			 */
		}
		posX=0;
		posY=0;
		setCursor(0,0,0);
		comp.repaint();
		getArea(lines,fonts);
	}
	public void newFile()
	{
        file=null;
        maxX=0;
        currentXpos=0;
        noDragged();
		textLines =new ArrayList<StringBuffer>(0);
		textFonts =new ArrayList<StringBuffer>(0);
		StringBuffer buff;
		buff = new StringBuffer("\n");
		textLines.add(buff);
		buff = new StringBuffer("0");
		textFonts.add(buff);
		posX=0;
		posY=0;
		setCursor(0,0,0);
		getArea(lines,fonts);
		comp.repaint();
	}
	public void save(String s)
	{
		try
		{
			FileWriter fr = new FileWriter(s);
			BufferedWriter br = new BufferedWriter(fr);
			for (int i=0; i< textLines.size(); ++i)
			{
				StringBuffer ss=new StringBuffer(textLines.get(i).toString());
				ss.deleteCharAt(ss.length()-1);
				while (true)
				{
					int ind=ss.indexOf(""+(char) 0);
					if (ind==-1) break;
					ss.deleteCharAt(ind);
				}
				br.write(ss.toString());
				if (i!= textLines.size()-1)
					br.newLine();
			}
			br.flush();
			file=s;
		}
		catch (Exception e)
		{
			/**
			 * Нет необходимости что-либо делать.
			 */
		}
		getArea(lines,fonts);
	}
	public void paste()
	{
		if (!isClearing && draggedTrue())
		{
			clear();
		}
		Object content=null;
		Transferable clipData = clip.getContents(content);
		String Text=null;
	    try
	    {
	    	Text = (String)clipData.getTransferData(DataFlavor.stringFlavor);
	    }
	      catch(Exception ex) {
	    	  /**
				 * Нет необходимости что-либо делать.
				 */
	      }
	      int pos=cursorPosY;
	      String g= textLines.get(pos).substring(cursorPosX);
	      textLines.get(pos).delete(cursorPosX, textLines.get(pos).length());
	      for (int i=0; i<Text.length(); ++i)
	      {
	    	  StringBuffer ss = new StringBuffer();
	    	  while (i<Text.length() && Text.charAt(i)!='\n')
	    	  {
	    		  ss.append(Text.charAt(i));
	    		  if (Text.charAt(i)=='\t')
	    		  {
	    			  for (int j=0; j<3; ++j)
	    			  {
	    				  ss.append((char) 0);
	    			  }
	    		  }
	    		  i++;
	    	  }
	    	  textLines.get(pos).append(ss);
	    	  if (i<Text.length() && Text.charAt(i)=='\n')
	    	  {
	    		  textLines.get(pos).append('\n');
	    		  pos++;
	    		  textLines.add(pos,new StringBuffer(""));
	    	  }
	      }
	      textLines.get(pos).append(g);
	      for (int i=cursorPosY; i<=pos; ++i)
	    	  {
	    	  	if (maxX< textLines.get(i).length()) maxX= textLines.get(i).length();
	    	  	if (i>cursorPosY)
	    	  	{
	    	  	signs.add(i, 0);
	    	  	textFonts.add(i,new StringBuffer());
	    	  	}
	    	  	resetLists(i);
	    	  }
          currentXpos=textLines.get(pos).length()-g.length();
          setCursor(0, textLines.get(pos).length()-g.length()-posX,pos-posY);
	      getArea(lines,fonts);
	      comp.repaint();
	}
	public void copy()
	{
		if (draggedTrue())
		{
			int x1,y1,x2,y2;
			if (draggedFirstY>draggedSecondY)
			{
				y1=draggedFirstY;
				x1=draggedFirstX;
				y2=draggedSecondY;
				x2=draggedSecondX;
			}
			else if (draggedFirstY<draggedSecondY)
			{
				y2=draggedFirstY;
				x2=draggedFirstX;
				y1=draggedSecondY;
				x1=draggedSecondX;
			}
			else if (draggedFirstX<draggedSecondX)
			{
				y2=draggedFirstY;
				x2=draggedFirstX;
				y1=draggedSecondY;
				x1=draggedSecondX;
			}
			else
			{
				y1=draggedFirstY;
				x1=draggedFirstX;
				y2=draggedSecondY;
				x2=draggedSecondX;
			}
			String s="";
			for (int i=y2; i<=y1; ++i)
			{
				if (i==y1 && i==y2) s+= textLines.get(i).substring(x2, x1);
				else if (i==y2) s+= textLines.get(i).substring(x2);
				else if (i==y1) s+= textLines.get(i).substring(0, x1);
				else s+= textLines.get(i).toString();
			}
			StringBuffer ss=new StringBuffer(s);
			while (true)
			{
				int ind=ss.indexOf(""+(char) 0);
				if (ind==-1) break;
				ss.deleteCharAt(ind);
			}
			StringSelection select = new StringSelection(ss.toString());
			clip.setContents(select, select);
		}
		getArea(lines,fonts);
	}
	public void cut()
	{
		copy();
		clear();
	}
	/**
	 * Очистка выделенной области.
	 */
	public void clear()
	{
		if (draggedTrue())
		{
			isClearing=true;
			int x1,y1,x2,y2;
			if (draggedFirstY>draggedSecondY)
			{
				y1=draggedFirstY;
				x1=draggedFirstX;
				y2=draggedSecondY;
				x2=draggedSecondX;
			}
			else if (draggedFirstY<draggedSecondY)
			{
				y2=draggedFirstY;
				x2=draggedFirstX;
				y1=draggedSecondY;
				x1=draggedSecondX;
			}
			else if (draggedFirstX<draggedSecondX)
			{
				y2=draggedFirstY;
				x2=draggedFirstX;
				y1=draggedSecondY;
				x1=draggedSecondX;
			}
			else
			{
				y1=draggedFirstY;
				x1=draggedFirstX;
				y2=draggedSecondY;
				x2=draggedSecondX;
			}
			for (int i=y2; i<=y1; ++i)
			{
				if (i==y2 && i==y1)
				{
					textLines.get(i).delete(x2, x1);
				}
				else if (i==y2)
				{
					textLines.get(i).delete(x2, textLines.get(i).length()-1);
				}
				else if (i==y1)
				{
					textLines.get(y2+1).delete(0, x1);
				}
				else
				{
					textLines.remove(y2+1);
					textFonts.remove(y2+1);
					signs.remove(y2+1);
				}
			}
			if (y1!=y2)
			setCursor(0,-posX,y2+1-posY);
			else
				setCursor(0,x2-posX,y2-posY);
			if (y1!=y2)
			insertKey((char)KeyEvent.VK_BACK_SPACE);
			resetLists(y2);
            if (y2< textLines.size()-1) resetLists(y2+1);
        }
		noDragged();
		isClearing=false;
		getArea(lines,fonts);
		comp.repaint();
	}
	public void selectAll()
	{
		draggedFirstX=0;
		draggedFirstY=0;
		draggedSecondY= textLines.size()-1;
		draggedSecondX= textLines.get(draggedSecondY).length()-1;
		getArea(lines,fonts);
		comp.repaint();
	}
	public void setIsShift(boolean b) {
		isShift=b;
	}
}