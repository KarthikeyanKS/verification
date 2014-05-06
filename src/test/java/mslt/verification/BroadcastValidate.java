/*	1. For a Minimum of 7 schedules for a channel
	2. 4 Hours gap between broadcasts (Begin time to begin time)
	3. The above two scenarios are validated for 3 days.
	4. The validation is done for the default channels. i.e. 17 channels for 3 days = 51 scenarios	*/


package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class BroadcastValidate  {
	public static WebDriver driver;int count = 0;
	String baseURL; 
	BroadcastValidateUtil util = new BroadcastValidateUtil();
	
	@Test(dataProvider = "channelSupplier")
	  public void channels(String channelTitle) throws InterruptedException {
			System.out.println("----- "+channelTitle);
			Reporter.log("----- "+channelTitle);
        	count = util.linkValidate(channelTitle);
        	Assert.assertTrue(count==0);
	  }
	
    
	@DataProvider(name = "channelSupplier")
    public Iterator<Object[]> channelSupplier () throws InterruptedException {
        
        List<Object[]> channelTitle = new ArrayList<Object[]>();
        List<WebElement> channels = driver.findElements(By.className("channel-link"));
        for(WebElement channel: channels){
            channelTitle.add(new Object[] { channel.getAttribute("title") } ); 
  		}
        return channelTitle.iterator();
 
    }
    
 
    
    @Parameters({"day", "url" })  
	@BeforeClass											
	  public void runBeforeAllTests(int day, String url) throws MalformedURLException, InterruptedException{      		
    	
//    	driver = new FirefoxDriver();
    	DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		
    	driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		
    	System.out.println("\n\n**************  Validating for the DAY : "+day+"  **************\n ");
    	Reporter.log(" **************  Validating for the DAY : "+day+"  ************** ");
    	baseURL = url;											
		
    	if(day==1){
			driver.get(baseURL); driver.manage().window().maximize();
		}else {
		driver.get(baseURL); driver.manage().window().maximize();
		driver.findElement(By.cssSelector(".text.common-select-box-title")).click();
		driver.findElement(By.xpath("//*[@id=\"epg-date-selector-region\"]/div/div[2]/ul/li["+day+"]/a")).click();
		}
    	
		Thread.sleep(2000);
		util.scrolldown();
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	
	}