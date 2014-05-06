package mslt.verification;

import java.util.ArrayList;
import java.util.List;

public class ProgramUtil {
	private String category, programType, programId, title, synopsisShort, synopsisLong;
	private List<String> tags;
	private ImagesUtil images;
	private ArrayList<CreditsUtil> credits = new ArrayList<CreditsUtil>();
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSynopsisShort() {
		return synopsisShort;
	}
	public void setSynopsisShort(String synopsisShort) {
		this.synopsisShort = synopsisShort;
	}
	public String getSynopsisLong() {
		return synopsisLong;
	}
	public void setSynopsisLong(String synopsisLong) {
		this.synopsisLong = synopsisLong;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public ImagesUtil getImages() {
		return images;
	}
	public void setImages(ImagesUtil images) {
		this.images = images;
	}
	public ArrayList<CreditsUtil> getCredits() {
		return credits;
	}
	public void setCredits(ArrayList<CreditsUtil> credits) {
		this.credits = credits;
	}
}

