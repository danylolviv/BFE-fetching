package org.example;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class OISQueryService {

    private static final String SERVICE_URL = "https://www.lifaois.dk/Oisservice/OISservice.asmx/Query";
    private static final String UID = "DanskScanning";
    private static final String PWD = "[IChicY9";
    private static final String META = "true";

    /**
     * Executes a SQL query by sending a GET request to a web service.
     * The SQL query is URL encoded and appended to the service URL along with other parameters.
     * This method parses the received XML response into a list of Record objects.
     *
     * @param sql The SQL query string to be executed.
     * @return List<Record> A list of Record objects representing the data returned from the query.
     * @throws Exception If any error occurs during the method execution, such as URL encoding issues,
     *                   connection errors, or XML parsing errors.
     */
    public List<Record> executeQuery(String sql) throws Exception {
        String encodedSQL = URLEncoder.encode(sql, StandardCharsets.UTF_8.toString());
        String urlString = SERVICE_URL + "?UID=" + UID + "&PWD=" + PWD + "&Meta=" + META + "&SQL=" + encodedSQL;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(connection.getInputStream());
        doc.getDocumentElement().normalize();

        return parseResponse(doc);
    }

    /**
     * Parses an XML Document object to extract data and converts it into a list of Record objects.
     * This method is designed to parse the specific XML structure returned by the web service
     * in response to the SQL query.
     *
     * NB! There are descrapancies in how data from different views is separated. In the case of Address I believe it is
     * /n newlines. In the case of Matrikel views it is comma. for that reason there is if statment that would handle
     * different types of separation
     *
     * @param doc The XML Document to be parsed.
     * @return List<Record> A list of Record objects representing the parsed data.
     */
    private List<Record> parseResponse(Document doc) {
        NodeList fields = doc.getElementsByTagName("Field");
        NodeList dataNodes = doc.getElementsByTagName("Data");
        List<Record> records = new ArrayList<>();

        for (int i = 0; i < dataNodes.getLength(); i++) {
            String dataContent = dataNodes.item(i).getTextContent().trim();
            String[] recordStrings;

            // Determine the delimiter based on the presence of a comma
            if (dataContent.contains(",")) {
                // If commas are present, split by commas
                recordStrings = dataContent.split(",");
            } else {
                // Otherwise, split by new lines
                recordStrings = dataContent.split("\n");
            }

            for (String recordString : recordStrings) {
                // Splitting each record by semicolon for fields
                String[] values = recordString.split(";");
                Record record = new Record();
                for (int j = 0; j < fields.getLength(); j++) {
                    String fieldName = fields.item(j).getAttributes().getNamedItem("Name").getNodeValue();
                    // Check if the field value is available to avoid ArrayIndexOutOfBoundsException
                    String fieldValue = j < values.length ? values[j] : "";
                    record.addField(fieldName, fieldValue);
                }
                records.add(record);
            }
        }
        return records;
    }


    //Old and working
//    private List<Record> parseResponse(Document doc) {
//        NodeList fields = doc.getElementsByTagName("Field");
//        NodeList dataNodes = doc.getElementsByTagName("Data");
//        List<Record> records = new ArrayList<>();
//
//        for (int i = 0; i < dataNodes.getLength(); i++) {
//            String[] recordStrings = dataNodes.item(i).getTextContent().trim().split("\\s+");
//            for (String recordString : recordStrings) {
//                String[] values = recordString.split(";");
//                Record record = new Record();
//                for (int j = 0; j < fields.getLength(); j++) {
//                    String fieldName = fields.item(j).getAttributes().getNamedItem("Name").getNodeValue();
//                    String fieldValue = values[j];
//                    record.addField(fieldName, fieldValue);
//                }
//                records.add(record);
//            }
//        }
//        return records;
//    }

}
