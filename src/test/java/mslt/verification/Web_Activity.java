/*		Test case to validate activity.
		Activity feed before sign up
		Activity feed less than 3 likes
		Activity feed more than 3 likes
		Activity feed Liked Shows
		Activity feed Recommendation
		Activity Feed Popular 
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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_Activity  {
	WebDriver driver;
	String baseURL,broadcastTitle=null,activityTitle = null;
	List<String> likesInProfile = new ArrayList<String>();
	List<String> broadcastTitles = new ArrayList<String>();

	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");


	@Test
	public void activity_before_signup() throws InterruptedException, ParseException {	
		driver.findElement(By.cssSelector(".link.actividad")).click();
		Assert.assertNotNull(driver.findElement(By.cssSelector(".auth-show-intro-wrapper")),"The authorisation page is not visible");
	}
	
	@Test (dependsOnMethods = { "login"})
	public void activity_recommendation() throws InterruptedException{
		driver.findElement(By.cssSelector(".link.actividad")).click();
		scroll();
		
		List<WebElement> categories = driver.findElements(By.cssSelector(".category"));
		List<WebElement> feedListSeparators = driver.findElements(By.cssSelector(".feed-list-separator"));
		
		List<String> category = new ArrayList<String>();
		for(WebElement temp:categories) category.add(temp.getText());
		
		System.out.println("categories listed: recommendation "+category.toString());
//		Assert.assertTrue(category.contains("PROGRAMMA POPULAR"),"Activity Category doesn't have PROGRAMMA POPULAR");
		Assert.assertTrue(category.toString().contains("POPULAR EN TWITTER"),"Activity Category doesn't have M?S POPULAR EN TWITTER");
		Assert.assertTrue(category.toString().contains("RECOMENDADAS PARA TI"),"Activity Category doesn't have RECOMENDADAS PARA TI");
//		Assert.assertTrue(category.contains("EVENTO DE DEPORTES POPULAR"),"Activity Category doesn't have EVENTO DE DEPORTES POPULAR");
//		Assert.assertTrue(category.contains("SERIES POPULAR"),"Activity Category doesn't have SERIES POPULAR");	
		
		for(WebElement temp:feedListSeparators) System.out.println(temp.getText());
	}
	
	
	@Test (dependsOnMethods = { "login"})
	public void activity_moreThan3Likes() throws InterruptedException{
		
		likeMovie();
		broadcastTitles.add(broadcastTitle);
		likeNinos();
		broadcastTitles.add(broadcastTitle);
		likeSeries();
		broadcastTitles.add(broadcastTitle);

		driver.findElement(By.cssSelector(".link.actividad")).click();
		scroll();
		List<WebElement> categories = driver.findElements(By.cssSelector(".category"));
		List<String> category = new ArrayList<String>();
		for(WebElement temp:categories) category.add(temp.getText());
		System.out.println("categories listed: more than 3 likes "+category.toString());
//		Assert.assertTrue(category.contains("PROGRAMMA POPULAR"),"Activity Category doesn't have PROGRAMMA POPULAR");
		Assert.assertTrue(category.contains("TE GUSTAN"),"Activity Category doesn't have TE GUSTAN");
		
		GetArray g = new GetArray();
		String[] categoryArray = g.getArray(category.toString());
		int count = 0;
		for(int i = 0;i<categoryArray.length;i++)		if(categoryArray[i].contains("TE GUSTAN")) 		count++;
		System.out.println("Total no of likes in activity: "+count);
		Assert.assertTrue((count>=3),"You have more than 3 likes, but in activity page, there are only "+count+" likes displayed");
	}
	
// THIS TEST CASE WILL BE UPDATED ONCE THE BUG 1955 IS FIXED
//	@Test (dependsOnMethods = { "activity_moreThan3Likes"})
//	public void activity_content_likedshows() throws InterruptedException{
//		driver.findElement(By.cssSelector(".link.actividad")).click();
//		scroll();		
		
//		List<WebElement> likedPrograms = driver.findElements(By.cssSelector(".button.like.like-link.selected"));
//		System.out.println("total likePrograms "+likedPrograms.size());
//		for(WebElement temp:likedPrograms){
//			temp.click();
//			activityTitle = driver.findElement(By.cssSelector(".content")).findElement(By.cssSelector(".title")).getText();
//			String tempArr[] = activityTitle.toLowerCase().split(System.getProperty("line.separator"));
//			activityTitle = tempArr[0];
//			System.out.println("activityTitle:-  "+activityTitle);
//			String date;
//			date = driver.findElement(By.cssSelector(".table-cell.broadcast-timestamp")).findElement(By.cssSelector(".date")).getText();
//			System.out.println("date -----------------> "+date);
//			
//		}
//	}
	
	
	
	public void likeMovie() throws InterruptedException{
		launchTag("peliculas");
	}
	
	public void likeSeries() throws InterruptedException{
		launchTag("series");
	}
	
	public void likeNinos() throws InterruptedException{
		launchTag("ninos");
	}
	
	public void launchTag(String tag) throws InterruptedException{
		navigateToChannel(tag);
		testLike();
	}
	
	public void navigateToChannel(String landing){
		String formateddate = format1.format(Calendar.getInstance().getTime());
		driver.get(baseURL+"/programacion/"+formateddate+"/"+landing);
		List<WebElement> posters = driver.findElements(By.cssSelector(".poster"));
		for(WebElement poster:posters){
			if(poster.isDisplayed()) {
				poster.click();break;
			}
		}
	}
	
	public void testLike() throws InterruptedException{	
		broadcastTitle = driver.findElement(By.cssSelector(".content")).findElement(By.cssSelector(".title")).getText();
		driver.findElement(By.cssSelector(".button.like.like-link")).click();
		Thread.sleep(2000);
		driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
		Thread.sleep(2000);

		// compares the liked title in profile page
		driver.get(baseURL+"/perfil");
		List<WebElement> likes = driver.findElements(By.cssSelector(".item"));
		for(WebElement temp:likes) likesInProfile.add(temp.findElement(By.cssSelector(".content")).getText().toLowerCase());
		
		String expected[] = broadcastTitle.toLowerCase().split(System.getProperty("line.separator"));
		broadcastTitle = expected[0];
		System.out.println("broadcastTitle:-  "+broadcastTitle);		

		for(WebElement temp:likes){
			String actual = temp.findElement(By.cssSelector(".content")).getText().toLowerCase() ;
			Assert.assertTrue(likesInProfile.toString().contains(expected[0]),"expected: ("+expected[0]+") but got ("+actual+")");
		}
	}

	public void dislike(){
		driver.get(baseURL+"/perfil");
		List<WebElement> dislikeables = driver.findElements(By.cssSelector(".button.selected"));
		if(dislikeables.size()!=0) for(WebElement temp:dislikeables)	temp.click();
		System.out.println("disliked all likes");
	}
	
	
	@Test
	public void login() throws InterruptedException{
		dislike();
		driver.findElement(By.cssSelector(".link.actividad")).click();
		Assert.assertNotNull(driver.findElement(By.cssSelector(".auth-show-intro-wrapper")),"The authorisation page is not visible");
		driver.findElement(By.cssSelector(".log-in-link")).click();
		driver.findElement(By.name("email")).sendKeys("activityCheck@dontdelete.com");
		driver.findElement(By.name("password")).sendKeys("asdfgh");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		Thread.sleep(2000);
		Assert.assertTrue(driver.getCurrentUrl().contains("actividad"),"after logining in landed in someother page other than activity page");
	}
	
	public void scroll() throws InterruptedException{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,3000)", "");
		Thread.sleep(2000);
		jse.executeScript("window.scrollBy(0,3000)", "");
		Thread.sleep(2000);
	}
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(@Optional () String url) throws MalformedURLException, InterruptedException{    
//		url =  "http://mi.tv";
		baseURL = url;
		RestAssured.baseURI = url; 
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
		dislike();
		driver.quit();
	  }
	}