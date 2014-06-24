/*		Test case to validate tv guide.
		Validates progress bar
		Validates movie icon
		Validates channel logo
		Validates main title in detail broadcast info page
		Validates genre and year for movie
		Validates Cast info
		Validates duration of the broadcast
		Validates long synopsis of the broadcast
*/

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_TV_Guide_BroadcastInfo  {
	WebDriver driver;
	String baseURL, baseTag =null;
	int countBrokenprogressBar=0,countBrokenImages = 0,countBrokenTitles=0,countBrokenGenres=0,countBrokenYears=0,countStartEndTime=0,countLongSynopsis=0,countCast=0;
	List<WebElement> displayedChannelItem = new ArrayList<WebElement>();;

	@Test(priority = 1)
	public void launchTag() throws InterruptedException, ParseException {
		System.out.println("launching tag "+ baseTag);
		driver.findElement(By.partialLinkText(baseTag)).click();
		Assert.assertNotNull("Title is empty for -> "+driver.getCurrentUrl(),driver.getTitle());
		Thread.sleep(2000);
		scrollDown();

	}
	
	
	@Test (dependsOnMethods={"launchTag"})
	public void test_onAir_progressBar() throws InterruptedException{
		System.out.println("Validating progress bar");
		List<WebElement> onair = driver.findElements(By.cssSelector(".broadcast.on-air"));
		for(WebElement temp:onair){
			if(temp.isDisplayed()){
				WebElement imagebroadcastlink = temp.findElement(By.cssSelector(".image.broadcast-link"));
				String title = imagebroadcastlink.getAttribute("title");
//				System.out.println("Validating the title "+title);
				WebElement statusbar = imagebroadcastlink.findElement(By.cssSelector(".statusbar"));
				String widthofStatusBar = statusbar.findElement(By.cssSelector(".statusbar-wrapper")).findElement(By.cssSelector(".statusbar-progress")).getAttribute("style");
				if(widthofStatusBar.isEmpty()) 
				{
					System.out.println("The on air progress bar is invalid. pls check here -> "+driver.getCurrentUrl());
					countBrokenprogressBar++;
				}
			}
		}
		Assert.assertTrue(countBrokenprogressBar==0,countBrokenprogressBar+" On air progressBar is broken. Please see the console");
	}

	@Test (dependsOnMethods={"test_onAir_progressBar"})
	public void test_movieIcon() throws InterruptedException{
		System.out.println("Validating movie icon");
		if(baseTag.equalsIgnoreCase("Pelis")){
			List<WebElement> onAirBroadcasts = driver.findElements(By.cssSelector(".broadcast.on-air"));
			List<WebElement> nextBroadcasts = driver.findElements(By.cssSelector(".broadcast.next"));
			List<WebElement> nextOnAirBroadcasts = driver.findElements(By.cssSelector(".broadcast.next-on-air"));
			int totalBroadcasts = 0;
			for(WebElement temp:onAirBroadcasts) if(temp.isDisplayed()) totalBroadcasts++;
			for(WebElement temp:nextBroadcasts) if(temp.isDisplayed()) totalBroadcasts++;
			for(WebElement temp:nextOnAirBroadcasts) if(temp.isDisplayed()) totalBroadcasts++;

			int totalMovieIcons = driver.findElements(By.cssSelector(".icon.movie")).size();
			if(totalBroadcasts!=totalMovieIcons){
				System.out.println("Total Broadcasts in the page: "+totalBroadcasts+" -> "+driver.getCurrentUrl());
				System.out.println("Total movie icons in the page: "+totalMovieIcons+" -> "+driver.getCurrentUrl());
				System.out.println("There is a mismatch");
			}
			Assert.assertTrue((totalBroadcasts==totalMovieIcons),"Total movie icons are not matching with listed broadcasts");
		}
	}	

	@Test (dependsOnMethods={"test_movieIcon"})
	public void test_channelLogo() throws InterruptedException{
		System.out.println("Validating channel logo");
		displayedChannelItem = driver.findElements(By.cssSelector(".channel.item"));
		String script = "return (typeof arguments[0].naturalWidth!=\"undefined\" &&  arguments[0].naturalWidth>0)";
		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver (driver);

		for(WebElement temp:displayedChannelItem) {
			if(temp.isDisplayed()){
//				System.out.println("channel name: "+temp.findElement(By.cssSelector(".channel-logo")).findElement(By.cssSelector(".channel-link")).findElement(By.tagName("img")).getAttribute("title").toString());
//				System.out.println("image url "+temp.findElement(By.cssSelector(".channel-logo")).findElement(By.cssSelector(".channel-link")).findElement(By.tagName("img")).getAttribute("src").toString());
				Object imgStatus = eventFiringWebDriver.executeScript(script, temp.findElement(By.cssSelector(".channel-logo")).findElement(By.cssSelector(".channel-link")).findElement(By.tagName("img")));          
					if(imgStatus.equals(false))
					{
								System.out.println("Channel logo is broken " + temp.findElement(By.cssSelector(".channel-logo")).findElement(By.cssSelector(".channel-link")).findElement(By.tagName("img")).getAttribute("src").toString()+ "@ "+driver.getCurrentUrl());				                
				                countBrokenImages++;
				     }
			}
		}
		Assert.assertTrue(countBrokenImages==0,countBrokenImages+" Channel logo is broken. Please see the console");
	}
		
//	navigating to the detailed movie page and Validating Main title
	@Test (dependsOnMethods={"test_channelLogo"})
	public void test_detailed_information() throws InterruptedException{	
		System.out.println("Validating main title in detail broadcast info page");
		List<WebElement> posters = driver.findElements(By.cssSelector(".poster"));
		for(WebElement temp:posters) {
			if(temp.isDisplayed()){
				temp.click();
				Assert.assertNotNull("Title is empty for -> "+driver.getCurrentUrl(),driver.getTitle());
//				 validating main title
				String title = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[1]/div[2]/div/h1")).getText().toString();
				if(title.isEmpty()){
					System.out.println("Broadcast Title is broken @ broadcast details page "+driver.getCurrentUrl());
					countBrokenTitles++;
				}
				
//				 validating movie genre, year, cast info
				if(baseTag.equalsIgnoreCase("Pelis")){	
					System.out.println("Validating genre and year for movie");
					String[] genreYear = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[1]/div[2]/div/h2")).getText().toString().split("\\s*,\\s*");
					String genre = genreYear[0];
					int year = Integer.parseInt(genreYear[1]);
					//	System.out.println("*"+genre+"*");
					//	System.out.println("*"+year+"*");

					if(genre.isEmpty()){
						System.out.println("Movie Genre is empty @ broadcast details page "+driver.getCurrentUrl());
						countBrokenGenres++;
					}
					if(year<=0||year>=2025){
						System.out.println("Movie year is empty @ broadcast details page "+driver.getCurrentUrl());
						countBrokenYears++;
					}
					
					System.out.println("Validating Cast info");
					String cast = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[2]/div[2]/p[2]")).getText();
					if(!cast.contains("Elenco:")){
						System.out.println("cast information is invalid or empty @ broadcast details page "+driver.getCurrentUrl());
						countCast++;
					}
				}
				
//				 validating duration of the broadcast
				System.out.println("Validating duration of the broadcast");
				String[] startEndTime = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[2]/div[1]/div/div/div[2]/div[2]")).getText().toString().split("\\s*-\\s*");
				String startTime = startEndTime[0];
				String endTime = startEndTime[1];
				//System.out.println("*"+startTime+"*");
				//System.out.println("*"+endTime+"*");
				if(startTime.isEmpty()||endTime.isEmpty()){
					System.out.println("Start time or end time of the broadcast is not valid @ broadcast details page "+driver.getCurrentUrl());
					countStartEndTime++;
				}
				
//				 validating long synopsis of the broadcast
				System.out.println("Validating long synopsis of the broadcast");
				String longSynopsis = null;
//				if(baseTag.equalsIgnoreCase("Deportes")) longSynopsis = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[2]/div[2]/p")).getText();
//				else longSynopsis = driver.findElement(By.xpath(".//*[@id='broadcasts-show-main-region']/div/div[2]/div[2]/p[1]")).getText();
				
				longSynopsis = driver.findElement(By.cssSelector(".body")).findElement(By.cssSelector(".summary")).getText();
				if(longSynopsis.isEmpty()){
					System.out.println("Long synopsis of the broadcast is not valid @ broadcast details page "+driver.getCurrentUrl());
					countLongSynopsis++;
				}
			}
			driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
		}
		System.out.println("-----------------------------------------------");
		Assert.assertTrue(countBrokenTitles==0,countBrokenTitles+" Broadcast title is broken and is empty in details page. Please see the console");
		Assert.assertTrue(countBrokenGenres==0,countBrokenGenres+" Movie Genre is broken and is empty in details page. Please see the console");
		Assert.assertTrue(countBrokenYears==0,countBrokenYears+" Movie year is broken and is empty in details page. Please see the console");
		Assert.assertTrue(countCast==0,countCast+" Cast is broken. Please see the console");
		Assert.assertTrue(countStartEndTime==0,countStartEndTime+" Broadcast Start or EndTime is broken. Please see the console");
//		Assert.assertTrue(countLongSynopsis==0,countLongSynopsis+" LongSynopsis is broken. Please see the console");
	}
			
	
	public void scrollDown(){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,3000)", "");
	}
	
	public void scrollUp(){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,0)", "");
	}
	
	
	@Parameters({ "url", "tag" })
	@BeforeClass											
	  public void runBeforeAllTests(@Optional() String url, @Optional() String tag) throws MalformedURLException, InterruptedException{    
//		url =  "http://mi.tv";
//		tag  = "Pelis";
		baseTag = tag;
		baseURL = url;
//		driver = new FirefoxDriver();
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		driver.get(baseURL);
		driver.manage().window().maximize();
		Assert.assertNotNull("Title is empty for -> "+driver.getCurrentUrl(),driver.getTitle());
//		Thread.sleep(2000);
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}