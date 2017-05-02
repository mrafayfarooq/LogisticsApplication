import java.util.List;

/**
 * Created by Muhammad Rafay on 4/26/17.
 *
 * Facility Interface determining all the methods needed to be implmented by FacilityImpl
 */
interface Facility {
    List getScheduleOfFacility(String facilityName) throws NullException;
    List getDetails(String facilityName) throws NullException;
    List getNetworks(String facilityName) throws NullException;
    List getInventory(String facilityName) throws NullException;
    List getShortestPath(String source, String destination) throws NullException;
    List getDepletedInventory(String  facilityName) throws NullException;
}
