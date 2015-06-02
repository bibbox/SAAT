/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SAAT.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import SAAT.textmining.DiseaseEntry;

/**
 *
 * @author Robert
 */
public class LinguistikParser {
    private int start_ = 0;
    private int increment_ = 1000;
    private int end_ = 1385525;
    private DiseaseStorageLinguistikParser dslp_;
    private HashMap<String, HashSet<Integer>> wl_ = new HashMap<String, HashSet<Integer>>();
    private HashMap<String, Integer> wl_db_ = new HashMap<String, Integer>();
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> word_link_ = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
    private HashMap<String, Integer> wordlist_ = new HashMap<String, Integer>();
    
    public LinguistikParser() {
        dslp_ = new DiseaseStorageLinguistikParser();
    }
    
    public void run() {
        ArrayList<DiseaseEntry> des;
        int old_size = 0;
        dslp_.loadAllWords(wl_db_);
        for(int i = start_; i <= end_; i+=increment_+1) {
            des = dslp_.loadFindings(i, (i+increment_));
            dslp_.setAutocommit(false);
            for(DiseaseEntry de : des) {
                //extract(de);
                insertneighbor(de);
            }
            dslp_.setAutocommit(true);
            System.out.println("Findings: ;" + (i+increment_) + "; | Words found: ;" + wl_.size() + ";" + (wl_.size() - old_size));
            old_size = wl_.size();
        }
        //insertWords();      
    }
    
    public static void main(String[] args) {
        LinguistikParser lp = new LinguistikParser();
        //lp.run(); 
        lp.generateWordLinks();
    }

    private void extract(DiseaseEntry de) {
        de.preparTextForICD();
        String diagnosis = de.textCleaned;
        String[] diagnosis_array = diagnosis.split("@");
        for(String w : diagnosis_array) {
            if(wl_.containsKey(w)) {
                wl_.get(w).add(de.id);
            } else {
                wl_.put(w, new HashSet<Integer>());
                wl_.get(w).add(de.id);
            }
        }
    }

    private void insertWords() {
        for(String w : wl_.keySet()) {
            dslp_.insertWord(w);
            dslp_.insertFindingId(wl_.get(w), w);
        }
    }

    private void insertneighbor(DiseaseEntry de) {
        de.preparTextForICD();
        String diagnosis = de.textCleaned;
        String[] diagnosis_array = diagnosis.split("@");
        for(int i=0; i<diagnosis_array.length; i++) {
            for(int y=i+1; y<diagnosis_array.length; y++) {              
                try {
                    Integer w1 = wl_db_.get(diagnosis_array[y]);
                    Integer w2 = wl_db_.get(diagnosis_array[i]);
                    Integer n = y - i;
                    dslp_.insertLink(w1,w2,n);
                } catch(Exception ex) {
                    System.err.println("D_is: " + de.id + " .. " + diagnosis_array[i] + " | "+ diagnosis_array[y]);
                    System.err.println(ex.getMessage());
                    continue;
                }     
            }
        }
        dslp_.commit();
    }
    
    private void generateWordLinks() {
        dslp_.loadAllWords(wordlist_);
        for(int crit = 0; crit < 400000; crit += 100000) {
            System.out.println((crit) + " | " + (crit + 99999));
        }
        
        
        //word_link_
    }
}
