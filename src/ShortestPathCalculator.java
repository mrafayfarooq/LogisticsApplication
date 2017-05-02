
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
    private static final int facilities = 18;
    private final Map<Integer, Map<Integer, List<Integer>>> shortestDistance = new HashMap<>();  // Each facility ID is mapped into a Map which contains the ID of the destination and details of distance.
    public static ShortestPathCalculator getInstance(FacilityImplementation facilityImplementation) {
        // Get Singleton instance of Class
        if (instance == null) {
            instance = new ShortestPathCalculator(facilityImplementation);
            FacilityImplementation facility = facilityImplementation;
        }
        return instance;
    }
    // Calculate Shortest Path
    private ShortestPathCalculator(FacilityImplementation facilityImplmentation) {
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
                List listDetails = facilityImplmentation.getNetworks(FacilityImplementation.getFacilityString(i));
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
    // Calculating Min Distance
    private int minDistance(int dist[], Boolean sptSet[]) {
        // Initializing min value and minimum index
        int min = Integer.MAX_VALUE, min_index = -1;

        // Looping through all facilities and checking if the facility
        for (int v = 1; v <= facilities; v++)
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }
        return min_index;
    }

    // A utility function to print the constructed distance array
    private void printSolution(int[] dist, int src, int[] source) {
        Map<Integer, List<Integer>> shortestPaths = new HashMap<>();
        for (int i = 1; i <= facilities; i++) {
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
        int distance[] = new int[facilities+1]; // Array which will hold all the distances
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[facilities+1];
        int source[] = new int[facilities+1];
        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 1; i <= facilities; i++) {
            source[0] = -1;
            distance[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        distance[src] = 0;

        // Find shortest path for all vertices
        for (int count = 1; count <= facilities; count++) {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(distance, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 1; v <= facilities; v++)

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && graph[u][v]!=0 &&
                        distance[u] != Integer.MAX_VALUE &&
                        distance[u]+graph[u][v] < distance[v]) {
                    source[v]  = u;
                    distance[v] = distance[u] + graph[u][v];
                }
        }

        // print the constructed distance array
        printSolution(distance, src, source);
    }
    Map<Integer, Map<Integer, List<Integer>>> getShortestDistance() {
        return shortestDistance;
    }
}
