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


public class FileSearch {

	private JFrame FileSearch;
	private JTextField txtFldBaseFolder;
	private JTextField txtFldKeyWords;
	
	private FileListView m_fileView;
	
	private String m_strBaseFold;
	private String m_strKeyWords;
	private Boolean m_bIsCaseSensitive;
	
	private File[] m_fileArray;
	private static ArrayList<String> m_strArrayFileInfo;  
	private static ArrayList<String> m_strArrayDirInfo;
	private static ArrayList<File>	m_lstFileModel;

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
		FileSearch.setBounds(100, 100, 450, 300);
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
		
		
		GroupLayout groupLayout = new GroupLayout(FileSearch.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(37)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnSearch)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblFileContains, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBaseFolder))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtFldBaseFolder, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtFldKeyWords))))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxCaseSensitive)
						.addComponent(btnBrowser)
						.addComponent(btnCancel))
					.addContainerGap(12, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(50)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBaseFolder)
						.addComponent(txtFldBaseFolder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowser))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFileContains)
						.addComponent(txtFldKeyWords, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbxCaseSensitive))
					.addPreferredGap(ComponentPlacement.RELATED, 96, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSearch)
						.addComponent(btnCancel))
					.addGap(29))
		);
		FileSearch.getContentPane().setLayout(groupLayout);
	}

	
}
