/***********************************************
 * XMLNode.java
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
 * The XML node class
 ***********************************************
 */
package SAAT.textmining.munich;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.jdom2.*;
import SAAT.generic.ConnectDataBase;
import SAAT.generic.XMLReader;
import SAAT.textmining.munich.MunichOrgan;
import SAAT.textmining.munich.XMLData;

/**
 * The XML node class
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class XMLNode {

    /**
     * Variable decleration
     */
    private XMLNode[] childNodes_;
    private XMLReader xml_reader_;
    private XMLData data_;
    private boolean root_;
    private MunichOrgan organ_;
    private String specimen_;

    /**
     * Generate an new xml node class
     *
     * @param xml_elements The xml element
     * @param root If its a root node or not
     */
    public XMLNode(Element xml_elements, boolean root) {
        root_ = root;
        organ_ = new MunichOrgan();
        xml_reader_ = new XMLReader();
        HashSet<Element> children = xml_reader_.getChildren(xml_elements);
        ArrayList<XMLNode> temp = new ArrayList<XMLNode>();
        data_ = new XMLData(xml_elements.getName());
        data_.setElementName(xml_elements.getName());
        data_.setElementText(xml_elements.getText());
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            temp.add(new XMLNode((Element) iterator.next()));
        }
        if (childNodes_ == null) {
            childNodes_ = new XMLNode[0];
        }
        childNodes_ = temp.toArray(childNodes_);
    }

    /**
     * Generate an new xml node class
     *
     * @param xml_elements The xml element
     */
    public XMLNode(Element xml_elements) {
        xml_reader_ = new XMLReader();
        HashSet<Element> children = xml_reader_.getChildren(xml_elements);
        ArrayList<XMLNode> temp = new ArrayList<XMLNode>();
        data_ = new XMLData(xml_elements.getName());
        data_.setElementName(xml_elements.getName());
        data_.setElementText(xml_elements.getText());
        Iterator iterator = children.iterator();
        while (iterator.hasNext()) {
            temp.add(new XMLNode((Element) iterator.next()));
        }
        if (childNodes_ == null) {
            childNodes_ = new XMLNode[0];
        }
        childNodes_ = temp.toArray(childNodes_);
    }

    /**
     * Get the children of the node in a array of nodes
     *
     * @return The children elements of the node
     */
    public XMLNode[] getChildren() {
        return childNodes_;
    }

    /**
     * Get the Elements text
     *
     * @return The Elements text
     */
    public String getText() {
        return data_.getElementText();
    }

    /**
     * Compairs if the element name matches this element
     *
     * @param element The element name
     * @return If the element names match
     */
    public boolean is(String element) {
        if (data_.element_.equals(element)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generate a Set of insert querys
     *
     * @param specimen The speciment
     * @return A set of insert querys
     */
    public HashSet<String> generateInsert(String specimen) {
        specimen_ = specimen;
        if (!root_) {
            return null;
        } else {
            HashSet<String> return_value = new HashSet<String>();
            for (int i = 0; i < childNodes_.length; ++i) {
                return_value.addAll(generateQuery(getData(childNodes_[i])));
            }
            return return_value;
        }
    }

    /**
     * Function generate the querys
     *
     * @param data The data elements
     * @return A set of querys
     */
    private HashSet<String> generateQuery(HashMap<String, String> data) {
        if (data.isEmpty()) {
            return null;
        }
        ConnectDataBase db = new ConnectDataBase();
        try {
            db.connectPostgresPropertie();
        } catch (org.postgresql.util.PSQLException exloc) {
            System.err.println("ImportIntoDb::ImportIntoDb - PSQLException Error: " + exloc.getMessage());
            System.err.println("ImportIntoDb::ImportIntoDb - Exit Program, No connection to lockal update database available");
            System.exit(-1);
        }

        String query = "INSERT INTO findings (";
        String query2 = " VALUES ( '";
        boolean first = true;
        String diagnosis_key = "";
        String diagnose_text = "";
        String eingangsnummer = "";
        String eingangsdatum = "";
        String pseudonym = "";
        for (String data_node : data.keySet()) {
            if (data_node.equals("Text")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "diagnosis";
                query2 += data.get(data_node).replaceAll("'", "");
                diagnose_text = data.get(data_node).replaceAll("'", "");
            }
            if (data_node.equals("Eingangsnummer")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "aura_index";
                query2 += data.get(data_node);
                diagnosis_key = data.get(data_node);
                eingangsnummer = data.get(data_node);
            }
            if (data_node.equals("Diagnoseschlüssel")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "organ";
                query2 += organ_.getOrgan(data.get(data_node));
            }
            if (data_node.equals("Eingangsdatum")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "examination_date";
                query2 += data.get(data_node);
                eingangsdatum = data.get(data_node);
            }
            if (data_node.equals("EinsenderKuerzel")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "sender";
                query2 += data.get(data_node);
            }
            if (data_node.equals("Befund-Typ")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "examination_type";
                query2 += data.get(data_node);
            }
            if (data_node.equals("Geschlecht")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "sex";
                if (data.get(data_node).equals("1")) {
                    query2 += "M";
                }
                if (data.get(data_node).equals("2")) {
                    query2 += "W";
                }
            }
            if (data_node.equals("Alter")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "age";
                query2 += data.get(data_node);
            }
            if (data_node.equals("Pseudonym")) {
                if (!first) {
                    query += ", ";
                    query2 += "', '";
                } else {
                    first = false;
                }
                query += "patient_id";
                query2 += data.get(data_node).replaceAll("'", "\'");
                pseudonym = data.get(data_node).replaceAll("'", "\'");
            }
        }
        query += ", specimen) " + query2 + "', '" + specimen_ + "');";
        boolean exists = false;
        try {
            Statement st = db.getNewStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM findings WHERE aura_index = '" + eingangsnummer + "' AND patient_id = " + pseudonym + " AND examination_date = '" + eingangsdatum + "' AND diagnosis = '" + diagnose_text + "'");
            while (rs.next()) {
                exists = true;
            }
        } catch (SQLException ex) {
            System.err.println("Transaction failed!");
            System.err.println("SQL Error: " + ex.getMessage());
        }

        HashSet<String> return_value = new HashSet<String>();
        if (!exists) {
            return_value.add(query);
        }
        db.closeConnection();
        return return_value;
    }

    /**
     * Get the data of the xml nodes and return them in a map
     *
     * @param childNode The child node
     * @return The elements in a map
     */
    private HashMap<String, String> getData(XMLNode childNode) {
        HashMap<String, String> return_value = new HashMap<String, String>();
        XMLNode[] children = childNode.getChildren();
        for (int i = 0; i < children.length; ++i) {
            return_value.putAll(getData(children[i]));
        }
        if (childNode.is("Text")) {
            return_value.put("Text", childNode.getText());
        }
        if (childNode.is("Eingangsnummer")) {
            return_value.put("Eingangsnummer", childNode.getText());
        }
        if (childNode.is("Diagnoseschlüssel")) {
            return_value.put("Diagnoseschlüssel", childNode.getText());
        }
        if (childNode.is("Eingangsdatum")) {
            return_value.put("Eingangsdatum", childNode.getText());
        }
        if (childNode.is("EinsenderKuerzel")) {
            return_value.put("EinsenderKuerzel", childNode.getText());
        }
        if (childNode.is("Befund-Typ")) {
            return_value.put("Befund-Typ", childNode.getText());
        }
        if (childNode.is("Geschlecht")) {
            return_value.put("Geschlecht", childNode.getText());
        }
        if (childNode.is("Alter")) {
            return_value.put("Alter", childNode.getText());
        }
        if (childNode.is("Pseudonym")) {
            return_value.put("Pseudonym", childNode.getText());
        }
        return return_value;
    }
}
