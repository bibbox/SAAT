/***********************************************
 * DictionaryNode.java
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
 * This class builds the tree and parses a given DiseaseEntry
 ***********************************************
 */
package SAAT.textmining.icd;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.postgresql.util.PSQLException;
import SAAT.evaldisplay.EvalStringNode;
import SAAT.generic.StringEncrypter;
import SAAT.textmining.DiseaseEntry;
import SAAT.textmining.gui.*;

/**
 * DictionaryNode
 * This class builds the tree and parses a given DiseaseEntry
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class DictionaryNode {

    /**
     * Variable decleration
     */
    private static SAAT.generic.ConnectDataBase db_ = new SAAT.generic.ConnectDataBase();

    public enum DOMAIN_CODE {

        ICD10, ICDO, ICDSCG
    };
    private LogViewer log_;
    public DictionaryData data_;
    private ICDParser parser_;
    private DictionaryNode[] child_nodes_;
    private int node_position_ = 0;
    private boolean eval_ = false;
    private String eval_text_ = "";
    private SAAT.evaldisplay.EvalStringNode eval_root_;

    /**
     * Creates a new instance of DictionaryNode
     * This one loads the complete Dictionary into a list of DictionaryData entrys
     * after that it will rekursivly generate the tree with this list.
     * This Methode is extreamly faster than the recursiv exploring Methode
     *
     * @param Takes a Logviewer, to write it's log messages into it
     */
    public DictionaryNode(LogViewer log, boolean use_postgres) throws PSQLException {
        log_ = log;

        try {
            /*
             *Connect to dictionary IBMT
             *IF not Possible connect to local backup
             *TODO:
             *connect to local backup
             */
            if (use_postgres) {
                String crypted_string = null;
                String connection_string = null;
                try {
                    FileInputStream fstream = new FileInputStream("dictserver.key");
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    // Read File Line By Line
                    while ((strLine = br.readLine()) != null) {
                        crypted_string = strLine;
                    }
                    // Close the input stream
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                /*try {
                    StringEncrypter encrypter;
                    encrypter = new StringEncrypter();
                    connection_string = encrypter.decrypt(crypted_string);
                } catch (StringEncrypter.EncryptionException ex) {
                    ex.printStackTrace();
                }

                String[] split = connection_string.split(",");*/
                //System.out.println("tm1db_.connectPostgres(" + split[0] + ":" + split[1] + "/" + split[2] +","+ split[3] +","+ split[4] + ");");
                //db_.connectPostgres(split[0] + ":" + split[1] + "/" + split[2], split[3], split[4]);
                db_.connectPostgresPropertie();
            } else {
                db_.connectSQLite("thesaurus", "", "");
            }
            java.sql.Statement stat1 = db_.getNewStatement();

            //TODO institute_id aus Datei lesen
            java.sql.ResultSet result1 = stat1.executeQuery("SELECT dictionary_id from dictionary2;");

            // create list
            HashMap<Integer, DictionaryData> dict = new HashMap<Integer, DictionaryData>();
            //create root_ node
            DictionaryData data0 = new DictionaryData(db_);
            data0.pattern_compile_ = java.util.regex.Pattern.compile("");
            dict.put(0, data0); //root_ node

            while (result1.next()) {
                dict.put(result1.getInt("dictionary_id"), new DictionaryData(db_));
            }
            result1.close();

            //populate list
            java.sql.Statement stat2 = db_.getNewStatement();
            java.sql.ResultSet result2 = stat2.executeQuery("SELECT * from dictionary2;");
            while (result2.next()) {
                int id = result2.getInt("dictionary_id");
                if (!dict.containsValue(id)) {

                    String root_test = result2.getString("root");
                    boolean root;
                    if (root_test.startsWith("f")) // sqlite lib dont support getBoolean
                    {
                        root = false;
                    } else {
                        root = true;
                    }
                    if (root) {
                        int ad = 0;

                        try {
                            //set child for root_
                            DictionaryData t1 = dict.get(ad);
                            t1.childs_.add(id);
                            dict.remove(ad);
                            dict.put(ad, t1);
                        } catch (java.lang.NullPointerException nullex) {
                            db_.closeConnection();
                            throw nullex;
                        }
                    } else {
                        java.sql.Statement stat3 = db_.getNewStatement();
                        java.sql.ResultSet result3 = stat3.executeQuery("SELECT * from dictionary2_dictionary2_addiction_link LEFT JOIN dictionary_institute_rules ON dictionary_institute_rules.dictionary2_dictionary2_addiction_link_id = dictionary2_dictionary2_addiction_link.id WHERE dictionary_id = " + id + " AND (institute_id = 1 OR institute_id IS NULL);");
                        while (result3.next()) {
                            int ad = result3.getInt("addiction_id");
                            int inst_id = result3.getInt("institute_id");
                            if (inst_id != 0) {
                                ad = 50000;
                            }
                            try {
                                //set child
                                DictionaryData t1 = dict.get(ad);
                                t1.childs_.add(id);
                                dict.remove(ad);
                                dict.put(ad, t1);
                            } catch (java.lang.NullPointerException nullex) {
                                db_.closeConnection();
                                throw nullex;
                            }
                        }
                        result3.close();
                    }
                    //load data
                    DictionaryData t2 = dict.get(id);
                    t2.parseResultSet(result2);

                    if (t2.root_) // everything matches
                    {
                        t2.occur_ = true;
                    }
                    dict.remove(id);
                    dict.put(id, t2);
                }
            }
            result2.close();

            //rekursive create DictionaryNodes
            data_ = dict.get(0);

            ArrayList<DictionaryNode> temp = new ArrayList<DictionaryNode>();
            for (Integer cid : data_.childs_) {
                if (cid != 50000) {
                    temp.add(new DictionaryNode(log_, cid, node_position_, dict));
                }
            }
            if (child_nodes_ == null) {
                child_nodes_ = new DictionaryNode[0];
            }
            child_nodes_ = temp.toArray(child_nodes_);

        } catch (org.postgresql.util.PSQLException ex) {
            System.err.println("DictionaryNode::DictionaryNode - PSQLException Error: " + ex.getMessage());
            throw (ex);
        } catch (SQLException ex) {
            System.err.println("SQLException in DictionaryNode::DictionaryNode" + ex.getMessage());
            ex.printStackTrace();
        }

        parser_ = new ICDParser(log_, data_);

        // check the tree for general Negation Disabler (such as "Methastase" in a tree branch)
        checkTreeForNegationDisabler(new GeneralNegationRule());
    }

    /**
     * Creates a new instance of DictionaryNode
     * This one loads the complete Dictionary into a list of DictionaryData entrys
     * after that it will rekursivly generate the tree with this list.
     * This Methode is extreamly faster than the recursiv exploring Methode
     *
     * @param log Takes a Logviewer, to write it's log messages into it
     * @param use_postgres If using postgresql
     * @param delete If first the database to generate a new copy
     * 
     * @throws PSQLException
     */
    public DictionaryNode(LogViewer log, boolean use_postgres, boolean delete) throws PSQLException {
        log_ = log;

        try {
            /*
             *Connect to dictionary IBMT
             *IF not Possible connect to local backup
             *TODO:
             *connect to local backup
             */
            if (use_postgres) {

                String crypted_string = null;
                String connection_string = null;
                try {
                    FileInputStream fstream = new FileInputStream("dictserver.key");
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    //Read File Line By Line
                    while ((strLine = br.readLine()) != null) {
                        crypted_string = strLine;
                    }
                    //Close the input stream
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                /*try {
                    StringEncrypter encrypter;
                    encrypter = new StringEncrypter();
                    connection_string = encrypter.decrypt(crypted_string);
                } catch (StringEncrypter.EncryptionException ex) {
                    ex.printStackTrace();
                }

                String[] split = connection_string.split(",");
                db_.connectPostgres(split[0] + ":" + split[1] + "/" + split[2], split[3], split[4]);
                */
                db_.connectPostgresPropertie();
            } else {
                db_.connectSQLite("thesaurus", "", "");
            }
            java.sql.Statement stat1 = db_.getNewStatement();

            if (delete && !use_postgres) {
                db_.executeQuerys(new String[]{"DELETE FROM dictionary2;DELETE FROM dictionary2_dictionary2_addiction_link;"});
            }
            java.sql.ResultSet result1 = stat1.executeQuery("SELECT dictionary_id from dictionary2;");

            // create list
            HashMap<Integer, DictionaryData> dict = new HashMap<Integer, DictionaryData>();
            //create root_ node
            DictionaryData data0 = new DictionaryData(db_);
            data0.pattern_compile_ = java.util.regex.Pattern.compile("");
            dict.put(0, data0); //root_ node

            while (result1.next()) {
                dict.put(result1.getInt("dictionary_id"), new DictionaryData(db_));
            }
            result1.close();

            //populate list
            java.sql.Statement stat2 = db_.getNewStatement();
            java.sql.ResultSet result2 = stat2.executeQuery("SELECT * from dictionary2;");
            while (result2.next()) {
                int id = result2.getInt("dictionary_id");

                String root_test = result2.getString("root");
                boolean root;
                if (root_test.startsWith("f")) // sqlite lib dont support getBoolean
                {
                    root = false;
                } else {
                    root = true;
                }
                if (root) {
                    int ad = 0;

                    try {
                        //set child for root_
                        DictionaryData t1 = dict.get(ad);
                        t1.childs_.add(id);
                        dict.remove(ad);
                        dict.put(ad, t1);
                    } catch (java.lang.NullPointerException nullex) {
                        db_.closeConnection();
                        throw nullex;
                    }
                } else {
                    java.sql.Statement stat3 = db_.getNewStatement();
                    java.sql.ResultSet result3 = stat3.executeQuery("SELECT * from dictionary2_dictionary2_addiction_link WHERE dictionary_id = " + id + ";");
                    while (result3.next()) {
                        int ad = result3.getInt("addiction_id");
                        try {
                            //set child
                            DictionaryData t1 = dict.get(ad);
                            t1.childs_.add(id);
                            dict.remove(ad);
                            dict.put(ad, t1);
                        } catch (java.lang.NullPointerException nullex) {
                            db_.closeConnection();
                            throw nullex;
                        }
                    }
                    result3.close();
                }

                //load data
                DictionaryData t2 = dict.get(id);
                t2.parseResultSet(result2);

                if (t2.root_) // everything matches
                {
                    t2.occur_ = true;
                }
                dict.remove(id);
                dict.put(id, t2);
            }
            result2.close();

            //rekursive create DictionaryNodes
            data_ = dict.get(0);

            ArrayList<DictionaryNode> temp = new ArrayList<DictionaryNode>();
            for (Integer cid : data_.childs_) {
                if (cid != 50000) {
                    temp.add(new DictionaryNode(log_, cid, node_position_, dict));
                }
            }
            if (child_nodes_ == null) {
                child_nodes_ = new DictionaryNode[0];
            }
            child_nodes_ = temp.toArray(child_nodes_);

        } catch (org.postgresql.util.PSQLException ex) {
            System.err.println("DictionaryNode::DictionaryNode - PSQLException Error: " + ex.getMessage());
            throw (ex);
        } catch (SQLException ex) {
            System.err.println("SQLException in DictionaryNode::DictionaryNode" + ex.getMessage());
            ex.printStackTrace();
        }

        parser_ = new ICDParser(log_, data_);

        // check the tree for general Negation Disabler (such as "Methastase" in a tree branch)
        checkTreeForNegationDisabler(new GeneralNegationRule());

    }

    /**
     * Constructor for the recursive Tree generation described before
     *
     * @param log Takes a Logviewer, to write it's log messages into it
     * @param dictionary_id_
     * @param parentNodePosition Position of the node in this branch of the tree
     * @param dict array of DictionaryData entrys
     */
    public DictionaryNode(LogViewer log, int dictionary_id, int parentNodePosition_, HashMap<Integer, DictionaryData> dict) {
        log_ = log;
        node_position_ = parentNodePosition_ + 1;

        data_ = dict.get(dictionary_id);
        parser_ = new ICDParser(log_, data_);

        if (data_.iscode_ == true) {
            return;        //rekursive create DictionaryNodes
        }
        ArrayList<DictionaryNode> temp = new ArrayList<DictionaryNode>();
        for (Integer cid : data_.childs_) {
            temp.add(new DictionaryNode(log_, cid, node_position_, dict));
        }
        if (child_nodes_ == null) {
            child_nodes_ = new DictionaryNode[0];
        }
        child_nodes_ = temp.toArray(child_nodes_);

    }

    /**
     * Enables the Evaluation Methode of the Parser
     */
    public void enableEval() {
        eval_ = true;
        if (child_nodes_ == null) {
            return;
        }
        if (node_position_ == 0) {
            eval_root_ = new SAAT.evaldisplay.EvalStringNode(0, 0, 0);
        }
        for (DictionaryNode item : child_nodes_) {
            item.enableEval();
        }
    }

    /**
     * Returns the EvalStringNode object for the SAAT.evaldisplay
     * 
     * @return The EvalStringNode object for the SAAT.evaldisplay
     */
    public EvalStringNode getEvalStringNode() {
        return eval_root_;
    }

    /**
     * Function for the SAAT.evaldisplay, clears als children from the root node
     */
    public void clearEval() {
        eval_root_.children().clear();
    }

    /**
     * Get the complete Evaluation
     */
    public String getEvalString() {
        return eval_text_;
    }

    /**
     * Checks the tree for general Negation Disabler
     * (such as "Methastase" in a tree branch)
     * This will be called only once by the constructor
     *
     * @param gnr this will be draggt up the tree until a code Node is reached (there it is safed)
     */
    private void checkTreeForNegationDisabler(GeneralNegationRule gnr) {
        //code node - set GeneralNegationRule
        if (child_nodes_ == null) {
            data_.general_negation_rules_ = gnr;
            return;
        }

        gnr.setUp(data_.synonym_);

        for (DictionaryNode item : child_nodes_) {
            item.checkTreeForNegationDisabler(gnr);
        }
    }

    /**
     * This Methode goes through the tree and gives back all
     * disease ids from changed Nodes.
     * Note that the ids from the parent will be given, because
     * there might be more matches with the changed rule.
     *
     * @return Disease ids from changed Nodes
     */
    public Integer[] getAllDirtyDiseaseIDs() {
        if (child_nodes_ == null) {
            return null;
        }
        for (DictionaryNode n : child_nodes_) {
            if (n.isDirty()) {
                return (Integer[]) data_.matching_disease_ids_.toArray(new Integer[data_.matching_disease_ids_.size()]);
            }
        }

        // nothing dirty_ under the childnodes
        HashSet<Integer> t2 = new HashSet<Integer>();
        for (DictionaryNode n : child_nodes_) {
            Integer[] t1 = n.getAllDirtyDiseaseIDs();
            if (t1 == null) {
                continue;
            }
            for (int i : t1) {
                if (!t2.contains(i)) {
                    t2.add(i);
                }
            }
        }
        if (t2.size() > 0) {
            return (Integer[]) t2.toArray(new Integer[t2.size()]);
        }
        return null;
    }

    /**
     * Return if node is flagged dirty
     * 
     * @return If node is flagged dirty
     */
    public boolean isDirty() {
        return data_.dirty_;
    }

    /**
     * Main Parse Methode of the ICD Tree
     * gives back if the actual match was succsessful
     *
     * @param de a DiseaseEntry
     * @param wirdIndex the wordindex of the previous matched word
     */
    public boolean parse(EvalStringNode eval, DiseaseEntry de, int wordIndex) {
        de.addMatchedIndex(wordIndex);
        boolean ret = false;
        if (node_position_ == 0) {
            ret = parseRootNode(de);
        } else {
            ret = parseSubNode(eval, de, wordIndex);
        }
        de.removeMatchedIndex(wordIndex);
        return ret;
    }

    /**
     * Internal parse Methode
     * Simply because we need to do the root_ node a little different
     * as every other node.
     *
     * @param de a DiseaseEntry
     */
    private boolean parseRootNode(DiseaseEntry de) {
        try {
            data_.matching_disease_ids_.add(de.id);

            for (DictionaryNode item : child_nodes_) {
                boolean hit = item.parse(eval_root_, de, -1);
                if (hit && eval_) {
                    //eval_text_ += Integer.toString(de.id) + "-" +  item.getEvalString();
                }
            }
        } catch (Exception ex) {
            System.err.println("Exception in DictionaryNode::parseRootNode: " + ex.getMessage());
        }
        return true;
    }

    /**
     * Internal parse Methode
     * Simply because we need parse the sub nodes a little different
     * than the root_ node
     *
     * @param de a DiseaseEntry
     */
    private boolean parseSubNode(EvalStringNode eval, DiseaseEntry de, int wordIndex) {
        parser_.setUp(de, wordIndex);

        //field is code - no more parsing needed
        //if(data_.dictionary_id_ == 2118) {
        //    System.out.println("Debug: dictionary ID hit: 2118");
        //}
        if (data_.iscode_ == true) {
            if (!parser_.isSuspiciousCode()) {
                data_.matching_disease_ids_.add(de.id);
            }
            if (eval_) {
                eval.addSubNode(new EvalStringNode(data_.dictionary_id_, 0, node_position_));
            }

            return true;
        }

        boolean hasHitted = parser_.parse();

        // nothing todo here....
        if (!hasHitted) {
            return false;        //match confirmed
        }
        data_.matching_disease_ids_.add(de.id);


        Integer[] hi = parser_.getHittedWordIndices();

        try {
            //now for every hit we parse the rest of the subnodes
            for (Integer id : hi) {
                if (id != null) {
                    EvalStringNode evaln = null;
                    if (eval_ && eval != null) {
                        evaln = new EvalStringNode(data_.dictionary_id_, id, node_position_);
                        eval.addSubNode(evaln);
                    }

                    for (DictionaryNode item : child_nodes_) {
                        boolean hit = item.parse(evaln, de, id);
                        /*
                        if(eval_ && hit) {
                        eval_text_ += Integer.toString(data_.dictionary_id_) + ":" + Integer.toString(id) + "(" + item.getEvalString() + ")";
                        }*/
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("Exception in DictionaryNode::parseSubNode: " + ex.getMessage());
            ex.printStackTrace();
        }

        return true;
    }

    /**
     * Methode to get all the matched codes for a Disease Entry
     *
     * @param disease_id The requested disease_id
     * @return The matching codes
     */
    public PriorityCodeTuple getCodesForDiseaseID(int disease_id) {
        if (!data_.matching_disease_ids_.contains(disease_id)) // is disease id matching this DictionaryNode?
        {
            return null;
        }
        if (data_.iscode_ == true) {       // is DictionaryNode code?
            PriorityCodeTuple tuple = new PriorityCodeTuple();
            tuple.addCode(data_.code_value_, data_.priority_, data_.dictionary_id_);
            return tuple;
        }

        //return childNode Codes
        PriorityCodeTuple tuple = new PriorityCodeTuple();
        for (DictionaryNode item : child_nodes_) {
            PriorityCodeTuple t = item.getCodesForDiseaseID(disease_id);

            if (t == null) {
                continue;            //false => merge codes
                //true  => prioriy codes
            }
            if (data_.priority_mode_) {
                tuple.addCode(t);
            } else {
                tuple.merge(t);
            }
        }

        return tuple;
    }

    /**
     * Collects all the dictionaryupdate querys from the subnodes
     *
     * @return A query arry with updates
     */
    public ArrayList<String> getSaveQuerys() {
        ArrayList<String> l = new ArrayList<String>();

        if (node_position_ != 0) {
            l.add(data_.getSaveString());
        }
        if (data_.iscode_) {
            return l;
        }
        for (DictionaryNode node : child_nodes_) {
            l.addAll(node.getSaveQuerys());
        }
        return l;
    }

    /**
     * Function to compare dictionary nodes
     *
     * @param dict The dictionary node
     */
    public void compareTo(DictionaryNode dict) {

        int t = data_.compareTo(dict.data_);

        if (t != 0) {
            dict.updateDictionaryNode(this);
        }

        HashSet<Integer> didlist = dict.getChildNodesDictionaryIdList();

        if (child_nodes_ == null) {
            return;
        }
        for (DictionaryNode d : child_nodes_) {
            didlist.remove(d.data_.dictionary_id_);
            DictionaryNode u = dict.getChildNode(d.data_.dictionary_id_);
            if (u != null) {
                d.compareTo(u);
            } else {
                dict.insertDictionaryNode(d);
                //insert all Notes + add to dict
                recursiveInsertDictionaryNode(d, dict);
            }
        }

        if (didlist.size() > 0) {
            for (Integer i : didlist) {
                dict.deleteDictionaryNode(getChildNode(i));
            }
        }
    }

    /**
     * Retruns the children from the dictionary id requested
     *
     * @param dictionary_id The requested id
     * @return The children
     */
    public DictionaryNode getChildNode(int dictionary_id) {
        for (DictionaryNode f : child_nodes_) {
            if (dictionary_id == f.data_.dictionary_id_) {
                return f;
            }
        }
        return null;
    }

    /**
     * Deleting the dictionary entry and the addicion entry
     *
     * @param dictionaryNode The node to delete
     */
    public void deleteDictionaryNode(DictionaryNode dictionaryNode) {
        if (dictionaryNode == null) {
            return;
        }
        db_.executeQuerys(new String[]{"DELETE FROM dictionary2 USING dictionary2_dictionary2_addiction_link WHERE dictionary_id = " + dictionaryNode.data_.dictionary_id_ + " AND dictionary2_dictionary2_addiction_link.addiction_id = " + dictionaryNode.data_.addiction_ + ";", "DELETE FROM dictionary2_dictionary2_addiction_link WHERE dictionary_id = " + dictionaryNode.data_.dictionary_id_ + " AND addiction_id = " + dictionaryNode.data_.addiction_ + ";"});
    }

    /**
     * Update the dictionary node
     *
     * @param dictionaryNode The node to update
     */
    public void updateDictionaryNode(DictionaryNode dictionaryNode) {
        System.out.println("DEBUGG: Update Dictionary");
        if (dictionaryNode == null) {
            return;
        }
        db_.executeQuerys(new String[]{dictionaryNode.data_.getSqlUpdateQuery()});
    }

    /**
     * Get the childs id list
     *
     * @return The childs id list
     */
    public HashSet<Integer> getChildNodesDictionaryIdList() {
        HashSet<Integer> didlist = new HashSet<Integer>();
        if (child_nodes_ == null) {
            return didlist;
        }
        for (DictionaryNode d : child_nodes_) {
            didlist.add(d.data_.dictionary_id_);
        }
        return didlist;
    }

    /**
     * Inserts an dictionary node to the db
     *
     * @param dictionaryNode The node to insert
     */
    public void insertDictionaryNode(DictionaryNode dictionaryNode) {
        if (dictionaryNode == null) {
            return;
        }
        db_.executeQuerys(new String[]{dictionaryNode.data_.getSqlInsertQuery()});
    }

    /**
     * Function to test if the update when criating an new dictionary was a success
     *
     * @return If if update was a success
     */
    public boolean updateSuccess() {
        try {
            Statement request = db_.getNewStatement();
            ResultSet result = request.executeQuery("SELECT COUNT(*), dictionary_id FROM dictionary2 GROUP BY dictionary_id HAVING COUNT(*) > 1;");
            while (result.next()) {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Function to insert recurivly the nodes
     *
     * @param d The starting node
     * @param dict The childe nodes
     */
    private void recursiveInsertDictionaryNode(DictionaryNode d, DictionaryNode dict) {
        if (d.child_nodes_ == null) {
            return;
        }
        for (DictionaryNode recursivedictNode : d.child_nodes_) {
            try {
                dict.deleteDictionaryNode(recursivedictNode);
            } catch (Exception ex) {
                System.err.println("dict.deleteDictionaryNode(recursivedictNode);");
                ex.printStackTrace();
            }
            try {
                dict.insertDictionaryNode(recursivedictNode);
            } catch (Exception ex) {
                System.err.println("dict.insertDictionaryNode(recursivedictNode);");
                ex.printStackTrace();
            }
            recursiveInsertDictionaryNode(recursivedictNode, dict);
        }
        return;
    }

    /**
     * Colses the DB connection
     */
    public void closeDbConnection() {
        db_.closeConnection();
    }
}
