package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager{
	

	
	


/* Extent report implementation in the framework 
 * Benefits of using Extent Report 
 * 1-> Extent report is popular reporting library in selenium 
 * it provides interactive and visually appeling reports for automation test cases :
 * Hoghlights 
 * Detailes testSteps And Logs 
 * Screenshot for test Validation  -->Base64 
 * We will make multithreaed for parallel execution 
 * 
 * User friendly ui --> easy to unsderstand for stakeholders 
 * customisable reports add to own branding thems and structure 
 * Screenshot intergation : Attach Screenshot for pass fails scenarios 
 * Support for MultiThreading : HELPS IN parallel testing 
 * Easy intergaration compatible with maven 
 * 
 * How extent reports works ::
 * Initilizr report ;configure theme ,name  etc 
 * start and end test : track individual test cases 
 * Logs :logs ,info pass,fail,skip with clear messages with extent report 
 * Screenshot : better
 * 
 * How to implement :
 * 1-depedencies.extentreport and commons-Io
 * 2-reate extent manager class - logic for extent report /take attach screenshot in BAser64 format 
 * 3- DEFINE methods t initiize report and manage test threads 
 * 4-change in baseclass,actionDriver and testClasses .
 * 
 * 
 * 
 * 	
 */
	
	/*How internally extent report is working now extent report works as central manager where it takes all the test details from extent test like log ,scrrenshot 
	 * and then in return extentreport send the test result to reporter(spark reporter) and tell whatever outline you want to provide you can do that 
	 * for example if you wantyour report to be dark theeme so this works are the work of reporter to print 
	 * 1-> where we are using map in extent report 
	 * 2-> it tells where my path should be stored
	 * 
	 * 
	 */
	//======================================================================= extent report start
	
	
/*1-> extent report acts as engine whereas extent test a componenet of extent report works inside each test method and  give the result as pass,fail,skip,or screenshot to be 
 * taken ,then extent test gives back all the result to extent report then we attach the spark reporter to extent report the job of spark reporter is to tell whhere my extent 
 * report will be saved and what theme we will use lets say dark theme we might use or which operating system we will configure or spo the jo of spark reporter 
 * is to print in html report 
 * 
 * lets configure the extent report 2 dependecy -> 1 extent report ,2 -> commons.io(Apache)
 * 
 */
	
	private static ExtentReports extentReports; //1st step 
	  private static ThreadLocal<ExtentTest>  extentTest= new ThreadLocal<ExtentTest>(); //for parallel execution  
	  private static Map<Long,WebDriver> driverMap = new HashMap<>();
	  
	    //initilize extentReport 
	  /*what is the purpose of getReporter method 
	   * ans :it is singlton factory method which ensures only on extent eport object is created in the entire framework 
	   * that object is available to everone (listenrs,the testMethods ,extentManagers)
	   * “each test method should reuse the same engine…
otherwise new engine = overwrite…
same like thread local ensures same thread uses same driver.”
	   * 
	   * 
	   * 
	   */
	        public synchronized static ExtentReports getReporter() {
	        	if(extentReports==null) {
	        	String 	reportPath= System.getProperty("user.dir") + "\\src\\test\\resources\\ExtentReport\\ExtentReport.html";
	        	//now i need to attach the spark reporter with my extent report but before that i need to say to my spark reported where the files will be stored 
	        	ExtentSparkReporter sparkReporter= new ExtentSparkReporter(reportPath);
	        	   sparkReporter.config().setReportName("Automation Test Report");
	        	   sparkReporter.config().setTheme(Theme.DARK);
	        	   sparkReporter.config().setDocumentTitle("OrangeHrm Report");
	        	   sparkReporter.config().setTimelineEnabled(true);
	        	   
	        	   
	        	   extentReports = new ExtentReports();
	        	   
	        	   //ATTACH THE SPARK REPORTER (mANDATORY)
	        	   extentReports.attachReporter(sparkReporter);
	        	   extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
	        	   extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
	        	   extentReports.setSystemInfo("User Name ", System.getProperty("user.name"));
	        	   
	        	   
	        	 
	        	  
	        	}
				return extentReports;
	        }
	        
	        //start the test who will start the test Extent Test  if no listeers manuall provide this to each test method
	        
	        public synchronized static ExtentTest startTest(String testName) {
	       ExtentTest extentCreateTest= 	getReporter().createTest(testName);
	       extentTest.set(extentCreateTest);
	       return extentCreateTest;
	       
	     }
	        
	        
	        
	        //End test 
	      public  synchronized static void endTest () {
	    		getReporter().flush();
		      
	      }
	      
	      //getCurrent Thread TestName
	      
	      public synchronized static ExtentTest getTest() {
	    	return  extentTest.get();
	      }
	      
	      /*why do even need to getTest and getTestName()
	       * -->when one report is generated at the end anyway 
	       * because during execution ExtentManager must always know which test method is runnig right now on this thread so it can 
	       * 1-Attach logs to the correct test 
	       * 2-Attach screenshot to the correct test 
	       * 3-aVOID CROSS thread contamination 
	       * 4- Work correctly with parallel execution 
	       * 5-Allow code inside ActionDriver Listeners or Page Objects to log steps without [assing ExtentTest manually 
	       * because during exection we need to log steps not just final result 
	       * 
	       * ExtentManager.getTest.info("clicked Loginbutton")
	       * 
	       * getTest() returns the active ExtentTest object for the current thread using ThreadLocal. 
	       * This allows each test method to log steps, failures,
	       *  and screenshots into the correct test node — especially during parallel execution.
	       *   Without getTest(), logs and screenshots would mix across tests,
	       *    listeners wouldn’t know which test is running, and reporting would break. 
	       * getTestName() is used to label screenshots and logs correctly per test.
	       * 
	       * 
	       * 
	       */
	      
	      
	      // actual method name of the current test
	      
	      public static String  getTestName() {
	    	 ExtentTest currentTest = getTest();
	    	 if(currentTest!=null) {
	    		return  currentTest.getModel().getName();
	    	 } else {
	    		 return "no Test is currently active for this thread";
	    	 }
	      }
	      
	      //now we will write to log this step 
	       public static void logStep(String logMessage) {
	    	  getTest().info("logMessage");
	      }
	      
	       //Log step validation with screenshot
	       
	       public static void logWithScreenShot(WebDriver driver  ,String logMessage ,String screenSHotMessage) {
		    	  getTest().pass("logMessage");
		    	  attachScreenShot(driver, screenSHotMessage);
		    	  
		      }
	        
	       
	       public static void logFailure(WebDriver driver  ,String logMessage ,String screenSHotMessage) {
		    	  getTest().fail("logMessage");
		    	  //screenshot method 
		    	  attachScreenShot(driver, screenSHotMessage);
		      }
	       
	       
	       
	       public static void logSkip(String logMessage ) {
		    	  getTest().skip("logMessage");
		    	
		    	 
		    	  
		      }
	        
	       
	       public synchronized  static String takeScreenShot(WebDriver driver   ,String screenShotName) {
		    	  TakesScreenshot takeScreenShot =(TakesScreenshot) driver;
		    	File src =  takeScreenShot.getScreenshotAs(OutputType.FILE);
		    	//format dat and time 
		    	
		           String timeStamp =  	new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		           
		             // savin g the screen shot to the file 
		           
		           String destinationPath = System.getProperty("user.dir") + "\\src\\test\\resources\\ExtentReport\\screnShots " +screenShotName+"_"+ timeStamp+".png";
		           File finalPath =   new File  (destinationPath);
			        try {
						FileUtils.copyFile(src, finalPath);
					} catch (IOException e) {
					
						e.printStackTrace();
					}
			        //convert in bas64 for embedded in report
			        String  base64Format = convertToBase64(src);
			        return base64Format;
		      }
	       
	       //Convert screenshot to base64 format 
	       
	       public static String convertToBase64(File screenShotFile) {
	    	   String base64Format ="";
	    	   //read the file content into Byte array
	    	   
			try {
				
				 byte[]	fileContent = FileUtils.readFileToByteArray(screenShotFile);
				 base64Format = Base64.getEncoder().encodeToString(fileContent);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	    	   //Convert the byte array to base 64 cString 
	    	   
	    	return   base64Format;
	       }
	       
	       //attach screenshot to the report using base64
	       
	       public synchronized  static void attachScreenShot(WebDriver driver ,String message ) {
	    	   try {
				String screenShotBase64 =takeScreenShot( driver,  getTestName());
				   getTest().info(message,com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(screenShotBase64).build());
			} catch (Exception e) {
				getTest().fail("Failed to Attach the screenShot" + message);
				e.printStackTrace();
			}                                           
	       }
	       
	      
	       
	    
	        
	        
	        
	   
	        
	        //Register webdriver for curretn thread 
	        
	        public static void registerDriver(WebDriver driver) {
	        	driverMap.put(Thread.currentThread().getId(), driver);
	        }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}