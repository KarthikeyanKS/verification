package mslt.verification;

public class BroadcastsUtil {
	
	private String beginTimeMillis,beginTime,endTime,broadcastType,shareUrl;
	private ProgramUtil program;
	public String getBeginTimeMillis() {
		return beginTimeMillis;
	}
	public void setBeginTimeMillis(String beginTimeMillis) {
		this.beginTimeMillis = beginTimeMillis;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getBroadcastType() {
		return broadcastType;
	}
	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public ProgramUtil getProgram() {
		return program;
	}
	public void setProgram(ProgramUtil program) {
		this.program = program;
	}
}

