/*+
  Created by Muhammad Rafay on 4/8/17.

  */
import org.w3c.dom.NodeList;

/**+
 * Parser Interface for parsing files. This class provide role and behavior for any class wish to parse
 * the input files.
 */
interface Parser {
     /**+
      * Parse function to parse the files and load it
      */
     void parse();

     /**+
      *
      * @param fileName the name of the file to get
      * @return NodeList
      * @throws NullException if file doesn't exist
      */
     NodeList getEntries(String fileName) throws NullException;
}
