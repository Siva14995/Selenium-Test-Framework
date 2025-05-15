package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
	public static final org.apache.logging.log4j.Logger Logger = LoggerManager.getLogger(BaseClass.class);

	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);

	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	@BeforeSuite
	public void loadConfig() throws IOException {
		// Load the configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "/src/main/resources/config.properties");
		prop.load(fis);
		Logger.info("config.properties file loaded");

		// Start the Extent Report
		// ExtentManager.getReporter(); ----> This has been implemented in TestListener
	}

	@SuppressWarnings("deprecation")
	@BeforeMethod
	public synchronized void setup() throws IOException {
		System.out.println("Setting up WebDriver for:" + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(5);
		Logger.info("WebDriver Initialized and Browser Maximized");
		Logger.trace("This is a trace message");
		Logger.error("This is a error message");
		Logger.debug("This is a debug message");
		Logger.fatal("This is a debug message");
		Logger.warn("This is a debug message");

		/*
		 * // Initialize the actionDriver only once if (actionDriver == null) {
		 * actionDriver = new ActionDriver(driver);
		 * Logger.info("ActionDriver instance is created. "+Thread.currentThread().getId
		 * ()); }
		 */

		// Initialize ActionDriver for the Current Thread
		actionDriver.set(new ActionDriver(getDriver()));
		Logger.info("ActionDriver Initialized for thread: " + Thread.currentThread().getId());
	}

	// Initialize the WebDriver based on browser defined in config.properties file
	private synchronized void launchBrowser() {
		String browser = prop.getProperty("browser");

		if (browser.equalsIgnoreCase("chrome")) {
			System.setProperty("webdriver.chrome.driver", "Drivers/chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("credentials_enable_service", false);
			prefs.put("password_manager_enabled", false);
			options.setExperimentalOption("prefs", prefs);
			options.addArguments("--remote-debugging-port=63292"); // Or some other unused port
			 options.addArguments("--headless"); // Run Chrome in headless mode
			 options.addArguments("--disable-gpu"); // Disable GPU for headless mode
			// options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments
			options.addArguments("chrome.switches", "--disable-extensions");
			options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
			options.setBinary("E:\\SELENIUM DOWNLOAD LATEST JAR FILES\\chrome-win64\\chrome-win64\\chrome.exe");
			// driver = new ChromeDriver(options);
			driver.set(new ChromeDriver(options)); // New Changes as per thread
			ExtentManager.registerDriver(getDriver());
			Logger.info("ChromeDriver Instance is created.");
		} else if (browser.equalsIgnoreCase("firefox")) {
			// Create FirefoxOptions
			FirefoxOptions options = new FirefoxOptions();
			options.addArguments("--headless"); // Run Firefox in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
			options.addArguments("--width=1920"); // Set browser width
			options.addArguments("--height=1080"); // Set browser height
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD environments
			options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(options)); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			Logger.info("FirefoxDriver Instance is created.");
		} else if (browser.equalsIgnoreCase("edge")) {
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless"); // Run Edge in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU acceleration
			options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable pop-up notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD
			options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes

			// driver = new EdgeDriver();
			driver.set(new EdgeDriver(options)); // New Changes as per Thread
			ExtentManager.registerDriver(getDriver());
			Logger.info("EdgeDriver Instance is created.");
		} else {
			throw new IllegalArgumentException("Browser Not Supported :" + browser);
		}
	}

	// configure browser settings such as implicit wait, maximize the browser and
	// navigate to URL
	private void configureBrowser() {
		// maximize the browser
		getDriver().manage().window().maximize();

		// Implicit Wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

		// Navigate to URL
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to Navigate to the URL:" + e.getMessage());
		}
	}

	@AfterMethod
	public synchronized void tearDown() {
		if (driver != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				System.out.println("unable to quit the driver:" + e.getMessage());
			}
		}
		Logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		// driver = null;
		// actionDriver = null;
		// ExtentManager.endTest(); ----> This has been implemented in TestListener

	}

	// Getter method for prop
	public static Properties getProp() {
		return prop;
	}

	// Driver getter method
	/*
	 * public WebDriver getDriver() { return driver; }
	 */

	// Getter Method for WebDriver
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// Getter Method for ActionDriver
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			System.out.println("ActionDriver is not initialized");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return actionDriver.get();
	}

	// Driver setter method
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}

	// static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

}
