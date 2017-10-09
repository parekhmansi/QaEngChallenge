# QaEngChallenge
Project Name: QaEngChallenge

Regression Test Cases for State Tax Feature.


-----------------
OVERVIEW
-----------------

This test source code runs the test for jungle socks application. 
It mainly covers the test for newly added feature to the application which adds the state tax to 
customers total amount and verifies that application is calculating correct state tax.

It is setup as a parameterized junit test with a test suite class.
Test case iterates over the test data (each row is one test) from the test data excel file. 
------------------
SOURCE CODE
------------------
Source testcode location: src/test/java --> com.junglesocks.taxes
File Names:1. Helper.java - Has methods to load property file, load and read excel file, 
                            calculate expected taxes and total cost. 
           2. TaxesTest.java - Has a testcase to verify state tax is always correctly applied.
           3. TaxesTestSuite.java - Has a test suite class which runs the TaxesTest.java class.

-------------------
TEST DATA
-------------------
Test data file name: testdata.xlsx
Test data file location: src --> test--> resources
testdata.xlsx file includes: 1. Different numeric quantities for each category of socks.(eg. 3 , 12, 4 etc)
                             2. Name of different state (eg. CA, MN, NY etc)
                             3. Valid state texes for each state (eg. 0.08, 0.05 etc)


-------------------
PROPERTY FILE
-------------------
Property file name: locator.properties
Property file location: src --> test--> resources
locator.properties file includes: 1. Xpaths for all the webelements
                                  2. URL of application in test
                                  


--------------------
BUILD INSTRUCTION
--------------------

1. For chrome driver path - code first checks if there is any System property set for webdriver.chrome.driver
   If not, it tries to get the property from locator.properties file.

This is a maven project with test suite class. You can run the test using below command on command line:

                       mvn clean test -Dtest=TaxesTestSuite.java

It will run one test scenario with 3 different test data by selecting different state with different taxes



                                   
                                
                                   
                                   
                                   
                                    


