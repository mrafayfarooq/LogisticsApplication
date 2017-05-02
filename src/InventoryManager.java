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
 *
 * Class to Manage Inventory of the Facility
 */

class InventoryManager {
    private final HashMap<String, List<String>> facilityInventory = new HashMap<>();
    private final String facilityIdTag;
    private final String itemIdTag;
    private final String quantityTag;

    /**+
     * Prepare and  Loads the inventory
     * @param inventoryDetails
     */
    InventoryManager(NodeList inventoryDetails) {
        this.facilityIdTag = "Id";
        this.itemIdTag = "ItemID";
        this.quantityTag = "Quantity";
        load(inventoryDetails);
    }

    /**+
     * Get Depleted(0) Inventory Details
     * @param facilityName name of facility
     * @return list of items which are depleted
     */
    public List getDepletedInventory(String facilityName) throws NullException {
        List depletedInventory = new ArrayList<>();
        List<String> listDetails = getDetails(facilityName);
        for (Object list : listDetails) {
            List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
            String formattedQuantity = inventoryDetails.get(1);
            if (formattedQuantity.equals("0")) {
                depletedInventory.add(inventoryDetails.get(0));
            }
        }
        return depletedInventory;
    }

    /**+
     * Load facility Inventory
     * @param facilityInventoryDetails the details of facility inventory
     */
    private void load(NodeList facilityInventoryDetails) {
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

    /**+
     * Get Details of Facility Inventory
     * @param facilityName name of the facility
     * @return List of facility inventory
     * @throws NullException if facility name doesn't exist
     */
    public List<String> getDetails(String facilityName) throws  NullException {
        if(this.facilityInventory.isEmpty()) {
            throw new NullException("Facility Inventory Details of Facility Name"+ facilityName);
        } else {
            return this.facilityInventory.get(facilityName);
        }
    }
}

