/***********************************************
 * DiseaseEntry.java
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
 * Dr. Christina Schröder
 * Christina.Schroeder(at)ibmt.fraunhofer.de
 * http://www.crip.fraunhofer.de/en/about/staff?noCache=776:1304399536
 ***********************************************
 * DESCRIPTION
 *
 * Is the class for storing the disease data. 
 * Prepares the text for the coding, stores the staging information for saving update 
 * time. Stores the word hitting indexes.
 ***********************************************
 */
package SAAT.textmining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DiseaseEntry
 * Is the class for storing the disease data. 
 * Prepares the text for the coding, stores the staging information for saving update 
 * time. Stores the word hitting indexes.
 * 
 * @author  Reihs Robert <robert.reihs @ medunigraz.at>
 * @author  Sauer Stefan <stefan.sauer @ medunigraz.at>
 * @version 2.0
 * @since   since_version_2_date
 */
public class DiseaseEntry {

    public int id;
    public String textCleaned = null;
    public String textOriginal;
    public String[] text;
    public HashSet<Integer> oldDictionaryIDs;
    private HashMap<String, String> oldStaging_ = null;
    private HashSet<Integer> matchedWordIndices_ = null;
    // For the new rulemarker
    private String icd10_codes_ = "";
    private String icdo_codes_ = "";
    public String type_ = "Finding ID";

    /**
     * Creates a new instance of DiseaseEntry
     */
    public DiseaseEntry() {
        oldDictionaryIDs = new HashSet<Integer>();
        matchedWordIndices_ = new HashSet<Integer>();
        oldStaging_ = new HashMap<String, String>();
    }

    /**
     * Adds stored staging information to the entry
     *
     * Adds the stored staging information from the database to the entry.
     *
     * @param type the staging type to store (tnm...)
     * @param value the value of the staging
     */
    public void addOldStaging(String type, String value) {
        if (value == null) {
            return;
        }
        if (value.equals("")) {
            return;
        }

        if (oldStaging_.containsKey(type)) {
            oldStaging_.remove(type);
        }

        oldStaging_.put(type, value);
    }

