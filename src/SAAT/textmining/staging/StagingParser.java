/***********************************************
 * StagingParser.java
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
 * Staging Parser for parsing the selected Statings and returning the
 * results.
 ***********************************************
 */
package SAAT.textmining.staging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import SAAT.textmining.DiseaseEntry;
import SAAT.textmining.gui.LogViewer;

/**
 * Staging Parser for parsing the selected Statings and returning the
 * results.
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class StagingParser {

    /**
     * Variable decleration
     */
    private boolean disableStaging_ = false;
    ArrayList<ParserStaging> parser_ = null;
    private LogViewer log_ = null;
    private HashSet<String> cmd_args_ = null;
    private DiseaseEntry de_ = null;
    private HashMap<String, String> parsedResult_ = null;

    /**
     * Creates a new instance of RezeptorParser and generate the
     * needed Staging parsers.
     *
     * @param cmd_args The arguments of the comandline witch staging to parse
     * @param log The logger
     */
    public StagingParser(HashSet<String> cmd_args, LogViewer log) {
        cmd_args_ = cmd_args;
        log_ = log;

        parser_ = new ArrayList<ParserStaging>();
        if (cmd_args_.contains("-T")) {
            parser_.add(new ParserStagingT());
        }
        if (cmd_args_.contains("-N")) {
            parser_.add(new ParserStagingN());
        }
        if (cmd_args_.contains("-M")) {
            parser_.add(new ParserStagingM());
        }
        if (cmd_args_.contains("-R")) {
            parser_.add(new ParserStagingR());
        }
        if (cmd_args_.contains("-G")) {
            parser_.add(new ParserStagingG());
        }
        if (cmd_args_.contains("-L")) {
            parser_.add(new ParserStagingL());
        }
        if (cmd_args_.contains("-V")) {
            parser_.add(new ParserStagingV());
        }

        if (parser_.size() == 0) {
            disableStaging_ = true;
        }

    }

    /**
     * Returns if the staging system is disabeld
     *
     * @return If the staging system is disabeld
     */
    public boolean isDisabled() {
        return disableStaging_;
    }

    /**
     * Set up the class for extracting the staging information
     *
     * @param de The DiseaseEntry to pars the stagings
     */
    public void setUp(DiseaseEntry de) {
        de_ = de;
        parsedResult_ = new HashMap<String, String>();
    }

    /**
     * Parse the Staging Infromation
     *
     * The Function start the parsing of all the stagings requested
     * in for the run, and stores the information in the return
     * Hash list.
     */
    public void parse() {
        if (disableStaging_) {
            return;
        }
        if (de_ == null) {
            return;
        }
        if (de_.equals("")) {
            return;
        }

        for (ParserStaging s : parser_) {
            String key = s.getType();
            String value = s.code(de_.textCleaned);

            if (value != null) {
                if (value.equals("")) {
                    value = null;
                }
            }

            parsedResult_.put(key, value);
        }

    }

    /**
     * Returns the generated Staging List
     *
     * @return The generated Stagings
     */
    public HashMap<String, String> getStagingForDiseaseID() {
        return parsedResult_;
    }
}
