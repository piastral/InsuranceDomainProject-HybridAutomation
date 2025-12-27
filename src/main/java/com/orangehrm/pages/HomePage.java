package com.orangehrm.pages;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage {
	
private ActionDriver actionDriver; //instance variable of actionDriver class
	
	// Define all the locators using By class
	
	private By adminTabLocator = By.xpath("//div[contains(@class,'oxd-sidepanel-body')]//ul//li//a//span[text()='Admin']");
	private By clickIdButtonLocator = By.xpath("//i[contains(@class,'oxd-icon bi-caret-down-fill oxd-userdropdown-icon')]") ;
	private By loginOutButtonLocator = By.xpath("//a[text()='Logout']");	
	private By orangeHrmLogoLocator = By.xpath("//div[@class='oxd-brand-banner']");
	
	
	/*
	 * public HomePage(WebDriver driver) throws IOException, InterruptedException {
	 * this.actionDriver = new ActionDriver(driver); }
	 */
	//// we have created singlton patterd for action driver and webdriver 
	public HomePage( )
	{
		this.actionDriver=BaseClass.getActionDriver();
	}
	
	
	
	
	
	//mETHOD TO VERIFY ADMIN TAB IS DISPLAYED OR NOT 
	
	public boolean adminTabDisplayed()
	{
	return	actionDriver.isDisplayed(adminTabLocator);
	}
	
	//Verify The Orange Hrm Logo IS displayed Properly Or NOT 
	public boolean verifyOrangeHrmLogoIsDisplayed ()
	{
		return actionDriver.isDisplayed(orangeHrmLogoLocator);
	}
	
	public void logOut()
	{
		actionDriver.click(clickIdButtonLocator);
		actionDriver.click(loginOutButtonLocator);
	}
	
	
	

}
