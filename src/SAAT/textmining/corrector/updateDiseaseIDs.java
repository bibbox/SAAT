/***********************************************
 * updateDiseaseIDs.java
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
 * Update the diseas tabel with findings ids
 ***********************************************
 */
package SAAT.textmining.corrector;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;

/**
 * Update the diseas tabel with findings ids
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class updateDiseaseIDs {

    static ConnectDataBase dbconnection_;
    BufferedWriter fout_;

    /**
     * Creates a new instance of correctDiseases and connect new databas
     * connection.
     */
    public updateDiseaseIDs() {
        dbconnection_ = new ConnectDataBase();
        try {
            dbconnection_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            System.err.println("updateDiseaseIDs::updateDiseaseIDs - PSQLException Error: " + ex.getMessage());
            System.err.println("updateDiseaseIDs::updateDiseaseIDs - Exit Program, No connection to lockal update database available");
        }
        fout_ = null;
    }

    /**
     * Generate the update for the findings ids
     */
    public void execute() {
        try {
            Statement st = dbconnection_.getNewStatement();
            fout_ = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("updateDiseasesIDs.sql")));
            //ResultSet result = st.executeQuery("SELECT disease_id, finding_id_diagnosen FROM diseases;");
            // §§§1 Changed findings
            ResultSet result = st.executeQuery("SELECT disease_id, finding_id_diagnosen FROM diseases;");
            HashMap<Integer, String> findingDiseases = new HashMap<Integer, String>();

            while (result.next()) {
                int did = result.getInt("disease_id");
                String fids = result.getString("finding_id_diagnosen");
                //crack fids string
                String[] fidsa = fids.split(",");
                if (fidsa.length == 0) {
                    fidsa = new String[1];
                    fidsa[0] = fids;
                }
                // make subquery
                for (int i = 0; i < fidsa.length; i++) {
                    if (findingDiseases.containsKey(Integer.parseInt(fidsa[i]))) {
                        String temp = (String) findingDiseases.get(Integer.parseInt(fidsa[i]));
                        temp += "," + Integer.toString(did);
                        findingDiseases.remove(fidsa[i]);
                        findingDiseases.put(Integer.parseInt(fidsa[i]), temp);
                    } else {
                        findingDiseases.put(Integer.parseInt(fidsa[i]), Integer.toString(did));
                    }
                }
            }
            Iterator it = findingDiseases.keySet().iterator();

            while (it.hasNext()) {
                int fid = (Integer) it.next();
                String didi = findingDiseases.get(fid);

                String q = "UPDATE findings SET disease_id = '" + didi + "' WHERE finding_id = " + fid + ";";
                try {
                    fout_.write(q);
                    fout_.newLine();
                } catch (IOException ex) {
                    System.err.println("correctDiseases::encodeDisease: " + ex.getMessage());
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
     * Class to execute updates from the generated file
     */
    public void upload() {
        dbconnection_.updateFromFile("updateDiseasesIDs.sql");
    }
}
