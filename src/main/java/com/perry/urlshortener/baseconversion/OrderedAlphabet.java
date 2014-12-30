package com.perry.urlshortener.baseconversion;

import java.util.Arrays;

/**
 * Ordered Alphabets have better efficiency O(log(n)) when
 * converting from a string into a number.
 */
public class OrderedAlphabet implements Alphabet {
    
    private final char[] alphabet;
    
    public OrderedAlphabet(String alphabet) {
        this.alphabet = alphabet.toCharArray();
    }

    @Override
    public final char charAt(int index) {
        return alphabet[index];
    }

    @Override
    public final int indexOf(char c) {
        return Arrays.binarySearch(alphabet, c);
    }

    @Override
    public int length() {
        return alphabet.length;
    }
}
