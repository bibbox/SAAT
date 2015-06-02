/***********************************************
 * PrintTime.java
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
 * Class to print standart time for output and logging, also logs the time
 * between the printings or marked printings.
 ***********************************************
 */
package SAAT.generic;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class to print standart time for output and logging, also logs the time
 * between the printings or marked printings.
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class PrintTime {

    /**
     * Variable decleration
     */
    // The time of the last printing
    private static GregorianCalendar lastPrint_;
    // The time of the last marked printing
    private static GregorianCalendar lastPrintMarked_;

    public PrintTime(boolean b) {
        GregorianCalendar cal = new GregorianCalendar();     // Default-TimeZone und -Lo
        int jahr = cal.get(Calendar.YEAR);                   // aktueller Jahr (1970-...)
        int month = cal.get(Calendar.MONTH) + 1;             // aktueller Monat (1-12)
        int tag = cal.get(Calendar.DAY_OF_MONTH);            // aktueller Tag (1-31)
        int stunden = cal.get(Calendar.HOUR_OF_DAY);         // aktuelle Stunde (0-24)
        int minuten = cal.get(Calendar.MINUTE);              // aktuelle Minuten (0-59)
        int sekunden = cal.get(Calendar.SECOND);             // aktuelle Sekunden (0-59)
        //int milli_sekunden = cal.get(Calendar.MILLISECOND);
        // aktuelle Millisekunden (0-999)
        String datetime = "";

        if (lastPrint_ == null) {
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - %6dms || ", tag, month, jahr, stunden, minuten, sekunden, 0);
        } else {
            long diff = cal.getTimeInMillis() - lastPrint_.getTimeInMillis();
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - %6dms || ", tag, month, jahr, stunden, minuten, sekunden, diff);
        }

        if (b && lastPrintMarked_ == null) {
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - Marked %6dms || ", tag, month, jahr, stunden, minuten, sekunden, 0);
        } else if (b) {
            long diff = cal.getTimeInMillis() - lastPrintMarked_.getTimeInMillis();
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - Marked %6dms || ", tag, month, jahr, stunden, minuten, sekunden, diff);
        }

        lastPrint_ = cal;
        if (b) {
            lastPrintMarked_ = cal;
        }
    }

    public PrintTime() {
    }

    /**
     *
     * @param mark Defines if the call is marked or nonmarked
     * @return The time string
     */
    public String getTime(boolean mark) {
        GregorianCalendar cal = new GregorianCalendar();     // Default-TimeZone und -Lo
        int jahr = cal.get(Calendar.YEAR);                   // aktueller Jahr (1970-...)
        int month = cal.get(Calendar.MONTH) + 1;             // aktueller Monat (1-12)
        int tag = cal.get(Calendar.DAY_OF_MONTH);            // aktueller Tag (1-31)
        int stunden = cal.get(Calendar.HOUR_OF_DAY);         // aktuelle Stunde (0-24)
        int minuten = cal.get(Calendar.MINUTE);              // aktuelle Minuten (0-59)
        int sekunden = cal.get(Calendar.SECOND);             // aktuelle Sekunden (0-59)
        //int milli_sekunden = cal.get(Calendar.MILLISECOND);
        // aktuelle Millisekunden (0-999)
        String datetime = "";

        if (lastPrint_ == null) {
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - %6dms || ", tag, month, jahr, stunden, minuten, sekunden, 0);
        } else {
            long diff = cal.getTimeInMillis() - lastPrint_.getTimeInMillis();
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - %6dms || ", tag, month, jahr, stunden, minuten, sekunden, diff);
        }

        if (mark && lastPrintMarked_ == null) {
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - Marked %6dms || ", tag, month, jahr, stunden, minuten, sekunden, 0);
        } else if (mark) {
            long diff = cal.getTimeInMillis() - lastPrintMarked_.getTimeInMillis();
            datetime = String.format("%2d.%2d.%4d  %2d:%2d:%2d - Marked %6dms || ", tag, month, jahr, stunden, minuten, sekunden, diff);
        }

        lastPrint_ = cal;
        if (mark) {
            lastPrintMarked_ = cal;
        }

        return datetime;
    }
}
