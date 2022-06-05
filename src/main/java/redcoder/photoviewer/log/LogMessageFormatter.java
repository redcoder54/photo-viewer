package redcoder.photoviewer.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

public class LogMessageFormatter extends Formatter {

    private final static String DEFAULT_FORMAT = "%1$tF %1$tT.%1$tL %2$s %3$s --- [%4$s] %5$s : %6$s%7$s%n";
    private String format = DEFAULT_FORMAT;
    private final Date dat = new Date();

    public LogMessageFormatter() {
        String value = LogManager.getLogManager().getProperty("redcoder.photoviewer.log.LogMessageFormatter.format");
        if (value != null) {
            format = value;
        }
    }

    @Override
    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());
        String tid = String.valueOf(Thread.currentThread().getId());
        String tname = Thread.currentThread().getName();
        String message = formatMessage(record);
        String throwable = throwableToString(record.getThrown());
        return String.format(format,
                dat,
                record.getLevel(),
                tid,
                tname,
                record.getLoggerName(),
                message,
                throwable);
    }

    private String throwableToString(Throwable throwable) {
        String str = "";
        if (throwable != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            throwable.printStackTrace(pw);
            pw.close();
            str = sw.toString();
        }
        return str;
    }
}
