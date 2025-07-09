package code;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;


public class CreditServlet extends HttpServlet {
    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://creditdb:5432/creditdb", "dima", "dima"
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
        if (passportParam == null) {
            resp.setStatus(400);
            out.println("{\"error\":\"Missing passport_number parameter\"}");
            return;
        }

        try {
            int passportNumber = Integer.parseInt(passportParam);

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT bank_name, credit, dolg FROM СКИ WHERE passport_number = ?")) {
                ps.setInt(1, passportNumber);
                ResultSet rs = ps.executeQuery();

                StringBuilder jsonResult = new StringBuilder();
                jsonResult.append("[");

                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        jsonResult.append(",");
                    } else {
                        first = false;
                    }

                    jsonResult.append("{");
                    jsonResult.append("\"bank_name\":\"").append(rs.getString("bank_name")).append("\",");
                    jsonResult.append("\"credit\":").append(rs.getDouble("credit")).append(",");
                    jsonResult.append("\"dolg\":").append(rs.getDouble("dolg"));
                    jsonResult.append("}");
                }

                jsonResult.append("]");

                if (first) { // значит записей не было
                    resp.setStatus(404);
                    out.println("{\"error\":\"Credit history not found\"}");
                } else {
                    out.println(jsonResult.toString());
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            out.println("{\"error\":\"Invalid passport_number format\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}