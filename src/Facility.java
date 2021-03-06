import java.util.List;
import java.util.Map;

/**+
 * The requirements of this interface is to provide a role every class
 * which extend this, need to implement. This interface will provide public
 * API to get all the necessary details needed to process an order from Facility.
 *
 * If any bad data is passed, a NullException will be thrown.
 */

interface Facility {
    Map getScheduleOfFacility(String facilityName) throws NullException;

    List getDetails(String facilityName) throws NullException;

    List getNetworks(String facilityName) throws NullException;

    List getInventory(String facilityName) throws NullException;

    List getShortestPath(String source, String destination) throws NullException;

    List getDepletedInventory(String facilityName) throws NullException;

    List getFacilitiesWithItem(String itemId) throws NullException;

    void reduceFacilityInventory(String facilityName, String itemId, int quantity);

    int findArrivalDay(int startDay, int quantityToProcess, String facilityName, List itemDetail) throws NullException;

    List setSchedule(int startDay, int quantityToProcess, String facilityName, List itemDetails) throws NullException;

    Map<String, Integer> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException;

    int getQuantityOfItem(String facilityName, List itemDetails) throws NullException;

    int getTravelTimeInDays(String source, String destination) throws NullException;

}