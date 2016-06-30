package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 *
 * @author bhannel
 */
public class HttpHelper {

    public static String post(String url, Map<String, String> arguments) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        connection.setDoInput(true); // Let the run-time system (RTS) know that we want input
        connection.setDoOutput(true); // Let the RTS know that we want to do output
        connection.setUseCaches(false); // No caching, we want the real thing
        connection.setRequestMethod("POST");

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(out);
        }

        String result = readAll(connection.getInputStream());
        return result;
    }

    public static String readAll(InputStream stream) throws IOException {
        BufferedReader output = new BufferedReader(new InputStreamReader(stream));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = output.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
