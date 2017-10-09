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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {

	private static Logger logger = LoggerFactory.getLogger(Helper.class);

	Properties prop;
	WebDriver driver;

	/*
	 * loads the properties file
	 * 
	 * @return - properties
	 */
	public Properties loadProperties() throws IOException {

		prop = new Properties();

		logger.debug("Loading properties file from - src/test/resources/locator.properties");
		// enter path for excel file here.
		FileInputStream fs = new FileInputStream("src/test/resources/locator.properties");
		prop.load(fs);

		return prop;
	}

	/*
	 * find web element from the page by using xpath from property file.
	 * 
	 * @return - WebElement
	 */
	public WebElement findElement(String key) throws IOException {

		Properties prop = loadProperties();
		String xpath = prop.getProperty(key);
		logger.debug("XPATH value for :" + key + "=" + xpath);

		WebElement element = driver.findElement(By.xpath(xpath));

		return element;
	}

	/*
	 * create chrome driver object of web driver
	 */
	public WebDriver createDriver() {

		//get the property from properties file if it is not already set
		if(System.getProperty("webdriver.chrome.driver") == null){
			System.setProperty("webdriver.chrome.driver", prop.getProperty("webdriver.chrome.driver"));
		}
		driver = new ChromeDriver();

		// set implicit wait to make the test visible while run.
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;

	}

	/*
	 * replace "$" sign with empty string and convert variable from string to
	 * double
	 * 
	 * @return - Double
	 */
	public Double covertStringToDouble(String s) throws Exception {

		String total = s.replaceAll("\\$", "");
		Double number = Double.parseDouble(total);
		return number;

	}

	/*
	 * calculate taxes for expected result
	 * 
	 * @return - Double
	 */
	public Double calculateTaxes(Double amount, Double tax) throws Exception {

		logger.debug("Calculate tax for - amount -" + amount + ": tax -" + tax);

		Double taxes = (amount * tax);

		Double taxAmt = BigDecimal.valueOf(taxes).setScale(3, RoundingMode.HALF_UP).doubleValue();

		return taxAmt;
	}

	/*
	 * load test data excel file
	 * 
	 * @return - List<Map<String, String>>
	 */
	public List<Map<String, String>> loadExcelData() throws IOException {

		Workbook workbook = null;
		FileInputStream inputStream = null;
		List<Map<String, String>> excelData = null;

		try {
			logger.debug("Excel file location -  src/test/resources/testdata.xlsx");
			String excelFilePath = "src/test/resources/testdata.xlsx";

			inputStream = new FileInputStream(new File(excelFilePath));

			
			workbook = new XSSFWorkbook(inputStream);
			
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
			logger.error("Excel file loading Error-" + e);

		} finally {
			workbook.close();
			inputStream.close();
		}

		return excelData;

	}

	/*
	 * load the header line from test data excel for keys
	 */
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
