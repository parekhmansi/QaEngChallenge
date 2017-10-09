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

@RunWith(Parameterized.class)
public class TaxesTests {
	WebDriver driver;
	
	Map<String, String> rowData;
	
	static Helper helper = new Helper();
	static Properties prop;
	static List<Map<String, String>> excelData = null;

	@BeforeClass
	public static void setUp() {
		System.out.println("inside");
		// invoke load properties method from helper

		try {
			prop = helper.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Before
	public void launchBrowser() {

		driver = helper.createDriver();
		System.out.println("url = " + prop.getProperty("url"));
		driver.navigate().to(prop.getProperty("url"));
	}
	
	public TaxesTests(Map<String, String> rowData){
		
		this.rowData = rowData;
	}

	@Parameters
	public static Iterable<Object[]> testData(){
		
		// invoke Load excel file method from helper
		try {
			excelData = helper.loadExcelData();
			System.out.println("excel data" + excelData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<Object[]> rowObjList = new ArrayList<Object[]>();
		
		for ( int i=0; i< excelData.size(); i++){
			Object[] temp = new Object[1];
			temp[0] = excelData.get(i);
			rowObjList.add(temp);
		}
		
		
		return rowObjList;
		
	}
	
	
	
	@Test
	public void testStateTax() throws Exception {


			Iterator<Entry<String, String>> rowIterator = rowData.entrySet().iterator();
			Double stateTaxValue = null;
			while (rowIterator.hasNext()) {

				Entry<String, String> entry = (Entry<String, String>) rowIterator.next();

				if (entry.getKey().equalsIgnoreCase("state.tax.value")) {
					stateTaxValue = new Double(entry.getValue());
				}

				if (entry.getKey().startsWith("webelement")) {

					WebElement element = helper.findElement(entry.getKey());
					System.out.println("element type =" + element.getAttribute("type"));
					if ("text".equalsIgnoreCase(element.getAttribute("type"))) {
						element.sendKeys(entry.getValue());
					} else if ("select-one".equalsIgnoreCase(element.getAttribute("type"))) {
						Select selectElement = new Select(element);
						selectElement.selectByValue(entry.getValue());
					}

				}

			}

			helper.findElement("webelement.welcome.checkout.button").click();

			String subTotalStr = helper.findElement("checkout.subtotal.amount").getText();
			Double subTotal = helper.covertStringToDouble(subTotalStr);

			// calculate expected tax and total values
			Double expectedTax = helper.calculateTaxes(subTotal, stateTaxValue);
			Double expectedTotal = subTotal + expectedTax;

			expectedTotal = BigDecimal.valueOf(expectedTotal).setScale(3, RoundingMode.HALF_UP).doubleValue();

			// get actual values from the page
			String taxesStr = helper.findElement("checkout.taxes.amount").getText();
			Double actualTaxes = helper.covertStringToDouble(taxesStr);

			String totalStr = helper.findElement("checkout.total.amount").getText();
			Double actualTotal = helper.covertStringToDouble(totalStr);

			assertEquals(expectedTax, actualTaxes);
			assertEquals(expectedTotal, actualTotal);
			
			//driver.navigate().back();
			System.out.println("complete test");

		

		// helper.findElement("welcome.zebra.quantity").sendKeys("1");
		// Select state = new
		// Select(helper.findElement("welcome.state.select"));
		// state.selectByIndex(5);

		// driver.findElement(By.xpath("/html/body/form/input")).click();

}
	


	@After
	public void cleanUp() {
		System.out.println("inside cleanUp");
		driver.close();
	}

}
