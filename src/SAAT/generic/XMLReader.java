/***********************************************
 * XMLReader.java
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
 * An xml Reader class to read the elements of the XML file
 ***********************************************
 */
package SAAT.generic;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jdom2.*;
import org.jdom2.input.*;

/**
 * An xml Reader class to read the elements of the XML file
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class XMLReader {

    /**
     * Variable decleration
     */
    // The xml document
    private Document doc;

    /**
     * Generates a new instance of the class and initialize the variables
     */
    public XMLReader() {
        doc = null;
    }

    /**
     * Reads the XML file and generates an Document
     *
     * @param xmlInFile The XML filename
     */
    public XMLReader(String xmlInFile) {
        File f = new File(xmlInFile);
        if (f.exists()) {
            try {
                // ---- Read XML file ----
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(new File(xmlInFile));
            } catch (Exception ex) {
                System.err.println("XML Open File Error: " + ex.getMessage());
            }
        } else {
            System.out.println("XML File doesn't exist: " + xmlInFile);
            doc = null;
        }
    }

    /**
     * Returns the root node of the XML File
     *
     * @return The root node of the XML File
     */
    public Element getRootNode() {
        if (doc != null) {
            return doc.getRootElement();
        } else {
            return null;
        }
    }

    /**
     * Generates a list of all child elemets of the given element
     *
     * @param current The current element
     * @return A Hashlist of all child elements
     */
    public HashSet<Element> getChildren(Element current) {
        List children = current.getChildren();
        Iterator iterator = children.iterator();
        HashSet<Element> children_return = new HashSet<Element>();
        while (iterator.hasNext()) {
            children_return.add((Element) iterator.next());
        }
        return children_return;
    }
}
