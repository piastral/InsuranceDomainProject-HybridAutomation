package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

/* WHY WE SHOULD USE STATIC WEBDRIVER INSTANCE
 * ==============================================
 * Using a static WebDriver in a parallel testing environment is a bad idea 
 * because it introduces shared mutable state, making the system prone to concurrency issues
 *  like race conditions and unpredictable test failures. 
 *  To avoid these problems, each test thread should have its own WebDriver instance,
 *   which can be achieved through mechanisms like ThreadLocal or dependency injection
 * 
 * ==================================================================================
 * Question Interview
 * Can we call static method using interface inside a different class Do we need to implement
 * method??
 * Ans==>> No implementation required:
 * Because static methods are tied to the interface and not to instances, 
  you do not need to implement the interface or override the static method in the class to call it. 
  In fact, static methods in interfaces cannot be overridden by implementing classes.
 * ==============================================================================================
 * ThreadLocal For ParallelTesting (TestNg +ThreadLocal)
 * Def :==> 🧩 Step-by-step logic (and you nailed all of it)
	1.	Each thread (test class) runs independently
→ In parallel execution, TestNG creates a separate thread for each class or test depending on your configuration.
	2.	ThreadLocal gives each thread its own “drawer”
→ Perfect analogy — each thread has its own little box to keep data (like its WebDriver instance).
→ No one else can see or touch what’s in another thread’s box.
	3.	When you call .set()
→ You’re storing the WebDriver instance into that thread’s personal drawer.
→ So Class A’s thread puts its ChromeDriver inside A’s box.
	4.	When you call .get()
→ The same thread later retrieves its own driver instance from its box.
→ It doesn’t get confused with Class B or C’s driver.
	5.	All classes (A, B, C, D, E)
→ Run simultaneously, but each has a different memory address for its driver —
so they don’t overlap, they don’t interfere, and each controls its own browser window.
	6.	Result
→ Everyone runs parallelly, isolated, independent, safe, and super efficient. 🚀

⸻

🧠 In simpler English
	•	ThreadLocal = private storage area for each running thread.
	•	.set() = “keep this value in my box.”
	•	.get() = “retrieve my own value from my box.”
	•	.remove() = “clean my box after I’m done.”
 * ==============================NOW WHY WE NEED STATIC IN THREADLOCAL==========================================
 *S o, when we run our tests in parallel using TestNG, each test actually runs in its own thread. 
 *Now, if all those threads share the same WebDriver instance, they’ll start interfering with each other — 
 *one test might click something while another test is loading a page. That’s where ThreadLocal comes in.
You can think of ThreadLocal like a big cabinet that has multiple drawers — one drawer for each thread.
 Each thread puts its own WebDriver inside its own drawer using the .set() method. 
 
 Later, when that same thread wants to use its driver, it just opens its own drawer using .get(). 
 So, no other thread can touch or even see that driver instance.
 
Now, we make the ThreadLocal variable static because we want only one cabinet to be shared across the entire framework. 
Every test class can access that same cabinet, but each thread still works with its own drawer inside it.
 If it wasn’t static, every class would create its own separate cabinet,
  
 and the whole point of managing thread-specific drivers would break.
In short, ThreadLocal helps each test thread keep its own copy of WebDriver,

 and making it static ensures that all tests share the same ThreadLocal container —
  it’s like having one shared cabinet with separate drawers for every test thread.
   That’s how we achieve proper parallel execution without tests overlapping each other.
 * 
 * 
 * 
 * THREAD-LOCAL DOESNT EXECUTE AS PARRALE TESTING THAT IS THE JOB OF TESTNG XML WHEN RUNNING THE THREADS AS PARALLEL
 * THE JOB OF THREADLOCAL IS ENSURING THAT EACH WEBDRIVER GETS ITS RESPECTIVE THREAD 
 * ITS ACTS AS POLICEMAN
 * 
 * 
 */

public class BaseClass {

	protected static Properties prop;
	// protected static  WebDriver driver;
	protected  FileInputStream file;

	protected  ChromeOptions chromeOption; // static variable will be shared across all OBJECT
	protected  FirefoxOptions firefoxOption;
	protected  EdgeOptions edgeOption;
	//private static ActionDriver actionDriver;
	
	private static ThreadLocal<WebDriver>driver=   new ThreadLocal<>(); // parallel testiing
	private static ThreadLocal<ActionDriver> actionDriver=   new ThreadLocal<>(); // this should also run in 
	
	
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	/*
	 * LOAD THE CONFIG FILE Reading The Config File From Config.Prop Load the prop
	 * file
	 */
//========================================LOAD CONFIG================================================================

