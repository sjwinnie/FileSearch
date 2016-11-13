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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;


public class FileSearch {

	// ����Ԫ��
	private JFrame FileSearch;
	private JTextField txtFldBaseFolder;
	private JTextField txtFldKeyWords;
	private JCheckBox cbxCaseSensitive;
	private JProgressBar progressBar;
	private JComboBox cbxCreatedTime;
	private JComboBox cbxModifiedTime;
	private JComboBox cbxAccessedTime;
	private JButton btnBrowser;
	private JButton btnCancel;
	private JButton btnSearch;
	
	
	// �ⲿ�����
	private FileListView m_fileView;
	private DateChooserJButton dtbCreatedTimeStart;
	private DateChooserJButton dtbModifiedTimeStart;
	private DateChooserJButton dtbAccessedTimeStart;
	private DateChooserJButton dtbCreatedTimeEnd;
	private DateChooserJButton dtbModifiedTimeEnd;
	private DateChooserJButton dtbAccessedTimeEnd;
	
	// ȫ�ֱ���
	private int m_nSearchMode = 0;
	private Boolean m_bIsCaseSensitive = false;
	private String m_strBaseFold = null;	//������·��
	private String m_strKeyWords = null;	//�ؼ���
	
	private File[] m_fileArray;
	
	// �����߳�
	private SearchThread m_searchThread = null;

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

		// ��Ӽ����¼�������
		btnBrowser.addActionListener(new ActionListenerBrowser());
		btnSearch.addActionListener(new ActionListenerSearch());
		btnCancel.addActionListener(new ActionListenerCancel());
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
		
		btnBrowser = new JButton("Browser...");
		
		JLabel lblFileContains = new JLabel("File Contains");
		
		txtFldKeyWords = new JTextField();
		txtFldKeyWords.setColumns(10);
		
