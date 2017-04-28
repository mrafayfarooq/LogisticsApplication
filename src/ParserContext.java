import org.w3c.dom.NodeList;

/**
 * Created by Muhammad Rafay on 4/26/17.
 */
public class ParserContext {
    private Parser parser;
    ParserContext(String fileType){
        switch (fileType) {
            case "XML":
                this.parser = XMLParser.getInstance();
                break;
        }
        this.parser.parse();
    }
    public NodeList[] getEntries() {
        return this.parser.getEntries();
    }
}

