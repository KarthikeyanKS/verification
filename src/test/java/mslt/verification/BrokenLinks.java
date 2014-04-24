//		To validate broken links with and without login

package mslt.verification;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BrokenLinks  {
	public static WebDriver driver;
	String baseURL;
	BrokenLinksUtil util = new BrokenLinksUtil();
	
	@Test(priority = 1)
	  public void login() throws InterruptedException {
		util.login(baseURL);
	  }
	
	@Test(dataProvider = "linkSupplier",dependsOnMethods = { "login"})
	  public void loginValidate(String cname) throws InterruptedException, IOException {
		util.brokenLinks(cname);
	  }
	
	@Test(dependsOnMethods = { "loginValidate"})
	  public void logout() throws InterruptedException {
		util.logout();
	  }
	
	@Test(dataProvider = "linkSupplier",dependsOnMethods = { "logout"})
	  public void logoutValidate(String cname) throws InterruptedException, IOException {
		util.brokenLinks(cname);
	  }

	@DataProvider(name = "linkSupplier", parallel = false)
	public Object[][] data() throws Exception {
		Object[][] retObjArr={{".link.profile"},{".link.search"},{".link.guide"},{".link.activity"}};
		return(retObjArr);
	}

	
																				//	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests() throws MalformedURLException{      		//	(String url) {
		baseURL = "http://www.mi.tv";											// 	url;
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
//		capability.setCapability("timeout", 1800);
//		capability.setCapability("browserTimeout", 2700);
//		capability.setCapability("nativeEvents", true);
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}