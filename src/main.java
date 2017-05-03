/**
 * Created by Muhammad Rafay on 4/8/17.
 */

class Main {
    public static void main(String[] args) {
        try {
            // Loading File with XML
            ParserContext parserContext = new ParserContext("XML");
            // Loading Item Catalog and its details
            ItemManager.getInstance().loadItems(parserContext.getEntries("ItemCatalog"));
            // Loading Facility and it's details
            FacilityManager facilityManager = new FacilityManager(parserContext);
             // Preparing for output
            Output output = new Output(facilityManager);
            // Output data
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