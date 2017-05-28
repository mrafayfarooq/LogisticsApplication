import java.util.*;


/**
 * Created by Muhammad Rafay on 5/8/17.
 *
 * This class handle the schedule of facilities. It is responsible
 * to find arrival day of an order, set initial schedule and setting
 * the schedule of the facility.
 */
public class Scheduler {
    private final HashMap<Integer, Map<Integer, Integer>> scheduler = new HashMap<>(); // <FacilityName, List of first 20 schedule>

    /**+
     * This class set the initial schedule of each facility for first 20 days.
     * @param facilityUtility List of facility with ID
     * @throws NullException throws exception if Facility is not found.
     */
    public void setInitialSchedule(HashMap<Integer, List<String>> facilityUtility) throws NullException {
        for(int i=1;i<=facilityUtility.size();i++) {
            if(facilityUtility.get(i).isEmpty()) throw new NullException("Facility Name");
            else {
                Map<Integer, Integer> copies = new HashMap<>();
                // Set the schedule for first 20 days.
                for(int j=0; j<=20; j++) {
                   copies.put(j, Integer.valueOf(facilityUtility.get(i).get(1)));
               }
                scheduler.put(i, copies);
            }
        }
    }
    /**+
     * Get Schedule of Facility
     * @param facilityID Id of the facility
     * @return list of facility schedule
     * @throws NullException if the facility does not exist
     */
    public Map<Integer, Integer> getScheduleOfFacility(int facilityID) throws NullException {
        return this.scheduler.get(facilityID);
    }
    /**+
     * Find Arrival Time of the Item based on start day
     * and quantity to process in particular facility.
     * @param startDay day the order starts
     * @param quantityToProcess number of items to process
     * @param quantityOfItemsInFacility number of items in facility
     * @param facilityDetails details of facility
     * @return arrival day of the item.
     * @throws NullException if facility name not found.
     */

    public int findArrivalDay(int startDay, int quantityToProcess, int quantityOfItemsInFacility, List<String> facilityDetails) throws NullException {
        int facilityID = Integer.valueOf(facilityDetails.get(0));
        int processingPower = Integer.valueOf(facilityDetails.get(1));
        int endDay = startDay;
        Map<Integer, Integer> scheduler = this.scheduler.get(facilityID);

        while(quantityToProcess > 0 && quantityOfItemsInFacility > 0) {
            if (scheduler.containsKey(endDay)) {
                quantityToProcess = quantityToProcess - scheduler.get(endDay);
                quantityOfItemsInFacility = quantityOfItemsInFacility - scheduler.get(endDay);
                endDay++;
            } else {
                scheduler.put(endDay, processingPower);
            }
        }
        this.scheduler.put(facilityID,scheduler);
        return endDay-1;
    }

    /**+
     * Set Schedule of the Facility using the following information
     * @param startDay the day order was given
     * @param quantityToProcess quantity to be processed
     * @param quantityOfItemsInFacility number of item in facility
     * @param facilityDetails details of facility
     * @return List includes Processing Days: Time it take for the facility to process the item, End Day: The end day of the facility.
     * @throws NullException
     */
    public List setSchedule(int startDay, int quantityToProcess, int quantityOfItemsInFacility, List<String> facilityDetails) throws NullException {
        List<Double> processingDayList = new ArrayList();
        Double processingDay = 0.0;
        int facilityId = Integer.valueOf(facilityDetails.get(0));
        Map<Integer, Integer> scheduler = this.scheduler.get(facilityId);
        int processingPower = Integer.valueOf(facilityDetails.get(1));
        int endDay = startDay;
        while (quantityToProcess > 0 && quantityOfItemsInFacility > 0) {
           if(scheduler.containsKey(endDay)) {
               if (scheduler.get(endDay-1) != 0) {
                   if (scheduler.get(endDay) > quantityToProcess) {
                       quantityOfItemsInFacility = quantityOfItemsInFacility - quantityToProcess;
                       scheduler.put(endDay-1, scheduler.get(endDay) - quantityToProcess);
                       processingDay = processingDay +  quantityToProcess/scheduler.get(endDay);
                       quantityToProcess = 0;
                       endDay++;
                   } else {
                       if (processingPower > quantityOfItemsInFacility) {
                           scheduler.put(endDay-1, processingPower - quantityOfItemsInFacility);
                           processingDay = processingDay +  ((float)quantityOfItemsInFacility / processingPower);
                           quantityOfItemsInFacility = 0;
                           endDay++;
                       } else {
                           quantityToProcess = quantityToProcess - scheduler.get(endDay);
                           quantityOfItemsInFacility = quantityOfItemsInFacility - processingPower;
                           scheduler.put(endDay-1, 0);
                           processingDay++;
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
        processingDayList.add(processingDay);
        processingDayList.add(Double.valueOf(endDay-1));
        return processingDayList;
    }
}
