/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SAAT.server;

import SAAT.rulemarker.RuleMarkerEntryStorage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import SAAT.generic.ConnectDataBase;
import SAAT.textmining.DiseaseEntry;
import SAAT.textmining.gui.LogViewer;

/**
 *
 * @author Robert
 */
public class DiseaseStorageLinguistikParser {

    /**
     * Variable declaration
     */
    private LogViewer log_;
    private ConnectDataBase db_;
    private PreparedStatement insertwords_;
    private PreparedStatement insertfindingid_;
    private PreparedStatement insertlinks_;
    private BufferedWriter file_;

    /**
     * Creates a new instance of RuleMarkerEntryStorage
     *
     * @param Takes a LogViewer, to write it's log messages into it
     */
    public DiseaseStorageLinguistikParser() {
        //log_ = log;
        db_ = new ConnectDataBase();

        FileWriter fstream;
        try {
            fstream = new FileWriter("Qutputfile.txt");
            file_ = new BufferedWriter(fstream);
        } catch (IOException ex) {
            Logger.getLogger(DiseaseStorageLinguistikParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            db_.connectPostgresPropertie();
            insertwords_ = db_.getPreparedStatement("INSERT INTO linguistik_parser.word (word) VALUES (?);");
            insertfindingid_ = db_.getPreparedStatement("INSERT INTO linguistik_parser.word_finding_id (word_id, finding_id) VALUES ((SELECT id FROM linguistik_parser.word WHERE word=? LIMIT 1), ?);");
            insertlinks_ = db_.getPreparedStatement("SELECT linguistik_parser.addwordlink(?, ?, ?);");
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("RuleMarkerDiseaseStorage::RuleMarkerDiseaseStorage - PSQLException Error: " + exloc.getMessage());
            System.err.println("RuleMarkerDiseaseStorage::RuleMarkerDiseaseStorage - Exit Program, No connection to findings database available");
            System.exit(-1);
        }
    }

    public ArrayList<DiseaseEntry> loadFindings(int start_id, int end_id) {
        ArrayList<DiseaseEntry> return_value = new ArrayList<DiseaseEntry>();
        try {
            Statement stat = db_.getNewStatement();
            ResultSet result;
            result = stat.executeQuery("SELECT finding_id id,diagnosis_clean AS diagnosis_clean,t,n,m,r,g,l,v, geticd10codesfindings(finding_id) icd10, geticdocodesfindings(finding_id) icdo FROM findings WHERE "
                    + " finding_id BETWEEN " + start_id + " AND " + end_id + ";");
            while (result.next()) {
                DiseaseEntry entry = new DiseaseEntry();
                entry.id = result.getInt("id");

                entry.textOriginal = result.getString("diagnosis_clean");

                entry.addOldStaging("T", result.getString("T"));
                entry.addOldStaging("N", result.getString("N"));
                entry.addOldStaging("M", result.getString("M"));
                entry.addOldStaging("R", result.getString("R"));
                entry.addOldStaging("G", result.getString("G"));
                entry.addOldStaging("L", result.getString("L"));
                entry.addOldStaging("V", result.getString("V"));
                return_value.add(entry);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    void insertWord(String w) {
        try {
            insertwords_.clearParameters();
            insertwords_.setString(1, w);
            insertwords_.execute();
            insertwords_.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DiseaseStorageLinguistikParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void insertFindingId(HashSet<Integer> get, String w) {
        db_.setAutocommit(false);
        for (Integer i : get) {
            try {
                insertfindingid_.clearParameters();
                insertfindingid_.setString(1, w);
                insertfindingid_.setInt(2, i);
                insertfindingid_.execute();
                insertfindingid_.clearParameters();
            } catch (SQLException ex) {
                Logger.getLogger(DiseaseStorageLinguistikParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        db_.commit();
        db_.setAutocommit(true);
    }

    void loadAllWords(HashMap<String, Integer> wl_db_) {
        try {
            boolean empty = false;
            Statement stat = db_.getNewStatement();
            ResultSet result = stat.executeQuery("SELECT id, word FROM linguistik_parser.word;");
            while (result.next()) {
                wl_db_.put(result.getString("word"), result.getInt("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DiseaseStorageLinguistikParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void setAutocommit(boolean b) {
        db_.setAutocommit(b);     
    }

    void insertLink(int word, int wordlink, int neighbor) {
        try {
            insertlinks_.clearParameters();
            insertlinks_.setInt(1, word);
            insertlinks_.setInt(2, wordlink);
            insertlinks_.setInt(3, neighbor);
            insertlinks_.execute();
            insertlinks_.clearParameters();
        } catch (SQLException ex) {
            Logger.getLogger(DiseaseStorageLinguistikParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void commit() {
        db_.commit();
    }
}
