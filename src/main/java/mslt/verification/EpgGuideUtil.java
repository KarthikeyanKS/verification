package mslt.verification;

import java.util.ArrayList;

public class EpgGuideUtil {
	
	private String channelId,name;
	private ArrayList<BroadcastsUtil> broadcasts = new ArrayList<BroadcastsUtil>();;
	private Logo logo;
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<BroadcastsUtil> getBroadcasts() {
		return broadcasts;
	}
	public void setBroadcasts(ArrayList<BroadcastsUtil> broadcasts) {
		this.broadcasts = broadcasts;
	}
	public Logo getLogo() {
		return logo;
	}
	public void setLogo(Logo logo) {
		this.logo = logo;
	}
	public class Logo{

		private String small,medium,large;

		public String getSmall() {
			return small;
		}

		public void setSmall(String small) {
			this.small = small;
		}

		public String getMedium() {
			return medium;
		}

		public void setMedium(String medium) {
			this.medium = medium;
		}

		public String getLarge() {
			return large;
		}

		public void setLarge(String large) {
			this.large = large;
		} 
	}
}

