package mslt.verification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class BroadcastCheckUtil {
	String linkValidate;Long tmp = null,diff;int count;
	double difference  = 0;
	int countLongSynopsis = 0;
	
	public int linkValidate(String channelTitle) throws InterruptedException, ParseException {
    	scrolldown();
		BroadcastCheck.driver.findElement(By.cssSelector("img[alt='"+channelTitle+"']")).click();
    	Thread.sleep(1000);
    	String homeurl = BroadcastCheck.driver.getCurrentUrl();
    	
    	String  links; int j = 0;count = 0;diff = (long) 0;
    	List<WebElement> linksinpage = BroadcastCheck.driver.findElements(By.cssSelector(".broadcast-link.next"));
    	
    	// validates for a minimum of 7 broadcasts in a channel
    	if(linksinpage.size()<7){
			count++;
			System.out.println("::: Only "+(linksinpage.size()+1)+" broadcasts scheduled for the channel "+channelTitle);
    		Reporter.log("Only "+(linksinpage.size()+1)+" broadcasts listed");
    	}
 
    	long[] begintime = new long[linksinpage.size()];
    	String[] startTime = new String[linksinpage.size()];
    	String[] endTime = new String[linksinpage.size()];

    	
    	for(WebElement templink: linksinpage){
    		links = (templink.getAttribute("href"));
    		links = links.substring(links.lastIndexOf("/")+1);
    		begintime[j] = Long.parseLong(links);  
    		
    		templink.click();
    		String tempTime = null;
    		List<WebElement> channels = BroadcastCheck.driver.findElements(By.cssSelector(".table-cell.broadcast-channel"));
    		List<WebElement> times = BroadcastCheck.driver.findElements(By.cssSelector(".table-cell.broadcast-timestamp"));
    		int tempcounter=0;
    		for(WebElement temp:channels){
//    			System.out.println("title in events page : "+temp.findElement(By.tagName("img")).getAttribute("title").toString());
        		if(temp.findElement(By.tagName("img")).getAttribute("title").toString().contains(channelTitle)){
            			System.out.println("time "+times.get(tempcounter).findElement(By.cssSelector(".time")).getText());
            			tempTime = times.get(tempcounter).findElement(By.cssSelector(".time")).getText();
        		}
        		tempcounter++;
        	}
    		
//    		String tempTime = BroadcastCheck.driver.findElement(By.cssSelector(".time")).getText();
//    		System.out.println(tempTime);
    		 
    		String t = tempTime.substring(tempTime.lastIndexOf(",")+1);
//    		System.out.println(t);
    		try{
    		String[] temp = t.split("-");
    		startTime[j] = temp[0].trim();
    		endTime[j] = temp[1].trim();
    		}catch(Exception e){
    			System.out.println(e);
    		}
//    		System.out.println(startTime[j]);
//    		System.out.println(endTime[j]);
    		
    		// Validating synopsis
			String longSynopsis = null;
			longSynopsis = BroadcastCheck.driver.findElement(By.cssSelector(".body")).findElement(By.cssSelector(".summary")).getText();
			if(longSynopsis.isEmpty()){
				System.out.println("::: Long synopsis of the broadcast is empty @ broadcast details page "+BroadcastCheck.driver.getCurrentUrl());
				countLongSynopsis++;
				count++;
			}
    		
    		BroadcastCheck.driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
    		j++;
    	 }
    	
		Thread.sleep(2000);
    	
    	for(int i = 0; i<startTime.length-1;i++){
    		
    		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm"); //.withZone(DateTimeZone.forID("America/Bogota"));;	
    		DateTime present = DateTime.now();
    		
    		String customBegintime, customEndTime;
    		customEndTime = present.getYear()+"-"+present.getMonthOfYear()+"-"+present.getDayOfMonth()+"T"+endTime[i];
    		customBegintime = present.getYear()+"-"+present.getMonthOfYear()+"-"+present.getDayOfMonth()+"T"+startTime[i+1];

    		DateTime end = new DateTime(fmt.parseDateTime(customEndTime));
    		DateTime start = new DateTime(fmt.parseDateTime(customBegintime));

			Minutes mins = Minutes.minutesBetween(start, end);
			difference = mins.getMinutes();
//			System.out.println("Broadcast difference: "+difference);
    		
			if(difference>0){
				System.out.println("::: Time gap between broadcasts in mins: "+difference+" for channel: "+channelTitle);
				count++;
			}
    	}
    
//    	for(int i = 0; i<begintime.length;i++){
//    		if(i==0){
//    			tmp = begintime[i];
//        	}else {
//        		diff = begintime[i] - tmp;
////        		System.out.println("Broadcast Time interval : "+diff); 
////        		4hrs - 4 x 60 x 60 x 1000 = 14400000
//        		if(diff>14400000){
//            		System.out.println("Reason : Time interval is more than 4 hours between Broadcasts for the channel "+channelTitle);
//            		Reporter.log("Time interval is more than 4 hours between Broadcasts");
//            		diff = (long) 0;
//            		count++;
//            	}
//        		tmp = begintime[i];
//        	}	
//    	}
		    	
    	if(count>0) System.out.println("::: verify here -> "+homeurl);

		return count;
	}
	
	public void scrolldown() throws InterruptedException {
		JavascriptExecutor jse = (JavascriptExecutor)BroadcastCheck.driver;
		 for (int second = 0;; second++) {
            if(second >=4){
                break;
            }
            jse.executeScript("window.scrollBy(0,2000)", "");
            Thread.sleep(1000);
		 } 
	}

}
