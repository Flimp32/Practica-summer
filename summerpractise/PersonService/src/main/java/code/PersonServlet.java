package code;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class PersonServlet extends HttpServlet {
    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://persondb:5432/persondb", "dima", "dima"
            );
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String passportParam = req.getParameter("passport_number");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");

        String sql;
        PreparedStatement ps = null;

        try {
            if (passportParam != null) {
                // Поиск по номеру паспорта
                sql = "SELECT first_name, last_name, age, gender, passport_number FROM СДОЧ WHERE passport_number = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(passportParam));
            } else if (firstName != null && lastName != null) {
                // Поиск по имени и фамилии
                sql = "SELECT first_name, last_name, age, gender, passport_number FROM СДОЧ WHERE first_name = ? AND last_name = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
            } else {
                resp.setStatus(400);
                out.println("{\"error\":\"Параметры запроса некорректны. Укажите либо passport_number, либо firstName и lastName\"}");
                return;
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                out.println("{");
                out.println("\"firstName\":\"" + rs.getString("first_name") + "\",");
                out.println("\"lastName\":\"" + rs.getString("last_name") + "\",");
                out.println("\"age\":" + rs.getInt("age") + ",");
                out.println("\"gender\":\"" + rs.getString("gender") + "\",");
                out.println("\"passport\":\"" + rs.getString("passport_number") + "\"");
                out.println("}");
            } else {
                resp.setStatus(404);
                out.println("{\"error\":\"Человек не найден\"}");
            }

        } catch (SQLException e) {
            resp.setStatus(500);
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            out.println("{\"error\":\"Некорректный номер паспорта\"}");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException ignore) {}
        }
    }
}
