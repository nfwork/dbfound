
package com.nfwork.dbfound.csv;

/**
 * Constants for this package.
 */
final class Constants {

    static final char BACKSLASH = '\\';

    static final char BACKSPACE = '\b';

    static final String COMMA = ",";

    /**
     * Starts a comment, the remainder of the line is the comment.
     */
    static final char COMMENT = '#';

    static final char CR = '\r';

    /** RFC 4180 defines line breaks as CRLF */
    static final String CRLF = "\r\n";

    static final Character DOUBLE_QUOTE_CHAR = Character.valueOf('"');

    static final String EMPTY = "";

    static final String[] EMPTY_STRING_ARRAY = {};

    /** The end of stream symbol */
    static final int END_OF_STREAM = -1;

    static final char FF = '\f';

    static final char LF = '\n';

    /**
     * Unicode line separator.
     */
    static final String LINE_SEPARATOR = "\u2028";

    /**
     * Unicode next line.
     */
    static final String NEXT_LINE = "\u0085";

    /**
     * Unicode paragraph separator.
     */
    static final String PARAGRAPH_SEPARATOR = "\u2029";

    static final char PIPE = '|';

    /** ASCII record separator */
    static final char RS = 30;

    static final char SP = ' ';

    static final String SQL_NULL_STRING = "\\N";

    static final char TAB = '\t';

    /** Undefined state for the lookahead char */
    static final int UNDEFINED = -2;

    /** ASCII unit separator */
    static final char US = 31;

    /** No instances. */
    private Constants() {
        // noop
    }

}
