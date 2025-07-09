package code;

import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class CentralServlet extends HttpServlet {
    private final String personServiceUrl  = "http://person-service:8080/person";
    private final String creditServiceUrl  = "http://credit-service:8080/credit";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        StringBuilder body = new StringBuilder();
        String line;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        JSONObject inputJson = new JSONObject(body.toString());
        String firstName = inputJson.getString("firstName");
        String lastName = inputJson.getString("lastName");

        String personUrl = personServiceUrl + "?firstName=" + URLEncoder.encode(firstName, "UTF-8")
                + "&lastName=" + URLEncoder.encode(lastName, "UTF-8");

        String personJsonStr = fetchFromService(personUrl);

        if (personJsonStr == null || personJsonStr.equals("{}")) {
            resp.setStatus(404);
            out.println("{\"error\":\"Человек не найден\"}");
            return;
        }

        JSONObject personJson = new JSONObject(personJsonStr);
        String passport = personJson.getString("passport");

        String creditJsonStr = fetchFromService(creditServiceUrl + "?passport_number=" + URLEncoder.encode(passport, "UTF-8"));
        if (creditJsonStr == null || creditJsonStr.equals("")) {
            creditJsonStr = "[]";
        }

        JSONArray creditArray;
        try {
            creditArray = new JSONArray(creditJsonStr);
        } catch (Exception e) {
            creditArray = new JSONArray();
            if (!creditJsonStr.equals("{}") && !creditJsonStr.isEmpty()) {
                creditArray.put(new JSONObject(creditJsonStr));
            }
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("person", personJson);
        responseJson.put("credit_history", creditArray);

        out.println(responseJson.toString(2));
    }

    private String fetchFromService(String serviceUrl) throws IOException {
        URL url = new URL(serviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) return null;

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        return content.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("CentralServlet is alive");
    }
}
