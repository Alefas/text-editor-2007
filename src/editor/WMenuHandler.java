package editor;


import java.awt.FileDialog;
import java.awt.event.*;

/**
 * Данный класс обслуживает события меню.
 * @author Александр Подхалюзин
 * @version 1.0
 */

public class WMenuHandler implements ActionListener{
	WindowView app;
	public WMenuHandler(WindowView app) {
		this.app =app;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Quit"))
		{
			System.exit(0);
		}
		else if (e.getActionCommand().equals("Open File..."))
		{
			app.fileDialog = new FileDialog(app,"Open File...",FileDialog.LOAD);
			app.fileDialog.setVisible(true);
			if (app.fileDialog.getFile()!=null)
			app.text.model.openFile(app.fileDialog.getDirectory()+ app.fileDialog.getFile());
		}
		else if (e.getActionCommand().equals("New"))
		{
			app.text.model.newFile();
		}
		else if (e.getActionCommand().equals("Save As..."))
		{
			app.fileDialog = new FileDialog(app,"Save File...",FileDialog.SAVE);
			app.fileDialog.setVisible(true);
			if (app.fileDialog.getFile()!=null)
			app.text.model.save(app.fileDialog.getDirectory()+ app.fileDialog.getFile());
		}
		else if (e.getActionCommand().equals("Save"))
		{
			if (app.text.model.file!=null)
				app.text.model.save(app.text.model.file);
			else
			{
                app.fileDialog = new FileDialog(app,"Save File...",FileDialog.SAVE);
				app.fileDialog.setVisible(true);
                if (app.fileDialog.getFile()!=null)
                    app.text.model.save(app.fileDialog.getDirectory()+ app.fileDialog.getFile());
			}
		}
		else if (e.getActionCommand().equals("Paste"))
		{
			app.text.model.paste();
		}
		else if (e.getActionCommand().equals("Copy"))
		{
			app.text.model.copy();
		}
		else if (e.getActionCommand().equals("Cut"))
		{
			app.text.model.cut();
		}
		else if (e.getActionCommand().equals("Select All"))
		{
			app.text.model.selectAll();
		}
    }
}
