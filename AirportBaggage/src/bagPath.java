import java.util.ArrayList;

public class bagPath {
	ArrayList<String> strLocations;
	int intTime;
	
	public bagPath(String strLocation, int intTime) {
		ArrayList<String> aryTemp = new ArrayList<String>();
		aryTemp.add(strLocation);
		this.strLocations = aryTemp;
		this.intTime = intTime;
	}
}
