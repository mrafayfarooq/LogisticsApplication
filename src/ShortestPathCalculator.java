import java.util.*;

/**
 * Created by Muhammad Rafay on 4/22/17.
 */
public class ShortestPathCalculator extends NetworkManager {
    private int [][] netWorkGraph = new int [19][19];
    private List listDetails = new ArrayList<>();
    private List<Integer> paths = new ArrayList<>();
    static final int V=18;
    protected Map<Integer, Map<Integer, List<Integer>>> shortestDistance = new HashMap<>();
    private Map<Integer,List<Integer>> shortestPaths = new HashMap<>();

    ShortestPathCalculator(NetworkManager networkManager) {
        for(int i=1;i<=18;i++) {
            for(int j=1;j<=18;j++) {
                this.netWorkGraph[i][j]= 0;
            }
        }
        for(int i=1;i<=18;i++) {
            try {
                listDetails = networkManager.getDetails(i);
                for (Object list : listDetails) {
                    String networkDetails = list.toString().replaceAll("^ *","");
                    List<String> networkDetailsList = Arrays.asList(networkDetails.split("-"));
                    String networkId  = networkDetailsList.get(0);
                    netWorkGraph[i][Integer.valueOf(networkId)] = Integer.valueOf(networkDetailsList.get(2).replaceAll(" ", ""));
                  }
            } catch (NullException e) {
                e.printStackTrace();
            }
        }
        for(int i=1;i<=18;i++) {
            dijkstra(netWorkGraph,i);
        }

    }
    // Function to print shortest path from source to j
// using parent array
    void printPath(int parent[], int j, List<Integer> paths)
    {
        // Base Case : If j is source
        if (parent[j]== -1)
            return ;
        printPath(parent, parent[j],paths);
        paths.add(j);
      //  System.out.printf(" %d ",j);
    }

    // A utility function to print the constructed distance // array
    int minDistance(int dist[], Boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 1; v <= V; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    // A utility function to print the constructed distance array
    void printSolution(int[] dist, int src, int[] parent)
    {
        //System.out.printf("\nVertex\t  Distance\tPath");
        shortestPaths = new HashMap<>();

        for (int i = 1; i <= V; i++)
        {
            paths = new ArrayList();

       //     System.out.printf("\n%d -> %d \t\t %d\t ", src, i, dist[i]);
            paths.add(dist[i]);
            printPath(parent,i,paths);
            shortestPaths.put(i,paths);
        }
        shortestDistance.put(src,shortestPaths);
    }

    // Funtion that implements Dijkstra's single source shortest path
    // algorithm for a graph represented using adjacency matrix
    // representation
    void dijkstra(int graph[][], int src)
    {
        int dist[] = new int[V+1]; // The output array. dist[i] will hold
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[V+1];
        int parent[] = new int[V+1];
        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 1; i <= V; i++)
        {
            parent[0] = -1;
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 1; count <= V; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 1; v <= V; v++)

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && graph[u][v]!=0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u]+graph[u][v] < dist[v]) {
                    parent[v]  = u;
                    dist[v] = dist[u] + graph[u][v];
                }
        }

        // print the constructed distance array
        printSolution(dist, src, parent);
    }
    Map<Integer, Map<Integer, List<Integer>>> getShortestDistance() {
        return shortestDistance;
    }
}
