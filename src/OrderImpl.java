import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 5/3/17.
 */
public class OrderImpl implements Order {
    private final HashMap<String, List<String>> orderDetail = new HashMap<>();

    OrderImpl(NodeList orderDetails) {
        loadOrder(orderDetails);
    }
    /**+
     * Loading the Order
     * @param orderDetails NodeList
     */
    private void loadOrder(NodeList orderDetails) {
        for (int i = 0; i < orderDetails.getLength(); i++) {
            if (orderDetails.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            ArrayList<String> order = new ArrayList<>();

            // Get Order Details
            NamedNodeMap aMap = orderDetails.item(i).getAttributes();
            String orderId = aMap.getNamedItem("Id").getNodeValue().trim();
            Element elem = (Element) orderDetails.item(i);
            String destination = elem.getElementsByTagName("Destination").item(0).getTextContent().trim();
            String time = elem.getElementsByTagName("Time").item(0).getTextContent().trim();
            order.add(destination + "-" + time);


            // Get Order Items
            NodeList itemList = elem.getElementsByTagName("Item");
            ArrayList<String> item = new ArrayList<>();
            for (int j = 0; j < itemList.getLength(); j++) {
                if (itemList.item(j).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                elem = (Element) itemList.item(j);
                // Loading Inventory
                String itemId = elem.getElementsByTagName("ItemID").item(0).getTextContent().trim();
                String quantity = elem.getElementsByTagName("Quantity").item(0).getTextContent().trim();
                item.add(itemId + ":" + quantity);
            }
            List<String> orderCompleteDetails = new ArrayList<>(order);
            orderCompleteDetails.addAll(item);

            orderDetail.putIfAbsent(orderId, orderCompleteDetails);
        }
    }

    /**+
     * Get all the orders
     * @return HashMap of the orders
     */
    public HashMap getOrders() {
        return orderDetail;
    }

}
