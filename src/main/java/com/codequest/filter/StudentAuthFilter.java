package com.codequest.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Module 3 / 12 - Login and Integration
 *
 * Protects the student pages (mapped in web.xml).
 * If there is no session or no "userId" in the session,
 * the user is sent to the login page.
 */
public class StudentAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // false = do not create a new session if none exists
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?msg=Please+log+in+as+a+student+to+continue.");
            return;
        }

        // Stop the browser from caching protected pages,
        // so the Back button does not show them after logout.
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        chain.doFilter(req, res);
    }
}
