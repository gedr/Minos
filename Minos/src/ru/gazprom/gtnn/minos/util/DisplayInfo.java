package ru.gazprom.gtnn.minos.util;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DisplayInfo implements Runnable {
	private int cmd = 0;
	private String str;
	private JPanel pan;
	private Component oldGlassPane;
	private JFrame parentFrame;
	private JLabel info;

	public DisplayInfo(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		info = new JLabel();

		pan = new JPanel();
		pan.setLayout(new GridBagLayout());
		pan.setOpaque(false);			
		pan.add(info);

		// block mouse and keyboard message
		pan.addMouseListener(new MouseAdapter() { });
		pan.addKeyListener(new KeyAdapter() { });		
	}

	@Override
	public void run() {
		switch(cmd) {
		case 1:
			oldGlassPane = parentFrame.getGlassPane();
			parentFrame.setGlassPane(pan);
			pan.setVisible(true);
			break;
		case 2:
			if(oldGlassPane != null)
				parentFrame.setGlassPane(oldGlassPane);
			pan.setVisible(false);
			break;			
		case 3:
			info.setText(str);
			break;
		}
	}

	public void show() {
		cmd = 1;
		try {
			EventQueue.invokeAndWait(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}		

	public void hide() {
		cmd = 2;
		try {
			EventQueue.invokeAndWait(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void setText(String txt) {
		cmd = 3;
		str = txt;
		EventQueue.invokeLater(this);
	}

}
