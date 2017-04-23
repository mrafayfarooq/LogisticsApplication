import java.util.*;
import java.text.DecimalFormat;

/**
 * Created by Muhammad Rafay on 4/12/17.
 */
class Output  {
    private List listDetails = new ArrayList<>();
    private final DecimalFormat daysFormatter = new DecimalFormat("#0.0");
    private final DecimalFormat costFormatter = new DecimalFormat("$#,###");


    private void printFacilityDetails(LogisticManager logisticManager, Integer key) {
        try {
            listDetails = logisticManager.getDetails(key);
            System.out.println(listDetails.get(0).toString().replaceAll("^ *","").split(",")[0]);
            System.out.println("Rate per Day:" + listDetails.get(1));
            System.out.println("Cost per Day: $"+listDetails.get(2).toString().replace(" ",""));
        } catch (NullException e) {
            e.printStackTrace();
        }
    }
    private void printNetworkDetails(LogisticManager logisticManager, Integer key) {
        try {
            listDetails = logisticManager.getDetails(key);
            java.util.Collections.sort(listDetails);
            System.out.println("Direct Links:");
            for (Object list : listDetails) {
                String networkDetails = list.toString().replaceAll("^ *","");
                List<String> networkDetailsList = Arrays.asList(networkDetails.split("-"));
                String formattedLocation  = networkDetailsList.get(1).replaceAll("^ *", "");
                String formattedDistance  = networkDetailsList.get(2).replaceAll(" ", "");
                float distance = ((float)(Integer.valueOf(formattedDistance))/400);
                System.out.printf(formattedLocation + "(" + daysFormatter.format(distance) + "d); ");
            }
            System.out.println("");
        } catch (NullException e) {
                e.printStackTrace();
        }
    }
    private void printFacilityInventory(LogisticManager logisticManager, Integer key) {
        try {
            listDetails = logisticManager.getDetails(key);
            Collections.sort(listDetails);
            System.out.println("Active Inventory:");
            Formatter formatter = new Formatter();
            formatter.format("%-12s %-12s\n", "Item ID", "Quantity");

            for (Object list : listDetails) {
                List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
                String formattedQuantity  = inventoryDetails.get(1).replaceAll(" ", "");
                String formattedItem  = inventoryDetails.get(0).replaceAll(" ", "");
                formatter.format("%-12s %-12s\n", formattedItem, formattedQuantity);
            }
            System.out.println(formatter);
        } catch (NullException e) {
            e.printStackTrace();
        }
    }
    private void printDepletedInventory(LogisticManager inventoryManager, Integer key) {
        List<String> depletedInventory = ((InventoryManager) inventoryManager).getDepletedInventory(key);
        System.out.printf("Depleted (Used-Up) Inventory:");
        if(depletedInventory.isEmpty()) {
            System.out.println(" None");
        } else {
            System.out.println(depletedInventory.toString().replace("[","").replace("]",""));
        }
    }
    private void printSceduleOfFacility(FacilityManager facility, Integer key) {
        List<String> schedule = facility.getSchedule(key);
        System.out.println("Schedule:");
        System.out.format(String.format("%1s", "Day:"));
        System.out.printf("      ");
        for(int i = 1; i<=20; i++) {
            System.out.format(String.format("%4s", String.valueOf(i)));
        }
        System.out.println();
        System.out.format(String.format("%1s", "Available:"));
        for (Object list : schedule) {
            System.out.format(String.format("%4s",list.toString().replace("[","").replace(",","").replace("]","")));
        }
        System.out.println();
    }
    public void printFacilityDetails(LogisticManager facility, LogisticManager network, LogisticManager inventory) {
        for (int i = 1; i<=18; i++) {
            this.printFacilityDetails(facility,i);
            System.out.println();
            this.printNetworkDetails(network, i);
            System.out.println();
            this.printFacilityInventory(inventory,i);
            this.printDepletedInventory(inventory, i);
            System.out.println();
            this.printSceduleOfFacility(((FacilityManager)facility), i);
            System.out.println();

        }
    }
    public void printItemCatalog(ItemManager item) throws NullException {
        TreeMap<String, String> Item = item.getItemDetails();
        Item.forEach( (k,v) -> {
            double cost = Double.parseDouble(v);
            System.out.println(k.replace(" ", "") + ": " + String.format("%5s",costFormatter.format(cost)));
        });
    }
}

