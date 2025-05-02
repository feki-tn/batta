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

@WebServlet("/place-bid")
public class PlaceBidServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }
        int userId = (int) session.getAttribute("user_id");
        int auctionId = Integer.parseInt(request.getParameter("auction_id"));
        double amount = Double.parseDouble(request.getParameter("amount"));

        try (Connection conn = DBConnect.getConnection()) {
            // Check auction type
            PreparedStatement ps = conn.prepareStatement(
                "SELECT type FROM auctions WHERE id=?"
            );
            ps.setInt(1, auctionId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                response.sendError(404);
                return;
            }
            String type = rs.getString("type");

            if ("sealed".equals(type)) {
                // ensure only one bid per user per auction
                ps = conn.prepareStatement(
                  "SELECT COUNT(*) FROM bids WHERE auction_id=? AND user_id=?"
                );
                ps.setInt(1, auctionId);
                ps.setInt(2, userId);
                rs = ps.executeQuery(); rs.next();
                if (rs.getInt(1)>0) {
                    response.getWriter().println("لقد قدمت عرضاً بالفعل في هذا المزاد السري.");
                    return;
                }
            }
            
            ps = conn.prepareStatement(
              "INSERT INTO bids(auction_id,user_id,amount) VALUES(?,?,?)"
            );
            ps.setInt(1, auctionId);
            ps.setInt(2, userId);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            response.sendRedirect("listing?id="+auctionId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}