package com.orangehrm.listeners;




	import org.testng.ITestContext;
	import org.testng.ITestListener;
	import org.testng.ITestResult;
	import com.orangehrm.utilities.ExtentManager;
	import com.orangehrm.base.BaseClass;
	import org.openqa.selenium.WebDriver;

	public class TestListener implements ITestListener {

	    @Override
	    public void onTestStart(ITestResult result) {
	        // Create dynamic ExtentTest node for every test method
	        ExtentManager.startTest(result.getMethod().getMethodName());
	    }

	    @Override
	    public void onTestSuccess(ITestResult result) {
	        ExtentManager.getTest().pass("Test Passed Successfully");
	    }

	    @Override
	    public void onTestFailure(ITestResult result) {
	        WebDriver driver = BaseClass.getDriver();
	        
	        ExtentManager.logFailure(
	                driver,
	                "Test Failed: " + result.getThrowable().getMessage(),
	                "Failure Screenshot"
	        );
	    }

	    @Override
	    public void onTestSkipped(ITestResult result) {
	        ExtentManager.getTest().skip("Test Skipped: " + result.getThrowable());
	    }

	    @Override
	    public void onFinish(ITestContext context) {
	        ExtentManager.endTest();  // flush report
	    }
	}


