/***********************************************
 * ReceptorEntry.java
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
 * A Datatype class for storing the receptor information
 ***********************************************
 */
package SAAT.textmining.receptor;

/**
 * A Datatype class for storing the receptor information
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class ReceptorEntry {

    /**
     * Variable decleration
     */
    // The receptor type
    private String type_;
    // The receptor value
    private String value_;
    // The receptor in textual form
    private String text_;
    // If the information (text and value) correspond
    private boolean isvalid_;
    // A Comment text
    private String comment_;

    /** 
     * Creates a new empty instance of ReceptorEntry
     */
    public ReceptorEntry() {
    }

    /**
     * Creates a new instance of ReceptorEntry
     *
     * Creates a new instance of ReceptorEntry taking all the information
     * for the receptor element.
     *
     * @param type The receptor type
     * @param value The receptor value
     * @param text The receptor in textual form
     * @param comment A Comment text
     * @param isvalid If the information (text and value) correspond
     */
    public ReceptorEntry(String type, String value, String text, String comment, boolean isvalid) {
        type_ = type;
        value_ = value;
        text_ = text;
        isvalid_ = isvalid;
        comment_ = comment;
    }

    /**
     * Returns If the value is set
     *
     * @return If the value is set
     */
    public boolean isNull() {
        if (value_ == null) {
            return false;
        }
        return (value_.equals("")) ? true : false;
    }

    /**
     * Returns the receptor type
     *
     * @return The receptor type
     */
    public String getType() {
        return type_;
    }

    /**
     * Set the receptor type
     *
     * @param type The receptor type
     */
    public void setType(String type) {
        type_ = type;
    }

    /**
     * Returns the receptor value
     *
     * @return The receptor value
     */
    public String getValue() {
        return value_;
    }

    /**
     * Sets the receptor value
     *
     * @param value The receptor value
     */
    public void setValue(String value) {
        value_ = value;
    }

    /**
     * Returns the receptor in textual form
     * 
     * @return The receptor in textual form
     */
    public String getText() {
        return text_;
    }

    /**
     * Sets the receptor in textual form
     *
     * @param text The receptor in textual form
     */
    public void setText(String text) {
        text_ = text;
    }

    /**
     * Returns the Comment text
     *
     * @return A Comment text
     */
    public String getComment() {
        return comment_;
    }

    /**
     * Sets the Comment text
     * @param comment The Comment text
     */
    public void setComment(String comment) {
        comment_ = comment;
    }

    /**
     * Returns if the information (text and value) correspond
     *
     * @return If the information (text and value) correspond
     */
    public boolean getIsvalid() {
        return isvalid_;
    }

    /**
     * Sets if the information (text and value) correspond
     * 
     * @param isvalid If the information (text and value) correspond
     */
    public void setIsvalid(boolean isvalid) {
        isvalid_ = isvalid;
    }
}
