import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/22/17.
 */
public class ShortestPathCalculator extends NetworkManager {
   private int [][] netWorkGraph = new int [18][18];
    private List listDetails = new ArrayList<>();
    static final int V=18;

    ShortestPathCalculator(NetworkManager networkManager) {
        for(int i=0;i<=17;i++) {
            for(int j=0;j<=17;j++) {
                this.netWorkGraph[i][j]= 0;
            }
        }
        for(int i=0;i<=17;i++) {
            try {
                listDetails = networkManager.getDetails(i+1);
                for (Object list : listDetails) {
                    String networkDetails = list.toString().replaceAll("^ *","");
                    List<String> networkDetailsList = Arrays.asList(networkDetails.split("-"));
                    String networkId  = networkDetailsList.get(0);
                    netWorkGraph[i][Integer.valueOf(networkId)-1] = Integer.valueOf(networkDetailsList.get(2).replaceAll(" ", ""));
                  }
            } catch (NullException e) {
                e.printStackTrace();
            }
        }

        dijkstra(netWorkGraph,0);
    }
    // Function to print shortest path from source to j
// using parent array
    void printPath(int parent[], int j)
    {
        // Base Case : If j is source
        if (parent[j]== -1)
            return;
        printPath(parent, parent[j]);
        System.out.printf(" %d ",j);
    }

    // A utility function to print the constructed distance // array
    int minDistance(int dist[], Boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index = -1;

        for (int v = 0; v < V; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    // A utility function to print the constructed distance array
    void printSolution(int[] dist, int n, int[] parent)
    {
        int src = 0;
        System.out.printf("Vertex\t  Distance\tPath");
        for (int i = 1; i < V; i++)
        {
            System.out.printf("\n%d -> %d \t\t %d\t\t%d ", src, i, dist[i], src);
            printPath(parent, i);
        }
    }

    // Funtion that implements Dijkstra's single source shortest path
    // algorithm for a graph represented using adjacency matrix
    // representation
    void dijkstra(int graph[][], int src)
    {
        int dist[] = new int[V]; // The output array. dist[i] will hold
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[V];
        int parent[] = new int[V];
        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < V; i++)
        {
            parent[0] = -1;
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < V-1; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++)

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
        printSolution(dist, V, parent);
    }
}
