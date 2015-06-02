/***********************************************
 * EvalStringParser.java
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
 * The EvalStringParser implementing a TreeModel
 ***********************************************
 */
package SAAT.evaldisplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;
import SAAT.textmining.icd.PriorityCodeTuple;
import java.io.*;
import SAAT.generic.StringEncrypter;

/**
 * The EvalStringParser implementing a TreeModel
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class EvalStringParser implements TreeModel {

    /**
     * Variable decleration
     */
    String evalString_;
    int disease_id_;
    String disease_text_ = "";
    public EvalStringNode rootNode_;
    ConnectDataBase db_;
    HashMap<Integer, String> cacheDictionary_ = null;
    private PriorityCodeTuple pct_ = null;

    /** 
     * Creates a new instance of EvalStringParser and connect to the
     * database
     */
    public EvalStringParser() {
        disease_id_ = 0;
        evalString_ = "";

        db_ = new ConnectDataBase();
        try {
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
            db_.connectPostgres(split[0] + ":" + split[1] + "/" + split[2], split[3], split[4]);*/
            db_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            ex.printStackTrace();
        }
        rootNode_ = new EvalStringNode(0, 0, 0);
    }

    /**
     * Parse the node
     *
     * @param node
     */
    public void parse(SAAT.evaldisplay.EvalStringNode node) {
        rootNode_ = node;
        loadDictionaryData();
    }

    /**
     * Set the codes
     *
     * @param tuple PriorityCodeTuple for setting
     */
    public void setPriorityCodeTuple(PriorityCodeTuple tuple) {
        pct_ = tuple;
        for (EvalStringNode n : rootNode_.children()) {
            n.setupPriorityCodeTuple(tuple);
        }
    }

    /**
     * Load the dictionary
     */
    private void loadDictionaryData() {
        if (cacheDictionary_ == null) {
            cacheDictionary_ = new HashMap<Integer, String>();

            try {
                Statement stat = db_.getNewStatement();
                ResultSet result = stat.executeQuery("SELECT dictionary_id, synonym, iscode, code_typ, code_value, negation, priority FROM dictionary2");

                while (result.next()) {
                    if (result.getString(3).startsWith("t")) // sqlite lib dont support getBoolean
                    {
                        cacheDictionary_.put(result.getInt(1), result.getString(4) + ": " + result.getString(5) + " (P" + result.getShort(7) + ")");
                    } else if (result.getString(6).startsWith("t")) // sqlite lib dont support getBoolean
                    {
                        cacheDictionary_.put(result.getInt(1), "--" + result.getString(2) + " (P" + result.getShort(7) + ")");
                    } else {
                        cacheDictionary_.put(result.getInt(1), result.getString(2) + " (P" + result.getShort(7) + ")");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        for (EvalStringNode n : rootNode_.children()) {
            loadDictionaryData(n);
        }
    }

    /**
     * Load the dictionary recursively
     *
     * @param node The node to load
     */
    private void loadDictionaryData(EvalStringNode node) {
        if (cacheDictionary_.containsKey(node.getDictionaryID())) {
            node.setSynonym(cacheDictionary_.get(node.getDictionaryID()));
        }

        for (EvalStringNode n : node.children()) {
            loadDictionaryData(n);
        }
    }

    /**
     * Get the root node
     *
     * @return The root node
     */
    public Object getRoot() {
        return rootNode_;
    }

    /**
     * Get the child of the parent with the index definde
     *
     * @param parent The parent element
     * @param index The index
     * @return The child node element
     */
    public Object getChild(Object parent, int index) {
        EvalStringNode n = (EvalStringNode) parent;
        return n.getChild(index);
    }

    /**
     * Get the child count
     *
     * @param parent The parent element
     * @return The count of the children
     */
    public int getChildCount(Object parent) {
        EvalStringNode n = (EvalStringNode) parent;
        return n.getChilds().size();
    }

    /**
     * If it is a leaf
     *
     * @param node The node object
     * @return if it is a leaf
     */
    public boolean isLeaf(Object node) {
        EvalStringNode n = (EvalStringNode) node;
        return n.isLeaf();
    }

    /**
     * Get the index of the child
     *
     * @param parent The parent node
     * @param child The child object
     * @return The index of the child
     */
    public int getIndexOfChild(Object parent, Object child) {
        EvalStringNode n = (EvalStringNode) parent;
        for (int i = 0; i < n.getChilds().size(); i++) {
            if (child == n.getChild(i)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Not implemented from the interface
     *
     * @param path
     * @param newValue
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     * Not implemented from the interface
     *
     * @param l
     */
    public void addTreeModelListener(TreeModelListener l) {
    }

    /**
     * Not implemented from the interface
     * 
     * @param l
     */
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
