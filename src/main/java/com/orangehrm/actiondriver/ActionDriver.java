package com.orangehrm.actiondriver;

import java.io.IOException;
import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {
	// now instead of creating object all the time better to call it inside base class 
	// and thus we will have singlton design pattern 
//yhud
	private WebDriver driver;
	private WebDriverWait wait; //GLOBAL CANN BE APPLIED ANYWHERE WITHIN CLASS 
	
	public static final Logger logger =BaseClass.logger;

	public ActionDriver(WebDriver driver ) throws InterruptedException {
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait)); // i am storing in wait variable 
		// and job of construtor is to initilize instance variable 
		this.driver = driver;
		logger.info("Webdriver instance is created ");

	}

	// method to click the element like button
	public void click(By by) {
		try {
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
		
			logger.info("Element is clicked ");
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			
		logger.error("Unable to click the Element" +  e.getMessage());
		}
	}

	// Method to enter the text into inputfield

	public void enterText(By by, String value) {
		try {
			waitForElementVisbility(by);
			 driver.findElement(by).clear();
			 driver.findElement(by).sendKeys(value);
			 logger.info("Text is displayed on application " + value);
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Unable to Send Value in Application using SendKeys"  + e.getMessage());
		}
	}

	// Method to get text from element return this method and print whenevr it is
	// called

	public String getText(By by) {

		try {
			waitForElementVisbility(by);
			
			return driver.findElement(by).getText();
			
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Unable To retieve the text " + e.getMessage());
		}
		return "";
	}

// Method to comaper Two Text
	public boolean compareText(By by, String expectedText) { //Invalid Credintails
		String actualText =null;
		try {
			waitForElementVisbility(by);
			 actualText = driver.findElement(by).getText(); // xpath

			if (expectedText.equals(actualText)) {
				
				logger.info("Text are Matching:" + actualText + "equals " + expectedText);
				return true;
				
			} else {
				
				logger.error("Mismatch between:" + actualText + "and" + expectedText);
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			System.out.println("Text MisMatch:" + e.getMessage());
			
		}
		return false;
		
	}

	// Method to check if an element is displayed
	public Boolean isDisplayed(By by) {

		try {
			waitForElementVisbility(by);
			Boolean displayActualELement = driver.findElement(by).isDisplayed();
			if ((displayActualELement)) {
				// same method will return true if element is Displayed
				
				logger.info("Elements are Displayed  ");
				return displayActualELement;

			} else {
				// same method will return false if element not displayed
				return displayActualELement;
			}
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Element is Not Visible" + e.getMessage());
			return false;
		}

	}

//Method to check is element is selectod or not 	

	public Boolean isSelected(By by) {

		try {
			waitForElementVisbility(by);
			Boolean selectActualELement = driver.findElement(by).isSelected();
			if ((selectActualELement)) {
				// same method will return true if element is Displayed
				logger.info("Elements are Selected  ");
				return selectActualELement;

			} else {
				// same method will return false if element not displayed
				return selectActualELement;
			}
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Element is Not Selected" + e.getMessage());
			return false;
		}

	}

//Method to check the element is enable or not 
	public Boolean isEnabled(By by) {

		try {
			waitForElementVisbility(by);
			Boolean enableActualELement = driver.findElement(by).isEnabled();
			if ((enableActualELement)) {
				// same method will return true if element is Displayed
				logger.info("Element is enabled  ");
				return enableActualELement;

			} else {
				// same method will return false if element not displayed
				return enableActualELement;
			}
		} catch (Exception e) {
			logger.error(e.getClass().toString());
		logger.error("Element is Not Enabled" + e.getMessage());
			return false;
		}

	}

// Scroll to element using javasscript executor
	public void scrollToElement(By by) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement jsElement = driver.findElement(by);
			js.executeScript("arguments[0] ,scrollIntoView(true);", jsElement);
			driver.findElement(by);
		} catch (Exception e) {
		
			logger.error("Unable to Scroll Into Desired Element: " + e.getMessage());
		}
	}
	//==============================================EXPLICIT WAIT STARTS ====================================================
// handle stale element refrence exception 
	private void handleStaleElementRefrenceException(By by) {
		 try {
			wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(by)));
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Stale element refrence " + e.getMessage());
		}
	}
	
//===================================================================================
	
	
// wait for element to be clickable using explicit wait 
	private void waitForElementToBeClickable(By by) {
		try {

			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error(e.getClass().toString());
			logger.error("Element is i not clickable" + e.getMessage());
		}
	}

// wait for  frame and switch to it
	public void waitForFrameAndSwitch(By by) {
		try {

			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
		} catch (Exception e) {
		
			logger.error("Unable to switch The frame" + e.getMessage());
		}
	}

	// Wait for Single Web element to be visibile
	private void waitForElementVisbility(By by) {
		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(by));

		} catch (Exception e) {
			
			logger.error("element is Not Visible" + e.getMessage());
		}

	}

	// wait for list of ALL web elements
	public void waitForListElementsVisbility(By by) {
		try {

			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
		} catch (Exception e) {
		
			logger.error("Elements are not visible " + e.getMessage());
		}
	}

	// check alert is present or not
	public Alert isAlertPresent() throws IOException {

		Alert alert = null;
		try {
			// if alert is present then my driver switches to alert no need to explcitly use
			// driver.switchto.alert
			alert = wait.until(ExpectedConditions.alertIsPresent());
			

		} catch (Exception e) {
			logger.error("Alert is not present"+ e.getMessage());
		}
		return alert;
	}
	
	

}
