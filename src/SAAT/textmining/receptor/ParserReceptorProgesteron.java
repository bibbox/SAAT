/***********************************************
 * ParserReceptorProgesteron.java
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
 * Class to extract the Oestrogen Receptor information from the diagnosis
 ***********************************************
 */
package SAAT.textmining.receptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to extract the Oestrogen Receptor information from the diagnosis
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class ParserReceptorProgesteron implements ParserReceptor {

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
     * Creates a new instance of ParserReceptorProgesteron
     */
    public ParserReceptorProgesteron() {
        String pattern = "PROGESTERONREZEPTOR(EN|ENNACHWEIS)?[ :-]?[ ]?(\\w+)?[ ]?([\\(]SCORE[ ]?(\\d+)\\))?";
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
        return CodingProgesteron();
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
        return CodingProgesteron();
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
        return new String("PROGESTERON");
    }

    /**
     * Extracts the Receptor
     *
     * Function that extracts with regular expressions the Ostroegen
     * Receptor and returns an receptor object.
     *
     * @return An receptor object
     */
    private ReceptorEntry CodingProgesteron() {
        if (diagnose_ == null) {
            return null;
        }
        if (diagnose_.equals("")) {
            return null;
        }


        try {
            Matcher match = pattern_.matcher(diagnose_);
            while (match.find()) {
                String rezeptor_wert = "leer";
                String rezeptor_text = "";
                String rezeptor_bemerkung = "";
                boolean rezeptor_true = false;
                if (match.group(3) == null) {
                    if (match.group(2).equalsIgnoreCase("NEGATIV")) {
                        rezeptor_wert = "0";
                        rezeptor_text = "NEGATIV";
                        rezeptor_true = false;
                    } else {
                        if (match.group(2).equalsIgnoreCase("GERINGGRADIG")) {
                            rezeptor_wert = "2";
                            rezeptor_text = "GERINGGRADIG";
                            rezeptor_true = false;
                        } else {
                            if (match.group(2).equalsIgnoreCase("MITTELGRADIG")) {
                                rezeptor_wert = "4";
                                rezeptor_text = "MITTELGRADIG";
                                rezeptor_true = false;
                            } else {
                                if (match.group(2).equalsIgnoreCase("HOCHGRADIG")) {
                                    rezeptor_wert = "9";
                                    rezeptor_text = "HOCHGRADIG";
                                    rezeptor_true = false;
                                }
                            }
                        }
                    }

                } else {
                    rezeptor_wert = match.group(4);
                    if (rezeptor_wert.equalsIgnoreCase("0")) {
                        rezeptor_text = "NEGATIV";
                        rezeptor_true = true;
                    } else {
                        if (rezeptor_wert.equalsIgnoreCase("1")) {
                            rezeptor_text = "GERINGGRADIG";
                            rezeptor_true = true;
                        } else {
                            if (rezeptor_wert.equalsIgnoreCase("2")) {
                                rezeptor_text = "GERINGGRADIG";
                                rezeptor_true = true;
                            } else {
                                if (rezeptor_wert.equalsIgnoreCase("3")) {
                                    rezeptor_text = "GERINGGRADIG";
                                    rezeptor_true = true;
                                } else {
                                    if (rezeptor_wert.equalsIgnoreCase("4")) {
                                        rezeptor_text = "MITTELGRADIG";
                                        rezeptor_true = true;
                                    } else {
                                        if (rezeptor_wert.equalsIgnoreCase("5")) {
                                            rezeptor_text = "MITTELGRADIG";
                                            rezeptor_true = false;
                                        } else {
                                            if (rezeptor_wert.equalsIgnoreCase("6")) {
                                                rezeptor_text = "MITTELGRADIG";
                                                rezeptor_true = true;
                                            } else {
                                                if (rezeptor_wert.equalsIgnoreCase("7")) {
                                                    rezeptor_text = "HOCHGRADIG";
                                                    rezeptor_true = false;
                                                } else {
                                                    if (rezeptor_wert.equalsIgnoreCase("8")) {
                                                        rezeptor_text = "HOCHGRADIG";
                                                        rezeptor_true = true;
                                                    } else {
                                                        if (rezeptor_wert.equalsIgnoreCase("9")) {
                                                            rezeptor_text = "HOCHGRADIG";
                                                            rezeptor_true = true;
                                                        } else {
                                                            if (rezeptor_wert.equalsIgnoreCase("10")) {
                                                                rezeptor_text = "HOCHGRADIG";
                                                                rezeptor_true = false;
                                                            } else {
                                                                if (rezeptor_wert.equalsIgnoreCase("11")) {
                                                                    rezeptor_text = "HOCHGRADIG";
                                                                    rezeptor_true = false;
                                                                } else {
                                                                    if (rezeptor_wert.equalsIgnoreCase("12")) {
                                                                        rezeptor_text = "HOCHGRADIG";
                                                                        rezeptor_true = true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
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
                // System.out.println(extracted);
            }
        } catch (Exception e) {
            System.err.println("rr_TextMining::rr_CodingOestrogenstatus: " + e.getMessage());
            //System.err.println(diagnosis_original);

        }
        return extracted_;

    }
}
