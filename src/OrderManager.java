import java.util.*;
import java.text.DecimalFormat;

/**
 * Created by Muhammad Rafay on 5/3/17.
 */
public class OrderManager implements Order {
    FacilityManager facilityManager;
    Order order;

    OrderManager(FacilityManager facilityManager,ParserContext parserContext) throws NullException {
       this.facilityManager = facilityManager;
       this.order = new OrderImpl(parserContext.getEntries("Orders"));
    }
    public HashMap getOrders() {
        return this.order.getOrders();
    }
    public List getFacilitiesWithItem(String itemID) throws NullException {
        return facilityManager.getFacilitiesWithItem(itemID);
    }
    public void processOrders() throws NullException {
        HashMap<String, List<String>> orders = this.getOrders();
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
}
