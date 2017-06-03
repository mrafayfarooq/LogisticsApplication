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
            FacilityManager.getInstance().loadFacility(parserContext);
            // Print Outputs
            FacilityManager.getInstance().printPrettyOutput();
            // Process Orders
            FacilityManager.getInstance().processOrders(parserContext);
            // Print Outputs
            FacilityManager.getInstance().printPrettyOutput();

        } catch (NullException e) {
            e.printException();
        }
    }
}