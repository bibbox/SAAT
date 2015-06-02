/***********************************************
 * DictionaryData.java
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
 * DESCRIPTIONDESCRIPTIONDESCRIPTIONDESCRIPTION
 ***********************************************
 */
package SAAT.textmining.icd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * DataStore for a DictionaryNode
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
class DictionaryData {

    /**
     * Variable decleration
     */
    public int dictionary_id_ = 0;
    public String synonym_ = "";
    public int before_synonym_ = 0;
    public int after_synonym_ = 0;
    public boolean foreword_ = false;
    public boolean ending_ = false;
    public boolean sentence_ = false;
    public boolean iscode_ = false;
    public String pattern_ = "";
    public String code_typ_ = "";
    public String code_value_ = "";
    public boolean root_ = false;
    public boolean negation_ = false;
    public boolean occur_ = false;
    public int priority_ = 0;
    public boolean priority_mode_ = false;
    public boolean dirty_ = false;
    public ArrayList<Integer> addiction_;
    public GeneralNegationRule general_negation_rules_ = null;
    public java.util.regex.Pattern pattern_compile_;
    public ArrayList<Integer> childs_;
    public Set matching_disease_ids_;
    private SAAT.generic.ConnectDataBase db_;

    /**
     * Creates a new instance of DictionaryData with
     * @param db
     */
    public DictionaryData(SAAT.generic.ConnectDataBase db) {
        db_ = db;
        childs_ = new ArrayList<Integer>();
        addiction_ = new ArrayList<Integer>();
        if (matching_disease_ids_ == null) {
            matching_disease_ids_ = Collections.synchronizedSet(new HashSet());
        }
    }

    /**
     *
     */
    public DictionaryData() {
        db_ = new SAAT.generic.ConnectDataBase();
        childs_ = new ArrayList<Integer>();
        addiction_ = new ArrayList<Integer>();
        if (matching_disease_ids_ == null) {
            matching_disease_ids_ = Collections.synchronizedSet(new HashSet());
        }
    }

