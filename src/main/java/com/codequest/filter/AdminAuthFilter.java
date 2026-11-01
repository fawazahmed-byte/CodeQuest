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
 * Module 4 / 12 - Admin Login and Integration
 *
 * Protects the admin pages (mapped in web.xml).
 * Only a session with "adminId" may continue. A logged-in student
 * has no adminId, so students cannot use admin features.
 */
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect(request.getContextPath()
                    + "/admin-login.jsp?msg=Admin+access+only.+Please+log+in+as+admin.");
            return;
        }

        // Do not cache admin pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        chain.doFilter(req, res);
    }
}
