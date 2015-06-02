/***********************************************
 * ParserReceptorHer2.java
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
 * Class to extract the HER2 Receptor information from the diagnosis
 ***********************************************
 */
package SAAT.textmining.receptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to extract the HER2 Receptor information from the diagnosis
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class ParserReceptorHer2 implements ParserReceptor {

    /**
     * Variable decleration
     */
    // The receptor object
    private ReceptorEntry extracted_;
    // The diagnosis to parse
    private String diagnose_;
    // The regular expression
    private Pattern pattern_ = null;

    /** 
     * Creates a new instance of ParserReceptorHer2
     */
    public ParserReceptorHer2() {
        String pattern = "HER[ |-]?2[ |-|/]?(?:NEU)?(.*)SCORE[: | ]?([0-3])";
        pattern_ = Pattern.compile(pattern);

        extracted_ = new ReceptorEntry();
    }

    /**
     * Set up the text to parse
     *
     * @param text The text to parse
     */
    public void setUp(String text) {
        diagnose_ = text;
    }

    /**
     * Returns the receptor object (runs the coding again)
     *
     * @return The receptor object
     */
    public ReceptorEntry code() {
        return CodingHer2();
    }

    /**
     * Function that generates an receptor object from the text
     *
     * Function that generates an receptor object from the text, running the coding
     * again.
     *
     * @param text The text to parse
     * @return The receptor object
     */
    public ReceptorEntry code(String text) {
        setUp(text);
        return CodingHer2();
    }

    /**
     * Returns the receptor object; not runing the coding again
     *
     * @return The receptor object
     */
    public ReceptorEntry getReceptor() {
        return extracted_;
    }

    /**
     * Returns The receptor type
     *
     * @return The receptor type
     */
    public String getType() {
        return new String("HER2");
    }

    /**
     * Extracts the Receptor
     *
     * Function that extracts with regular expressions the Her2
     * Receptor and returns an receptor object.
     *
     * @return An receptor object
     */
    private ReceptorEntry CodingHer2() {
        if (diagnose_ == null) {
            return null;
        }
        if (diagnose_.equals("")) {
            return null;
        }

        extracted_ = null;

        try {
            Matcher match = pattern_.matcher(diagnose_);
            while (match.find()) {
                String rezeptor_wert = "leer";
                String rezeptor_text = "";
                String rezeptor_bemerkung = "";
                boolean rezeptor_true = false;
                if (match.group(2) != null) {
                    rezeptor_wert = match.group(2);
                    if (rezeptor_wert.equalsIgnoreCase("0")) {
                        rezeptor_text = "KEINE UEBEREXPRESSION";
                        rezeptor_true = true;
                    } else {
                        if (rezeptor_wert.equalsIgnoreCase("1")) {
                            rezeptor_text = "KEINE UEBEREXPRESSION";
                            rezeptor_true = true;
                        } else {
                            if (rezeptor_wert.equalsIgnoreCase("2")) {
                                rezeptor_text = "INDIFFERENT - GERINGGRADIG UEBEREXPRESSION";
                                rezeptor_true = true;
                                rezeptor_bemerkung = "Fischer Test not implemented";
                                //String fischer = rr_CodingFischer();
                            } else {
                                if (rezeptor_wert.equalsIgnoreCase("3")) {
                                    rezeptor_text = "HOCHGRADIG UEBEREXPRESSION";
                                    rezeptor_true = true;
                                }
                            }
                        }
                    }
                }
                if (!rezeptor_wert.equalsIgnoreCase("leer")) {
                    extracted_.setValue(rezeptor_wert);
                    extracted_.setText(rezeptor_text);
                    extracted_.setComment(rezeptor_bemerkung);
                    extracted_.setIsvalid(rezeptor_true);
                    extracted_.setType(getType());
                }
            }
        } catch (Exception e) {
            System.err.println("TextMining::CodingOestrogenstatus: " + e.getMessage());
        }
        return extracted_;
    }
}
