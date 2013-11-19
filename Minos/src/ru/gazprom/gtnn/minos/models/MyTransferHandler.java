package ru.gazprom.gtnn.minos.models;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

public class MyTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	private Image img;
	private String str;

	public MyTransferHandler(String str) {
		this.str = str;
		try {
			img = ImageIO.read(new File("C:\\Users\\Ed\\Google Диск\\Новая папка\\Minos\\image\\page_add_32.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			img = null;
		}
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		System.out.println(str + "  : MyTransferHandler.getSourceActions  :  " + c.getName());
		return TransferHandler.LINK;
		// 		return super.getSourceActions(c);
	}

	@Override
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.canImport  " + arg0.getName());
		return super.canImport(arg0, arg1);
	}

	@Override
	public boolean canImport(TransferSupport arg0) {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.canImport");
		return arg0.isDataFlavorSupported(DataFlavor.stringFlavor);
		//return super.canImport(arg0);
	}

	@Override
	protected Transferable createTransferable(JComponent arg0) {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.createTransferable  " + arg0.getName());
		return new StringSelection("hello");
		//return super.createTransferable(arg0);
	}

	@Override
	public Image getDragImage() {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.getDragImage");
		return img == null ? super.getDragImage() : img;
		//return super.getDragImage();
	}

	@Override
	public boolean importData(JComponent arg0, Transferable arg1) {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.importData   " + arg0.getName());
		
		return super.importData(arg0, arg1);
	}

	@Override
	public boolean importData(TransferSupport arg0) {
		// TODO Auto-generated method stub
		System.out.println(str + "  : MyTransferHandler.importData");

		if(arg0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				// н/о добавить в модель новую строку
				System.out.println(arg0.getDropLocation());
				System.out.println(arg0.getComponent().getName());
				System.out.println(((JTree)arg0.getComponent()).getPathForLocation(arg0.getDropLocation().getDropPoint().x, 
						arg0.getDropLocation().getDropPoint().y));
				System.out.println(arg0.getTransferable().getTransferData(DataFlavor.stringFlavor));
			
				return true;
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		//return super.importData(arg0);
		return false;
	}




}

