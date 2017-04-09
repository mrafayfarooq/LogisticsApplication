/**
 * Created by Muhammad Rafay on 4/9/17.
 */
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import org.w3c.dom.DOMException;
import java.io.IOException;


public class XMLParser implements Parser {
    private Document document;
    @Override
    public void parseFileWithName(String name) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            File xml = new File(name);
            if (!xml.exists()) {
                System.err.println("**** XML File '" + name + "' cannot be found");
                System.exit(-1);
            }

            this.document = db.parse(xml);
            document.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }
    }
}
