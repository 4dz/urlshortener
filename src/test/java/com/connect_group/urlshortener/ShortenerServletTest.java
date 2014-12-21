package com.connect_group.urlshortener;

import com.connect_group.urlshortener.stub.StubShortenerService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
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



    @Before
    public void init() throws IOException {
        servlet = new ShortenerServlet(new StubShortenerService());
        mockResponse = mock(HttpServletResponse.class);
        mockRequest = mock(HttpServletRequest.class);
        stringWriter = new StringWriter();

        given(mockResponse.getWriter()).willReturn(new PrintWriter(stringWriter));

    }

    @Test
    public void shouldShortenUrl_WhenShortenParameterSupplied() throws ServletException, IOException {
        given(mockRequest.getParameter("shorten")).willReturn("http://test/");

        servlet.doGet(mockRequest, mockResponse);

        assertThat(stringWriter.toString(), equalTo("1"));
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
        assertThat(stringWriter.toString(), equalTo("2"));
    }

    @Test
    public void shouldReturnPageNotFound_WhenShortenParameterNotSupplied() throws ServletException, IOException {
        servlet.doGet(mockRequest, mockResponse);
        verify(mockResponse).sendError(eq(404), anyString());
        verify(mockResponse, times(1)).sendError(anyInt(), anyString());
    }

    @Test
    public void shouldRedirectToExpectedPage_WhenShortTokenSupplied() throws ServletException, IOException {
        given(mockRequest.getServletPath()).willReturn("/valid");
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
        given(mockRequest.getServletPath()).willReturn("/valid+");
        servlet.doGet(mockRequest, mockResponse);

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
        assertThat(stringWriter.toString(), equalTo("myfunction({\"url\": \"1\"});"));
    }

}