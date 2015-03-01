package com.perry.urlshortener.persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class BigOrderedReplicatedMapDBSetTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final static String IPV4_PREFERENCE_PROPERTY = "java.net.preferIPv4Stack";
    private static String IPSTACK_PREFERENCE_VALUE;

    @BeforeClass
    public static void before() {
        IPSTACK_PREFERENCE_VALUE = System.getProperty(IPV4_PREFERENCE_PROPERTY);
        System.setProperty(IPV4_PREFERENCE_PROPERTY,"true");
    }

    @AfterClass
    public static void after() {
        if(IPSTACK_PREFERENCE_VALUE ==null) {
            System.clearProperty(IPV4_PREFERENCE_PROPERTY);
        } else {
            System.setProperty(IPV4_PREFERENCE_PROPERTY, IPSTACK_PREFERENCE_VALUE);
        }
    }

    @Test
    public void shouldDecorateMapWithLazyReplicatedHashMap() throws Exception {
        File tmp = folder.newFile();
        String dbFilename = tmp.getAbsolutePath();
        BigOrderedReplicatedMapDBSet<String> db = new BigOrderedReplicatedMapDBSet<>(dbFilename, "cluster", 1000L);
        
        ConcurrentMap<Long,String> orgMap = new ConcurrentHashMap<>();
        
        Map<Long,String> newMap = db.decorate(orgMap);
        
        assertThat(newMap, instanceOf(LazyReplicatedMap.class));
    }

    @Test
    public void shouldCreateUniqueEntriesOnMultipleNodes() throws Exception {
        File tmp = folder.newFile();
        String dbFilename1 = tmp.getAbsolutePath();
        BigOrderedReplicatedMapDBSet<String> db1 = new BigOrderedReplicatedMapDBSet<>(dbFilename1, "cluster", 1000L);

        tmp = folder.newFile();
        String dbFilename2 = tmp.getAbsolutePath();
        BigOrderedReplicatedMapDBSet<String> db2 = new BigOrderedReplicatedMapDBSet<>(dbFilename2, "cluster", 1000L);


        long l1 = db1.add("ELEMENT1");
        long l2 = db2.add("ELEMENT2");
        assertThat(l2, equalTo(l1+1));
        
        assertThat(db2.get(l1), equalTo("ELEMENT1"));
        assertThat(db1.get(l2), equalTo("ELEMENT2"));
    }


}