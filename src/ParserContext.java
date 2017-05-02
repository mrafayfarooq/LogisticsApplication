import org.w3c.dom.NodeList;

/**
 * Created by Muhammad Rafay on 4/26/17.
 */

/**+
 * ParserContext - The context class for Parser.
 *
 * This class takes a string in it's constructor and make a relevent object.
 * The string is valid file extension/type of input file.
 */
class ParserContext {
    private Parser parser;

    /**+
     *
     * @param fileType type of file
     * @throws NullException if invalid extension is given
     */
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

    /**+
     *
     * @param fileName the name of the file to get
     * @return NodeList
     * @throws NullException if file doesn't exist
     */
    public NodeList getEntries(String fileName) throws NullException {
        return parser.getEntries(fileName);
    }
}

