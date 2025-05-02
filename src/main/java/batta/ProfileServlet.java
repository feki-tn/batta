package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        String name = "", email = "", idNumber = "";

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT name, email, id_number FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString("name");
                email = rs.getString("email");
                idNumber = rs.getString("id_number");
            } else {
                response.sendRedirect("login.html");
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ar' dir='rtl'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'/>");
        out.println("<title>الملف الشخصي - بتة</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background: #f8f9fa; display: flex; flex-direction: column; min-height: 100vh; }");
        out.println("header, footer { background: #fff; padding: 1rem 2rem; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        out.println("header { display: flex; justify-content: flex-start; align-items: center; }");
        out.println("header h1 { color: #007bff; font-size: 1.8rem; margin: 0; }");
        out.println("nav { display: flex; justify-content: flex-start; width: 100%; }");
        out.println("nav a { margin-left: 15px; text-decoration: none; color: #333; font-weight: bold; }");
        out.println("main { flex: 1; display: flex; justify-content: center; align-items: center; }");
        out.println(".form-container { background: #fff; padding: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border-radius: 8px; width: 300px; }");
        out.println(".form-container p { margin: 10px 0; }");
        out.println("footer { text-align: center; margin-top: auto; box-shadow: 0 -2px 5px rgba(0,0,0,0.05); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>بتة</h1>");
        out.println("<nav><div class='nav-links'>");
        out.println("<a href='index.html'>الرئيسية</a>");
        out.println("<a href='my-listings'>قائمتي</a>");
        out.println("<a href='profile'>الملف الشخصي</a>");
        out.println("<a href='logout'>تسجيل الخروج</a>");
        out.println("</div></nav>");
        out.println("</header>");

        out.println("<main>");
        out.println("<div class='form-container'>");
        out.println("<h2>الملف الشخصي</h2>");
        out.println("<p><strong>الاسم:</strong> " + name + "</p>");
        out.println("<p><strong>البريد الإلكتروني:</strong> " + email + "</p>");
        out.println("<p><strong>رقم الهوية:</strong> " + idNumber + "</p>");
        out.println("</div>");
        out.println("</main>");

        out.println("<footer><p>&copy; 2025 بتة</p></footer>");
        out.println("</body>");
        out.println("</html>");
    }
}
