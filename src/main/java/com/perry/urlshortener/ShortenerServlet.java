package com.perry.urlshortener;

import com.perry.urlshortener.config.Configuration;
import com.perry.urlshortener.lifecycle.Scope;
import com.perry.urlshortener.lifecycle.ScopeAwareHttpServlet;
import com.perry.urlshortener.service.ShortenerService;
import com.perry.urlshortener.service.ShortenerServiceException;
import com.perry.urlshortener.util.StringHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This servlet can both shorten and expand a URL.
 *
 * URL Parameters:
 *  ?shorten=url
 *  This will shorten a URL and return the shortened token in a plain text response.
 *  If an error occurs (such as an invalid URL), a 400 error is returned.
 *
 *  ?shorten=url&callback=json
 *  This will shorten a URL and return a JSONP response.
 *  If an error occurs, the JSONP response will contain an explanation of the error.
 *
 * When no shorten parameter is supplied, the servlet will try to expand the URL.
 * If the URL ends in a +, the expanded URL is returned in a plain text response.
 * If the URL does not end in a +, a permenant redirect is sent.
 * If a callback parameter is supplied, then a JSONP response is sent.
 *
 */
@WebServlet(urlPatterns={"/"})
public class ShortenerServlet extends ScopeAwareHttpServlet {

    private ShortenerService shortenerService;
    
    public final String SHORTEN_PARAM = "shorten";
    public final String CALLBACK_PARAM = "callback";
    
    private String baseUrl;

    public void setConfiguration(Configuration configuration) {
        this.baseUrl = ensureSafeBaseUrl(configuration.get(Configuration.Key.BASE_URL));
    }
    
    public void setShortenerService(ShortenerService service) {
        this.shortenerService = service;
    }
    
    @Override
    public void init(Scope scope) {
        if(shortenerService==null) {
            setShortenerService(scope.getShortenerService());
        }
        
        if(baseUrl==null) {
            setConfiguration(scope.getConfiguration());
        }
    }
    
    /**
     * The default constructor which is used by the web container.
     */
    public ShortenerServlet() {}

    private String ensureSafeBaseUrl(String baseUrl) {
        if(!baseUrl.endsWith("/")) {
            baseUrl+="/";
        }
        return baseUrl;
    }

    public ShortenerService getShortenerService() {
        return shortenerService;
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {

        enableCORS(resp);

        
        String url = req.getParameter(SHORTEN_PARAM);
        String callback = req.getParameter(CALLBACK_PARAM);

        if(url==null) {
            expand(req.getServletPath(), callback, resp);
        } else {
            shorten(resp, url, callback);
        }
    }

    private void enableCORS(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin","*");
    }

    private void expand(String servletPath, String callback, HttpServletResponse resp) throws IOException {
        try {
            Token token = new Token(servletPath);

            String redirectUrl = shortenerService.expand(token.getTokenString());
            if(redirectUrl==null) {
                throw new UnrecognisedTokenException("Null expanded url");
            }

            if(StringHelper.isNotEmpty(callback)) {
                respondWithJsonP(resp, redirectUrl, callback);
            } else if (token.isDisplayOnly()) {
                respondWithMessage(resp, redirectUrl);
            } else {
                permenantRedirect(resp, redirectUrl);
            }
        } catch (UnrecognisedTokenException e) {
            handleError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage(), callback);
        } catch(ShortenerServiceException ex) {
            handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), callback);
        }
    }

    private void respondWithJsonPError(HttpServletResponse resp, String message, String callback) throws IOException {
        String jsonp = callback + "({\"error\": \""+message +"\"});";
        respondWithJsonPMessage(resp, jsonp);
    }

    private void respondWithJsonP(HttpServletResponse resp, String redirectUrl, String callback) throws IOException {
        String jsonp = callback + "({\"url\": \""+redirectUrl +"\"});";
        respondWithJsonPMessage(resp, jsonp);
    }

    private void permenantRedirect(HttpServletResponse resp, String redirectUrl) {
        resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", redirectUrl);
    }

    private void shorten(HttpServletResponse resp, String urlString, String callback) throws IOException {
        try {
            String token = shortenerService.shorten(new URL(urlString));
            String shortUrl = baseUrl + token;

            if(StringHelper.isNotEmpty(callback)) {
                respondWithJsonP(resp, shortUrl, callback);
            } else {
                respondWithMessage(resp, shortUrl);
            }
        } catch (MalformedURLException ex) {
            handleError(resp, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage(), callback);
        } catch(ShortenerServiceException ex) {
            handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), callback);
        }
    }
    
    private void handleError(HttpServletResponse resp, int code, String message, String callback) throws IOException {
        if(StringHelper.isNotEmpty(callback)) {
            respondWithJsonPError(resp, message, callback);
        } else {
            resp.sendError(code, message);
        }
    }

    private void respondWithJsonPMessage(HttpServletResponse resp, String msg) throws IOException {
        respondWithMessage(resp, "application/javascript", msg);
    }

    private void respondWithMessage(HttpServletResponse resp, String msg) throws IOException {
        respondWithMessage(resp, "text/plain", msg);
    }

    private void respondWithMessage(HttpServletResponse resp, String contentType, String msg) throws IOException {
        resp.setContentType(contentType);
        PrintWriter writer = resp.getWriter();
        writer.print(msg);
    }


}
