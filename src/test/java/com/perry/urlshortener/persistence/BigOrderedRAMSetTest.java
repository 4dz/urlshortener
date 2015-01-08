package com.perry.urlshortener.persistence;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BigOrderedRAMSetTest {
    private BigOrderedSet<String> set;
    
    @Before
    public void init() {
        set = new BigOrderedRAMSet<>();
    }

    @Test
    public void shouldAddElement_WhenFirstPageFull() {
        set = new BigOrderedRAMSet<>(2);
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
        assertThat(set.add("entry3"), equalTo(2L));
        assertThat(set.add("entry4"), equalTo(3L));
        assertThat(set.get(3L), equalTo("entry4"));
    }

    @Test
    public void shouldAddElement_WhenSecondPageFull() {
        set = new BigOrderedRAMSet<>(2);
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
        assertThat(set.add("entry3"), equalTo(2L));
        assertThat(set.add("entry4"), equalTo(3L));
        assertThat(set.add("entry5"), equalTo(4L));
        assertThat(set.add("entry6"), equalTo(5L));

        assertThat(set.get(5L), equalTo("entry6"));
    }


}