
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager {
    private Facility facility;
    FacilityManager(ParserContext parserContext) {
        NodeList entries = parserContext.getEntries()[0];
        this.facility = new FacilityImplmentation();
        facility.loadFacility(entries);
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

}
