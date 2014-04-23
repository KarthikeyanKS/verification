package mslt.verification;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Reporter;

public class BrokenLinksUtil {
public final WebDriver driver = new FirefoxDriver();

	public void login(String baseURL) throws InterruptedException{
		//driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
		driver.get(baseURL+"/profile/likes");
		driver.manage().window().maximize();
		driver.findElement(By.className("log-in-link")).click();
		driver.findElement(By.name("email")).sendKeys("1test@test.com");
		WebElement passwordElement = driver.findElement(By.name("password"));
		passwordElement.sendKeys("Karts007");
		passwordElement.submit();
		Thread.sleep(3000);
	}
	
	public void logout() throws InterruptedException{
		driver.findElement(By.cssSelector(".link.profile")).click();
		Thread.sleep(1000);
		driver.findElement(By.cssSelector(".text-button.logout-link")).click();
	}
	
	public void brokenLinks(String cname) throws InterruptedException, IOException{
		 driver.findElement(By.cssSelector(cname)).click();
		 JavascriptExecutor jse = (JavascriptExecutor)driver;
		 for (int second = 0;; second++) {
             if(second >=5){
                 break;
             }
             jse.executeScript("window.scrollBy(0,2000)", "");
             Thread.sleep(1000);
		 }
		 List<WebElement> links = driver.findElements(By.tagName("a"));
		 System.out.println("total links in page "+cname+"  " +links.size());
		 URL url;
		 for (WebElement temp: links){
			//System.out.println("links --- "+temp.getAttribute("href"));
			url = new URL(temp.getAttribute("href"));
			HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();
			httpURLConnect.setConnectTimeout(3000); // 6 seconds delay
			httpURLConnect.connect();
			
			if(httpURLConnect.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND){
				Reporter.log("WARNING --- Bad response received for the link -- "+url+" <<<-- BROKEN LINK .. NEED YOUR ACTION -->>> Got the Response code: "+httpURLConnect.getResponseCode()+" - "+httpURLConnect.getResponseMessage()
				+ " - "+ HttpURLConnection.HTTP_NOT_FOUND,true);
				org.testng.Assert.fail(url+" ---->>> Response code: "+httpURLConnect.getResponseCode()+" - "+httpURLConnect.getResponseMessage() + " - "+ HttpURLConnection.HTTP_NOT_FOUND);

			} else {	
				org.testng.Assert.assertTrue(true,url+" <---- This URL is good: Got the response code: "+httpURLConnect.getResponseCode());
				//Reporter.log(httpURLConnect.getResponseCode()+" Got a valid response code *** "+url+" <---- This URL is good",true);
			}

		 }
		 
	}
}
