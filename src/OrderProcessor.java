import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.*;

/**
 * Created by Muhammad Rafay on 5/6/17.
 */
public class OrderProcessor {
    OrderManager orderManager;
    ItemManager itemManager;
    DecimalFormat twoDForm = new DecimalFormat(".");

    OrderProcessor(OrderManager orderManager) {
        this.itemManager = ItemManager.getInstance();
        this.orderManager = orderManager;
    }

    public void processOrder(TreeMap<String, List<String>> order) throws NullException {
        for (Map.Entry<String, List<String>> entry : order.entrySet()) {
            // Get Order Details
            String orderID = entry.getKey();
            List itemOrdered = entry.getValue().subList(1,entry.getValue().size());
            String destination = entry.getValue().get(0);

            // Traverse all the order
            for(int i=0; i<itemOrdered.size(); i++) {
                // Get ItemID and Quantity
                List itemDetails = Arrays.asList(itemOrdered.get(i).toString().split(":"));

                // Check if Item Exist
                if(itemManager.checkItem(itemDetails.get(0).toString())) {
                    // Get the ID
                    String itemID = itemDetails.get(0).toString();
                    // Get the quantity
                    int quantity = Integer.parseInt(itemDetails.get(1).toString());
                    // Find all facilities which has that item
                    List facilitiesWithItem = orderManager.getFacilitiesWithItem(itemID);
                    // Find travel time
                    Map<String , Integer> facilitiesWithShortestPath = this.findFacilitiesWithShortestPath(destination, facilitiesWithItem);
                    // Find Total Processing Time
                    List<Entry<String,Integer>> facilityRecord = this.getProcessingTimeOfFacilities(facilitiesWithShortestPath, itemDetails,Integer.parseInt(destination.toString().split("-")[1]));
                  //  System.out.println(facilityRecord);
                    processItem(facilityRecord, itemDetails, destination);
                } else {
                    System.out.println("Item does not exist");
                    return;
                }
            }
        }
    }

    private Map<String, Integer> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        Map<String,Integer> facilityWithShortestPath = new HashMap();
        for (Object list : facilitiesWithItem) {
            List path = orderManager.getShortestPath(list.toString(), destination.split("-")[0]);
            if(path!= null) {
                int distance = (int) path.get(path.size()-1);
                float days = (float)distance/400;

                facilityWithShortestPath.put(list.toString(), (int) Math.ceil(days));
            }
        }
        return facilityWithShortestPath;
    }
    private int getTravelTime(String source, String destination) throws NullException {
        List path = orderManager.getShortestPath(source, destination);
        float days = 0;
        if(path!= null) {
            int distance = (int) path.get(path.size()-1);
            days = (float) distance/400;
        }
        return (int)Math.ceil(days);
    }


    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(Map<String, Integer> facilityWithShortestPath, List itemDetails, int startDay) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for (Map.Entry<String, Integer> entry : facilityWithShortestPath.entrySet()) {
            int totalProcessingTime = calculateProcessingEndDay(itemDetails, entry, startDay);
            facilitiesWithTravelTime.put(entry.getKey(), totalProcessingTime );
        }
       return sort(facilitiesWithTravelTime);
    }

    private int calculateProcessingEndDay(List itemDetails, Map.Entry<String, Integer> facility, int startDay) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
//        int quantityOfItemInFacility = getQuantityOfItem(facility.getKey(), itemDetails);
        int travelTime =  (int) Math.ceil(Double.parseDouble(facility.getValue().toString()));
        int endDay = orderManager.findArrivalDay(startDay, quantityToProcess, facility.getKey(), itemDetails) + travelTime;
        return endDay;
    }
    private int calculateProcessingEndDay(List itemDetails, String travelTime, String facilityName,  Integer startDay, int quantityToProcess) throws NullException {
        int travelTimeInFloat =  (int) Math.ceil(Double.parseDouble(travelTime.toString()));
        int endDay = orderManager.setSchedule(startDay, quantityToProcess, facilityName, itemDetails) + travelTimeInFloat;
        return (int)Math.ceil(endDay);
    }


    private int getQuantityOfItem(String facilityName, List itemDetails) throws NullException {
        List inventoryDetails = orderManager.getInventory(facilityName);
        for(Object list : inventoryDetails) {
            List<String> inventory = Arrays.asList(list.toString().split(":"));
            if (inventory.get(0).equals(itemDetails.get(0).toString())) {
                return Integer.parseInt(inventory.get(1));
            }
        }
        return 0;
    }
    private List<Entry<String,Integer>> sort(Map map) {
        Set<Entry<String, Integer>> set = map.entrySet();
        ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );
        return list;
    }

    private void processItem(List facilityRecords, List itemDetails, String startDay) throws NullException {
        Map<String, Integer > facilitySolution = new HashMap<>();
        int totalProcessingTime = 0;
        String travelTime;
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());

        for(Object entry:facilityRecords) {
            String facilityName = entry.toString().split("=")[0];
            int orderArrivalDay = Integer.valueOf(startDay.split("-")[1]);
            int quantityOfItemInFacility = getQuantityOfItem(facilityName, itemDetails);
            String itemID = itemDetails.get(0).toString();
            if(quantityToProcess <= 0) {
                System.out.println(facilitySolution);
                return;
            }
            if(quantityOfItemInFacility <= quantityToProcess) {
                travelTime = String.valueOf(getTravelTime(facilityName, startDay.split("-")[0]));
                totalProcessingTime = calculateProcessingEndDay(itemDetails, travelTime, facilityName, orderArrivalDay, quantityToProcess);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityOfItemInFacility);
                quantityToProcess = quantityToProcess - quantityOfItemInFacility;
                facilitySolution.put(facilityName, totalProcessingTime);
                calculateTotalCost(facilityName, itemDetails, quantityOfItemInFacility);

            } else if(quantityOfItemInFacility > quantityToProcess) {
                travelTime = String.valueOf(getTravelTime(facilityName, startDay.split("-")[0]));
                totalProcessingTime = calculateProcessingEndDay(itemDetails, travelTime, facilityName, orderArrivalDay, quantityToProcess);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityToProcess);
                calculateTotalCost(facilityName, itemDetails, quantityToProcess);
                quantityToProcess = 0;
                facilitySolution.put(facilityName, (int) Math.ceil(totalProcessingTime));
            }
        }
        System.out.println(facilitySolution);
    }
    private void calculateTotalCost(String facilityName, List itemDetails, int quantityOfItem) {
        int itemCost = calculateItemCost(itemDetails, quantityOfItem);
        int facilityProcessingCost = calculateFacilityCost(facilityName);

        System.out.println(itemCost);

    }
    private int calculateItemCost(List itemDetails, int quantityOfItems) {
        return ItemManager.getInstance().getItemCost(itemDetails.get(0).toString())*quantityOfItems;
    }
    private int calculateFacilityCost(String facilityName) {
        return 1;
    }

//    private int getProcessingPower(String facilityName) throws NullException {
//        List facilityDetails = new ArrayList(orderManager.getFacilityDetails(facilityName));
//        return Integer.valueOf((facilityDetails.get(1).toString()));
//    }
}
