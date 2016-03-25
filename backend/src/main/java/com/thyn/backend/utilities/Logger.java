package com.thyn.backend.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class is used for logging information and errors.
 *
 * @author Angelo Agatino Nicolosi
 */
public class Logger {

    private static java.util.logging.Logger log = java.util.logging.Logger.getLogger("Yupik");

    public static void logInfo(String msg)
    {
        log.info("[" + getTimeStamp() + "] " + msg);
    }

    public static void logWarning(String msg)
    {
        log.warning("[" + getTimeStamp() + "] " + msg);
    }

    public static void logError(String msg, Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.severe("[" + getTimeStamp() + "] " + msg + "\n" + sw.toString());
    }

    private static String getTimeStamp()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        return sdf.format(date);
    }

}
