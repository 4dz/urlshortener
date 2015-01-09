package com.perry.urlshortener;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.lifecycle.LifecycleListener;
import com.perry.urlshortener.lifecycle.MutableScope;
import com.perry.urlshortener.lifecycle.Scope;
import com.perry.urlshortener.persistence.BigOrderedMapDBSetFactory;
import com.perry.urlshortener.persistence.BigOrderedRAMSet;
import com.perry.urlshortener.persistence.DatabaseOnStartup;
import com.perry.urlshortener.stub.Config;
import com.perry.urlshortener.stub.StubServletConfig;
import com.perry.urlshortener.stub.StubShortenerService;
import com.perry.urlshortener.util.Utf8String;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ShortenerServletTest {
    private ShortenerServlet servlet;
    private HttpServletResponse mockResponse;
    private HttpServletRequest mockRequest;
    private StringWriter stringWriter;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Before
    public void init() throws IOException {
        servlet = getShortenerServlet(new StubShortenerService(), new Config().with("BASE_URL", "http://t.ag/"));
        resetMockRequest();
        resetMockResponse();
    }
    
    private static ShortenerServlet getShortenerServlet(ShortenerService service, Configuration config) {
        ShortenerServlet servlet = new ShortenerServlet();
        servlet.setShortenerService(service);
        servlet.setConfiguration(config);
        return servlet;
    }

    private static ShortenerServlet getShortenerServlet(Scope scope) {
        ShortenerServlet servlet = new ShortenerServlet();
        StubServletConfig servletConfig = new StubServletConfig();
        given(servletConfig.mockServletContext.getAttribute(LifecycleListener.SCOPE_ATTRIBUTE_NAME)).willReturn(scope);

        servlet.init(servletConfig);
        return servlet;
    }

    private void resetMockRequest() {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void shouldShortenUrl_WhenShortenParameterSupplied() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");

        servlet.doGet(mockRequest, mockResponse);

        assertThat(stringWriter.toString(), equalTo("http://t.ag/1"));
    }

    @Test
    public void shouldReturnError_WhenShortenParameterIsNotValidUrl() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("htt://test/");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(eq(400), anyString());
    }

    @Test
    public void shouldReturnDifferentShortUrl_WhenSecondUrlRequested() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");
        servlet.doGet(mockRequest, mockResponse);
        stringWriter = new StringWriter();
        given(mockRequest.getParameter("shorten")).willReturn("http://test2/");
        given(mockResponse.getWriter()).willReturn(new PrintWriter(stringWriter));

        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("http://t.ag/2"));
    }

    @Test
    public void shouldReturnPageNotFound_WhenShortenParameterNotSupplied() throws ServletException, IOException {
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(eq(404), anyString());
        verify(mockResponse, times(1)).sendError(anyInt(), anyString());
    }

    @Test
    public void shouldRedirectToExpectedPage_WhenShortTokenSupplied() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("valid");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(mockResponse).setHeader("Location", "expanded_url");
    }

    @Test
    public void shouldReturnPageNotFound_WhenShortTokenNotRecognised() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/unknown");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(eq(404), anyString());
    }

    @Test
    public void shouldPrintExpandedUrl_WhenShortTokenRecognised_AndEndsWithPlus() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("valid+");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("expanded_url"));
    }

    @Test
    public void shouldIgnoreLeadingSlash_WhenShortTokenSupplied() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/valid+");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("expanded_url"));
    }

    @Test
    public void shouldReturnExpandedUrlInJsonp_WhenCallbackSpecified() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/valid");
        given(mockRequest.getParameter("callback")).willReturn("myfunction");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("myfunction({\"url\": \"expanded_url\"});"));
    }

    @Test
    public void shouldReturnShortUrlInJsonp_WhenCallbackSpecified() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");
        given(mockRequest.getParameter("callback")).willReturn("myfunction");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("myfunction({\"url\": \"http://t.ag/1\"});"));
    }

    @Test
    public void shouldReturnErrorMessage_WhenNullExpandedUrlIsObtained() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/return-null");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(eq(404), anyString());
    }

    @Test
    public void shouldReturnJsonPError_WhenUnknownShortTokenAndCallbackSpecified() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/unknown");
        given(mockRequest.getParameter("callback")).willReturn("myfunction");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("myfunction({\"error\": \"shortened token was not recognised\"});"));
    }

    @Test
    public void shouldReturnJsonPError_WhenInvalidUrlAndCallbackSpecified() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("htt://test/");
        given(mockRequest.getParameter("callback")).willReturn("somefunction");
        servlet.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("somefunction({\"error\": \"unknown protocol: htt\"});"));
    }

    @Test
    public void shouldSetJavascriptContentType_WhenJsonP() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("htt://test/");
        given(mockRequest.getParameter("callback")).willReturn("somefunction");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).setContentType("application/javascript");
    }
    
    @Test
    public void shouldUseDefaultServiceImplementation_WhenDefaultConstructorIsUsedByWebApp() throws IOException {
        MutableScope scope = new MutableScope(new Config());
        scope.setDatabase(new BigOrderedRAMSet<Utf8String>());
        servlet = getShortenerServlet(scope);
        assertThat(servlet.getShortenerService(), instanceOf(ShortenerServiceImpl.class));
    }
    
    @Test
    public void shouldSetCORSHeader() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("htt://test/");
        given(mockRequest.getParameter("callback")).willReturn("somefunction");
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).setHeader("Access-Control-Allow-Origin","*");
    }
    
    @Test
    public void shouldAddSlashToBaseUrl_WhenBaseUrlHasNoSlash() throws ServletException, IOException {
        servlet = getShortenerServlet(new StubShortenerService(), new Config().with("BASE_URL", "http://t.ag/x"));
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");

        servlet.doGet(mockRequest, mockResponse);

        assertThat(stringWriter.toString(), equalTo("http://t.ag/x/1"));
    }
    
    @Test
    public void shouldRecordDataToDisk_WhenUsingRAMSet_AndDiskWriter() throws ServletException, IOException {
        File dir = folder.newFolder();
        String backupFilePath = dir.getAbsoluteFile() + "/backup.txt";

        MutableScope scope = new MutableScope(new Config().with("DISK_BACKUP_FILEPATH", backupFilePath));
        new DatabaseOnStartup().onStart(scope);
        servlet = getShortenerServlet(scope);
        
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");
        servlet.doGet(mockRequest, mockResponse);

        byte[] encoded = Files.readAllBytes(Paths.get(backupFilePath));
        String fileContents = new String(encoded, "UTF-8");
        MatcherAssert.assertThat(fileContents, equalTo("http://test/\n"));
    }
    
    @Test
    public void shouldDisplayErrorInfoOnShortenPageLoad_WhenFailsToCreateBackupDatabaseFile() throws IOException, ServletException {
        MutableScope scope = new MutableScope(new Config());
        scope.setError("Error occurred at startup");
        servlet = getShortenerServlet(scope);

        given(mockRequest.getParameter("shorten")).willReturn("http://test/");
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(eq(500), anyString());
        verify(mockResponse, times(1)).sendError(anyInt(), anyString());
    }

    @Test
    public void shouldDisplayErrorInfoOnExpandPageLoad_WhenErrorsOccurredAtStartup() throws IOException, ServletException {
        MutableScope scope = new MutableScope(new Config());
        scope.setError("Error occurred at startup");
        servlet = getShortenerServlet(scope);

        given(mockRequest.getServletPath()).willReturn("/expand-me");
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).sendError(eq(500), anyString());
        verify(mockResponse, times(1)).sendError(anyInt(), anyString());
    }

    @Test
    public void shouldInitialiseSystemWithDataRecordedToDisk() throws ServletException, IOException {
        File dir = folder.newFolder();
        String backupFilePath = dir.getAbsoluteFile() + "/backup.txt";
        MutableScope scope = new MutableScope(new Config().with("DISK_BACKUP_FILEPATH", backupFilePath));
        new DatabaseOnStartup().onStart(scope);
        servlet = getShortenerServlet(scope);

        given(mockRequest.getParameter("shorten")).willReturn("http://test1/").willReturn("http://test2/");
        servlet.doGet(mockRequest, mockResponse);
        servlet.doGet(mockRequest, mockResponse);
        
        byte[] encoded = Files.readAllBytes(Paths.get(backupFilePath));
        String fileContents = new String(encoded, "UTF-8");
        assertThat(fileContents, equalTo("http://test1/\nhttp://test2/\n"));

        ShortenerServlet servlet2 = getShortenerServlet(scope);

        resetMockRequest();
        resetMockResponse();
        given(mockRequest.getServletPath()).willReturn("/-+");
        servlet2.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("http://test1/"));

        resetMockRequest();
        resetMockResponse();
        given(mockRequest.getServletPath()).willReturn("/2+");
        servlet2.doGet(mockRequest, mockResponse);
        assertThat(stringWriter.toString(), equalTo("http://test2/"));
    }
    
    @Test(expected=RuntimeException.class)
    public void shouldThrowRuntimeException_WhenDatabaseClasspathInvalid() throws IOException {
        MutableScope scope = new MutableScope(new Config().with("DATABASE_FACTORY_CLASSPATH", "com.does.not.exist"));
        scope.setDatabase(new BigOrderedMapDBSetFactory().newSet(scope.getConfiguration()));
        servlet = getShortenerServlet(scope);
    }


    @Test(expected=RuntimeException.class)
    public void shouldThrowRuntimeException_WhenDatabaseClasspathIsNotInstanceOfExpectedFactory() throws IOException {
        MutableScope scope = new MutableScope(new Config().with("DATABASE_FACTORY_CLASSPATH", "java.lang.Object"));
        scope.setDatabase(new BigOrderedMapDBSetFactory().newSet(scope.getConfiguration()));
        servlet = getShortenerServlet(scope);
    }
    
    private void resetMockResponse() {
        stringWriter = new StringWriter();
        mockResponse = mock(HttpServletResponse.class);
        try {
            given(mockResponse.getWriter()).willReturn(new PrintWriter(stringWriter));
        } catch (IOException e) {
            fail("Mock threw exception on getWriter()");
        }
    }
}