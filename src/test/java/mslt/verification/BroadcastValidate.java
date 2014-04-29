//	To validate long gaps in broadcasts for all the default channels. 
// 	If there is more than 4 hours of gap in broadcast schedule these test cases will fail

package mslt.verification;

import static org.junit.Assert.assertTrue;

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
			System.out.println("--------> validating the channel  --------->   "+channelTitle);
        	count = util.linkValidate(channelTitle);
        	assertTrue(count==0);
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
    
 
    
    @Parameters("day")
	@BeforeClass											
	  public void runBeforeAllTests(int day) throws MalformedURLException, InterruptedException{      		
    	
//    	driver = new FirefoxDriver();
    	DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		
    	driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		
    	System.out.println(" **************  Validating for the DAY : "+day+"  ************** ");
    	Reporter.log(" **************  Validating for the DAY : "+day+"  ************** ");
    	baseURL = "http://www.mi.tv";											
		
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