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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/my-listings")
public class MyListingsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if the user is logged in
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
        out.println("<title>قائمتي - بتة</title>");
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
        out.println("button { padding: 10px 20px; background-color: #dc3545; color: white; border: none; border-radius: 5px; cursor: pointer; }");
        out.println("button:hover { background-color: #c82333; }");
        out.println("footer { text-align: center; margin-top: auto; box-shadow: 0 -2px 5px rgba(0,0,0,0.05); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>بتة</h1>");
        out.println("<nav>");
        out.println("<a href='index.html'>الرئيسية</a>");
        out.println("<a href='create_listing.html'>إضافة إعلان</a>");
        out.println("<a href='profile'>الملف الشخصي</a>");
        out.println("<a href='logout'>تسجيل الخروج</a>");
        out.println("</nav>");
        out.println("</header>");

        out.println("<main>");
        out.println("<div class='table-container'>");
        out.println("<h2>قائمتي</h2>");

        try (Connection conn = DBConnect.getConnection()) {
            // 1) Query the right table
            String sql = "SELECT id, title, url, type, end_date FROM auctions WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            // 2) Check for empty result set
            if (!rs.next()) {
                out.println("<p>لا توجد إعلانات لعرضها.</p>");
            } else {
                // 3) Render table header
                out.println("<table>");
                out.println("<tr>");
                out.println("<th>العنوان</th>");
                out.println("<th>الرابط</th>");
                out.println("<th>النوع</th>");
                out.println("<th>تاريخ الانتهاء</th>");
                out.println("<th>إجراءات</th>");
                out.println("<th>مشاركة</th>");
                out.println("</tr>");

                
                out.println("<th>مشاركة</th>");
                out.println("</tr>");

                // iterate over rs.next() …
                do {
                    int auctionId   = rs.getInt("id");
                    String title    = rs.getString("title");
                    String url      = rs.getString("url");
                    String type     = rs.getString("type");
                    Timestamp endTs = rs.getTimestamp("end_date");
                    String endDate  = endTs.toLocalDateTime().toLocalDate().toString();

                    out.println("<tr>");
                    out.println("<td>" + title + "</td>");
                    out.println("<td><a href='" + url + "' target='_blank'>رابط</a></td>");
                    out.println("<td>" + (type.equals("sealed") ? "سري" : "علني") + "</td>");
                    out.println("<td>" + endDate + "</td>");

                    // Delete button cell
                    out.println("<td>");
                    
                   
                    out.println("<button type='submit'><a href=http://localhost:8080/batta/delete-listing?="+auctionId+">حذف</a></button>");
                   
                    out.println("</td>");

                    
                    out.println("<td>");
                    out.println("  <button onclick=\"navigator.clipboard.writeText('http://localhost:8080/batta/listing?id="+auctionId+"').then(()=>alert('تم نسخ الرابط!'));\">مشاركة</button>");
                    out.println("</td>");

                    out.println("</tr>");
                } while (rs.next());

                out.println("</table>");
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }

        out.println("</div>");
        out.println("</main>");

      
        out.println("</body>");
        out.println("</html>");
    }
}