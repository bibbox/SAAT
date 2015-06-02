/***********************************************
 * praeperatType.java
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
 * Update the diseas tabel with examination type
 ***********************************************
 */
package SAAT.textmining.corrector;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;
import SAAT.generic.PrintTime;

/**
 * Update the diseas tabel with examination type
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class praeperatType {

    /**
     * Variable decleration
     */
    static ConnectDataBase dbconnection_;
    BufferedWriter fout_;

    /**
     * Creates a new instance of correctDiseases and connect new databas
     * connection.
     */
    public praeperatType() {
        dbconnection_ = new ConnectDataBase();
        try {
            dbconnection_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            System.err.println("praeperatType::praeperatType - PSQLException Error: " + ex.getMessage());
            System.err.println("praeperatType::praeperatType - Exit Program, No connection to lockal update database available");
        }
        fout_ = null;
    }

    /**
     * Generate the update for the examination type
     */
    public void execute() {
        try {
            Statement st = dbconnection_.getNewStatement();

            new PrintTime(false);
            System.out.println(" - read ids");

            ResultSet result = st.executeQuery("SELECT disease_id, finding_id_diagnosen FROM diseases where diagnosis IN (SELECT DISTINCT diagnosis FROM diseases WHERE topologyid like '%C56%');");

            HashSet<Integer> findingDiseasesID = new HashSet<Integer>();

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
                    if (!findingDiseasesID.contains(Integer.parseInt(fidsa[i]))) {
                        findingDiseasesID.add(Integer.parseInt(fidsa[i]));
                    }
                }
            }
            new PrintTime(false);
            System.out.println(" - read type");

            Iterator it = findingDiseasesID.iterator();
            HashMap<String, Integer> praep = new HashMap<String, Integer>();
            ResultSet result2 = st.executeQuery("SELECT finding_id, examination_type FROM findings;");

            new PrintTime(false);
            System.out.println(" - calc result");
            while (result2.next()) {
                int fidididid = result2.getInt("finding_id");

                if (!findingDiseasesID.contains(fidididid)) {
                    continue;
                }

                String p = result2.getString("examination_type");

                if (praep.containsKey(p)) {
                    int i = praep.get(p);
                    praep.remove(p);
                    ++i;
                    praep.put(p, i);
                } else {
                    praep.put(p, 1);
                }
            }

            Iterator it2 = praep.keySet().iterator();

            while (it2.hasNext()) {
                String p = (String) it2.next();
                int count = praep.get(p);

                System.out.println(p + " = " + Integer.toString(count));
            }
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
