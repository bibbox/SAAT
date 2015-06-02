/***********************************************
 * XMLData.java
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
 * XML Data element class
 ***********************************************
 */

package SAAT.textmining.munich;

/**
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class XMLData {

    /**
     * Variable decleration
     */
    public String element_;
    public String element_name_;
    public String element_type_;
    public String element_text_;

    /**
     * Generate a new Instance of the XMLData class
     */
    public XMLData() {
    }

    /**
     * Generate a new Instance of the XMLData class
     *
     * @param element The element
     */
    public XMLData(String element) {
        element_ = element;
    }

    /**
     * Set the element
     *
     * @param element The element
     */
    public void setElement(String element) {
        element_ = element;
    }

    /**
     * Set the elements name
     *
     * @param element_name The elements name
     */
    public void setElementName(String element_name) {
        element_name_ = element_name;
    }

    /**
     * Set the element type
     * @param element_type The element type
     */
    public void setElementType(String element_type) {
        element_type_ = element_type;
    }

    /**
     * Set the element text
     * @param element_text The element text
     */
    public void setElementText(String element_text) {
        element_text_ = element_text;
    } 

    /**
     * Get the element text
     *
     * @return The element text
     */
    public String getElementText() {
        return element_text_;
    }
}
