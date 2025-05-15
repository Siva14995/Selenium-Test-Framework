package com.orangehrm.utilities;

import java.util.List;

import org.testng.annotations.DataProvider;

public class DataProviders {

	private static final String FILE_PATH = System.getProperty("user.dir")+ "/src/test/resources/TestData/TestData.xlsx";
	
	@DataProvider(name = "validLoginData")
	public static Object[][] validLoginData() {
		return getSheetData("validLoginData");
	}

	@DataProvider(name = "invalidLoginData")
	public static Object[][] inValidLoginData() {
		return getSheetData("invalidLoginData");
	}
	
	@DataProvider(name = "emplVerification")
	public static Object[][] emplVerification() {
		return getSheetData("emplVerification");
	}

	private static Object[][] getSheetData(String sheetName) {
		List<String[]> sheetData = ExcelReaderUtility.getSheetData(FILE_PATH, sheetName);
		
		if(sheetData==null||sheetData.isEmpty()) {
			throw new RuntimeException("No data found in sheet: "+sheetName);
		}

		Object[][] data = new Object[sheetData.size()][sheetData.get(0).length];

		for (int i = 0; i < sheetData.size(); i++) {
			data[i] = sheetData.get(i);
		}
		return data;
	}

}
