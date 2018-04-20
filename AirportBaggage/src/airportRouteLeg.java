
public class airportRouteLeg {
	/**
	 * airportRouteLeg : This class defines the structure used to store information
	 *                   regarding the conveyor routes within the airports.
	 *                   
	 *     The constructor sets all the internal variables to facilitate its use
	 *     as the Route Legs are read in from the input file.
	 * 
	 */
	String location1;
	String location2;
	int time;
	
	public airportRouteLeg(String location1, String location2, int legTime) {
		this.location1 = location1;
		this.location2 = location2;
		this.time = legTime;
	}
	
}
