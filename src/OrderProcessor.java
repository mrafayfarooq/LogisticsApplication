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

    public void processOrder(HashMap<String, List<String>> order) throws NullException {
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
                    List<Entry<String,Integer>> facilityRecord = this.getProcessingTimeOfFacilities(facilitiesWithShortestPath, itemDetails,destination.toString().split("-")[1]);
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


    private List<Entry<String,Integer>> getProcessingTimeOfFacilities(Map<String, Integer> facilityWithShortestPath, List itemDetails, String startDay) throws NullException {
        Map<String, Integer> facilitiesWithTravelTime = new HashMap<>();
        for (Map.Entry<String, Integer> entry : facilityWithShortestPath.entrySet()) {
            int totalProcessingTime = calculateProcessingEndDay(itemDetails, getProcessingPower(entry.getKey()), entry, startDay);
            facilitiesWithTravelTime.put(entry.getKey(), totalProcessingTime );
        }
       return sort(facilitiesWithTravelTime);
    }

    private int calculateProcessingEndDay(List itemDetails, Integer processingPowerOfFacility, Map.Entry<String, Integer> facility, String startDay) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        int quantityOfItemInFacility = getQuantityOfItem(facility.getKey(), itemDetails);
        int travelTime =  (int) Math.ceil(Double.parseDouble(facility.getValue().toString()));
        int endDay;
        if(quantityOfItemInFacility <= quantityToProcess) {
           endDay = (quantityOfItemInFacility/processingPowerOfFacility) + travelTime + Integer.parseInt(startDay.toString());
        } else {
            endDay = (quantityToProcess/processingPowerOfFacility) + travelTime + Integer.parseInt(startDay.toString());
        }
        return endDay;
    }
    private int calculateProcessingEndDay(List itemDetails, Integer processingPowerOfFacility, String travelTime, Integer startDay) throws NullException {
        double endDay;
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        Double travelTimeInFloat =  Double.parseDouble(travelTime.toString());
        endDay = (quantityToProcess/processingPowerOfFacility) + travelTimeInFloat + Integer.parseInt(startDay.toString());
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
        for(Object entry:facilityRecords) {
            String facilityName = entry.toString().split("=")[0];
            String travelTime = entry.toString().split("=")[1];

            int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
            int quantityOfItemInFacility = getQuantityOfItem(facilityName, itemDetails);
            String itemID = itemDetails.get(0).toString();
            if(quantityToProcess == 0) {
                System.out.println(facilitySolution);
                return;
            }
            if(quantityOfItemInFacility <= quantityToProcess) {
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityOfItemInFacility);
                itemDetails.set(1, Integer.toString(quantityToProcess-quantityOfItemInFacility));
                facilitySolution.put(facilityName,(int) Math.ceil(Double.valueOf(travelTime)));
            } else if(quantityOfItemInFacility > quantityToProcess) {
                int processingPower = getProcessingPower(facilityName);
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityToProcess);
                travelTime = String.valueOf(getTravelTime(facilityName, startDay.split("-")[0]));
                int totalProcessingTime = calculateProcessingEndDay(itemDetails, processingPower, travelTime, Integer.valueOf(startDay.split("-")[1]));
                itemDetails.set(1, Integer.toString(quantityToProcess-quantityToProcess));
                facilitySolution.put(facilityName, (int) Math.ceil(Double.valueOf(totalProcessingTime)));
            }
        }
    }

    private int getProcessingPower(String facilityName) throws NullException {
        List facilityDetails = new ArrayList(orderManager.getFacilityDetails(facilityName));
        return Integer.valueOf((facilityDetails.get(1).toString()));
    }
}
