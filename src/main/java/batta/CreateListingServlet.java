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

@WebServlet("/create-listing")
public class CreateListingServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user_id") == null) {
      response.sendRedirect("login.html");
      return;
    }

    int userId = (int) session.getAttribute("user_id");
    String name = request.getParameter("name");
    String link = request.getParameter("link");
    String type = request.getParameter("type");
    String endDateStr = request.getParameter("endDate");
    String price = request.getParameter("price");
    try (Connection conn = DBConnect.getConnection()) {
      String sql = "INSERT INTO auctions (user_id, title, url, type, end_date,start_price) VALUES (?, ?, ?, ?, ?,?)";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setInt(1, userId);
      stmt.setString(2, name);
      stmt.setString(3, link);
      stmt.setString(4, type);
      stmt.setString(5, endDateStr);
      stmt.setString(6, price);
      stmt.executeUpdate();

      response.sendRedirect("my-listings");
    } catch (SQLException e) {
      e.printStackTrace();
      response.setContentType("text/html;charset=UTF-8");
      response.getWriter().println("<h3>حدث خطأ أثناء إنشاء المزاد!</h3><pre>" + e.getMessage() + "</pre>");
    }
  }
}