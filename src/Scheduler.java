import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Muhammad Rafay on 5/8/17.
 */
public class Scheduler {
    FacilityImplementation facilityImplementation;
    private final HashMap<Integer, Map<Integer, Integer>> scheduler = new HashMap<>(); // <FacilityName, List of first 20 schedule>

    Scheduler(FacilityImplementation facilityImpl) {
        this.facilityImplementation = facilityImpl;
    }
    /**+
     * This class will be modified in next phase. Right now we are making 20 copies,
     * but eventually will be having schedule of more than that.
     * @throws NullException if Schedule is not present
     */
    public void setScheduler(HashMap<Integer, List<String>> facilityUtility) throws NullException {
        int processingPower = 0;
        for(int i=1;i<=facilityUtility.size();i++) {
            processingPower = facilityImplementation.getProcessingPower(facilityUtility.get(i).get(0));
            if(facilityUtility.get(i).isEmpty()) throw new NullException("Facility Name");
            else {
                Map<Integer, Integer> copies = new HashMap<>();
                for(int j=0; j<20; j++) {
                   copies.put(j, processingPower);
               }
                scheduler.put(i, copies);
            }
        }
    }
    /**+
     * Get Schedule of Facility
     * @param facilityName name of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public Map<Integer, Integer> getScheduleOfFacility(String facilityName) throws NullException {
        return this.scheduler.get(facilityImplementation.getFacilityId(facilityName));
    }
    /**+
     * Find Arrival Time of the Item based on start day
     * and quantity to process in particular facility.
     * @param startDay day the order starts
     * @param qunatityToProcess number of items to process
     * @param facilityName name of the facility
     * @return arrival day of the item.
     * @throws NullException if facility name not found.
     */

    public int findArrivalDay(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        Map<Integer, Integer> scheduler = this.scheduler.get(facilityImplementation.getFacilityId(facilityName));
        int processingPower = facilityImplementation.getProcessingPower(facilityName);
        int quantityOfItemsInFacility = facilityImplementation.getQuantityOfItem(facilityName, itemDetails);
        int endDay = startDay;
        while(qunatityToProcess > 0 && quantityOfItemsInFacility > 0) {
            if (scheduler.containsKey(endDay)) {
                qunatityToProcess = qunatityToProcess - scheduler.get(endDay);
                quantityOfItemsInFacility = quantityOfItemsInFacility - scheduler.get(endDay);
                endDay++;
            } else {
                scheduler.put(endDay, processingPower);
            }
        }
        this.scheduler.put(facilityImplementation.getFacilityId(facilityName),scheduler);
        return endDay-1;
    }
    public int setSchedule(int startDay, int qunatityToProcess, String facilityName, List itemDetails) throws NullException {
        int facilityId = facilityImplementation.getFacilityId(facilityName);
        Map<Integer, Integer> scheduler = this.scheduler.get(facilityId);
        int processingPower = facilityImplementation.getProcessingPower(facilityName);
        int quantityOfItemsInFacility = facilityImplementation.getQuantityOfItem(facilityName, itemDetails);
        int endDay = startDay;
        while (qunatityToProcess > 0 && quantityOfItemsInFacility > 0) {
           if(scheduler.containsKey(endDay)) {
               if (scheduler.get(endDay) != 0) {
                   if (scheduler.get(endDay) > qunatityToProcess) {
                       quantityOfItemsInFacility = quantityOfItemsInFacility - qunatityToProcess;
                       scheduler.put(endDay, scheduler.get(endDay) - qunatityToProcess);
                       qunatityToProcess = 0;
                       endDay++;

                   } else {
                       if (processingPower > quantityOfItemsInFacility) {
                           scheduler.put(endDay, processingPower - quantityOfItemsInFacility);
                           qunatityToProcess = qunatityToProcess - quantityOfItemsInFacility;
                           quantityOfItemsInFacility = 0;
                           endDay++;

                       } else {
                           qunatityToProcess = qunatityToProcess - scheduler.get(endDay);
                           quantityOfItemsInFacility = quantityOfItemsInFacility - processingPower;
                           scheduler.put(endDay, 0);
                           endDay++;
                       }
                   }

               } else {
                   endDay++;
               }
           } else {
               scheduler.put(endDay, processingPower);
           }
        }
        this.scheduler.put(facilityId, scheduler);
        return endDay-1;
    }
}
