package com.junglesocks.taxes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs test for different state tax calculation on jungle socks application.
 * This is a regression test case for state tax calculation.
 * 
 * @author mansi parekh
 *
 *
 */
@RunWith(Parameterized.class)
public class TaxesTestsFailScenarios {
	static Logger logger = LoggerFactory.getLogger(TaxesTestsFailScenarios.class);
	WebDriver driver;

	Map<String, String> rowData;

	static Helper helper = new Helper();
	static Properties prop;
	static List<Map<String, String>> excelData = null;

	/*
	 * invoke load properties method from helper class
	 */
	@BeforeClass
	public static void setUp() {
		
	}

	/*
	 * launch a new browser for every test
	 */
	@Before
	public void launchBrowser() {
		logger.debug("Launching Browser with URL = " + prop.getProperty("url"));
		driver = helper.createDriver();
		driver.navigate().to(prop.getProperty("url"));
	}

	public TaxesTestsFailScenarios(Map<String, String> rowData) {
		this.rowData = rowData;
	}

	/*
	 * invoke load excel file method from helper class. and feed the data as
	 * parameters to test.
	 * 
	 * @return Iterable<Object[]>
	 */
	@Parameters
	public static Iterable<Object[]> testData() {
		
		logger.debug("Loading Properties");

		try {
			prop = helper.loadProperties();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		logger.debug("Loading Excel file");

		try {
			String dataFile = prop.getProperty("taxes_fail_scenarios_datafile");
			excelData = helper.loadExcelData(dataFile);
			logger.debug("excel data" + excelData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// converting excel data from List to List of Arrays
		// to make it compatible for @Parameters
		ArrayList<Object[]> rowObjList = new ArrayList<Object[]>();

		for (int i = 0; i < excelData.size(); i++) {
			Object[] temp = new Object[1];
			temp[0] = excelData.get(i);
			rowObjList.add(temp);
		}

		return rowObjList;

	}

	/*
	 * Tests different state tax calculations. Gets the data from @Parameters
	 */
	@Test
	public void testStateTaxFailure() throws Exception {

		// get a row data (map entry) from parameters.
		Iterator<Entry<String, String>> rowIterator = rowData.entrySet().iterator();
		Double stateTaxValue = helper.submitFirstPageValues(rowIterator);
		
		String errorMsg = helper.findElement("errorpage.errormsg").getText();
		assertNotNull(errorMsg);

		logger.debug("Test Complete");
		logger.debug("-------------------------------------------------");


	}

	@After
	public void cleanUp() {
		logger.debug("Closing Browser");
		driver.close();
	}

}
