package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/my-bids")
public class MyBidsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("user_id");

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ar' dir='rtl'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'/>");
        out.println("<title>مزاداتي - بتة</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background: #f8f9fa; display: flex; flex-direction: column; min-height: 100vh; }");
        out.println("header, footer { background: #fff; padding: 1rem 2rem; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        out.println("header { display: flex; justify-content: flex-start; align-items: center; }");
        out.println("header h1 { color: #007bff; font-size: 1.8rem; margin: 0; }");
        out.println("nav { display: flex; justify-content: flex-start; width: 100%; }");
        out.println("nav a { margin-left: 15px; text-decoration: none; color: #333; font-weight: bold; }");
        out.println("main { flex: 1; display: flex; justify-content: center; align-items: center; padding: 20px; }");
        out.println(".table-container { background: #fff; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border-radius: 8px; width: 80%; }");
        out.println("table { width: 100%; border-collapse: collapse; }");
        out.println("th, td { padding: 12px; text-align: right; border: 1px solid #ccc; }");
        out.println("footer { text-align: center; margin-top: auto; box-shadow: 0 -2px 5px rgba(0,0,0,0.05); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>بتة</h1>");
        out.println("<nav>");
        out.println("<a href='index.html'>الرئيسية</a>");
        out.println("<a href='create_listing.html'>إضافة إعلان</a>");
        out.println("<a href='my-listings'>قائمتي</a>");
        out.println("<a href='my-bids'>مزاداتي</a>");
        out.println("<a href='profile'>الملف الشخصي</a>");
        out.println("<a href='logout'>تسجيل الخروج</a>");
        out.println("</nav>");
        out.println("</header>");

        out.println("<main>");
        out.println("<div class='table-container'>");
        out.println("<h2>مزاداتي</h2>");

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT b.amount, b.bid_time, l.name AS listing_name, l.id AS listing_id FROM bids b JOIN auctions l ON b.listing_id = l.id WHERE b.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            out.println("<table>");
            out.println("<tr>");
            out.println("<th>اسم الإعلان</th>");
            out.println("<th>المبلغ</th>");
            out.println("<th>الوقت</th>");
            out.println("<th>رابط</th>");
            out.println("</tr>");

            boolean hasBids = false;
            while (rs.next()) {
                hasBids = true;
                String name = rs.getString("listing_name");
                double amount = rs.getDouble("amount");
                java.sql.Timestamp time = rs.getTimestamp("bid_time");
                int listingId = rs.getInt("listing_id");

                out.println("<tr>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + amount + "</td>");
                out.println("<td>" + time + "</td>");
                out.println("<td><a href='auction?id=" + listingId + "' target='_blank'>عرض</a></td>");
                out.println("</tr>");
            }

            if (!hasBids) {
                out.println("<tr><td colspan='4' style='text-align:center;'>لا توجد مزايدات حتى الآن.</td></tr>");
            }

            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace(out);
        }

        out.println("</div>");
        out.println("</main>");

        out.println("<footer>");
        out.println("<p>&copy; 2025 بتة</p>");
        out.println("</footer>");
        out.println("</body>");
        out.println("</html>");
    }
}