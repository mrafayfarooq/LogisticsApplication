import java.util.List;

/**
 * Created by Muhammad Rafay on 5/28/17.
 */
public class CostCalculator {
    public int calculateTotalCost( List itemDetails, int quantityOfItem, Double processingDays, int travelTime, int costOfFacility ) throws NullException {
        int itemCost = calculateItemCost(itemDetails, quantityOfItem);
        Double facilityProcessingCost = calculateFacilityCost(costOfFacility, processingDays);
        int transportCost = calculateTransportCost(travelTime);
        return (int) (itemCost+facilityProcessingCost+transportCost);

    }
    private int calculateItemCost(List itemDetails, int quantityOfItems) {
        return ItemManager.getInstance().getItemCost(itemDetails.get(0).toString())*quantityOfItems;
    }
    private Double calculateFacilityCost(int costOfFacility, Double processingDays) throws NullException {
        return costOfFacility*processingDays;
    }
    private int calculateTransportCost(int travelTime) {
        return travelTime*500;
    }

}
