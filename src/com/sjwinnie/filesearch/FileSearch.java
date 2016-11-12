package com.sjwinnie.filesearch;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class FileSearch {

	private JFrame FileSearch;
	private JTextField txtFldBaseFolder;
	private JTextField txtFldKeyWords;
	
	private FileListView m_fileView;
	
	private String m_strBaseFold;
	private String m_strKeyWords;
	private Boolean m_bIsCaseSensitive;
	
	private File[] m_fileArray;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileSearch window = new FileSearch();
					window.FileSearch.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileSearch() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		FileSearch = new JFrame();
		FileSearch.setTitle("FileSearch");
		FileSearch.setBounds(100, 100, 620, 373);
		FileSearch.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
		
		JLabel lblBaseFolder = new JLabel("Base Folder");
		
		txtFldBaseFolder = new JTextField();
		txtFldBaseFolder.setColumns(10);
		
		JButton btnBrowser = new JButton("Browser...");
		btnBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				Component parent = null;
				int returnVal = chooser.showOpenDialog(parent);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					m_strBaseFold = chooser.getSelectedFile().getAbsolutePath();
					txtFldBaseFolder.setText(m_strBaseFold);
				}
			}
		});
		
		JLabel lblFileContains = new JLabel("File Contains");
		
		txtFldKeyWords = new JTextField();
		txtFldKeyWords.setColumns(10);
		
		JCheckBox cbxCaseSensitive = new JCheckBox("Case Sensitive");
		m_bIsCaseSensitive = cbxCaseSensitive.isSelected();
		
		m_fileView = new FileListView();
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_strBaseFold == null)
					return;
				m_strKeyWords = txtFldKeyWords.getText();
				m_bIsCaseSensitive = cbxCaseSensitive.isSelected();
				
				// searchfile from dir
				m_fileArray = new File(m_strBaseFold).listFiles();
				if (m_fileArray == null)
					return;
					
				DefaultTableModel dtm = (DefaultTableModel) m_fileView.getModel();
				dtm.getDataVector().clear();
				if (m_fileView.isVisible())
					m_fileView.updateUI();
				
				for (File file : m_fileArray) {
					if (file.isFile()) {
						// ×Ö·û´®Æ¥Åä
						String filename = file.getName();
						Pattern pattern;
						if(m_bIsCaseSensitive)
							pattern = Pattern.compile(m_strKeyWords);
						else
							pattern = Pattern.compile(m_strKeyWords, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(filename);
						if (matcher.find()) {
							Vector<String> v = new Vector<String>();
							v.add(file.getName());
							v.add(m_strBaseFold);
							dtm.addRow(v);		
						}				
		            } 					
				}			

				// show UI
				m_fileView.setVisible(true);			

			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JLabel lblSearchMode = new JLabel("Search Mode");
		
		JComboBox cbxSearchMode = new JComboBox();
		
		cbxSearchMode.setModel(new DefaultComboBoxModel(new String[] {"Keywords Search", "Time Range Search"}));
		
		JLabel lblFileCreate = new JLabel("File Created");
		
		JComboBox cbxCreateTime = new JComboBox();
		
		cbxCreateTime.setModel(new DefaultComboBoxModel(new String[] {"Time Range", "All Times"}));
		
		JLabel lblFileModifed = new JLabel("File Modified");
		
		JComboBox cbxModifiedTime = new JComboBox();
		
		cbxModifiedTime.setModel(new DefaultComboBoxModel(new String[] {"Time Range", "All Times"}));
		
		JLabel lblFile = new JLabel("File Accessed");
		
		JComboBox cbxAccessedTime = new JComboBox();
		
		cbxAccessedTime.setModel(new DefaultComboBoxModel(new String[] {"Time Range", "All Times"}));
				
		
		DateChooserJButton dtbCreatedTimeStart = new DateChooserJButton();
		
		DateChooserJButton dtbModifiedTimeStart = new DateChooserJButton();
		
		DateChooserJButton dtbAccessedTimeStart = new DateChooserJButton();
		
		DateChooserJButton dtbCreatedTimeEnd = new DateChooserJButton();
		
		DateChooserJButton dtbModifiedTimeEnd = new DateChooserJButton();
		
		DateChooserJButton dtbAccessedTimeEnd = new DateChooserJButton();
		

		cbxCreateTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cbx = (JComboBox) arg0.getSource();
				int index = cbx.getSelectedIndex();
				switch(index) {
				case 0:
					dtbCreatedTimeStart.setEnabled(true);
					dtbCreatedTimeEnd.setEnabled(true);
					break;
				case 1:
					dtbCreatedTimeStart.setEnabled(false);
					dtbCreatedTimeEnd.setEnabled(false);
					break;
				default:
					break;
				}
						
			}
		});
		
		cbxModifiedTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cbx = (JComboBox) arg0.getSource();
				int index = cbx.getSelectedIndex();
				switch(index) {
				case 0:
					dtbModifiedTimeStart.setEnabled(true);
					dtbModifiedTimeEnd.setEnabled(true);
					break;
				case 1:
					dtbModifiedTimeStart.setEnabled(false);
					dtbModifiedTimeEnd.setEnabled(false);
					break;
				default:
					break;
				}
						
			}
		});
		
		cbxAccessedTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cbx = (JComboBox) arg0.getSource();
				int index = cbx.getSelectedIndex();
				switch(index) {
				case 0:
					dtbAccessedTimeStart.setEnabled(true);
					dtbAccessedTimeEnd.setEnabled(true);
					break;
				case 1:
					dtbAccessedTimeStart.setEnabled(false);
					dtbAccessedTimeEnd.setEnabled(false);
					break;
				default:
					break;
				}
						
			}
		});
		
		
		GroupLayout groupLayout = new GroupLayout(FileSearch.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(48)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblFileContains, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBaseFolder)
						.addComponent(lblSearchMode, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFileCreate)
						.addComponent(lblFileModifed)
						.addComponent(lblFile))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(cbxAccessedTime, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(cbxModifiedTime, Alignment.LEADING, 0, 86, Short.MAX_VALUE))
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(dtbAccessedTimeStart, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
										.addComponent(dtbModifiedTimeStart, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(cbxCreateTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(dtbCreatedTimeStart, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(dtbAccessedTimeEnd, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
								.addComponent(dtbCreatedTimeEnd, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
								.addComponent(dtbModifiedTimeEnd, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnSearch)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnCancel))))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(cbxSearchMode, Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(txtFldBaseFolder, Alignment.LEADING)
								.addComponent(txtFldKeyWords, Alignment.LEADING, 299, 299, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(cbxCaseSensitive)
								.addComponent(btnBrowser))))
					.addGap(129))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(17)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSearchMode)
						.addComponent(cbxSearchMode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBaseFolder)
						.addComponent(txtFldBaseFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowser))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileContains)
						.addComponent(txtFldKeyWords, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbxCaseSensitive))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileCreate)
						.addComponent(cbxCreateTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbCreatedTimeStart, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbCreatedTimeEnd, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileModifed)
						.addComponent(cbxModifiedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbModifiedTimeStart, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbModifiedTimeEnd, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFile)
						.addComponent(cbxAccessedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbAccessedTimeStart, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
						.addComponent(dtbAccessedTimeEnd, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSearch)
						.addComponent(btnCancel))
					.addGap(29))
		);
		FileSearch.getContentPane().setLayout(groupLayout);
	}
}
