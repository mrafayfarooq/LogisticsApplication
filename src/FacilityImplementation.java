import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.*;

/**
 * Created by Muhammad Rafay on 4/26/17.
 *
 * FacilityImpl class is implementing Facility and composed of InventoryManager
 * and Shortest Path Calculator. The Constructor of this class takes list
 * which contains parsed data necessary to make inventory object.
 * All the "Facility" interface methods are implemented with addition to
 * some utility methods needed to do the necessary calculation.
 */
public class FacilityImplementation implements Facility {
    private final HashMap<String, List<String>> facility = new HashMap<>(); // <FacilityName , ID - Processing Power - Cost>
    private static final HashMap<Integer, List<String>> facilityUtility = new HashMap<>(); // Utility Hash Map for getting Facility Name from ID
    private final HashMap<String, List<String>> network = new HashMap<>(); // <FacilityName, List of Facility connected>
    private final InventoryManager inventoryManager; // Inventory Object for managing facility Inventory
    private final ShortestPathCalculator shortestPathCalculator; // Object to calculate shortest path
    private Scheduler scheduler;

    /**
     * +
     * Constructor loading the facility and networks
     *
     * @param facilityDetails facility details list
     */
    FacilityImplementation(NodeList facilityDetails[]) throws NullException {
        /*+
          We are loading the facility, network and making
          inventory and shortest path object.
         */
        scheduler = new Scheduler(this);
        loadFacility(facilityDetails[0]);
        loadNetworks(facilityDetails[0]);
        inventoryManager = new InventoryManager(facilityDetails[1]);
        shortestPathCalculator = ShortestPathCalculator.getInstance(this);

    }

    /**
     * +
     * Loading the Network
     *
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
                    // Getting the ID, Location and Distance seperated by "-"
                    String networkId = aMap.getNamedItem("Id").getNodeValue().trim();
                    String location = elem.getElementsByTagName("Location").item(0).getTextContent().trim();
                    String distance = elem.getElementsByTagName("Distance").item(0).getTextContent().trim();
                    networkLinks.add(networkId + "-" + location + "-" + distance);
                }
                // Adding networkDetails into List
                ArrayList<String> networkDetails = new ArrayList(networkLinks);
                network.putIfAbsent(getFacilityString(Integer.valueOf(facilityId)), networkDetails);
            }
        } catch (NullException e) {
            e.printException();
        }
    }

    /**
     * +
     * Load Facility Details
     *
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

            List<String> facilityDetails = Arrays.asList(facilityId, processingPowerPerDay, cost);
            List<String> facilityDetailsUtility = Arrays.asList(location, processingPowerPerDay, cost);

            facility.put(location.trim(), facilityDetails);
            facilityUtility.put(Integer.valueOf(facilityId), facilityDetailsUtility);
        }
        // Setting the Scheduler.
        this.setScheduler(facilityUtility);
    }

    /**
     * +
     * Get Schedule of Facility
     *
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public Map<Integer, Integer> getScheduleOfFacility(String facilityName) throws NullException {
        Map<Integer, Integer> copyScheduler = this.scheduler.getScheduleOfFacility(facilityName);
        return copyScheduler;
    }

    /**
     * +
     * Get Details of Facility
     *
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getDetails(String facilityName) throws NullException {
        if (this.facility.get(facilityName) == null) {
            throw new NullException("Details for Facility " + facilityName);
        } else {
            List<String> copyDetails = this.facility.get(facilityName);
            return copyDetails;
        }
    }

    /**
     * +
     * Get Network of Facility
     *
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getNetworks(String facilityName) throws NullException {
        if (this.network.get(facilityName) == null) {
            throw new NullException("Network for facility name " + facilityName);
        } else {
            List<String> copyDetails = this.network.get(facilityName);
            return copyDetails;
        }
    }

    /**
     * +
     * Get Inventory of Facility
     *
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getInventory(String facilityName) throws NullException {
        List<String> copyDetails = inventoryManager.getDetails(facilityName);
        return copyDetails;
    }

    /**
     * +
     * Get Depleted Inventory of Facility
     *
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public List<String> getDepletedInventory(String facilityName) throws NullException {
        List<String> copyDetails = inventoryManager.getDepletedInventory(facilityName);
        return copyDetails;
    }

    /**
     * +
     * Get Shortest Path between Source and Destination
     *
     * @param source      facility name
     * @param destination facility name
     * @return List of facility with shortest cost
     * @throws NullException if source or destination does not found.
     */
    public List<String> getShortestPath(String source, String destination) throws NullException {
        if (!source.equals(destination)) {
            // Get all Shortest Paths
            Map<Integer, Map<Integer, List<Integer>>> shortestDistance = new HashMap<>(shortestPathCalculator.getShortestDistance());
            // Get shortest path of source from all
            Map<Integer, List<Integer>> pathDetails = new HashMap<>(shortestDistance.get(this.getFacilityId(source)));
            // Get distance
            Integer distance = pathDetails.get(this.getFacilityId(destination)).get(0);
            // Adding path into Path List
            List path = new ArrayList();
            for (int values : pathDetails.get(this.getFacilityId(destination))) {
                if (values <= 18) { //temperory bug fix
                    path.add(getFacilityString(values));
                }
            }
            path.add(distance);
            pathDetails.clear();
            return path;
        } else {
            return null;
        }
    }

