/*		Test case to validate tv guide.
		1. validates guide overview for 7 days
		2. validates guide filter tags
*/

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_TV_Guide  {
	WebDriver driver;
	String baseURL, broadcastTitle =null;
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	String formateddate = format1.format(Calendar.getInstance().getTime());
	String uuid = null,email;

	@Test
	public void guide_overview_7days() throws InterruptedException, ParseException {
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		
		for(int i =1;i<=7;i++){
			Actions action = new Actions(driver);
			WebElement we = driver.findElement(By.xpath(".//*[@id='components-menu-date-region']/div/ul/li/a"));
			if(i!=1) {
				action.moveToElement(we).moveToElement(driver.findElement(By.xpath(".//*[@id='components-menu-date-region']/div/ul/li/ul/li["+i+"]/a"))).click().build().perform();
			}

			Thread.sleep(2000);
			jse.executeScript("window.scrollBy(0,3000)", "");
			Thread.sleep(2000);
			
			List<WebElement> channels = driver.findElements(By.cssSelector(".channel-wrapper"));
			int totalChannels = channels.size();
			System.out.println("Total channels in day "+i+" = "+totalChannels);
			Assert.assertEquals(totalChannels, 12,"check the total default channels.Its not 12 for day "+i);
		}
	}
	
	@Test
	public void guide_filter_tags() throws InterruptedException{
		for(int i=2;i<=6;i++){
		driver.findElement(By.xpath(".//*[@id='components-menu-tag-region']/ul/li["+i+"]/a")).click();
		Thread.sleep(2000);
		}
	}
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException, InterruptedException{    
		baseURL = url; //"http://mi.tv";
		RestAssured.baseURI = url; //"http://mi.tv";
		RestAssured.port = 80;
//		driver = new FirefoxDriver();
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		driver.get(baseURL);
		driver.manage().window().maximize();
		Thread.sleep(2000);
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}