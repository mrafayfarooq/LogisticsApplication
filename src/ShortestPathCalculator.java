
import java.util.*;


/**
 * Created by Muhammad Rafay on 4/22/17.
 *
 * Class to calculate Shortest Path between Facilities.
 *
 * This class takes instance of Facility Impl class which has
 * all the details of every facility
 */

public class ShortestPathCalculator {
    private static ShortestPathCalculator instance;
    private FacilityImplementation facilityImpl;
    private final Map<Integer, Map<Integer, List<Integer>>> shortestDistance;  // Each facility ID is mapped into a Map which contains the ID of the destination and details of distance.
    public static ShortestPathCalculator getInstance(FacilityImplementation facilityImplementation) {
        // Get Singleton instance of Class
        if (instance == null) {
            instance = new ShortestPathCalculator(facilityImplementation);
        }
        return instance;
    }
    // Calculate Shortest Path
    private ShortestPathCalculator(FacilityImplementation facilityImplementation) {
        shortestDistance = new HashMap<>();
        facilityImpl = facilityImplementation;
        int facilities = FacilityImplementation.getFacilityQuantities();
        int[][] network = new int[facilities+1][facilities+1];
        // Initializing the network graph
        for(int i = 1; i<=facilities; i++) {
            for(int j=1;j<=facilities;j++) {
                network[i][j]= 0;
            }
        }
        // Load all the networks
        for(int i=1;i<=facilities;i++) {
            try {
                List listDetails = facilityImplementation.getNetworks(FacilityImplementation.getFacilityString(i));
                for (Object list : listDetails) {
                    List<String> networkDetails= Arrays.asList(list.toString().split("-"));
                    String networkId  = networkDetails.get(0);
                    network[i][Integer.valueOf(networkId)] = Integer.valueOf(networkDetails.get(2).trim());
                  }
            } catch (NullException e) {
                e.printStackTrace();
            }
        }
        
        // Find Shortest path from each facility
        for(int i=1;i<=18;i++) {
            calculateShortestPath(network,i);
        }

    }
    // Print Shortest Path
    private void calculatePath(int source[], int j, List<Integer> paths) {
        if (source[j]== -1)
            return;
        calculatePath(source, source[j],paths);
        paths.add(j);
    }
    // Calculating Min Distance from set of facilities not already processed
    private int minDistance(int dist[], Boolean seen[]) {
        // Initializing min value and minimum index
        int min = Integer.MAX_VALUE, min_index = -1;

        // Looping through all facilities and checking if the facility
        for (int v = 1; v <= FacilityImplementation.getFacilityQuantities(); v++)
            if (!seen[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }
        return min_index;
    }

    /**+
     * This function calculate the final shortest distance and store in hash map.
     * It calculate the shortest path from each facility to every adjacent facility
     * and store it on hashMap and finally add that hash map into final array
     * with src as key.
     * @param dist distance array
     * @param src source facility starting from 1
     * @param source the initial source facility
     */
    private void calculateSolution(int[] dist, int src, int[] source) {
        Map<Integer, List<Integer>> shortestPaths = new HashMap<>();
        for (int i = 1; i <= FacilityImplementation.getFacilityQuantities(); i++) {
            List<Integer> paths = new ArrayList();
            paths.add(dist[i]);
            calculatePath(source,i, paths);
            shortestPaths.put(i, paths);
        }
        shortestDistance.put(src, shortestPaths);
    }

    /**+
     * Function to calculate shortest path.
     * It takes source, initialize the graph and calculate shortest path
     * from each node to every other node.
     * @param graph which is representation of facilities
     * @param src the source facility to start the algorithm
     */
    private void calculateShortestPath(int graph[][], int src) {
        int facilities = FacilityImplementation.getFacilityQuantities();
        int distance[] = new int[facilities+1]; // Array which will hold all the distances
        Boolean seen[] = new Boolean[facilities+1];
        int source[] = new int[facilities+1];
        /*+
          Initializing the distance array, source and seen to following:-
          source[] = -1
          distance[] = MAX Value
          seen[] = false
         */
        for (int i = 1; i <= facilities; i++) {
            source[0] = -1;
            distance[i] = Integer.MAX_VALUE;
            seen[i] = false;
        }

        // Initializing distance of source to source which is always 0
        distance[src] = 0;

        // Start the shortest path algorithm
        for (int count = 1; count <= facilities; count++) {
            int u = minDistance(distance, seen);
            // Mark the picked vertex as processed
            seen[u] = true;
            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 1; v <= facilities; v++)
                // Update dist[v] only if is not in seen, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!seen[v] && graph[u][v]!=0 &&
                        distance[u] != Integer.MAX_VALUE &&
                        distance[u]+graph[u][v] < distance[v]) {
                    source[v]  = u;
                    distance[v] = distance[u] + graph[u][v];
                }
        }
        // print the constructed distance array
        calculateSolution(distance, src, source);
    }
    // Return Shortest Distance from all the facilities.
    Map<Integer, Map<Integer, List<Integer>>> getShortestDistance() {
        return shortestDistance;
    }
    /**
     * +
     * Get Shortest Path between Source and Destination
     *
     * @param source      facility name
     * @param destination facility name
     * @return List of facility with shortest cost
     * @throws NullException if source or destination does not found.
     */
    public List<String> getShortestPath(String source, String destination) throws NullException {
        if (!source.equals(destination)) {
            // Get all Shortest Paths
            Map<Integer, Map<Integer, List<Integer>>> shortestDistance = new HashMap<>(getShortestDistance());
            // Get shortest path of source from all
            Map<Integer, List<Integer>> pathDetails = new HashMap<>(shortestDistance.get(facilityImpl.getFacilityId(source)));
            // Get distance
            Integer distance = pathDetails.get(facilityImpl.getFacilityId(destination)).get(0);
            // Adding path into Path List
            List path = new ArrayList();
            for (int values : pathDetails.get(facilityImpl.getFacilityId(destination))) {
                if (values <= FacilityImplementation.getFacilityQuantities()) {
                    path.add(FacilityImplementation.getFacilityString(values));
                }
            }
            path.add(distance);
            pathDetails.clear();
            return path;
        } else {
            return null;
        }
    }
    public Map<String, Integer> findFacilitiesWithShortestPath(String destination, List facilitiesWithItem) throws NullException {
        Map<String,Integer> facilityWithShortestPath = new HashMap();
        for (Object list : facilitiesWithItem) {
            List path = getShortestPath(list.toString(), destination.split("-")[0]);
            if(path!= null) {
                int distance = (int) path.get(path.size()-1);
                float days = (float)distance/400;

                facilityWithShortestPath.put(list.toString(), (int) Math.ceil(days));
            }
        }
        return facilityWithShortestPath;
    }
    public int getTravelTimeInDays(String source, String destination) throws NullException {
        List path = getShortestPath(source, destination);
        float days = 0;
        if(path!= null) {
            int distance = (int) path.get(path.size()-1);
            days = (float) distance/400;
        }
        return (int)Math.ceil(days);
    }

}
