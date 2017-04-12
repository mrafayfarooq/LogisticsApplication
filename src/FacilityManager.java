import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager {
    private HashMap<Integer, List<String>> facility = new HashMap<>();
    private List facilityDetails = new ArrayList<>();
    private String idTag;
    private String locationTag;
    private String powerTag;
    private String costTag;

    FacilityManager(String idTag, String locationTag, String powerTag, String costTag) {
        this.idTag = idTag;
        this.locationTag = locationTag;
        this.powerTag = powerTag;
        this.costTag = costTag;
    }
    public void loadFacilitiesAndNetwork(NodeList facilities) {
        for (int i = 0; i < facilities.getLength(); i++) {
            if (facilities.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }

            String entryName = facilities.item(i).getNodeName();
            if (!entryName.equals("Facility")) {
                System.err.println("Unexpected node found: " + entryName);
                return;
            }
             // Get Node Id
            NamedNodeMap aMap = facilities.item(i).getAttributes();
            String facilityId = aMap.getNamedItem(idTag).getNodeValue();

            // Get Attributes
            Element elem = (Element) facilities.item(i);
            String location = elem.getElementsByTagName(locationTag).item(0).getTextContent();
            String processingPowerPerDay = elem.getElementsByTagName(powerTag).item(0).getTextContent();
            String cost = elem.getElementsByTagName(costTag).item(0).getTextContent();

            facilityDetails = Arrays.asList(location, processingPowerPerDay, cost);
            facility.put(Integer.valueOf(facilityId), facilityDetails);
        }
    }
    public HashMap<Integer, List<String>> getFacilityDetails() throws NullException {
        if(this.facility.isEmpty()) {
            throw new NullException("Facility Details");
        } else {
            return this.facility;
        }
    }
}
