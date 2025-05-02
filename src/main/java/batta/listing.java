package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/listing")
public class listing extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // --- Session check ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }
        int userId = Integer.parseInt((String) session.getAttribute("user_id"));

        // --- Parse auction ID ---
        int auctionId;
        try {
            auctionId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid auction ID");
            return;
        }

        String title = "", url = "", type = "";
        LocalDateTime endDate = LocalDateTime.now();

        double maxBid = 0.0;
        Double userSealedBid = null;

        // --- Fetch auction + highest bid info ---
        try (Connection conn = DBConnect.getConnection()) {
            // 1) Auction details
            PreparedStatement ps = conn.prepareStatement(
                "SELECT title,url,type,end_date FROM auctions WHERE id=?"
            );
            ps.setInt(1, auctionId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                response.sendError(404, "Auction not found");
                return;
            }
            title   = rs.getString("title");
            url     = rs.getString("url");
            type    = rs.getString("type");
            endDate = rs.getTimestamp("end_date").toLocalDateTime();

            // 2) Highest bid for open or sealed
            ps = conn.prepareStatement(
              type.equals("open")
                ? "SELECT MAX(amount) FROM bids WHERE auction_id=?"
                : "SELECT amount FROM bids WHERE auction_id=? AND user_id=?"
            );
            ps.setInt(1, auctionId);
            if (type.equals("sealed")) ps.setInt(2, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (type.equals("open")) {
                    maxBid = rs.getDouble(1);
                } else {
                    userSealedBid = rs.getObject(1) != null ? rs.getDouble(1) : null;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        // --- Render HTML ---
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html lang='ar' dir='rtl'><head>");
        out.println("<meta charset='UTF-8'><title>" + title + " - بتة</title>");
        out.println("<style>");
        out.println("body{font-family:Arial,sans-serif;background:#f8f9fa;margin:0;}");
        out.println("header,footer{background:#fff;padding:1rem;box-shadow:0 2px 5px rgba(0,0,0,0.1);}");
        out.println("main{padding:20px;max-width:600px;margin:auto;}");
        out.println(".timer{font-size:1.2rem;color:#dc3545;margin-bottom:1rem;}");
        out.println(".bid-form input{width:100%;padding:8px;margin:0.5rem 0;}");
        out.println(".bid-form button{padding:10px 20px;background:#007bff;color:#fff;border:none;cursor:pointer;}");
        out.println(".info{margin:1rem 0;padding:10px;background:#e9ecef;border-radius:5px;}");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<header><h1>بتة</h1></header>");
        out.println("<main>");

        // Auction details
        out.println("<h2>" + title + "</h2>");
        out.println("<p><a href='" + url + "' target='_blank'>رابط المزاد</a></p>");
        out.println("<p>نوع المزاد: " + (type.equals("sealed") ? "سري" : "علني") + "</p>");

        // Highest bid info
        out.println("<div class='info'>");
        if (type.equals("open")) {
            out.println("أعلى عرض حالياً: " + (maxBid > 0 ? maxBid + " دينار" : "لا توجد عروض بعد") + "</p>");
        } else {
            if (userSealedBid != null) {
                out.println("عرضك السري: " + userSealedBid + " دينار");
            } else {
                out.println("لم تقدّم عرضاً بعد");
            }
        }
        out.println("</div>");

        // Timer
        out.println("<div class='timer' id='timer'>وقت متبقي: حساب...</div>");

        // Bid form (disabled if time up)
        out.println("<form class='bid-form' action='place-bid' method='post'>");
        out.println("<input type='hidden' name='auction_id' value='" + auctionId + "'/>");
        out.println("<label>المبلغ:</label>");
        out.println("<input type='number' name='amount' step='0.01' required/>");
        out.println("<button type='submit'>ضع عرضك</button>");
        out.println("</form>");

        // Countdown script
        out.println("<script>");
        out.println("const endTime=new Date('" + endDate + "');");
        out.println("function updateTimer(){");
        out.println("  let diff=(endTime-new Date())/1000; if(diff<0) diff=0;");
        out.println("  const d=Math.floor(diff/86400); diff%=86400;");
        out.println("  const h=Math.floor(diff/3600); diff%=3600;");
        out.println("  const m=Math.floor(diff/60); const s=Math.floor(diff%60);");
        out.println("  document.getElementById('timer').textContent=`وقت متبقي: ${d}ي ${h}س ${m}د ${s}ث`;");
        out.println("  if(endTime-new Date()<=0) document.querySelector('.bid-form button').disabled=true;");
        out.println("} setInterval(updateTimer,1000); updateTimer();");
        out.println("</script>");

        out.println("</main>");
        out.println("<footer>&copy; 2025 بتة</footer>");
        out.println("</body></html>");
    }
}
