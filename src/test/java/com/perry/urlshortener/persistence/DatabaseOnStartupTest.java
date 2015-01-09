package com.perry.urlshortener.persistence;

import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.stub.Config;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

public class DatabaseOnStartupTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldSetScopeError_WhenDatabaseFailsToCreate() throws IOException {
        String backupFilePath = createReadOnlyBackupFile();
        MutableScope scope = new MutableScope(new Config().with("DISK_BACKUP_FILEPATH", backupFilePath));
        DatabaseOnStartup databaseStarter = new DatabaseOnStartup();
        databaseStarter.onStart(scope);

        assertThat(scope.isError(), equalTo(true));
        assertThat(scope.getErrorMessage(), notNullValue());
    }

    private String createReadOnlyBackupFile() throws IOException {
        File dir = folder.newFolder();
        String backupFilePath = dir.getAbsoluteFile() + "/backup.txt";
        File file = new File(backupFilePath);
        if(!file.createNewFile()) {
            fail("Could not create temporary file");
        }

        if(!file.setReadOnly()) {
            fail("Could not make temporary file read-only");
        }
        return backupFilePath;
    }
}