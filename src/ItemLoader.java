import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.TreeMap;

/**
 * Created by Muhammad Rafay on 5/28/17.
 *
 * This class loads all the item from a file
 */
public class ItemLoader {
    private static ItemLoader instance;
    private ItemLoader() {}
    public static ItemLoader getInstance() {
        if (instance == null) {
            instance = new ItemLoader();
        }
        return instance;
    }
    // Load Items
    public TreeMap<String, String> loadItems(NodeList Items) throws NullException {
        TreeMap<String, String> item = new TreeMap<>();
        for (int i = 0; i < Items.getLength(); i++) {
            if (Items.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }

            String entryName = Items.item(i).getNodeName();
            if (!entryName.equals("Item")) {
                System.err.println("Unexpected node found: " + entryName);
                throw new NullException("Node");
            }
            // Get Attributes
            Element elem = (Element) Items.item(i);
            String itemId = elem.getElementsByTagName("ItemID").item(0).getTextContent().trim();
            String price = elem.getElementsByTagName("Price").item(0).getTextContent().trim();

            item.put(itemId, price);
        }
        return item;
    }
}
