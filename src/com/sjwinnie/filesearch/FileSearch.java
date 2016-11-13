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


public class FileSearch {

	private JFrame FileSearch;
	private JTextField txtFldBaseFolder;
	private JTextField txtFldKeyWords;
	
	private FileListView m_fileView;
	
	private int m_nSearchMode;
	private String m_strBaseFold;	//待搜索路径
	private String m_strKeyWords;	//关键字
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
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JLabel lblSearchMode = new JLabel("Search Mode");
		m_nSearchMode = 0;
		
		JComboBox cbxSearchMode = new JComboBox();
		cbxSearchMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox cbx = (JComboBox) arg0.getSource();
				m_nSearchMode = cbx.getSelectedIndex();
			}
		});
		
		cbxSearchMode.setModel(new DefaultComboBoxModel(new String[] {"Keywords Search", "Time Range Search"}));
		
		JLabel lblFileCreate = new JLabel("File Created");
		
		JComboBox cbxCreatedTime = new JComboBox();
		
		cbxCreatedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
		
		JLabel lblFileModifed = new JLabel("File Modified");
		
		JComboBox cbxModifiedTime = new JComboBox();
		
		cbxModifiedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
		
		JLabel lblFile = new JLabel("File Accessed");
		
		JComboBox cbxAccessedTime = new JComboBox();
		
		cbxAccessedTime.setModel(new DefaultComboBoxModel(new String[] {"All Times", "Time Range"}));
				
		
		DateChooserJButton dtbCreatedTimeStart = new DateChooserJButton();
		dtbCreatedTimeStart.setEnabled(false);
		
		DateChooserJButton dtbModifiedTimeStart = new DateChooserJButton();
		dtbModifiedTimeStart.setEnabled(false);
		
		DateChooserJButton dtbAccessedTimeStart = new DateChooserJButton();
		dtbAccessedTimeStart.setEnabled(false);
		
		DateChooserJButton dtbCreatedTimeEnd = new DateChooserJButton();
		dtbCreatedTimeEnd.setEnabled(false);
		
		DateChooserJButton dtbModifiedTimeEnd = new DateChooserJButton();
		dtbModifiedTimeEnd.setEnabled(false);
		
		DateChooserJButton dtbAccessedTimeEnd = new DateChooserJButton();
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
		
		// 搜索按钮监听事件
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_strBaseFold == null)	// 如果未选择路径
					return;
				
				// searchfile from dir
				m_fileArray = new File(m_strBaseFold).listFiles();
				if (m_fileArray == null)
					return;
					
				DefaultTableModel dtm = (DefaultTableModel) m_fileView.getModel();
				dtm.getDataVector().clear();
				
				// 不关闭搜索结果，继续搜索
				if (m_fileView.isVisible())
					m_fileView.updateUI();
				
				
				m_nSearchMode = cbxSearchMode.getSelectedIndex();
				m_strKeyWords = txtFldKeyWords.getText();
				m_bIsCaseSensitive = cbxCaseSensitive.isSelected();		
				
				
				if(m_nSearchMode==0){	//关键字搜索
					for (File file : m_fileArray) {
						if (file.isFile()) {							
							String filename = file.getName();
							// 字符串匹配，正则表达式
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
				}
				else if(m_nSearchMode==1){	//时间范围搜索
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
					
					for (File file : m_fileArray) {
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
					}
				}							

				// show UI
				m_fileView.setVisible(true);			

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
					.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSearch)
						.addComponent(btnCancel))
					.addGap(29))
		);
		FileSearch.getContentPane().setLayout(groupLayout);
	}

	
	private Date getFileCreateDate(final File file){
		Date ret = null;
		
		if(null==file){
			return ret;
		}
		
		final StringBuilder sb = new StringBuilder();
		Process pro=null;
		
		try{
			pro = Runtime.getRuntime().exec(String.format("cmd /C dir %s /tc", file.getAbsolutePath()));
		}
		catch(IOException e){
			return ret;
		}
		
		final InputStream is = pro.getInputStream();
		final InputStreamReader ir = new InputStreamReader(is);
		final BufferedReader br = new BufferedReader(ir);
			
		Runnable runnable = new Runnable(){
			@Override  
            public void run() {  
                String data = null;  
                  
                try{  
                    while(null != (data = br.readLine())){  
                        if(-1 != data.toLowerCase().indexOf(file.getName().toLowerCase())){  
                            String[] temp = data.split(" +");  
                              
                            if(2 <= temp.length)  {  
                                String time = String.format("%s %s", temp[0], temp[1]);  
                                sb.append(time);  
                            }                                
                            break;  
                        }  
                    }  
                }
                catch(IOException e){  
                    e.printStackTrace();  
                }  
                finally {  
                    try {  
                        if(null != br) {  
                            br.close();  
                        }  
                          
                        if(null != ir) {  
                            ir.close();  
                        }  
                          
                        if(null != is) {  
                            is.close();  
                        }  
                    }  
                    catch(IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
		};
		
		 Thread thread = new Thread(runnable);  
	     thread.start();  
	     
	     try{
	    	 thread.join();
	     } catch(InterruptedException e){
	    	 e.printStackTrace();
	     }
	     
	     if(0 != sb.length()){
	    	 	sb.append(":00");	//windows提取的时间不带秒
	            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
	            try {
					ret = sdf.parse(sb.toString());
				} catch (ParseException e) {					
					e.printStackTrace();
				}
	     }
		
		return ret;				
	}
}
