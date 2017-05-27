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
                    Map<String , Integer> facilitiesWithShortestPath = this.findFacilitiesWithShortestPath(destination, facilitiesWithItem);
                    // Find Total Processing Time
                    List<Entry<String,Integer>> facilityRecord = this.getProcessingTimeOfFacilities(facilitiesWithShortestPath, itemDetails,destination);
                  //  System.out.println(facilityRecord);
                    System.out.println(itemDetails.get(0) + ":");
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


    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(Map<String, Integer> facilityWithShortestPath, List itemDetails, String startDay) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for (Map.Entry<String, Integer> entry : facilityWithShortestPath.entrySet()) {
            if(getQuantityOfItem(entry.getKey(),itemDetails) > 0) {
                int totalProcessingTime = calculateProcessingEndDay(itemDetails, entry, startDay);
                facilitiesWithTravelTime.put(entry.getKey(), totalProcessingTime);
            }
        }
       return sort(facilitiesWithTravelTime);
    }
    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(List facilityRecords, List itemDetails, String destination) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();

        for(Object entry:facilityRecords) {
            String facilityName = entry.toString().split("=")[0];
            int totalProcessingTime = calculateProcessingEndDay(itemDetails, entry, destination);
            facilitiesWithTravelTime.put(facilityName, totalProcessingTime );
        }
        return sort(facilitiesWithTravelTime);
    }

    private int calculateProcessingEndDay(List itemDetails, Object facility, String destination) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        String facilityName = facility.toString().split("=")[0];

//        int quantityOfItemInFacility = getQuantityOfItem(facility.getKey(), itemDetails);
        int travelTime = getTravelTime(facilityName, destination.split("-")[0]);
        int endDay = orderManager.findArrivalDay(Integer.valueOf(destination.split("-")[1]), quantityToProcess, facilityName, itemDetails) + travelTime;
        return endDay;
    }

    private List<Double> calculateProcessingEndDay(List itemDetails, String travelTime, String facilityName, Integer startDay, int quantityToProcess) throws NullException {
        int travelTimeInFloat =  (int) Math.ceil(Double.parseDouble(travelTime.toString()));
        List<Double> processingDayList = orderManager.setSchedule(startDay, quantityToProcess, facilityName, itemDetails);
        int endDay = (int) (processingDayList.get(1)+ travelTimeInFloat);
        processingDayList.set(1, (double) endDay);
        return processingDayList;
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

    private void processItem(List facilityRecords, List itemDetails, String destination) throws NullException {
        LinkedHashMap<String, List<Integer> > facilitySolution = new LinkedHashMap<>();
        List<Double> totalProcessingTime;
        String travelTime;
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        int cost = 0;
        while(facilityRecords.size() != 0) {
            List <Integer> timeAndQuantity = new ArrayList<>();
            String recordDetails = facilityRecords.get(0).toString();
            String facilityName = recordDetails.split("=")[0];
            int orderArrivalDay = Integer.valueOf(destination.split("-")[1]);
            int quantityOfItemInFacility = getQuantityOfItem(facilityName, itemDetails);
            String itemID = itemDetails.get(0).toString();
            if(quantityToProcess <= 0) {
                prettyPrint(facilitySolution);
                return;
            }
            if(quantityOfItemInFacility <= quantityToProcess) {
                travelTime = String.valueOf(getTravelTime(facilityName, destination.split("-")[0]));
                totalProcessingTime = calculateProcessingEndDay(itemDetails, travelTime, facilityName, orderArrivalDay, quantityToProcess);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityOfItemInFacility);
                quantityToProcess = quantityToProcess - quantityOfItemInFacility;
                timeAndQuantity.add((int) Math.ceil(totalProcessingTime.get(1)));
                timeAndQuantity.add(quantityOfItemInFacility);
                cost = calculateTotalCost(facilityName, itemDetails, quantityOfItemInFacility, totalProcessingTime.get(0), travelTime);
                timeAndQuantity.add(cost);
                facilitySolution.put(facilityName, timeAndQuantity);

            } else if(quantityOfItemInFacility > quantityToProcess) {
                travelTime = String.valueOf(getTravelTime(facilityName, destination.split("-")[0]));
                totalProcessingTime = calculateProcessingEndDay(itemDetails, travelTime, facilityName, orderArrivalDay, quantityToProcess);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityToProcess);
                cost  = calculateTotalCost(facilityName, itemDetails, quantityToProcess, totalProcessingTime.get(0), travelTime);
                timeAndQuantity.add((int) Math.ceil(totalProcessingTime.get(1)));
                timeAndQuantity.add(quantityToProcess);
                timeAndQuantity.add(cost);
                facilitySolution.put(facilityName, timeAndQuantity);
                quantityToProcess = 0;

            }
            itemDetails.set(1,Integer.toString(quantityToProcess));
            facilityRecords.remove(0);
            facilityRecords = new ArrayList(getProcessingTimeOfFacilities(facilityRecords, itemDetails, destination));
        }
        prettyPrint(facilitySolution);
    }
    private int calculateTotalCost(String facilityName, List itemDetails, int quantityOfItem, Double processingDays, String travelTime) throws NullException {
        int itemCost = calculateItemCost(itemDetails, quantityOfItem);
        Double facilityProcessingCost = calculateFacilityCost(facilityName, processingDays);
        int transportCost = calculateTransportCost((int) Math.ceil(Double.parseDouble(travelTime.toString())));
        return (int) (itemCost+facilityProcessingCost+transportCost);

    }
    private int calculateItemCost(List itemDetails, int quantityOfItems) {
        return ItemManager.getInstance().getItemCost(itemDetails.get(0).toString())*quantityOfItems;
    }
    private Double calculateFacilityCost(String facilityName, Double processingDays) throws NullException {
       List<String> facilityDetails = orderManager.getFacilityDetails(facilityName);
       return Integer.valueOf(facilityDetails.get(2))*processingDays;
    }
    private int calculateTransportCost(int travelTime) {
        return travelTime*500;
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

}
