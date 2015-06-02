/***********************************************
 * UpdateStorage.java
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
 * Prepares all update querys and cache them until
 * the queue limit is reached.
 ***********************************************
 */
package SAAT.textmining;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import SAAT.generic.ConnectDataBase;
import SAAT.textmining.gui.LogViewer;
import SAAT.textmining.icd.PriorityCodeTuple;
import SAAT.textmining.receptor.ReceptorEntry;

/**
 * UpdateStorage
 * Prepares all update querys and cache them until
 * the queue limit is reached.
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class UpdateStorage {

    private ConnectDataBase db_;
    private LogViewer log_;
    private ArrayList<String> updateQuerys_;
    private int queryCache_ = 1000;

    /**
     * Creates a new instance of UpdateStorage
     *
     * @param Takes a Logviewer, to write it's log messages into it
     */
    public UpdateStorage(LogViewer log) {
        db_ = new ConnectDataBase();
        try {
            db_.connectPostgresPropertie();
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("UpdateStorage::UpdateStorage - PSQLException Error: " + exloc.getMessage());
            System.err.println("UpdateStorage::UpdateStorage - Exit Program, No connection to lockal update database available");
            System.exit(-1);
        }

        log_ = log;
        updateQuerys_ = new ArrayList<String>();

    }

    /**
     * Load all cached querys into the db
     */
    public void updateDB() {
        String[] querys = updateQuerys_.toArray(new String[updateQuerys_.size()]);
        updateQuerys_.clear();

        updateDBTransaction(querys);
    }

    /**
     * Load all cached querys as transaction into the db
     * if transaction fails, querys will be divided into half
     * and recrusiv as transactions reworked until only 1 query
     * is left. This (defect) query is written into the log
     *
     * @param Array of SQL querys
     */
    private void updateDBTransaction(String[] querys) {
        //prepare transaction query
        String q = "";
        for (int i = 0; i < querys.length; ++i) {
            q += querys[i] + "\r\n";
        }

        try {
            Statement st = db_.getNewStatement();
            st.execute(q);

        } catch (SQLException ex) {
            log_.err_println("Transaction failed!");

            //transaction failed
            // if only 1 wrong entry write it to the error log
            if (querys.length == 1) {
                log_.err_println("SQL Error: " + ex.getMessage());
                log_.err_println(querys[0]);
                return;
            }

            //part querys & try again
            int size = querys.length / 2;
            String[] p1 = new String[size];
            String[] p2 = new String[querys.length - size];

            for (int i = 0; i < size; ++i) {
                p1[i] = querys[i];
            }

            for (int i = 0; i < size; ++i) {
                p2[i] = querys[size + i];
            }

            updateDBTransaction(p1);
            updateDBTransaction(p2);

        }
    }

    /**
     * This methode is used to update the ICD Codes of a disease.
     * It will automatically delete all deleted links and
     * inserts new links for Disease to Dictionary.
     * Also it updates the db, if the queue limit is reached
     *
     * @param a DiseaseEntry
     * @param a PriorityCodeTuple
     */
    public void updateICD(DiseaseEntry de, PriorityCodeTuple tuple3) {
        java.util.ArrayList<String> temp = new java.util.ArrayList<String>();
        //String delete = "DELETE FROM dictionary2_diseases_link WHERE disease_id = " + de.id + " AND (";
        // §§§1 Changed findings
        String delete = "DELETE FROM dictionary2_findings_link WHERE finding_id = " + de.id + " AND (";

        try {
            if (tuple3 != null) {
                Integer[] ids = tuple3.getDictionaryIDArray();
                if (ids != null) {
                    for (Integer id : ids) {
                        delete += " dictionary_id != " + id + " AND ";
                        if (!de.oldDictionaryIDs.contains(id)) {
                            //temp.add("INSERT INTO dictionary2_diseases_link (disease_id, dictionary_id) VALUES (" + de.id + ", " + id + ");");
                            // §§§1 Changed findings
                            temp.add("INSERT INTO dictionary2_findings_link (finding_id, dictionary_id) VALUES (" + de.id + ", " + id + ");");
                        }
                    }
                } else {
                    //delete = "DELETE FROM dictionary2_diseases_link WHERE disease_id = " + de.id + "; -- ";
                    // §§§1 Changed findings
                    delete = "DELETE FROM dictionary2_findings_link WHERE finding_id = " + de.id + "; -- ";
                }
            }
        } catch (Exception ex) {
            System.err.println("" + ex.getMessage());
            ex.printStackTrace();
        }
        delete += " 1 != 2);";

        updateQuerys_.add(delete);
        updateQuerys_.addAll(temp);

        updateDB();

    }

    /**
     * This methode is used to update the Staging Information of a disease.
     * It will only update changed values
     * Also it updates the db, if the queue limit is reached
     *
     * @param a DiseaseEntry
     * @param Staging information (KEY is T,N,M....)
     */
    void updateStaging(DiseaseEntry de, HashMap<String, String> staging) {
        if (staging.size() == 0) {
            return;
        }

        //String update = "UPDATE diseases SET ";
        // §§§1 Changed findings
        String update = "UPDATE findings SET ";

        boolean first = true;
        java.util.Iterator<String> it = staging.keySet().iterator();
        while (it.hasNext()) {
            String type = it.next();
            String value = staging.get(type);

            if (value != null) {
                if (first) {
                    update += type + " = '" + value + "' ";
                } else {
                    update += "," + type + " = '" + value + "' ";
                }
            } else {
                if (first) {
                    update += type + " = " + value + " ";
                } else {
                    update += "," + type + " = " + value + " ";
                }
            }

            first = false;
        }

        //update += " WHERE disease_id = " + Integer.toString(de.id) + ";";
        // §§§1 Changed findings
        update += " WHERE finding_id = " + Integer.toString(de.id) + ";";

        updateQuerys_.add(update);

        if (updateQuerys_.size() > queryCache_) {
            updateDB();
        }
    }

    /**
     * This methode is used to update the Staging Information of a disease.
     * It will only update changed values
     * Also it updates the db, if the queue limit is reached
     *
     * @param a DiseaseEntry
     * @param Staging information (KEY is T,N,M....)
     */
    void updateReceptor(DiseaseEntry de, HashMap<String, ReceptorEntry> receptor) {
        if (receptor.size() == 0) {
            return;
        }
        //Delete old receptor entrys
        updateQuerys_.add("DELETE FROM receptor WHERE disease_id = " + de.id + ";");
        //boolean first = true;
        java.util.Iterator<String> it = receptor.keySet().iterator();
        ReceptorEntry entry;
        String type;
        while (it.hasNext()) {
            type = it.next();
            entry = receptor.get(type);

            if (entry == null) {
                continue;
            }

            String value = entry.getValue();
            String receptor_text = entry.getText();
            String receptor_comment = entry.getComment();
            boolean value_true = entry.getIsvalid();


            //Insert or update receptor entrys
            String update = "SELECT receptorui( ";
            update += de.id + ", '" + type + "', " + value + ", '" + receptor_text + "', '" + receptor_comment + "', " + value_true;
            update += ")";

            updateQuerys_.add(update);

        }
        if (updateQuerys_.size() > queryCache_) {
            updateDB();
        }
    }
}
