//package com.hospital.util;
//
//import jakarta.servlet.http.HttpSession;
//
//public class SessionUtil {
//
//    public static boolean isLoggedIn(HttpSession session) {
//        return session != null && session.getAttribute("user") != null;
//    }
//
//    public static String getCurrentUser(HttpSession session) {
//        return session != null ? (String) session.getAttribute("user") : null;
//    }
//
//    public static String getCurrentRole(HttpSession session) {
//        return session != null ? (String) session.getAttribute("role") : null;
//    }
//
//    public static boolean hasRole(HttpSession session, String role) {
//        String currentRole = getCurrentRole(session);
//        return currentRole != null && currentRole.equals(role);
//    }
//}