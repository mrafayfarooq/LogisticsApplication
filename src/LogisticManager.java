import org.w3c.dom.NodeList;
import java.util.List;

/**
 * Created by Muhammad Rafay on 4/14/17.
 */
public interface LogisticManager {
    public void load(NodeList node);
    public List getDetails(Integer key) throws NullException;

}
