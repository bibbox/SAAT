/***********************************************
 * GeneralNegationRule.java
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
 * Simple Datastrukture for the general Negation Rule
 ***********************************************
 */
package SAAT.textmining.icd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Datastrukture for the general Negation Rule
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class GeneralNegationRule {

    /**
     * Variable decleration
     */
    public boolean has_metastase_ = false;
    public boolean has_kein_ = false;
    private String pattern_ = null;

    /**
     * Given the synonym of a DictionaryNode, it will decide
     * if the GeneralNegationRule is in Place for this branch of the tree
     *
     * @param value The Synonym
     */
    public void setUp(String value) {
        if (value.equals("METASTASE")) {
            has_metastase_ = true;
        }

        if (value.equals("KEIN")) {
            has_kein_ = true;
        }
    }

    /**
     * Methode to look if thes rule needs to be checked
     *
     * @return look if thes rule needs to be checked
     */
    public boolean isInPlace() {
        if (has_metastase_ && has_kein_) {
            return false;
        }
        return true;
    }

    /**
     * Gets the Corresponding RegEx Matcher for a String
     *
     * @param value to be matched
     */
    public Matcher getMatcher(String value) {
        if (pattern_ == null) {
            if (!has_metastase_) {
                pattern_ += "(METASTASE)";
            }
            if (!has_metastase_) {
                if (pattern_.endsWith(")")) {
                    pattern_ += "|";
                }
            }
            pattern_ += "(KEIN)";
        }
        return Pattern.compile(pattern_).matcher(value);
    }
}
