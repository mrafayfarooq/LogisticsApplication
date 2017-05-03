/*+
  Created by Muhammad Rafay on 4/9/17.

  */
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**+
 * XML Parse class implementing Parser interface.
 *
 */

public class XMLParser implements Parser {
    // input files
    private final String[] inputs = {"inputs/Facilities&Network.xml", "inputs/ItemCatalog.xml","inputs/FacilityInventory.xml"};
    private final NodeList[] xmlEntries = new NodeList[inputs.length];
    private static XMLParser instance = null;
    private XMLParser(){}

    /**
     * XMLParser Singleton.
     * @return instance of type XMLParser
     */
    public static XMLParser getInstance(){
        if(instance == null){
            instance = new XMLParser();
        }
        return instance;
    }

    /**+
     * Parse the file and store it into NodeList
     */
    public void parse() {
        try {
            for (int i=0;i< inputs.length; i++) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                File xml = new File(inputs[i]);
                if (!xml.exists()) {
                    System.err.println("**** XML File '" + inputs[i] + "' cannot be found");
                    System.exit(-1);
                }
                Document doc = db.parse(xml);
                doc.getDocumentElement().normalize();
                this.xmlEntries[i] = doc.getDocumentElement().getChildNodes();
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }
    }

    /**+
     *
     * @param fileName the name of the file to get
     * @return NodeList
     * @throws NullException if file doesn't exist
     */
    public NodeList getEntries(String fileName) throws NullException {
        switch (fileName) {
            case "Facility&Network":
                return xmlEntries[0];
            case "FacilityInventory":
                return xmlEntries[2];
            case "ItemCatalog":
                return xmlEntries[1];
            default:
                throw new NullException("File Name"+ fileName);
        }
    }
}
