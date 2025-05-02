package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;





@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String idNumber = request.getParameter("idNumber");
    String password = request.getParameter("password");

    try (Connection conn = DBConnect.getConnection()) {
      String sql = "INSERT INTO users (name, email, id_number, password, role) VALUES (?, ?, ?, ?, 'user')";
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.setString(1, name);
      stmt.setString(2, email);
      stmt.setString(3, idNumber);
      stmt.setString(4, password);
      stmt.executeUpdate();

      response.sendRedirect("login.html");
    } catch (SQLException e) {
      e.printStackTrace();
      response.setContentType("text/html;charset=UTF-8");
      try (PrintWriter out = response.getWriter()) {
        out.println("<h3>حدث خطأ أثناء التسجيل!</h3>");
        out.println("<pre>" + e.getMessage() + "</pre>");
      }
    }
  }
}
