/***********************************************
 * ExportIBMT.java
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
 * Export date for the import into the IFDB from the CRIP Project
 ***********************************************
 */
package SAAT.textmining.munich;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import SAAT.generic.ConnectDataBase;
import java.util.HashMap;
import java.util.HashSet;
import SAAT.generic.StringEncrypter;

/**
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class ExportIBMT {

    /**
     * Variable decleration
     */
    private String export_folder_;
    private ConnectDataBase db_;
    private HashMap<Integer, String> icd10codes_;
    private HashMap<Integer, String> icdocodes_;
    private HashMap<Integer, HashSet<Integer>> dictionary_diseases_link_;

    /**
     * Generate a new instance of the export file and generate the codes
     *
     * @param folder The export folder
     */
    public ExportIBMT(String folder) {
        export_folder_ = folder;
        db_ = new ConnectDataBase();
        loadICDCodes();
        try {
            db_.connectPostgresPropertie();
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("ExportIBMT::ExportIBMT - PSQLException Error: " + exloc.getMessage());
            System.err.println("ExportIBMT::ExportIBMT - Exit Program, No connection to lockal update database available");
            System.exit(-1);
        }
        loadDictionaryDiseasesLink();
    }

    /**
     * Generate a list of the link ids tabel for dictionary and the disease
     * tabel.
     */
    private void loadDictionaryDiseasesLink() {
        try {
            java.sql.Statement st = db_.getNewStatement();
            java.sql.ResultSet result = st.executeQuery("SELECT dictionary_id, disease_id FROM dictionary2_diseases_link ORDER BY disease_id;");
            int old_dise_id = 0;
            int dise_id = 0;
            int dict_id = 0;
            HashSet<Integer> dict_ids = null;
            dictionary_diseases_link_ = new HashMap<Integer, HashSet<Integer>>();
            while (result.next()) {
                dise_id = result.getInt("disease_id");
                dict_id = result.getInt("dictionary_id");

                if (old_dise_id == 0) {
                    old_dise_id = dise_id;
                    dict_ids = new HashSet<Integer>();
                    dict_ids.add(dict_id);
                } else {
                    if (old_dise_id == dise_id) {
                        dict_ids.add(dict_id);
                    } else {
                        dictionary_diseases_link_.put(old_dise_id, new HashSet<Integer>());
                        dictionary_diseases_link_.get(old_dise_id).addAll(dict_ids);
                        old_dise_id = dise_id;
                        dict_ids.clear();
                        dict_ids = null;
                        dict_ids = new HashSet<Integer>();
                        dict_ids.add(dict_id);
                    }
                }
            }
            dictionary_diseases_link_.put(old_dise_id, dict_ids);
        } catch (SQLException ex) {
            System.err.println("ExportIBMT::loadICD10Codes - PSQLException Error: " + ex.getMessage());
            System.err.println("ExportIBMT::loadICD10Codes - Exit Program, No connection to lockal update database available");

        }
    }

    /**
     * Load ICD 10 and ICD O codes
     */
    private void loadICDCodes() {
        try {
            String crypted_string = null;
            String connection_string = null;
            try {
                FileInputStream fstream = new FileInputStream("dictserver.key");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    crypted_string = strLine;
                }
                //Close the input stream
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                StringEncrypter encrypter;
                encrypter = new StringEncrypter();
                connection_string = encrypter.decrypt(crypted_string);
            } catch (StringEncrypter.EncryptionException ex) {
                ex.printStackTrace();
            }

            String[] split = connection_string.split(",");
            //System.out.println("ex1db_.connectPostgres(" + split[0] + ":" + split[1] + "/" + split[2] +","+ split[3] +","+ split[4] + ");");
            db_.connectPostgres(split[0] + ":" + split[1] + "/" + split[2], split[3], split[4]);
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("ExportIBMT::loadICDCodes - PSQLException Error: " + exloc.getMessage());
            System.err.println("ExportIBMT::loadICDCodes - Exit Program, No connection to lockal update database available");
            System.exit(-1);
        }

        loadICD10Codes();
        loadICDOCodes();

        db_.closeConnection();
    }

    /**
     * Generate a map for ICD 10 codes
     */
    private void loadICD10Codes() {
        try {
            icd10codes_ = new HashMap<Integer, String>();
            java.sql.Statement st = db_.getNewStatement();
            java.sql.ResultSet result = st.executeQuery("SELECT dictionary_id, code_value FROM dictionary2 WHERE iscode = true AND code_typ ILIKE 'ICD-10';");
            while (result.next()) {
                icd10codes_.put(result.getInt("dictionary_id"), result.getString("code_value"));
            }
        } catch (SQLException ex) {
            System.err.println("ExportIBMT::loadICD10Codes - PSQLException Error: " + ex.getMessage());
            System.err.println("ExportIBMT::loadICD10Codes - Exit Program, No connection to lockal update database available");

        }
    }

    /**
     * Generate a map for ICD O codes
     */
    private void loadICDOCodes() {
        try {
            icdocodes_ = new HashMap<Integer, String>();
            java.sql.Statement st = db_.getNewStatement();
            java.sql.ResultSet result = st.executeQuery("SELECT dictionary_id, code_value FROM dictionary2 WHERE iscode = true AND code_typ ILIKE 'ICD-O';");
            while (result.next()) {
                icdocodes_.put(result.getInt("dictionary_id"), result.getString("code_value"));
            }
        } catch (SQLException ex) {
            System.err.println("ExportIBMT::loadICD10Codes - PSQLException Error: " + ex.getMessage());
            System.err.println("ExportIBMT::loadICD10Codes - Exit Program, No connection to lockal update database available");

        }
    }

    /**
     * Function to get the ICD 10 code for a disease ID.
     *
     * @param disease_id The ID for geting the ICD 10 code
     * @return The ICD 10 code
     */
    private String getICD10(Integer disease_id) {
        HashSet<Integer> tmp = new HashSet<Integer>();
        String return_value = "";
        if (dictionary_diseases_link_.containsKey(disease_id)) {
            tmp = dictionary_diseases_link_.get(disease_id);
            for (Integer item : tmp) {
                if (icd10codes_.containsKey(item)) {
                    String s = icd10codes_.get(item);
                    if (return_value.length() == 0) {
                        return_value = s;
                    } else {
                        return_value += "; " + s;
                    }
                }
            }
        }
        return return_value;
    }

    /**
     * Function to get the ICD O code for a disease ID.
     *
     * @param disease_id The ID for geting the ICD O code
     * @return The ICD O code
     */
    private String getICDO(Integer disease_id) {
        HashSet<Integer> tmp = new HashSet<Integer>();
        String return_value = "";
        if (dictionary_diseases_link_.containsKey(disease_id)) {
            tmp = dictionary_diseases_link_.get(disease_id);
            for (Integer item : tmp) {
                if (icdocodes_.containsKey(item)) {
                    String s = icdocodes_.get(item);
                    if (return_value.length() == 0) {
                        return_value = s;
                    } else {
                        return_value += "; " + s;
                    }
                }
            }
        }
        return return_value;
    }

    /**
     * Export the data into a csv file with tabes as seperators selected from
     * the Database.
     */
    private void exportCsv() {
        try {

            Date dt = new Date();
            // Festlegung des Formats:
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
            // Formatierung zu String:
            String date = df.format(dt);
            File export_file = new File(export_folder_ + "/export_" + date + ".csv");

            FileWriter fw = new FileWriter(export_file);
            BufferedWriter out = new BufferedWriter(fw);

            String topic = "PSN\tPPSN\tAGE\tSEX\tT\tN\tM\tR\tG\tMORPHOLOGYCLASS\tMORPHOLOGYID\tTOPOGRAPHYCLASS\tTOPOGRAPHYID\tTYPEOFSPECIMEN";
            out.write(topic);
            out.write("\r\n");

            try {
                java.sql.Statement st = db_.getNewStatement();
                java.sql.ResultSet result = st.executeQuery("SELECT disease_id, patient_id, age, diagnosis_clean, t, g, m, r, n, sex, specimen FROM diseases;");

                while (result.next()) {
                    String line = "";
                    line += result.getString("disease_id") + "\t";
                    line += result.getString("patient_id") + "\t";
                    String age = result.getString("age");
                    if (age.equals("0")) {
                        age = "";
                    } //OG// Alter 0 rausnehmen für IFDB.
                    line += age + "\t";
                    line += result.getString("sex") + "\t";
                    line += result.getString("t") + "\t";
                    line += result.getString("n") + "\t";
                    line += result.getString("m") + "\t";
                    line += result.getString("r") + "\t";
                    line += result.getString("g") + "\t";
                    line += "ICD-O" + "\t";
                    line += getICDO(result.getInt("disease_id")) + "\t";
                    line += "ICD-10" + "\t";
                    line += getICD10(result.getInt("disease_id")) + "\t";
                    line += result.getString("specimen");
                    line = line.replaceAll("null", "");
                    out.write(line);
                    out.write("\r\n");

                }
            } catch (SQLException ex) {
                System.err.println("Transaction failed!");
                System.err.println("SQL Error: " + ex.getMessage());
            }
            out.close();
        } catch (Exception se) {
            System.out.println("Error - " + se.toString());
        }
    }

    /**
     * Run the exporting script
     */
    public void run() {
        exportCsv();
    }
}
