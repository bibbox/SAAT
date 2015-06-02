/***********************************************
 * MunichOrgan.java
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
 * Generate a Metching betwean the organ file and the codes in the diagnosis.
 ***********************************************
 */

package SAAT.textmining.munich;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class MunichOrgan {

    /**
     * Variable decleration
     */
    private HashMap<String, String> munich_organs_ = null;

    /**
     * Generate a new Instance of the class MunichOrgan to generate
     * a Metching betwean the organ file and the codes in the diagnosis.
     */
    public  MunichOrgan() {  
        try {
            munich_organs_ = new HashMap<String, String>();

            File file = new File("munich.organ");

            BufferedReader bufRdr = null;
            String line_read = null;
            int row = 0;
            int col = 0;
        
            bufRdr = new BufferedReader(new FileReader(file));
            //read each line of text file
            while ((line_read = bufRdr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line_read, ";");
                String[] tmp = new String[2];
                while (st.hasMoreTokens()) {
                    //get next token and store it in the array
                    tmp[col] = st.nextToken();
                    col++;
                }
                munich_organs_.put(tmp[0], tmp[1]);
                col = 0;
                row++;
            }
        } catch (IOException ex) {
                Logger.getLogger(MunichOrgan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the organ from the list
     *
     * @param token The token
     * @return The organ
     */
    public String getOrgan(String token) {
        String[] split_token = token.split(" ");
        String return_value = "";
        for(int i=0; i<split_token.length; ++i) {
            if(munich_organs_.containsKey(split_token[i]))
                return_value += munich_organs_.get(split_token[i]) + ", ";
        }
        return return_value;
    }
}
