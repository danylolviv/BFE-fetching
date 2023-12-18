package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import sun.net.www.http.HttpClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class OISTestRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    // Constants for POST parameters should be defined here
    private static final String LIFA_ROW_SEPARATOR = "\n";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String POST_UID = "UID";
    private static final String POST_PWD = "PWD";
    private static final String POST_SQL = "SQL";
    private static final String POST_META = "Meta";
    private static final String POST_FUNC = "Proc";
    private static final String POST_FUNC_PAR = "Par";

    private String url = "https://www.lifaois.dk/OISservice/OISService.asmx/Query";
    private String userId = "DanskScanning";
    private String password = "[IChicY9";

    public String[] getRecordsFrom(Node node) throws OISTestRequest.OisRequestException {
        String[] records = null;

        try {
            Node data = (Node) XPathFactory.newInstance().newXPath().evaluate("/Query/Data", node, XPathConstants.NODE);

            if (data == null) {
                throw new NullPointerException("The LifaOIS response does not contain any data");
            }

            String dataContent = data.getTextContent();
            if (dataContent != null && !dataContent.isEmpty() && !dataContent.equalsIgnoreCase("EOF")) {
                records = dataContent.split(LIFA_ROW_SEPARATOR);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to evaluate data", e);
            throw new OISTestRequest.OisRequestException("An error occured while extracting lifa datarows", e);
        }

        return records;
    }

    public Node performRequest(String query) throws OISTestRequest.OisRequestException {
        System.out.println(query);
        OutputStreamWriter writer = null;
        try {
            System.out.println("here");
            StringBuilder data = new StringBuilder();
            data.append(URLEncoder.encode(POST_UID, DEFAULT_ENCODING)).append("=")
                    .append(URLEncoder.encode(userId, DEFAULT_ENCODING))
                    .append("&").append(URLEncoder.encode(POST_PWD, DEFAULT_ENCODING)).append("=")
                    .append(URLEncoder.encode(password, DEFAULT_ENCODING))
                    .append("&").append(URLEncoder.encode(POST_SQL, DEFAULT_ENCODING)).append("=")
                    .append(URLEncoder.encode(query, DEFAULT_ENCODING))
                    .append("&").append(URLEncoder.encode(POST_META, DEFAULT_ENCODING)).append("=")
                    .append(URLEncoder.encode("0", DEFAULT_ENCODING));

            URL documentUrl = new URL(url);
            URLConnection connection = documentUrl.openConnection();
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setDoOutput(true);

            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(data.toString());
            writer.flush();

            InputSource source = new InputSource(new GZIPInputStream(connection.getInputStream()));
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (Exception e) {
            System.out.println("error");
            LOGGER.error("An error occurred while performing request", e);
            throw new OISTestRequest.OisRequestException("An error occurred while performing request", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close writer", e);
                }
            }
        }
    }

    // Custom exception class for handling request errors
    public static class OisRequestException extends Exception {
        public OisRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

//    public void sipmlerequest(){
//        try {
//            String baseUrl = "https://www.lifaois.dk/Oisservice/OISservice.asmx/Query";
//            String uid = "DanskScanning";
//            String pwd = "[IChicY9";
//            String meta = "false";
//            String sql = "Select * from BBR.BygningView WHERE BFEnummer = 8893836";
//
//            String encodedSql = java.net.URLEncoder.encode(sql, "UTF-8");
//            String fullUrl = baseUrl + "?UID=" + uid + "&PWD=" + java.net.URLEncoder.encode(pwd, "UTF-8")
//                    + "&Meta=" + meta + "&SQL=" + encodedSql;
//
//            URL url = new URL(fullUrl);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//
//            int status = con.getResponseCode();
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
//            doc.getDocumentElement().normalize();
//
//            NodeList nList = doc.getElementsByTagName("Data");
//            for (int temp = 0; temp < nList.getLength(); temp++) {
//                Node nNode = nList.item(temp);
//                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element eElement = (Element) nNode;
//
//                    Building building = new Building();
//                    building.setLifaId(eElement.getElementsByTagName("LifaId").item(0).getTextContent());
//                    building.setBygningId(eElement.getElementsByTagName("BygningId").item(0).getTextContent());
//                    // set other fields...
//
//                    // add the building object to a list or process it as needed
//                }
//            }
//
//
//            StringBuffer content = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                content.append(inputLine);
//            }
//            in.close();
//            con.disconnect();
//
//            // Print the response
//            System.out.println("Response Code: " + status);
//            System.out.println("Response Body: \n" + content.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void sipmlerequest() {
        try {
            String baseUrl = "https://www.lifaois.dk/Oisservice/OISservice.asmx/Query";
            String uid = "DanskScanning";
            String pwd = "[IChicY9";
            String meta = "true";
            String sql = "Select * from BBR.BygningView WHERE BFEnummer = 8893836";
//            String sql = "SELECT TOP 100 * FROM BBR.BygningView";
//            String sqlQuery = "SELECT BFEnummer, SFElokalId, Ejerlavskode, Kommunekode, Kommunenavn, Matrikelnummer, Matnr_EjerlavsNavn FROM MATRIKEL.JordstykkeGeoView WHERE BFEnummer = 742475";
            String sqlQuery = "Select * from MATRIKEL.JordstykkeGeoView WHERE BFEnummer = 8893836";


            String encodedSql = java.net.URLEncoder.encode(sql, "UTF-8");
            String fullUrl = baseUrl + "?UID=" + uid + "&PWD=" + java.net.URLEncoder.encode(pwd, "UTF-8")
                    + "&Meta=" + meta + "&SQL=" + encodedSql;

            URL url = new URL(fullUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // Now parse the XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(content.toString())));
            doc.getDocumentElement().normalize();

            List<Building> list = new ArrayList<>();


            NodeList nList = doc.getElementsByTagName("Data");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Building building = new Building();
                    building.setLifaId(eElement.getElementsByTagName("LifaId").item(0).getTextContent());
                    building.setBygningId(eElement.getElementsByTagName("BygningId").item(0).getTextContent());

                    list.add(building);
                    // set other fields...

                    // add the building object to a list or process it as needed
                }
            }

            System.out.println(list);

            // Print the response
            System.out.println("Response Code: " + status);
            System.out.println("Response Body: \n" + content.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Building {
        private String lifaId;
        private String bygningId;
        // other fields...

        // Getter and setter for lifaId
        public String getLifaId() {
            return lifaId;
        }

        public void setLifaId(String lifaId) {
            this.lifaId = lifaId;
        }

        // Getter and setter for bygningId
        public String getBygningId() {
            return bygningId;
        }

        public void setBygningId(String bygningId) {
            this.bygningId = bygningId;
        }

        // Getters and setters for other fields...
    }

}
