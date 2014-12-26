package com.connect_group.urlshortener.baseconversion;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class BaseNTest {

    private static final String LARGE_ORDERED_ALPHABET="-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~";
    private static final String LARGE_UNORDERED_ALPHABET="0123456789-.ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~";
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowException_WhenSuppliedEmptyAlphabet() {
        new BaseN(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowException_WhenAlphabetHasOneEntry() {
        new BaseN("0");
    }

    @Test
    public void shouldEncodeZeroUsingFirstAlphabetEntry() {
        assertThat(new BaseN("01").encode(0L), equalTo("0"));
    }

    @Test
    public void shouldEncodeOneUsingSecondAlphabetEntry() {
        assertThat(new BaseN("01").encode(1L), equalTo("1"));
    }
    
    @Test
    public void shouldEncodeTwoDigits_WhenValueIsGreaterThanOrEqualToRadix_AndUsingOrderedAlphabet() {
        assertThat(new BaseN("01").encode(2L), equalTo("10"));
    }

    @Test
    public void shouldEncodeTwoDigits_WhenValueIsGreaterThanOrEqualToRadix_AndUsingUnorderedAlphabet() {
        assertThat(new BaseN("OI").encode(2L), equalTo("IO"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowException_WhenEncodingNegativeNumber() {
        new BaseN("01").encode(-1L);
    }
    
    @Test
    public void shouldDecodeFirstAlphabetEntryAsZero_WhenZero() {
        assertThat(new BaseN("01").decode("0000"), equalTo(0L));
    }

    @Test
    public void shouldDecodeSecondAlphabetEntryAsOne_WhenOne() {
        assertThat(new BaseN("01").decode("1"), equalTo(1L));
    }
    
    @Test
    public void shouldDecodeMultiCharacterString_WhenTwoCharacters() {
        assertThat(new BaseN("01").decode("10"), equalTo(2L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowException_WhenEncodedStringIsEmpty() {
        new BaseN("01").decode("");
    }
    
    @Test
    public void shouldIdentifyOrderedAlphabet_WhenAlphabetInAsciiOrder() {
        assertThat(new BaseN(LARGE_ORDERED_ALPHABET).isOrderedAlphabet(), equalTo(true));
    }

    @Test
    public void shouldNotIdentifyOrderedAlphabet_WhenAlphabetNotInAsciiOrder() {
        assertThat(new BaseN(LARGE_UNORDERED_ALPHABET).isOrderedAlphabet(), equalTo(false));
    }
    
    @Test
    public void shouldEncodeLargeNumbers() {
        BaseN base = new BaseN(LARGE_UNORDERED_ALPHABET);
        long id = Long.MAX_VALUE;
        assertThat(base.decode(base.encode(id)), equalTo(Long.MAX_VALUE));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowException_WhenTokenExceedsMaxLongValue() {
        BaseN base37 = new BaseN("-0123456789abcdefghijklmnopqrstuvwxyz");
        base37.decode("zzzzzzzzzzzzz");
    }
    
    @Test
    public void shouldDecodeFaster_WhenOrderedAlphabet() {
        
        BaseN fast = new BaseN(LARGE_ORDERED_ALPHABET);
        BaseN slow = new BaseN(LARGE_UNORDERED_ALPHABET);
        
        long start = System.nanoTime();
        for(int i=0; i<2000000; i++) {
            fast.decode("bcr89xyAini");
        }
        long end = System.nanoTime();
        long fastDuration = end-start;

        start = System.nanoTime();
        for(int i=0; i<2000000; i++) {
            slow.decode("bcr89xyAini");
        }
        end = System.nanoTime();
        long slowDuration = end-start;


        if(slowDuration>fastDuration) {
            long diff = slowDuration-fastDuration;
            System.out.println("Ordered Alphabet was "+diff +"ns ("+diff/1000000+"ms, "+ (diff*100)/(slowDuration) +"%) faster");
        } else {
            long diff = fastDuration-slowDuration;
            System.out.println("Ordered Alphabet was "+diff +"ns ("+diff/1000000+"ms) slower");
        }
        
        assertThat(slowDuration-fastDuration, greaterThan(0L));
    }
}