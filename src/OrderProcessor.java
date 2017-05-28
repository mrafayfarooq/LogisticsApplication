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
    CostCalculator costCalculator;
    DecimalFormat twoDForm = new DecimalFormat(".");

    OrderProcessor(OrderManager orderManager) {
        this.itemManager = ItemManager.getInstance();
        this.orderManager = orderManager;
        this.costCalculator = new CostCalculator();
    }

    public void processOrder(TreeMap<String, List<String>> order) throws NullException {
        for (Map.Entry<String, List<String>> entry : order.entrySet()) {
            // Get Order Details
            List itemOrdered = entry.getValue().subList(1,entry.getValue().size());
            String orderDetails = entry.getValue().get(0);
            prettyPrint(entry);

            // Traverse all the order
            for(int i=0; i<itemOrdered.size(); i++) {
                // Get ItemID and Quantity
                List itemDetails = Arrays.asList(itemOrdered.get(i).toString().split(":"));

                // Check if Item Exist
                if(itemManager.checkItem(itemDetails.get(0).toString())) {
                    // Get the ID
                    String itemID = itemDetails.get(0).toString();
                    // Find all facilities which has that item
                    List facilitiesWithItem = orderManager.getFacilitiesWithItem(itemID);
                    // Find travel time
                    Map<String , Integer> facilitiesWithShortestPath = orderManager.findFacilitiesWithShortestPath(orderDetails, facilitiesWithItem);
                    // Find Total Processing Time
                    List<Entry<String,Integer>> facilityRecord = this.getProcessingTimeOfFacilities(facilitiesWithShortestPath, itemDetails, orderDetails);
                  //  System.out.println(facilityRecord);
                    System.out.println(itemDetails.get(0) + ":");
                    processEachItem(facilityRecord, itemDetails, orderDetails);
                } else {
                    System.out.println("Item does not exist");
                    return;
                }
            }
        }
    }

    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(Map<String, Integer> facilityWithShortestPath, List itemDetails, String orderDetails) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for (Map.Entry<String, Integer> entry : facilityWithShortestPath.entrySet()) {
            if(orderManager.getQuantityOfItem(entry.getKey(),itemDetails) > 0) {
                String facilityName = entry.toString().split("=")[0];
                int totalProcessingTime = calculateProcessingEndDay(itemDetails, facilityName, orderDetails);
                facilitiesWithTravelTime.put(entry.getKey(), totalProcessingTime);
            }
        }
       return sort(facilitiesWithTravelTime);
    }
    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(List facilityRecords, List itemDetails, String orderDetails) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for(Object entry:facilityRecords) {
            String facilityName = entry.toString().split("=")[0];
            int totalProcessingTime = calculateProcessingEndDay(itemDetails, facilityName, orderDetails);
            facilitiesWithTravelTime.put(facilityName, totalProcessingTime );
        }
        return sort(facilitiesWithTravelTime);
    }

    private int calculateProcessingEndDay(List itemDetails, String facilityName, String orderDetails) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        int travelTime = orderManager.getTravelTimeInDays(facilityName, orderDetails.split("-")[0]);
        int endDay = orderManager.findArrivalDay(Integer.valueOf(orderDetails.split("-")[1]), quantityToProcess, facilityName, itemDetails) + travelTime;
        return endDay;
    }

    private List<Double> calculateProcessingEndDay(List itemDetails, int travelTime, String facilityName, int startDay) throws NullException {
        List<Double> processingDayList = orderManager.setSchedule(startDay, Integer.parseInt(itemDetails.get(1).toString()), facilityName, itemDetails);
        int endDay = (int) (processingDayList.get(1)+ travelTime);
        processingDayList.set(1, (double) endDay);
        return processingDayList;
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

    private void processEachItem(List facilityRecords, List itemDetails, String orderDetails) throws NullException {
        LinkedHashMap<String, List<Integer> > facilitySolution = new LinkedHashMap<>();
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        while(facilityRecords.size() != 0) {
            String recordDetails = facilityRecords.get(0).toString();
            String facilityName = recordDetails.split("=")[0];
            int quantityOfItemInFacility = orderManager.getQuantityOfItem(facilityName, itemDetails);
            if(quantityToProcess <= 0) {
                prettyPrint(facilitySolution);
                return;
            }
            int travelTime = orderManager.getTravelTimeInDays(facilityName, orderDetails.split("-")[0]);
            List completeProcessingDetails =  Arrays.asList(facilityName, orderDetails, itemDetails, travelTime);
            if(quantityOfItemInFacility <= quantityToProcess) {
                quantityToProcess = quantityToProcess - quantityOfItemInFacility;
                facilitySolution.put(facilityName, getProcessingSolution(completeProcessingDetails));
            } else if(quantityOfItemInFacility > quantityToProcess) {
                facilitySolution.put(facilityName, getProcessingSolution(completeProcessingDetails));
                quantityToProcess = 0;

            }
            itemDetails.set(1,Integer.toString(quantityToProcess));
            facilityRecords.remove(0);
            facilityRecords = new ArrayList(getProcessingTimeOfFacilities(facilityRecords, itemDetails, orderDetails));
        }
        prettyPrint(facilitySolution);
    }
       private void prettyPrint(Entry<String, List<String>> entry) {
        System.out.println("Order Id:   " + entry.getKey());
        System.out.println("Order Time: " + entry.getValue().get(0).split("-")[1]);
        System.out.println("Order Destination:   " + entry.getValue().get(0).split("-")[0]);
        System.out.println("List of Order Items:");
        for(int i=1; i< entry.getValue().size(); i++) {
            System.out.print(i + ")" + "  Item ID:  " + entry.getValue().get(i).split(":")[0] + ",");
            System.out.print("  Quantity:  " + entry.getValue().get(i).split(":")[1]);
            System.out.println();
        }
        System.out.println("Processing Solution:");

    }
    private void prettyPrint(Map<String, List<Integer>> facilitySolution) {
        Formatter formatter = new Formatter();
        formatter.format("%-18s %-12s %-12s %-12s \n", "Facility ", "Quantity", "Cost",  "Arrival Day");
        for (Map.Entry<String, List<Integer>> entry : facilitySolution.entrySet()) {
            formatter.format("%-18s %-12s %-12s %-12s\n", entry.getKey(), entry.getValue().get(1), entry.getValue().get(2), entry.getValue().get(0));
        }
        System.out.println(formatter);
    }
    private List<Double> calculateTotalProcessingTime(List completeProcessingDetails) throws NullException {
        return calculateProcessingEndDay((List) completeProcessingDetails.get(2),(int) completeProcessingDetails.get(3), (String)completeProcessingDetails.get(0), Integer.valueOf(completeProcessingDetails.get(1).toString().split("-")[1]));
    }

    private List <Integer> getProcessingSolution(List completeProcessingDetails) throws NullException {
        List itemDetails = (List)(completeProcessingDetails.get(2));
        String itemID = itemDetails.get(0).toString();
        String facilityName = (String)completeProcessingDetails.get(0);
        int quantityOfItemInFacility = orderManager.getQuantityOfItem(facilityName, itemDetails);
        int travelTime = (int)completeProcessingDetails.get(3);
        int costOfFacility = Integer.valueOf(orderManager.getFacilityDetails(facilityName).get(2).toString());
        List <Integer> timeAndQuantity = new ArrayList<>();
        List<Double> totalProcessingTime = calculateTotalProcessingTime(completeProcessingDetails);
        orderManager.reduceFacilityInventory(facilityName, itemID, quantityOfItemInFacility);
        int cost = costCalculator.calculateTotalCost(itemDetails, quantityOfItemInFacility, totalProcessingTime.get(0), travelTime, costOfFacility );
        timeAndQuantity.add(totalProcessingTime.get(1).intValue());
        timeAndQuantity.add(quantityOfItemInFacility);
        timeAndQuantity.add(cost);
        return timeAndQuantity;
    }

}
