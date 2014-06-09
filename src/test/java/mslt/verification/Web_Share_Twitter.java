/*		Test case to validate sharing broadcasts in Twitter	
*/
package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_Share_Twitter  {
	WebDriver driver;
	String baseURL, mitvWindow,shareURL,title,twitterWindow;
	
	@Test
	public void share_twitter() throws InterruptedException{
		twitterDeleteExistingPosts();
		driver.get(baseURL);
		selectBroadcastToShare();

		driver.findElement(By.cssSelector(".js-twitter-link.twitter")).click();
		Thread.sleep(2000);
		
		twitterLogin();
		System.out.println("Twitter shareURL "+shareURL);
		Thread.sleep(2000);
		driver.switchTo().window(mitvWindow);
		verifyInTwitter();
		
		Thread.sleep(2000);
		twitterlogout();
	}
	
	
	public void selectBroadcastToShare() throws InterruptedException{
		WebElement imageToClick = driver.findElement(By.cssSelector(".lazy-image-wrapper"));
		if(imageToClick.isDisplayed()){
			imageToClick.click();
			Thread.sleep(2000);
		}
		title = driver.findElement(By.cssSelector(".title")).getText();
		System.out.println("title -- "+title);
		shareURL = driver.getCurrentUrl();
		System.out.println("shareURL -- "+shareURL);

		mitvWindow = driver.getWindowHandle();
		driver.findElement(By.cssSelector(".button.share.share-popup-link")).click();
	}
	
	public void twitterLogin() throws InterruptedException{
		Set<String> windowhandles = driver.getWindowHandles();
		windowhandles.remove(mitvWindow);
		twitterWindow = windowhandles.iterator().next();
		driver.switchTo().window(twitterWindow);
	    driver.findElement(By.cssSelector(".text")).sendKeys("k.s.karthikeyan.mitv@gmail.com");
	    driver.findElement(By.cssSelector(".password.text")).sendKeys("Karts007");
		driver.findElement(By.cssSelector(".button.selected.submit")).click();
		Thread.sleep(2000);
	}
	
	
	public void verifyInTwitter() throws InterruptedException{
		driver.get("https://twitter.com/");
		Thread.sleep(2000);
		twitterWindow = driver.getWindowHandle();

		driver.findElement(By.cssSelector(".js-display-url")).click();
		Set<String> winhandles = driver.getWindowHandles();
		winhandles.remove(mitvWindow);
		mitvWindow = winhandles.iterator().next();
		driver.switchTo().window(mitvWindow);
		String twitterLinkURL = driver.getCurrentUrl();
		System.out.println("twitterLinkURL -- "+twitterLinkURL);
		Assert.assertTrue(shareURL.equals(twitterLinkURL),"The url shared is not reflecting in twitter");
		driver.switchTo().window(twitterWindow);
	}
	
	
	public void twitterlogout() throws InterruptedException{
		driver.get("https://www.twitter.com");
		Thread.sleep(2000);
		driver.findElement(By.cssSelector(".Icon.Icon--cog.Icon--large")).click();
		driver.findElement(By.cssSelector(".js-signout-button")).click();
	}
	
	public void twitterDeleteExistingPosts() throws InterruptedException{
		driver.get("https://www.twitter.com");
		Thread.sleep(2000);
		
		driver.findElement(By.cssSelector(".text-input.email-input")).sendKeys("k.s.karthikeyan.mitv@gmail.com");
		driver.findElement(By.cssSelector(".text-input.flex-table-input")).sendKeys("Karts007");
		driver.findElement(By.cssSelector(".submit.btn.primary-btn.flex-table-btn.js-submit")).click();
		List<WebElement> deletables = driver.findElements(By.cssSelector(".with-icn.js-action-del.js-tooltip"));
		for(WebElement delete:deletables){
			delete.click();
			driver.findElement(By.cssSelector(".btn.primary-btn.delete-action")).click();
			Thread.sleep(2000);
		}
		twitterlogout();
		Thread.sleep(2000);
	}
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException{    
		baseURL = url; //"http://mi.tv"; //
		RestAssured.baseURI = url;//"http://mi.tv";//
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
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}