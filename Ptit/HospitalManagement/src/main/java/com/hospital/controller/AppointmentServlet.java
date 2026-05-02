// src/java/controller/AppointmentServlet.java
package com.hospital.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/AppointmentServlet")
public class AppointmentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Legacy servlet endpoint is disabled; use Spring Boot controllers instead.
        response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Legacy servlet disabled");
    }
}