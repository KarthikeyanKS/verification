/*		Test case to validate signup and login flows & error scenarios. It validates for facebook and email user.
		
*/

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

public class Web_Signup_Login  {
	WebDriver driver;
	String baseURL, broadcastTitle =null;
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	String formateddate = format1.format(Calendar.getInstance().getTime());
	String uuid = null,email;
	
	@Test
	public void signup_email_success() throws InterruptedException, ParseException {
		uuid = UUID.randomUUID().toString();	
		email = uuid +"@delete.com";
		signup_email("validfname","validlname",email,"asdfgh");
		Thread.sleep(2000);
		Assert.assertTrue(false,"purposely failing to get clarification on the landing page." +
				" If its not cached, its landing in home page, or else its landing in profile page");
		logout();
	}
	
	@Test
	public void login_facebook_success() throws InterruptedException, ParseException {
		if(!baseURL.contains("192")){
		driver.get(baseURL+"/perfil/me-gusta");
		String mitvWindow = driver.getWindowHandle();
		driver.findElement(By.cssSelector(".fb-login-button")).click();
		Thread.sleep(2000);
		Set<String> handles = driver.getWindowHandles();
		handles.remove(mitvWindow);
		String facebookWindow = handles.iterator().next();
		driver.switchTo().window(facebookWindow);
	    driver.findElement(By.id("email")).sendKeys("k.s.karthikeyan.mitv@gmail.com");
		driver.findElement(By.id("pass")).sendKeys("Karts007");
		driver.findElement(By.id("loginbutton")).click();
		Thread.sleep(2000);
		driver.switchTo().window(mitvWindow);
		Thread.sleep(2000);
		driver.get(baseURL+"/perfil/me-gusta");
		Assert.assertTrue(driver.findElement(By.cssSelector(".text-button.logout-link")).isDisplayed());
		logout();
		}
	}
	
	@Test(dependsOnMethods = { "signup_email_success"})
	public void login_email(){
		driver.get(baseURL+"/perfil/me-gusta");
		driver.findElement(By.cssSelector(".log-in-link")).click();
		driver.findElement(By.name("email")).sendKeys(email);
		driver.findElement(By.name("password")).sendKeys("asdfgh");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		Assert.assertTrue(false,"purposely failing to get clarification on the landing page." +
				" If its not cached, its landing in home page, or else its landing in profile page");
		logout();
	}

	@Test
	public void signup_email_error_handling_invalidFirstName() throws InterruptedException, ParseException {
		uuid = UUID.randomUUID().toString();		
		signup_email("123456789","lname",uuid+"@delete.com","asdfgh");
		Thread.sleep(2000);
		Assert.assertNotNull(driver.findElement(By.id("login")),"Its logged in..It shouldn't");
	}
	
	@Test
	public void signup_email_error_handling_invalidLastName() throws InterruptedException, ParseException {
		uuid = UUID.randomUUID().toString();		
		signup_email("fname","1234sfsdfsfs56789",uuid+"@delete.com","asdfgh");
		Thread.sleep(2000);
		Assert.assertNotNull(driver.findElement(By.id("login")),"Its logged in..It shouldn't");
	}
	
	@Test
	public void signup_email_error_handling_invalidEmail() throws InterruptedException, ParseException {
		uuid = UUID.randomUUID().toString();		
		signup_email("fname","lname",uuid+"@delete","asdfgh");
		Thread.sleep(2000);
		String errorText = driver.findElement(By.cssSelector(".auth-show-errors")).getText();
		Assert.assertTrue(errorText.contains("Correo"),"it is not displaying the expected auth error message");
	}
	
	@Test
	public void signup_email_error_handling_invalidPassword() throws InterruptedException, ParseException {
		uuid = UUID.randomUUID().toString();		
		signup_email("fname","lname",uuid+"@delete.com","1");
		Thread.sleep(2000);
		String errorText = driver.findElement(By.cssSelector(".auth-show-errors")).getText();
//		System.out.println(errorText);
		Assert.assertTrue(errorText.contains("6 caracteres"), "its letting single digit password for signup");
		
	}
	
	
	public void signup_email(String fname, String lname, String email, String password){
		driver.get(baseURL+"/perfil/me-gusta");
		driver.findElement(By.cssSelector(".register-link")).click();
		driver.findElement(By.id("firstName")).sendKeys(fname);
		driver.findElement(By.id("lastName")).sendKeys(lname);	
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.xpath("//button[@type='submit']")).click();
	}
	
	
	
	public void logout(){
		driver.get(baseURL+"/perfil/me-gusta");
		driver.findElement(By.cssSelector(".text-button.logout-link")).click();
	}
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException{    
//		String url = "http://192.168.2.125"; 
		baseURL = url; 
		RestAssured.baseURI = url;
		RestAssured.port = 80;
//		driver = new FirefoxDriver();
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
		driver.get(baseURL+"/perfil/me-gusta");
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}