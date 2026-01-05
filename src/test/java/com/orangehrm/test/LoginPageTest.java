package com.orangehrm.test;

import static org.testng.Assert.assertTrue;


import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;



@Test(groups="regression")
public class LoginPageTest extends BaseClass {

	private LoginPage loginPage;
	

	@BeforeMethod
	public void setUpPages() throws IOException, InterruptedException {
		loginPage = new LoginPage();
		}

	@Test
	public void verifyInvalidLoginTest() {
		loginPage.login("adm", "123");
		String expectedErrorMessage = "Invalid credentials";
		assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage));

	}

	@Test
	public void displayErrorMessage() {
		loginPage.login("Adm", "admin123");
		String errorMessage = loginPage.getErrorMessageText();
		System.out.println(errorMessage);
		staticWait(2); 

	}

}
