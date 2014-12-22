package com.connect_group.urlshortener.baseconversion;

import com.connect_group.urlshortener.util.StringHelper;

/**
 * Encode and Decode a long value into a String based on an alphabet.
 */
public class BaseN {
    private Alphabet alphabet;
    private final int radix;
    
    public BaseN(String alphabet) {
        if(StringHelper.isEmpty(alphabet) || alphabet.length()==1) {
            throw new IllegalArgumentException("alphabet must contain at least 2 entries! ["+alphabet+"]");
        }

        if(isBinarySearchableAlphabet(alphabet)) {
            this.alphabet = new OrderedAlphabet(alphabet);
        } else {
            this.alphabet = new UnorderedAlphabet(alphabet);
        }
        this.radix = this.alphabet.length();
    }

    private boolean isBinarySearchableAlphabet(String alphabet) {
        boolean orderedAlphabet=true;
        char c1=alphabet.charAt(0);
        for(int i=1; i<alphabet.length(); i++) {
            char c2 = alphabet.charAt(i);
            if(c2<c1) {
                orderedAlphabet=false;
                break;
            }
        }
        return orderedAlphabet;
    }

    public String encode(long number) {
        if(number<0) {
            throw new IllegalArgumentException("Cannot encode negative number (" + number + ")");
        }
        
        int capacity = (int)(number/radix)+1;
        int index=capacity-1;
        char[] encoded = new char[capacity];

        do {
            long remainder = number % radix;
            encoded[index--]=alphabet.charAt((int)remainder);
            number = number / radix;
        } while(number>0);
        
        return new String(encoded);
    }
    
    public long decode(String encoded) {
        if(StringHelper.isEmpty(encoded)) {
            throw new IllegalArgumentException("Cannot decode empty or null string");
        }
        long result=0;
        for(int index=0; index<encoded.length(); index++) {
            result = (result*radix) + indexOf(encoded.charAt(index));
        }
        return result;
    }
    
    public boolean isOrderedAlphabet() {
        return alphabet instanceof OrderedAlphabet;
    }

    private long indexOf(char c) {
        return alphabet.indexOf(c);
    }
}
