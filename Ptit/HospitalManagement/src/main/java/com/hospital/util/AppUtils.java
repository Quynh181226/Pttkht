// util/AppUtils.java
package com.hospital.util;

import jakarta.servlet.http.HttpSession;

public class AppUtils {
    public static boolean isAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("userPhone") != null;
    }

    public static boolean isAdmin(HttpSession session) {
        return isAuthenticated(session) && "ADMIN".equals(session.getAttribute("role"));
    }

    public static String getUserPhone(HttpSession session) {
        return session != null ? (String) session.getAttribute("userPhone") : null;
    }
}