package mslt.verification;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class EpgUtil extends RestAssured {
private String formateddate, programId, channelId,beginTimeMillis,seriesId,broadcastType,synopsisLong,episodeNumber;

public String getFormateddate() {
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.DATE, 0);
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	formateddate = format1.format(cal.getTime());
	return formateddate;
}

public void setFormateddate(String formateddate) {
	this.formateddate = formateddate;
}

public String getProgramId(String url) throws JSONException {
	Response response = get(url);
	JSONArray recvObj = new JSONArray(response.asString());
	String broadcasts = recvObj.getJSONObject(0).getString("broadcasts");
	JSONArray programObj = new JSONArray(broadcasts);
	String program =  programObj.getJSONObject(0).getString("program");
	programId = new JSONObject(program).getString("programId");
	return programId;
}

public void setProgramId(String programId) {
	this.programId = programId;
}

public String getChannelId() throws JSONException {
	Response response = get("/api/epg/channels");
	JSONArray recvObj = new JSONArray(response.asString());
	channelId = recvObj.getJSONObject(0).getString("channelId");
	return channelId;
}

public void setChannelId(String channelId) throws JSONException {
	this.channelId = channelId;
}

public String getBeginTimeMillis() throws JSONException {
	Response response = get("/api/epg/guide/"+formateddate+"?channelId="+channelId);
	JSONArray recvObj = new JSONArray(response.asString());
	String broadcasts = recvObj.getJSONObject(0).getString("broadcasts");
	JSONArray beginTimeObj = new JSONArray(broadcasts);
	beginTimeMillis = beginTimeObj.getJSONObject(0).getString("beginTimeMillis");
	return beginTimeMillis;
}

public void setBeginTimeMillis(String beginTimeMillis) {
	this.beginTimeMillis = beginTimeMillis;
}

public String getSeriesId() throws JSONException {
	Response response = get("/api/epg/channels/"+channelId+"/broadcasts/"+beginTimeMillis);
	JSONObject programObj = new JSONObject(response.asString());
    String program = programObj.getString("program");
	String seriesObj = new JSONObject(program).getString("series");
	seriesId = new JSONObject(seriesObj).getString("seriesId");
	return seriesId;
}

public void setSeriesId(String seriesId) {
	this.seriesId = seriesId;
}

public String getBroadcastType(String url) throws JSONException {
	Response response = get(url);
	JSONArray recvObj = new JSONArray(response.asString());
	broadcastType = recvObj.getJSONObject(0).getString("broadcastType");
	return broadcastType;
}

public void setBroadcastType(String broadcastType) {
	this.broadcastType = broadcastType;
}

public String getSynopsisLong() throws JSONException {
	Response response = get("/api/epg/channels/"+channelId+"/broadcasts/"+beginTimeMillis);
	JSONObject programObj = new JSONObject(response.asString());
    String program = programObj.getString("program");
	synopsisLong = new JSONObject(program).getString("synopsisLong");
	return synopsisLong;
}

public void setSynopsisLong(String synopsisLong) {
	this.synopsisLong = synopsisLong;
}

public String getEpisodeNumber() throws JSONException {
	Response response = get("/api/epg/series/"+seriesId+"/broadcasts/upcoming");
	JSONArray recvObj = new JSONArray(response.asString());
	String program = recvObj.getJSONObject(0).getString("program");
	episodeNumber = new JSONObject(program).getString("episodeNumber");
	return episodeNumber;
}

public void setEpisodeNumber(String episodeNumber) {
	this.episodeNumber = episodeNumber;
}

}
