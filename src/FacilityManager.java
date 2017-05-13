
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Muhammad Rafay on 4/9/17.
 *
 * FacilityManager class implements Facility. This is facade implementing Facility interface.
 * The constructor of this class takes a valid Parser Context which contains the details of
 * the data loaded by the parses and load the object accordingly.
 *
 * The class throws Null Exception if parser context is null.
 */

public class FacilityManager {
    private ArrayList<Facility> facility = new ArrayList<>();
    private static FacilityManager instance;


    /**
     * +
     * Constructor taking Parser Context
     *
     * @param parserContext Context for parser. Can be XML, JSON etc
     * @throws NullException if invalid extension is passed
     */
    // Singelton Instance
    public static FacilityManager getInstance() throws NullException {
        if(instance == null) {
            instance = new FacilityManager();
        }
        return instance;
    }
    private FacilityManager() {}

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
    public void processOrders(ParserContext parserContext) throws NullException {
        for (Facility facility: this.facility) {
            OrderManager orderManager = new  OrderManager(facility, parserContext);
            orderManager.processOrders();
        }
    }
}
