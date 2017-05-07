import java.util.List;

/**+
 * The requirements of this interface is to provide a role every class
 * which extend this, need to implement. This interface will provide public
 * API to get all the necessary details needed to process an order from Facility.
 *
 * If any bad data is passed, a NullException will be thrown.
 */

interface Facility {
    List getScheduleOfFacility(String facilityName) throws NullException;
    List getDetails(String facilityName) throws NullException;
    List getNetworks(String facilityName) throws NullException;
    List getInventory(String facilityName) throws NullException;
    List getShortestPath(String source, String destination) throws NullException;
    List getDepletedInventory(String  facilityName) throws NullException;
    List getFacilitiesWithItem(String itemId) throws NullException;
    void reduceFacilityInventory(String facilityName, String itemId, int quantity);
}