	@BeforeSuite
	public  void loadConfig() throws IOException {
		prop = new Properties();
		file = new FileInputStream("src/main/resources/config.properties");
		prop.load(file);
	
		logger.info("Config.properties file loaded ");
		
		//Start the extent report 
		
		ExtentManager.getReporter();
	}
//==============================================END LOAD CONFIG========================================================================================	

//==================================BEFORE METHOD SETUP ==========================================
	@BeforeMethod
	public synchronized void setUp() throws IOException, IllegalAccessException, InterruptedException {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowserUsingImplcitWait();
		staticWait(2);
		
		
		//Initilize action driver for the current thread also tied up with webdriver 
		actionDriver.set(new ActionDriver(getDriver())); // here i am creating object of actionDriver class
		logger.info("ActionDriver Initlized for thread" + Thread.currentThread().getId());
		
		
		
		
		//Initilaize the actinDriver only once And make it static so that object wont be created everytime
		
		logger.info("WebDriver Intialized and Browser Maximized");
		logger.trace("This is Trace message");
		logger.error("This is error message");
		logger.debug("This is Debug message");
		logger.fatal("This is Fatal message");
		logger.warn("This is Warn message");
		
		
	}

	/*
	 * private static ActionDriver createActionDriver() throws InterruptedException
	 * { if(actionDriver == null) actionDriver= new ActionDriver(driver);
	 * logger.info("Action Driver Instance is Created::");
	 * 
	 * return actionDriver; }
	 */
	
	
	
	
	
	
//========================END BEFORE METHOD SETUP==============================================================	

//==================================LAUNCH RBOWSER ======================================================================          
// why beforemethod because i want to run my setup before every test case

	private synchronized  void launchBrowser() throws IllegalAccessException {

// to debug press twice to break point in line 62 also for Properties we need to use static 
		String browserOptions = prop.getProperty("browserOptions");

		if (browserOptions.equalsIgnoreCase("chrome")) {

			chromeOption = new ChromeOptions();
			chromeOption.addArguments("--start-maximized","--incognito");
			
			//Create webdriver instance and put into threadlocal
			driver.set(new ChromeDriver(chromeOption));
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver & ChromeOption Instance is created");
			
			

		} else if (browserOptions.equalsIgnoreCase("firefox")) {
              firefoxOption = new FirefoxOptions();
              firefoxOption.addArguments("--start-maximized ","--incognito");
              
              driver.set(new FirefoxDriver(firefoxOption));
              ExtentManager.registerDriver(getDriver());
  			logger.info("FirefoxDriver & FireFox Instance is created");
              
			
		}

		else if (browserOptions.equalsIgnoreCase("edge")) {
			edgeOption = new EdgeOptions();
			  edgeOption.addArguments("--start-maximized ","--incognito");
            
            driver.set(new EdgeDriver(edgeOption));
            ExtentManager.registerDriver(getDriver());
			logger.info("EdgeDriver & EdgeOptions Instance is created");
			
			

			
		} else {
			throw new IllegalAccessException("Not valid options");
		}
		
		getDriver().get(prop.getProperty("url"));

	}

	public void configureBrowserUsingImplcitWait() {
		int implictWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implictWait));

		

	}

//==============================================END THE LAUNCH BROWSER METHOD==================================================	

	// Intialize the webdriver based on brwoser defined on config.properties file

	// In runtime i can provide in any value in pro file based on that my test Will
	// run on respectiver browser

	/*
	 * Implicit Wait convert into integer from properties cause inside properties
	 * file we have string browser and navigate to the url Open the chromeOption
	 * Using chromeOption,Firefox Option i can configure behaviour of browser Can
	 * help to maximize my browserwindow , run headless run incognito ...etc Open
	 * the implicit wait
	 */
//=========================================================BROWSER WITH IMPLICIT WAIT AND URL OPEN ===================================	

//=============================================END BROWSER CONFIG ===================================================================	

//===================================================Explicit wait====================================================================	

//===================================================End OF EXPLICT AIT METHOD============================================================	

//================================================================= TEARDOWN =================================================	
	@AfterMethod
	public synchronized  void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e)  {
				System.out.println("Unable to quit the driver:" + e.getMessage());
			
		
		//after completing the case inteqar down we are making actiondriver null 
	 } finally {
         driver.remove();  // Clean up ThreadLocal drawer
         actionDriver.remove();
         ExtentManager.endTest(); // will flush the report after each test class we will flush the report 
   
	} 
		}
	}
	
	
	//getter method for prop
	public static Properties getProp()
	{
		return prop;
		
	}
	

				//using getter and setter with driver instance so that we can modify 
	//important GetterEncapsulation
	public static WebDriver getDriver () 
	{
		if(driver.get()==null)
		{
			System.out.println("Webdriver is not iniltized");
			throw new IllegalStateException("Webdriver is not iniltized");
		}
		return driver.get();
	}
	

	
	public static ActionDriver getActionDriver()
	{
		if(actionDriver.get()==null) // if my actionDriver becoms null else create new actionDriver
		{
			System.out.println("ActionDriver is not Iniltilized");
			throw new IllegalStateException("ActionDriver is not Iniltilized");
		}
		return actionDriver.get();

	}
	
	
	
	
	
	
	
	
	/*
	 * public void setDriver(WebDriver driver) { this.driver=driver; }
	 */
	
	

	// we are introducing static wait instead of Thread.sleep o that we cans ee our
	// script is running properly
	
	public void staticWait(int seconds) {
		
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	
//=====================================================================END===============================================================
}
//====================================================================END OF BASE CLASS==============================================