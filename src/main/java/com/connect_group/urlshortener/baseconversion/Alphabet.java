package com.connect_group.urlshortener.baseconversion;

/**
 * Abstract alphabet so we can have different search (indexOf) 
 * algorithms depending on properties of the alphabet.
 */
public interface Alphabet {
    char charAt(int index);
    int indexOf(char c);
    int length();
}
