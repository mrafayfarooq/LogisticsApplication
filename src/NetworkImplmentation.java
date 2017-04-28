/**
 * Created by Muhammad Rafay on 4/27/17.
 */
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class NetworkImplmentation extends FacilityImplmentation implements Network {
    protected final HashMap<Integer, List<String>> network = new HashMap<>();
    private static NetworkImplmentation instance = null;
    private NetworkImplmentation(NodeList networks) {
        loadNetworks(networks);
    }
    public static NetworkImplmentation getInstance(NodeList networks){
        if(instance == null){
            instance = new NetworkImplmentation(networks);
        }
        return instance;
    }
    private void loadNetworks(NodeList networks) {
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
                aMap = elem.getAttributes();
                String networkId = aMap.getNamedItem("Id").getNodeValue();
                String location = elem.getElementsByTagName("Location").item(0).getTextContent();
                String distance = elem.getElementsByTagName("Distance").item(0).getTextContent();
                networkLinks.add(networkId + "-" + location + "-" + distance);
            }
            List <String> networkDetails = new ArrayList(networkLinks);
            network.putIfAbsent(Integer.valueOf(facilityId), networkDetails);
        }
    }
    public List<String> getNetwork(String  facilityName) throws NullException {
        if(this.network.isEmpty()) {
            throw new NullException("Network Details");
        } else {
            return this.network.get(super.getFacilityId(facilityName));
        }
    }
}
