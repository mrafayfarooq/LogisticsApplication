import org.w3c.dom.NodeList;
import java.util.TreeMap;
/**
 * Created by Muhammad Rafay on 4/11/17.
 *
 * This class is managing the Items. It has details of Item and it's cost.
 */
class ItemManager {
    private static TreeMap<String, String> item = new TreeMap<>();
    private static ItemManager instance;

    private ItemManager() {}
    // Singleton Instance
    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }
    public void loadItems(NodeList items) throws NullException {
        item = ItemLoader.getInstance().loadItems(items);
    }


        /**+
         * Get Item Details
         * @return Map of every item with its Quantity
         * @throws NullException if Item list is empty
         */
    public TreeMap<String, String> getItemDetails() throws NullException {
        if(item.isEmpty()) {
            throw new NullException("Item Details");
        } else {
            return item;
        }
    }

    /**+
     * Check if the item exist in the inventory
     * @param itemID id of the item.
     * @return boolean
     */
    public Boolean checkItem(String itemID) {
       return item.get(itemID) != null;
    }

    /**+
     * Utility function to get the cost of the item
     * @param itemID ItemID
     * @return cost
     */

    public int getItemCost(String itemID) {
        return Integer.parseInt(item.get(itemID));
    }


}
