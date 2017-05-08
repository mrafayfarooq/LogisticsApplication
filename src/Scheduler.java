import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Muhammad Rafay on 5/8/17.
 */
public class Scheduler {
    FacilityImplementation facilityImplementation;
    private final HashMap<Integer, List<Integer>> scheduler = new HashMap<>(); // <FacilityName, List of first 20 schedule>

    Scheduler(FacilityImplementation facilityImpl) {
        this.facilityImplementation = facilityImpl;
    }
    /**+
     * This class will be modified in next phase. Right now we are making 20 copies,
     * but eventually will be having schedule of more than that.
     * @throws NullException if Schedule is not present
     */
    public void setScheduler(HashMap<Integer, List<String>> facilityUtility) throws NullException {
        for(int i=1;i<=facilityUtility.size();i++) {
            if(facilityUtility.get(i).isEmpty()) throw new NullException("Facility Name");
            else {
                List<Integer> copies = Collections.nCopies(20, facilityImplementation.getProcessingPower(facilityUtility.get(i).get(0).trim()));
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
    public List getScheduleOfFacility(String facilityName) throws NullException {
        return scheduler.get(facilityImplementation.getFacilityId(facilityName));
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

//    public int findArrivalDay(int startDay, int qunatityToProcess, String facilityName) throws NullException {
//        List scheduler = this.scheduler.get(facilityImplementation.getFacilityId(facilityName));
//        int processingPower = facilityImplementation.getProcessingPower(facilityName);
//        int endDay = startDay;
//        while(qunatityToProcess > 0) {
//            if(!scheduler.get(endDay).equals(0)) {
//                endDay++;
//                qunatityToProcess = qunatityToProcess - processingPower;
//            } else {
//                endDay++;
//            }
//        }
//        return endDay-1;
//    }
//    public void setSchedule(int startDay, int qunatityToProcess, String facilityName) throws NullException {
//        int facilityId = facilityImplementation.getFacilityId(facilityName);
//        List scheduler = this.scheduler.get(facilityId);
//        int processingPower = facilityImplementation.getProcessingPower(facilityName);
//        int endDay = startDay;
//        while(qunatityToProcess > 0) {
//            if(!scheduler.get(endDay).equals(0)) {
//                endDay++;
//                if(qunatityToProcess >= processingPower) {
//                    qunatityToProcess = qunatityToProcess - processingPower;
//                    this.scheduler
//
//                }
//                this.scheduler.put(facilityId, )
//            } else {
//                endDay++;
//            }
//        }
//    }
}
