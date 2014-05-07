// 				Validates /api/sports/competitions

package mslt.verification;
import java.io.IOException;

import org.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ObjectArrays;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class Sports extends RestAssured {
	int count = 0;
	public String[] competitionIds={}, phaseIds={},eventIds={},teamIds={};
	@Test
	  public void sportsCompetitions() throws JSONException, IOException{              
		  Response response = get("/api/sports/competitions");	
		  JsonPath jsonpath = new JsonPath(response.asString());
		  String competitionId = jsonpath.getString("competitionId");
		  String startDate = jsonpath.getString("startDate");
		  String endDate = jsonpath.getString("endDate");
		  String visible = jsonpath.getString("visible");
		  String displayName = jsonpath.getString("displayName");
		  
		  GetArray g = new GetArray();
		  competitionIds = g.getArray(competitionId);
		  // validations
		  System.out.println("\n* Validating api Competitions");
		  if(!(response.statusCode()==200)){
			  System.out.println("Reason for failure: unexpected status code");
			  count++;
		  }
		  if(competitionId.isEmpty() 
				  || startDate.isEmpty() || endDate.isEmpty() || visible.isEmpty() || displayName.isEmpty()){
			  System.out.println("Reason for failure: No value in Competition Id or " +
			  		"startDate or endDate or visible or displayName"+baseURI+"/api/sports/competitions");	
			  count++;
		  }
		  Assert.assertTrue(count==0);
	  }
	
	@Test(dependsOnMethods = { "sportsCompetitions"})
	  public void sportsCompetition() throws JSONException, IOException{              
		  
		System.out.println("\n* Validating api Competition");
		for(String cId:competitionIds){
			System.out.println("checking the competition: "+cId);
			  Response response = get("/api/sports/competitions/"+cId);
			  JsonPath jsonpath = new JsonPath(response.asString());
			  String compId = jsonpath.getString("competitionId");
			  String startDate = jsonpath.getString("startDate");
			  String endDate = jsonpath.getString("endDate");
			  String visible = jsonpath.getString("visible");
			  String displayName = jsonpath.getString("displayName");
			  
			  if(!(response.statusCode()==200)){
				  System.out.println("Reason for failure: unexpected status code");
				  count++;
			  }
			  if(compId.isEmpty() 
					  || startDate.isEmpty() || endDate.isEmpty() || visible.isEmpty() || displayName.isEmpty()){
				  System.out.println("Reason for failure: No value in Competition Id or startDate " +
				  		"or endDate or visible or displayName"+baseURI+"/api/sports/competitions/"+cId);
				  count++;
			  }
		}
		  Assert.assertTrue(count==0);
	  }
	
	
	@Test(dependsOnMethods = { "sportsCompetition"})
	  public void sportsPhases() throws JSONException, IOException{              
		  
		System.out.println("\n* Validating api Phases");
		for(String cId:competitionIds){
			System.out.println("checking phases for the competition: "+cId);
			Response response = get("/api/sports/competitions/"+cId+"/phases");	
			JsonPath jsonpath = new JsonPath(response.asString());
			String competitionId = jsonpath.getString("competitionId");
			String phaseId = jsonpath.getString("phaseId");
			String phase = jsonpath.getString("phase");
			String stage = jsonpath.getString("stage");
			String table = jsonpath.getString("table");
			String current = jsonpath.getString("current");
			String currents = jsonpath.getString("currents");
			String dateStart = jsonpath.getString("dateStart");
			String dateEnd = jsonpath.getString("dateEnd");
			
			GetArray g = new GetArray();
			
			if(phaseIds.length==0) phaseIds =g.getArray(phaseId);
			else phaseIds = ObjectArrays.concat(phaseIds, g.getArray(phaseId),String.class);
			
			// validations 
			  if(!(response.statusCode()==200)){
				  System.out.println("Reason for failure: unexpected status code");
				  count++;
			  }
			  if(competitionId.isEmpty() 
					  || phaseId.isEmpty() || phase.isEmpty() || stage.isEmpty() || table.isEmpty() 
					  || current.isEmpty() || currents.isEmpty() || dateStart.isEmpty() || dateEnd.isEmpty()){
				  System.out.println("Reason for failure: No value in either Competition Id or phaseId " +
				  		"or phase or stage or table or current or currents or dateStart " +
				  		"or dateEnd"+baseURI+"/api/sports/competitions/"+cId+"/phases");
				  count++;
			  }
		}
		  Assert.assertTrue(count==0);
	  }
	
	@Test(dependsOnMethods = { "sportsCompetition"})
	  public void sportsTeams() throws JSONException, IOException{              
		  
		System.out.println("\n* Validating api Teams");
		for(String cId:competitionIds){
			System.out.println("checking teams for the competition: "+cId);
			Response response = get("/api/sports/competitions/"+cId+"/teams");	
			JsonPath jsonpath = new JsonPath(response.asString());
			String teamId = jsonpath.getString("teamId");
			String displayName = jsonpath.getString("displayName");
			String nation = jsonpath.getString("nation");
			String nationCode = jsonpath.getString("nationCode");
			
			GetArray g = new GetArray();
			
			if(teamIds.length==0) teamIds =g.getArray(teamId);
			else teamIds = ObjectArrays.concat(teamIds, g.getArray(teamId),String.class);
			
			// validations 
			  if(!(response.statusCode()==200)){
				  System.out.println("Reason for failure: unexpected status code");
				  count++;
			  }
			  if(teamId.isEmpty() || displayName.isEmpty() || nation.isEmpty() || nationCode.isEmpty()){
				  System.out.println("Reason for failure: No value in either teamId or displayName " +
				  		"or nation or nationCode"+baseURI+"/api/sports/competitions/"+cId+"/teams");
				  count++;
			  }
		}
		  Assert.assertTrue(count==0);
	  }
	
	
	
	@Test(dependsOnMethods = { "sportsPhases"})
	  public void sportsEvents() throws JSONException, IOException{              
		  
		System.out.println("\n* Validating api Events");
		
		for(String cId:competitionIds){
		for(String pId:phaseIds){
			Response response = get("/api/sports/phases/"+pId+"/events");	
			JsonPath jsonpath = new JsonPath(response.asString());
			String eventId = jsonpath.getString("eventId");
			String competitionId = jsonpath.getString("competitionId");
			String eventDate = jsonpath.getString("eventDate");
			String rescheduled = jsonpath.getString("rescheduled");
			String phaseId = jsonpath.getString("phaseId");
			String homeTeamId = jsonpath.getString("homeTeamId");
			String homeTeam = jsonpath.getString("homeTeam");
			String awayTeamId = jsonpath.getString("awayTeamId");
			String awayTeam = jsonpath.getString("awayTeam");
			String stadiumId = jsonpath.getString("stadiumId");
			String stadium = jsonpath.getString("stadium");
			String cityId = jsonpath.getString("cityId");
			String city = jsonpath.getString("city");
			String country = jsonpath.getString("country");
			String homeGoals = jsonpath.getString("homeGoals");
			String awayGoals = jsonpath.getString("awayGoals");
			String homeGoalsHalfTime = jsonpath.getString("homeGoalsHalfTime");
			String awayGoalsHalfTime = jsonpath.getString("awayGoalsHalfTime");
			String matchStatusId = jsonpath.getString("matchStatusId");
			String matchStatus = jsonpath.getString("matchStatus");
			String postponed = jsonpath.getString("postponed");
			String finished = jsonpath.getString("finished");
			String abandoned = jsonpath.getString("abandoned");
			String awarded = jsonpath.getString("awarded");
			String live = jsonpath.getString("live");
			String refereeId = jsonpath.getString("refereeId");
			String referee = jsonpath.getString("referee");
			String refereeNation = jsonpath.getString("refereeNation");
			String dataEntryLiveScore = jsonpath.getString("dataEntryLiveScore");
			String dataEntryLiveGoal = jsonpath.getString("dataEntryLiveGoal");
			String dataEntryLiveLineUp = jsonpath.getString("dataEntryLiveLineUp");
			
			GetArray g = new GetArray();
			if(eventIds.length==0) eventIds =g.getArray(eventId);
			else eventIds = ObjectArrays.concat(eventIds, g.getArray(eventId),String.class);
			
			// validations 
			  if(!(response.statusCode()==200)){
				  System.out.println("Reason for failure: unexpected status code");
				  count++;
			  }
			  if(eventId.isEmpty() || competitionId.isEmpty() || eventDate.isEmpty() || rescheduled.isEmpty() || phaseId.isEmpty() 
					  || homeTeamId.isEmpty() || homeTeam.isEmpty() || awayTeamId.isEmpty() || awayTeam.isEmpty() || stadiumId.isEmpty() 
					  || stadium.isEmpty() || cityId.isEmpty() || city.isEmpty() || country.isEmpty() || homeGoals.isEmpty() || awayGoals.isEmpty() 
					  || homeGoalsHalfTime.isEmpty() || awayGoalsHalfTime.isEmpty() || matchStatusId.isEmpty() || matchStatus.isEmpty() 
					  || postponed.isEmpty() || finished.isEmpty() || abandoned.isEmpty() || awarded.isEmpty() || live.isEmpty() 
					  || refereeId.isEmpty() || referee.isEmpty() || refereeNation.isEmpty() || dataEntryLiveScore.isEmpty() 
					  || dataEntryLiveGoal.isEmpty() || dataEntryLiveLineUp.isEmpty()){
				  System.out.println("Reason for failure: No value in any one of the field in events: "+baseURI+"/api/sports/phases/"+pId+"/events");
				  count++;
			  }
		}
		}
//		System.out.println("\nThere are "+competitionIds.length+" competitions happening in "
//				+phaseIds.length+" phases with "+eventIds.length+" events in total and with "+teamIds.length+" teams");
		  Assert.assertTrue(count==0);
	  }
	
	
	@Test(dependsOnMethods = { "sportsCompetition"})
	  public void sportsStandings() throws JSONException, IOException{              
		  
		System.out.println("\n* Validating api Standings");
		for(String pId:phaseIds){
			System.out.println("checking standings for the phaseId: "+pId);
			Response response = get("/api/sports/phases/"+pId+"/standings");	
			JsonPath jsonpath = new JsonPath(response.asString());
			String _id = jsonpath.getString("_id");
			String competitionId = jsonpath.getString("competitionId");
			String phaseId = jsonpath.getString("phaseId");
			String phase = jsonpath.getString("phase");
			String team = jsonpath.getString("team");
			String teamId = jsonpath.getString("teamId");
			String rank = jsonpath.getString("rank");
			String matches = jsonpath.getString("matches");
			String matchesWon = jsonpath.getString("matchesWon");
			String matchesLost = jsonpath.getString("matchesLost");
			String matchesDrawn = jsonpath.getString("matchesDrawn");
			String points = jsonpath.getString("points");
			String goalsFor = jsonpath.getString("goalsFor");
			String goalsAgainst = jsonpath.getString("goalsAgainst");
			
			// validations 
			  if(!(response.statusCode()==200)){
				  System.out.println("Reason for failure: unexpected status code");
				  count++;
			  }
			  if(_id.isEmpty() || competitionId.isEmpty() || phaseId.isEmpty() || phase.isEmpty() || team.isEmpty() || teamId.isEmpty() 
					  || rank.isEmpty() || matches.isEmpty() || matchesWon.isEmpty()|| matchesLost.isEmpty() 
					  || matchesDrawn.isEmpty() || points.isEmpty() || goalsFor.isEmpty() || goalsAgainst.isEmpty()){
				  System.out.println("Reason for failure: No value in either _id or competitionId " +
				  		"or phaseId or phase or team or teamId or rank or matches or matchesWon or matchesLost or matchesDrawn " +
				  		"or points or goalsFor or goalsAgainst: "+baseURI+"/api/sports/phases/"+pId+"/standings");
				  count++;
			  }
		}
		  Assert.assertTrue(count==0);
	  }
	
	
	
	
	
@Parameters({ "url" })  
	@BeforeMethod
	  public void runBeforeAllTests(String url) {
		RestAssured.baseURI = url; //"http://gitrgitr.com"	
		RestAssured.port = 80;
	}

	@AfterMethod
	  public void teardown() {

	  }
	}


