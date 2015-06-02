/***********************************************
 * ParserStagingM.java
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
 * Class to extract the M Staging information from the diagnosis
 ***********************************************
 */
package SAAT.textmining.staging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to extract the M Staging information from the diagnosis
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class ParserStagingM implements ParserStaging {

    /**
     * Variable decleration
     */
    // The extracted diagnosis
    private String extracted_ = "";
    // The original diagnosis
    private String diagnose_ = "";
    // The Regular expression pattern
    private Pattern pattern_ = null;

    /** 
     * Creates a new instance of ParserStagingM* Creates a new instance of ParserStagingL and generate the
     * regular expression pattern
     */
    public ParserStagingM() {
        String pattern = "[ (,\\|\\n\\r\\t\\f][A-LN-Z]?(PM|M|pM)[ -:(]?[ -:(]?([0-1]|X|I)[ ]?([ACBM]?[M]?)[ ]?([IV]?[IV]?[IV]?[IV]?)[ ]?[(]?([0-4]|M)?[)]?(UND|ODER|AND|OR|BIS|���)?([ (]?(PM|M)?[ -:(]?[ -:(]?([0-1]|X|I)([ACBM]?[M]?)([IV]?[IV]?[IV]?[IV]?)[(]?([0-1])?[(]?)?";
        pattern_ = Pattern.compile(pattern);
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
     * Returns the generated code (runs the coding again)
     *
     * @return the generated code
     */
    public String code() {
        return rr_CodingM();
    }

    /**
     * Function that generates the code out of the text
     *
     * Function that generates the code out of the text, running the coding
     * again.
     *
     * @param text The text to parse
     * @return The generated code
     */
    public String code(String text) {
        setUp(text);
        return rr_CodingM();
    }

    /**
     * Returns the coded staging; not runing the coding again
     *
     * @return The coded staging
     */
    public String getStaging() {
        return extracted_;
    }

    /**
     * Returns The Staging type
     *
     * @return The Staging type
     */
    public String getType() {
        return new String("M");
    }

    /**
     * Generates the Staging information with regular expression an
     * a weighting system to get the correct result.
     *
     * @return The staging information
     */
    public String rr_CodingM() {
        if (diagnose_ == null) {
            return null;
        }
        if (diagnose_.equals("")) {
            return null;
        }

        extracted_ = null;

        try {
            String M_extracted = "";
            String M_extracted_sec = "";
            float erster_wert = -100;
            float zweiter_wert = -100;
            float returM_wert = -100;
            Matcher M_match = pattern_.matcher(diagnose_);
            while (M_match.find()) {

                // groupe 2. ([0-4]|X|II|I)
                // if groupe 2. = I than if groupe 3. is S
                // for IS or roman 1
                if (M_match.group(2) != null) {
                    if (M_match.group(2).equalsIgnoreCase("I")) {
                        if (M_match.group(3) != null) {
                            M_extracted = "1";
                            erster_wert = 1.0f;
                        }
                    }
                    // groupe 2. roman X
                    if (M_match.group(2).equalsIgnoreCase("X")) {
                        M_extracted = "X";
                        erster_wert = -1.0f;
                    }
                    // 2. groupe is a number
                    if (M_match.group(2).matches("\\d")) {
                        M_extracted = M_match.group(2);
                        erster_wert = (float) Integer.valueOf(M_match.group(2)).intValue();
                    }
                }
                // If a number is found search in the next feald
                if (M_extracted.matches("\\d")) {
                    if (M_match.group(3) != null) {
                        if (M_match.group(3).matches("[A|B|C]")) {
                            M_extracted = M_extracted + M_match.group(3);

                            // summate the weight for the letters
                            if (M_match.group(3).matches("A")) {
                                erster_wert += 0.2f;
                            }
                            if (M_match.group(3).matches("B")) {
                                erster_wert += 0.4f;
                            }
                            if (M_match.group(3).matches("C")) {
                                erster_wert += 0.6f;
                            }
                        }
                    }
                }

                // if groupe 5 is a number
                if (M_match.group(5) != null) {
                    if (M_match.group(5).matches("[0-1]")) {
                        if (erster_wert < (float) Integer.valueOf(
                                M_match.group(5)).intValue()) {
                            M_extracted = M_match.group(5);
                            erster_wert = (float) Integer.valueOf(
                                    M_match.group(5)).intValue();
                        }
                    }
                }

                // if a copula in groupe 6 found
                // (UND|ODER|AND|OR|BIS|���)
                // continue searche
                if (M_match.group(6) != null) {
                    if (M_match.group(6).matches("(UND|ODER|AND|OR|BIS|���)")) {
                        // groupe 9. ([0-4]|X|III|II|I)
                        // if groupe 9. = I than if groupe 10. is S
                        // for IS or roman 1
                        if (M_match.group(9) != null) {
                            if (M_match.group(9).equalsIgnoreCase("I")) {
                                if (M_match.group(10) != null) {
                                    M_extracted_sec = "1";
                                    zweiter_wert = 1.0f;
                                }
                            }
                            // 9. groupe X
                            if (M_match.group(9).equalsIgnoreCase("X")) {
                                M_extracted_sec = "X";
                                zweiter_wert = -1.0f;
                            }
                            // 9. groupe is a number
                            if (M_match.group(9).matches("\\d")) {
                                M_extracted_sec = M_match.group(9);
                                zweiter_wert = (float) Integer.valueOf(
                                        M_match.group(9)).intValue();
                            }
                        }
                        // If a number is found search in the next feald
                        if (M_extracted_sec.matches("\\d")) {
                            if (M_match.group(10) != null) {
                                if (M_match.group(10).matches("[A|B|C]")) {
                                    M_extracted_sec = M_extracted_sec
                                            + M_match.group(10);

                                    // summate the weight for the letters
                                    if (M_match.group(10).matches("A")) {
                                        zweiter_wert += 0.2f;
                                    }
                                    if (M_match.group(10).matches("B")) {
                                        zweiter_wert += 0.4f;
                                    }
                                    if (M_match.group(10).matches("C")) {
                                        zweiter_wert += 0.6f;
                                    }
                                }
                            }
                        }

                        // If groupe 5 is a number
                        if (M_match.group(12) != null) {
                            if (M_match.group(12).matches("[0-1]")) {
                                if (zweiter_wert < (float) Integer.valueOf(
                                        M_match.group(12)).intValue()) {
                                    M_extracted_sec = M_match.group(12);
                                    zweiter_wert = (float) Integer.valueOf(
                                            M_match.group(12)).intValue();
                                }
                            }
                        }
                    }
                }
                // generate the return value if no value is set
                if (returM_wert == -100.f) {
                    if (erster_wert > -50.0f) {
                        if (zweiter_wert > -50.0f) {
                            if (erster_wert > zweiter_wert) {
                                extracted_ = M_extracted;
                                returM_wert = erster_wert;
                            } else {
                                extracted_ = M_extracted_sec;
                                returM_wert = zweiter_wert;
                            }
                        } else {
                            extracted_ = M_extracted;
                            returM_wert = erster_wert;
                        }
                    } else {
                        extracted_ = "";
                    }
                } else { // generate the return value if one is set
                    if (erster_wert > -50.0f) {
                        if (zweiter_wert > -50.0f) {
                            if (erster_wert > zweiter_wert) {
                                if (erster_wert > returM_wert) {
                                    extracted_ = M_extracted;
                                    returM_wert = erster_wert;
                                }
                            } else {
                                if (zweiter_wert > returM_wert) {
                                    extracted_ = M_extracted_sec;
                                    returM_wert = zweiter_wert;
                                }
                            }
                        } else {
                            if (erster_wert > returM_wert) {
                                extracted_ = M_extracted;
                                returM_wert = erster_wert;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in CLASS rr_DiagnosticCoding in Function \"rr_CodingM()\": "
                    + e);
        }
        return extracted_;
    }
}
