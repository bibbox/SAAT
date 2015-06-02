/***********************************************
 * FileFilterXML.java
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
 * Filter for xml files for the file selection
 ***********************************************
 */
package SAAT.textmining.munich.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filter for xml files for the file selection
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class FileFilterXML extends FileFilter {

    /**
     * The filter if the file is accepted
     *
     * @param f The file
     * @return If file is accepted
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        if (ext != null) {
            if (ext.equals("xml")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the description for the filter
     *
     * @return The description for the filter
     */
    public String getDescription() {
        return "XML File";
    }
}
