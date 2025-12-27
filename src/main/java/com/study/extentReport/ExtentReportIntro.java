package com.study.extentReport;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportIntro {
	public static void main(String[] args) throws IOException {
          ExtentReports extentReport = new ExtentReports();
          File file = new File("report.html");
          ExtentSparkReporter sparkReporter = new ExtentSparkReporter(file);
          extentReport.attachReporter(sparkReporter);//attach this sparkReprter to engine which is Extent Report
          
          
      ExtentTest extentTest=    extentReport.createTest("test 1"); //WE WILL JUST GENERATE TEST1  By default we dont mention and skipp or fail i t will pass also 
      extentTest.pass("This is Pass");
      
      ExtentTest extentTest2=    extentReport.createTest("test 1"); //WE WILL JUST GENERATE TEST1  By default we dont mention and skipp or fail i t will pass also 
      extentTest2.log(Status.SKIP,"tHIS IS SKIPPED");
      
      
      ExtentTest extentTest3=    extentReport.createTest("test 1"); //WE WILL JUST GENERATE TEST1  By default we dont mention and skipp or fail i t will pass also 
      extentTest3.log(Status.FAIL,"This is failed");
          
          extentReport.flush();// when you done with reporting you jsut flush then the report will come to your folder 
          
          Desktop.getDesktop().browse(new File ("report.html").toURI());
          
          //lets add some test should be created under extent report
          
          
         
	}
}