		cbxCaseSensitive = new JCheckBox("Case Sensitive");
		cbxCaseSensitive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_bIsCaseSensitive = cbxCaseSensitive.isSelected();
			}
		});
		m_bIsCaseSensitive = cbxCaseSensitive.isSelected();
		
		m_fileView = new FileListView();
		
		btnSearch = new JButton("Search");
		
		btnCancel = new JButton("Cancel");
		
		JLabel lblSearchMode = new JLabel("Search Mode");
		
		progressBar = new JProgressBar(0,100);
		progressBar.setVisible(false);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		
		// ʱ������ѡ��ؼ�
		
		JComboBox cbxSearchMode = new JComboBox();
		
		cbxSearchMode.addActionListener(new ActionListenerSearchMode());
		
		cbxSearchMode.setModel(new DefaultComboBoxModel(new String[] {"Keywords Search", "Time Range Search"}));
		
		JLabel lblFileCreate = new JLabel("File Created");
		
		cbxCreatedTime = new JComboBox();
		
		cbxCreatedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
		
		JLabel lblFileModifed = new JLabel("File Modified");
		
		cbxModifiedTime = new JComboBox();
		
		cbxModifiedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
		
		JLabel lblFile = new JLabel("File Accessed");
		
		cbxAccessedTime = new JComboBox();
		
		cbxAccessedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
					
		dtbCreatedTimeStart = new DateChooserJButton();
		dtbCreatedTimeStart.setEnabled(false);
		
		dtbModifiedTimeStart = new DateChooserJButton();
		dtbModifiedTimeStart.setEnabled(false);
		
		dtbAccessedTimeStart = new DateChooserJButton();
		dtbAccessedTimeStart.setEnabled(false);
		
		dtbCreatedTimeEnd = new DateChooserJButton();
		dtbCreatedTimeEnd.setEnabled(false);
		
		dtbModifiedTimeEnd = new DateChooserJButton();
		dtbModifiedTimeEnd.setEnabled(false);
		
		dtbAccessedTimeEnd = new DateChooserJButton();
		dtbAccessedTimeEnd.setEnabled(false);
		

		cbxCreatedTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cbx = (JComboBox) arg0.getSource();
				int index = cbx.getSelectedIndex();
				switch(index) {
				case 0:
					dtbCreatedTimeStart.setEnabled(false);
					dtbCreatedTimeEnd.setEnabled(false);
					break;
				case 1:
					dtbCreatedTimeStart.setEnabled(true);
					dtbCreatedTimeEnd.setEnabled(true);
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
					dtbModifiedTimeStart.setEnabled(false);
					dtbModifiedTimeEnd.setEnabled(false);
					break;
				case 1:
					dtbModifiedTimeStart.setEnabled(true);
					dtbModifiedTimeEnd.setEnabled(true);
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
					dtbAccessedTimeStart.setEnabled(false);
					dtbAccessedTimeEnd.setEnabled(false);
					break;
				case 1:
					dtbAccessedTimeStart.setEnabled(true);
					dtbAccessedTimeEnd.setEnabled(true);
					break;
				default:
					break;
				}
						
			}
		});
		
		
		//���沼��
		
		GroupLayout groupLayout = new GroupLayout(FileSearch.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(48)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 277, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
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
											.addComponent(cbxCreatedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
							.addGap(129))))
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
						.addComponent(cbxCreatedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnSearch)
							.addComponent(btnCancel))
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		FileSearch.getContentPane().setLayout(groupLayout);
	}
	
	
	// ����ļ��¼�����
	private class ActionListenerBrowser implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			Component parent = null;
			int returnVal = chooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				m_strBaseFold = chooser.getSelectedFile().getAbsolutePath();
				txtFldBaseFolder.setText(m_strBaseFold);
			}
        }
	}
	
	
	// ѡ��ģʽ�¼�����
	private class ActionListenerSearchMode implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JComboBox cbx = (JComboBox) e.getSource();
			m_nSearchMode = cbx.getSelectedIndex();
        }
	}
	
	
	// ȡ���¼�����
	private class ActionListenerCancel implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
        }
	}
	
	
	// �����¼����� �¿��߳�
	private class ActionListenerSearch implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(m_searchThread != null){
				m_searchThread.interrupt();
				try {
					m_searchThread.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m_searchThread = null;
			}
				
			m_searchThread = new SearchThread();
			m_searchThread.start();	
		}
	}
	
	
	// ���������߳�
	class SearchThread extends Thread {
		
		public void run(){
			searchFile();
		}
	}
	
	// �����ļ�
	private void searchFile(){
		if (m_strBaseFold == null)	// ���δѡ��·��
			return;		
		
		// searchfile from dir
		m_fileArray = new File(m_strBaseFold).listFiles();
		if (m_fileArray == null)
			return;
		
		m_strKeyWords = txtFldKeyWords.getText();
		if(m_strKeyWords == null)
			return;
			
		// ���ԭ������
		DefaultTableModel dtm = (DefaultTableModel) m_fileView.getModel();
		dtm.getDataVector().clear();
		
		// ���ر������������������
		if (m_fileView.isVisible())
			m_fileView.updateUI();
		
		progressBar.setVisible(true);					
		
		int nFileCount = m_fileArray.length;			
		
		if(m_nSearchMode==0){	//�ؼ�������
			for (int i=0; i<nFileCount; ++i) {
				File file = m_fileArray[i];
				if (file.isFile()) {							
					String filename = file.getName();
					// �ַ���ƥ�䣬������ʽ
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
				// progressbar
				progressBar.setValue(i*100/nFileCount);
			}
		}
		else if(m_nSearchMode==1){	//ʱ�䷶Χ����
			Date dCreatedStart = null;
			Date dCreatedEnd = null;
			Date dModifiedStart = null;
			Date dModifiedEnd = null;
			Date dAccessedStart = null;
			Date dAccessedEnd = null;
			
			boolean bCreatedRange = cbxCreatedTime.getSelectedIndex()==1;
			boolean bModifiedRange = cbxModifiedTime.getSelectedIndex()==1;
			boolean bAccessedRange = cbxAccessedTime.getSelectedIndex()==1;
					
			if(bCreatedRange){
				dCreatedStart = dtbCreatedTimeStart.getDate();
				dCreatedEnd = dtbCreatedTimeEnd.getDate();
			}
			if(bModifiedRange){
				dModifiedStart = dtbModifiedTimeStart.getDate();
				dModifiedEnd = dtbModifiedTimeEnd.getDate();
			}
			if(bAccessedRange){
				dAccessedStart = dtbAccessedTimeStart.getDate();
				dAccessedEnd = dtbAccessedTimeEnd.getDate();
			}
			
			for (int i=0; i<nFileCount; ++i) {
				File file = m_fileArray[i];
				
				if (file.isFile()) {
					BasicFileAttributeView basicview = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
					try {
						BasicFileAttributes basicfile = basicview.readAttributes();
					
						Date created = new Date(basicfile.creationTime().toMillis());
						Date modified = new Date(file.lastModified());
						Date accessed = new Date(basicfile.lastAccessTime().toMillis());
						
						boolean bCreatedValid = bCreatedRange ? created.after(dCreatedStart) && created.before(dCreatedEnd) : true;
						boolean bModifiedValid = bModifiedRange ? modified.after(dCreatedStart) && modified.before(dCreatedEnd) : true;
						boolean bAccessedValid = bAccessedRange ? accessed.after(dAccessedStart) && accessed.before(dAccessedEnd) : true;
						
						if ( bCreatedValid && bModifiedValid && bAccessedValid) {
							Vector<String> v = new Vector<String>();
							v.add(file.getName());
							v.add(m_strBaseFold);
							dtm.addRow(v);		
						}
					
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            } 						
				// progressbar
				progressBar.setValue(i*100/nFileCount);
			}
		}
		progressBar.setValue(100);
		
		//progressBar.setVisible(false);
		
		// show UI
		m_fileView.setVisible(true);			
	}
	
}
