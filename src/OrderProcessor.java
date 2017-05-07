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
                    Map<String , Double> facilitiesWithShortestPath = this.findFacilitiesWithShortestPath(destination, facilitiesWithItem);
                    // Find Total Processing Time
                    List<Entry<String,Double>> facilityRecord = this.getProcessingTimeOfFacilities(facilitiesWithShortestPath, itemDetails,destination.toString().split("-")[1]);
                    System.out.println(facilityRecord);

                    processItems(facilityRecord, itemDetails, destination);
                } else {
                    System.out.println("Item does not exist");
                    return;
                }
            }
        }
    }

    private Map<String, Double> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        Map<String,Double> facilityWithShortestPath = new HashMap();
        for (Object list : facilitiesWithItem) {
            List path = orderManager.getShortestPath(list.toString(), destination.split("-")[0]);
            if(path!= null) {
                int distance = (int) path.get(path.size()-1);
                float days = (float)distance/400;
                facilityWithShortestPath.put(list.toString(),(double)days);
            }
        }
        return facilityWithShortestPath;
    }
    private float getTravelTime(String source, String destination) throws NullException {
        List path = orderManager.getShortestPath(source, destination);
        float days = 0;
        if(path!= null) {
            int distance = (int) path.get(path.size()-1);
            days = (float) distance/400;
        }
        return days;
    }


        private List<Entry<String,Double>> getProcessingTimeOfFacilities(Map<String, Double> facilityWithShortestPath, List itemDetails, String startDay) throws NullException {
        for (Map.Entry<String, Double> entry : facilityWithShortestPath.entrySet()) {
            List facilityDetails = new ArrayList(orderManager.getFacilityDetails(entry.getKey()));
            Double totalProcessingTime = calculateProcessingEndDay(itemDetails, Integer.valueOf((facilityDetails.get(1).toString())), entry, startDay);
            facilityWithShortestPath.put(entry.getKey(), totalProcessingTime );
        }
       return sort(facilityWithShortestPath);
    }

    private Double calculateProcessingEndDay(List itemDetails, Integer processingPowerOfFacility, Map.Entry<String, Double> facility, String startDay) throws NullException {
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        int quantityOfItemInFacility = getQuantityOfItem(facility.getKey(), itemDetails);
        Double travelTime =  Double.parseDouble(facility.getValue().toString());
        double endDay;
        if(quantityOfItemInFacility <= quantityToProcess) {
           endDay = (quantityOfItemInFacility/processingPowerOfFacility) + travelTime + Integer.parseInt(startDay.toString());
        } else {
            endDay = (quantityToProcess/processingPowerOfFacility) + travelTime + Integer.parseInt(startDay.toString());
        }
        return endDay;
    }
    private Double calculateProcessingEndDay(List itemDetails, Integer processingPowerOfFacility, String travelTime, Integer startDay) throws NullException {
        double endDay;
        int quantityToProcess = Integer.parseInt(itemDetails.get(1).toString());
        Double travelTimeInFloat =  Double.parseDouble(travelTime.toString());
        endDay = (quantityToProcess/processingPowerOfFacility) + travelTimeInFloat + Integer.parseInt(startDay.toString());
        return endDay;
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
    private List<Entry<String,Double>> sort(Map map) {
        Set<Entry<String, Double>> set = map.entrySet();
        ArrayList<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Double>>() {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );
        return list;
    }

    private void processItems(List facilityRecords, List itemDetails, String startDay) throws NullException {
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
                List facilityDetails = new ArrayList(orderManager.getFacilityDetails(facilityName));
                int processingPower = Integer.valueOf((facilityDetails.get(1).toString()));
                orderManager.reduceFacilityInventory(facilityName, itemID, quantityToProcess);
                travelTime = String.valueOf(getTravelTime(facilityName, startDay.split("-")[0]));
                Double totalProcessingTime = calculateProcessingEndDay(itemDetails, processingPower, travelTime, Integer.valueOf(startDay.split("-")[1]));
                itemDetails.set(1, Integer.toString(quantityToProcess-quantityToProcess));
                facilitySolution.put(facilityName, (int) Math.ceil(Double.valueOf(totalProcessingTime)));
            }
        }
    }
}
