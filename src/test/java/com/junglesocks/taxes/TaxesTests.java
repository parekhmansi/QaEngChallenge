package com.junglesocks.taxes;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runs test for different state tax calculation on jungle socks application.
 * This is a regression test case for state tax calculation.
 * @author mansi parekh
 *
 *
 */
@RunWith(Parameterized.class)
public class TaxesTests {
	private static Logger logger = LoggerFactory.getLogger(TaxesTests.class);
	WebDriver driver;

	Map<String, String> rowData;

	static Helper helper = new Helper();
	static Properties prop;
	static List<Map<String, String>> excelData = null;

	
	/*
	 *  invoke load properties method from helper class
	 */
	@BeforeClass
	public static void setUp() {
		logger.debug("Loading Properties");
		
		

		try {
			prop = helper.loadProperties();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

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

	public TaxesTests(Map<String, String> rowData) {
		this.rowData = rowData;
	}

	/*
	 * invoke load excel file method from helper class.
	 * and feed the data as parameters to test.
	 * @return Iterable<Object[]>
	 */
	@Parameters
	public static Iterable<Object[]> testData() {
		logger.debug("Loading Excel file");
		
		try {
			excelData = helper.loadExcelData();
			logger.debug("excel data" + excelData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//converting excel data from List to List of Arrays 
		//to make it compatible for @Parameters
		ArrayList<Object[]> rowObjList = new ArrayList<Object[]>();

		for (int i = 0; i < excelData.size(); i++) {
			Object[] temp = new Object[1];
			temp[0] = excelData.get(i);
			rowObjList.add(temp);
		}

		return rowObjList;

	}

	/*
	 *  Tests different state tax calculations.
	 *  Gets the data from @Parameters
	 */
	@Test
	public void testStateTax() throws Exception {

		//get a row data (map entry) from parameters.
		Iterator<Entry<String, String>> rowIterator = rowData.entrySet().iterator();
		Double stateTaxValue = null;
		while (rowIterator.hasNext()) {

			Entry<String, String> entry = (Entry<String, String>) rowIterator.next();

			if (entry.getKey().equalsIgnoreCase("state.tax.value")) {
				stateTaxValue = new Double(entry.getValue());

			}

			//check 'webelement' prefix to find its xpath and feed into application.
			if (entry.getKey().startsWith("webelement")) {

				WebElement element = helper.findElement(entry.getKey());
				
				//check web element type and use the relevant action.
				if ("text".equalsIgnoreCase(element.getAttribute("type"))) {
					element.sendKeys(entry.getValue());
				} else if ("select-one".equalsIgnoreCase(element.getAttribute("type"))) {
					Select selectElement = new Select(element);
					selectElement.selectByValue(entry.getValue());
					logger.debug("State = " + entry.getValue());

				}

			}
			logger.debug("State tax value = " + stateTaxValue);
		}
		//click checkout button.
		helper.findElement("webelement.welcome.checkout.button").click();

		String subTotalStr = helper.findElement("checkout.subtotal.amount").getText();
		Double subTotal = helper.covertStringToDouble(subTotalStr);

		// calculate expected tax and total values
		Double expectedTaxes = helper.calculateTaxes(subTotal, stateTaxValue);
		Double expectedTotal = subTotal + expectedTaxes;

		expectedTotal = BigDecimal.valueOf(expectedTotal).setScale(3, RoundingMode.HALF_UP).doubleValue();

		// get actual values from the page
		String taxesStr = helper.findElement("checkout.taxes.amount").getText();
		Double actualTaxes = helper.covertStringToDouble(taxesStr);

		String totalStr = helper.findElement("checkout.total.amount").getText();
		Double actualTotal = helper.covertStringToDouble(totalStr);

		logger.debug("Expected Tax = " + expectedTaxes + "- Actual Tax = " + actualTaxes);
		logger.debug("Expected Total = " + expectedTotal + "- Actual Total = " + actualTotal);

		assertEquals(expectedTaxes, actualTaxes);
		assertEquals(expectedTotal, actualTotal);

		logger.debug("Test Complete");

	}

	@After
	public void cleanUp() {
		logger.debug("Closing Browser");
		driver.close();
	}

}
