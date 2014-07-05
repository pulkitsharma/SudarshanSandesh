package com.app.sandesh.ui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.app.sandesh.ExcelThroughApi;
import com.app.sandesh.model.ResultSetTableModel;
import com.app.sandesh.model.UserModel;

public class MainPanelInterface extends JFrame implements ActionListener{

	private JPanel searchPanel;
	private JPanel resultPanel;
	private JPanel buttonPanel;
	
	JTextField searchTextField;
	JTextArea searchTextInfo;
	JButton searchButton;
	JList jList;
	JButton clearButton;
	JButton insertButton;
	JButton updateRowButton;
	JTable table;
	ResultSetTableModel resModel;
	
	String[] searchOptionsList = { "Sr.No.", "Name", "Locality", "Mobile" };

	String[] columnNames = {"Select", "Sr.No.", "Name", "Address", "State", "PinCode", "MobileNo","Mode of Dispatch", "Reciept No","From", "To","Year/Five"};

	Object[][] data = new Object[10][12];

	MainPanelInterface() {
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		setTitle("Sudarshan Sandesh Application");
		container.setBackground(Color.BLUE);

		searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout());
		resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout());
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border emptyBorder = new EmptyBorder(40, 100, 40, 100);
		Border compound = BorderFactory.createCompoundBorder(emptyBorder,blackline);
		
		/*
		 * Search Panel
		 */
		searchTextInfo = new JTextArea("Search By");
		searchTextInfo.setBackground(getBackground());
		searchTextInfo.setEditable(false);

		JTextArea emptySpace1 = new JTextArea("    ");
		emptySpace1.setPreferredSize(new Dimension(100, 40));
		emptySpace1.setBackground(getBackground());
		emptySpace1.setEditable(false);
		
		jList = new JList(searchOptionsList);
		jList.setSelectedIndex(1);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setVisibleRowCount(4);
		JScrollPane scrollPane = new JScrollPane(jList);
		scrollPane.setPreferredSize(new Dimension(200, 100));

		JTextArea emptySpace2 = new JTextArea("    ");
		emptySpace2.setPreferredSize(new Dimension(100, 40));
		emptySpace2.setBackground(getBackground());
		emptySpace2.setEditable(false);
		
		searchTextField = new JTextField(20);
		searchTextField.setPreferredSize(new Dimension(150, 40));
		
		JTextArea emptySpace3 = new JTextArea("    ");
		emptySpace3.setPreferredSize(new Dimension(100, 40));
		emptySpace3.setBackground(getBackground());
		emptySpace3.setEditable(false);
		
		searchButton = new JButton("Search");
		searchButton.setPreferredSize(new Dimension(150, 40));
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);

		searchPanel.add(searchTextInfo);
		searchPanel.add(emptySpace1);
		searchPanel.add(scrollPane);
		searchPanel.add(emptySpace2);
		searchPanel.add(searchTextField);
		searchPanel.add(emptySpace3);
		searchPanel.add(searchButton);
		searchPanel.setBorder(compound);

		/*
		 * Result Panel
		 */
		resModel = new ResultSetTableModel(columnNames,data);
		table = new JTable(resModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(3).setPreferredWidth(300);
		table.getColumnModel().getColumn(2).setPreferredWidth(140);
		ResultSetTableModel.minWidth(table);
		resultPanel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		resultPanel.setBorder(new EmptyBorder(10, 100, 10, 100));
		
		/*
		 * Button Panel
		 */
		clearButton = new JButton("Clear");
		clearButton.setPreferredSize(new Dimension(200, 40));
		clearButton.setActionCommand("clear");
		clearButton.setToolTipText("Click to clear results");
		clearButton.addActionListener(this);
		
		JTextArea emptySpace = new JTextArea("        ");
		emptySpace.setPreferredSize(new Dimension(150, 50));
		emptySpace.setBackground(getBackground());
		emptySpace.setEditable(false);

		insertButton = new JButton("Insert");
		insertButton.setPreferredSize(new Dimension(200, 40));
		insertButton.setActionCommand("insert");
		insertButton.setToolTipText("Click to add a new member");
		insertButton.addActionListener(this);
		
		JTextArea emptySpace4 = new JTextArea("        ");
		emptySpace4.setPreferredSize(new Dimension(150, 50));
		emptySpace4.setBackground(getBackground());
		emptySpace4.setEditable(false);
		
		updateRowButton = new JButton("Update");
		updateRowButton.setPreferredSize(new Dimension(200, 40));
		updateRowButton.setToolTipText("Click to renew membership / update details");
		updateRowButton.setActionCommand("update");
		updateRowButton.addActionListener(this);

		buttonPanel.add(clearButton);
		buttonPanel.add(emptySpace);
		buttonPanel.add(insertButton);
		buttonPanel.add(emptySpace4);
		buttonPanel.add(updateRowButton);
		buttonPanel.setBorder(compound);
		
		container.add(searchPanel, BorderLayout.PAGE_START);
		container.add(resultPanel, BorderLayout.CENTER);
		container.add(buttonPanel, BorderLayout.PAGE_END);
		
	}

	public void actionPerformed(ActionEvent e) {
        if ("search".equals(e.getActionCommand())) {
        	String searchedName = searchTextField.getText();
        	String selectedOption = jList.getSelectedValue().toString();
        	if(null!=searchedName && searchedName.length()!=0 && searchedName.trim()!=""){
        		ArrayList searchedResults = ExcelThroughApi.searchEntry(selectedOption,searchedName);
        		Object[][] stringArray = new Object[searchedResults.size()][12];
        		Iterator iter = searchedResults.iterator();
        		int i=0;
        		while(iter.hasNext()) {
        			UserModel user = (UserModel) iter.next(); 
	        		stringArray[i][0] = new Boolean(false); 
	        		stringArray[i][1] = user.getSerialNumber(); 
	        		stringArray[i][2] = user.getUsername(); 
	        		stringArray[i][3] = user.getAddress(); 
	        		stringArray[i][4] = user.getState(); 
	        		stringArray[i][5] = user.getPinCode(); 
	        		stringArray[i][6] = user.getMobileNo(); 
	        		stringArray[i][7] = user.getModeOfDispatch(); 
	        		stringArray[i][8] = user.getReceiptNumber(); 
	        		stringArray[i][9] = user.getFrom(); 
	        		stringArray[i][10] = user.getTo(); 
	        		stringArray[i][11] = user.getYear(); 
	        		i++;
        		}
//        		data = ExcelThroughApi.searchEntry(selectedOption,searchedName);
//        		for(int i=0;i<data.length;i++){
//        			data[i][0] = new Boolean(false);
//        		}
        		resModel.updateDataSet(stringArray);
        	}
        } else if("insert".equals(e.getActionCommand())) {
        	//int[] selectedRows = table.getSelectedRows();
        	Object[] selectedRows = ResultSetTableModel.getSelectedRows(table);
        	int countRows = selectedRows.length;
        	while(countRows>0){
        		String[] args = new String[11];
        		String checkSrNo = (String)table.getValueAt((Integer)selectedRows[countRows-1], 1);
        		if(checkSrNo==null || checkSrNo.trim().length()==0){
    	            countRows--;
    	            JOptionPane.showMessageDialog(null, "Member " + args[1] + " added successfully");
        			continue;
        		}
        		for(int i=0;i<11;i++){
    	            String value = (String)table.getValueAt((Integer)selectedRows[countRows-1], i+1);
    	            args[i] = value;
        		}
	            if(args[0]!=null && args[0]!=""){
	            	JOptionPane.showMessageDialog(null, args[0] + " not added because it already exists");
	            }else {	            	
		            int result = ExcelThroughApi.insertEntries(args);
		            if(result == 0) {
		            	JOptionPane.showMessageDialog(null, "Member " + args[1] + " added successfully");
		            }
	            }
	            countRows--;
        	}
        	data = new Object[10][12];
        	resModel.updateDataSet(data);
        } else if("update".equals(e.getActionCommand())) {
        	Object[] selectedRows = ResultSetTableModel.getSelectedRows(table);
        	int countRows = selectedRows.length;
        	int actualUpdates=0;
        	for(int counter=0;counter<countRows;counter++) {
        		String[] args = new String[11];
        		String checkSrNo = (String)table.getValueAt((Integer)selectedRows[countRows-1], 1);
        		if(checkSrNo==null || checkSrNo.trim().length()==0){
        			continue;
        		}
        		for(int i=0;i<11;i++){
    	            String value = (String)table.getValueAt((Integer)selectedRows[countRows-1], i+1);
    	            args[i] = value;
        		}
	            ExcelThroughApi.updateEntries(args);
	            actualUpdates++;
        	}
        	JOptionPane.showMessageDialog(null, actualUpdates + " rows updated");
        	data = new Object[10][12];
        	resModel.updateDataSet(data);
        } else if("clear".equals(e.getActionCommand())) {
        	data = new Object[10][12];
        	resModel.updateDataSet(data);
        }

    }
	
	public void initializeProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			String filename = "config.properties";
			input = MainPanelInterface.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
				return;
			}
			prop.load(input);
			ExcelThroughApi.setPathToFile(prop.getProperty("pathToFile","Sudarshan_Sandesh_April-14_Pulkit"));
			ExcelThroughApi.setWarehousePathToFile(prop.getProperty("warehousePathToFile","Sudarshan_Sandesh_April-14_Pulkit"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
	}

	public static void main(String args[]) {

		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	MainPanelInterface mainUI = new MainPanelInterface();
            	mainUI.initializeProperties();
            	mainUI.setVisible(true);
            	mainUI.setExtendedState(mainUI.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            	mainUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
			
	}

}
