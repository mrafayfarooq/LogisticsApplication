import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Muhammad Rafay on 5/28/17.
 * This class is used to determine the cost of Order.
 */
class CostCalculator {
    private static int totalCost;
    private static final DecimalFormat costFormatter = new DecimalFormat("$#,###");


    /**+
     * Calculate the total cost of an order using the following information.
     * @param itemDetails details of item - For Item Cost
     * @param quantityOfItem quantity - For Item Cost
     * @param processingDays days it take to process the item - For facility cost
     * @param travelTime    travel time - For Transport Cost
     * @param costOfFacility    Cost of facility.
     * @return total cost in whole number.
     */
    public int calculateTotalCost( List itemDetails, int quantityOfItem, Double processingDays, int travelTime, int costOfFacility ) {
        int itemCost = calculateItemCost(itemDetails, quantityOfItem);
        Double facilityProcessingCost = calculateFacilityCost(costOfFacility, processingDays);
        int transportCost = calculateTransportCost(travelTime);
        return (int) (itemCost+facilityProcessingCost+transportCost);

    }
    private int calculateItemCost(List itemDetails, int quantityOfItems) {
        return ItemManager.getInstance().getItemCost(itemDetails.get(0).toString())*quantityOfItems;
    }
    private Double calculateFacilityCost(int costOfFacility, Double processingDays) {
        return costOfFacility*processingDays;
    }
    private int calculateTransportCost(int travelTime) {
        return travelTime*500;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void calculateTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }
    public void printTotalCost() {
        System.out.println("Total Cost:     " + costFormatter.format(totalCost));
        totalCost = 0;
    }
 }
