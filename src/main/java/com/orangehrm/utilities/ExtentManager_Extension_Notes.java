package com.orangehrm.utilities;

public class ExtentManager_Extension_Notes {
/* pArt -1 
 * PART 1 — FULL, DEEP-DIVE EXPLANATION OF BASE64 SCREENSHOT (Beautiful, Long, Elaborated,
 *  “Read-After-2-Weeks-And-Still-Understand” Version)

Read this anytime in future and it will click instantly again.

⭐ Why We Convert Screenshots to Base64 in Automation Frameworks
(A full conceptual + technical explanation you can reuse forever)

When Selenium takes a screenshot, it does NOT magically “draw it inside ExtentReport”.
Instead, Selenium simply produces a PNG file stored on your machine.

Example:

C:\MyFramework\Screenshots\login_failed.png


This seems fine when running on a local machine.
But modern automation frameworks run across:

Jenkins CI/CD

Docker containers

AWS EC2

Selenium Grid

GitHub Actions

Linux servers

Parallel test environments

On these environments, storing PNG files becomes a BIG problem.

❌ Problem 1 — File paths are NOT consistent

Your Windows path:

C:\Users\Mohammed\Project\Report\Screenshots


On Linux:

/home/ec2-user/Project/Report/Screenshots


On Docker:

/usr/local/project/screenshots


ExtentReport tries to load the screenshot from the exact file path…

But the file does NOT exist there → BROKEN IMAGE.

❌ Problem 2 — Docker containers delete files automatically

Docker containers are ephemeral (temporary). After test execution ends:

✔ Containers destroyed
✔ Workspace wiped
✔ PNG files deleted

So even if your PNG existed during execution,
by the time you open the report → screenshot is gone.

❌ Problem 3 — Selenium Grid nodes capture screenshots on nodes, not hub

If your test runs on Grid Node #3, the PNG saves in:

Node3:/tmp/screenshots


But your report is opened on your local machine or Jenkins → path mismatch.

❌ Problem 4 — ExtentReport cannot embed physical PNG inside HTML

ExtentReport can display PNG only if:

✔ File exists
✔ Path is correct
✔ File is accessible from browser

But HTML reports must be portable — you should be able to email them or open on another machine.

PNG breaks this portability.

⭐ THE SOLUTION — Base64 Encoding

Base64 solves ALL these issues.
Because instead of storing an image file, we convert the PNG into a STRING.

This string represents the entire image.

Example Base64 output:
iVBORw0KGgoAAAANSUhEUgAAAfQAAAC... (10,000+ chars)


This string can be safely:

✔ Embedded inside the HTML
✔ Sent over network
✔ Stored in memory
✔ Viewed without file system dependency

The screenshot becomes SELF-CONTAINED inside the report.

⭐ How Base64 Conversion Works (Line-by-Line)

Here is your method:

public static String convertToBase64(File screenshotFile) {
    String base64Format = "";
    try {
        byte[] fileContent = FileUtils.readFileToByteArray(screenshotFile);
        base64Format = Base64.getEncoder().encodeToString(fileContent);
    } catch (IOException e) {
        e.printStackTrace();
    }
    return base64Format;
}

✔ Step 1 — Read PNG file bytes
byte[] fileContent = FileUtils.readFileToByteArray(screenshotFile);


This produces an array of binary data, something like:

[137, 80, 78, 71, 13, 10, 26, ...]


This is the actual raw image content.

✔ Step 2 — Convert bytes → Base64 String
base64Format = Base64.getEncoder().encodeToString(fileContent);


Java encodes these bytes into a printable string.

This string is mathematically reversible → you can decode it back into the original PNG.

✔ Step 3 — Return Base64 String

ExtentReport takes this Base64 string and embeds it inside HTML:

<img src="data:image/png;base64,iVBORw0KG..." />


Now the screenshot is IN the report — not stored somewhere else.

⭐ Why Base64 Is the BEST Practice in Modern Automation
Feature	PNG File	Base64
Portable across OS	❌	✔
Works in Docker	❌	✔
Works in Jenkins	❌	✔
No broken paths	❌	✔
HTML report self-contained	❌	✔
Works in Grid	❌	✔

This is why ALL advanced frameworks use Base64 for screenshots.

You can confidently tell interviewers:

“Base64 screenshots make our ExtentReport fully portable and CI/CD-safe.
The screenshot is embedded inside the HTML itself, so it never depends on a file path or OS.”
 * 
 * 
 * 
 * ==================================================================================================================
 * 
 * PArt ==> 2 
 * 
 * ✅ PART 2 — FULL EXTENT MANAGER CLASS (LINE-BY-LINE EXPLANATION YOU CAN READ ANYTIME)

Below is your exact ExtentManager class, broken into sections with explanation after each block.

⭐ 🔹 Section 1 — Global Objects
private static ExtentReports extentReports;
private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
private static Map<Long, WebDriver> driverMap = new HashMap<>();

✔ ExtentReports

The main engine → created ONCE per test run.

✔ ThreadLocal<ExtentTest>

Ensures EACH test method gets its OWN ExtentTest object
→ Prevents parallel reporting overlap.

✔ driverMap

Optional → used if you need driver lookup by thread.

⭐ 🔹 Section 2 — getReporter(): Initialize the Engine
public synchronized static ExtentReports getReporter() {
    if (extentReports == null) {
        String reportPath = System.getProperty("user.dir") + "\\src\\test\\resources\\ExtentReport";
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setReportName("Automation Test Report");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("OrangeHrm Report");
        sparkReporter.config().setTimelineEnabled(true);

        extentReports = new ExtentReports();
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name ", System.getProperty("user.name"));
    }
    return extentReports;
}

What this section does:

Create the report engine only once

Attach SparkReporter (decides UI, theme, output file)

Add meta info (OS, Java version, etc.)

Return the SAME engine for every test method

This is exactly like your BaseClass WebDriver singleton.

⭐ 🔹 Section 3 — Start a Test
public synchronized static ExtentTest startTest(String testName) {
    ExtentTest extentCreateTest = getReporter().createTest(testName);
    extentTest.set(extentCreateTest); // store in ThreadLocal
    return extentCreateTest;
}

Meaning:

Create a new ExtentTest object for THIS test method

Store it in ThreadLocal → thread-safe

Return it

This ensures:

✔ TestA gets TestA logs
✔ TestB gets TestB logs
No mixing.

⭐ 🔹 Section 4 — Finish a Test
public synchronized static void endTest() {
    getReporter().flush();
}

Meaning:

Write everything to the HTML file

Finalize the report

Call this once per test class or once per suite (listener will handle it).

⭐ 🔹 Section 5 — Getting Current Test for Logging
public synchronized static ExtentTest getTest() {
    return extentTest.get();
}


This always returns:

The active ExtentTest object

For the current thread

⭐ 🔹 Section 6 — Helper: Get the Test Name
public static String getTestName() {
    ExtentTest currentTest = getTest();
    if(currentTest != null) {
        return currentTest.getModel().getName();
    } else {
        return "No Test is currently active for this thread";
    }
}


Used for:

Naming screenshots

Logging

Debugging

⭐ 🔹 Section 7 — Logging Methods
public static void logStep(String logMessage) {
    getTest().info(logMessage);
}


Each logging method attaches info to the current ExtentTest:

logStep() → info

logStepWithScreenshot() → info + screenshot

logFailure() → fail + screenshot

logSkip() → skip

These methods use:

getTest()


…so they automatically attach to the correct test thread.

⭐ 🔹 Section 8 — Screenshot Conversion & Embedding

You already understand this thoroughly.
This section:

Captures screenshot

Saves PNG

Converts to Base64

Embeds inside HTML report

This makes your report portable.

⭐ 🔹 Section 9 — Registering Driver per Thread (Optional)
public static void registerDriver(WebDriver driver) {
    driverMap.put(Thread.currentThread().getId(), driver);
}


This is optional, but helpful if:

You want screenshot → driver lookup

You want logging from utility classes that don’t have direct driver access

⭐ FINAL INTERVIEW SUMMARY FOR EXTENTMANAGER

You can repeat this anytime:

ExtentManager creates a single ExtentReports engine and one ExtentTest per test method using ThreadLocal.
Each ExtentTest contains step logs, statuses, exceptions, and Base64 screenshots.
The HTML report is generated by flushing the engine at the end of execution.
Base64 ensures screenshots work in Jenkins, Docker, Grid, and across OS.”
 */
}
