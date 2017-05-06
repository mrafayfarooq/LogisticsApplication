import java.util.*;

/**
 * Created by Muhammad Rafay on 5/3/17.
 */
public class OrderManager {
    FacilityManager facilityManager;
    ItemManager itemManager;
    Order order;

    OrderManager(FacilityManager facilityManager,ParserContext parserContext) throws NullException {
       this.facilityManager = facilityManager;
       this.order = new OrderImpl(parserContext.getEntries("Orders"));
       this.itemManager = ItemManager.getInstance();
    }
    private HashMap getOrders() {
        return this.order.getOrders();
    }
    public void processOrders() throws NullException {
        HashMap<String, List<String>> orders = this.getOrders();
        for (Map.Entry<String, List<String>> entry : orders.entrySet()) {
            String orderID = entry.getKey();
            List itemOrdered = entry.getValue().subList(1,entry.getValue().size());
            String destination = entry.getValue().get(0);
            for(int i=0; i<itemOrdered.size(); i++) {
                List itemDetails = Arrays.asList(itemOrdered.get(i).toString().split(":"));
                if(itemManager.checkItem(itemDetails.get(0).toString())) {
                    String itemID = itemDetails.get(0).toString();
                    int quantity = Integer.parseInt(itemDetails.get(1).toString());
                    List facilitiesWithItem = facilityManager.getFacilitiesWithItem(itemID);
                    System.out.println(this.findFacilitiesWithShortestPath(destination, facilitiesWithItem));
                 } else {
                    System.out.println("Item does not exist");
                    return;
                }
            }
            order.processOrder(entry.getValue());
        }
    }
    private Map<Double, String> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        Map<Double,String> facilityWithShortestPath = new HashMap();
        for (Object list : facilitiesWithItem) {
            List path = facilityManager.getShortestPath(list.toString(), destination.split("-")[0]);
            if(path!= null) {
                int distance = (int) path.get(path.size()-1);
                float days = (float)distance/400;
                facilityWithShortestPath.put((double) days,list.toString());
            }
        }
        Map<Double, String> sortedList = new TreeMap<Double, String>(facilityWithShortestPath);
        return sortedList;
    }
}
