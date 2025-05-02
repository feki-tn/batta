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


@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    
    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return role != null && role.equals("admin");
    }

    // Handle GET request to display the control panel
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null || !isAdmin(session)) {
            response.sendRedirect("login.html");
            return;
        }

        String action = request.getParameter("action");

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ar' dir='rtl'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'/>");
        out.println("<title>لوحة التحكم - بتة</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background: #f8f9fa; display: flex; flex-direction: column; min-height: 100vh; }");
        out.println("header, footer { background: #fff; padding: 1rem 2rem; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        out.println("header { display: flex; justify-content: flex-start; align-items: center; }");
        out.println("header h1 { color: #007bff; font-size: 1.8rem; margin: 0; }");
        out.println("nav { display: flex; justify-content: flex-start; width: 100%; }");
        out.println("nav a { margin-left: 15px; text-decoration: none; color: #333; font-weight: bold; }");
        out.println("main { flex: 1; display: flex; justify-content: center; align-items: center; padding: 20px; flex-direction: column; }");
        out.println(".section { margin: 20px 0; background: #fff; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border-radius: 8px; width: 80%; }");
        out.println("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }");
        out.println("th, td { padding: 12px; text-align: right; border: 1px solid #ccc; }");
        out.println("button { padding: 10px 20px; background-color: #dc3545; color: white; border: none; border-radius: 5px; cursor: pointer; }");
        out.println("button:hover { background-color: #c82333; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>بتة - لوحة التحكم</h1>");
        out.println("<nav>");
        out.println("<a href='admin?action=manage-users'>إدارة المستخدمين</a>");
        out.println("<a href='admin?action=manage-listings'>إدارة العروض</a>");
        out.println("<a href='admin?action=manage-bids'>إدارة العروض</a>");
        out.println("<a href='admin?action=generate-report'>توليد التقرير</a>");
        out.println("<a href='logout'>تسجيل الخروج</a>");
        out.println("</nav>");
        out.println("</header>");

        out.println("<main>");
        out.println("<h2>لوحة التحكم - إدارة بتة</h2>");

        // Handle different actions based on the request parameter
        if ("manage-users".equals(action)) {
            out.println("<h3>إدارة المستخدمين</h3>");
            out.println("<p>عرض وتعديل وحذف المستخدمين.</p>");
            try (Connection conn = DBConnect.getConnection()) {
                String sql = "SELECT user_id, username, email, role FROM users";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                out.println("<table>");
                out.println("<tr><th>اسم المستخدم</th><th>البريد الإلكتروني</th><th>الدور</th><th>الإجراءات</th></tr>");
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("username") + "</td>");
                    out.println("<td>" + rs.getString("email") + "</td>");
                    out.println("<td>" + rs.getString("role") + "</td>");
                    out.println("<td><button>حذف</button></td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            } catch (SQLException e) {
                out.println("<p>حدث خطأ أثناء استرجاع البيانات: " + e.getMessage() + "</p>");
            }
        } else if ("manage-listings".equals(action)) {
            out.println("<h3>إدارة العروض</h3>");
            out.println("<p>عرض وتعديل وحذف العروض.</p>");
            try (Connection conn = DBConnect.getConnection()) {
                String sql = "SELECT * FROM listings";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                out.println("<table>");
                out.println("<tr><th>اسم العرض</th><th>نوع المزاد</th><th>تاريخ الانتهاء</th><th>الإجراءات</th></tr>");
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>" + rs.getString("auction_type") + "</td>");
                    out.println("<td>" + rs.getDate("ending_date") + "</td>");
                    out.println("<td><button>حذف</button><button>تعديل</button></td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            } catch (SQLException e) {
                out.println("<p>حدث خطأ أثناء استرجاع البيانات: " + e.getMessage() + "</p>");
            }
        } else if ("manage-bids".equals(action)) {
            out.println("<h3>إدارة العروض</h3>");
            out.println("<p>عرض جميع العروض المقدمة ورفض أو قبول العروض.</p>");
            try (Connection conn = DBConnect.getConnection()) {
                String sql = "SELECT b.bid_id, b.amount, b.user_id, l.name AS listing_name FROM bids b JOIN listings l ON b.listing_id = l.id";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                out.println("<table>");
                out.println("<tr><th>العرض</th><th>المبلغ</th><th>المستخدم</th><th>الإجراءات</th></tr>");
                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("listing_name") + "</td>");
                    out.println("<td>" + rs.getDouble("amount") + "</td>");
                    out.println("<td>" + rs.getInt("user_id") + "</td>");
                    out.println("<td><button>قبول</button><button>رفض</button></td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            } catch (SQLException e) {
                out.println("<p>حدث خطأ أثناء استرجاع البيانات: " + e.getMessage() + "</p>");
            }
        } else if ("generate-report".equals(action)) {
            out.println("<h3>توليد التقرير</h3>");
            out.println("<p>تقرير يومي لجميع المزادات والعروض المقدمة.</p>");
            out.println("<button>توليد التقرير</button>");
        } else {
            out.println("<p>اختر إجراء من القائمة أعلاه.</p>");
        }

        out.println("</main>");

        out.println("<footer>");
        out.println("<p>&copy; 2025 بتة</p>");
        out.println("</footer>");
        out.println("</body>");
        out.println("</html>");
    }
}