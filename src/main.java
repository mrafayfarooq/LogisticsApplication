import org.w3c.dom.NodeList;
/**
 * Created by Muhammad Rafay on 4/8/17.
 */
class main {
    public static void main(String[] args) {
        String facilitiesAndNetworks = "inputs/Facilities&Network.xml";
        String itemCatalog = "inputs/ItemCatalog.xml";
        String facilityInventory = "inputs/FacilityInventory.xml";


        XMLParser parser = new XMLParser();
        parser.parseFileWithName(facilitiesAndNetworks);
        try {
            NodeList parsedFile = parser.getXmlEntries();

            FacilityManager facilityManager = new FacilityManager("Id", "Location", "ProcessingPowerPerDay", "Cost");
            facilityManager.loadFacilitiesAndNetwork(parsedFile);

            NetworkManager networkManager = new NetworkManager("Location", "Distance");
            networkManager.loadNetworks(parsedFile);

            ItemManager itemManager = new ItemManager("ItemID", "Price");
            parser.parseFileWithName(itemCatalog);
            parsedFile = parser.getXmlEntries();
            itemManager.loadItems(parsedFile);

            InventoryManager inventoryManager = new InventoryManager("Id", "ItemID", "Quantity");
            parser.parseFileWithName(facilityInventory);
            parsedFile = parser.getXmlEntries();
            inventoryManager.loadInventory(parsedFile);

            System.out.println(facilityManager.getFacilityDetails());
            System.out.println(networkManager.getNetworkDetils());
            System.out.println(itemManager.getItemDetails());
            System.out.println(inventoryManager.getFacilityInventoryDetails());

        } catch (NullException e) {
            e.printException();
        }
        // parser.parseFileWithName(facilitiesAndNetworks);


        /*
        try {
            String fileName = "inputs/Facilities&Network.xml";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            File xml = new File(fileName);
            if (!xml.exists()) {
                System.err.println("**** XML File '" + fileName + "' cannot be found");
                System.exit(-1);
            }

            Document doc = db.parse(xml);
            doc.getDocumentElement().normalize();

            NodeList facilityEntries = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < facilityEntries.getLength(); i++) {
                if (facilityEntries.item(i).getNodeType() == Node.TEXT_NODE) {
                    continue;
                }

                String entryName = facilityEntries.item(i).getNodeName();
                if (!entryName.equals("Facility")) {
                    System.err.println("Unexpected node found: " + entryName);
                    return;
                }

                // Get a node attribute
                NamedNodeMap aMap = facilityEntries.item(i).getAttributes();
                String facilityId = aMap.getNamedItem("Id").getNodeValue();

                // Get a named nodes
                Element elem = (Element) facilityEntries.item(i);
                String location = elem.getElementsByTagName("Location").item(0).getTextContent();
                String processingPowerPerDay = elem.getElementsByTagName("ProcessingPowerPerDay").item(0).getTextContent();

                // Get all nodes named "Book" - there can be 0 or more
                ArrayList<String> bookDescriptions = new ArrayList<>();
                NodeList bookList = elem.getElementsByTagName("Book");
                for (int j = 0; j < bookList.getLength(); j++) {
                    if (bookList.item(j).getNodeType() == Node.TEXT_NODE) {
                        continue;
                    }

                    entryName = bookList.item(j).getNodeName();
                    if (!entryName.equals("Book")) {
                        System.err.println("Unexpected node found: " + entryName);
                        return;
                    }

                    // Get some named nodes
                    elem = (Element) bookList.item(j);
                    String bookTitle = elem.getElementsByTagName("Title").item(0).getTextContent();
                    String bookAuthor = elem.getElementsByTagName("Author").item(0).getTextContent();
                    String bookDate = elem.getElementsByTagName("Date").item(0).getTextContent();
                    String bookIsbn13 = elem.getElementsByTagName("ISBN13").item(0).getTextContent();
                    // Create a string summary of the book
                    bookDescriptions.add(bookTitle + ", by " + bookAuthor + ", " + bookDate + " [" + bookIsbn13 + "]");
                }

                // Here I would create a Store object using the data I just loaded from the XML
                System.out.println("Store: " + storeName + " [Store: #" + storeId + "], " + storeAddress + "\n" + bookDescriptions + "\n");

            }

        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            e.printStackTrace();
        }*/
    }
}

