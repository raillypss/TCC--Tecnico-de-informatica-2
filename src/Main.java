import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Main {
    private static Usuario usuario = new Usuario("admin", "123");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticFileHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/tutor", new TutorHandler());
        server.createContext("/pet", new PetHandler());
        server.createContext("/vacina", new VacinaHandler());
        server.createContext("/consulta", new ConsultaHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/login.html";
            }
            String filePath = "web" + path;
            File file = new File(filePath);
            if (file.exists()) {
                String content = readFile(file);
                if (path.equals("/cadastro_pet.html")) {
                    content = populateTutors(content);
                } else if (path.equals("/registro_vacina.html") || path.equals("/registro_consulta.html")) {
                    content = populatePets(content);
                }
                exchange.sendResponseHeaders(200, content.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(content.getBytes(StandardCharsets.UTF_8));
                os.close();
            } else {
                String response = "File not found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String readFile(File file) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }

        private String populateTutors(String content) {
            List<Tutor> tutores = Tutor.listar();
            StringBuilder options = new StringBuilder();
            for (Tutor t : tutores) {
                options.append("<option value=\"").append(t.getNome()).append("\">").append(t.getNome()).append("</option>\n");
            }
            return content.replace("<select id=\"tutor\" name=\"tutor\">", "<select id=\"tutor\" name=\"tutor\">\n" + options.toString());
        }

        private String populatePets(String content) {
            List<Pet> pets = Pet.listar();
            StringBuilder options = new StringBuilder();
            for (Pet p : pets) {
                options.append("<option value=\"").append(p.getNome()).append("\">").append(p.getNome()).append("</option>\n");
            }
            return content.replace("<select id=\"pet\" name=\"pet\">", "<select id=\"pet\" name=\"pet\">\n" + options.toString());
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
                String username = params.get("username");
                String password = params.get("password");
                if (usuario.validarLogin(username, password)) {
                    exchange.getResponseHeaders().set("Location", "/dashboard.html");
                    exchange.sendResponseHeaders(302, -1);
                } else {
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"error\">Usuário ou senha incorretos</div><a href=\"/login.html\">Voltar</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        }
    }

    static class TutorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
                String nome = params.get("nome");
                String cpf = params.get("cpf");
                String telefone = params.get("telefone");
                String endereco = params.get("endereco");
                Tutor tutor = new Tutor(nome, cpf, telefone, endereco);
                Tutor.adicionar(tutor);
                String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"success\">Tutor cadastrado com sucesso!</div><a href=\"/dashboard.html\">Voltar ao Dashboard</a></div></body></html>";
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }

    static class PetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
                String nome = params.get("nome");
                String especie = params.get("especie");
                String raca = params.get("raca");
                int idade = Integer.parseInt(params.get("idade"));
                String tutorNome = params.get("tutor");
                Tutor tutor = Tutor.buscarPorNome(tutorNome);
                if (tutor != null) {
                    Pet pet = new Pet(nome, especie, raca, idade, tutor);
                    Pet.adicionar(pet);
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"success\">Pet cadastrado com sucesso!</div><a href=\"/dashboard.html\">Voltar ao Dashboard</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else {
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"error\">Tutor não encontrado</div><a href=\"/cadastro_pet.html\">Voltar</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        }
    }

    static class VacinaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
                String petNome = params.get("pet");
                String nomeVacina = params.get("nomeVacina");
                String dataAplicacao = params.get("dataAplicacao");
                String dataProximaDose = params.get("dataProximaDose");
                Pet pet = Pet.buscarPorNome(petNome);
                if (pet != null) {
                    Vacina vacina = new Vacina(pet, nomeVacina, dataAplicacao, dataProximaDose);
                    Vacina.adicionar(vacina);
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"success\">Vacina registrada com sucesso!</div><a href=\"/dashboard.html\">Voltar ao Dashboard</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else {
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"error\">Pet não encontrado</div><a href=\"/registro_vacina.html\">Voltar</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        }
    }

    static class ConsultaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                Map<String, String> params = parseFormData(formData);
                String petNome = params.get("pet");
                String dataConsulta = params.get("dataConsulta");
                String motivo = params.get("motivo");
                String observacoes = params.get("observacoes");
                Pet pet = Pet.buscarPorNome(petNome);
                if (pet != null) {
                    Consulta consulta = new Consulta(pet, dataConsulta, motivo, observacoes);
                    Consulta.adicionar(consulta);
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"success\">Consulta registrada com sucesso!</div><a href=\"/dashboard.html\">Voltar ao Dashboard</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                } else {
                    String response = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"style.css\"></head><body><div class=\"container\"><h1>PetCare System</h1><div class=\"error\">Pet não encontrado</div><a href=\"/registro_consulta.html\">Voltar</a></div></body></html>";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.close();
                }
            }
        }
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        if (formData != null) {
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                    params.put(key, value);
                }
            }
        }
        return params;
    }
}
