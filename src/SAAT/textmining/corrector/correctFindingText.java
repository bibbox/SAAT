/***********************************************
 * correctFindingText.java
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
 * Update the diagnosis from the findings tabel
 ***********************************************
 */
package SAAT.textmining.corrector;

import java.io.*;
import java.sql.*;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;
import SAAT.generic.PrintTime;

/**
 * Update the diagnosis from the findings tabel
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class correctFindingText {

    /**
     * Variable decleration
     */
    static ConnectDataBase dbconnection_;
    static BufferedWriter fout_;
    String table_;
    String col_input_;
    String col_output_;
    String row_id_;

    /**
     * Creates a new instance of correctFindingText an generate a 
     * database connection.
     */
    public correctFindingText() {
        dbconnection_ = new ConnectDataBase();
        try {
            dbconnection_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            System.err.println("correctFindingText::correctFindingText - PSQLException Error: " + ex.getMessage());
            System.err.println("correctFindingText::correctFindingText - Exit Program, No connection to lockal update database available");
        }
        table_ = "findings";
        col_input_ = "diagnosis";
        col_output_ = "diagnosis_clean";
        row_id_ = "finding_id";

        fout_ = null;
    }

    /**
     * Start the Diseases update
     */
    public void execute() {
        try {
            fout_ = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("updateFindings.sql")));
            Statement st = dbconnection_.getNewStatement();

            boolean stop = false;
            int start = 0;
            int increment = 10000;

            while (!stop) {
                new PrintTime(false);
                System.out.println(String.format("read " + table_ + " DB from %7d to %7d", start, start + increment));
                ResultSet result = st.executeQuery("SELECT " + row_id_ + ", " + col_input_ + "  FROM " + table_ + " ORDER BY " + row_id_ + " LIMIT " + Integer.toString(increment) + " OFFSET " + Integer.toString(start) + ";");

                new PrintTime(false);
                System.out.println("start encoding");

                int counter = 0;

                while (result.next()) {
                    correctFindingString(result.getInt(row_id_), result.getString(col_input_));

                    counter++;
                }

                start += increment;

                if (counter == 0) {
                    stop = true;
                    break;
                }
            }
            fout_.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Generate the update querys for the findings correction
     *
     * @param id The findings id
     * @param f The findings text
     */
    private void correctFindingString(int id, String f) {
        String text = f.toUpperCase();
        text = f.replace("�", "OE");
        text = text.replace("�", "UE");
        text = text.replace("�", "AE");
        text = text.replace("�", "SZ");

        text = text.replace("[", "AE");
        text = text.replace("]", "UE");
        text = text.replace("~", "SZ");
        text = text.replaceAll("\\s+", " "); // space \r \n

        text = text.replace("HOCHGR.", "HOCHGRADIG");
        text = text.replace("MITTELGR.", "MITTELGRADIG");
        text = text.replace("GERINGGR.", "GERINGGRADIG");
        text = text.replace("MAESIGGR.", "MAESIGGRADIG");

        text = text.replace("MAKROS.", "MAKROSKOPISCH");

        text = text.replace("FRMIG.", "FOERMIG");
        text = text.replace("GRSZTE.", "GROESZTE");
        text = text.replace("AKTI- VIERUNG", "AKTIVIERUNG");
        text = text.replace("DUKATALEN", "DUKTALEN");

        text = text.replace("FREMDKRPER", "FREMDKOERPER");
        text = text.replace("FREUNDLICHERWEIE", "FREUNDLICHERWEISE");

        text = text.replace("REZIDIV.", "REZIDIVIEREND");
        text = text.replace("SOG.", "SOGENANNT");
        text = text.replace("UNSPEZ.", "UNSPEZIFISCH");
        text = text.replace("CHRON.", "CHRONISCH");
        text = text.replace("HIST.", "HISTOLOGISCH");
        text = text.replace(" OP.", " OP");
        text = text.replace("GRSZTE", "GROESZTE");

        text = text.replace("PROGERSTERON", "PROGESTERON");
        text = text.replace("DRMATOFIBROM", "DERMATOFIBROM");
        text = text.replace("REZEPTORENN ", "REZEPTOREN ");
        text = text.replace("ZY-STISCHE", "ZYSTISCHE");

        text = text.replace("CARCINOM", "KARZINOM");
        text = text.replace("FIBRS", "FIBROES");
        text = text.replace("FIBRINS", "FIBRINOES");
        text = text.replace("RTLICH", "OERTLICH");

        text = text.replace("SCIRRHSES", "SKIRRHOES");
        text = text.replace("PRIMAERS", "PRIMAERES");
        text = text.replace(" SOPHAGUS", " OESOPHAGUS");

        text = text.replace("MATS", "MATOES");
        text = text.replace("MASTOPHATIE", "MASTOPATHIE");

        text = text.replaceAll("RESEKTIONSFLAECHE( IST)?N? TUMORFREI", "RESEKTIONSFLAECHE TUMORFREI (R-0)");
        text = text.replaceAll("RESEKTIONSRAND( IST)? TUMORFREI", "RESEKTIONSRAND TUMORFREI (R-0)");
        text = text.replaceAll("RESEKTIONSRAENDER( SIND)? TUMORFREI", "RESEKTIONSRAENDER TUMORFREI (R-0)");
        text = text.replaceAll("KARZINOMFREIE? RESEKTIONSRAENDER", "KARZINOMFREIE RESEKTIONSRAENDER (R-0)");

        text = text.replaceAll("LYMPHKNOTEN( IST)?( SIND)? TUMORFREI", "LYMPHKNOTEN TUMORFREI (N-0)");
        text = text.replaceAll("LYMPHKNOTEN( IST)?( SIND)? METASTASENFREI", "LYMPHKNOTEN METASTASENFREI (N-0)");
        text = text.replaceAll("TUMORFREIE?R? LYMPHKNOTEN", "TUMORFREIE LYMPHKNOTEN (N-0)");
        text = text.replaceAll("METASTASENFREIE?R? LYMPHKNOTEN", "METASTASENFREIE LYMPHKNOTEN (N-0)");

        text = text.replaceAll("VENÖSER", "VENAESER");
        text = text.replaceAll("BLUTGEFÄßE", "BLUTGEFAESZE");
        text = text.replaceAll("RESEKTATRÄNDER", "RESEKTATRAENDER");
        text = text.replaceAll("AKTI- VIERUNG", "AKTIVIERUNG");
        text = text.replaceAll("BLUTGEF\\[~E", "BLUTGEFAESZE");
        text = text.replaceAll("DÜNNDARM", "DUENNDARM");
        text = text.replaceAll("RESEKTIONSRÄNDER", "RESEKTIONSRAENDER");
        text = text.replaceAll("GESCHÄDIGTES HAUTSTÜCK", "GESCHAEDIGTES HAUTSTUECK");
        text = text.replaceAll("AUSSCHLIEßBAR", "AUSSCHLIESZBAR");
        text = text.replaceAll("FREMDKÖRPERREAKTION", "FREMDKOERPERREAKTION");

        text = text.replaceAll("SCHILDDRÜßESCHILDDRÜßE", "SCHILDDRUESZESCHILDDRUESZE");
        text = text.replaceAll("SCHILDDRÜßE", "SCHILDDRUESZE");
        text = text.replaceAll("ÖSOPHAGUS", "OE�SOPHAGUS");
        text = text.replaceAll("GR~TEM", "GROESZTEM");
        text = text.replaceAll("FIBRSE", "FIRROESE");
        text = text.replaceAll("M\\[~ISIGGR", "MAESZIGGRADIG");
        text = text.replaceAll("M\\[~ISIGGR", "MAESZIGGRADIG");

        text = text.replaceAll("HEPATI- TISCHEM", "HEPATITISCHEM");
        text = text.replaceAll("HEPATI- TIS", "HEPATITIS");
        text = text.replaceAll("SCHILD- DRUESE", "SCHILDDRUESE");
        text = text.replaceAll("LYMPHADENI- TIS", "LYMPHADENITIS");
        text = text.replaceAll("VOLLREMISION", "VOLLREMISSION");
        text = text.replaceAll("VOLLMENISSION", "VOLLREMISSION");
        text = text.replaceAll("THROMBO- ZYTHAEMIE", "THROMBOZYTHAEMIE");

        text = text.replace(" Z.T. ", " ZUM TEIL ");
        text = text.replace(" Z. T. ", " ZUM TEIL ");

        text = text.replace(" Z.B: ", " ZUM BEISPIEL ");
        text = text.replace(" Z.B.: ", " ZUM BEISPIEL ");
        text = text.replace(" Z.B. ", " ZUM BEISPIEL ");

        text = text.replace(" Z.BSP: ", " ZUM BEISPIEL ");
        text = text.replace(" Z.BSP.: ", " ZUM BEISPIEL ");
        text = text.replace(" Z.BSP. ", " ZUM BEISPIEL ");

        // replace!!!!!
        text = text.replaceAll("OHNE AXILLAERE LYMPHKNOTENMETASTASEN", "<replaced text> (N-0)");
        text = text.replaceAll("OHNE LYMPHKNOTENMETASTASEN", "<replaced text> (N-0)");

        text = text.replace("T-O", "T-0"); // o => NULL
        text = text.replace("N-O", "N-0"); // o => NULL
        text = text.replace("M-O", "M-0"); // o => NULL
        text = text.replace("R-O", "R-0"); // o => NULL
        text = text.replace("G-O", "G-0"); // o => NULL

        text = text.replace("%IG", "PROZENTIG");
        text = text.replace("'", "\\'");

        text = text.toUpperCase();

        String q = "UPDATE " + table_ + " SET " + col_output_ + " = '" + text + "' WHERE " + row_id_ + " = " + Integer.toString(id) + ";";
        try {
            fout_.write(q);
            fout_.newLine();
        } catch (IOException ex) {
            System.err.println("correctFindingText::correctFindingString: " + ex.getMessage());
        }
    }

    /**
     * Class to execute updates from the generated file
     */
    public void upload() {
        System.out.print("Upload updateFindings.sql...");
        dbconnection_.updateFromFile("updateFindings.sql");
        System.out.print("Upload updateFindings.sql...done");
    }
}
