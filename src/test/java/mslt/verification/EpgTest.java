package mslt.verification;

import java.text.ParseException;


import org.json.*;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class EpgTest extends RestAssured {
	
	EpgUtil util = new EpgUtil();

	@Test
	  public void channelsDefault() throws JSONException{              
		  Response response = get("/api/epg/channels/default");	  	  
		  assert(response.asString().contains("channelId"));
	  }
	  
	  @Test
	  public void channels() throws JSONException{   
		  Assert.assertNotNull(util.getChannelId()); 
	  }
	  
	  @Test
	  public void dates() throws ParseException, JSONException{                    
		  Response response = get("/api/epg/dates");
		  assert(response.asString().contains(util.getFormateddate()));
	  }
	  
	  @Test
	  public void tags(){                       
		  Response response = get("/api/epg/tags/visible");
		  assert(response.asString().contains("Movies"));
	  }

	  @Test (dependsOnMethods = { "dates","channels"})
	  public void guide() throws JSONException {          
		  Assert.assertNotNull(util.getBeginTimeMillis());
	  }

	  @Test 
	  public void programBroadcasts() throws JSONException{  
		  
		  String url = "/api/epg/guide/"+util.getFormateddate()+"?channelId="+util.getChannelId();
		  url = "/api/epg/programs/"+util.getProgramId(url)+"/broadcasts";
		  Assert.assertNotNull(util.getBroadcastType(url));
		   
	  }
	  
	  @Test (dependsOnMethods = { "guide"})
	  public void detailedBroadcasts() throws JSONException { 
	  
//	      http://www.gitrgitr.com/api/epg/channels/0524ecb4-aac9-42a5-aa71-23fe6eb777d8/broadcasts/1380962800000 
//		  Validates synopsisLong  from the response
		  Assert.assertNotNull(util.getSynopsisLong());
	  }
	  
	  @Test (dependsOnMethods = { "guide"})
	  public void upcomingBroadcasts() throws JSONException { 
	  
//	      http://www.gitrgitr.com/api/epg/series/{series}/broadcasts/upcoming 
		  util.getSeriesId();
//		  Validates episode number  from the response
		  Assert.assertNotNull(util.getEpisodeNumber());
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
//	                                                         @Parameters({ "url" })  
	@BeforeMethod
	  public void runBeforeAllTests(){                         //(String url) {
		RestAssured.baseURI = "http://www.mi.tv";       // url;
		RestAssured.port = 80;
	}

	  @AfterMethod
	  public void teardown() {

	  }
	}