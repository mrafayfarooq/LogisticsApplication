/**
 * Created by Muhammad Rafay on 4/9/17.
 */

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.NodeList;

public class XMLParser implements Parser {
    final String[] inputs = {"inputs/Facilities&Network.xml", "inputs/ItemCatalog.xml","inputs/FacilityInventory.xml"};
    private NodeList[] xmlEntries = new NodeList[3];
    private static XMLParser instance = null;
    private XMLParser(){}
    public static XMLParser getInstance(){
        if(instance == null){
            instance = new XMLParser();
        }
        return instance;
    }
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
    public NodeList[] getEntries() {
        return xmlEntries;
    }
}
