
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<Facility> facility = new ArrayList<>();   // Collection of Facility
    private static FacilityManager instance;    // Singleton Instance


    /**
     *
     * Constructor providing Singleton
     *
     * @throws NullException if invalid extension is passed
     */
    public static FacilityManager getInstance() throws NullException {
        if(instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }
    private FacilityManager() {}

    /**+
     * Loads all the facilities by taking parser context and Facility Details.
     * Throws Exception if Context is null or input details for facility is wrong.
     * @param parserContext Context of Parser
     * @param facilityNetworks Details of Facility Networks
     * @param facilityInventory Details of Facility Inventory
     * @throws NullException if any params is Null or not found.
     */

    public void loadFacility(ParserContext parserContext, String facilityNetworks, String facilityInventory) throws NullException {
        if (parserContext == null) {
            throw new NullException("Parser Context" + null);
        } else {
            NodeList[] facilityDetail = {parserContext.getEntries(facilityNetworks), parserContext.getEntries(facilityInventory)};
            this.facility.add(new FacilityImplementation(facilityDetail));
        }
    }

    public void printPrettyOutput() throws NullException {
        for (Facility facility: this.facility) {
            Output output= new Output(facility);
            output.printFacilityDetails();
            output.printItemCatalog();
            output.printShortestPath("Santa Fe, NM", "Chicago, IL");
                output.printShortestPath("Santa Fe, NM", "Chicago, IL");
                output.printShortestPath("Atlanta, GA", "St. Louis, MO");
                output.printShortestPath("Seattle, WA", "Nashville, TN");
                output.printShortestPath("New York City, NY", "Phoenix, AZ");
                output.printShortestPath("Fargo, ND", "Austin, TX");
                output.printShortestPath("Denver, CO", "Miami, FL");
                output.printShortestPath("Austin, TX", "Norfolk, VA");
                output.printShortestPath("Miami, FL", "Seattle, WA");
                output.printShortestPath("Los Angeles, CA", "Chicago, IL");
                output.printShortestPath("Detroit, MI", "Nashville, TN");
        }
    }

    /**
     * Process Orders using Parser Context
     * @param parserContext Context of Parser
     * @throws NullException if Parser context is null
     */
    public void processOrders(ParserContext parserContext) throws NullException {
        for (Facility facility: this.facility) {
            OrderManager orderManager = new  OrderManager(facility, parserContext);
            orderManager.processOrders();
        }
    }
}
