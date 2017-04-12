import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;

/**
 * Created by Muhammad Rafay on 4/11/17.
 */
public class ItemManager {
    private HashMap<String, String> Item = new HashMap<>();
    private String itemIdTag;
    private String priceTag;

    ItemManager(String itemIdTag, String priceTag) {
        this.itemIdTag = itemIdTag;
        this.priceTag = priceTag;
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
    public HashMap<String, String> getItemDetails() throws NullException {
        if(this.Item.isEmpty()) {
            throw new NullException("Item Details");
        } else {
            return this.Item;
        }
    }
    //FacilityManager(String)
    // public void loadFacilities(NodeList)

}
