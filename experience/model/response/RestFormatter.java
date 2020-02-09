package com.td.dcts.eso.experience.model.response;

public class RestFormatter {

    public static final String INFO = "Info";
    public static final String WARNING = "Warning";
    public static final String ERROR = "Error";

    public static String mapSeverity(SeverityLevel severity) {
        if (SeverityLevel.INFO.equals(severity)) {
            return INFO;
        }
        if (SeverityLevel.WARNING.equals(severity)) {
            return WARNING;
        }
        if (SeverityLevel.ERROR.equals(severity)) {
            return ERROR;
        }
        return "";
    }

}
