
import org.w3c.dom.NodeList;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 *
 * FacilityManager class implements Facility. This is facade interface implmenting Facility
 */

public class FacilityManager implements Facility {
    private Facility facility;

    /**+
     * Constructor taking Parser Context
     * @param parserContext Context for parser. Can be XML, JSON etc
     * @throws NullException if invalid extension is passed
     */
    FacilityManager(ParserContext parserContext) throws NullException {
        if(parserContext == null) {
            throw  new NullException("Parser Context" + parserContext);
        } else {
            NodeList[] facilityDetails = { parserContext.getEntries("Facility&Network"), parserContext.getEntries("FacilityInventory") };
            this.facility = new FacilityImplmentation(facilityDetails);
        }
    }
    // Get Schedule of Facility
    public List getScheduleOfFacility(String facilityName) throws NullException {
        return facility.getScheduleOfFacility(facilityName);
    }
    // Get Details fo Facility
    public List getDetails(String facilityName) throws NullException {
        return this.facility.getDetails(facilityName);
    }
    // Get Facility Networks
    public List getNetworks(String facilityName) throws NullException {
        return this.facility.getNetworks(facilityName);
    }
    // Get Inventory for the Facility
    public List getInventory(String facilityName) throws NullException {
        return this.facility.getInventory(facilityName);
    }
    // Get Shortest Path from two facility
    public List getShortestPath(String source, String destination) throws NullException {
         return this.facility.getShortestPath(source,destination);
    }
    // Get Depleted Inventory
    public List getDepletedInventory(String  facilityName) throws NullException {
        return this.facility.getDepletedInventory(facilityName);
    }

}
