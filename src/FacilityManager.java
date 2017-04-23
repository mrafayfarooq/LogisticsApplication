import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */

public class FacilityManager implements LogisticManager {
    private final HashMap<Integer, List<String>> facility = new HashMap<>();
    private final HashMap<Integer, List<Integer>> scheduler = new HashMap<>();
    private final HashMap<String, Integer> facilityUtility = new HashMap<>();
    private final String idTag;
    private final String locationTag;
    private final String powerTag;
    private final String costTag;

    FacilityManager() {
        this.idTag = "Id";
        this.locationTag = "Location";
        this.powerTag = "ProcessingPowerPerDay";
        this.costTag = "Cost";
    }
    public void setScheduler() {
        try {
            for(int i=1;i<=18;i++) {
                List<Integer> copies = Collections.nCopies(20, Integer.valueOf(getProcessingPower(i)));
                scheduler.put(i, copies);
            }
        } catch (NullException e) {
            e.printStackTrace();
        }
    }
    public List getSchedule(Integer key) {
        return scheduler.get(key);
    }
    public void load(NodeList facilities) {
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

            List <String> facilityDetails = Arrays.asList(location, processingPowerPerDay, cost);
            facility.put(Integer.valueOf(facilityId), facilityDetails);
            facilityUtility.put(location.replaceAll("(^ )|( $)", ""), Integer.valueOf(facilityId));
        }
    }
    public HashMap<Integer, List<String>> getFacilityDetails() throws NullException {
        if(this.facility.isEmpty()) {
            throw new NullException("Facility Details");
        } else {
            return this.facility;
        }
    }
    public List<String> getDetails(Integer key) throws  NullException {
        if(this.facility.isEmpty()) {
            throw new NullException("Facility Details");
        } else {
            return this.facility.get(key);
        }
    }
    private String getProcessingPower(Integer key) throws NullException {
        List <String> tempList = getDetails(key);
        if(tempList.isEmpty()) {
            throw new NullException("Facility Details");
        } else {
            return tempList.get(1).replace(" ", "");
        }
    }
    public int getFacilityId(String facilityName) {
        return facilityUtility.get(facilityName);
    }
    public String getFacilityString(Integer facilityId) {
        return facility.get(facilityId).get(0);
    }
}
