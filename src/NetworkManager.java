import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/9/17.
 */
public class NetworkManager {
    private HashMap<Integer, List<String>> network = new HashMap<>();
    private List networkDetails = new ArrayList<>();
    private String idTag;
    private String locationTag;
    private String distanceTag;
    static String networkTag;
    NetworkManager(String locationTag, String distanceTag) {
        this.locationTag = locationTag;
        this.distanceTag = distanceTag;
    }

    public void loadNetworks(NodeList networks) {
        for (int i = 0; i < networks.getLength(); i++) {
            if (networks.item(i).getNodeType() == Node.TEXT_NODE) {
                continue;
            }

             // Get Facility Id
            NamedNodeMap aMap = networks.item(i).getAttributes();
            String facilityId = aMap.getNamedItem("Id").getNodeValue();

            // Get Networks
            Element elem = (Element) networks.item(i);
            NodeList networkList = elem.getElementsByTagName("Facility");
            ArrayList<String> networkLinks = new ArrayList<>();

            for (int j = 0; j < networkList.getLength(); j++) {
                if (networkList.item(j).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }
                elem = (Element) networkList.item(j);
                String location = elem.getElementsByTagName(locationTag).item(0).getTextContent();
                String distance = elem.getElementsByTagName(distanceTag).item(0).getTextContent();
                networkLinks.add(location + "-" + distance);
            }
            networkDetails =  new ArrayList(networkLinks);
            network.putIfAbsent(Integer.valueOf(facilityId), networkDetails);

        }
    }
    public HashMap<Integer, List<String>> getNetworkDetils() throws NullException {
        if(this.network.isEmpty()) {
            throw new NullException("Network Details");
        } else {
            return this.network;
        }
    }
}
