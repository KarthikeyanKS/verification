//		It compares MiTV with Caracol site and warns about the missing programs


package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class MiTv_Caracol  {
	public static WebDriver driver;
	String baseURL;
	HashMap<Integer,ArrayList<String>> caracolMap = new HashMap<Integer,ArrayList<String>>();
	HashMap<Integer,ArrayList<String>> mitvMap = new HashMap<Integer,ArrayList<String>>();

	ArrayList<String> caracolTime = new ArrayList<String>();
	ArrayList<String> mitvTime = new ArrayList<String>();
	ArrayList<String> caracolSchedule = new ArrayList<String>();
	ArrayList<String> mitvSchedule = new ArrayList<String>();
	ArrayList<String> tempTimeFront = new ArrayList<String>();
	ArrayList<String> caraFinal = new ArrayList<String>();
	ArrayList<String> mitvFinal = new ArrayList<String>();
	ArrayList<String> diff = new ArrayList<String>() ;
	
	final Calendar c = Calendar.getInstance();
	
	Gson gson = new Gson();
	GetArray convArray = new GetArray();
	String[] dateIds={};String time[] = {}; String[] times={};
	int countToRemove= 0, countToAdd = 0,count=0;

	@Test
	  public void caracolMitvValidate() throws InterruptedException, ParseException {
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		// Get the broadcast list from caracol site
		int j = 0;	
		for(int i=1;i<=7;i++){
			driver.get("http://www.caracoltv.com/programacion?d="+i+"&t=n");
			if(i==1) driver.manage().window().maximize();
			List<WebElement> times = driver.findElements(By.className("horario"));
			for(WebElement time: times){		
				String str = time.getText();
				String formatted = ("00000000" + str).substring(str.length());
				caracolTime.add(formatted);
			}
			caracolMap.put(j, (ArrayList<String>) caracolTime);
			caracolTime = new ArrayList<String>();
			j++;
		}
		
		// Get the broadcast list from mitv for Caracol
		Response dateResponse = RestAssured.get("/api/epg/dates");
		JsonPath datePath = new JsonPath(dateResponse.asString());
		String dateId = datePath.getString("id");
		dateIds=convArray.getArray(dateId);
		
		
		j = 0;
		for(int i=0;i<=6;i++){	
			driver.get(baseURL+"/channels/co_8cec18c5-05e4-442f-bfea-1a7c55b72118/"+dateIds[i]);
			List<WebElement> onair = driver.findElements(By.cssSelector(".broadcast-link.on-air"));
			List<WebElement> nextList = driver.findElements(By.cssSelector(".broadcast-link.next"));
			
			List<WebElement> linksinpage = new ArrayList<WebElement>(onair);
			linksinpage.addAll(nextList);
	    	
		// convert mitv begin time from milliseconds into HH:MM a format
	        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
			String links;
	    	for(WebElement templink: linksinpage){
	    		links = (templink.getAttribute("href"));
	    		links = links.substring(links.lastIndexOf("/")+1);
	    		Date date = new Date(Long.parseLong(links));
	    		mitvTime.add(dateFormat.format(date));
	    		
	    	 }  	
	    	
	    	mitvMap.put(j, (ArrayList<String>) mitvTime);
			mitvTime = new ArrayList<String>();
			j++;
		}
		
		// As caracol broadcast starts from Monday to Sunday, it removes the previous broadcast list based on the current day.
	    int today = (c.get(Calendar.DAY_OF_WEEK));
		int caracolday = 0;
		
		if(today==1) caracolday = 7;
	    else caracolday = today -1;
	         
		switch (caracolday) {
	      case 1:  	break;
	      case 2: 	caracolMap.remove(0);
	      			break;
	      case 3: 	for(int i=0;i<=1;i++) 		caracolMap.remove(i);
	               	break;
	      case 4:  	for(int i=0;i<=2;i++) 		caracolMap.remove(i);
	      			break;
	      case 5:  	for(int i=0;i<=3;i++) 		caracolMap.remove(i);			    
			         	break;
	      case 6: 	for(int i=0;i<=4;i++) 		caracolMap.remove(i);
			         	break;
	      case 7: 	for(int i=0;i<=5;i++) 		caracolMap.remove(i);
	      			break;                
	      }
	
		// As mitv broadcast lists for 7 days from current day, it removes and keeps the broadcast list until sunday.
		switch (caracolday) {
	      case 1:  	break;
	      case 2: 	mitvMap.remove(6);
	      			break;
	      case 3: 	for(int i=5;i<=6;i++) 		mitvMap.remove(i);
	               	break;
	      case 4:  	for(int i=4;i<=6;i++) 		mitvMap.remove(i);
	      			break;
	      case 5:  	for(int i=3;i<=6;i++) 		mitvMap.remove(i);			    
			         	break;
	      case 6: 	for(int i=2;i<=6;i++) 		mitvMap.remove(i);
			         	break;
	      case 7: 	for(int i=1;i<=6;i++) 		mitvMap.remove(i);
	      			break;                
	      }
	
		for(int i=6;i>=caracolday-1;i--){
			ArrayList<String> cday = caracolMap.get(i);
			int k=0;
			while(countToAdd>0)
	        {	
	    	cday.add(tempTimeFront.get(k));
	    	k++;
	    	countToAdd--;
	    	}
			tempTimeFront.clear();
			
			
			// As caracol lists the broadcasts from 5:00 AM, we at mitv list the same from 6:00 AM, some logical alterations are done here to remove the broadcast from next day and appending that to present day at the end of the list.
	
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
	    	String validatetime = "6:00 AM";
	        Date validate = sdf.parse(validatetime);
			
			for(String s:cday){
				Date date = sdf.parse(s);
    	        if(date.getTime()>=39600000) break;        // breaks the loop when it reaches 6:00 AM 
    	        else countToRemove++;
			
				if(date.getTime()<validate.getTime()){	   
	        		tempTimeFront.add(s);
	        		countToAdd++;
				}
			}
			
			while(countToRemove>0)
	        {	
	    	cday.remove(0);
	    	countToRemove--;
	    	}
		}
	
		// Comparing both the lists from caracol with mitv and warns about the difference.
		j=0;
		for(int i=caracolday-1;i<7;i++) {
			caraFinal = caracolMap.get(i);
			mitvFinal = mitvMap.get(j);  
			
			List<String> CaraMinusMitv = new ArrayList<String>(caraFinal);  
			CaraMinusMitv.removeAll(mitvFinal);  
			  
			System.out.println("\nDay : "+(j+1)+"  diff with caracol");
			for(String diff : CaraMinusMitv){	
				System.out.println("Reason for failure: There is a difference in the broadcast for the day: "+(j+1)+" - "+diff);  
				count++;
			}
			System.out.println("***************************");
			
			
//		    Boolean isFound = false;
//		    for(i=0;i<caraFinal.size();i++){
//		    	isFound = false;
//		    	for(j=0;j<mitvFinal.size();j++) if(caraFinal.get(i).equals(mitvFinal.get(j))) isFound = true;
//		    	
//		    	if(!isFound){
//		    		String temp = caraFinal.get(i);
//		    		diff.add(temp);
////		    		System.out.println(caraFinal.get(i));
//		    	}
//	    		System.out.println(diff.toString());
//				diff = new ArrayList<String>();
//		    }
			
			j++;
		}
		
		Assert.assertTrue(count==0);	
	}


	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException{    
		baseURL = url;		       //"http://mi.tv";
		RestAssured.baseURI = url; // "http://mi.tv";
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