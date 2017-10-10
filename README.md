# QaEngChallenge
Project Name: QaEngChallenge

Parameterized Regression Test Cases for State Tax Feature.


-----------------
OVERVIEW
-----------------

Technologies used: Java 8, Junit 4.8.1, Selenium Webdriver 2.53, Apache POI 3.17, maven 3.3.9, slf4j (with log4j).

This test source code runs the test for jungle socks application. 
It mainly covers the test for newly added feature to the application which adds the state tax to 
customers total amount and verifies that application is calculating correct state tax.

It is setup as a parameterized junit test with a test suite class.
Test case iterates over the test data (each row is one test) from the test data excel files. 


--------------------
BUILD/RUN INSTRUCTION
--------------------

1. Important: ChromeDriver is provided as part of the project resources to run the tests. 

Code first checks if there is any System property set for webdriver.chrome.driver
If not, it tries to get the property from locator.properties file (which defaults to /src/test/resources).

2. locator.properties and test data files are provided as part of the project (under /src/test/resources).

This is a maven project with test suite class. You can run the tests using below commands on command line:

              mvn clean test -Dtest=TaxesTestSuite
              mvn clean test -Dtest=TaxesTestsSuccessScenarios
              mvn clean test -Dtest=TaxesTestsFailScenarios


It will run success and /or fail test scenarios with 2 different test data files (provided through the locator.properties file).

------------------
SOURCE CODE
------------------
Source testcode location: src/test/java  (com.junglesocks.taxes)
Classes:

1. Helper.java - Has helper methods to load property file, load and read excel file, calculate expected taxes and total cost. 
2. TaxesTestSuite.java - Has a test suite class which runs the test classes
3. TaxesTestsFailScenarios.java - Takes failure scenarios data from excel file and expects error page
4. TaxesTestsSuccessScenarios.java - Takes success scenarios data from excel file and expects correct data on checkout page.

-------------------
TEST DATA
-------------------
Test data file location: src/test/resources/

1.taxes_success_scenarios_datafile.xlsx
2.taxes_fail_scenarios_datafile.xlsx


-------------------
PROPERTY FILE
-------------------

Property file location: src/test/resources/locator.properties

1. URL of application in test, chrome driver path, test data file paths etc.
2. Xpaths for all the web elements.
                                  






                                   
                                
                                   
                                   
                                   
                                    


