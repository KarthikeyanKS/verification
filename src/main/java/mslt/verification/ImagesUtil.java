package mslt.verification;

public class ImagesUtil {
	private Landscape landscape;
	private Portrait portrait;
	public Portrait getPortrait() {
		return portrait;
	}
	public void setPortrait(Portrait portrait) {
		this.portrait = portrait;
	}
	public class Landscape{
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
	public class Portrait{
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
	public Landscape getLandscape() {
		return landscape;
	}
	public void setLandscape(Landscape landscape) {
		this.landscape = landscape;
	}
}

