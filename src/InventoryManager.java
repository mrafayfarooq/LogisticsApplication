import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/11/17.
 */

public class InventoryManager {
    private final HashMap<String, List<String>> facilityInventory = new HashMap<>();
    private final String facilityIdTag;
    private final String itemIdTag;
    private final String quantityTag;

    InventoryManager(NodeList inventoryDetails) {
        this.facilityIdTag = "Id";
        this.itemIdTag = "ItemID";
        this.quantityTag = "Quantity";
        load(inventoryDetails);
    }
    public List getDepletedInventory(String  facilityName) {
        List depletedInventory = new ArrayList<>();
        try {
            List<String> listDetails = getDetails(facilityName);
            for (Object list : listDetails) {
                List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
                String formattedQuantity = inventoryDetails.get(1);
                if (formattedQuantity.equals("0")) {
                    depletedInventory.add(inventoryDetails.get(0));
                }
            }
        } catch (NullException e) {
            e.printStackTrace();
        }
        return depletedInventory;
    }
    public void load(NodeList facilityInventoryDetails) {
        for (int i = 0; i < facilityInventoryDetails.getLength(); i++) {
            if (facilityInventoryDetails.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            // Get Facility Id
            NamedNodeMap aMap = facilityInventoryDetails.item(i).getAttributes();
            String facilityId = aMap.getNamedItem(facilityIdTag).getNodeValue();

            // Get Inventory Details
            Element elem = (Element) facilityInventoryDetails.item(i);
            String location = elem.getElementsByTagName("Location").item(0).getTextContent().trim();

            NodeList inventoryList = elem.getElementsByTagName("Item");
            ArrayList<String> inventory = new ArrayList<>();

            for (int j = 0; j < inventoryList.getLength(); j++) {
                if (inventoryList.item(j).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                elem = (Element) inventoryList.item(j);
                String itemId = elem.getElementsByTagName(itemIdTag).item(0).getTextContent().trim();
                String quantity = elem.getElementsByTagName(quantityTag).item(0).getTextContent().trim();
                inventory.add(itemId + ":" + quantity);
            }
            List <String> inventoryDetails = new ArrayList<>(inventory);
            facilityInventory.putIfAbsent(location, inventoryDetails);
        }
    }
    public List<String> getDetails(String facilityName) throws  NullException {

        if(this.facilityInventory.isEmpty()) {
            throw new NullException("Facility Inventory Details of Facility Name"+ facilityName);
        } else {
            return this.facilityInventory.get(facilityName);
        }
    }
}

