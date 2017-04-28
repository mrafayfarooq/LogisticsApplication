
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager {
    private Facility facility;
    private Network network;
    FacilityManager(ParserContext parserContext) throws NullException {
        if(parserContext == null) {
            throw  new NullException("Parser Context");
        } else {
            NodeList facilityDetails = parserContext.getEntries()[0];
            facility = new FacilityImplmentation();
            facility.loadFacility(facilityDetails);
            network = NetworkImplmentation.getInstance(facilityDetails);
        }
    }
    List getScheduleOfFacility(String facilityName) throws NullException {
        return facility.getScheduleOfFacility(facilityName);
    }
    int getProcessingPower(String facilityName) throws NullException {
        return facility.getProcessingPower(facilityName);
    }
    List getDetails(String facilityName) throws NullException {
        return facility.getDetails(facilityName);
    }
    List getNetwork(String facilityName) throws NullException {
        return network.getNetwork(facilityName);
    }
}
