/***********************************************
 * TextMining.java
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
 * TextMining Base Program Class
 * Control Class for the Textmining Program
 ***********************************************
 */
package SAAT.textmining;

import java.util.HashMap;
import java.util.HashSet;
import SAAT.textmining.gui.*;
import SAAT.textmining.icd.DictionaryNode;
import SAAT.textmining.icd.PriorityCodeTuple;
import SAAT.textmining.receptor.ReceptorEntry;

/**
 * TextMining Base Program Class
 * Control Class for the Textmining Program
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class TextMining {

    /**
     * Variable decleration
     */
    private LogViewer log_ = new LogViewer();
    private DictionaryNode dictionaryRoot_;
    private DictionaryNode localDictionaryRoot_;
    private DiseaseStorage storage_;
    private UpdateStorage storageUpdate_;
    private SAAT.textmining.staging.StagingParser parser_staging_ = null;
    private SAAT.textmining.receptor.ReceptorParser parser_receptor_ = null;
    private HashSet<String> cmd_args_ = null;
    private boolean eval_;
    private String evalString_ = "";
    private DiseaseEntry lastDiseaseEntry_ = null;
    private HashMap<String, String> lastStaging_ = null;
    private HashMap<String, ReceptorEntry> lastReceptor_ = null;
    private PriorityCodeTuple lastPriorityCodeTuple_ = null;

    /**
     * TextMining Base Program
     * This will load and set up everything
     *
     * @param command line arguments
     */
    public TextMining(String[] args) {
        cmd_args_ = new HashSet<String>();
        for (String t : args) {
            cmd_args_.add(t);
        }

        parser_staging_ = new SAAT.textmining.staging.StagingParser(cmd_args_, log_);
        parser_receptor_ = new SAAT.textmining.receptor.ReceptorParser(cmd_args_, log_);

        if (cmd_args_.contains("-GUI")) {
            log_.setVisible(true);
        } else {
            log_.setVisible(false);
        }

        boolean isLocal = false;

        try {
            log_.out_printTime(false, false);
            log_.out_println("creating dictionary...");

            dictionaryRoot_ = new DictionaryNode(log_, true); // create zero Dictionary node
            log_.out_printTime(false, false);
            log_.out_println("creating dictionary...done");
        } catch (org.postgresql.util.PSQLException ex) {
            //try{
            System.err.println("TextMining::TextMining - PSQLException Error: " + ex.getMessage());
            // Function for the local dictionary
            /*
            log_.out_printTime(false, false);
            log_.out_println("creating local dictionary...");
            dictionaryRoot_ = new DictionaryNode(log_, false);
            isLocal = true;
            log_.out_printTime(false, false);
            log_.out_println("creating local dictionary...done");
            } catch(org.postgresql.util.PSQLException exloc) {
            System.err.println("TextMining::TextMining - PSQLException Error: " + exloc.getMessage());
            System.err.println("TextMining::TextMining - Exit Program, No dictionary available");
             */
            System.exit(-1);
            //}
        }

        // Function for the local dictionary
        /*
        if(!isLocal) {
        try{
        log_.out_printTime(false, false);
        log_.out_println("creating local dictionary..."); 
        //java.lang.NullPointerException Fangen dictionary locla l�schen und ganzes nochmal machen
        try{
        localDictionaryRoot_ = new DictionaryNode(log_, false);
        } catch(java.lang.NullPointerException nullex) {
        System.err.println("TextMining::TextMining - Lockal dictionary dead Ending");
        System.err.println("TextMining::TextMining - NullPointerException: " + nullex.getMessage());
        localDictionaryRoot_ = new DictionaryNode(log_, false, true);
        }
        log_.out_printTime(false, false);
        log_.out_println("creating local dictionary...done");
        
        log_.out_printTime(false, false);
        log_.out_println("updating local dictionary..."); 
        dictionaryRoot_.compareTo(localDictionaryRoot_);
        if(!localDictionaryRoot_.updateSuccess()) {
        System.err.println("Error in local dictionary restart update.");
        dictionaryRoot_.compareTo(localDictionaryRoot_);
        }
        localDictionaryRoot_ = null;
        log_.out_printTime(false, false);
        log_.out_println("updating local dictionary...done");
        
        } catch(org.postgresql.util.PSQLException exloc) {
        System.err.println("TextMining::TextMining - Lockal dictionary not available");
        System.err.println("TextMining::TextMining - PSQLException Error: " + exloc.getMessage());
        }
        }*/

        log_.out_printTime(false, false);
        log_.out_println("creating disease storage...");
        storage_ = new DiseaseStorage(log_);
        storageUpdate_ = new UpdateStorage(log_);
        log_.out_printTime(false, false);
        log_.out_println("creating disease storage...done");

    }

    /**
     * Start commando
     */
    public void start() {
        SAAT.generic.PrintTime pt = new SAAT.generic.PrintTime();

        boolean multithread = false;

        if (!multithread) {
            System.out.println(pt.getTime(true));
            threadWorker(0);
            System.out.println(pt.getTime(true));

        } else {

            Thread t1 = new Thread(new Runnable() {

                public void run() {
                    threadWorker(1);
                }
                ;
            });
            Thread t2 = new Thread(new Runnable() {

                public void run() {
                    threadWorker(2);
                }

                ;
            });
            Thread t3 = new Thread(new Runnable() {

                public void run() {
                    threadWorker(3);
                }

                ;
            });

            t1.setName("TextMining Thread 1");
            t2.setName("TextMining Thread 2");
            t3.setName("TextMining Thread 3");


            t1.start();
            t2.start();
            t3.start();

            try {
                t1.join();
                t2.join();
                t3.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void start(int disease_id) {
        eval_ = true;

        SAAT.generic.PrintTime pt = new SAAT.generic.PrintTime();

        storage_.enablePartialLoad(new int[]{disease_id});
        dictionaryRoot_.enableEval();

        System.out.println(pt.getTime(true));
        threadWorker(0);
        System.out.println(pt.getTime(true));

    }

    /**
     * Get the eval string node
     *
     * @return The eval string node
     */
    public SAAT.evaldisplay.EvalStringNode getEvalStringNode() {
        return dictionaryRoot_.getEvalStringNode();
    }

    /**
     * Get the last DiseaseEntry
     *
     * @return The last DiseaseEntry
     */
    public DiseaseEntry getLastDiseaseEntry() {
        return lastDiseaseEntry_;
    }

    /**
     * Get the last PriorityCodeTuples
     * 
     * @return The last PriorityCodeTuples
     */
    public PriorityCodeTuple getLastPriorityCodeTuple() {
        return lastPriorityCodeTuple_;
    }

    public HashMap<String, String> getLastStaging() {
        return lastStaging_;
    }

    /**
     *  ThreadWorker - Code Encoder
     *  is designed to work in threads (but not now!)
     * 
     *  @param Unique Worker identification number
     */
    private void threadWorker(int id) {
        DiseaseEntry de = null;
        synchronized (storage_) {
            de = storage_.getNextEntry();
        }
        while (de != null) {
            //ICD Parser
            if (cmd_args_.contains("-ICD")) {
                de.preparTextForICD();

                if (eval_) {
                    dictionaryRoot_.clearEval();
                }
                //code ICD
                dictionaryRoot_.parse(null, de, 0);

                PriorityCodeTuple tuple3 = dictionaryRoot_.getCodesForDiseaseID(de.id);

                if (eval_) {
                    lastDiseaseEntry_ = de;
                    lastPriorityCodeTuple_ = tuple3;


                } else { // don't update db if eval is in progress
                    synchronized (storageUpdate_) {
                        storageUpdate_.updateICD(de, tuple3);
                    }
                }
            }

            //Staging
            if (!parser_staging_.isDisabled()) {
                de.preparedTextForStaging();
                parser_staging_.setUp(de);
                parser_staging_.parse();

                synchronized (storageUpdate_) {

                    if (eval_) {
                        lastStaging_ = parser_staging_.getStagingForDiseaseID();
                    }

                    storageUpdate_.updateStaging(de, parser_staging_.getStagingForDiseaseID());
                }
            }
            //Receptor
            if (!parser_receptor_.isDisabled()) {



                de.preparedTextForRezeptor();
                parser_receptor_.setUp(de);
                parser_receptor_.parse();

                synchronized (storageUpdate_) {
                    if (eval_) {
                        lastReceptor_ = parser_receptor_.getReceptorForDiseaseID();
                    }

                    storageUpdate_.updateReceptor(de, parser_receptor_.getReceptorForDiseaseID());
                }
            }
            synchronized (storage_) {
                de = storage_.getNextEntry();
            }
        }
        //update last few entrys in storage
        synchronized (storageUpdate_) {
            storageUpdate_.updateDB();
        }
    }
}
