
import java.util.*;
import java.text.DecimalFormat;


/**
 * Created by Muhammad Rafay on 4/12/17.
 */

class Output  {
    private List listDetails = new ArrayList<>();
    private final DecimalFormat daysFormatter = new DecimalFormat("#0.0");
    private final DecimalFormat daysFormatterTwo = new DecimalFormat("#0.00");

    private final DecimalFormat costFormatter = new DecimalFormat("$#,###");
    private final DecimalFormat distanceFormatter = new DecimalFormat("#,### mi");
    private final FacilityManager facilityManager;

    Output(FacilityManager facilityManager) {
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
                String formattedQuantity  = inventoryDetails.get(1).replaceAll(" ", "");
                String formattedItem  = inventoryDetails.get(0).replaceAll(" ", "");
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
    private void printSceduleOfFacility(String facilityName) throws NullException {

        List<String> schedule = this.facilityManager.getScheduleOfFacility(facilityName);
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
    public void printFacilityDetails() throws NullException {
        for (int i = 1; i<=18; i++) {
            String facilityName = FacilityImplmentation.getFacilityString(i);
            this.printFacilityDetails(facilityName);
            System.out.println();
            this.printNetworkDetails(facilityName);
            System.out.println();
            this.printFacilityInventory(facilityName);
            this.printDepletedInventory(facilityName);
            System.out.println();
            this.printSceduleOfFacility(facilityName);
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
            System.out.printf("-> %s ", path);
        }
        System.out.printf("= " + distanceFormatter.format(distance));
        System.out.println("\n • " + distanceFormatter.format(distance) + " / " + "(8 hours per day * 50 mph) = " + daysFormatterTwo.format((float)distance/400) + " days \n");
    }

}


