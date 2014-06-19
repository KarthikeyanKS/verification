/*Validates Broadcasts with the following conditions:
	1. For a Minimum of 7 schedules in each channel
	2. 4 Hours gap between broadcasts (Begin time to begin time)
	3. The above two scenarios are validated for 3 days.
	4. The validation is done for the default channels. i.e. 17 channels for 3 days = 51 scenarios	*/

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class BroadcastCheck  {
	public static WebDriver driver;int count = 0;
	String baseURL, homeurl; 
	BroadcastCheckUtil util = new BroadcastCheckUtil();
	public static int totalBroadcastsWithEmptySynopsisInAllChannels;
	public static int totalBroadcastsInAllChannels;
	int checkingDay;
	
	@Test(dataProvider = "channelSupplier")
	  public void channels(String channelTitle) throws InterruptedException, ParseException {
			driver.get(homeurl);
			System.out.println("----- "+channelTitle);
			Reporter.log("----- "+channelTitle);
        	count = util.linkValidate(channelTitle);
        	Assert.assertTrue(count==0,"Pls check the console for more info");
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
    
    @AfterMethod
    public void totalBroadcastsWithEmptySynopsisInAllChannels(){
    	totalBroadcastsWithEmptySynopsisInAllChannels = totalBroadcastsWithEmptySynopsisInAllChannels+BroadcastCheckUtil.countLongSynopsis;
    	System.out.println("************************* totalBroadcastsWithEmptySynopsisInAllChannels :  "+totalBroadcastsWithEmptySynopsisInAllChannels);
    	totalBroadcastsInAllChannels = totalBroadcastsInAllChannels + BroadcastCheckUtil.countTotalBroadcasts;
    	System.out.println("************************* totalBroadcastsInAllChannels :  "+totalBroadcastsInAllChannels);
    	BroadcastCheckUtil.countLongSynopsis = 0;
    	BroadcastCheckUtil.countTotalBroadcasts= 0; 

    }
    
    @AfterTest
    public void totalBroadcastsWithEmptySynopsisInAllChannelsForDay(){
    	System.out.println("::: @@@@@@@@@@@@@@ Validated "+totalBroadcastsInAllChannels+" broadcasts and found Synopsis is missing in "+totalBroadcastsWithEmptySynopsisInAllChannels+" broadcasts for day "+checkingDay);
//    	totalBroadcastsWithEmptySynopsisInAllChannelsForDay = totalBroadcastsWithEmptySynopsisInAllChannelsForDay + totalBroadcastsWithEmptySynopsisInAllChannels;
    	totalBroadcastsInAllChannels = 0;
    	totalBroadcastsWithEmptySynopsisInAllChannels = 0;
    }
    
    
    @Parameters({"day", "url" })  
	@BeforeClass											
	  public void runBeforeAllTests(@Optional("0") int day, @Optional() String url) throws MalformedURLException, InterruptedException{      		
//    	url = "http://www.mi.tv";
//		day = 3;
    	checkingDay = day;
    	baseURL = url;											
//    	driver = new FirefoxDriver();
    	DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		
    	driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		
    	System.out.println("\n\n::: **********  DAY : "+day+"  **********\n ");
    	Reporter.log(" **************  Validating for the DAY : "+day+"  ************** ");
		
    	if(day==1){
			driver.get(baseURL); driver.manage().window().maximize();
			homeurl = driver.getCurrentUrl();
		}else {
		driver.get(baseURL); driver.manage().window().maximize();
		WebElement we = driver.findElement(By.xpath(".//*[@id='epg-guide-datepicker-region']/div/ul/li/a"));
		we.click();
		driver.findElement(By.xpath(".//*[@id='epg-guide-datepicker-region']/div/ul/li/ul/li["+day+"]/a")).click();
		homeurl = driver.getCurrentUrl();
		}
    	
		Thread.sleep(2000);
		util.scrolldown();
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	
	}