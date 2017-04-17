import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.TreeMap;
/**
 * Created by Muhammad Rafay on 4/11/17.
 */
class ItemManager {
    private final TreeMap<String, String> Item = new TreeMap<>();
    private final String itemIdTag;
    private final String priceTag;

    ItemManager() {
        this.itemIdTag = "ItemID";
        this.priceTag = "Price";
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
            String itemId = elem.getElementsByTagName(itemIdTag).item(0).getTextContent();
            String price = elem.getElementsByTagName(priceTag).item(0).getTextContent();

            this.Item.put(itemId, price);
        }
    }
    public TreeMap<String, String> getItemDetails() throws NullException {
        if(this.Item.isEmpty()) {
            throw new NullException("Item Details");
        } else {
            return this.Item;
        }
    }
}
