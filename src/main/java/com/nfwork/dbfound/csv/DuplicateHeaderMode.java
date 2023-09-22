
package com.nfwork.dbfound.csv;

/**
 * Determines how duplicate header fields should be handled
 * if {@link CSVFormat.Builder#setHeader(Class)} is not null.
 *
 * @since 1.10.0
 */
public enum DuplicateHeaderMode {

    /**
     * Allows all duplicate headers.
     */
    ALLOW_ALL,

    /**
     * Allows duplicate headers only if they're empty, blank, or null strings.
     */
    ALLOW_EMPTY,

    /**
     * Disallows duplicate headers entirely.
     */
    DISALLOW
}
