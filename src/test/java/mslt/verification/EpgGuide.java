//				Validating the api Guide - for 7 days x 98 channels = 686 scenarios
// 				/api/epg/guide/{date}?channelId={channelId}

package mslt.verification;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.json.JSONException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class EpgGuide extends RestAssured {
	
	Gson gson = new Gson();

	@Test
	  public void guide() throws JSONException, IOException, AssertionFailedError{              
		 	 
		List<EpgChannelsUtil> chan = gson.fromJson(get("/api/epg/channels").asString(), new TypeToken<List<EpgChannelsUtil>>(){}.getType());
		
		String[] channelId= new String[chan.size()];
		
		for(int i=0;i<chan.size();i++){	  
			channelId[i] = chan.get(i).getChannelId();
		}
		
		List<EpgDatesUtil> dates = gson.fromJson(get("/api/epg/dates").asString(), new TypeToken<List<EpgDatesUtil>>(){}.getType());
		String[] date= new String[dates.size()];
		for(int i=0;i<dates.size();i++){	  
			date[i] = dates.get(i).getDate();
		}
		
		int count = 0;
		System.out.println("\nValidating api Guide - for 7 days x 98 channels = 686 scenarios");
		for(String dt: date){
			System.out.println("*****for the date:  "+dt);
			for(String chanId:channelId){
				
				Response response = get("/api/epg/guide/"+dt+"?channelId="+chanId);
				
				  JsonPath jsonpath = new JsonPath(response.asString());
				  String name = jsonpath.getString("name");
				  String logo_small = jsonpath.getString("logo.small");
				  String broadcasts = jsonpath.getString("broadcasts");
				  String beginTimeMillis = jsonpath.getString("broadcasts.beginTimeMillis");
				  String program = jsonpath.getString("broadcasts.program");
				  String programId = jsonpath.getString("broadcasts.program.programId");
				  String programType = jsonpath.getString("broadcasts.program.programType");
				  String synopsisShort = jsonpath.getString("broadcasts.program.synopsisShort");
				  String images_landscape_small = jsonpath.getString("broadcasts.program.images.landscape.small");
				  String images_portrait_large = jsonpath.getString("broadcasts.program.images.portrait.large");
				  String tags = jsonpath.getString("broadcasts.program.tags");
				  
//				  validations 
				  if(!(response.statusCode()==200)){
					  System.out.println("Reason for failure: unexpected status code for the date "+dt+" , channel: "+chanId);
					  count++;
				  }
				  
				  if(!(broadcasts.contains("program"))){
					  System.out.println("Reason for failure: programId not present for  "+baseURI+"/api/epg/guide/"+dt+"?channelId="+chanId);
					  count++;
				  }
				  
				  if(name.isEmpty() || logo_small.isEmpty() || broadcasts.isEmpty() || beginTimeMillis.isEmpty() 
						  || program.isEmpty() || programId.isEmpty() || programType.isEmpty() || synopsisShort.isEmpty() 
						  || images_landscape_small.isEmpty() || images_portrait_large.isEmpty() || tags.isEmpty()){
					  System.out.println("Reason for failure: No value in channelId or channel name or logo");
					  count++;
				  }
			}		 
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


