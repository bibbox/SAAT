/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SAAT.rulemarker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import SAAT.generic.ConnectDataBase;
import SAAT.textmining.DiseaseEntry;
import SAAT.textmining.gui.LogViewer;

/**
 *
 * @author Robert
 */
public class RuleMarkerEntryStorage {
    /**
     * Variable declaration
     */
    private LogViewer log_;
    private ConnectDataBase db_;
    
    /**
     * Creates a new instance of RuleMarkerEntryStorage
     *
     * @param Takes a LogViewer, to write it's log messages into it
     */
    public RuleMarkerEntryStorage() {
        //log_ = log;
        db_ = new ConnectDataBase();

        try {
            db_.connectPostgresPropertie();
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("RuleMarkerDiseaseStorage::RuleMarkerDiseaseStorage - PSQLException Error: " + exloc.getMessage());
            System.err.println("RuleMarkerDiseaseStorage::RuleMarkerDiseaseStorage - Exit Program, No connection to findings database available");
            System.exit(-1);
        }
    }
    
    public DiseaseEntry load(int id, String table) {
        try {
            Statement stat = db_.getNewStatement();
            ResultSet result;
            String query = "SELECT §id§ id, diagnosis_clean AS diagnosis_clean,t,n,m,r,g,l,v, §icd10§ icd10, §icdo§ icdo FROM §tabel§ WHERE diagnosis_clean IS NOT NULL AND §id§ = " + id + ";";
            if(table.equalsIgnoreCase("Finding ID")) {
                query = query.replaceAll("§id§", "finding_id");
                query = query.replaceAll("§tabel§", "findings");
                query = query.replaceAll("§icd10§", "geticd10codesfindings(finding_id)");
                query = query.replaceAll("§icdo§", "geticdocodesfindings(finding_id)");
            } else if(table.equalsIgnoreCase("Diseas ID")) {
                query = query.replaceAll("§id§", "disease_id");
                query = query.replaceAll("§tabel§", "diseases");
                query = query.replaceAll("§icd10§", "geticd10codes(disease_id)");
                query = query.replaceAll("§icdo§", "geticdocodes(disease_id)");
            }
            //System.out.println(query);
            result = stat.executeQuery(query);
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
                
                entry.setICD10(result.getString("icd10"));
                entry.setICDO(result.getString("icdo"));
                entry.type_ = table;
                
                return entry;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ArrayList<DiseaseEntry> load(String query) {
        ArrayList<DiseaseEntry> return_value = new ArrayList<DiseaseEntry>();
        try {           
            Statement stat = db_.getNewStatement();
            ResultSet result;
            result = stat.executeQuery(query);
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
                
                entry.setICD10(result.getString("icd10"));
                entry.setICDO(result.getString("icdo"));
                
                return_value.add(entry);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return return_value;
    }

    public EvaluationEntry loadEvaluationData(int id, String type_) {
        try {
            Statement stat = db_.getNewStatement();
            ResultSet result;
            String query = "SELECT §id§ id, t,n,m,r,g,l,v, icd10, icdo, command FROM §tabel§ WHERE §id§ = " + id + ";";
            if(type_.equalsIgnoreCase("Finding ID")) {
                query = query.replaceAll("§id§", "finding_id");
                query = query.replaceAll("§tabel§", "findings_evaluation");
            } else if(type_.equalsIgnoreCase("Diseas ID")) {
                query = query.replaceAll("§id§", "disease_id");
                query = query.replaceAll("§tabel§", "diseases_evaluation");
            }
            //System.out.println(query);
            result = stat.executeQuery(query);
            while (result.next()) {
                EvaluationEntry entry = new EvaluationEntry();
                entry.id = result.getInt("id");

                entry.addOldStaging("T", result.getString("T"));
                entry.addOldStaging("N", result.getString("N"));
                entry.addOldStaging("M", result.getString("M"));
                entry.addOldStaging("R", result.getString("R"));
                entry.addOldStaging("G", result.getString("G"));
                entry.addOldStaging("L", result.getString("L"));
                entry.addOldStaging("V", result.getString("V"));
                
                entry.setICD10(result.getString("icd10"));
                entry.setICDO(result.getString("icdo"));
                entry.setCommand(result.getString("command"));
                
                entry.table_ = type_;
                
                return entry;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void insertEvaluation(EvaluationEntry ee) {
        try {
            Statement stat = db_.getNewStatement();
            ResultSet result;
            String query = "INSERT INTO §tabel§ (§id§, t,n,m,r,g,l,v, icd10, icdo, command) VALUES " + 
                    "(§rid§, '§t§', '§n§', '§m§', '§r§', '§g§', '§l§', '§v§', '§icd10§', '§icdo§', '§command§');";
            if(ee.table_.equalsIgnoreCase("Finding ID")) {
                query = query.replaceAll("§id§", "finding_id");
                query = query.replaceAll("§tabel§", "findings_evaluation");
            } else if(ee.table_.equalsIgnoreCase("Diseas ID")) {
                query = query.replaceAll("§id§", "disease_id");
                query = query.replaceAll("§tabel§", "diseases_evaluation");
            }
            query = query.replaceAll("§rid§", String.valueOf(ee.id));
            query = query.replaceAll("§t§", ee.getStaging("T"));
            query = query.replaceAll("§n§", ee.getStaging("N"));
            query = query.replaceAll("§m§", ee.getStaging("M"));
            query = query.replaceAll("§l§", ee.getStaging("L"));
            query = query.replaceAll("§v§", ee.getStaging("V"));
            query = query.replaceAll("§r§", ee.getStaging("R"));
            query = query.replaceAll("§g§", ee.getStaging("G"));
            query = query.replaceAll("§icd10§", ee.getICD10());
            query = query.replaceAll("§icdo§", ee.getICDO());
            query = query.replaceAll("§command§", ee.getCommand());
            System.out.println(query);
            stat.execute(query);            
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateEvaluation(EvaluationEntry ee) {
        try {
            Statement stat = db_.getNewStatement();
            ResultSet result;
            String query = "UPDATE §tabel§ SET t='§t§',n='§n§',m='§m§',r='§r§',g='§g§',l='§l§',v='§v§', " + 
                    " icd10='§icd10§', icdo='§icdo§', command='§command§' WHERE §id§ = §rid§;";
            if(ee.table_.equalsIgnoreCase("Finding ID")) {
                query = query.replaceAll("§id§", "finding_id");
                query = query.replaceAll("§tabel§", "findings_evaluation");
            } else if(ee.table_.equalsIgnoreCase("Diseas ID")) {
                query = query.replaceAll("§id§", "disease_id");
                query = query.replaceAll("§tabel§", "diseases_evaluation");
            }
            query = query.replaceAll("§rid§", String.valueOf(ee.id));
            query = query.replaceAll("§t§", ee.getStaging("T"));
            query = query.replaceAll("§n§", ee.getStaging("N"));
            query = query.replaceAll("§m§", ee.getStaging("M"));
            query = query.replaceAll("§l§", ee.getStaging("L"));
            query = query.replaceAll("§v§", ee.getStaging("V"));
            query = query.replaceAll("§r§", ee.getStaging("R"));
            query = query.replaceAll("§g§", ee.getStaging("G"));
            query = query.replaceAll("§icd10§", ee.getICD10());
            query = query.replaceAll("§icdo§", ee.getICDO());
            query = query.replaceAll("§command§", ee.getCommand());
            //System.out.println(query);
            stat.executeUpdate(query);            
        } catch (SQLException ex) {
            Logger.getLogger(RuleMarkerEntryStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