    /**
     * This Constructor will load the Dictionary data himself
     * USE CAREFULLY - slow as hell if it's used to build the complete tree
     *
     * @param dictionary id
     */
    public DictionaryData(int dictionary_id) {
        db_ = new SAAT.generic.ConnectDataBase();
        childs_ = new ArrayList<Integer>();
        addiction_ = new ArrayList<Integer>();
        if (matching_disease_ids_ == null) {
            matching_disease_ids_ = Collections.synchronizedSet(new HashSet());
        }

        try {
            //db_.connectPostgres();
            db_.connectPostgresPropertie();
            java.sql.Statement stat1 = db_.getNewStatement();

            java.sql.ResultSet result1 = stat1.executeQuery("SELECT * from dictionary2 WHERE dictionary_id = " + Integer.toString(dictionary_id) + ";");

            while (result1.next()) {
                parseResultSet(result1);
            }
            result1.close();

            //don't load childs_ if this is a code field
            if (iscode_ == true) {
                return;
            }

            java.sql.Statement stat2 = db_.getNewStatement();
            java.sql.ResultSet result2 = stat2.executeQuery("SELECT dictionary_id from dictionary2_dictionary2_addiction_link WHERE addiction_id = " + Integer.toString(dictionary_id) + ";");
            while (result2.next()) {
                childs_.add(result2.getInt("dictionary_id"));
            }

            result2.close();

        } catch (SQLException ex) {
            System.err.println("SQLException in DictionaryData::DictionaryData" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Parse a given Resultset and load the data
     * Should only be used by a DictionaryNode
     *
     * @param Resultset; defined like in Construktor
     */
    public void parseResultSet(java.sql.ResultSet result1) throws SQLException {
        dictionary_id_ = result1.getInt("dictionary_id");
        //-------

        //ERROR PROBLEM NO CONNECTION ??????
    /*
        if(!db_.isConnected()){
        String crypted_string = null;
        String connection_string = null;
        try {
        FileInputStream fstream = new FileInputStream("dictserver.key");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
        crypted_string = strLine;
        }
        //Close the input stream
        in.close();
        } catch (IOException ex) {
        ex.printStackTrace();
        }

        try {
        StringEncrypter encrypter;
        encrypter = new StringEncrypter();
        connection_string = encrypter.decrypt(crypted_string);
        } catch (StringEncrypter.EncryptionException ex) {
        ex.printStackTrace();
        }

        String[] split = connection_string.split(",");
        //System.out.println("db_.connectPostgres(" + split[0] + ":" + split[1] + "/" + split[2] +","+ split[3] +","+ split[4] + ");");
        db_.connectPostgres(split[0] + ":" + split[1] + "/" + split[2], split[3], split[4]);

        //db_.connectPostgresPropertie();
        //db_.connectPostgres();
        }*/
        java.sql.Statement stat2 = db_.getNewStatement();
        java.sql.ResultSet result2 = stat2.executeQuery("SELECT addiction_id from dictionary2_dictionary2_addiction_link WHERE dictionary_id = " + dictionary_id_ + ";");
        while (result2.next()) {
            addiction_.add(result2.getInt("addiction_id"));
        }
        //-------
        synonym_ = result1.getString("synonym");
        before_synonym_ = result1.getInt("before_synonym");
        after_synonym_ = result1.getInt("after_synonym");
        foreword_ = result1.getString("foreword").startsWith("t"); // sqlite lib dont support getBoolean
        ending_ = result1.getString("ending").startsWith("t"); // sqlite lib dont support getBoolean
        sentence_ = result1.getString("sentence").startsWith("t"); // sqlite lib dont support getBoolean
        iscode_ = result1.getString("iscode").startsWith("t"); // sqlite lib dont support getBoolean
        pattern_ = result1.getString("pattern");
        code_typ_ = result1.getString("code_typ");
        code_value_ = result1.getString("code_value");
        root_ = result1.getString("root").startsWith("t"); // sqlite lib dont support getBoolean
        negation_ = result1.getString("negation").startsWith("t"); // sqlite lib dont support getBoolean
        occur_ = result1.getString("occur").startsWith("t"); // sqlite lib dont support getBoolean
        priority_ = result1.getInt("priority");
        priority_mode_ = result1.getString("priority_mode").startsWith("t"); // sqlite lib dont support getBoolean
        dirty_ = result1.getString("dirty").startsWith("t"); // sqlite lib dont support getBoolean

        // sqlite lib corrections
        // pg     returns  null
        // sqlite returns  ""
        if (code_typ_ != null) {
            if (code_typ_.equals("null")) {
                code_typ_ = null;
            }
        }
        if (synonym_ != null) {
            if (synonym_.equals("null")) {
                synonym_ = null;
            }
        }
        if (pattern_ != null) {
            if (pattern_.equals("null")) {
                pattern_ = null;
            }
        }
        if (code_value_ != null) {
            if (code_value_.equals("null")) {
                code_value_ = null;
            }
        }
        if (code_value_ != null) {
            if (code_value_.equals("null")) {
                code_value_ = null;
            }
        }
        // end of corrections

        if (!dirty_) {
            String did = result1.getString("disease_ids");
            if (did != null) {
                if (!dirty_) {
                    //this is quite slow....
          /*
                    String[] t = did.split(";");
                    for(String tt : t)
                    if(!tt.equals(""))
                    matching_disease_ids_.add(Integer.parseInt(tt));
                     **/
                }
            }
        }

        if (pattern_ != null) {
            pattern_compile_ = java.util.regex.Pattern.compile(pattern_);
        }

        if (root_) {
            occur_ = true;
        }
    }

    /**
     * This gives back Update Query for the dictionary.
     * The Matched disease ids and the number of them will be stored.
     * Also the dirty_ flag will be set back
     */
    public String getSaveString() {
        String did = "'";

        //java.util.Collections.sort(matching_disease_ids_);

        Iterator it = matching_disease_ids_.iterator();
        while (it.hasNext()) {
            Integer i = (Integer) it.next();
            did += ";" + Integer.toString(i);
        }
        did += "'";

        if (did.equals("''")) {
            did = "null";
        }

        String q = "UPDATE dictionary2 SET dirty = false, disease_ids = " + did + ", disease_ids_count = " + Integer.toString(matching_disease_ids_.size()) + " WHERE dictionary_id = " + dictionary_id_ + ";";

        return q;
    }

    /**
     *
     * @param data_
     * @return
     */
    public int compareTo(DictionaryData data_) {
        if (data_.dictionary_id_ != dictionary_id_) {
            return -1;
        }
        //-------
        if (data_.synonym_ != null && synonym_ != null) {
            if (!data_.synonym_.equals(synonym_)) {
                return -1;
            }
        } else if ((data_.synonym_ != null && synonym_ == null) || (data_.synonym_ == null && synonym_ != null)) {
            return -1;
        }
        //-------
        if (data_.before_synonym_ != before_synonym_) {
            return -1;
        }
        if (data_.after_synonym_ != after_synonym_) {
            return -1;
        }
        if (data_.foreword_ != foreword_) {
            return -1;
        }
        if (data_.ending_ != ending_) {
            return -1;
        }
        if (data_.sentence_ != sentence_) {
            return -1;
        }
        if (data_.iscode_ != iscode_) {
            return -1;
        }
        if (data_.addiction_ != null && addiction_ != null) {
            if (data_.addiction_.equals(addiction_)) {
                return -1;
            }
        }
        //-------
        if (data_.pattern_ != null && pattern_ != null) {
            if (!data_.pattern_.equals(pattern_)) {
                return -1;
            }
        } else if ((data_.pattern_ != null && pattern_ == null) || (data_.pattern_ == null && pattern_ != null)) {
            return -1;
        }
        //-------
        if (data_.code_typ_ != null && code_typ_ != null) {
            if (!data_.code_typ_.equals(code_typ_)) {
                return -1;
            }
        } else if ((data_.code_typ_ != null && code_typ_ == null) || (data_.code_typ_ == null && code_typ_ != null)) {
            return -1;
        }
        //-------
        if (data_.code_value_ != null && code_value_ != null) {
            if (!data_.code_value_.equals(code_value_)) {
                return -1;
            }
        } else if ((data_.code_value_ != null && code_value_ == null) || (data_.code_value_ == null && code_value_ != null)) {
            return -1;
        }
        //-------
        if (data_.root_ != root_) {
            return -1;
        }
        if (data_.negation_ != negation_) {
            return -1;
        }
        if (data_.occur_ != occur_) {
            return -1;
        }
        if (data_.dirty_ != dirty_) {
            return -1;
        }
        if (data_.priority_ != priority_) {
            return -1;
        }
        if (data_.priority_mode_ != priority_mode_) {
            return -1;
        }
        return 0;
    }

    /**
     *
     * @return
     */
    public String getSqlInsertQuery() {
        String query = "INSERT INTO dictionary2 ("
                + "dictionary_id, synonym, before_synonym, after_synonym, foreword, "
                + "ending, sentence, iscode, pattern, code_typ, code_value, "
                + "root, negation, occur, dirty, priority, "
                + "priority_mode) VALUES ( "
                + dictionary_id_ + ", '" + synonym_ + "', '" + before_synonym_ + "', '" + after_synonym_ + "', '" + foreword_ + "', '"
                + ending_ + "', '" + sentence_ + "', '" + iscode_ + "', '" + pattern_ + "', '" + code_typ_ + "', '" + code_value_ + "', '"
                + root_ + "', '" + negation_ + "', '" + occur_ + "', '" + dirty_ + "', '" + priority_ + "', '"
                + priority_mode_ + "');";
        for (Integer ad : addiction_) {
            query += "INSERT INTO dictionary2_dictionary2_addiction_link ("
                    + "dictionary_id, addiction_id) VALUES ("
                    + dictionary_id_ + ", " + ad + ");";
        }
        return query;
    }

    /**
     * 
     * @return
     */
    public String getSqlUpdateQuery() {
        String query = "DELETE FROM dictionary2_dictionary2_addiction_link WHERE dictionary_id = " + dictionary_id_ + ";";
        query += "UPDATE dictionary2 SET "
                + "synonym='" + synonym_ + "', before_synonym='" + before_synonym_ + "', after_synonym='" + after_synonym_ + "', "
                + "foreword='" + foreword_ + "', ending='" + ending_ + "', sentence='" + sentence_ + "', iscode='" + iscode_ + "', addiction='" + addiction_ + "', pattern='" + pattern_ + "', "
                + "code_typ='" + code_typ_ + "', code_value='" + code_value_ + "', root='" + root_ + "', negation='" + negation_ + "', occur='" + occur_ + "', "
                + "dirty='" + dirty_ + "', priority='" + priority_ + "', priority_mode='" + priority_mode_
                + "' WHERE dictionary_id = " + dictionary_id_ + " AND addiction = " + addiction_ + ";";
        for (Integer ad : addiction_) {
            query += "INSERT INTO dictionary2_dictionary2_addiction_link ("
                    + "dictionary_id, addiction_id) VALUES ("
                    + dictionary_id_ + ", " + ad + ");";
        }
        return query;
    }
}
