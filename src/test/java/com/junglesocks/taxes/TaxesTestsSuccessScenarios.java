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
public class TaxesTestsSuccessScenarios {
	static Logger logger = LoggerFactory.getLogger(TaxesTestsSuccessScenarios.class);
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

	public TaxesTestsSuccessScenarios(Map<String, String> rowData) {
		this.rowData = rowData;
	}

	/*
	 * invoke load excel file method from helper class. and feed the data as
	 * parameters to test.
	 * 
	 * @return Iterable<Object[]>
	 */
	@Parameters
	public static Iterable<Object[]> testData() throws Exception {
		
		logger.debug("Loading Properties");

		try {
			prop = helper.loadProperties();
		} catch (IOException e) {

			e.printStackTrace();
			throw e;
		}
		
		logger.debug("Loading Excel file");

		try {
			String dataFile = prop.getProperty("taxes_success_scenarios_datafile");
			excelData = helper.loadExcelData(dataFile);
			logger.debug("excel data" + excelData);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
	public void testStateTaxSuccess() throws Exception {

		// get a row data (map entry) from parameters.
		Iterator<Entry<String, String>> rowIterator = rowData.entrySet().iterator();
		Double stateTaxValue = helper.submitFirstPageValues(rowIterator);
		assertNotNull("state tax value cannot be null", stateTaxValue);
		
		String subTotalStr = helper.findElement("checkout.subtotal.amount").getText();
		assertNotNull ("subtotal value cannot be null", subTotalStr);
		Double subTotal = helper.covertStringToDouble(subTotalStr);

		// calculate expected tax and total values
		
		Double expectedTaxes = helper.calculateTaxes(subTotal, stateTaxValue);
		Double expectedTotal = subTotal + expectedTaxes;

		expectedTotal = BigDecimal.valueOf(expectedTotal).setScale(3, RoundingMode.HALF_UP).doubleValue();

		// get actual values from the page
		String taxesStr = helper.findElement("checkout.taxes.amount").getText();
		assertNotNull("tax value on page cannot be null", taxesStr);
		Double actualTaxes = helper.covertStringToDouble(taxesStr);

		String totalStr = helper.findElement("checkout.total.amount").getText();
		assertNotNull ("total value on page cannot be null", totalStr);
		Double actualTotal = helper.covertStringToDouble(totalStr);

		logger.debug("Expected Tax = " + expectedTaxes + "- Actual Tax = " + actualTaxes);
		logger.debug("Expected Total = " + expectedTotal + "- Actual Total = " + actualTotal);

		assertEquals(expectedTaxes, actualTaxes);
		assertEquals(expectedTotal, actualTotal);

		logger.debug("Test Complete");
		logger.debug("-------------------------------------------------");


	}

	@After
	public void cleanUp() {
		logger.debug("Closing Browser");
		driver.close();
	}

}
