package com.perry.urlshortener.baseconversion;

/**
 * Unordered Alphabets have an O(n) search efficiency when
 * converting from string back to a number.
 */
public class UnorderedAlphabet implements Alphabet {
    private final String alphabet;
    
    public UnorderedAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }

    @Override
    public final char charAt(int index) {
        return alphabet.charAt(index);
    }

    @Override
    public final int indexOf(char c) {
        return alphabet.indexOf(c);
    }

    @Override
    public int length() {
        return alphabet.length();
    }
}
