/***********************************************
 * correctDiseases.java
 ***********************************************
 *
 ***********************************************
 * VERSION 1
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 ***********************************************
 * VERSION 2
 * http://sourceforge.net/projects/saat/
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 * Fraunhofer-Gesellschaft
 * Fraunhofer Institute for Biomedical Engineering
 * Central Research Infrastructure for molecular Pathology
 * Dr. Christina Schr�der
 * Christina.Schroeder(at)ibmt.fraunhofer.de
 * http://www.crip.fraunhofer.de/en/about/staff?noCache=776:1304399536
 ***********************************************
 * DESCRIPTION
 *
 * Update the Diseases from the diseases tabel
 ***********************************************
 */
package SAAT.textmining.corrector;

import java.io.*;
import java.sql.*;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;
import SAAT.generic.PrintTime;

/**
 * Update the Diseases from the diseases tabel
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class correctDiseases {

    /**
     * Variable decleration
     */
    static ConnectDataBase dbconnection_;
    BufferedWriter fout_;

    /**
     * Creates a new instance of correctDiseases
     */
    public correctDiseases() {
        dbconnection_ = new ConnectDataBase();
        try {
            dbconnection_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            System.err.println("correctDiseases::correctDiseases - PSQLException Error: " + ex.getMessage());
            System.err.println("correctDiseases::correctDiseases - Exit Program, No connection to lockal update database available");
        }
        fout_ = null;
    }

    /**
     * Start the Diseases update
     */
    public void execute() {
        try {
            Statement st = dbconnection_.getNewStatement();
            fout_ = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("updateDiseases.sql")));

            boolean stop = false;
            int start = 0;
            int increment = 10000;

            while (!stop) {
                new PrintTime(false);
                System.out.println(String.format("read disease DB from %7d to %7d", start, start + increment));
                ResultSet result = st.executeQuery("SELECT disease_id, finding_id_diagnosen FROM diseases WHERE disease_id > " + Integer.toString(start) + " AND disease_id < " + Integer.toString(start + increment) + " ORDER BY disease_id;");

                new PrintTime(false);
                System.out.println("start encoding");

                int counter = 0;

                while (result.next()) {
                    encodeDisease(result.getInt("disease_id"), result.getString("finding_id_diagnosen"));
                    counter++;
                }
                start += increment;

                if (counter == 0) {
                    stop = true;
                    break;
                }
            }
            fout_.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generate the update querys for diseases tabel
     * 
     * @param did The disease_id
     * @param fids The diagnos text
     */
    private void encodeDisease(int did, String fids) {
        if (fids == null) {
            return;
        }
        if (fids.equals("")) {
            return;
        }

        //crack fids string
        String[] fidsa = fids.split(",");
        if (fidsa.length == 0) {
            fidsa = new String[1];
            fidsa[0] = fids;
        }
        // make subquery
        String sq = "finding_id = " + fidsa[0];
        for (int i = 1; i < fidsa.length; i++) {
            sq += " OR finding_id = " + fidsa[i];
        }

        try {
            // get data
            Statement ste = dbconnection_.getNewStatement();

            ResultSet result = ste.executeQuery("SELECT finding_id, diagnosis,  diagnosis_clean, organ, organ_zuordnung FROM findings WHERE " + sq + " ORDER BY examination_date, finding_id;");

            String diagnosis = "";

            while (result.next()) {
                if (!diagnosis.equals("")) {
                    diagnosis += " ----- ";
                }

                String organ = result.getString("organ");
                String organZ = result.getString("organ_zuordnung");

                if (organ != null) {
                    organ = organ.replace("[", "AE");
                    organ = organ.replace("]", "UE");
                    organ = organ.replace("~", "SZ");
                    organ = organ.replaceAll("\\s+", " "); // space \r \n

                    organ = organ.replace("�", "OE");
                    organ = organ.replace("�", "UE");
                    organ = organ.replace("�", "AE");
                    organ = organ.replace("�", "SZ");
                } else {
                    organ = new String();
                }

                if (organZ != null) {
                    organZ = organZ.replace("[", "AE");
                    organZ = organZ.replace("]", "UE");
                    organZ = organZ.replace("~", "SZ");
                    organZ = organZ.replaceAll("\\s+", " "); // space \r \n

                    organZ = organZ.replace("�", "OE");
                    organZ = organZ.replace("�", "UE");
                    organZ = organZ.replace("�", "AE");
                    organZ = organZ.replace("�", "SZ");
                } else {
                    organZ = new String();
                }

                String temp = "";

                if (!organ.equals("")) {
                    temp += "((" + organ + ")) ";
                }

                diagnosis += temp.replace("'", "\\'");
                diagnosis += result.getString("diagnosis_clean").replace("'", "\\'");
            }

            String q = "UPDATE diseases SET diagnosis_clean = '" + diagnosis.toUpperCase() + "' WHERE disease_id = " + Integer.toString(did) + ";";
            try {
                fout_.write(q);
                fout_.newLine();
            } catch (IOException ex) {
                System.err.println("correctDiseases::encodeDisease: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Class to execute updates from the generated file
     */
    public void upload() {
        System.out.print("Uploading updateDiseases.sql...");
        dbconnection_.updateFromFile("updateDiseases.sql");
        System.out.print("Uploading updateDiseases.sql...done");
    }
}
