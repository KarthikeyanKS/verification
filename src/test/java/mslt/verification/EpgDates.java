// 				Validates /api/epg/dates

package mslt.verification;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class EpgDates extends RestAssured {
	int count = 0;
	@Test
	  public void channelsDefault() throws JSONException, IOException{              
		  Response response = get("/api/epg/dates");	
		  
		  JsonPath jsonpath = new JsonPath(response.asString());
		  String id = jsonpath.getString("id");
		  String date = jsonpath.getString("date");
		  String displayName = jsonpath.getString("displayName");
		  System.out.println("\14.api Dates");
		  
		  Calendar cal = Calendar.getInstance();
		  cal.add(Calendar.DATE, 0);
		  SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		  String formateddate = format1.format(cal.getTime());
		  
		  // validations
		  if(!(response.statusCode()==200)){
			  System.out.println("Reason for failure: unexpected status code");
			  count++;
		  }
		  if(!(response.asString().contains("id"))){
			  System.out.println("Reason for failure: Date ID Sport not present");
			  count++;
		  }
		  if(!(id.isEmpty() || date.isEmpty() || displayName.isEmpty() == false)){
			  System.out.println("Reason for failure: No value in id or date or displayname");
			  count++;
		  }
		  if(!(response.asString().contains(formateddate))){
			  System.out.println("Reason for failure: Date not present");
			  count++;
		  }
		  Assert.assertTrue(count==0);
	  }
	
	
	
	
	
	
	
	
@Parameters({ "url" })  
	@BeforeMethod
	  public void runBeforeAllTests(String url) {
		RestAssured.baseURI = url;
		RestAssured.port = 80;
	}

	@AfterMethod
	  public void teardown() {

	  }
	}


