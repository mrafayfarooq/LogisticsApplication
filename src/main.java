import org.w3c.dom.NodeList;
/**
 * Created by Muhammad Rafay on 4/8/17.
 */
class main {
    public static void main(String[] args) {
        String facilitiesAndNetworks = "inputs/Facilities&Network.xml";
        String itemCatalog = "inputs/ItemCatalog.xml";
        String facilityInventory = "inputs/FacilityInventory.xml";

        XMLParser parser = new XMLParser();
        try {
            parser.parseFileWithName(facilitiesAndNetworks);
            NodeList parsedFile = parser.getXmlEntries();
            Output out = new Output();

            LogisticManager facility = LogisticFactory.getObject("Facility");
            facility.load(parsedFile);
            ((FacilityManager) facility).setScheduler();

            LogisticManager network  = LogisticFactory.getObject("Network");
            network.load(parsedFile);


            LogisticManager inventory = LogisticFactory.getObject("Inventory");
            parser.parseFileWithName(facilityInventory);
            parsedFile = parser.getXmlEntries();
            inventory.load(parsedFile);


            out.printFacilityDetails(facility, network, inventory);



            ItemManager itemManager = new ItemManager();
            parser.parseFileWithName(itemCatalog);
            parsedFile = parser.getXmlEntries();
            itemManager.loadItems(parsedFile);

            out.printItemCatalog(itemManager);

            ShortestPathCalculator shortestPathCalculator = new ShortestPathCalculator((NetworkManager) network);
            FacilityManager facilityManager = ((FacilityManager)facility);

            out.printShortestPath("Santa Fe, NM", "Chicago, IL", facilityManager, shortestPathCalculator);
            out.printShortestPath("Atlanta, GA", "St. Louis, MO", facilityManager, shortestPathCalculator);
            out.printShortestPath("Seattle, WA", "Nashville, TN", facilityManager, shortestPathCalculator);
            out.printShortestPath("New York City, NY", "Phoenix, AZ", facilityManager, shortestPathCalculator);
            out.printShortestPath("Fargo, ND", "Austin, TX", facilityManager, shortestPathCalculator);
            out.printShortestPath("Denver, CO", "Miami, FL", facilityManager, shortestPathCalculator);
            out.printShortestPath("Austin, TX", "Norfolk, VA", facilityManager, shortestPathCalculator);
            out.printShortestPath("Miami, FL", "Seattle, WA", facilityManager, shortestPathCalculator);
            out.printShortestPath("Los Angeles, CA", "Chicago, IL", facilityManager, shortestPathCalculator);
            out.printShortestPath("Detroit, MI", "Nashville, TN", facilityManager, shortestPathCalculator);
            
        } catch (NullException e) {
            e.printException();
        }

    }
}