    /**
     * +
     * Get Processing Power of Facility
     *
     * @param facilityName name of the facility
     * @return processing power in integer
     */
    public int getProcessingPower(String facilityName) throws NullException {
        if (facility.get(facilityName) == null)
            throw new NullException("Facility " + facilityName);
        else {
            return Integer.valueOf(facility.get(facilityName).get(1));
        }
    }

    /**
     * +
     * Get Facility ID if passed facility Name. This is utility function
     * to easily parsed through facilities.
     *
     * @param facilityName name of the facility
     * @return facility id
     * @throws NullException if facility doesnt exist
     */
    public int getFacilityId(String facilityName) throws NullException {
        if (facility.get(facilityName) == null)
            throw new NullException("Facility " + facilityName);
        else {
            return Integer.valueOf(facility.get(facilityName).get(0));
        }
    }

    /**
     * +
     * A static function which will get Facility String from facility ID. A utility function
     * used by other classes.
     *
     * @param facilityId ID of the facility
     * @return String the facility name
     * @throws NullException if facility name does not exist
     */
    public static String getFacilityString(Integer facilityId) throws NullException {
        if (facilityId <= 0) {
            throw new NullException("Facility Id " + facilityId);
        } else {
            return facilityUtility.get(facilityId).get(0);
        }
    }

    /**
     * +
     * Get the list of facilities with Item
     *
     * @param itemId id of the order
     * @return list of facilties which has the item
     */
    public List getFacilitiesWithItem(String itemId) throws NullException {
        HashSet<String> facilities = new HashSet<>();
        for (int i = 1; i <= facility.size(); i++) {
            List listDetails = this.getInventory(getFacilityString(i));
            for (Object list : listDetails) {
                List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
                if (itemId.equals(inventoryDetails.get(0))) {
                    facilities.add(getFacilityString(i));
                }
            }
        }
        List<String> list = new ArrayList<>();
        list.addAll(facilities);
        return list;
    }

    public void reduceFacilityInventory(String facilityName, String itemId, int quantity) {
        inventoryManager.reduceFacilityInventory(facilityName, itemId, quantity);
    }

    /**
     * +
     * This class will be modified in next phase. Right now we are making 20 copies,
     * but eventually will be having schedule of more than that.
     *
     * @param facilityUtility
     * @throws NullException if Schedule is not present
     */
    private void setScheduler(HashMap<Integer, List<String>> facilityUtility) throws NullException {
        this.scheduler.setScheduler(FacilityImplementation.facilityUtility);
    }

    public int findArrivalDay(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        return this.scheduler.findArrivalDay(startDay, qunatityToProcess, facilityName, itemDetails);
    }
    public List setSchedule(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        return this.scheduler.setSchedule(startDay,qunatityToProcess,facilityName,itemDetails);
    }

    public int getQuantityOfItem(String facilityName, List itemDetails) throws NullException {
        return inventoryManager.getQuantityOfItem(facilityName,itemDetails);
    }

}
