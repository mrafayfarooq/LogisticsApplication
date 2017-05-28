import java.util.*;

/**
 * Created by Muhammad Rafay on 5/3/17.
 */
public class OrderManager {
    Facility facilityManager;
    Order order;
    private static OrderManager instance;    // Singleton Instance
    public static OrderManager getInstance(Facility facilityManager, ParserContext parserContext) throws NullException {
        if(instance == null) {
            instance = new OrderManager(facilityManager, parserContext);
        }
        return instance;
    }

    private OrderManager(Facility facilityManager,ParserContext parserContext) throws NullException {
       this.facilityManager = facilityManager;
       this.order = new OrderImpl(parserContext.getEntries("Orders"));
    }
    public TreeMap<String, List<String>> getOrders() {
        return this.order.getOrders();
    }
    public List getFacilitiesWithItem(String itemID) throws NullException {
        return facilityManager.getFacilitiesWithItem(itemID);
    }
    public void processOrders() throws NullException {
        TreeMap<String, List<String>> orders = this.getOrders();
        OrderProcessor orderProcessor = new OrderProcessor(this);
        orderProcessor.processOrder(orders);
    }
    public  void getNextOrder() {

    }
    public List getShortestPath(String source, String destination) throws NullException {
        return facilityManager.getShortestPath(source,destination);
    }
    public List getFacilityDetails(String facilityName) throws NullException {
        return facilityManager.getDetails(facilityName);
    }
    public List getInventory(String facilityName) throws NullException {
        return facilityManager.getInventory(facilityName);
    }
    public void reduceFacilityInventory(String facilityName, String itemId, int quantity) {
        facilityManager.reduceFacilityInventory(facilityName,itemId,quantity);
    }
    public int findArrivalDay(Integer startDay, int qunatityToProcess, String facilityName, List itemDetail) throws NullException {
        return facilityManager.findArrivalDay(startDay,qunatityToProcess,facilityName, itemDetail);
    }
    public List setSchedule(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
       return this.facilityManager.setSchedule(startDay,qunatityToProcess,facilityName,itemDetails);
    }

    }
