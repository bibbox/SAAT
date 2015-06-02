/***********************************************
 * ParserReceptor.java
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
 * Interface for the Receptor Parser Elements
 ***********************************************
 */
package SAAT.textmining.receptor;

/**
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public interface ParserReceptor {

    /**
     * Set up the text to parse
     *
     * @param text The text to parse
     */
    public void setUp(String text);

    /**
     * Returns the receptor object (runs the coding again)
     *
     * @return The receptor object
     */
    public ReceptorEntry code();

    /**
     * Function that generates an receptor object from the text
     *
     * Function that generates an receptor object from the text, running the coding
     * again.
     *
     * @param text The text to parse
     * @return The receptor object
     */
    public ReceptorEntry code(String text);

    /**
     * Returns the receptor object; not runing the coding again
     *
     * @return The receptor object
     */
    public ReceptorEntry getReceptor();

    /**
     * Returns The receptor type
     *
     * @return The receptor type
     */
    public String getType();
}
