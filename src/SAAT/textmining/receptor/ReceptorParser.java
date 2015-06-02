/***********************************************
 * ReceptorParser.java
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
 * Receptor Parser for parsing the selected receptors and returning the
 * results.
 ***********************************************
 */
package SAAT.textmining.receptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import SAAT.textmining.DiseaseEntry;
import SAAT.textmining.gui.LogViewer;

/**
 * Receptor Parser for parsing the selected receptors and returning the
 * results.
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class ReceptorParser {

    /**
     * Variable decleration
     */
    private boolean disableReceptor_ = false;
    ArrayList<ParserReceptor> parser_ = null;
    private LogViewer log_ = null;
    private HashSet<String> cmd_args_ = null;
    private DiseaseEntry de_ = null;
    private HashMap<String, ReceptorEntry> parsedResult_ = null;

    /** 
     * Creates a new instance of RezeptorParser and generate the
     * needed rezeptor parsers.
     *
     * @param cmd_args The arguments of the comandline witch rezeptor to parse
     * @param log The logger
     */
    public ReceptorParser(HashSet<String> cmd_args, LogViewer log) {
        cmd_args_ = cmd_args;
        log_ = log;

        parser_ = new ArrayList<ParserReceptor>();
        if (cmd_args_.contains("-Progesteron") || cmd_args_.contains("-Prog")) {
            parser_.add(new ParserReceptorProgesteron());
        }
        if (cmd_args_.contains("-Oestrogen") || cmd_args_.contains("-Oest")) {
            parser_.add(new ParserReceptorOestrogen());
        }
        if (cmd_args_.contains("-Her2")) {
            parser_.add(new ParserReceptorHer2());
        }
        if (parser_.size() == 0) {
            disableReceptor_ = true;
        }

    }

    /**
     * Returns if the receptor system is disabeld
     *
     * @return If the receptor system is disabeld
     */
    public boolean isDisabled() {
        return disableReceptor_;
    }

    /**
     * Set up the class for extracting the receptor information
     *
     * @param de The DiseaseEntry to pars the receptor
     */
    public void setUp(DiseaseEntry de) {
        de_ = de;
        parsedResult_ = new HashMap<String, ReceptorEntry>();
    }

    /**
     * Parse the receptor Infromation
     *
     * The Function start the parsing of all the receptors requested
     * in for the run, and stores the information in the return
     * ReceptorEntry object list.
     */
    public void parse() {
        if (disableReceptor_) {
            return;
        }
        if (de_ == null) {
            return;
        }
        if (de_.equals("")) {
            return;
        }

        for (ParserReceptor s : parser_) {
            String key = s.getType();
            ReceptorEntry value = s.code(de_.textCleaned);

            if (value != null) {
                if (value.isNull()) {
                    value = null;
                }
            }

            parsedResult_.put(key, value);
        }

    }

    /**
     * Returns the generated ReceptorEntry List
     *
     * @return The generated ReceptorEntrys
     */
    public HashMap<String, ReceptorEntry> getReceptorForDiseaseID() {
        return parsedResult_;
    }
}
