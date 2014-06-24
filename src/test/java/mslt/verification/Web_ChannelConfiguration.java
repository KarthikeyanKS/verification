//		It validates channel configuration - add/remove channels

package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Web_ChannelConfiguration  {
	WebDriver driver;
	String baseURL;
	
	@Test
	public void signup() throws InterruptedException, ParseException {
		System.out.println("Validating channel configuration");
		driver.get(baseURL+"/perfil/me-gusta");
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.findElement(By.cssSelector(".register-link")).click();
		driver.findElement(By.id("firstName")).sendKeys("testChanConfig");
		driver.findElement(By.id("lastName")).sendKeys("tesChanConf");	
		String uuid = UUID.randomUUID().toString();		
		driver.findElement(By.id("email")).sendKeys(uuid+"@delete.com");
		driver.findElement(By.id("password")).sendKeys("asdfgh");
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		Thread.sleep(2000);
	}
	
	
	@Test (dependsOnMethods = { "signup"})
	public void verifyDefaultChannels() throws InterruptedException{
		System.out.println("  channel configuration - verifyDefaultChannels");
		Set<String> difference = new TreeSet<String>();

		difference = verify("default",3);
		Assert.assertTrue((difference.size()==0),"Channels missing in homepage: "+difference.toString());
		
	}
	
	
	@Test (dependsOnMethods = { "verifyDefaultChannels"})
	public void verifyRemoveChannel() throws InterruptedException{
		System.out.println("  channel configuration - verifyRemoveChannel");
		Set<String> difference = new TreeSet<String>();

		difference = verify("Remover",1);
		Assert.assertTrue((difference.size()==0),"These Channel are not selected but in homepage: "+difference.toString());
	}
	
	
	@Test (dependsOnMethods = { "verifyDefaultChannels"})
	public void verifyAddChannel() throws InterruptedException{
		System.out.println("  channel configuration - verifyAddChannel");
		Set<String> difference = new TreeSet<String>();

		difference = verify("Agregar",12);
		Assert.assertTrue((difference.size()==0),"Channels missing in homepage: "+difference.toString());
	}
	
	
	
	public Set<String> verify(String addRemove, int iter) throws InterruptedException{
		Set<String> channelsInHomePage = new TreeSet<String>();
		Set<String> selectedChannels = new TreeSet<String>();

		driver.get(baseURL+"/perfil");
		driver.findElement(By.partialLinkText("Selecciona tus Canales")).click();
		Thread.sleep(1500);
		List<WebElement> channels = driver.findElements(By.cssSelector(".item"));
		
		for(int i =1;i<=channels.size();i++){
			String addOrRemove = driver.findElement(By.xpath(".//*[@id='profile-list-content-region']/ul/li["+i+"]/a/div[1]/span/span[2]")).getText();
			if(addOrRemove.equals(addRemove)){
				driver.findElement(By.xpath(".//*[@id='profile-list-content-region']/ul/li["+i+"]/a/div[1]/span/span[2]")).click();
				Thread.sleep(1000);
			}
			if(addRemove.equals("Agregar")) selectedChannels.add(driver.findElement(By.xpath(".//*[@id='profile-list-content-region']/ul/li["+i+"]/a/div[3]/h3")).getText().trim());
		}
		
		
		if(addRemove.equals("default")){
			for(int i =1;i<=channels.size();i++){
				String addOrRemove = driver.findElement(By.xpath(".//*[@id='profile-list-content-region']/ul/li["+i+"]/a/div[1]/span/span[2]")).getText();
				if(addOrRemove.equals("Remover"))
					selectedChannels.add(driver.findElement(By.xpath(".//*[@id='profile-list-content-region']/ul/li["+i+"]/a/div[3]/h3")).getText().trim());
			}
		}
		
		System.out.println("Total selected Channels: "+selectedChannels.size());
		System.out.println(selectedChannels.toString());

		driver.findElement(By.cssSelector(".home-link")).click();
		scrollDown(iter);

		List<WebElement> logos = driver.findElements(By.cssSelector(".channel-logo"));
		for(WebElement temp:logos) 
			if(temp.isDisplayed()) 
				channelsInHomePage.add(temp.findElement(By.cssSelector(".channel-link")).getAttribute("title").trim());
		
		
		System.out.println(channelsInHomePage.toString());		
		selectedChannels.removeAll(channelsInHomePage);
		System.out.println("missing Channels: "+selectedChannels);
		
		return selectedChannels;
	}
	
	
	public void scrollDown(int iter) throws InterruptedException{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		for(int i = 1;i<=iter;i++){
			Thread.sleep(1000);
			jse.executeScript("window.scrollBy(0,3000)", "");
			Thread.sleep(1000);
		}
	}
	
	
	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(@Optional () String url) throws MalformedURLException{    
//		url =  "http://mi.tv";
//		driver = new FirefoxDriver();
		baseURL = url;
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