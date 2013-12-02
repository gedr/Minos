package ru.gazprom.gtnn.minos.test.main;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.Font;

public class compet extends JPanel {

	/**
	 * Create the panel.
	 */
	public compet() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{29, 29, 75, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JComboBox comboBox = new JComboBox();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.BOTH;
		gbc_comboBox.gridx = 0;
		gbc_comboBox.gridy = 0;
		add(comboBox, gbc_comboBox);
		
		JLabel lblNewLabel = new JLabel("New label");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		add(lblNewLabel, gbc_lblNewLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 2;
		add(scrollPane_1, gbc_scrollPane_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		add(scrollPane, gbc_scrollPane);
		
		JList list = new JList();
		scrollPane.setViewportView(list);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		add(panel, gbc_panel);
		
		JButton btnPrev = new JButton("\u041F\u0440\u0435\u0434\u044B\u0434\u0443\u0449\u0430\u044F \u043A\u043E\u043C\u043F\u0435\u0442\u0435\u043D\u0446\u0438\u044F");
		panel.add(btnPrev);
		
		JButton btnNewButton_1 = new JButton("\u0421\u043F\u0438\u0441\u043E\u043A \u043B\u0438\u0446");
		panel.add(btnNewButton_1);
		
		JButton btnNext = new JButton("\u0421\u043B\u0435\u0434\u0443\u044E\u0449\u0430\u044F \u043A\u043E\u043C\u043F\u0435\u0442\u0435\u043D\u0446\u0438\u044F");
		panel.add(btnNext);

	}

}
