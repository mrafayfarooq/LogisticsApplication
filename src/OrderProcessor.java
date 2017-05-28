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
            String orderID = entry.getKey();
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
                    // Get the quantity
                    int quantity = Integer.parseInt(itemDetails.get(1).toString());
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

    private int getTravelTime(String source, String destination) throws NullException {
        List path = orderManager.getShortestPath(source, destination);
        float days = 0;
        if(path!= null) {
            int distance = (int) path.get(path.size()-1);
            days = (float) distance/400;
        }
        return (int)Math.ceil(days);
    }


    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(Map<String, Integer> facilityWithShortestPath, List itemDetails, String orderDetails) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for (Map.Entry<String, Integer> entry : facilityWithShortestPath.entrySet()) {
            if(orderManager.getQuantityOfItem(entry.getKey(),itemDetails) > 0) {
                int totalProcessingTime = calculateProcessingEndDay(itemDetails, entry, orderDetails);
                facilitiesWithTravelTime.put(entry.getKey(), totalProcessingTime);
            }
        }
       return sort(facilitiesWithTravelTime);
    }
    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(List facilityRecords, List itemDetails, String orderDetails) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();

        for(Object entry:facilityRecords) {
            String facilityName = entry.toString().split("=")[0];
            int totalProcessingTime = calculateProcessingEndDay(itemDetails, entry, orderDetails);
            facilitiesWithTravelTime.put(facilityName, totalProcessingTime );
        }
        return sort(facilitiesWithTravelTime);
    }

    private int calculateProcessingEndDay(List itemDetails, Object facility, String destination) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        String facilityName = facility.toString().split("=")[0];
        int travelTime = getTravelTime(facilityName, destination.split("-")[0]);
        int endDay = orderManager.findArrivalDay(Integer.valueOf(destination.split("-")[1]), quantityToProcess, facilityName, itemDetails) + travelTime;
        return endDay;
    }

    private List<Double> calculateProcessingEndDay(List itemDetails, int travelTime, String facilityName, Integer startDay, int quantityToProcess) throws NullException {
        List<Double> processingDayList = orderManager.setSchedule(startDay, quantityToProcess, facilityName, itemDetails);
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
        List<Double> totalProcessingTime;
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        while(facilityRecords.size() != 0) {
            List <Integer> timeAndQuantity = new ArrayList<>();
            String recordDetails = facilityRecords.get(0).toString();
            String facilityName = recordDetails.split("=")[0];
            int quantityOfItemInFacility = orderManager.getQuantityOfItem(facilityName, itemDetails);
            String itemID = itemDetails.get(0).toString();
            if(quantityToProcess <= 0) {
                prettyPrint(facilitySolution);
                return;
            }
            int travelTime = getTravelTime(facilityName, orderDetails.split("-")[0]);
            int costOfFacility = Integer.valueOf(orderManager.getFacilityDetails(facilityName).get(2).toString());
            List completeProcessingDetails =  Arrays.asList(facilityName, quantityToProcess, orderDetails, itemDetails, travelTime);

            if(quantityOfItemInFacility <= quantityToProcess) {
                totalProcessingTime = calculateTotalProcessingTime(completeProcessingDetails);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityOfItemInFacility);
                quantityToProcess = quantityToProcess - quantityOfItemInFacility;
                int cost = costCalculator.calculateTotalCost(itemDetails, quantityOfItemInFacility, totalProcessingTime.get(0), travelTime, costOfFacility );
                timeAndQuantity.add(totalProcessingTime.get(1).intValue());
                timeAndQuantity.add(quantityOfItemInFacility);
                timeAndQuantity.add(cost);
                facilitySolution.put(facilityName, timeAndQuantity);

            } else if(quantityOfItemInFacility > quantityToProcess) {
                totalProcessingTime = calculateTotalProcessingTime(completeProcessingDetails);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityToProcess);
                int cost = costCalculator.calculateTotalCost(itemDetails, quantityOfItemInFacility, totalProcessingTime.get(0), travelTime, costOfFacility);
                timeAndQuantity.add(totalProcessingTime.get(1).intValue());
                timeAndQuantity.add(quantityToProcess);
                timeAndQuantity.add(cost);
                facilitySolution.put(facilityName, timeAndQuantity);
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
        return calculateProcessingEndDay((List) completeProcessingDetails.get(3),(int) completeProcessingDetails.get(4), (String)completeProcessingDetails.get(0), Integer.valueOf(completeProcessingDetails.get(2).toString().split("-")[1]), Integer.valueOf(completeProcessingDetails.get(1).toString()));
    }

}
