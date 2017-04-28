
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager {
    private Facility facility;
    FacilityManager(ParserContext parserContext) throws NullException {
        if(parserContext == null) {
            throw  new NullException("Parser Context");
        } else {
            NodeList facilityDetails = parserContext.getEntries()[0];
            this.facility = FacilityImplmentation.getInstance(facilityDetails);
        }
    }
    List getScheduleOfFacility(String facilityName) throws NullException {
        return facility.getScheduleOfFacility(facilityName);
    }
    List getDetails(String facilityName) throws NullException {
        return this.facility.getDetails(facilityName);
    }
    List getNetworks(String facilityName) throws NullException {
        return this.facility.getNetworks(facilityName);
    }

}
