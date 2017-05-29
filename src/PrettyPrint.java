
import java.util.*;
import java.text.DecimalFormat;


/**
 * Created by Muhammad Rafay on 4/12/17.
 */

class PrettyPrint  {
    private List listDetails = new ArrayList<>();
    private final DecimalFormat daysFormatter = new DecimalFormat("#0.0");
    private final DecimalFormat daysFormatterTwo = new DecimalFormat("#0.00");
    private static final DecimalFormat costFormatter = new DecimalFormat("$#,###");
    private final DecimalFormat distanceFormatter = new DecimalFormat("#,### mi");
    private final Facility facilityManager;
    PrettyPrint(Facility facilityManager) {
        this.facilityManager = facilityManager;
    }

    private void printFacilityDetails(String facilityName) throws NullException {
        if (facilityManager.getDetails(facilityName).isEmpty()) {
            throw new NullException("Facility " + facilityName);
        } else {
            listDetails = facilityManager.getDetails(facilityName);
            System.out.println(facilityName.split(",")[0]);
            for(int i=0;i<facilityName.split(",")[0].length();i++) {
                System.out.printf("-");
            }
            System.out.println();
            System.out.println("Rate per Day: " + listDetails.get(1));
            System.out.println("Cost per Day: $" + listDetails.get(2));
        }
    }

    private void printNetworkDetails(String facilityName) throws NullException {
        if (facilityManager.getNetworks(facilityName).isEmpty()) {
            throw new NullException("Facility " + facilityName);
        } else {
            listDetails = facilityManager.getNetworks(facilityName);
            java.util.Collections.sort(listDetails);
            System.out.println("Direct Links:");
            for (Object list : listDetails) {
                List<String> networkDetailsList = Arrays.asList(list.toString().split("-"));
                String formattedLocation = networkDetailsList.get(1);
                String formattedDistance = networkDetailsList.get(2);
                float distance = ((float) (Integer.valueOf(formattedDistance)) / 400);
                System.out.printf(formattedLocation + " (" + daysFormatter.format(distance) + "d); ");
            }
        }
    }
    private void printFacilityInventory(String  facilityName) throws NullException {
        if(facilityManager.getInventory(facilityName).isEmpty()) {
            throw new NullException("Facility " + facilityName);
        } else {
            listDetails = this.facilityManager.getInventory(facilityName);
            System.out.println("\nActive Inventory:");
            Formatter formatter = new Formatter();
            formatter.format("%-12s %-12s\n", "Item ID", "Quantity");

            for (Object list : listDetails) {
                List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
                String formattedQuantity  = inventoryDetails.get(1);
                String formattedItem  = inventoryDetails.get(0);
                formatter.format("%-12s %-12s\n", formattedItem, formattedQuantity);
            }
            System.out.println(formatter);
        }
    }
    private void printDepletedInventory(String facilityName) throws NullException {
        List<String> depletedInventory = this.facilityManager.getDepletedInventory(facilityName);
        System.out.printf("Depleted (Used-Up) Inventory:");
        if(depletedInventory.isEmpty()) {
            System.out.println(" None");
        } else {
            System.out.println(depletedInventory.toString().replace("[","").replace("]",""));
        }
    }
    private void printScheduleOfFacility(String facilityName) throws NullException {

        Map<Integer, Integer> schedule = this.facilityManager.getScheduleOfFacility(facilityName);
        System.out.println("Schedule:");
        System.out.format(String.format("%1s", "Day:"));
        System.out.printf("      ");
        for(int i = 1; i<=20; i++) {
            System.out.format(String.format("%4s", String.valueOf(i)));
        }
        System.out.println();
        System.out.format(String.format("%1s", "Available:"));
        for(int i = 1; i<=20; i++) {
            System.out.format(String.format("%4d", schedule.get(i)));
        }
//        schedule.forEach( (k,v) -> {
//            System.out.format(String.format("%4s",v));
//        });
        System.out.println();
    }
    public void printFacilityDetails() throws NullException {
        for (int i = 1; i<=FacilityImplementation.getFacilityQuantities(); i++) {
            for(int j=0; j<60; j++) {
                System.out.printf("-");
            }
            System.out.println();
            String facilityName = FacilityImplementation.getFacilityString(i);
            this.printFacilityDetails(facilityName);
            System.out.println();
            this.printNetworkDetails(facilityName);
            System.out.println();
            this.printFacilityInventory(facilityName);
            this.printDepletedInventory(facilityName);
            System.out.println();
            this.printScheduleOfFacility(facilityName);
            System.out.println();
        }
    }
    public void printItemCatalog() throws NullException {
        TreeMap<String, String> Item = ItemManager.getInstance().getItemDetails();
        Item.forEach( (k,v) -> {
            double cost = Double.parseDouble(v);
            System.out.println(k.replace(" ", "") + ": " + String.format("%5s",costFormatter.format(cost)));
        });
        System.out.println();
    }

    public void printShortestPath(String source, String destination) throws NullException {
        List paths = this.facilityManager.getShortestPath(source,destination);
        int distance = (int) paths.get(paths.size()-1);
        paths.remove(paths.size()-1);
        System.out.printf("%s to %s:\n • %s ", source, destination, source);
        for (Object path : paths) {
            if (!path.equals(source)) {
                System.out.printf("-> %s ", path);
            }
        }
        System.out.printf("= " + distanceFormatter.format(distance));
        System.out.println("\n • " + distanceFormatter.format(distance) + " / " + "(8 hours per day * 50 mph) = " + daysFormatterTwo.format((float)distance/400) + " days \n");
    }

    public static void prettyPrint(Map.Entry<String, List<String>> entry) {
        for(int j=0; j<60; j++) {
            System.out.printf("-");
        }
        System.out.println();
        System.out.println("Order #" + Character.toString(entry.getKey().split("-")[1].charAt(2)));
        System.out.println("Order Id:       " + entry.getKey());
        System.out.println("Order Time:     " + "Day " + entry.getValue().get(0).split("-")[1]);
        System.out.println("Destination:    " + entry.getValue().get(0).split("-")[0]);
        System.out.println();
        System.out.println("List of Order Items:");
        for(int i=1; i< entry.getValue().size(); i++) {
            System.out.print(i + ")" + "  Item ID:  " + entry.getValue().get(i).split(":")[0] + ",");
            System.out.print("  Quantity:  " + entry.getValue().get(i).split(":")[1]);
            System.out.println();
        }
        System.out.println();
        System.out.println("Processing Solution:");
        System.out.println();

    }
    public static void prettyPrint(Map<String, List<Integer>> facilitySolution) {
        Formatter formatter = new Formatter();
        formatter.format("%18s %18s %8s %19s \n", "Facility ", "Quantity", "Cost",  "Arrival Day");
        int i = 1;
        int totalCost = 0;
        int totalQuantity = 0;
        for (Map.Entry<String, List<Integer>> entry : facilitySolution.entrySet()) {
            formatter.format("%8s %-19s %-12s %-12s %-12s\n", i+")", entry.getKey(), entry.getValue().get(1), costFormatter.format(entry.getValue().get(2)), entry.getValue().get(0));
            i++;
            totalCost = totalCost + entry.getValue().get(2);
        }
        formatter.format("%18s %18s %8s %19s \n", "TOTAL ", "Quantity", totalCost,  "Arrival Day");
        System.out.println(formatter);
        System.out.println();
    }
}


