package com.junglesocks.taxes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
	TaxesTestsSuccessScenarios.class, 
	TaxesTestsFailScenarios.class

})

public class TaxesTestSuite {

}