    /**
     * Compares the stored staging information with the new
     *
     * Compares the stored staging information from the database stored with the function
     * {@link #addOldStaging(String type, String value)}. It returns true if the staging
     * information is in the storage and have the same value, if the value is different or
     * the staging is not set it returns false.
     *
     * @param type the staging type to store (tnm...)
     * @param value the value of the staging
     * @return true if the staging is the same otherwise false
     */
    public boolean isSameStaging(String type, String value) {
        if (!oldStaging_.containsKey(type)) {
            return false;
        }
        if (oldStaging_.get(type).equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * Stores the hit indexes from the text mining system.
     *
     * @param index the index number hited in the text
     */
    public void addMatchedIndex(int index) {
        matchedWordIndices_.add(index);
    }

    /**
     * Removes index from the stored indexes in the object.
     *
     * @param index the index of hitted words
     */
    public void removeMatchedIndex(int index) {
        matchedWordIndices_.remove(index);
    }

    /**
     * Get the matching indices
     *
     * @return The matched indices
     */
    public String getMatchedIndicesString() {
        String temp = "0";
        java.util.Iterator it = matchedWordIndices_.iterator();
        while (it.hasNext()) {
            Integer i = (Integer) it.next();
            temp += Integer.toString(i);
        }
        return temp;
    }

    /**
     * Returns an array of all matched indexes.
     *
     * @return Integer[] an array of all matched indexes
     */
    public Integer[] getMatchedIndices() {
        return matchedWordIndices_.toArray(new Integer[matchedWordIndices_.size()]);
    }

    /**
     * Prepares the text for the coding in text mining tool.
     *
     * Prepares the text stored in the object for the processing in the text mining tool
     * for the coding for ICD codes text codes. The function removes all multiple with
     * spaces, removes all )( ][. Connect special words for coding issues. Replace all with
     * spaces with @ as coding character.
     *
     *
     */
    public void preparTextForICD() {
        String temp = textOriginal.replace("\r", " ").replace("\n", " ").toUpperCase();

        ArrayList<String> words = new ArrayList<String>();
        temp = temp.replace("-----", ".");

        temp = temp.replace("(", " ").replace(")", " ");
        temp = temp.replace("[", " ").replace("]", " ");

        // change "  " to " " (>1 space to 1 space)
        temp = temp.replaceAll("\\s{2,}", " ");

        //merge some words....
        temp = temp.replaceAll("HANDELT\\sES\\sSICH\\sNICHT", "HANDELT_ES_SICH_NICHT"); //dict_id: 2730;
        temp = temp.replaceAll("LIEGT\\sNICHT\\sVOR", "LIEGT_NICHT_VOR"); //dict_id: 3635;4004;3981;4005;4195;3974;
        temp = temp.replaceAll("OHNE\\sMORPHOLOGISCHEN\\sHINWEIS", "OHNE_MORPHOLOGISCHEN_HINWEIS"); //dict_id: 3965;3969;
        temp = temp.replaceAll("METASTASEN?\\sDES\\sBEKANNT", "METASTASEN_DES_BEKANNTEN"); //dict_id: 4018;
        temp = temp.replaceAll("BESTEHT\\sJEDOCH\\sNICHT", "BESTEHT_JEDOCH_NICHT"); //dict_id: 3991;
        temp = temp.replaceAll("IN\\sERSTER\\sLINIE", "IN_ERSTER_LINIE"); //dict_id: 4111;4122;4133;
        temp = temp.replaceAll("ERGEBEN\\sSICH\\sNICHT", "ERGEBEN_SICH_NICHT"); //dict_id: 3961;
        temp = temp.replaceAll("KEIN\\sREST\\sDES", "KEIN_REST_DES"); //dict_id: 3652;

        temp = temp.replaceAll("ENTSPRICHT\\sNICHT", "ENTSPRICHT_NICHT"); //dict_id:
        temp = temp.replaceAll("NICHT\\sSICHER", "NICHT_SICHER"); //dict_id:
        temp = temp.replaceAll("SPRECHEN\\sGEGEN", "SPRECHEN_GEGEN"); //dict_id:
        temp = temp.replaceAll("DENKEN\\sLASSEN", "DENKEN_LASSEN"); //dict_id: 4065;
        temp = temp.replaceAll("METASTASEN?\\sEINES?", "METASTASE_EINES"); //dict_id: 4065;4016;
        temp = temp.replaceAll("METASTASEN?\\sDES", "METASTASE_DES"); //dict_id: 4065;4016;
        temp = temp.replaceAll("METASTASIERUNG\\sDURCH", "METASTASIERUNG_DURCH"); //dict_id: 4017;
        temp = temp.replaceAll("NICHT\\sNACHWEISBAR", "NICHT_NACHWEISBAR"); //dict_id: 4123;3326;4112;3325;3989;3982;3324;3830;3992;4134;
        temp = temp.replaceAll("NICHT\\sVERIFIZIE?R", "NICHT_VERIFIZIERT"); //dict_id: 3642;
        temp = temp.replaceAll("NICHT\\sERFUELLT", "NICHT_ERFUELLT"); //dict_id: 3327;
        temp = temp.replaceAll("NICHT\\sTYPISCH", "NICHT_TYPISCH"); //dict_id: 3732;
        temp = temp.replaceAll("NICHT\\sERKENNBAR", "NICHT_ERKENNBAR"); //dict_id: 3970;3966;3979;
        temp = temp.replaceAll("KEINE\\sMALIGNITAET", "KEINE_MALIGNITAET"); //dict_id: 3681;3676;
        temp = temp.replaceAll("KEINE\\sZELLEN", "KEINE_ZELLEN"); //dict_id: 3646;
        temp = temp.replaceAll("KEINE\\sAUSLAEUFER", "KEINE_AUSLAEUFER"); //dict_id: 3647;
        temp = temp.replaceAll("KEINE\\sRESIDUELLE", "KEINE_RESIDUELLES"); //dict_id: 3645;
        temp = temp.replaceAll("KEINE\\sZEICHEN", "KEINE_ZEICHEN"); //dict_id: 3337;3336;
        temp = temp.replaceAll("KEIN\\sHINWEIS", "KEIN_HINWEIS"); //dict_id: 3322;3634;3637;3321;3636;4026;4110;3684;3644;4121;3323;4132;
        temp = temp.replaceAll("KEIN\\sANHALTSPUNKT", "KEIN_ANHALTSPUNKT"); //dict_id: 3690;
        temp = temp.replaceAll("KEIN\\sANHALT", "KEIN_ANHALT"); //dict_id: 3960;3980;3973;
        temp = temp.replaceAll("KEIN\\sANTEIL", "KEIN_ANTEIL"); //dict_id: 3643;
        temp = temp.replaceAll("OHNE\\sHINWEIS", "OHNE_HINWEIS"); //dict_id: 3964;
        temp = temp.replaceAll("OHNE\\sHINWEIS", "OHNE_HINWEIS"); //dict_id: 3968;
        temp = temp.replaceAll("BESTEHT\\sNICHT", "BESTEHT_NICHT"); //dict_id: 3990;
        temp = temp.replaceAll("WAERE\\sMOEGLICH", "WAERE_MOEGLICH"); //dict_id: 4014;
        temp = temp.replaceAll("IN\\sFRAGE", "IN_FRAGE"); //dict_id: 4015;
        temp = temp.replaceAll("BEREITS\\sBEKANNT", "BEREITS_BEKANNT"); //dict_id: 3590;
        temp = temp.replaceAll("EHER\\sFUER", "EHER_FUER"); //dict_id: 3967;3962;
        temp = temp.replaceAll("ZUM\\sABSCHLUSS", "ZUM_ABSCHLUSS"); //dict_id: 3648;3641;
        temp = temp.replaceAll("LIEG(T|EN)\\sNICHT", "LIEGT_NICHT"); //dict_id: 3689;4193;
        temp = temp.replaceAll("SPRECHEN\\sFUER", "SPRECHEN_FUER"); //dict_id: 4143;
        temp = temp.replaceAll("VEREINBAR\\sMIT", "VEREINBAR_MIT"); //dict_id: 4066;
        temp = temp.replaceAll("WENIG\\sWAHRSCHEINLICH", "WENIG_WAHRSCHEINLICH"); //dict_id: 4314;

        temp = temp.replaceAll("[.]", "@.@");

        text = temp.split("\\s|[@]"); // same as std tokenizer

        textCleaned = "";

        for (String s : text) {
            textCleaned += "@" + s;
        }

    }

    /**
     * Prepares the text for the staging extraction.
     *
     * Prepares the text stored in the object for the processing in the text mining tool
     * for the extracting of the  staging information. Replace problematic words with
     * special characters for better results in the staging extraction and removes all
     * multiple with spaces.
     */
    public void preparedTextForStaging() {
        // change "  " to " " (>1 space to 1 space)
        String Diagnose = textOriginal.trim().replaceAll("\\s{2,}", " ");

        Diagnose = Diagnose.toUpperCase();

        String pattern = "BIS";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("���");
        pattern = "MIT";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("���");
        pattern = "[0-9]{2,}";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("%%%");
        pattern = "(M|N)I[A-HJ-Z]{1,}";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("���");
        pattern = "(\\w{1,}RI\\w{2,}|GI\\w{2,})";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("���");
        pattern = "(LI)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("��");
        pattern = "(TA)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("��");
        pattern = "(VI)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("��");
        pattern = "(KNOTEN)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "(TISIERENDER)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "(AKTINISCH)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "(ET ADENO)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "(AKTIVITAET)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "TI[\\w]{3,}";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "KNOTEN";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "NO[\\w]{3,}";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "BEI ANNAHME VON M[0|1]";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        Diagnose = m.replaceAll("������");
        pattern = "\\(PALLIATIVER EINGRIFF/R-2\\)";
        p = Pattern.compile(pattern);
        m = p.matcher(Diagnose);
        textCleaned = m.replaceAll("������");
    }

    /**
     * Prepares the text for the rezeptor extraction.
     *
     * Prepares the text stored in the object for the processing in the text mining tool
     * for the extracting of the  receptor information. Remove all multiple with spaces.
     */
    public void preparedTextForRezeptor() {
        String Diagnose = textOriginal.trim().replaceAll("\\s{2,}", " ");
        Diagnose = Diagnose.toUpperCase();
        textCleaned = Diagnose;
    }

    /**
     * Get the position of the word in relation to the complete text.
     *
     * Where is the function called??
     *
     * @return int[] returns the integer array with [0] word starting [1] word ending
     */
    public int[] getWordBoundarysWithWordIndex(int wordindex) {
        if (text.length < wordindex) {
            return null;
        }

        String temp = "";

        for (int i = 0; i < text.length; ++i) {
            if (i == wordindex) {
                return new int[]{temp.length(), temp.length() + text[i].length() + 1};
            }
            temp += "@" + text[i];
        }
        return null;
    }

    public String getStaging(String t) {
        String return_value = oldStaging_.get(t);
        if(return_value == null) {
            return "";
        } else {
            return return_value;
        }      
    }
    
    public void setICD10(String codes) {
        icd10_codes_ = codes;
    }
    
    public void setICDO(String codes) {
        icdo_codes_ = codes;
    }
    
    public String getICDO() {
        return icdo_codes_;
    }
    
    public String getICD10() {
        return icd10_codes_;
    }
}
