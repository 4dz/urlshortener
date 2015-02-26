package com.perry.urlshortener.persistence;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BigOrderedRAMSetTest {
    private BigOrderedRAMSet<String> set;
    
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

    @Test
    public void shouldAddElementToSet_WhenMirroringClusteredDatabase_AndPageExists() {
        set.mirror(777L, "HELLO");
        assertThat(set.get(777L), equalTo("HELLO"));
    }

    @Test
    public void shouldAddElementToSet_AfterMirroringClusteredDatabase_AndPageDoesNotExist() {
        set = new BigOrderedRAMSet<>(2);
        set.mirror(5L, "HELLO");
        assertThat(set.get(5L), equalTo("HELLO"));
    }

    @Test
    public void shouldAddElementToSet_AfterMirroringClusteredDatabase() {
        set = new BigOrderedRAMSet<>(2);
        set.mirror(5L, "HELLO");
        assertThat(set.add("entry1"), equalTo(0L));
    }

}