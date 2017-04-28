import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/26/17.
 */
public class FacilityImplmentation implements Facility {
    private HashMap<Integer, List<String>> facility = new HashMap<>();
    private HashMap<Integer, List<Integer>> scheduler = new HashMap<>();
    private HashMap<String, Integer> facilityUtility = new HashMap<>();

    public void loadFacility(NodeList facilities) {
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
            String facilityId = aMap.getNamedItem("Id").getNodeValue();

            // Get Attributes
            Element elem = (Element) facilities.item(i);
            String location = elem.getElementsByTagName("Location").item(0).getTextContent();
            String processingPowerPerDay = elem.getElementsByTagName("ProcessingPowerPerDay").item(0).getTextContent();
            String cost = elem.getElementsByTagName("Cost").item(0).getTextContent();

            List <String> facilityDetails = Arrays.asList(location, processingPowerPerDay, cost);
            facility.put(Integer.valueOf(facilityId), facilityDetails);
            facilityUtility.put(location.replaceAll("(^ )|( $)", ""), Integer.valueOf(facilityId));
        }
        this.setScheduler();
    }

    public List getScheduleOfFacility(String facilityName) throws NullException {
        return scheduler.get(getFacilityId(facilityName));
    }
    public int getProcessingPower(String facilityName) throws NullException {
        return Integer.valueOf(getDetails(facilityName).get(1).trim());
    }

    public List<String> getDetails(String facilityName) throws NullException {
        return this.facility.get(getFacilityId(facilityName));
    }

    private int getFacilityId(String facilityName) throws NullException {
        if (facilityUtility.get(facilityName) == null)
            throw new NullException("Facility Name");
        else {
            return facilityUtility.get(facilityName);
        }
    }
    private String getFacilityString(Integer facilityId) {
        return facility.get(facilityId).get(0).trim();
    }

    private void setScheduler() {
        try {
            for(int i=1;i<=18;i++) {
                List<Integer> copies = Collections.nCopies(20, getProcessingPower(getFacilityString(i)));
                scheduler.put(i, copies);
            }
        } catch (NullException e) {
            e.printException();
        }
    }
}
