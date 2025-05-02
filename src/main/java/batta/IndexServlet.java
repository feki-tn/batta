package batta;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


import java.io.PrintWriter;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='ar' dir='rtl'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>بتة</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 0; padding: 0; display: flex; flex-direction: column; min-height: 100vh; background: #f8f9fa; }");
        out.println("header, footer { background: #fff; padding: 1rem 2rem; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        out.println("header { display: flex; justify-content: space-between; align-items: center; }");
        out.println("header h1 { color: #007bff; font-size: 1.8rem; margin: 0; }");
        out.println("nav a { margin-left: 15px; text-decoration: none; color: #333; font-weight: bold; }");
        out.println("main { flex: 1; text-align: center; padding: 50px 20px; }");
        out.println("input[type='text'] { padding: 12px; width: 250px; font-size: 1rem; border: 1px solid #ccc; border-radius: 5px; }");
        out.println("button { padding: 12px 20px; font-size: 1rem; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer; margin-right: 10px; }");
        out.println("button:hover { background-color: #0056b3; }");
        out.println("footer { text-align: center; margin-top: auto; box-shadow: 0 -2px 5px rgba(0,0,0,0.05); }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<header>");
        out.println("<h1>بتة</h1>");
        out.println("<nav>");
        if (loggedIn) {
            out.println("<a href='profile'>الملف الشخصي</a>");
            out.println("<a href='my-listings'>قائمتي</a>");
            out.println("<a href='logout'>تسجيل خروج</a>");
        } else {
            out.println("<a href='login.html'>تسجيل دخول</a>");
            out.println("<a href='signup.html'>إنشاء حساب</a>");
        }
        out.println("</nav>");
        out.println("</header>");

        out.println("<main>");
        out.println("<h2>مرحبا بيك في بتة</h2>");
        out.println("<p>ابحث على مزادك عن طريق رقم المعرف</p>");
        out.println("<form action='viewListing' method='get'>");
        out.println("<input type='text' name='auctionId' placeholder='رقم المزاد' required>");
        out.println("<button type='submit'>بحث</button>");
        out.println("</form>");
        out.println("</main>");

        out.println("<footer>");
        out.println("&copy; 2025 بتة - جميع الحقوق محفوظة");
        out.println("</footer>");

        out.println("</body>");
        out.println("</html>");
    }
}
