/**
 * Created by Muhammad Rafay on 4/14/17.
 */
class LogisticFactory {

    public static LogisticManager getObject(String object) throws NullException {
        if (object.equals("Facility")) {
            return new FacilityManager("Id", "Location", "ProcessingPowerPerDay", "Cost");
        } else if (object.equals("Inventory")) {
            return new InventoryManager("Id", "ItemID", "Quantity");
        } else if (object.equals("Network")) {
            return new NetworkManager("Location", "Distance");
        }
        throw new NullException("Facility Inventory Details");
    }
}
