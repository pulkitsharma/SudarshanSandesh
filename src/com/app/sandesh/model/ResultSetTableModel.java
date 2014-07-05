package com.app.sandesh.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel{

	Object[][] data;
	String columnNames[];
 
	
	public ResultSetTableModel(String[] columnNames, Object[][] data2) {
		this.data = data2;
		this.columnNames = columnNames;
		for(int i=0;i<data.length;i++){
			this.data[i][0] = new Boolean(false);
		}
	}


	public int getColumnCount() {
	    return columnNames.length;
	}
	
	public int getRowCount() {
	    return data.length;
	}
	
	public String getColumnName(int col) {
	    return columnNames[col];
	}
	
	public Object getValueAt(int row, int col) {
	    return data[row][col];
	}

	public void updateDataSet(Object[][] data2) {
		this.data = data2;
		this.fireTableDataChanged();
	}
	
	public Class getColumnClass(int c) {
		if(getValueAt(0, c)!=null)
			return getValueAt(0, c).getClass();
		else 
			return String.class;
    }
	
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public static void minWidth(JTable table){
		int numberOfColumns = table.getColumnCount();
		for(int i=0;i<numberOfColumns;i++){
			table.getColumnModel().getColumn(i).setMinWidth(60);
		}
		table.getColumnModel().getColumn(0).setMinWidth(30);
	}
	
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	if(col==1)
    		return false;
    	else 
    		return true;
    }
    
    public static Object[] getSelectedRows(JTable table) {
    	ArrayList<Integer> integerArray = new ArrayList<Integer>();
    	for(int i=0;i<table.getRowCount();i++){
    		Boolean checked = (Boolean)table.getValueAt(i, 0);
    		if(checked){
    			integerArray.add(new Integer(i));
    		}
    	}
    	return integerArray.toArray();
    }
}
