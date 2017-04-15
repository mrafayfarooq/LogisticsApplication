import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Formatter;
/**
 * Created by Muhammad Rafay on 4/12/17.
 */
public class Output  {
    private List listDetails = new ArrayList<>();
    private DecimalFormat days = new DecimalFormat("#0.0");
    private StringBuilder stringBuilder = new StringBuilder();

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
                String formattedLocation  = networkDetailsList.get(0).replaceAll("^ *", "");
                String formattedDistance  = networkDetailsList.get(1).replaceAll(" ", "");
                float distance = ((float)(Integer.valueOf(formattedDistance))/400);
                System.out.printf(formattedLocation + "(" + days.format(distance) + "d); ");
            }
            System.out.println("");
        } catch (NullException e) {
                e.printStackTrace();
        }
    }
    private void printFacilityInventory(LogisticManager logisticManager, Integer key) {
        try {
            listDetails = logisticManager.getDetails(key);
            java.util.Collections.sort(listDetails);
            System.out.println("Active Inventory:");
            Formatter formatter = new Formatter();
          //  System.out.printf("  ");
            formatter.format("%-12s %-12s\n", "Item ID", "Quantity");

            for (Object list : listDetails) {
                List<String> inventoryDetails = Arrays.asList(list.toString().split(":"));
                String formattedQuantity  = inventoryDetails.get(1).replaceAll(" ", "");
                String formattedItem  = inventoryDetails.get(0).replaceAll(" ", "");
                formatter.format("%-12s %-12s\n", formattedItem, formattedQuantity);

                //     System.out.printf(inventoryDetails.get(0));
             //   System.out.println(inventoryDetails.get(1));
           //     System.out.println(formatter.format("%,12d", Integer.valueOf(formattedQuantity)));
                /*String networkDetails = list.toString().replaceAll("^ *","");
                List<String> networkDetailsList = Arrays.asList(networkDetails.split("-"));
                String formattedLocation  = networkDetailsList.get(0).replaceAll("^ *", "");
                float distance = ((float)(Integer.valueOf(formattedDistance))/400);
                System.out.printf(formattedLocation + "(" + days.format(distance) + "d); ");*/
            }
            System.out.println(formatter);
                  // System.out.println("");
        } catch (NullException e) {
            e.printStackTrace();
        }
    }

    public void printFacilityDetails(LogisticManager facility, LogisticManager network, LogisticManager inventory) throws NullException {
        for (int i = 1; i<=18; i++) {
            this.printFacilityDetails(facility,i);
            this.printNetworkDetails(network, i);
            this.printFacilityInventory(inventory,i);
        }
    }
}

