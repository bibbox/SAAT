/***********************************************
 * Main.java
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
 * The main class for the textmining, starting the coding as a console
 * aplication.
 ***********************************************
 */

package SAAT.textmining;

/**
 * The main class for the textmining, starting the coding as a console
 * aplication.
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class Main {
  
  /** 
   * Creates a new instance of Main
   */
  public Main() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
	System.setProperty("javax.net.ssl.keyStore", "cacerts.jks"); //og//
	System.setProperty("javax.net.ssl.keyStorePassword", "apollo"); //og//
	System.setProperty("javax.net.ssl.trustStore", "cacerts.jks"); //og//
	System.setProperty("javax.net.ssl.trustStorePassword", "apollo"); //og//
	System.setProperty("javax.net.debug", "ssl"); //og//

    TextMining miner = new TextMining(args);
    miner.start();
  }
  
}
