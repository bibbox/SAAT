/***********************************************
 * PriorityCodeTuple.java
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
 * Collection of Codes
 ***********************************************
 */
package SAAT.textmining.icd;

import java.util.HashMap;

/**
 * Collection of Codes
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class PriorityCodeTuple {

    /**
     * Variable decleration
     */
    private int priority_;
    private HashMap<String, Integer> codes_;

    /**
     * Creates a new instance of PriorityCodeTuple
     */
    public PriorityCodeTuple() {
        priority_ = -1;
        codes_ = new HashMap<String, Integer>();
    }

    /**
     * Adds a code to the Structure
     * This will be done with the priority system in place
     * 
     * @param code the actual ICD code
     * @param priority of the added code
     * @param dictionary ID
     */
    public void addCode(String code, int priority, int dictionary_id) {
        if (priority_ > priority) {
            return;
        }

        if (priority_ < priority) {
            codes_.clear();
        }

        priority_ = priority;
        codes_.put(code, dictionary_id);
    }

    /**
     * Merge 2 PriorityCodeTuple with priority checking
     *
     * @param value The other code tuple
     */
    public void addCode(PriorityCodeTuple value) {
        int vp = value.getPriority();
        if (vp == priority_) {
            codes_.putAll(value.codes_);
        } else if (vp > priority_) {
            codes_.clear();
            priority_ = vp;
            codes_.putAll(value.codes_);
        }
    }

    /**
     * Merge 2 PriorityCodeTuple without priority checking
     *
     * @param value The other code tuple
     */
    public void merge(PriorityCodeTuple value) {
        if (priority_ < value.getPriority()) {
            priority_ = value.getPriority();
        }
        codes_.putAll(value.codes_);
    }

    /**
     * Returns an array of all codes
     *
     * @return An array of all codes
     */
    public String[] getCodeArray() {
        return codes_.keySet().toArray(new String[0]);
    }

    /**
     * Return an array of all dictionary ids from the codes
     *
     * @return An array of all dictionary ids from the codes
     */
    public Integer[] getDictionaryIDArray() {
        if (codes_ == null) {
            return null;
        }
        if (codes_.size() == 0) {
            return null;
        }
        return codes_.values().toArray(new Integer[0]);
    }

    /**
     * Retruns the priority of the tuple
     *
     * @return The priority of the tuple
     */
    public int getPriority() {
        return priority_;
    }

    /**
     * Prints out the code
     */
    public void print() {
        for (String code : getCodeArray()) {
            System.out.println(priority_ + " => " + code);
        }
    }
}
