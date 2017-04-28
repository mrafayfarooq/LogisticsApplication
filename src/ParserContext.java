import org.w3c.dom.NodeList;

/**
 * Created by Muhammad Rafay on 4/26/17.
 */
public class ParserContext {
    private Parser parser;
    ParserContext(String fileType) throws NullException {
        switch (fileType) {
            case "XML":
                parser = XMLParser.getInstance();
                parser.parse();
                break;
            default:
                throw new NullException(fileType);
           }
    }
    public NodeList[] getEntries() {
        return parser.getEntries();
    }
}

