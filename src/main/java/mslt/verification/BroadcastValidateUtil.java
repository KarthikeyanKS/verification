package mslt.verification;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class BroadcastValidateUtil {
	String linkValidate;Long tmp = null,diff;int count;

	public int linkValidate(String channelTitle) throws InterruptedException {
		BroadcastValidate.driver.findElement(By.cssSelector("img[alt='"+channelTitle+"']")).click();
    	Thread.sleep(1000);
    	String  links; int j = 0;count = 0;diff = (long) 0;
    	List<WebElement> linksinpage = BroadcastValidate.driver.findElements(By.cssSelector(".broadcast-link.next"));
    	
    	// validates for a minimum of 7 broadcasts in a channel
    	if(linksinpage.size()<7){
			count++;
			System.out.println("Only "+(linksinpage.size()+1)+" broadcasts listed");
    		Reporter.log("Only "+(linksinpage.size()+1)+" broadcasts listed");
    	}
 
    	long[] begintime = new long[linksinpage.size()];
    	
    	for(WebElement templink: linksinpage){
    		links = (templink.getAttribute("href")).substring(77,90);
    		begintime[j] = Long.parseLong(links);  
    		j++;
    	 }
    
    	for(int i = 0; i<begintime.length;i++){
    		if(i==0){
    			tmp = begintime[i];
        	}else {
        		diff = begintime[i] - tmp;
//        		System.out.println("Broadcast Time interval : "+diff); 
//        		4hrs - 4 x 60 x 60 x 1000 = 14400000
        		if(diff>14400000){
            		System.out.println("Time interval is more than 4 hours between Broadcasts");
            		Reporter.log("Time interval is more than 4 hours between Broadcasts");
            		diff = (long) 0;
            		count++;
            	}
        		tmp = begintime[i];
        	}	
    	}
    	Thread.sleep(1000);
    	BroadcastValidate.driver.navigate().back();
    	scrolldown();
    	
		return count;
	}
	
	public void scrolldown() throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor)BroadcastValidate.driver;
		 for (int second = 0;; second++) {
            if(second >=4){
                break;
            }
            jse.executeScript("window.scrollBy(0,2000)", "");
            Thread.sleep(1000);
		 } 
	}

}
