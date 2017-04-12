import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/11/17.
 */
public class InventoryManager {
    private HashMap<Integer, List<String>> facilityInventory = new HashMap<>();
    private List inventoryDetails = new ArrayList<>();
    private String facilityIdTag;
    private String itemIdTag;
    private String quantityTag;

    InventoryManager(String facilityIdTag, String itemIdTag, String quantityTag) {
        this.facilityIdTag = facilityIdTag;
        this.itemIdTag = itemIdTag;
        this.quantityTag = quantityTag;
    }

    public void loadInventory(NodeList facilityInventoryDetails) {
        for (int i = 0; i < facilityInventoryDetails.getLength(); i++) {
            if (facilityInventoryDetails.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            // Get Facility Id
            NamedNodeMap aMap = facilityInventoryDetails.item(i).getAttributes();
            String facilityId = aMap.getNamedItem(facilityIdTag).getNodeValue();

            // Get Inventory Details
            Element elem = (Element) facilityInventoryDetails.item(i);
            NodeList inventoryList = elem.getElementsByTagName("Item");
            ArrayList<String> inventory = new ArrayList<>();

            for (int j = 0; j < inventoryList.getLength(); j++) {
                if (inventoryList.item(j).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                elem = (Element) inventoryList.item(j);
                String itemId = elem.getElementsByTagName(itemIdTag).item(0).getTextContent();
                String quantity = elem.getElementsByTagName(quantityTag).item(0).getTextContent();
                inventory.add(itemId + "-" + quantity);
            }
            inventoryDetails =  new ArrayList(inventory);
            facilityInventory.putIfAbsent(Integer.valueOf(facilityId), inventoryDetails);

        }
    }
    public HashMap<Integer, List<String>> getFacilityInventoryDetails() throws NullException {
        if(this.facilityInventory.isEmpty()) {
            throw new NullException("Facility Inventory Details");
        } else {
            return this.facilityInventory;
        }
    }
}

