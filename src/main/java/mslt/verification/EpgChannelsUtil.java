package mslt.verification;

public class EpgChannelsUtil {
    private String channelId;
    private String name;
    private Logo logo;
    public class Logo {
    	private String small;
    	private String medium;
    	private String large;
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
	public Logo getLogo() {
		return logo;
	}
	public void setLogo(Logo logo) {
		this.logo = logo;
	}
}

