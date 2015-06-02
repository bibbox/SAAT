/***********************************************
 * TreeViewRenderer.java
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
 * 
 ***********************************************
 */
package SAAT.evaldisplay.gui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.*;
import SAAT.evaldisplay.EvalStringNode;

/**
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class TreeViewRenderer implements TreeCellRenderer {

    /**
     * Variable decleration
     */
    private JPanel panel_;
    private JButton button_;
    private JLabel label_;
    private ArrayList<Color> colors_;

    /**
     * Creates a new instance of TreeViewRenderer
     */
    public TreeViewRenderer() {
        panel_ = new JPanel(new GridBagLayout());
        button_ = new JButton();
        label_ = new JLabel();

        label_.setOpaque(true);
        panel_.add(label_, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * Set the color array for the renderer
     *
     * @param colors The color array for the renderer
     */
    public void setColors(ArrayList<Color> colors) {
        colors_ = colors;
    }

    /**
     * Get the TreeCellRendererComponent
     *
     * @param tree
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        EvalStringNode node = (EvalStringNode) value;

        String syn = node.getSynonym();

        if (leaf) {
            if (syn != null) {
                label_.setForeground(Color.BLACK);

                if (node.isCodeUsed() && syn.contains("ICD-10")) {
                    label_.setBackground(new Color(0, 238, 0));
                } else if (node.isCodeUsed() && syn.contains("ICD-O")) {
                    label_.setBackground(new Color(0, 221, 255));
                } else if (node.isCodeUsed() && syn.contains("ICD-SCG")) {
                    label_.setBackground(new Color(238, 238, 0));
                } else if (!node.isCodeUsed() && (syn.contains("ICD-SCG") || syn.contains("ICD-10") || syn.contains("ICD-O"))) {
                    label_.setBackground(Color.lightGray);
                } else {
                    label_.setBackground(Color.GRAY);
                }
            } else {
                label_.setForeground(Color.BLACK);
                label_.setBackground(Color.GRAY);
            }
        } else if (syn != null) {
            if (syn.contains("--")) {
                label_.setForeground(Color.BLACK);
                label_.setBackground(tree.getBackground());
            } else {
                if (colors_.size() > node.getLevel()) {
                    label_.setForeground(colors_.get(node.getLevel()));
                } else {
                    label_.setForeground(Color.BLACK);
                }
                label_.setBackground(tree.getBackground());
            }
        } else {
            if (colors_.size() > node.getLevel()) {
                label_.setForeground(colors_.get(node.getLevel()));
            } else {
                label_.setForeground(Color.BLACK);
            }

            label_.setBackground(tree.getBackground());
        }
        label_.setText(value.toString());

        return panel_;
    }
}
