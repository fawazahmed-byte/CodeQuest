package com.codequest.util;

/**
 * Module 12 - Integration (used by every JSP)
 *
 * Helper methods for safely printing user-entered text in JSP pages.
 * esc() replaces special HTML characters so that text such as
 * "<script>" is shown as plain text instead of breaking the page.
 */
public class HtmlUtil {

    /**
     * Escapes the characters < > & " ' for safe HTML output.
     * Returns an empty string for null.
     */
    public static String esc(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<':  out.append("&lt;");   break;
                case '>':  out.append("&gt;");   break;
                case '&':  out.append("&amp;");  break;
                case '"':  out.append("&quot;"); break;
                case '\'': out.append("&#39;");  break;
                default:   out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Returns true if the string is null or only spaces.
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
