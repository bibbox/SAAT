/***********************************************
 * CorrectorMain.java
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
 * Main function for starting the corrector
 ***********************************************
 */

package SAAT.textmining.corrector;

/**
 * Main function for starting the corrector
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class CorrectorMain {
  
  /** 
   * Creates a new instance of CorrectorMain
   */
  public CorrectorMain() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
    correctFindingText f = new correctFindingText();
    f.execute();
    f.upload();
    
    /*correctDiseases d = new correctDiseases();
    d.execute();
    d.upload();*/
    
    /*
    updateDiseaseIDs d = new updateDiseaseIDs();
    d.execute();
    d.upload();
    */
    /*
    praeperatType d = new praeperatType();
    d.execute();
     */
    /*
    OPQuery d = new OPQuery();
    d.execute();
    */
  }
}
