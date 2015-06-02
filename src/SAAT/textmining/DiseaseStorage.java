/***********************************************
 * DiseaseStorage.java
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
 * Handles the DB Source.
 * It proviedes the other Parts of this Program with Data from the Database
 ***********************************************
 */
package SAAT.textmining;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import SAAT.textmining.gui.LogViewer;

/**
 * DiseaseStorage
 * Handles the DB Source.
 * It proviedes the other Parts of this Program with Data from the Database
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class DiseaseStorage {

    /**
     * Variable declaration
     */
    private LogViewer log_;
    private SAAT.generic.ConnectDataBase db_;
    private LinkedBlockingQueue<DiseaseEntry> queue_ = new LinkedBlockingQueue<DiseaseEntry>();
    private int offset_ = 0;
    private int increment_ = 1000;
    private int max_diseases_ = 2000000;
    private int[] disease_ids_to_code_ = null;
    private HashMap<Integer, HashSet<Integer>> old_disease_dictionary_link_cache_ = null;

    /**
     * Creates a new instance of DiseaseStorage
     *
     * @param Takes a LogViewer, to write it's log messages into it
     */
    public DiseaseStorage(LogViewer log) {
        log_ = log;
        db_ = new SAAT.generic.ConnectDataBase();

        try {
            //db_.connectPostgres();
            db_.connectPostgresPropertie();
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("DiseaseStorage::DiseaseStorage - PSQLException Error: " + exloc.getMessage());
            System.err.println("DiseaseStorage::DiseaseStorage - Exit Program, No connection to lockal findings database available");
            System.exit(-1);
        }

        max_diseases_ = 2000000;
    }

    /**
     * Enables the Partial DB load method of the Storage
     * It takes an array of disease ids, so only they will be encoded.
     *
     * @param array of disease ids
     */
    public void enablePartialLoad(int[] disease_ids) {
        disease_ids_to_code_ = disease_ids;
        increment_ = 5000; //this should not be set to high!
        offset_ = 0;
        max_diseases_ = disease_ids_to_code_.length;
    }

    /**
     * Public accessor to get a DiseaseEntry
     */
    public DiseaseEntry getNextEntry() {
        if (queue_.size() <= 0) {
            loadData();
        }

        DiseaseEntry ret = null;
        synchronized (this) {
            ret = queue_.poll();
        }
        return ret;
    }

    /**
     * Internal methode to load more data into the storage
     * Fills up the queue
     */
    private synchronized void loadData() {
        if (offset_ > max_diseases_) {
            return;
        }

        if (old_disease_dictionary_link_cache_ == null
                && disease_ids_to_code_ == null) {

            old_disease_dictionary_link_cache_ = new HashMap<Integer, HashSet<Integer>>();

            log_.out_printTime(false, false);
            log_.out_println("loading disease link data...");
            try {
                java.sql.Statement stat2 = db_.getNewStatement();
                //java.sql.ResultSet result2 = stat2.executeQuery("SELECT disease_id, dictionary_id from dictionary2_diseases_link ORDER BY disease_id, dictionary_id;");
                // §§§1 Changed findings
                java.sql.ResultSet result2 = stat2.executeQuery("SELECT finding_id disease_id, dictionary_id from dictionary2_findings_link ORDER BY disease_id, dictionary_id;");


                while (result2.next()) {
                    int did1 = result2.getInt("disease_id");
                    int did2 = result2.getInt("dictionary_id");

                    if (!old_disease_dictionary_link_cache_.containsKey(did1)) {
                        old_disease_dictionary_link_cache_.put(did1, new HashSet<Integer>());
                    }

                    old_disease_dictionary_link_cache_.get(did1).add(did2);
                }
                stat2.close();
            } catch (SQLException ex) {
                System.err.println("SQLException in DiseaseStorage::loadData: " + ex.getMessage());
                ex.printStackTrace();
            }
            log_.out_printTime(false, false);
            log_.out_println("loading disease link data...done");
        }

        System.out.println(String.format("loading disease data (%07d-%07d)...", offset_, offset_ + increment_));

        try {
            java.sql.Statement stat = db_.getNewStatement();
            java.sql.ResultSet result;

            if (disease_ids_to_code_ != null) {
                if (disease_ids_to_code_.length < 1) {
                    return;
                }

                String sq = "( 1 = 2";
                for (int i = offset_; i < offset_ + increment_; ++i) {
                    if (disease_ids_to_code_.length > i) {
                        //sq += " OR disease_id = " + Integer.toString(disease_ids_to_code_[i]);
                        sq += " OR finding_id = " + Integer.toString(disease_ids_to_code_[i]);
                    }
                }
                sq += ")";

                //result = stat.executeQuery("SELECT disease_id, diagnosis_clean,t,n,m,r,g,l,v FROM diseases WHERE diagnosis_clean IS NOT null AND " + sq + " ORDER BY disease_id OFFSET " + offset_ + " LIMIT " + increment_ + ";");
                // §§§1 Changed findings
                result = stat.executeQuery("SELECT finding_id disease_id, diagnosis_clean AS diagnosis_clean,t,n,m,r,g,l,v FROM findings WHERE diagnosis_clean IS NOT null AND " + sq + " ORDER BY finding_id OFFSET " + offset_ + " LIMIT " + increment_ + ";");
            } else {
                //result = stat.executeQuery("SELECT disease_id, diagnosis_clean,t,n,m,r,g,l,v FROM diseases WHERE diagnosis_clean IS NOT null AND disease_id >= " + offset_ + " AND disease_id < " + (offset_ + increment_) + " ORDER BY disease_id;");
                // §§§1 Changed findings
                result = stat.executeQuery("SELECT finding_id disease_id, diagnosis_clean AS diagnosis_clean,t,n,m,r,g,l,v FROM findings WHERE diagnosis_clean IS NOT null AND finding_id >= " + offset_ + " AND finding_id < " + (offset_ + increment_) + " ORDER BY finding_id;");
            }

            synchronized (this) {
                offset_ += increment_;
            }

            while (result.next()) {
                DiseaseEntry entry = new DiseaseEntry();
                entry.id = result.getInt("disease_id");
                
                entry.textOriginal = result.getString("diagnosis_clean");

                entry.addOldStaging("T", result.getString("T"));
                entry.addOldStaging("N", result.getString("N"));
                entry.addOldStaging("M", result.getString("M"));
                entry.addOldStaging("R", result.getString("R"));
                entry.addOldStaging("G", result.getString("G"));
                entry.addOldStaging("L", result.getString("L"));
                entry.addOldStaging("V", result.getString("V"));

                if (disease_ids_to_code_ != null) {
                    java.sql.Statement stat2 = db_.getNewStatement();


                    //java.sql.ResultSet result2 = stat2.executeQuery("SELECT dictionary_id from dictionary2_diseases_link WHERE disease_id = " + Integer.toString(entry.id) + ";");
                    // §§§1 Changed findings
                    java.sql.ResultSet result2 = stat2.executeQuery("SELECT dictionary_id from dictionary2_findings_link WHERE finding_id = " + Integer.toString(entry.id) + ";");

                    while (result2.next()) {
                        try {
                            entry.oldDictionaryIDs.add(result2.getInt("dictionary_id"));
                        } catch (Exception ex) {
                        }
                    }

                    stat2.close();

                } else {
                    HashSet<Integer> temp = old_disease_dictionary_link_cache_.get(entry.id);
                    if (temp != null) {
                        entry.oldDictionaryIDs = temp;
                    }
                }

                try {
                    synchronized (this) {
                        queue_.put(entry);
                    }
                } catch (InterruptedException ex) {
                    System.err.println("InterruptedException in DiseaseStorage::loadData: " + ex.getMessage());
                }
            }
            stat.close();

        } catch (SQLException ex) {
            System.err.println("SQLException in DiseaseStorage::loadData: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("loading disease data...done");
    }
}
