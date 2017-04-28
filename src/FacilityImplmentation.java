import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created by Muhammad Rafay on 4/26/17.
 */
public class FacilityImplmentation implements Facility {
    private HashMap<String, List<String>> facility = new HashMap<>();
    private HashMap<Integer, List<String>> facilityUtility = new HashMap<>();
    private HashMap<Integer, List<Integer>> scheduler = new HashMap<>();
    protected final HashMap<String, List<String>> network = new HashMap<>();
    private static FacilityImplmentation instance = null;
    private FacilityImplmentation(NodeList facilityDetails) {
        loadFacility(facilityDetails);
        loadNetworks(facilityDetails);
    }
    public static FacilityImplmentation getInstance(NodeList facilityDetails){
        if(instance == null){
            instance = new FacilityImplmentation(facilityDetails);
        }
        return instance;
    }

    private void loadNetworks(NodeList networks) {
        try {
            for (int i = 0; i < networks.getLength(); i++) {
                if (networks.item(i).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }

                // Get Facility Id
                NamedNodeMap aMap = networks.item(i).getAttributes();
                String facilityId = aMap.getNamedItem("Id").getNodeValue();

                // Get Networks
                Element elem = (Element) networks.item(i);
                NodeList networkList = elem.getElementsByTagName("Facility");
                ArrayList<String> networkLinks = new ArrayList<>();

                for (int j = 0; j < networkList.getLength(); j++) {
                    if (networkList.item(j).getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }
                    elem = (Element) networkList.item(j);
                    aMap = elem.getAttributes();
                    String networkId = aMap.getNamedItem("Id").getNodeValue();
                    String location = elem.getElementsByTagName("Location").item(0).getTextContent();
                    String distance = elem.getElementsByTagName("Distance").item(0).getTextContent();
                    networkLinks.add(networkId + "-" + location + "-" + distance);
                }
                List<String> networkDetails = new ArrayList(networkLinks);
                network.putIfAbsent(getFacilityString(Integer.valueOf(facilityId)), networkDetails);
            }
        } catch (NullException e) {
                e.printStackTrace();
        }
    }
    private void loadFacility(NodeList facilities) {
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

            List <String> facilityDetails = Arrays.asList(facilityId, processingPowerPerDay, cost);
            List <String> facilityDetailsUtility = Arrays.asList(location, processingPowerPerDay, cost);

            facility.put(location.trim(), facilityDetails);
            facilityUtility.put(Integer.valueOf(facilityId), facilityDetailsUtility);
        }
        this.setScheduler();
    }

    public List getScheduleOfFacility(String facilityName) throws NullException {
        return scheduler.get(getFacilityId(facilityName));
    }
    public List<String> getDetails(String facilityName) throws NullException {
        return this.facility.get(facilityName);
    }
    public List<String> getNetworks(String facilityName) throws NullException {
        if(this.network.isEmpty()) {
            throw new NullException("Network Details");
        } else {
            return this.network.get(facilityName);
        }
    }
    private int getProcessingPower(String facilityName) throws NullException {
        return Integer.valueOf(facility.get(facilityName).get(1).trim());
    }
    private int getFacilityId(String facilityName) throws NullException {
        if (facility.get(facilityName) == null)
            throw new NullException("Facility Name");
        else {
            return Integer.valueOf(facility.get(facilityName).get(0).trim());
        }
    }
    private String getFacilityString(Integer facilityId) throws NullException {
        if(facilityId <= 0) {
            throw new NullException("Facility Id");
        } else {
            return facilityUtility.get(facilityId).get(0).toString().trim();
        }
    }

    private void setScheduler() {
        try {
            for(int i=1;i<=18;i++) {
                List<Integer> copies = Collections.nCopies(20, getProcessingPower(facilityUtility.get(i).get(0).toString().trim()));
                scheduler.put(i, copies);
            }
        } catch (NullException e) {
            e.printException();
        }
    }
}
