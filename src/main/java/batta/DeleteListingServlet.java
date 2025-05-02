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
import java.sql.SQLException;

@WebServlet("/delete-listing")
public class DeleteListingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        String listingIdParam = request.getParameter("id");

        if (listingIdParam == null) {
            response.sendRedirect("my-listings");
            return;
        }

        int listingId = Integer.parseInt(listingIdParam);

        try (Connection conn = DBConnect.getConnection()) {
            // Ensure the listing belongs to this user before deleting
            String sql = "DELETE FROM auctions WHERE id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setInt(2, userId);
            int affected = stmt.executeUpdate();

            // Optional: You may also want to delete related bids
            if (affected > 0) {
                PreparedStatement delBids = conn.prepareStatement("DELETE FROM bids WHERE listing_id = ?");
                delBids.setInt(1, listingId);
                delBids.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("my-listings");
    }
}
