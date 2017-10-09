package com.junglesocks.taxes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Helper {
	Properties prop;
	WebDriver driver;

	public Properties loadProperties() throws IOException {

		prop = new Properties();

		FileInputStream fs = new FileInputStream(
				"/Users/kirtishparekh/Documents/workspace/Engineering Challenge/src/test/resources/locator.properties");
		prop.load(fs);

		return prop;
	}

	public WebElement findElement(String key) throws IOException {

		// get xpath from property object
		Properties prop = loadProperties();
		String xpath = prop.getProperty(key);
		System.out.println("XPATH for :" + key + "=" + xpath);

		// find webelement with driver

		WebElement element = driver.findElement(By.xpath(xpath));

		// retrun element

		return element;
	}

	public WebDriver createDriver() {

		System.setProperty("webdriver.chrome.driver", "/Users/kirtishparekh/Downloads/chromedriver 2");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		return driver;

	}

	public Double covertStringToDouble(String s) throws Exception {

		String total = s.replaceAll("\\$", "");
		Double number = Double.parseDouble(total);
		return number;

	}

	public Double calculateTaxes(Double amount, Double tax) throws Exception {

		System.out.println("amount -"+ amount + ": tax -"+ tax); 
		
		Double taxes = (amount * tax) ;

		Double taxAmt = BigDecimal.valueOf(taxes)
			    .setScale(3, RoundingMode.HALF_UP)
			    .doubleValue();

		return taxAmt;
	}

	public List<Map<String, String>> loadExcelData() throws IOException {

		Workbook workbook = null;
		FileInputStream inputStream = null;
		List<Map<String, String>> excelData = null;

		try {
			String excelFilePath = "/Users/kirtishparekh/Documents/workspace/Engineering Challenge/src/test/resources/testdata.xlsx";

			inputStream = new FileInputStream(new File(excelFilePath));

			System.out.println("inputStream=" + inputStream);

			try {
				workbook = new XSSFWorkbook(inputStream);
			} catch (Exception ee) {
				System.out.println("error-" + ee);
			}

			System.out.println(workbook);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();

			List<String> headerList = populaterHeaderList(iterator);

			excelData = new ArrayList<Map<String, String>>();

			while (iterator.hasNext()) {

				Map<String, String> rowDataMap = new HashMap<String, String>();

				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				int headerCellCount = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = null;
					if (CellType.STRING.equals(cell.getCellTypeEnum())) {
						cellValue = cell.getStringCellValue();
					} else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
						cellValue = Double.toString(cell.getNumericCellValue());
					}

					rowDataMap.put(headerList.get(headerCellCount), cellValue);
					headerCellCount++;
				}
				excelData.add(rowDataMap);

			}

		} catch (Exception e) {
			System.out.println("Error: " + e);
			e.printStackTrace();

		} finally {
			workbook.close();
			inputStream.close();
		}

		return excelData;

	}

	private List<String> populaterHeaderList(Iterator<Row> iterator) {
		List<String> headerList = new ArrayList<String>();
		if (iterator.hasNext()) {

			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String cellValue = null;
				if (CellType.STRING.equals(cell.getCellTypeEnum())) {
					cellValue = cell.getStringCellValue();
				} else if (CellType.NUMERIC.equals(cell.getCellTypeEnum())) {
					cellValue = Double.toString(cell.getNumericCellValue());
				}

				headerList.add(cellValue);
			}

		}

		return headerList;
	}

}
