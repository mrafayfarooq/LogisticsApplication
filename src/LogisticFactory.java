/**
 * Created by Muhammad Rafay on 4/14/17.
 */
class LogisticFactory {

    public static LogisticManager getObject(String object) throws NullException {
        switch (object) {
            case "Facility":
                return new FacilityManager();
            case "Inventory":
                return new InventoryManager();
            case "Network":
                return new NetworkManager();
        }
        throw new NullException("Facility Inventory Details");
    }
}
