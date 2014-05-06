// 				/api/epg/channels

package mslt.verification;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class EpgChannels extends RestAssured {
	int count = 0;
	@Test
	  public void channelsDefault() throws JSONException, IOException{              
		  Response response = get("/api/epg/channels");	
		  
		  JsonPath jsonpath = new JsonPath(response.asString());
		  String channelId = jsonpath.getString("channelId");
		  String name = jsonpath.getString("name");
		  String logoSmall = jsonpath.getString("logo.small");
		  String logoMedium = jsonpath.getString("logo.medium");
		  String logoLarge = jsonpath.getString("logo.large");
		
		  Gson gson = new Gson();
		  List<EpgChannelsUtil> chan = gson.fromJson(response.asString(), new TypeToken<List<EpgChannelsUtil>>(){}.getType());
		  
		  System.out.println("\n----api Channels: Total channels: "+chan.size());
		  
		  for(int i=0;i<chan.size();i++){
			  if(!(chan.get(i).getChannelId().contains("co_"))){
				  System.out.println("Reason for failure: Channel ID not present");
				  count++;
			  }
		  }
		  
		  if(!(response.statusCode()==200)){
			  System.out.println("Reason for failure: unexpected status code");
			  count++;
		  }
		  if(channelId.isEmpty() 
				  || name.isEmpty() || logoSmall.isEmpty() || logoMedium.isEmpty() || logoLarge.isEmpty()){
			  System.out.println("Reason for failure: No value in channelId or channel name or logo");
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
	  public void teardown() throws Throwable {
	  }
	}


