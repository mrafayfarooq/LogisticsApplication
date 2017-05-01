
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager implements Facility {
    private Facility facility;
    FacilityManager(ParserContext parserContext) throws NullException {
        if(parserContext == null) {
            throw  new NullException("Parser Context");
        } else {
            NodeList[] facilityDetails = parserContext.getEntries();
            this.facility = new FacilityImplmentation(facilityDetails);
        }
    }
    public List getScheduleOfFacility(String facilityName) throws NullException {
        return facility.getScheduleOfFacility(facilityName);
    }
    public List getDetails(String facilityName) throws NullException {
        return this.facility.getDetails(facilityName);
    }
    public List getNetworks(String facilityName) throws NullException {
        return this.facility.getNetworks(facilityName);
    }
    public List getInventory(String facilityName) throws NullException {
        return this.facility.getInventory(facilityName);
    }
    public List getShortestPath(String source, String destination) throws NullException {
         return this.facility.getShortestPath(source,destination);
    }
    public List getDepletedInventory(String  facilityName) throws NullException {
        return this.facility.getDepletedInventory(facilityName);
    }

}
