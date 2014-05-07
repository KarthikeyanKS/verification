package mslt.verification;

public class GetArray {
    private String[] array;
	public String[] getArray(String s) {
		s = s.replaceAll("\\[", "").replaceAll("\\]", "");
		array = s.split(",");
		String[] trimmedArray = new String[array.length];
		for (int i = 0; i < array.length; i++)
		    trimmedArray[i] = array[i].trim();
		return trimmedArray;
	}
}

