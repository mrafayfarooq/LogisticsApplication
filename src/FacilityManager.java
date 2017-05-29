
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Muhammad Rafay on 4/9/17.
 *
 * This is a Facade for Facility. It holds the collection of Facility and provides singleton.
 * This Facade can ask appropriate classes to process order, get the details from the facility and find
 * shortest path between two facility.
 *
 */

public class FacilityManager {
    private final ArrayList<Facility> facility = new ArrayList<>();   // Collection of Facility
    private static FacilityManager instance;    // Singleton Instance


    /**
     * Constructor providing Singleton
     *
     * @throws NullException if invalid extension is passed
     */
    public static FacilityManager getInstance() {
        if (instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }

    private FacilityManager() {
    }

    /**
     * +
     * Loads all the facilities by taking parser context and Facility Details.
     * Throws Exception if Context is null or input details for facility is wrong.
     *
     * @param parserContext     Context of Parser
     * @throws NullException if any params is Null or not found.
     */

    public void loadFacility(ParserContext parserContext) throws NullException {
        if (parserContext == null) {
            throw new NullException("Parser Context" + null);
        } else {
            NodeList[] facilityDetail = {parserContext.getEntries("Facilities&Networks"), parserContext.getEntries("FacilityInventory")};
            this.facility.add(new FacilityImplementation(facilityDetail));
        }
    }

    public void printPrettyOutput() throws NullException {
        for (Facility facility : this.facility) {
            PrettyPrint output = new PrettyPrint(facility);
            output.printFacilityDetails();
        }
    }

    /**
     * Process Orders using Parser Context
     *
     * @param parserContext Context of Parser
     * @throws NullException if Parser context is null
     */
    public void processOrders(ParserContext parserContext) throws NullException {
        OrderManager.getInstance(this, parserContext).processOrders();
    }

    public List getShortestPath(String source, String destination) throws NullException {
        return facility.get(0).getShortestPath(source, destination);
    }

    public List getFacilityDetails(String facilityName) throws NullException {
        return facility.get(0).getDetails(facilityName);
    }

    public List getInventory(String facilityName) throws NullException {
        return facility.get(0).getInventory(facilityName);
    }

    public void reduceFacilityInventory(String facilityName, String itemId, int quantity) {
        facility.get(0).reduceFacilityInventory(facilityName, itemId, quantity);
    }

    public int findArrivalDay(Integer startDay, int qunatityToProcess, String facilityName, List itemDetail) throws NullException {
        return facility.get(0).findArrivalDay(startDay, qunatityToProcess, facilityName, itemDetail);
    }

    public List setSchedule(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        return this.facility.get(0).setSchedule(startDay, qunatityToProcess, facilityName, itemDetails);
    }

    public List getFacilitiesWithItem(String itemId) throws NullException {
        return this.facility.get(0).getFacilitiesWithItem(itemId);
    }

    public Map<String, Integer> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        return this.facility.get(0).findFacilitiesWithShortestPath(destination, facilitiesWithItem);
    }

    public int getQuantityOfItem(String facilityName, List itemDetails) throws NullException {
        return this.facility.get(0).getQuantityOfItem(facilityName, itemDetails);
    }

    public int getTravelTimeInDays(String source, String destination) throws NullException {
        return this.facility.get(0).getTravelTimeInDays(source, destination);
    }
}