/***********************************************
 * ICDParser.java
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
 * It contains only the parsing logic of 1 DictionaryNode
 ***********************************************
 */
package SAAT.textmining.icd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import SAAT.textmining.DiseaseEntry;

/**
 * The actial ICD Parser
 * It contains only the parsing logic of 1 DictionaryNode
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class ICDParser {

    /**
     * Variable decleration
     */
    private SAAT.textmining.gui.LogViewer log_;
    private DiseaseEntry de_;
    private int word_index_;
    private DictionaryData data_;
    private HashSet<Integer> hitted_word_indices_ = null;

    /** 
     * Creates a new instance of ICDParser
     *
     * @param log The logign class
     * @param dictData The dictionary data Element
     */
    public ICDParser(SAAT.textmining.gui.LogViewer log, DictionaryData dictData) {
        log_ = log;
        data_ = dictData;
    }

    /**
     * Methode to set up the Parser
     *
     * @param de a DiseaseEntry
     * @param wordIndex the index of the previous matched word
     */
    public void setUp(DiseaseEntry de, int wordIndex) {
        de_ = de;
        word_index_ = wordIndex;
        hitted_word_indices_ = new HashSet<Integer>();
    }

    /**
     * Return the hitted index in the text
     *
     * @return The hitted index in the text
     */
    public Integer[] getHittedWordIndices() {
        return hitted_word_indices_.toArray(new Integer[0]);
    }

    /*
     * The actual parse command
     *
     * @return if an hit occured
     */
    public boolean parse() {
        int id = de_.id;
        try {
            ArrayList<Integer> hittedWordIndices = new ArrayList<Integer>();

            if (data_ == null) {
                System.out.print("asd");
            }
            if (de_ == null) {
                System.out.print("asd");
            }
            if (de_.textCleaned == null) {
                System.out.print("asd");
            }
            if (data_.pattern_compile_ == null) {
                System.out.print("asd");
            }
            //find all occurances of this pattern_
            Matcher m = data_.pattern_compile_.matcher(de_.textCleaned);

            if (m.find()) { // check if at least 1 occurance is in string
                for (int i = 0; i < de_.text.length; ++i) {
                    m = data_.pattern_compile_.matcher(de_.text[i]);
                    if (m.find()) {
                        hittedWordIndices.add(i);
                    }
                }
            }


            //rule negation_ - part 1
            //word is not even in the diagnose
            //wordIndices of the previous (not the negative matched)
            if (data_.negation_ && hittedWordIndices.size() == 0) {
                hittedWordIndices.clear();
                hittedWordIndices.add(word_index_);
                hitted_word_indices_.addAll(hittedWordIndices);
                return true;
            }

            //nothing matched
            if (hittedWordIndices.size() == 0) {
                return false;
            }

            //everything matched (rootNode entry) !!should not be hitted!!
            if (hittedWordIndices.size() == de_.text.length) {
                return true;
            }

            int wordindex_negation_set = -1;

            ArrayList<Integer> hittedWordIndices2 = (ArrayList<Integer>) hittedWordIndices.clone();
            for (Integer hit : hittedWordIndices2) {
                boolean isHit = false;
                boolean isHitNegative = false;

                //lowest priority_ first
                if (data_.dictionary_id_ == 3959) {
                    System.out.println("Debug - dictionary_id: " + data_.dictionary_id_);
                }

                //rule before synonym_
                if (!data_.occur_ && !data_.sentence_) {
                    if (data_.before_synonym_ > 0) {
                        if (hit <= word_index_) { // hit is before the wordindex
                            if (((word_index_ - hit) <= data_.before_synonym_)
                                    && ((word_index_ - hit) >= 0)) {
                                isHit = true; // hit for the worde
                            }              //else // CHANGED
                            // hit is to far away
                        } //else
                        // hit is after the wordindex
                    }

                    //rule after synonym_
                    if (data_.after_synonym_ > 0) {
                        if (hit >= word_index_) { // hit is after the wordindex
                            if (((hit - word_index_) <= data_.after_synonym_)
                                    && ((hit - word_index_) >= 0)) {
                                isHit = true; // hit for the worde
                            }              //else // CHANGED
                            // hit is to far away
                        } //else
                        // hit is before the wordindex
                    }
                }

                //rule forword
                if (data_.foreword_) {
                    if (hit == word_index_) {
                        isHit = true;
                    }
                }

                //rule ending_
                if (data_.ending_) {
                    if (hit == word_index_) {
                        isHit = true;
                    }
                }

                //hit = current hit
                //wordindex = last hit
                //rule sentence_
                if (!data_.occur_ && data_.sentence_ && !isHit) {
                    //declaration
                    isHit = true;
                    //go from wordindex to hitindex and look if a . is in between
                    if (hit < word_index_) {
                        for (int i = hit; i < word_index_ && i < de_.text.length; ++i) {
                            if (de_.text[i].equals(".")) {
                                //Gegenbeweis
                                isHit = false;
                                break;
                            }
                        }
                    } else {
                        for (int i = hit; i > word_index_ && i >= 0; --i) {
                            if (de_.text[i].equals(".")) {
                                //rebuttal
                                isHit = false;
                                break;
                            }
                        }
                    }
                }

                //rule occur_
                if (data_.occur_) {
                    isHit = true;
                }

                if (!isHit && !data_.negation_) // no hit for this rule and not a negation_
                {
                    hittedWordIndices.remove(hit);
                } else if (isHit && data_.negation_) {
                    hittedWordIndices.clear();
                }
            }

            hitted_word_indices_.addAll(hittedWordIndices);

            //rule negation_ - part 2
            //negated word is in the diagnose - full stop
            if (data_.negation_ && hittedWordIndices.size() > 0) {
                hittedWordIndices.clear();
                hitted_word_indices_.addAll(hittedWordIndices);
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
            System.err.println("p1: > id = " + Integer.toString(id) + "< - textlength= " + Integer.toString(de_.text.length) + " - > de.id = " + Integer.toString(de_.id));
            System.err.println("ArrayIndexOutOfBoundsException in ICDParser::parse: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Exception in ICDParser::parse: " + ex.getMessage());
            ex.printStackTrace();
        }

        if (hitted_word_indices_.size() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Checks it the code is a suspicious one (General Negation Rule)
     *
     * return => false => code is NOT suspicious
     * return => true  => code IS     suspicious
     */
    public boolean isSuspiciousCode() {

        try {
            if (data_.general_negation_rules_ == null) {
                System.err.println("generalNegationRules error: " + Integer.toString(data_.dictionary_id_));
                return false;
            }

            if (!data_.general_negation_rules_.isInPlace()) {
                return false;
            }

            Integer[] matchedWordIndices = de_.getMatchedIndices();

            for (int h : matchedWordIndices) {
                if (h < 2) {
                    continue;
                }

                String t = de_.text[h - 1];

                Matcher m = data_.general_negation_rules_.getMatcher(t);
                if (m.find()) { 
                    return true;
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("ArrayIndexOutOfBoundsException in ICDParser::isSuspiciousCode: " + ex.getMessage() + " of " + Integer.toString(de_.text.length) + " with id " + Integer.toString(de_.id));
        } catch (Exception ex) {
            System.err.println("Exception in ICDParser::isSuspiciousCode: " + ex.getMessage());
            ex.printStackTrace();
        }

        return false;
    }
}
