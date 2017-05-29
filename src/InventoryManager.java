import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/11/17.
 *
 * This class Manages Inventory of the Facility.
 * It has details of all the inventory of the facility with Item ID, and quantity.
 *
 * The constructor of this class takes raw inventory details and loads it into object.
 */


class InventoryManager {
    private final HashMap<String, List<String>> facilityInventory = new HashMap<>();
    private final String itemIdTag;
    private final String quantityTag;

    /**+
     * Prepare and  Loads the inventory
     * @param inventoryDetails details of the inventory.
     */
    InventoryManager(NodeList inventoryDetails) {
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
        List<String> listDetails = getDetails(facilityName); // Get details of the facility inventory which contains inventory name and quantity. Ex. ABCD:190
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
                // Loading Inventory
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
        if(this.facilityInventory.get(facilityName) == null) {
            throw new NullException("Inventory Details of Facility Name "+ facilityName);
        } else {
            return this.facilityInventory.get(facilityName);
        }
    }
    public void reduceFacilityInventory(String facilityName, String itemId, int quantity) {
        List facilityInventory = this.facilityInventory.get(facilityName);
        int index = 0;
        for(Object list: facilityInventory) {
            index ++;
            List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
            if(inventoryDetails.get(0).equals(itemId)) {
                Integer modifiedQuantity = Integer.parseInt(inventoryDetails.get(1)) - quantity;
                String itemID = inventoryDetails.get(0);
                this.facilityInventory.get(facilityName).set(index-1, itemID+":"+modifiedQuantity);
            }
        }

    }
    public int getQuantityOfItem(String facilityName, List itemDetails) throws NullException {
        List inventoryDetails = this.getDetails(facilityName);
        for(Object list : inventoryDetails) {
            List<String> inventory = Arrays.asList(list.toString().split(":"));
            if (inventory.get(0).equals(itemDetails.get(0).toString())) {
                return Integer.parseInt(inventory.get(1));
            }
        }
        return 0;
    }
}

