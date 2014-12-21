package com.connect_group.urlshortener;

import com.connect_group.urlshortener.util.StringHelper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

@WebServlet(urlPatterns={"/"})
public class ShortenerServlet extends HttpServlet {

    private final ShortenerService shortenerService;

    public final String SHORTEN_PARAM = "shorten";
    public final String CALLBACK_PARAM = "callback";

    public ShortenerServlet() {
        super();
        this.shortenerService = new ShortenerServiceImpl();
    }

    public ShortenerServlet(ShortenerService shortenerService) {
        super();
        this.shortenerService = shortenerService;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        String url = req.getParameter(SHORTEN_PARAM);
        String callback = req.getParameter(CALLBACK_PARAM);

        if(url==null) {
            expand(req.getServletPath(), callback, resp);
        } else {
            shorten(resp, url, callback);
        }
    }

    private void expand(String servletPath, String callback, HttpServletResponse resp) throws IOException {
        try {
            Token token = new Token(servletPath);

            String redirectUrl = shortenerService.expand(token.getTokenString());

            if(StringHelper.isNotEmpty(callback)) {
                respondWithJsonP(resp, redirectUrl, callback);
            } else if (token.isDisplayOnly()) {
                respondWithMessage(resp, redirectUrl);
            } else {
                permenantRedirect(resp, redirectUrl);
            }
        } catch (UnrecognisedTokenException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void respondWithJsonP(HttpServletResponse resp, String redirectUrl, String callback) throws IOException {
        String jsonp = callback + "({\"url\": \""+redirectUrl +"\"});";
        respondWithMessage(resp, jsonp);
    }

    private void permenantRedirect(HttpServletResponse resp, String redirectUrl) {
        resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", redirectUrl);
    }

    private void shorten(HttpServletResponse resp, String urlString, String callback) throws IOException {
        try {
            String shortUrl = shortenerService.shorten(new URL(urlString));

            if(StringHelper.isNotEmpty(callback)) {
                respondWithJsonP(resp, shortUrl, callback);
            } else {
                respondWithMessage(resp, shortUrl);
            }
        } catch (MalformedURLException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private void respondWithMessage(HttpServletResponse resp, String msg) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.print(msg);
    }

}
