import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.text.DecimalFormat;


/**
 * Created by Muhammad Rafay on 4/26/17.
 */
public class FacilityImplmentation implements Facility {
    protected final HashMap<String, List<String>> facility = new HashMap<>();
    private static final HashMap<Integer, List<String>> facilityUtility = new HashMap<>();
    private HashMap<Integer, List<Integer>> scheduler = new HashMap<>();
    private final HashMap<String, List<String>> network = new HashMap<>();
    private InventoryManager inventoryManager;
    private ShortestPathCalculator shortestPathCalculator;
    private final DecimalFormat distanceFormatter = new DecimalFormat("#,### mi");
    private final DecimalFormat daysFormatterTwo = new DecimalFormat("#0.00");


    FacilityImplmentation(NodeList[] facilityDetails) {
        loadFacility(facilityDetails[0]);
        loadNetworks(facilityDetails[0]);
        inventoryManager = new InventoryManager(facilityDetails[2]);
        shortestPathCalculator = ShortestPathCalculator.getInstance(this);
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
                    String networkId = aMap.getNamedItem("Id").getNodeValue().trim();
                    String location = elem.getElementsByTagName("Location").item(0).getTextContent().trim();
                    String distance = elem.getElementsByTagName("Distance").item(0).getTextContent().trim();
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
            String facilityId = aMap.getNamedItem("Id").getNodeValue().trim();

            // Get Attributes
            Element elem = (Element) facilities.item(i);
            String location = elem.getElementsByTagName("Location").item(0).getTextContent().trim();
            String processingPowerPerDay = elem.getElementsByTagName("ProcessingPowerPerDay").item(0).getTextContent().trim();
            String cost = elem.getElementsByTagName("Cost").item(0).getTextContent().trim();

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
            throw new NullException("Facility " + facilityName);
        } else {
            return this.network.get(facilityName);
        }
    }
    public List<String> getInventory(String facilityName) throws NullException {
        return inventoryManager.getDetails(facilityName);
    }
    public List<String> getDepletedInventory(String facilityName) throws NullException {
        return inventoryManager.getDepletedInventory(facilityName);
    }
    public List getShortestPath(String source, String destination) throws NullException {
        Map<Integer, Map<Integer, List<Integer>>> shortestDistance = shortestPathCalculator.getShortestDistance();
        Map<Integer, List<Integer>> pathDetails = (shortestDistance.get(this.getFacilityId(source)));
        Integer distance = pathDetails.get(this.getFacilityId(destination)).get(0);
        Integer s = pathDetails.get(this.getFacilityId(destination)).get(1);
        pathDetails.get(this.getFacilityId(destination)).remove(distance);
        pathDetails.get(this.getFacilityId(destination)).remove(s);

        List path = new ArrayList();
        for (int values : pathDetails.get(this.getFacilityId(destination))) {
            path.add(getFacilityString(values));
        }
        path.add(distance);
        return path;
    }

    private int getProcessingPower(String facilityName) throws NullException {
        return Integer.valueOf(facility.get(facilityName).get(1));
    }
    private int getFacilityId(String facilityName) throws NullException {
        if (facility.get(facilityName) == null)
            throw new NullException("Facility " + facilityName);
        else {
            return Integer.valueOf(facility.get(facilityName).get(0));
        }
    }
    public static String getFacilityString(Integer facilityId) throws NullException {
        if(facilityId <= 0) {
            throw new NullException("Facility Id" + facilityId);
        } else {
            return facilityUtility.get(facilityId).get(0);
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
