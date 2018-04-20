import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class baggageRouting {
	static ArrayList<airportRouteLeg> alRoutes = new ArrayList<>();
	static ArrayList<baggageEntry> alBags = new ArrayList<>();
	static ArrayList<flightEntry> alFlights = new ArrayList<>();
	static String lineType="";
	static boolean skip = false;
	static Map<String, ArrayList<String>> mapNeighbors = 
			new HashMap<String, ArrayList<String>>();
	static ArrayList<String> alstrNeighbors = new ArrayList<String>();
	static Map<String, String> mapProcessed = new HashMap<String, String>();
	
	public static void main(String[] args) {
		
		// Load Input File
		loadInputFile(args[1]);
		
		// Preprocess at table of all possible routes
		//   The best route for each pair of locations is saved
		routePreprocessing();
		
		// Process bags
		processBaggage();
	}
	
	
	private static void loadInputFile(String fileName) {
		/**
		 * Process the input file into it's 3 constituent line types:
		 *    routeLegs:	An arraylist of type airportRouteLeg
		 *    bagList:		An arraylist of type baggageEntry
		 *    flightTable:	An arraylist of type flightTable
		 *    
		 *    Each section is preceded by a # and an indicator so that it doesn't 
		 *    matter what order the sections are listed.
		 */
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
			try {
				String[] items = {};
				String line = null;
				
				while ((line = br.readLine()) != null) {
			    	// Read the next line from the file
					System.out.println(line);

			    	// Check to see if the line type changes - set line type
			    	if (line.indexOf("#") == 0 && line.indexOf("Conveyor") > 0)
			    	{
			    		lineType = "Legs";
			    		skip = true;
			    	}
			    	if (line.indexOf("#") == 0 && line.indexOf("Bag") > 0)
			    	{
			    		lineType = "Bags";
			    		skip = true;
			    	}
			    	if (line.indexOf("#") == 0 && line.indexOf("Depart") > 0)
			    	{
			    		lineType = "Flights";
			    		skip = true;
			    	}
			    	// If line is blank, skip it
			    	if (line.length()==0)
			    	{
			    		skip = true;
			    	}
			    	
			    	// Parse line if the skip flag is not true
			    	if (skip != true) {
			    		items = line.split(" ");
				    	switch (lineType) {
				    	case "Legs":
				    		/**
				    		 *   items[0] : location 1
				    		 *   items[1] : location 2
				    		 *   items[2] : time
				    		 *   
				    		 *   Conveyor routes at the airport need to go in both ways
				    		 */
				    		if (mapNeighbors.containsKey(items[0])) {
				    			// If Node1 is present, add node 2 to list of neighbors
				    			alstrNeighbors = mapNeighbors.get(items[0]);
				    			if (!alstrNeighbors.contains(items[1])) {
				    				alstrNeighbors.add(items[1]);
				    				mapNeighbors.put(items[0], alstrNeighbors);
				    			}
				    		} else {
				    			// Add node 1 as key and list node 2 as its first neighbor
				    			ArrayList<String> newList = new ArrayList<String>();
				    			newList.add(items[1]);
				    			mapNeighbors.put(items[0],newList);
				    		}
				    		if (mapNeighbors.containsKey(items[1])) {
				    			// If Node2 is present, add node 1 to list of neighbors
				    			alstrNeighbors = mapNeighbors.get(items[1]);
				    			if (!alstrNeighbors.contains(items[0])) {
				    				alstrNeighbors.add(items[0]);
				    				mapNeighbors.put(items[1], alstrNeighbors);
				    			}
				    		} else {
				    			// Add node 2 as key and list node 1 as its first neighbor
				    			ArrayList<String> newList = new ArrayList<String>();
				    			newList.add(items[0]);
				    			mapNeighbors.put(items[1],newList);
				    		}
				    		alRoutes.add(new airportRouteLeg(items[0], items[1], Integer.parseInt(items[2])));
				    		alRoutes.add(new airportRouteLeg(items[1], items[0], Integer.parseInt(items[2])));
				    		break;
				    	case "Bags":
				    		/**
				    		 * items[0] : bag number
				    		 * items[1] : current bag location
				    		 * items[2] : flight number
				    		 *            Check flight number against flight list for routing location
				    		 *            If flight number says "ARRIVAL", then send to baggage claim
				    		 */
				    		alBags.add(new baggageEntry(items[0], items[1], items[2]));
				    		break;
				    	case "Flights":
				    		/**
				    		 * items[0] : flight identifier
				    		 * items[1] : departure location
				    		 * items[2] : flight destination
				    		 * items[3] : departure time
				    		 */
				    		alFlights.add(new flightEntry(items[0], items[1], items[2], items[3]));
				    		break;
			    	}
				}
			    	// If the line was skipped, flip the flag back to false
			    	skip = false;
				}
				} catch(Exception ex) {
					// Read fails on EOF
				}
				finally {
					try {
						br.close();
					} catch (IOException e) {
						// Buffered Reader doesn't need to be closed
					}
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void processBaggage() {
		// Loop through the bag list
		for (baggageEntry bag: alBags) {
			String startPoint = bag.location;
			String endPoint = "";
			// Identify the destination point from the flight number
			for (flightEntry flt : alFlights ) {
				if (flt.flightID.equals(bag.flightID)) {
					endPoint = flt.departGate;
					// Flight arrivals should be routed to the baggage claims area
					if (endPoint.equals("ARRIVAL"))
						endPoint = "BaggageClaim";
					break;
				}
			}
			
			// Search for the shortest path for the bag to travel
			if (!endPoint.equals("")) {
				// Look up the path in the preprocessed matrix
				System.out.println(bag.baggageID+" "+mapProcessed.get(startPoint+"|"+endPoint));
			} else {
				// Path from bag location to destination cannot be found.  Warn operator.
				System.out.printf("Could not find destination for bag (%s)\n",bag.baggageID);
			}
		}
	}

	private static void routePreprocessing() {
		/**
		 * Preprocessing all possible route combinations
		 *   Nested loops will find all routes from one point to the other with lowest possible time.
		 * 
		 */
		for (String strX : mapNeighbors.keySet()) {
			for (String strY : mapNeighbors.keySet()) {
				// Create key.       Origin | Destination
				String mapKey = strX + "|" + strY;
				// Prepare a blank path to pass in to the findPath routine
				bagPath nullPath = new bagPath("",0);
				bagPath bpShortPath = new bagPath("",0);
				// only bother to calculate if the start and end points are different
				if (strX != strY)
					bpShortPath = findPath(strX, strY, nullPath);
				// Add calculated path to the matrix
				//    - Allow blank path to fall through to the map where start and end are the same
				mapProcessed.put(mapKey, 
						bpShortPath.strLocations.toString().replace("[", "").replace("]", "") 
						+ " : "+Integer.toString((bpShortPath.intTime)));
			}
		}
	}
	
	private static bagPath findPath(String strStart, String strEnd, bagPath bpPath) {
		bagPath route = null;
		if (strStart.equals(strEnd)) {
			// Catch the base case
			route = new bagPath(strStart,0);
		} else {
			// Get neighbor list
			ArrayList<String> neighbors = mapNeighbors.get(strStart);
			// Prepare to store possible solutions
			ArrayList<bagPath> possibles = new ArrayList<bagPath>();
			for (String strLocation : neighbors) {
				// Only check unused paths
				if (!bpPath.strLocations.contains(strLocation)) {
					bagPath nextPath = bpPath;
					// Important: Ensure that the current start node is also removed from consideration of downstream solutions
					nextPath.strLocations.add(strStart);
					bagPath responsePath;
					try {
						// Find path from neighbor to end point
						responsePath = findPath(strLocation,strEnd,nextPath);
						// Save result if it's not null
						if (responsePath != null) {
							possibles.add(responsePath);
						}
					} catch (Exception ex) {
						// Unable to get a valid path back
						//   - Let the default blank return value fall through
					}
				}
			}
			// Check possibles and return the shortest
			// Zero Possibles
			//      - route is already set to null.  Let it pass.
			// One possible
			if (possibles.size() == 1) {
				// Set route to the only possible
				route = possibles.get(0);
				route.strLocations.add(0, strStart);
				// Grab time for the newly added leg
				for (airportRouteLeg leg : alRoutes) {
					if (leg.location1.equals((strStart)) && 
							leg.location2.equals(route.strLocations.get(1))) {
						route.intTime = route.intTime + leg.time;
					}
				}
			} else if (possibles.size() > 1) {
				// Multiples that need to be parsed
				int smallIndex = 0;
				for (int intIndex = 1; intIndex < possibles.size(); intIndex++) {
					// Assume first is smallest time, test if any others are smaller
					if (possibles.get(intIndex).intTime < possibles.get(smallIndex).intTime) {
						// Save the index of the smallest time
						smallIndex = intIndex;
					}
				}
				// Set to the possible with the shortest time.
				route = possibles.get(smallIndex);
				route.strLocations.add(0, strStart);
				// Grab time for the newly added leg
				for (airportRouteLeg leg : alRoutes) {
					if (leg.location1.equals((strStart)) && 
							leg.location2.equals(route.strLocations.get(1))) {
						route.intTime = route.intTime + leg.time;
					}
				}
			}	
		}
		System.out.println("About to return :"+String.valueOf(route.intTime)+":"+route.strLocations.toString());
		return route;
	}
}
