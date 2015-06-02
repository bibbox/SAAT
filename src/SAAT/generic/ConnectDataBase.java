/***********************************************
 * ConnectDataBase.java
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
 * The Database class for the project, connecting in differnt ways to
 * PostgreSQL or SQLite Databases. Executing Querys from file or in
 * a different thread.
 ***********************************************
 */
package SAAT.generic;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.postgresql.util.PSQLException;

/**
 * The Database class for the project, connecting in differnt ways to
 * PostgreSQL or SQLite Databases. Executing Querys from file or in
 * a different thread.
 * 
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class ConnectDataBase {

    /**
     * Variable decleration
     */
    // Connection to the database
    private Connection con_ = null;
    // Query Array for bulk execute of querys
    private String[] querys_ = null;
    // Thread Pool for query bulk execution
    private ExecutorService threadPool_ = Executors.newCachedThreadPool();

    /**
     * Connect to a PGSQL DB from property file
     *
     * Class to connect to a PostgreSQL Database using the Propertys stored
     * in the 'dbparam.properties' file in the root directory of the
     * project. Function reads the property file and extracts the username
     * password and db url. Calls the connectPostgres(String, String, String)
     * function with the parameters from the file.
     *
     * @throws PSQLException
     */
    public void connectPostgresPropertie() throws PSQLException {
        try {
            // read parameter-file
            FileInputStream in = new FileInputStream("dbparam.properties");
            Properties prop = new Properties();
            prop.load(in);
            in.close();

            // extract properties
            String url = prop.getProperty("url");
            String user = prop.getProperty("user");
            String password = prop.getProperty("password");

            // connect to the database
            connectPostgres(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println("ConnectDatabase::connectPropertie - IO Error: " + ex.getMessage());
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Connect to a PGSQL DB
     *
     * Class to connect to a PostgreSQL Database; prepairs the given data to
     * a property elements and calls the connectPostgres(String, Properties)
     * function.
     *
     * @param server The url of the server
     * @param user The DB username
     * @param password The DB password
     * @throws PSQLException
     */
    public void connectPostgres(String server, String user, String password) throws PSQLException {
        // prepairs the server url
        if (!server.contains("jdbc:postgresql:")) {
            server = String.format("jdbc:postgresql://%s", server);
        }

        // generats the Properties object
        final Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);

        // connect to the database
        connectPostgres(server, properties);
    }

    /**
     * Connect to a PGSQL DB with SSL option
     *
     * Class to connect to a PostgreSQL Database; prepairs the given data to
     * a property elements and calls the connectPostgres(String, Properties)
     * function.
     *
     * @param server The url of the server
     * @param user user The DB username
     * @param password The DB password
     * @param SSL The option for SSL connection
     * @throws PSQLException
     */
    public void connectPostgres(String server, String user, String password, Boolean SSL) throws PSQLException {
        // prepairs the server url
        if (!server.contains("jdbc:postgresql:")) {
            server = String.format("jdbc:postgresql://%s", server);
        }

        // generats the Properties object
        final Properties properties = new Properties();
        properties.put("user", user);
        properties.put("password", password);
        if (SSL) {
            properties.put("ssl", "true");
        } else {
            properties.put("ssl", "false");
        }

        // connect to the database
        connectPostgres(server, properties);
    }

    /**
     * Connect to a PGSQL DB with Properties object
     *
     * The main connection class for the PostgreSQL Database using the
     * url and the Properties to connect to the Database.
     *
     * @param server The url of the server
     * @param properties The property file for the connection
     * @throws PSQLException
     */
    public void connectPostgres(String server, Properties properties) throws PSQLException {
        // prepairs the server url
        if (!server.contains("jdbc:postgresql:")) {
            server = String.format("jdbc:postgresql://%s", server);
        }

        //set ssl if not uses to true, if definde as false remove it
        if (properties.getProperty("ssl") == null) {
            properties.put("ssl", "true");
        } else if (properties.getProperty("ssl") == "false") {
            properties.remove("ssl");
        }

        try {
            // Connect to the database
            Class.forName("org.postgresql.Driver");
            con_ = DriverManager.getConnection(server, properties);

            // Output the SSL option of the connection
            if (properties.getProperty("ssl") == null) {
                System.out.println("connected: SSL disabled");
            } else {
                System.out.println("connected: SSL enabled");
            }

            return;
        } catch (org.postgresql.util.PSQLException ex) {
            //System.err.println("ConnectDatabase::connectPostgres - PSQLException Error: " + ex.getMessage());
            //ex.printStackTrace();
            System.out.println("ConnectDatabase::connectPostgres - PSQLException Error: Der Server unterstützt SSL nicht.");
        } catch (SQLException e) {
            System.err.println("ConnectDatabase::connectPostgres - SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ConnectDatabase::connectPostgres - Error: " + e.getMessage());
            e.printStackTrace();
        }

        // if connection was not possible with SSL use without SSL
        try {
            properties.remove("ssl");

            Class.forName("org.postgresql.Driver");
            con_ = DriverManager.getConnection(server, properties);

            System.out.println("connected: SSL disabled (fallback)");

        } catch (org.postgresql.util.PSQLException ex) {
            System.err.println("ConnectDatabase::connectPostgres - PSQLException Error: " + ex.getMessage());
            //System.exit(-1);
            throw (ex);
        } catch (SQLException e) {
            System.err.println("ConnectDatabase::connectPostgres - SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ConnectDatabase::connectPostgres - Error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Connect to a SQLite DB
     * 
     * Class to connect to a SQLite Database
     *
     * @param filename The SQLite file
     * @param user The DB username
     * @param password The DB password
     */
    public void connectSQLite(String filename, String user, String password) {
        try {
            // lode driver
            Class.forName("org.sqlite.JDBC");
            // connect to the DB
            con_ = DriverManager.getConnection("jdbc:sqlite:" + filename, user, password);

        } catch (SQLException ex) {
            System.err.println("ConnectDatabase::connectSQLite - SQL Error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            System.err.println("ConnectDatabase::connectSQLite - ClassNotFound Error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.err.println("ConnectDatabase::connectSQLite - Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Methode for returning the connection Object.
     *
     * @return The connection object
     */
    public Connection getConnection() {
        return con_;
    }

    /**
     * Methode to generate a new Statemant
     *
     * @return A new DB statement
     * @throws SQLException
     */
    public Statement getNewStatement() throws SQLException {
        return con_.createStatement();
    }

    /**
     * Cloase the DB connection
     */
    public void closeConnection() {
        try {
            con_.close();
        } catch (Exception e) {
            System.err.println("ConnectDatabase::closeConnection - Error: " + e.getMessage());
            System.err.println(e);
        }
    }

    /**
     * File query update function
     * 
     * Function to run multipel querys from a file with error handling and 
     * bulk execution of the querys.
     *
     * @param fn The filename of the query file
     */
    public void updateFromFile(String fn) {
        int increment = 64; // only even values!

        // delete the error file from last run
        File f_old = new File(fn + "_err_" + Integer.toString(1) + ".sql");
        f_old.delete();

        // Update the first file and write the errors to the output file
        updateFromFile(fn, fn + "_err_" + Integer.toString(increment) + ".sql", increment);

        // Execute the error files untill the number of executed querys is 1
        for (int i = increment / 2; i >= 1; i /= 2) {
            updateFromFile(fn + "_err_" + Integer.toString(i * 2) + ".sql", fn + "_err_" + Integer.toString(i) + ".sql", i);
        }

        // delete error files except the last one with the error querys
        for (int i = increment; i > 1; i /= 2) {
            File f = new File(fn + "_err_" + Integer.toString(i) + ".sql");
            f.delete();
        }
    }

    /**
     * Bulk update funktion
     *
     * Methode to update queris from a file, with an error file where
     * querys with executions errors are writen.
     *
     * @param file
     * @param errfile
     * @param increment
     */
    private void updateFromFile(String file, String errfile, int increment) {
        try {
            // open the error file
            BufferedWriter ferr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errfile)));
            // generate new sql statement
            Statement st = getNewStatement();

            // display Error file and file to read
            System.out.println("Updating from file: " + file);
            System.out.println("writing errors in: " + errfile);

            // read the input file
            BufferedReader ifile = new BufferedReader(new FileReader(file));

            String updateq = new String();
            int counter_old = 0;
            int counter = 0;
            while (true) {
                // generate query bulk with defined increment
                String line = ifile.readLine();

                if (line == null) {
                    break;
                }

                if (counter != 0) {
                    updateq += "\r\n" + line;
                } else {
                    updateq += line;
                }

                ++counter;

                // update (execution) block
                if (counter % increment == 0) {
                    System.out.println(String.format("Updating %7d to %7d ", counter_old, counter));
                    try {
                        // execute query bulk
                        st.executeUpdate(updateq);
                    } catch (Exception ex) {
                        System.err.println(String.format("Error between %7d to %7d ", counter_old, counter));
                        // write query bulk tu error file if error occured
                        ferr.write(updateq);
                    }
                    counter_old = counter;
                    updateq = "";
                }
            }

            // update last incompled block
            if (!updateq.equals("")) {
                System.out.println(String.format("Updating %7d to %7d ", counter_old, counter));
                try {
                    // execute query bulk
                    st.executeUpdate(updateq);
                } catch (Exception ex) {
                    System.err.println(String.format("Error between %7d to %7d ", counter_old, counter));
                    // write query bulk tu error file if error occured
                    ferr.write(updateq);
                }
            }

            ferr.close();

        } catch (FileNotFoundException ex) {
            System.err.println("file not found: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Connection or file ERROR");
        }
    }

    /**
     * execution function witch executes an query array in an other
     * thread.
     * 
     * @param querys String array with the querys to execute
     */
    public void executeQuerys(String[] querys) {
        querys_ = querys;
        Runnable r = new Runnable() {
            public void run() {
                executeQuerysThread();
            };
        };
        executeQuerysThread();
    }

    /**
     * Executes the querys given by an array asyncron in an other thread
     */
    private void executeQuerysThread() {
        if (querys_ == null) {
            return;
        }

        String[] querys = querys_.clone();
        try {
            Statement st = getNewStatement();

            for (int i = 0; i < querys.length; ++i) {
                try {
                    st.execute(querys[i]);
                } catch (SQLException ex) {
                    String msg = ex.getMessage();
                    if (!msg.equals("General error")) {
                        System.err.println(msg);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Returns if the connection is opend or not.
     *
     * @return If a connection is opend
     */
    public boolean isConnected() {
        if (con_ != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public PreparedStatement getPreparedStatement(String query) {
        try {
            PreparedStatement ps = con_.prepareStatement(query);
            return ps;
        } catch (SQLException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void setAutocommit(boolean commit) {
        try {     
            con_.setAutoCommit(commit);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void commit() {
        try {
            con_.commit();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
