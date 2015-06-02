/***********************************************
 * LanAddress.java
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
 * The Function calls with the windows commandline the ipconfig
 * command and extracts the MAC address out of it. Not useabel
 * on all platforms and need to be changed.
 * Feature Requests: 3416774
 ***********************************************
 */
package SAAT.generic;

import java.io.*;
import java.util.regex.*;

/**
 * The Function calls with the windows commandline the ipconfig
 * command and extracts the MAC address out of it. Not useabel
 * on all platforms and need to be changed.
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class LanAddress {

    /**
     * Function to get the MAC address of the system
     *
     * The Function calls with the windows commandline the ipconfig
     * command and extracts the MAC address out of it.
     *
     * @return The MAC address
     * @throws IOException
     */
    public static String getMacAddress() throws IOException {
        // calls the comadline and executes the ipconfig command
        Process proc = Runtime.getRuntime().exec("cmd /c ipconfig /all");
        InputStream IS = proc.getInputStream();
        String ipconfig = parseISToString(IS).replace("\r", "").replace("\n", "");
        String r = null;
        try {
            // extract the MAC address from the pc
            Pattern Regex = Pattern.compile("[0-9a-fA-F]{2}(-[0-9a-fA-F]{2}){5}", Pattern.CANON_EQ);
            Matcher RegexMatcher = Regex.matcher(ipconfig);
            while (RegexMatcher.find()) {
                r = RegexMatcher.group();
                if (r != null) {
                    break;
                }
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return r;
    }

    /**
     * Function to generate a String out of the InputStream
     *
     * @param is The input stream
     * @return The generated string
     */
    private static String parseISToString(java.io.InputStream is) {
        java.io.DataInputStream din = new java.io.DataInputStream(is);
        StringBuffer sb = new StringBuffer();
        try {
            String line = null;
            while ((line = din.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
        return sb.toString();
    }
}
