package org.example;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws Exception {

        // Menu items in the console with the numbers you need to type in order to select menu
        // Number 3 is the one I used the most there I also have all the working queries

        String[] menuItems = {"Item 1: Request OIS", "Item 2: OIS Query service Single", "Item 3: OIS Query difference_Municode_And_All_Postal_Codes", "Item 4: Get count", "Item 5: Water"};
        Scanner scanner = new Scanner(System.in);

        StringBuilder sb = new StringBuilder();
        sb.append(URLEncoder.encode("SELECT * FROM JY64400V", "UTF-8"));
        // loop that runs the menu
        while (true) {
            System.out.println("Menu:");
            for (String item : menuItems) {
                System.out.println(item);
            }
            System.out.println("Enter the number of the item you want (or 0 to exit):");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 0) {
                break;
            } else if (choice > 0 && choice <= menuItems.length) {
                if (choice == 1){
                    // write logic to run the request here
                    OISTestRequest oisClassInstance = new OISTestRequest();
                    oisClassInstance.sipmlerequest();
//                    String[] data = oisClassInstance.getRecordsFrom(oisClassInstance.performRequest(workingSQLQuery));
//                    System.out.println(data);
                } else if (choice == 2) {
                    OISQueryService service = new OISQueryService();
                    List<Record> records = service.executeQuery("SELECT TOP 10 BFEnummer ,SFElokalId FROM MATRIKEL.BPFGpunktGeoView WHERE BFEnummer = 742475");
                    records.forEach(System.out::println);
                } else if (choice == 3) {
                    // service for handling requests
                    OISQueryService service = new OISQueryService();

                    List<String> sqlQueries = new ArrayList<>();
//                     AND Postnr NOT IN (6690, 6700, 6705, 6710, 6715, 6731, 6740, 6760, 6771)
                sqlQueries.add("SELECT DISTINCT Vejnavn, Vejkode_0, Vejkode, Kommunekode, Postnr FROM DAR.AdresseGeoView WHERE Kommunekode = 561");
//                sqlQueries.add("SELECT TOP 10 BFEnummer, SFElokalId FROM MATRIKEL.EjerlejlighedView");
//                sqlQueries.add("SELECT Vejnavn, Vejkode FROM DAR.AdresseForeloebigGeoView WHERE Kommunekode = 0561");
//                sqlQueries.add("SELECT Vejnavn, Vejkode FROM DAR.NavngivenvejGeoView WHERE Kommunekode = 0561");
//                sqlQueries.add("SELECT DISTINCT Vejnavn, Vejkode_0, Kommunekode, Postnr FROM DAR.AdresseGeoView WHERE Kommunekode = 561 AND Postnr NOT IN (6690, 6700, 6705, 6710, 6715, 6731, 6740, 6760, 6771)");
//                sqlQueries.add("SELECT DISTINCT Vejkode FROM DAR.HusnummerGeoView WHERE Kommunekode = 0561 AND Vejnavn = 'Guldregns Alle' ");
//                sqlQueries.add("SELECT id_lokalId, Vejkode, Status, Status_T, Vejkode_0, Vejnavn, husNrTal, husNrTal_0, HusNr, Jordstykke, KoorNord FROM DAR.HusnummerGeoView WHERE Vejkode = 2792 AND Kommunekode = 561");
//                sqlQueries.add("SELECT LifaId, id_lokalId, AdressepunktId, Position FROM DAR.AdressepunktGeoView WHERE id_lokalId = '0a3f508d-8669-32b8-e044-0003ba298018'");
//                sqlQueries.add("SELECT id_lokalId, AdressepunktId FROM DAR.AdressepunktGeoView WHERE id_lokalId != AdressepunktId;");

//                sqlQueries.add("SELECT DISTINCT k.NavngivenVej, h.Vejnavn, h.VejKode, h.Postnr, h.PostDistrikt " +
//                        "FROM DAR.NavngivenvejkommunedelView k, DAR.HusnummerGeoView h " +
//                        "WHERE k.Kommune = 561 " +
//                        "and h.NavngivenVej = k.NavngivenVej");

                    String potentialQuery = "SELECT" +
                            "    k.NavngivenVej as nanvgivenVejId," +
                            "    k.VejKode as vejKode," +
                            "    v.Vejnavn as vejnavn," +
                            "    CASE" +
                            "        WHEN (" +
                            "            SELECT COUNT(v2.Vejnavn)" +
                            "            FROM DAR.NavngivenvejGeoView v2" +
                            "            LEFT JOIN DAR.NavngivenvejkommunedelView k2 ON v2.NavngivenvejId = k2.NavngivenVej" +
                            "            WHERE k2.Kommune = 561 AND v2.Vejnavn = v.Vejnavn" +
                            "            GROUP BY v2.Vejnavn" +
                            "        ) > 1 THEN" +
                            "        (" +
                            "            v.Vejnavn+' ('+(SELECT TOP 1 CAST(h.Postnr as VARCHAR) + ' ' + h.PostDistrikt FROM DAR.HusnummerGeoView h where h.NavngivenVej = k.NavngivenVej)+')'\n" +
                            "        )" +
                            "        ELSE v.Vejnavn" +
                            "    END AS orgvejnavn" +
                            "FROM" +
                            "    DAR.NavngivenvejkommunedelView k" +
                            "    LEFT JOIN DAR.NavngivenvejGeoView v ON v.NavngivenvejId = k.NavngivenVej" +
                            "WHERE" +
                            "    k.Kommune = 561;";

                    sqlQueries.add(potentialQuery);




                    String ql = "SELECT Vejnavn, HusnummerTekst from DAR.AdresseGeoView where Kommunekode = 561";

//                sqlQueries.add("SELECT Vejnavn, Vejkode, Kommunenavn FROM DAR.HusnummerGeoView WHERE Kommunekode = 561 ");

//                sqlQueries.add("SELECT v.LifaId, v.Vejnavn, v.NavngivenvejId, k.VejKode, k.Kommune, p.Postnummer " +
//                                "FROM DAR.NavngivenvejGeoView v, DAR.NavngivenvejkommunedelView k, DAR.NavngivenvejpostnummerView p " +
//                                "WHERE v.AdministreresAfKommune = 561 and k.vejKode = 519  " +
//                                "and k.NavngivenVej = v.NavngivenvejId and p.NavngivenVej = k.NavngivenVej");

                // Loop through each query and execute it
                for (String query : sqlQueries) {
                    List<Record> records = service.executeQuery(query);
                    System.out.println("Results for the query ");
                    System.out.println(query);
                    records.forEach(record -> System.out.println(record.toString().replace("\n", ", "))); // replacing new lines with ", "
                }

                } else if (choice == 4) {

                }
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    // some code for the sub menu items, not used
    private static void handleSubMenuOption() throws Exception {
        Scanner scanner = new Scanner(System.in);
        String[] views = {
                "MATRIKEL.EjerlejlighedView",
                "MATRIKEL.BestemtFastEjendomView",
                "MATRIKEL.JordstykkeGeoView"
        };

        System.out.println("Select a view to query:");
        for (int i = 0; i < views.length; i++) {
            System.out.println((i + 1) + ": " + views[i]);
        }

        int viewChoice;
        try {
            viewChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        if (viewChoice < 1 || viewChoice > views.length) {
            System.out.println("Invalid choice. Please try again.");
            return;
        }

        String selectedView = views[viewChoice - 1];
        String sqlQuery = "SELECT COUNT(*) FROM " + selectedView;
        OISQueryService service = new OISQueryService();
        List<Record> records = service.executeQuery(sqlQuery);
        records.forEach(System.out::println);
    }
}