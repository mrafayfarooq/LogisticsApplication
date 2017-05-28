import java.util.*;

/**
 * Created by Muhammad Rafay on 5/3/17.
 */
public class OrderManager {
    private FacilityManager facilityManager;
    private OrderLoader orderLoader;
    private Order order;

    private static OrderManager instance;    // Singleton Instance

    public static OrderManager getInstance(FacilityManager facilityManager, ParserContext parserContext) throws NullException {
        if (instance == null) {
            instance = new OrderManager(facilityManager, parserContext);
        }
        return instance;
    }

    private OrderManager(FacilityManager facilityManager, ParserContext parserContext) throws NullException {
        this.facilityManager = facilityManager;
        this.orderLoader = new OrderLoader(parserContext.getEntries("Orders"));
    }

    public TreeMap<String, List<String>> getOrders() {
        return this.orderLoader.getOrders();
    }

    public List getFacilitiesWithItem(String itemID) throws NullException {
        return facilityManager.getFacilitiesWithItem(itemID);
    }

    public void processOrders() throws NullException {
        TreeMap<String, List<String>> orders = this.getOrders();
        OrderProcessor orderProcessor = new OrderProcessor(this);
        orderProcessor.processOrder(orders);
    }

    public List getShortestPath(String source, String destination) throws NullException {
        return facilityManager.getShortestPath(source, destination);
    }

    public List getFacilityDetails(String facilityName) throws NullException {
        return facilityManager.getFacilityDetails(facilityName);
    }

    public List getInventory(String facilityName) throws NullException {
        return facilityManager.getInventory(facilityName);
    }

    public void reduceFacilityInventory(String facilityName, String itemId, int quantity) {
        facilityManager.reduceFacilityInventory(facilityName, itemId, quantity);
    }

    public int findArrivalDay(Integer startDay, int qunatityToProcess, String facilityName, List itemDetail) throws NullException {
        return facilityManager.findArrivalDay(startDay, qunatityToProcess, facilityName, itemDetail);
    }

    public List setSchedule(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        return this.facilityManager.setSchedule(startDay, qunatityToProcess, facilityName, itemDetails);
    }

    public Map<String, Integer> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        return this.facilityManager.findFacilitiesWithShortestPath(destination, facilitiesWithItem);
    }

    public int getQuantityOfItem(String facilityName, List itemDetails) throws NullException {
        return this.facilityManager.getQuantityOfItem(facilityName, itemDetails);
    }
}
