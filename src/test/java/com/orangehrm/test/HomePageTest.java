package com.orangehrm.test;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

@Test(groups="regression") // class level regression grouping done will help eventually 

public class HomePageTest extends BaseClass {
	

	private LoginPage loginPage; // null
	private HomePage homePage;

	@BeforeMethod
	public void setUpPages() throws IOException, InterruptedException {
		this.loginPage = new LoginPage();
		this.homePage = new HomePage(
				);
	}
	//for every test two times the instance will be created 
	//
	//tell
	  @Test
	    public void verifyHrmLogoDisplay() {

	        ExtentManager.startTest("verifyHrmLogoDisplay");

	        try {
	            ExtentManager.logStep("Logging into application");
	            loginPage.login("Admin", "admin123");

	            ExtentManager.logStep("Checking if OrangeHRM logo is displayed");
	            boolean verifyLogoDisplay = homePage.verifyOrangeHrmLogoIsDisplayed();

	            if (verifyLogoDisplay) {
	                ExtentManager.logWithScreenShot(
	                        getDriver(),
	                        "Logo is displayed successfully",
	                        "Logo Verification Passed"
	                );
	            } else {
	                ExtentManager.logFailure(
	                        getDriver(),
	                        "Logo was NOT displayed",
	                        "Logo Verification Failed"
	                );
	                Assert.fail("The Logo was expected but NOT displayed");
	            }

	            ExtentManager.logStep("Logging out of application");
	            homePage.logOut();
	            staticWait(2);

	        } catch (Exception e) {
	            ExtentManager.logFailure(getDriver(), e.getMessage(), "Unexpected Failure");
	            Assert.fail(e.getMessage());
	        } finally {
	            ExtentManager.endTest();
	        }
	    }
	
	
	


	@Test
	public void verifyValidAdminTabTest() {
		loginPage.login("Admin", "admin123");
		System.out.println("Successfully**********Login***********");
		boolean adminTabVerify = homePage.adminTabDisplayed();
		Assert.assertEquals(adminTabVerify, true, "Admin Tab should be visible after successfull Login");
		homePage.logOut();
		System.out.println("Sucessfully**********Logout**************");
		staticWait(2);
	}

}
