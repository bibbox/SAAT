/***********************************************
 * ParserStagingG.java
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
 * Class to extract the G Staging information from the diagnosis
 ***********************************************
 */
package SAAT.textmining.staging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to extract the G Staging information from the diagnosis
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class ParserStagingG implements ParserStaging {

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
     * Creates a new instance of ParserStagingG and generate the
     * regular expression pattern
     */
    public ParserStagingG() {
        String pattern = "[ (,\\|\\n\\r\\t\\f][A-Z]?(PG|G)[ -:(]?[ -:(]?([0-4]|X|III|II|I)[ ]?([SACBMD]?[M]?)[ -]?([IV]?[IV]?[IV]?[IV]?)[ ]?[(]?([0-4]|M)?[)]?(UND|ODER|AND|OR|BIS|���)?([ (]?(PG|G)?[ -:(]?[ -:(]?([0-4]|X|III|II|I)([SACBMD]?[M]?)([IV]?[IV]?[IV]?[IV]?)[(]?([0-4])?[(]?)?";
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
        return rr_CodingG();
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
        return rr_CodingG();
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
        return new String("G");
    }

    /**
     * Generates the Staging information with regular expression an
     * a weighting system to get the correct result.
     *
     * @return The staging information
     */
    public String rr_CodingG() {
        if (diagnose_ == null) {
            return null;
        }
        if (diagnose_.equals("")) {
            return null;
        }

        extracted_ = null;

        try {
            String G_extracted = "";
            String G_extracted_sec = "";
            float erster_wert = -100;
            float zweiter_wert = -100;
            float return_wert = -100;
            Matcher G_match = pattern_.matcher(diagnose_);
            while (G_match.find()) {

                // groupe 2. ([0-4]|X|II|I)
                // if groupe 2. = I than if groupe 3. is S
                // for IS or roman 1
                if (G_match.group(2) != null) {
                    if (G_match.group(2).equalsIgnoreCase("I")) {
                        if (G_match.group(3) != null) {
                            if (G_match.group(3).equalsIgnoreCase("V")) {
                                G_extracted = "4";
                                // Weighting system for comparing
                                erster_wert = 4.0f;
                            } else {
                                G_extracted = "1";
                                erster_wert = 1.0f;
                            }
                        }
                    }
                    // groupe 2. roman 2
                    if (G_match.group(2).equalsIgnoreCase("II")) {
                        G_extracted = "2";
                        erster_wert = 2.0f;
                    }
                    // 2. groupe roman 3
                    if (G_match.group(2).equalsIgnoreCase("III")) {
                        G_extracted = "3";
                        erster_wert = 3.0f;
                    }
                    // 2. groupe X
                    if (G_match.group(2).equalsIgnoreCase("X")) {
                        G_extracted = "X";
                        erster_wert = -1.0f;
                    }
                    // 2. groupe is a number
                    if (G_match.group(2).matches("\\d")) {
                        G_extracted = G_match.group(2);
                        erster_wert = (float) Integer.valueOf(G_match.group(2)).intValue();
                    }
                }

                // if groupe 5 is a number
                if (G_match.group(5) != null) {
                    if (G_match.group(5).matches("[0-4]")) {
                        if (erster_wert < (float) Integer.valueOf(
                                G_match.group(5)).intValue()) {
                            G_extracted = G_match.group(5);
                            erster_wert = (float) Integer.valueOf(
                                    G_match.group(5)).intValue();
                        }
                    }
                }

                // if a copula in groupe 6 found
                // (UND|ODER|AND|OR|BIS|���)
                // continue searche
                if (G_match.group(6) != null) {
                    if (G_match.group(6).matches("(UND|ODER|AND|OR|BIS|���)")) {
                        // groupe 9. ([0-4]|X|III|II|I)
                        // if groupe 9. = I than if groupe 10. is S
                        // for IS or roman 1
                        if (G_match.group(9) != null) {
                            if (G_match.group(9).equalsIgnoreCase("I")) {
                                if (G_match.group(10) != null) {
                                    if (G_match.group(10).equalsIgnoreCase("V")) {
                                        G_extracted_sec = "4";
                                        // Weighting system for comparing
                                        zweiter_wert = 4.0f;
                                    } else {
                                        G_extracted_sec = "1";
                                        zweiter_wert = 1.0f;
                                    }
                                }
                            }
                            // 9. groupe roman 2
                            if (G_match.group(9).equalsIgnoreCase("II")) {
                                G_extracted_sec = "2";
                                zweiter_wert = 2.0f;
                            }
                            // 9. groupe roman 3
                            if (G_match.group(9).equalsIgnoreCase("III")) {
                                G_extracted_sec = "3";
                                zweiter_wert = 3.0f;
                            }
                            // 9. groupe X
                            if (G_match.group(9).equalsIgnoreCase("X")) {
                                G_extracted_sec = "X";
                                zweiter_wert = -1.0f;
                            }
                            // 9. groupe is a number
                            if (G_match.group(9).matches("\\d")) {
                                G_extracted_sec = G_match.group(9);
                                zweiter_wert = (float) Integer.valueOf(
                                        G_match.group(9)).intValue();
                            }
                        }

                        // If groupe 5 is a number
                        if (G_match.group(12) != null) {
                            if (G_match.group(12).matches("[0-4]")) {
                                if (zweiter_wert < (float) Integer.valueOf(
                                        G_match.group(12)).intValue()) {
                                    G_extracted_sec = G_match.group(12);
                                    zweiter_wert = (float) Integer.valueOf(
                                            G_match.group(12)).intValue();
                                }
                            }
                        }
                    }
                }
                // generate the return value if no value is set
                if (return_wert == -100.f) {
                    if (erster_wert > -50.0f) {
                        if (zweiter_wert > -50.0f) {
                            if (erster_wert > zweiter_wert) {
                                extracted_ = G_extracted;
                                return_wert = erster_wert;
                            } else {
                                extracted_ = G_extracted_sec;
                                return_wert = zweiter_wert;
                            }
                        } else {
                            extracted_ = G_extracted;
                            return_wert = erster_wert;
                        }
                    } else {
                        extracted_ = "";
                    }
                } else { // generate the return value if one is set
                    if (erster_wert > -50.0f) {
                        if (zweiter_wert > -50.0f) {
                            if (erster_wert > zweiter_wert) {
                                if (erster_wert > return_wert) {
                                    extracted_ = G_extracted;
                                    return_wert = erster_wert;
                                }
                            } else {
                                if (zweiter_wert > return_wert) {
                                    extracted_ = G_extracted_sec;
                                    return_wert = zweiter_wert;
                                }
                            }
                        } else {
                            if (erster_wert > return_wert) {
                                extracted_ = G_extracted;
                                return_wert = erster_wert;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR in CLASS rr_DiagnosticCoding in Function \"rr_CodingG()\": " + e);
        }
        return extracted_;
    }
}
