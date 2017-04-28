import org.w3c.dom.NodeList;
import java.util.List;
/**
 * Created by Muhammad Rafay on 4/27/17.
 */
interface Network {
    List<String> getNetwork(String facilityName) throws NullException;
}
