import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.*;

/**
 * Created by Muhammad Rafay on 4/26/17.
 *
 * FacilityImpl class which is implmenting Facility and composed of InventoryManager
 * and Shortest Path Calculator
 */
public class FacilityImplmentation implements Facility {
    private final HashMap<String, List<String>> facility = new HashMap<>();
    private static final HashMap<Integer, List<String>> facilityUtility = new HashMap<>();
    private final HashMap<Integer, List<Integer>> scheduler = new HashMap<>();
    private final HashMap<String, List<String>> network = new HashMap<>();
    private final InventoryManager inventoryManager;
    private final ShortestPathCalculator shortestPathCalculator;

    /**+
     * Constructor loading the facility and networks
     * @param facilityDetails facility details list
     */
    FacilityImplmentation(NodeList facilityDetails[]) throws NullException {
        loadFacility(facilityDetails[0]);
        loadNetworks(facilityDetails[0]);
        inventoryManager = new InventoryManager(facilityDetails[1]);
        shortestPathCalculator = ShortestPathCalculator.getInstance(this);
    }

    /**+
     * Loading the Network
     * @param networks NodeList
     */
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
                // Adding networkDetails into List
                List<String> networkDetails = new ArrayList(networkLinks);
                network.putIfAbsent(getFacilityString(Integer.valueOf(facilityId)), networkDetails);
            }
        } catch (NullException e) {
            e.printException();
        }
    }

    /**+
     * Load Facility Details
     * @param facilities Nodelist of facility details
     */
    private void loadFacility(NodeList facilities) throws NullException {
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
        // Setting the Scheduler
        this.setScheduler();
    }

    /**+
     * Get Scedule of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List getScheduleOfFacility(String facilityName) throws NullException {
        return scheduler.get(getFacilityId(facilityName));
    }

    /**+
     * Get Details of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getDetails(String facilityName) throws NullException {
        return this.facility.get(facilityName);
    }

    /**+
     * Get Network of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getNetworks(String facilityName) throws NullException {
        return this.network.get(facilityName);
    }

    /**+
     * Get Inventory of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getInventory(String facilityName) throws NullException {
        return inventoryManager.getDetails(facilityName);
    }

    /**+
     * Get Depleted Inventory of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getDepletedInventory(String facilityName) throws NullException {
        return inventoryManager.getDepletedInventory(facilityName);
    }

    /**+
     * Get Shortest Path between Source and Destination
     * @param source facility name
     * @param destination facility name
     * @return List of facility with shortest cost
     * @throws NullException if source or destination does not found.
     */
    public List getShortestPath(String source, String destination) throws NullException {
        // Get all Shortest Paths
        Map<Integer, Map<Integer, List<Integer>>> shortestDistance = shortestPathCalculator.getShortestDistance();
        // Get shortest path of source from all
        Map<Integer, List<Integer>> pathDetails = (shortestDistance.get(this.getFacilityId(source)));
        // Get distance
        Integer distance = pathDetails.get(this.getFacilityId(destination)).get(0);
        // Remove the source from the list
        Integer s = pathDetails.get(this.getFacilityId(destination)).get(1);
        pathDetails.get(this.getFacilityId(destination)).remove(distance);
        pathDetails.get(this.getFacilityId(destination)).remove(s);

        // Adding path into Path List
        List path = new ArrayList();
        for (int values : pathDetails.get(this.getFacilityId(destination))) {
            path.add(getFacilityString(values));
        }
        path.add(distance);
        return path;
    }

    /**+
     * Get Processing Power of Facility
     * @param facilityName name of the facility
     * @return processing power in integer
     */
    private int getProcessingPower(String facilityName) throws NullException {
        if (facility.get(facilityName) == null)
            throw new NullException("Facility " + facilityName);
        else {
            return Integer.valueOf(facility.get(facilityName).get(1));
        }
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
    private void setScheduler() throws NullException {
        for(int i=1;i<=18;i++) {
            if(facilityUtility.get(i).isEmpty()) {
                throw new NullException("Facility Name");
            } else {
                List<Integer> copies = Collections.nCopies(20, getProcessingPower(facilityUtility.get(i).get(0).toString().trim()));
                scheduler.put(i, copies);
            }
        }
    }
}
