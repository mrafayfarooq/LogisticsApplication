import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.TreeMap;
/**
 * Created by Muhammad Rafay on 4/11/17.
 */
class ItemManager {
    private static final TreeMap<String, String> Item = new TreeMap<>();
    private static ItemManager instance;
    private ItemManager() {}
    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }
        return instance;
    }
    public void loadItems(NodeList Items) {
        for (int i = 0; i < Items.getLength(); i++) {
            if (Items.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }

            String entryName = Items.item(i).getNodeName();
            if (!entryName.equals("Item")) {
                System.err.println("Unexpected node found: " + entryName);
                return;
            }
            // Get Attributes
            Element elem = (Element) Items.item(i);
            String itemId = elem.getElementsByTagName("ItemID").item(0).getTextContent().trim();
            String price = elem.getElementsByTagName("Price").item(0).getTextContent().trim();

            Item.put(itemId, price);
        }
    }
    public TreeMap<String, String> getItemDetails() throws NullException {
        if(Item.isEmpty()) {
            throw new NullException("Item Details");
        } else {
            return Item;
        }
    }

    static int getCostOfItem(String itemName) throws NullException {
        if(Item.get(itemName) == null) {
            throw new NullException("Item Name"+ itemName);
        } else {
            return Integer.valueOf(Item.get(itemName));
        }
    }


}
