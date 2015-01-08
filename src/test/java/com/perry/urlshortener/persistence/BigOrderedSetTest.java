package com.perry.urlshortener.persistence;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(Parameterized.class)
public class BigOrderedSetTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private final SetFactory factory;
    private BigOrderedSet<String> set;

    public BigOrderedSetTest(SetFactory factory) {
        this.factory = factory;
    }

    @Before
    public void init() {
        set = factory.newSet(tmpFolder);
    }

    @Test
    public void shouldReturnExpectedIndex_WhenUniqueEntryAdded() {
        assertThat(set.add("entry1"), equalTo(0L));
        assertThat(set.add("entry2"), equalTo(1L));
    }

    @Test
    public void shouldReturnOriginalIndex_WhenDuplicateEntryAdded() {
        assertThat(set.add("entryA"), equalTo(0L));
        assertThat(set.add("entryA"), equalTo(0L));
    }

    @Test
    public void shouldReturnExpectedElement_WhenIndexSupplied() {
        set.add("entry01");
        set.add("entry02");

        assertThat(set.get(0L), equalTo("entry01"));
        assertThat(set.get(1L), equalTo("entry02"));
    }

    @Test(expected=NoSuchElementException.class)
    public void shouldThrowException_WhenRequestingIndexOutOfBounds() {
        set.get(0L);
    }

    @Test
    public void shouldNotifyDiskWriter_WhenEntryAdded() {
        final ArrayList<String> entries = new ArrayList<>();
        SetModificationListener<String> listener = new SetModificationListener<String>() {

            @Override
            public void add(long index, String s) {
                entries.add((int)index, s);

            }

            @Override
            public void close() {}
        };

        set.setSynchronizedListener(listener);
        set.add("entryWithListener");
        assertThat(entries.get(0), equalTo("entryWithListener"));
    }

    @Test
    public void shouldCloseListener_WhenSetIsClosed() {
        final boolean[] closed = {false};

        SetModificationListener<String> listener = new SetModificationListener<String>() {
            @Override
            public void add(long index, String s) {}

            @Override
            public void close() {
                closed[0] = true;

            }
        };

        set.setSynchronizedListener(listener);
        assertThat(closed[0], equalTo(false));
        set.close();
        assertThat(closed[0], equalTo(true));

    }

    @Test(expected=IllegalAccessError.class)
    public void shouldFail_WhenCloseIsCalledTwice() {
        set.close();
        set.close();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> instancesToTest() throws IOException {


        return Arrays.asList(
                new Object[]{new SetFactory() {
                    @Override
                    public BigOrderedSet<String> newSet(TemporaryFolder tmpFolder) {
                        return new BigOrderedRAMSet<>();
                    }
                }},

                new Object[]{new SetFactory() {
                    @Override
                    public BigOrderedSet<String> newSet(TemporaryFolder tmpFolder) {
                        try {
                            File tmp = tmpFolder.newFile();
                            return new BigOrderedMapDBSet<>(tmp.getAbsolutePath());
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to create temp file for use with MapDB");
                        }
                    }
                }}
        );
    }



    public static interface SetFactory {
        BigOrderedSet<String> newSet(TemporaryFolder tmpFolder);
    }
}
