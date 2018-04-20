
public class flightEntry {
	/**
	 * flightTable : This class defines the structure used to store flight locations
	 *                   
	 *     The constructor sets all the internal variables to facilitate its use
	 *     as the flights are read in from the input file.
	 * 
	 */
	
	String flightID;
	String departGate;
	String destination;
	String departureTime;
	
	public flightEntry (String flightID, String departGate, 
			String destination, String departureTime) {
		this.flightID = flightID;
		this.departGate = departGate;
		this.destination = destination;
		this.departureTime = departureTime;
	}
	
}
