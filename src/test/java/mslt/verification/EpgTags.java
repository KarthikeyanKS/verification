// 				Validates /api/epg/tags/visible

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

public class EpgTags extends RestAssured {
	int count = 0;
	@Test
	  public void channelsDefault() throws JSONException, IOException{              
		  Response response = get("/api/epg/tags/visible");	
		  
		  JsonPath jsonpath = new JsonPath(response.asString());
		  String id = jsonpath.getString("id");
		  String displayName = jsonpath.getString("displayName");

		  Gson gson = new Gson();
		  List<EpgTagsUtil> chan = gson.fromJson(response.asString(), new TypeToken<List<EpgTagsUtil>>(){}.getType());
		  
		  System.out.println("\n----api Tags: Total tags: "+chan.size());
		 
		  // validations
		  if(!(response.statusCode()==200)){
			  System.out.println("Reason for failure: unexpected status code");
			  count++;
		  }
		  if(!(response.asString().contains("Sport"))){
			  System.out.println("Reason for failure: Tag ID Sport not present");
			  count++;
		  }
		  if(!(response.asString().contains("Series"))){
			  System.out.println("Reason for failure: Dispaly Name Series not present"); 
			  count++;
		  }
		  if(id.isEmpty() || displayName.isEmpty()){
			  System.out.println("Reason for failure: No value in id or displayname");  
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


