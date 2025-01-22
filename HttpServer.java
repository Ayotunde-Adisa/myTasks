import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class HttpServer {


        public static void main(String[] args) {
            int port = 8080;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started on port " + port);
                ExecutorService executor = Executors.newFixedThreadPool(10);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleRequest(clientSocket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void handleRequest(Socket clientSocket) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();

                String line = in.readLine();
                if (line == null) return;

                System.out.println("Request received: " + line);
                String[] requestParts = line.split(" ");
                if (requestParts.length < 2) {
                    sendResponse(out, "HTTP/1.1 400 Bad Request", "Invalid request");
                    return;
                }

                String path = requestParts[1];
                if ("/path".equals(path)) {
                    handlePath(out);
                } else if ("/json".equals(path)) {
                    handleJson(out);
                } else {
                    sendResponse(out, "HTTP/1.1 404 Not Found", "Path not found");
                }

                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void handlePath(OutputStream out) throws IOException {
            String content = "<html><body><h1>Welcome to /path!</h1></body></html>";
            sendResponse(out, "HTTP/1.1 200 OK", content);
        }

        private static void handleJson(OutputStream out) throws IOException {
            String jsonResponse = "Welcome to the json endpoint";
            sendResponse(out, "HTTP/1.1 200 OK", jsonResponse);
        }

        private static void sendResponse(OutputStream out, String statusLine, String content) throws IOException {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.println(statusLine);
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println("Content-Length: " + content.length());
            writer.println("");
            writer.println(content);
            writer.flush();
        }
    }
