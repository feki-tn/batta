package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get login credentials from form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password); 

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("user_id", rs.getInt("id"));
                        String role = rs.getString("role");
                        session.setAttribute("role", role);
                        if(role.equals("user"))
                        response.sendRedirect("profile");
                        else
                        response.sendRedirect("admin");
                    } else {
                        response.getWriter().write("Invalid login credentials.");
                    }
                }
            }
        } catch (SQLException e) {
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get login credentials from form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Database connection
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password); 

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        
                        HttpSession session = request.getSession();
                        session.setAttribute("user_id", rs.getString("name"));
                        
                        response.sendRedirect("profile");
                    } else {
                        response.getWriter().write("Invalid login credentials.");
                    }
                }
            }
        } catch (SQLException e) {
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }
}
