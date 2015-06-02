/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SAAT.rulemarker;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Robert
 */
public class EvaluationEntry {
    public int id;
    public String table_;
    private HashMap<String, String> oldStaging_ = null;
    // For the new rulemarker
    private String icd10_codes_ = "";
    private String icdo_codes_ = "";
    private String command_ = "";
    
    public EvaluationEntry() {
        oldStaging_ = new HashMap<String, String>();
    }
    
    /**
     * Adds stored staging information to the entry
     *
     * Adds the stored staging information from the database to the entry.
     *
     * @param type the staging type to store (tnm...)
     * @param value the value of the staging
     */
    public void addOldStaging(String type, String value) {
        if (value == null) {
            return;
        }
        if (value.equals("")) {
            return;
        }

        if (oldStaging_.containsKey(type)) {
            oldStaging_.remove(type);
        }

        oldStaging_.put(type, value);
    }
    
    public String getStaging(String t) {
        String return_value = oldStaging_.get(t);
        if(return_value == null) {
            return "";
        } else {
            return return_value;
        }      
    }
    
    public void setICD10(String codes) {
        icd10_codes_ = codes;
    }
    
    public void setICDO(String codes) {
        icdo_codes_ = codes;
    }
    
    public String getICDO() {
        return icdo_codes_;
    }
    
    public String getICD10() {
        return icd10_codes_;
    }
    
    public void setCommand(String command) {
        command_ = command;
    }
    
    public String getCommand() {
        return command_;
    }
}
