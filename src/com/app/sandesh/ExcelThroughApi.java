package com.app.sandesh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import com.app.sandesh.model.UserModel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelThroughApi {

	private static String pathToFile;
	private static String warehousePathToFile;
	
	public static String getPathToFile() {
		return pathToFile;
	}

	public static void setPathToFile(String pathToFile) {
		ExcelThroughApi.pathToFile = pathToFile;
	}

	public static String getWarehousePathToFile() {
		return warehousePathToFile;
	}

	public static void setWarehousePathToFile(String warehousePathToFile) {
		ExcelThroughApi.warehousePathToFile = warehousePathToFile;
	}


	private static int maxOutputRows = 50;
	
	private static int columnsToShow = 12;

	public static int insertEntries(String[] args) {
		try {
			FileInputStream file = new FileInputStream(new File(pathToFile));

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);

			int rownum = sheet.getPhysicalNumberOfRows();
			
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (int i=0;i<columnsToShow-1;i++) {
				String obj = (String)args[i];
				Cell cell = row.createCell(cellnum++);
				if(i==0){
					cell.setCellValue(rownum-1);
				}else {					
					cell.setCellValue(obj);
				}
			}
			file.close();

			FileOutputStream outFile =new FileOutputStream(new File(pathToFile));
			workbook.write(outFile);
			outFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}


	public static ArrayList<UserModel> searchEntry(String field, String value) {
		
		Object[][] matchedResults = new Object[maxOutputRows][columnsToShow];
		
		ArrayList<UserModel> matchedUsers = new ArrayList<UserModel>();

		try {
			FileInputStream file = new FileInputStream(new File(warehousePathToFile));

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);

			int rownum = 1;
			int matchedRows=0;
			int totalRows = sheet.getPhysicalNumberOfRows();

			for (rownum = 1;rownum<totalRows&&matchedRows<maxOutputRows;rownum++) {
				Row row = sheet.getRow(rownum);
				Cell cell = row.getCell(getCellIndexCorrespondingToField(field));
				if(cell == null){
					continue;
				}
				String nameAtIndex = getStringValueInCell(cell).toLowerCase();
				if(nameAtIndex.indexOf(value) != -1){
					UserModel user=new UserModel();
					user.setSerialNumber(getStringValueInCell(row.getCell(0)));
					user.setUsername(getStringValueInCell(row.getCell(1)));
					user.setAddress(getStringValueInCell(row.getCell(2)));
					user.setState(getStringValueInCell(row.getCell(3)));
					user.setPinCode(getStringValueInCell(row.getCell(4)));
					user.setMobileNo(getStringValueInCell(row.getCell(5)));
					user.setModeOfDispatch(getStringValueInCell(row.getCell(6)));
					user.setReceiptNumber(getStringValueInCell(row.getCell(7)));
					user.setFrom(getStringValueInCell(row.getCell(8)));
					user.setTo(getStringValueInCell(row.getCell(9)));
					user.setYear(getStringValueInCell(row.getCell(10)));
					matchedUsers.add(user);
					for(int i=0;i<columnsToShow-1;i++) {
						matchedResults[matchedRows][i+1]=getStringValueInCell(row.getCell(i));
					}
					matchedRows++;
				}
			}
			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//return matchedResults;
		return matchedUsers;

	}
	
	/**
	 * Updates all entries for the serial number
	 * Desirable : 
	 * @param args : Takes String array of args and updates the row accordingly.
	 */
	public static void updateEntries(String[] args) {
		try {
			FileInputStream file = new FileInputStream(new File(pathToFile));

			HSSFWorkbook workbook = new HSSFWorkbook(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFSheet auditSheet = workbook.getSheetAt(1);

			Integer srNo = Integer.parseInt(args[0]);
			String srNoString = srNo.toString();
			int rownum = sheet.getPhysicalNumberOfRows();

			int totalRows = sheet.getPhysicalNumberOfRows();

			for (rownum = 1;rownum<totalRows;rownum++) {
				Row row = sheet.getRow(rownum);
				Cell cell = row.getCell(getCellIndexCorrespondingToField("Sr.No."));
				if(cell == null){
					continue;
				}
				String valueAtIndex = getStringValueInCell(cell);
				if(valueAtIndex.equalsIgnoreCase(srNoString)){
					for(int i=1;i<columnsToShow-1;i++) {
						String strValueInCell = getStringValueInCell(row.getCell(i));
						if(!strValueInCell.equalsIgnoreCase(args[i])){
							auditSheet = auditUpdate(auditSheet, args[0],getFieldCorrespondingToCellIndex(i),args[i],strValueInCell);
						}
						
						row.getCell(i).setCellValue(args[i]);
					}
				}
			}
			file.close();

			FileOutputStream outFile =new FileOutputStream(new File(pathToFile));
			workbook.write(outFile);
			outFile.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static HSSFSheet auditUpdate(HSSFSheet auditSheet, String serailNumber, String property, String newValue, String oldValue) {

			int rownum = auditSheet.getPhysicalNumberOfRows();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			String dateChanged = dateFormat.format(date);
			
			Row row = auditSheet.createRow(rownum++);
			int cellnum = 0;
			for (int i=0;i<5;i++) {
				Cell cell = row.createCell(cellnum++);
				if(i==0){
					cell.setCellValue(serailNumber);
				}else if(i==1){
					cell.setCellValue(property);
				}else if(i==2){
					cell.setCellValue(oldValue);
				}else if(i==3){
					cell.setCellValue(newValue);
				}else if(i==4){
					cell.setCellValue(dateChanged);
				}
			}

			return auditSheet;
		
	}

	public static String getStringValueInCell(Cell cell) {
		
		if(cell!=null) {
			cell.getCellType();
		}
		int type;
	    Object result;
	    type = cell.getCellType();

	    switch (type) {

	        case 0: // numeric value in Excel
	            result = cell.getNumericCellValue();
	            if(result != null) {
	            	Double dResult = (Double)result;
	            	result = dResult.intValue();
	            }
	            break;
	        case 1: // String Value in Excel 
	            result = cell.getStringCellValue();
	            break;
	        default:  
	            result="";                        
	    }
	    if(result == null){
	    	result = "";
	    }

	    return result.toString();

	}
	
	public static int getCellIndexCorrespondingToField(String field){
		
		if(field.equalsIgnoreCase("Sr.No.")) {
			return 0;
		} else if(field.equalsIgnoreCase("Name")){
			return 1;
		} else if(field.equalsIgnoreCase("Locality")){
			return 2;
		} else if(field.equalsIgnoreCase("Mobile")){
			return 5;
		} else {
			return 1;
		}

	}

	public static String getFieldCorrespondingToCellIndex(int index){
		String retStr;
		if(index==0) {
			retStr =  "Serial No.";
		} else if(index==1) {
			retStr =  "Name";
		} else if(index==2) {
			retStr =  "Address";
		} else if(index==3) {
			retStr =  "State";
		} else if(index==4) {
			retStr =  "Pin Code";
		} else if(index==5) {
			retStr =  "Mobile No.";
		} else if(index==6) {
			retStr =  "Mode of Dispatch";
		} else if(index==7) {
			retStr =  "Reciept No.";
		} else if(index==8) {
			retStr =  "From";
		} else if(index==9) {
			retStr =  "To";
		} else if(index==10) {
			retStr =  "Year";
		} else {
			retStr =  "Bug";
		}
		return retStr;
	}
}
