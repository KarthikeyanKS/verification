//		It compares MiTV with Caracol site and warns about the missing programs


package mslt.verification;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DirectTV_mitv_Integration  {
	public static WebDriver driver;
	private static final String PLAIN_ASCII =
		      "AaEeIiOoUu"    // grave
		    + "AaEeIiOoUuYy"  // acute
		    + "AaEeIiOoUuYy"  // circumflex
		    + "AaOoNn"        // tilde
		    + "AaEeIiOoUuYy"  // umlaut
		    + "Aa"            // ring
		    + "Cc"            // cedilla
		    + "OoUu"          // double acute
		    ;

		    private static final String UNICODE =
		     "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"             
		    + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD" 
		    + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177" 
		    + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
		    + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF" 
		    + "\u00C5\u00E5"                                                             
		    + "\u00C7\u00E7" 
		    + "\u0150\u0151\u0170\u0171" 
		    ;
		    
	String baseURL;
	List<String> time = new ArrayList<String>();
	List<String> title = new ArrayList<String>();
	List<WebElement> timeElements = new ArrayList<WebElement>();
	List<WebElement> titleElements = new ArrayList<WebElement>();

	HashMap<Integer,List<String>> mapBroadcasts = new HashMap<Integer,List<String>>();

	public String[] eventIds={},eventDates={},eventsToCompare={},datesToCompare={};
	GetArray g = new GetArray();

	ArrayList<String> diff = new ArrayList<String>() ;
		
	Gson gson = new Gson();
	int countToRemove= 0, countToAdd = 0,count=0;

	@Test(dataProvider = "channelSupplier")
	  public void extractFromDirectTv(String channel) throws InterruptedException, ParseException, JSONException {
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//Get the channel list from DirectTV
		driver.get("http://www.directv.com.co/guia/guia.aspx?type=&link=nav");
		driver.manage().window().maximize();

		datesToCompare = getTimes(0);
		eventsToCompare = getTimes(1);
		WebElement e = driver.findElement(By.id(channel));
		String channelName = e.getAttribute("name");
		driver.get("http://www.directv.com.co/guia/ChannelDetail.aspx?id="+channel+"&name="+e.getAttribute("name")+"&l="+e.getAttribute("l"));
		
		Select dateSelect = new Select(driver.findElement(By.id("ctl10_drpDays")));
		List<WebElement> selectOptions = dateSelect.getOptions();
		
		int iter = 0;
		for (WebElement temp : selectOptions){
			dateSelect.selectByIndex(iter);
			Thread.sleep(2000);

			timeElements = driver.findElements(By.xpath("//*[contains(@id,'liTime')]"));
			titleElements = driver.findElements(By.xpath("//*[contains(@id,'liTitle')]"));
			
			int count=0;
			for (WebElement temptime : timeElements){
				
				time.add(temptime.getText());
				title.add(titleElements.get(count).getText());
				
//				Locale l=new Locale("es", "CO");
				String str = convertNonAscii(titleElements.get(count).getText());
				
//				String str = titleElements.get(count).getText().format(l, "UTF-8");
//				System.out.println("str...: "+str);
//				datesToCompare = getTimes(0);
//				eventsToCompare = getTimes(1);
				compare(iter,temptime.getText(),datesToCompare,channel, str,eventsToCompare,channelName);
				count++;
			}
			
			mapBroadcasts.put(iter,time);
			time = new ArrayList<String>();
			
			iter++;
		}	
			
//		System.out.println(channel+" : "+mapBroadcasts.toString());		
		driver.navigate().back();
		Thread.sleep(4000);	
		
	}

	public static String convertNonAscii(String s) {
	       if (s == null) return null;
	       StringBuilder sb = new StringBuilder();
	       int n = s.length();
	       for (int i = 0; i < n; i++) {
	          char c = s.charAt(i);
	          int pos = UNICODE.indexOf(c);
	          if (pos > -1){
	              sb.append(PLAIN_ASCII.charAt(pos));
	          }
	          else {
	              sb.append(c);
	          }
	       }
	       return sb.toString();
	    }
	
	
	public void compare(int iter, String temptime,String[] datesToCompare, String channel, String title,String[] eventsToCompare, String channelName) throws ParseException, JSONException{
		
		String broadcasttime = temptime;
//		System.out.println(temptime);
	    int ind = temptime.lastIndexOf(".");
	    temptime = new StringBuilder(temptime).replace(ind, ind+1,"").toString();
	    ind = temptime.lastIndexOf(".");
	    temptime = new StringBuilder(temptime).replace(ind, ind+1,"").toString();
//	    System.out.println(temptime);
		
	    temptime = ("00000000" + temptime).substring(temptime.length());
	    
	    SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
	    SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm:ss"); 
	    String time24 = outFormat.format(inFormat.parse(temptime));
//	    System.out.println("24hr:-- "+time24);
	  
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'z'"); //.withZone(DateTimeZone.forID("America/Bogota"));;	
		DateTime present = DateTime.now();
		
		String customDtime;
		DateTime dt = new DateTime();
		customDtime = present.getYear()+"-"+present.getMonthOfYear()+"-"+present.getDayOfMonth()+"T"+time24+"z";
		dt = new DateTime(fmt.parseDateTime(customDtime));
		dt = dt.plusDays(iter);
//		System.out.println("dt--- "+dt);
		
		//comparing direct tv broadcast with event dates minus 1 hr
		
		for(int i =0;i<datesToCompare.length;i++)
		{
			//compare the dates.. Condition: 1.difference>=-60 && difference<=0 .. if true then save the dt, broadcasttime
			
			DateTime dateToCompare = new DateTime(fmt.parseDateTime(datesToCompare[i]));
			Minutes mins = Minutes.minutesBetween(dateToCompare, dt);
//			Hours hrs = Hours.hoursBetween(dt, dateToCompare);
//			double difference = hrs.getHours();
			double difference = mins.getMinutes();
//			System.out.println("mins --- "+mins);
			if (difference>=-315 && difference<=-300 ) {
//			System.out.println("Channel:- "+channel+"  Diff mins ---> "+difference+"  for date:--->  "+dt+
//					"  which has the title:  "+title+"   matches with the event: "+eventsToCompare[i]);
//			System.out.println("difference: mins"+(difference+300));
//			System.out.println("EventId: "+eventsToCompare[i]);
//			System.out.println("channel: id: "+channel+"   name: "+channelName);
//			System.out.println("Program: beginTime: "+dt+"   title: "+title+"\n");
			
//			Response response = RestAssured.post()
			
			JSONObject event = new JSONObject();
			event.put("eventId", eventsToCompare[i]);
			event.put("programTitle", title);
			event.put("beginTime", dt);
			event.put("channelId", "co_erik_custom_directtv_600");
			event.put("channelName", "DirecTV");
			event.put("channelLogoSmall", "http://images.mi.tv/channels/directv-logo_s.jpg");
			event.put("channelLogoMedium", "http://images.mi.tv/channels/directv-logo_m.jpg");
			event.put("channelLogoLarge", "http://images.mi.tv/channels/directv-logo_l.jpg");
	
			
			String requestBody = event.toString();
			System.out.println(requestBody);
			
			RestAssured.responseContentType("application/json");
		    Response response = RestAssured.given().contentType("application/json").and().body(requestBody).post("/sports/eventbroadcast/external?token=f4c988ac-461c-4325-a1c6-714ce79ca0ae");
			System.out.println("response status : "+response.getStatusCode());
			}
		}
		
	}
	
	
	

	public String[] getTimes(int flag) throws ParseException{
		//getting Events
				Response response = RestAssured.get("/sports/competitions/17694/events");	
				JsonPath jsonpath = new JsonPath(response.asString());
				String eventId = jsonpath.getString("eventId");
				String eventDate = jsonpath.getString("eventDate");
				
				eventDates = g.getArray(eventDate);
				
				String returnThisArray[] = new String[eventDates.length];
				
				if(flag == 0){
					for(int i = 0;i<eventDates.length;i++){
//						System.out.println("Original DateTime-- "+eventDates[i]);
						
					    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'z'");
						DateTime dt = new DateTime(fmt.parseDateTime(eventDates[i]));
//					    System.out.println("formated datetime:- "+dt.toString(fmt));
					    DateTime dtToCompare = dt; //.minusHours(1);
					    returnThisArray[i] = dtToCompare.toString(fmt);
					    
//						System.out.println("one hour minus event dateTime:- "+dtsToCompare[i]);
					}
				}
				else if(flag == 1){
					returnThisArray = g.getArray(eventId);
				}
				return returnThisArray;
				
	}
	
	@DataProvider(name = "channelSupplier", parallel = false)
	public Object[][] data() throws Exception {
		Object[][] retObjArr={{"600"},{"610"},{"613"},{"614"},{"615"},{"616"}};
		return(retObjArr);
	}

	@Parameters({ "url" })
	@BeforeClass											
	  public void runBeforeAllTests(String url) throws MalformedURLException{    
		baseURL = url; //"http://api.mi.tv";
		RestAssured.baseURI = url; // "http://api.mi.tv";
		RestAssured.port = 80;
//		driver = new FirefoxDriver();
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		driver = new RemoteWebDriver(new URL("http://192.168.2.202:4444/wd/hub"), capability);
		capability.setJavascriptEnabled(true);
		capability.setBrowserName("firefox"); 
		capability.setVersion("28.0");
//		Locale list[] = DateFormat.getAvailableLocales();
//        for (Locale aLocale : list) {
//            System.out.println(aLocale.toString());
//        }
	}

	@AfterClass
	  public void teardown() {
		driver.quit();
	  }
	}