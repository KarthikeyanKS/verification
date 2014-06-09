//		It validates like, dislike for movies, series, sports, other

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

public class Web_LikeDislike  {
	WebDriver driver;
	String baseURL, broadcastTitle =null;
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	String formateddate = format1.format(Calendar.getInstance().getTime());
	
	@Test
	public void login() throws InterruptedException, ParseException {
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		driver.get(baseURL+"/perfil/me-gusta");
		driver.manage().window().maximize();
		driver.findElement(By.cssSelector(".log-in-link")).click();
		driver.findElement(By.name("email")).sendKeys("testLike@dontdelete.com");
		driver.findElement(By.name("password")).sendKeys("asdfgh");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		Thread.sleep(2000);
	}
	
	@Test (dependsOnMethods = { "login"})
	public void likeMovie() throws InterruptedException{
		launchTag("peliculas");
	}
	
	@Test (dependsOnMethods = { "login"})
	public void likeSeries() throws InterruptedException{
		launchTag("series");
	}
	
	@Test (dependsOnMethods = { "login"})
	public void likeDeportes() throws InterruptedException{
		launchTag("deportes");
	}
	
	@Test (dependsOnMethods = { "login"})
	public void likeNinos() throws InterruptedException{
		launchTag("ninos");
	}
	
	public void launchTag(String tag) throws InterruptedException{
		dislike();
		navigateToChannel(tag);
		testLike();
		navigateToChannel(tag);
		testDislike();
	}
	public void dislike(){
		driver.get(baseURL+"/perfil");
//		driver.findElement(By.cssSelector(".link.profile")).click();
		List<WebElement> dislikeables = driver.findElements(By.cssSelector(".button.selected"));
		if(dislikeables.size()!=0) {
		for(WebElement temp:dislikeables)	temp.click();
		}
	}
	
	public void testLike() throws InterruptedException{
		List<WebElement> broadcasts = driver.findElements(By.cssSelector(".broadcast-link.next"));
		for(WebElement temp:broadcasts){
			broadcastTitle = temp.getAttribute("title");
			temp.click();  
			break;
		}		
		System.out.println("broadcastTitle:-  "+broadcastTitle);		
		driver.findElement(By.cssSelector(".button.like.like-link")).click();
		driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
		Thread.sleep(2000);

		// compares the liked title in profile page
		driver.get(baseURL+"/perfil");
		List<WebElement> likes = driver.findElements(By.cssSelector(".item"));
		for(WebElement temp:likes){
			String expected = broadcastTitle.toLowerCase();
			String actual = temp.findElement(By.cssSelector(".content")).getText().toLowerCase() ;
			
			Assert.assertEquals((actual.contains(expected)),true,"expected: ("+expected+") but got ("+actual+")");
		}
	}
	
	public void testDislike() throws InterruptedException{
		dislike();
		List<WebElement> broadcasts = driver.findElements(By.cssSelector(".broadcast-link.next"));
		for(WebElement temp:broadcasts){
			temp.click(); 
			break;
		}		

		List<WebElement> likes = driver.findElements(By.cssSelector(".button.like.like-link.selected"));
//		System.out.println("This should be zero. As we have a ticket raised for this ignoring right now : "+likes.size());
		Assert.assertEquals(likes.size(),0);
		driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
	}
	
	
	
	public void navigateToChannel(String landing){
		driver.get(baseURL+"/programacion/"+formateddate+"/"+landing);
		List<WebElement> logos = driver.findElements(By.cssSelector(".channel-link"));
		for(WebElement logo:logos){
			if(logo.isDisplayed()) {
				logo.click();break;
			}
		}
	}
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException{    
		baseURL = url; //"http://mi.tv";
		RestAssured.baseURI = url; //"http://mi.tv";
		RestAssured.port = 80;
//		driver = new FirefoxDriver();
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}