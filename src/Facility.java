/**
 * Created by Muhammad Rafay on 4/26/17.
 */

import org.w3c.dom.NodeList;
import java.util.List;

interface Facility {
    void loadFacility(NodeList entries);
    List getScheduleOfFacility(String facilityName) throws NullException;
    int getProcessingPower(String facilityName) throws  NullException;
    List getDetails(String facilityName) throws NullException;
}
