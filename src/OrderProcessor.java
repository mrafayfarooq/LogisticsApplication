import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.*;

/**
 * Created by Muhammad Rafay on 5/6/17.
 */
class OrderProcessor {
    private final OrderManager orderManager;
    private final ItemManager itemManager;
    private final CostCalculator costCalculator;
    private static int totalCost;
    private static final DecimalFormat costFormatter = new DecimalFormat("$#,###");

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
            totalCost = 0;
            PrettyPrint.prettyPrint(entry);

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
                    if(i+1 == itemOrdered.size()) System.out.println("Total Cost:     " + costFormatter.format(totalCost));

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
        return orderManager.findArrivalDay(Integer.valueOf(orderDetails.split("-")[1]), quantityToProcess, facilityName, itemDetails) + travelTime;
    }

    private List<Double> calculateProcessingEndDay(List itemDetails, int travelTime, String facilityName, int startDay) throws NullException {
        List<Double> processingDayList = orderManager.setSchedule(startDay, Integer.parseInt(itemDetails.get(1).toString()), facilityName, itemDetails);
        int endDay = (int) (processingDayList.get(1)+ travelTime);
        processingDayList.set(1, (double) endDay);
        return processingDayList;
    }

    private List<Entry<String,Integer>> sort(Map map) {
        Set<Entry<String, Integer>> set = map.entrySet();
        ArrayList<Entry<String, Integer>> list = new ArrayList<>(set);
        list.sort(Comparator.comparing(o -> (o.getValue())));
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
                PrettyPrint.prettyPrint(facilitySolution);
                return;
            }
            int travelTime = orderManager.getTravelTimeInDays(facilityName, orderDetails.split("-")[0]);
            List completeProcessingDetails =  Arrays.asList(facilityName, orderDetails, itemDetails, travelTime);
            if(quantityOfItemInFacility <= quantityToProcess) {
                quantityToProcess = quantityToProcess - quantityOfItemInFacility;
                facilitySolution.put(facilityName, getProcessingSolution(completeProcessingDetails, quantityOfItemInFacility));
            } else if(quantityOfItemInFacility > quantityToProcess) {
                facilitySolution.put(facilityName, getProcessingSolution(completeProcessingDetails, quantityToProcess));
                quantityToProcess = 0;

            }
            itemDetails.set(1,Integer.toString(quantityToProcess));
            facilityRecords.remove(0);
            facilityRecords = new ArrayList(getProcessingTimeOfFacilities(facilityRecords, itemDetails, orderDetails));
            totalCost = totalCost + facilitySolution.get(facilityName).get(2);
        }
        PrettyPrint.prettyPrint(facilitySolution);
    }

    private List<Double> calculateTotalProcessingTime(List completeProcessingDetails) throws NullException {
        return calculateProcessingEndDay((List) completeProcessingDetails.get(2),(int) completeProcessingDetails.get(3), (String)completeProcessingDetails.get(0), Integer.valueOf(completeProcessingDetails.get(1).toString().split("-")[1]));
    }

    private List <Integer> getProcessingSolution(List completeProcessingDetails, int quantity) throws NullException {
        List itemDetails = (List)(completeProcessingDetails.get(2));
        String itemID = itemDetails.get(0).toString();
        String facilityName = (String)completeProcessingDetails.get(0);
        int travelTime = (int)completeProcessingDetails.get(3);
        int costOfFacility = Integer.valueOf(orderManager.getFacilityDetails(facilityName).get(2).toString());
        List <Integer> timeAndQuantity = new ArrayList<>();
        List<Double> totalProcessingTime = calculateTotalProcessingTime(completeProcessingDetails);
        orderManager.reduceFacilityInventory(facilityName, itemID, quantity);
        int cost = costCalculator.calculateTotalCost(itemDetails, quantity, totalProcessingTime.get(0), travelTime, costOfFacility );
        timeAndQuantity.add(totalProcessingTime.get(1).intValue());
        timeAndQuantity.add(quantity);
        timeAndQuantity.add(cost);
        return timeAndQuantity;
    }

}
