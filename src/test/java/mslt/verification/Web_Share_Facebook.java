/*		Test case to validate sharing broadcasts in facebook	
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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_Share_Facebook  {
	WebDriver driver;
	String baseURL, mitvWindow,facebookWindow,shareURL,title;
	
	@Test
	public void share_facebook() throws InterruptedException, ParseException {
		
		selectBroadcastToShare();
		driver.findElement(By.cssSelector(".js-facebook-link.facebook")).click();
		Thread.sleep(2000);
		
		facebookLogin();
		String shareTitle = driver.findElement(By.cssSelector(".UIShareStage_Title")).getText();
		System.out.println("shareTitle -- "+shareTitle);
		
//		Assert.assertTrue(title.contains(shareTitle),"The title shared is not matching with the source");
		driver.findElement(By.name("share")).click();
		Thread.sleep(2000);
		driver.switchTo().window(mitvWindow);

		verifyInFacebook();
		
		Thread.sleep(2000);
		facebooklogout();
	}
	
	public void selectBroadcastToShare() throws InterruptedException{
		WebElement imageToClick = driver.findElement(By.cssSelector(".image.broadcast-link"));
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
	
	public void facebookLogin() throws InterruptedException{
		Set<String> handles = driver.getWindowHandles();
		handles.remove(mitvWindow);
		facebookWindow = handles.iterator().next();
		driver.switchTo().window(facebookWindow);
	    driver.findElement(By.id("email")).sendKeys("k.s.karthikeyan.mitv@gmail.com");
		driver.findElement(By.id("pass")).sendKeys("Karts007");
		driver.findElement(By.id("loginbutton")).click();
		Thread.sleep(2000);
	}
	
	
	public void verifyInFacebook() throws InterruptedException{
		driver.get("https://www.facebook.com");
		Thread.sleep(2000);
		facebookWindow = driver.getWindowHandle();
		driver.findElement(By.partialLinkText("link")).click();
		Set<String> winhandles = driver.getWindowHandles();
		winhandles.remove(mitvWindow);
		mitvWindow = winhandles.iterator().next();
		driver.switchTo().window(mitvWindow);
		String facebookLinkURL = driver.getCurrentUrl();
		System.out.println("facebookLinkURL -- "+facebookLinkURL);
		Assert.assertTrue(shareURL.equals(facebookLinkURL),"The url shared is not reflecting in facebook");
		driver.switchTo().window(facebookWindow);
	}
	
	public void facebooklogout() throws InterruptedException{
		driver.get("https://www.facebook.com");
		Thread.sleep(2000);
		driver.findElement(By.id("navAccountLink")).click();
		driver.findElement(By.cssSelector(".uiLinkButton.navSubmenu")).click();
	}
	
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(@Optional () String url) throws MalformedURLException{
//		url = "http://www.mi.tv"; 
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
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}