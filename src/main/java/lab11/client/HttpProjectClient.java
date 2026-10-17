package lab11.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab11.model.Project;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpProjectClient {
    private static final String BASE_URL = "http://localhost:8080/projects/projects";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    
    public List<Project> getAllProjects() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?format=json"))
                .GET()
                .build();
        HttpResponse<String> response = send(request);
        checkStatus(response);
        try {
            Project[] projects = mapper.readValue(response.body(), Project[].class);
            return Arrays.asList(projects);
        } catch (Exception e) {
            throw new RuntimeException("Некоректна JSON-відповідь сервера", e);
        }
    }

    public void addProject(Project project) {
        String form = "action=insert"
                + "&format=json"
                + "&name=" + enc(project.getName())
                + "&manager=" + enc(project.getManager())
                + "&duration=" + project.getDuration();
        checkStatus(send(postRequest(form)));
    }

    public void updateProject(Project project) {
        String form = "action=update"
                + "&format=json"
                + "&id=" + project.getId()
                + "&name=" + enc(project.getName())
                + "&manager=" + enc(project.getManager())
                + "&duration=" + project.getDuration();
        checkStatus(send(postRequest(form)));
    }

    public void deleteProject(int id) {
        String url = BASE_URL + "?action=delete&format=json&id=" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        checkStatus(send(request));
    }

    private HttpRequest postRequest(String form) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося звернутися до сервера", e);
        }
    }

    private void checkStatus(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    private String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
