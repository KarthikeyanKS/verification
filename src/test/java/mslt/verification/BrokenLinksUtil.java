package mslt.verification;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class BrokenLinksUtil {
	public void login(String baseURL) throws InterruptedException{
		BrokenLinks.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		BrokenLinks.driver.get(baseURL);
		BrokenLinks.driver.manage().window().maximize();
		BrokenLinks.driver.findElement(By.cssSelector(".link.profile")).click();
		BrokenLinks.driver.findElement(By.className("log-in-link")).click();
		BrokenLinks.driver.findElement(By.name("email")).sendKeys("k.s.karthikeyan.mitv@gmail.com");
		WebElement passwordElement = BrokenLinks.driver.findElement(By.name("password"));
		passwordElement.sendKeys("Karts007");
		passwordElement.submit();
		Reporter.log("<----- User Logged in ----->");
		Thread.sleep(3000);
//		BrokenLinks.driver.findElement(By.className("profile-channel-link")).click();
	}
	
	public void logout() throws InterruptedException{
		BrokenLinks.driver.findElement(By.cssSelector(".link.profile")).click();
		Thread.sleep(1000);
		BrokenLinks.driver.findElement(By.cssSelector(".text-button.logout-link")).click();
		Reporter.log("<----- User Logged out ----->");
		Thread.sleep(2000);
	}
	
	public void brokenLinks(String cname) throws InterruptedException, IOException{
		 BrokenLinks.driver.findElement(By.cssSelector(cname)).click();
		 Thread.sleep(1500);
		 JavascriptExecutor jse = (JavascriptExecutor)BrokenLinks.driver;
		 if(cname == ".link.guide" || cname == ".link.activity" )
		 for (int second = 0;; second++) {
             if(second >=4){
                 break;
             }
             jse.executeScript("window.scrollBy(0,2000)", "");
             Thread.sleep(1000);
		 } 
		 
		 List<WebElement> links = BrokenLinks.driver.findElements(By.tagName("a"));
		 URL url;
		 for (WebElement temp: links){
			url = new URL(temp.getAttribute("href"));
			HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();
			httpURLConnect.setConnectTimeout(3000); // 3 second delay
			httpURLConnect.connect();
			if(httpURLConnect.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND){
				Reporter.log("WARNING --- Bad response received for the link -- "+url+" <<<-- BROKEN LINK .. NEED YOUR ACTION -->>> Got the Response code: "+httpURLConnect.getResponseCode()+" - "+httpURLConnect.getResponseMessage()
				+ " - "+ HttpURLConnection.HTTP_NOT_FOUND,true);
				System.out.println("WARNING --- Bad response received for the link -- "+url+" --- BROKEN LINK .. NEED YOUR ACTION -->>> Got the Response code: "+httpURLConnect.getResponseCode()+" - "+httpURLConnect.getResponseMessage()
						+ " - "+ HttpURLConnection.HTTP_NOT_FOUND);
				org.testng.Assert.fail(url+" ---->>> Response code: "+httpURLConnect.getResponseCode()+" - "+httpURLConnect.getResponseMessage() + " - "+ HttpURLConnection.HTTP_NOT_FOUND);

			}
		 }
		 
	}
}
