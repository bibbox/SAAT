/***********************************************
 * EvalStringNode.java
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
 * The EvalStringNode class structure for evaldisplay
 ***********************************************
 */
package SAAT.evaldisplay;

import java.util.ArrayList;
import javax.swing.tree.TreePath;
import SAAT.textmining.icd.PriorityCodeTuple;

/**
 * The EvalStringNode class structure for evaldisplay
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class EvalStringNode implements java.util.Comparator {

    /**
     * Variable decleration
     */
    private int dictionary_id_;
    private int hittedWordIndex_;
    private int level_;
    private boolean codeUsed_ = false;
    private String synonym_ = null;
    private ArrayList<EvalStringNode> nodes_;
    private String tab_ = "";

    /**
     * Creates a new instance of EvalStringNode
     * 
     * @param did The dictionary id
     * @param hwi The hitted word index
     * @param level The level
     */
    public EvalStringNode(int did, int hwi, int level) {
        dictionary_id_ = did;
        hittedWordIndex_ = hwi;
        level_ = level;
        nodes_ = new ArrayList<EvalStringNode>();

        for (int i = 0; i < level; i++) {
            tab_ += " ";
        }
    }

    /**
     * Get the word index
     *
     * @return The word index
     */
    public int getWordIndex() {
        return hittedWordIndex_;
    }

    /**
     * Get the level
     *
     * @return The level
     */
    public int getLevel() {
        return level_;
    }

    /**
     * Get the dictionary id
     *
     * @return The dictionary id
     */
    public int getDictionaryID() {
        return dictionary_id_;
    }

    /**
     * Set the synonym
     *
     * @param value The synonym of the element
     */
    public void setSynonym(String value) {
        synonym_ = value;
    }

    /**
     * Get the synonym
     *
     * @return The synonym of the element
     */
    public String getSynonym() {
        return synonym_;
    }

    /**
     * Get the TreePath
     *
     * @param forward The forward TreePath
     * @return A array of TreePath
     */
    public ArrayList<TreePath> getTreePaths(TreePath forward) {
        if (level_ != 0) {
            forward = forward.pathByAddingChild(this);
        }
        if (nodes_.size() == 0) { //leaf
            ArrayList<TreePath> rl = new ArrayList<TreePath>();
            rl.add(forward);
            return rl;
        }
        ArrayList<TreePath> r = new ArrayList<TreePath>();
        for (EvalStringNode e : nodes_) {
            r.addAll(e.getTreePaths(forward));
        }
        return r;
    }

    /**
     * Add subnodes
     *
     * @param node An EvalStringNode
     */
    public void addSubNode(EvalStringNode node) {
        boolean addnode = true;
        for (EvalStringNode n : nodes_) {
            if (n.dictionary_id_ == node.dictionary_id_ && n.hittedWordIndex_ == node.hittedWordIndex_) {
                for (EvalStringNode n2 : node.nodes_) {
                    n.addSubNode(n2);
                }
                addnode = false;
            }
        }
        if (addnode) {
            nodes_.add(node);
        }
    }

    /**
     * Sort the EvalStringNode
     */
    public void sort() {
        java.util.Collections.sort(nodes_, new EvalStringNode(0, 0, 0));
        for (EvalStringNode n : nodes_) {
            n.sort();
        }
    }

    /**
     * Generate PriorityCodeTuple
     *
     * @param tuple Generate PriorityCodeTuple
     */
    public void setupPriorityCodeTuple(PriorityCodeTuple tuple) {
        codeUsed_ = false;
        if (isLeaf()) {
            Integer[] dids = tuple.getDictionaryIDArray();
            if (dids == null) {
                return;
            }
            for (int i : dids) {
                if (i == dictionary_id_) {
                    codeUsed_ = true;
                }
            }
            return;
        }

        for (EvalStringNode n : nodes_) {
            n.setupPriorityCodeTuple(tuple);
        }
    }

    /**
     * Retruns if the code is used
     *
     * @return If the code is used
     */
    public boolean isCodeUsed() {
        return codeUsed_;
    }

    /**
     * Tree Properties
     *
     * @return a tree properties element
     */
    public String toString() {
        if (level_ == 0) //root node
        {
            return new String("root");
        }

        String text = String.format("%05d => ", dictionary_id_);

        if (hittedWordIndex_ != 0) {
            text += String.format("%03d", hittedWordIndex_);
        }
        if (synonym_ != null) {
            text += " : " + synonym_;
        }
        return text;
    }

    /**
     * Returns if its a leaf
     *
     * @return If its a leaf
     */
    public boolean isLeaf() {
        if (nodes_.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Get the child of the index
     *
     * @param index The index node
     * @return The EvalStringNode
     */
    public EvalStringNode getChild(int index) {
        return nodes_.get(index);
    }

    /**
     * Get the Childs
     *
     * @return A array list of the EvalStringNode
     */
    public ArrayList<EvalStringNode> getChilds() {
        return nodes_;
    }

    /**
     * Compare two EvalStringNode
     * 
     * @param o1 EvalStringNode 1
     * @param o2 EvalStringNode 2
     * @return The comparing value
     */
    public int compare(Object o1, Object o2) {
        EvalStringNode n1 = (EvalStringNode) o1;
        EvalStringNode n2 = (EvalStringNode) o2;

        if (n1.dictionary_id_ < n2.dictionary_id_) {
            return -1;
        }
        if (n2.dictionary_id_ > n2.dictionary_id_) {
            return 1;
        }
        if (n1.hittedWordIndex_ < n2.hittedWordIndex_) {
            return -1;
        }
        if (n1.hittedWordIndex_ > n2.hittedWordIndex_) {
            return 1;
        }
        return 0;
    }

    /**
     * Get the number of childs
     *
     * @return The number of childs
     */
    public int getChildCount() {
        return nodes_.size();
    }

    /**
     * Get an array of all child nodes
     *
     * @return An array of all child nodes
     */
    public ArrayList<EvalStringNode> children() {
        return nodes_;
    }
}
