
public class baggageEntry {
	/**
	 * baggageEntry : This class defines the structure of Baggage entries as imported 
	 *                from the input file
	 *                   
	 *     The constructor sets all the internal variables to facilitate its use
	 *     as the baggage items are read in.
	 * 
	 *   (bagNumber) is a string entry that is essentially used as a sequence number
	 *   (location) refers to the current location of the bag
	 *   (FlightIdentifier) is the ID of the flight from the flightTable
	 *      - FlightIdentifier will equal "ARRIVAL" for arriving bags that need to go 
	 *        to baggage claim
	 */
	
	String baggageID;
	String location;
	String flightID;
	
	public baggageEntry (String bagNumber, String bagLocation, String flightIdentifier) {
		this.baggageID = bagNumber;
		this.location = bagLocation;
		this.flightID = flightIdentifier;
	}
	
}
