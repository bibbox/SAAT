/***********************************************
 * diseaseCreate.java
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
 * Create the diagnosis entys for the different organs
 ***********************************************
 */
package SAAT.textmining.corrector;

import java.sql.*;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;
import SAAT.generic.ConnectDataBase;
import SAAT.generic.PrintTime;

/**
 * Create the diagnosis entys for the different organs
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class diseaseCreate {

    /**
     * Variable decleration
     */
    ConnectDataBase dbcon_ = null;
    int DAYS_IN_DIAGNOSES = 62;
    HashSet<String> findings_;

    /** 
     * Creates a new instance of diseaseCreate and create a db connetion
     */
    public diseaseCreate() {
        dbcon_ = new ConnectDataBase();
        try {
            dbcon_.connectPostgresPropertie();
        } catch (PSQLException ex) {
            Logger.getLogger(diseaseCreate.class.getName()).log(Level.SEVERE, null, ex);
        }
        findings_ = new HashSet<String>();
    }

    /**
     * Starting the bring together of the organ diagnosis
     */
    public void doWork() {

        System.out.println("Start Combine");

        try {
            Connection test_connection = dbcon_.getConnection();
            Statement drops = test_connection.createStatement();
            drops.execute("DELETE FROM diseases;");
            drops.execute("SELECT setval('diseases2_disease_id_seq',1,false);");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println("MAMMA");
        uebertragen("organ LIKE '%MAMMA%' OR organ_zuordnung LIKE '%MAMMA%' OR organ LIKE '%AXILLA%'");

        System.out.println("PROSTATA");
        uebertragen("organ LIKE '%PROSTATA%' OR organ_zuordnung LIKE '%PROSTATA%'");

        System.out.println("LUNGE");
        uebertragen("organ LIKE '%LUNGE%' OR organ_zuordnung LIKE '%LUNGE%'");

        System.out.println("KOLON");
        uebertragen("organ LIKE '%KOLON%' OR organ_zuordnung LIKE '%KOLON%'");

        System.out.println("REKTUM");
        uebertragen("organ_zuordnung LIKE '%REKTUM%' OR organ LIKE '%REKTUM%' OR organ_zuordnung LIKE '%ANUS%' OR organ LIKE '%ANUS%'");

        System.out.println("HRANBLASE");
        uebertragen("organ LIKE '%HARNBLASE%' OR organ_zuordnung LIKE '%HARNBLASE%'");

        System.out.println("MAGEN");
        uebertragen("organ LIKE '%MAGEN%' OR organ_zuordnung LIKE '%MAGEN%'");

        System.out.println("PANKREAS");
        uebertragen("organ LIKE '%PANKREAS%' OR organ_zuordnung LIKE '%PANKREAS%' OR organ LIKE '%DICKDARM%' OR organ_zuordnung LIKE '%DICKDARM%'");

        System.out.println("NIERE");
        uebertragen("organ LIKE '%NIERE%' OR organ_zuordnung LIKE '%NIERE%'");

        System.out.println("OVAR");
        uebertragen("organ_zuordnung LIKE '%OVAR%'");

        System.out.println("UTERUS");
        uebertragen("organ LIKE '%UTERUS%'");

        System.out.println("LIPPE, etc");
        uebertragen("organ_zuordnung LIKE '%LIPPE%' OR organ LIKE '%LIPPE%' OR organ_zuordnung LIKE '%MUNDHOEHLE%' OR organ LIKE '%MUNDHOEHLE%' OR organ_zuordnung LIKE '%PHARYNX%' OR organ LIKE '%PHARYNX%' OR organ_zuordnung LIKE '%ZUNGE%' OR organ LIKE '%ZUNGE%' OR organ_zuordnung LIKE '%WANGENSCHLEIMHAUT%' OR organ LIKE '%WANGENSCHLEIMHAUT%' OR organ_zuordnung LIKE '%UNTERLIPPE%' OR organ LIKE '%UNTERLIPPE%' OR organ_zuordnung LIKE '%OBERLIPPE%' OR organ LIKE '%OBERLIPPE%' OR organ_zuordnung LIKE '%RACHEN%' OR organ LIKE '%RACHEN%' OR organ_zuordnung LIKE '%GAUMEN%' OR organ LIKE '%GAUMEN%'");

        System.out.println("MELANOM");
        uebertragen("organ_zuordnung LIKE '%MELANOM%' OR organ LIKE '%MELANOM%' OR ((organ_zuordnung LIKE '%HAUT%'    OR organ LIKE '%HAUT%'  ) AND diagnosis LIKE '%MELANOM%')");

        System.out.println("LYMPHKNOTEN");
        uebertragen("organ_zuordnung LIKE '%LYMPHKNOTEN%' OR organ LIKE '%LYMPHKNOTEN%' OR ((organ_zuordnung LIKE '%HAUT%'        OR organ LIKE '%HAUT%'        ) AND (diagnosis LIKE '%LYMPH%' OR diagnosis LIKE '%CHYCOSIS%FUNGOIDES%' OR diagnosis LIKE '%SEZARY%SYNDROM%')) OR ((organ_zuordnung LIKE '%KNOCHENMARK%' OR organ LIKE '%KNOCHENMARK%' ) AND (diagnosis LIKE '%LEUKAEMIE%' OR diagnosis LIKE '%PLASMOZYTOM%'))");

        System.out.println("KNOCHEN");
        uebertragen("organ_zuordnung LIKE '%KNOCHEN%' OR organ LIKE '%KNOCHEN%'");

        System.out.println("MUSKEL");
        uebertragen("organ_zuordnung LIKE '%MUSKEL%' OR organ LIKE '%MUSKEL%'");

        System.out.println("HERZ");
        uebertragen("organ_zuordnung LIKE '%HERZ%' OR organ LIKE '%HERZ%'");

        System.out.println("ZNS");
        uebertragen("organ_zuordnung LIKE '%ZNS%' OR organ LIKE '%ZNS%'");

        System.out.println("HARNWEGE");
        uebertragen("organ_zuordnung LIKE '%HARNWEGE%' OR organ LIKE '%HARNWEGE%'");

        System.out.println("LUNGE");
        uebertragen("organ_zuordnung LIKE '%LUNGE%' OR organ LIKE '%LUNGE%'");

        System.out.println("OESOPHAGUS");
        uebertragen("organ_zuordnung LIKE '%OESOPHAGUS%' OR organ LIKE '%OESOPHAGUS%'");

        System.out.println("SCHILDDRUESE");
        uebertragen("organ_zuordnung LIKE '%SCHILDDRUESE%' OR organ LIKE '%SCHILDDRUESE%'");

        System.out.println("LEBER");
        uebertragen("organ_zuordnung LIKE '%LEBER%' OR organ LIKE '%LEBER%'");

        System.out.println("APPENDIX");
        uebertragen("organ_zuordnung LIKE '%APPENDIX%' OR organ LIKE '%APPENDIX%'");

        System.out.println("Ende Combine");

        System.out.println("import rest");
        importRest();
    }

    /**
     * Generate the disease entrys in the diseases tabel
     *
     * @param subquery The subquery for the organ selection
     */
    private void uebertragen(String subquery) {
        try {
            Statement test_statment = dbcon_.getNewStatement();
            Statement insert_statment = dbcon_.getNewStatement();
            ResultSet test_result = test_statment.executeQuery("SELECT * FROM findings WHERE (" + subquery + ") ORDER BY patient_id, examination_date");
            Boolean first = true;
            Integer patienten_id = 0;
            Integer ref_patienten_id = 0;
            java.util.Date udate;
            java.util.Date ref_udate = new GregorianCalendar(0000, 00, 00, 00, 00).getTime();
            String finding_id = "";
            int age = 0;
            String unum = "";
            int es_anz = 0;
            String Zeile = "";
            String utyp = "";
            String organ = "";
            String doctor = "";
            String sender = "";
            String diagnose_enter = "";
            String organ_zuordnung = "";
            String sex = "";
            String specimen = "";
            System.out.println("Abfrage gestartet");
            int test = 0;
            int test1 = 0;
            int test2 = 0;
            int test3 = 0;
            int test4 = 0;
            int time_diff = 0;
            long diff = 0;
            while (test_result.next()) {
                // get information
                test++;

                if (first) {
                    if (findings_.contains(test_result.getString("finding_id"))) {
                        break;
                    }
                    patienten_id = test_result.getInt("patient_id");
                    udate = test_result.getDate("examination_date");
                    ref_patienten_id = test_result.getInt("patient_id");
                    ref_udate = test_result.getDate("examination_date");

                    finding_id = test_result.getString("finding_id");
                    findings_.add(finding_id);
                    age = test_result.getInt("age");
                    unum = test_result.getString("examination_number");
                    es_anz = test_result.getInt("es_anz");
                    Zeile = test_result.getString("diagnosis");
                    utyp = test_result.getString("examination_type");
                    organ = test_result.getString("organ");
                    doctor = test_result.getString("doctor");
                    sender = test_result.getString("sender");
                    organ_zuordnung = test_result.getString("organ_zuordnung");
                    diagnose_enter = test_result.getString("diagnosis");
                    sex = test_result.getString("sex");
                    specimen = test_result.getString("specimen");
                } else {
                    patienten_id = test_result.getInt("patient_id");
                    udate = test_result.getDate("examination_date");
                }

                // information processing
                if (patienten_id.intValue() == ref_patienten_id.intValue()) {
                    test1++;
                    diff = (udate.getTime() - ref_udate.getTime()) / (1000 * 60 * 60 * 24);
                    if (diff <= DAYS_IN_DIAGNOSES) { // Allowable diagnostic range in days
                        time_diff = (int) diff;
                        if (first) {
                            first = false;
                        } else {
                            test4++;
                            // belong together
                            if (findings_.contains(test_result.getString("finding_id"))) {
                                break;
                            }
                            finding_id = finding_id + "," + test_result.getString("finding_id");
                            findings_.add(test_result.getString("finding_id"));
                            unum = unum + "," + test_result.getString("examination_number");
                            es_anz = es_anz + test_result.getInt("es_anz");
                            Zeile = Zeile + "\n" + test_result.getString("diagnosis");
                            utyp = utyp + "\n" + test_result.getString("examination_type");
                            organ = organ + "\n" + test_result.getString("organ");
                            doctor = doctor + "\n" + test_result.getString("doctor");
                            sender = sender + "\n" + test_result.getString("sender");
                            organ_zuordnung = organ_zuordnung + "\n" + test_result.getString("organ_zuordnung");
                            diagnose_enter = diagnose_enter + "\n-----\n" + test_result.getString("diagnosis");
                        }
                    } else {
                        test3++;

                        String de = diagnose_enter.replace("'", "\\'");
                        String org = "";
                        if (organ != null) {
                            org = organ.replace("'", "\\'");
                        }
                        String orz = "";
                        if (organ_zuordnung != null) {
                            orz = organ_zuordnung.replace("'", "\\'");
                        }
                        insert_statment.execute("INSERT INTO diseases (patient_id, time_diff, diagnosis, finding_id_diagnosen, organ, organ_zuordnung, age, sex, specimen) VALUES ('" + ref_patienten_id + "', '" + time_diff + "', '" + de + "', '" + finding_id + "', '" + org + "', '" + orz + "', '" + age + "', '" + sex + "', '" + specimen + "')");

                        // not belong to
                        ref_udate = udate;
                        ref_patienten_id = test_result.getInt("patient_id");
                        // delete
                        finding_id = "";
                        age = 0;
                        unum = "";
                        es_anz = 0;
                        Zeile = "";
                        utyp = "";
                        organ = "";
                        doctor = "";
                        sender = "";
                        organ_zuordnung = "";
                        diagnose_enter = "";

                        if (findings_.contains(test_result.getString("finding_id"))) {
                            break;
                        }
                        finding_id = test_result.getString("finding_id");
                        findings_.add(test_result.getString("finding_id"));
                        age = test_result.getInt("age");
                        unum = test_result.getString("examination_number");
                        es_anz = test_result.getInt("es_anz");
                        Zeile = test_result.getString("diagnosis");
                        utyp = test_result.getString("examination_type");
                        organ = test_result.getString("organ");
                        doctor = test_result.getString("doctor");
                        sender = test_result.getString("sender");
                        organ_zuordnung = test_result.getString("organ_zuordnung");
                        diagnose_enter = test_result.getString("diagnosis");
                        sex = test_result.getString("sex");
                        specimen = test_result.getString("specimen");
                        time_diff = 0;

                    }
                } else {
                    test2++;

                    String de = diagnose_enter.replace("'", "\\'");
                    String org = "";
                    if (organ != null) {
                        org = organ.replace("'", "\\'");
                    }
                    String orz = "";
                    if (organ_zuordnung != null) {
                        orz = organ_zuordnung.replace("'", "\\'");
                    }
                    try {
                        insert_statment.execute("INSERT INTO diseases (patient_id, time_diff, diagnosis, finding_id_diagnosen, organ, organ_zuordnung, age, sex, specimen) VALUES ('" + ref_patienten_id + "', '" + time_diff + "', '" + de + "', '" + finding_id + "', '" + org + "', '" + orz + "', '" + age + "', '" + sex + "', '" + specimen + "');");
                    } catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                    }
                    // not belong to
                    ref_udate = udate;
                    ref_patienten_id = test_result.getInt("patient_id");

                    // delete
                    finding_id = "";
                    age = 0;
                    unum = "";
                    es_anz = 0;
                    Zeile = "";
                    utyp = "";
                    organ = "";
                    doctor = "";
                    sender = "";
                    organ_zuordnung = "";
                    diagnose_enter = "";

                    if (findings_.contains(test_result.getString("finding_id"))) {
                        break;
                    }
                    finding_id = test_result.getString("finding_id");
                    findings_.add(test_result.getString("finding_id"));
                    age = test_result.getInt("age");
                    unum = test_result.getString("examination_number");
                    es_anz = test_result.getInt("es_anz");
                    Zeile = test_result.getString("diagnosis");
                    utyp = test_result.getString("examination_type");
                    organ = test_result.getString("organ");
                    doctor = test_result.getString("doctor");
                    sender = test_result.getString("sender");
                    organ_zuordnung = test_result.getString("organ_zuordnung");
                    diagnose_enter = test_result.getString("diagnosis");
                    sex = test_result.getString("sex");
                    specimen = test_result.getString("specimen");
                    time_diff = 0;
                }
            }
            String de = diagnose_enter.replace("'", "\\'");
            String org = "";
            if (organ != null) {
                org = organ.replace("'", "\\'");
            }
            String orz = "";
            if (organ_zuordnung != null) {
                orz = organ_zuordnung.replace("'", "\\'");
            }
            try {
                if (!(ref_patienten_id == 0 && time_diff == 0 && de.equals("") && finding_id.equals("") && org.equals("") && orz.equals("") && age == 0)) {
                    insert_statment.execute("INSERT INTO diseases (patient_id, time_diff, diagnosis, finding_id_diagnosen, organ, organ_zuordnung, age, sex, specimen) VALUES ('" + ref_patienten_id + "', " + time_diff + ", '" + de + "', '" + finding_id + "', '" + org + "', '" + orz + "', '" + age + "', '" + sex + "', '" + specimen + "');");
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }

            System.out.println(test + " = " + test1 + " - " + test2 + " - " + test3 + " - " + test4);
            System.out.println("Fertig");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Import all not jet imported
     */
    private void importRest() {
        try {
            Statement st = dbcon_.getNewStatement();
            Statement insert_statment = dbcon_.getNewStatement();

            HashSet<Integer> finding_ids = new HashSet<Integer>();

            new PrintTime(false);
            System.out.println(String.format("read disease DB"));
            ResultSet result1 = st.executeQuery("SELECT finding_id_diagnosen FROM diseases;");
            while (result1.next()) {
                String fids = result1.getString("finding_id_diagnosen");

                if (!fids.equals("")) {
                    String[] fidsa = fids.split(",");
                    if (fidsa.length == 0) {
                        fidsa = new String[1];
                        fidsa[0] = fids;
                    }

                    // make subquery
                    for (int i = 0; i < fidsa.length; i++) {
                        if (fidsa[i].equals("")) {
                            break;
                        }
                        int fidi = Integer.parseInt(fidsa[i]);

                        if (!finding_ids.contains(fidi)) {
                            finding_ids.add(fidi);
                        }
                    }

                }
            }

            boolean stop = false;
            int start = 0;
            int increment = 10000;

            while (!stop) {
                new PrintTime(false);
                System.out.println(String.format("read findings DB from %7d to %7d", start, start + increment));
                ResultSet result = st.executeQuery("SELECT finding_id FROM findings WHERE finding_id > " + Integer.toString(start) + " AND finding_id < " + Integer.toString(start + increment) + " ORDER BY finding_id;");

                new PrintTime(false);
                System.out.println("start updating...");

                int counter = 0;
                int counter2 = 0;

                while (result.next()) {
                    int fid = result.getInt("finding_id");

                    if (!finding_ids.contains(fid)) {
                        insert_statment.execute("INSERT INTO diseases (patient_id, finding_id_diagnosen, age, diagnosis, diagnosis_clean, organ, organ_zuordnung, sex, specimen) (SELECT patient_id, finding_id, age, diagnosis, diagnosis_clean, organ, organ_zuordnung, sex, specimen FROM findings WHERE finding_id = " + fid + ");");
                        counter2++;
                    }
                    counter++;
                }

                new PrintTime(false);
                System.out.println(String.format("%d non indexed entrys found", counter2));

                start += increment;

                if (counter == 0) { // why cant i get the number of rows affected directly from ResultSet?
                    stop = true;
                    break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
