import org.w3c.dom.NodeList;
/**
 * Created by Muhammad Rafay on 4/8/17.
 */
class main {
    public static void main(String[] args) {
        try {
            ParserContext parserContext = new ParserContext("XML");
            FacilityManager facilityManager = new FacilityManager(parserContext);
            ItemManager.getInstance().loadItems(parserContext.getEntries()[1]);
            Output output = new Output(facilityManager);
            output.printFacilityDetails();
            output.printItemCatalog();
            output.printShortestPath("Santa Fe, NM", "Chicago, IL");
            output.printShortestPath("Atlanta, GA", "St. Louis, MO");
            output.printShortestPath("Seattle, WA", "Nashville, TN");
            output.printShortestPath("New York City, NY", "Phoenix, AZ");
            output.printShortestPath("Fargo, ND", "Austin, TX");
            output.printShortestPath("Denver, CO", "Miami, FL");
            output.printShortestPath("Austin, TX", "Norfolk, VA");
            output.printShortestPath("Miami, FL", "Seattle, WA");
            output.printShortestPath("Los Angeles, CA", "Chicago, IL");
            output.printShortestPath("Detroit, MI", "Nashville, TN");
        } catch (NullException e) {
            e.printException();
        }
    }
}