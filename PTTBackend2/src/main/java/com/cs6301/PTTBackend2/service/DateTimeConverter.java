package com.cs6301.PTTBackend2.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class DateTimeConverter {
    private final static DateTimeFormatter isoDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm'Z'");
    private final static DateTimeFormatter sqlDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");

    public static Timestamp toSQLTimestampUTC(String isoDateTime) {
        DateTime utcTime = isoDateTimeFormatter.parseDateTime(isoDateTime).toDateTime(DateTimeZone.UTC);
        return new Timestamp(utcTime.toDateTime().getMillis());
    }

    public static String getISO8601TimeZone(String isoDateTime) {
        return isoDateTimeFormatter.parseDateTime(isoDateTime).getZone().getID();
    }

    public static String toISO8601(Timestamp sqlTimestamp, String timeZone) {
        DateTime utcTime = sqlDateTimeFormatter.parseDateTime(sqlTimestamp.toString()).withZone(DateTimeZone.UTC);
        DateTime localTime = utcTime.withZone(DateTimeZone.forID(timeZone));
        return isoDateTimeFormatter.print(localTime);
    }
}
