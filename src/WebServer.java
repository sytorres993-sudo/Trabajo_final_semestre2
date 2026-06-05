import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servidor web mínimo para exponer una UI estática y endpoints REST sencillos
 * que interactúan con el ParqueaderoRepository existente.
 */
public class WebServer {
    private static ParqueaderoRepository repository;
    private static ParqueaderoService service;

    public static void main(String[] args) throws Exception {
        // Inicializar con datos semilla (reutilizar lógica similar a MenuConsole)
        repository = new ParqueaderoRepository(3,2,1);
        service = new ParqueaderoService(repository);

        Usuario admin = new Usuario(1, "admin", "admin123", RolUsuario.ADMINISTRADOR, true);
        Usuario cajero = new Usuario(2, "cajero", "cajero123", RolUsuario.CAJERO_OPERADOR, true);
        repository.crearUsuario(admin);
        repository.crearUsuario(cajero);
        repository.establecerTarifa("CARRO",5000.0);
        repository.establecerTarifa("MOTO",3000.0);
        repository.establecerTarifa("BICICLETA",1000.0);

        // Crear algunos vehículos estacionados
        Vehiculo v1 = new Vehiculo("ABC123", TipoVehiculo.CARRO, LocalDateTime.now().minusHours(2), EstadoVehiculo.ACTIVO, "Semilla");
        Vehiculo v2 = new Vehiculo("XYZ789", TipoVehiculo.CARRO, LocalDateTime.now().minusMinutes(90), EstadoVehiculo.ACTIVO, "Semilla");
        Vehiculo v3 = new Vehiculo("MOTO55", TipoVehiculo.MOTO, LocalDateTime.now().minusMinutes(30), EstadoVehiculo.ACTIVO, "Semilla");
        repository.registrarVehiculoActivo(v1);
        repository.registrarVehiculoActivo(v2);
        repository.registrarVehiculoActivo(v3);

        // Ocupar espacios y crear registros
        EspacioParqueo e1 = repository.obtenerEspacioDisponible(TipoVehiculo.CARRO);
        repository.ocuparEspacio(e1.getNumeroEspacio(), v1.getPlaca());
        repository.crearRegistroParqueo(v1, e1.getNumeroEspacio(), admin);
        EspacioParqueo e2 = repository.obtenerEspacioDisponible(TipoVehiculo.CARRO);
        repository.ocuparEspacio(e2.getNumeroEspacio(), v2.getPlaca());
        repository.crearRegistroParqueo(v2, e2.getNumeroEspacio(), cajero);
        EspacioParqueo e3 = repository.obtenerEspacioDisponible(TipoVehiculo.MOTO);
        repository.ocuparEspacio(e3.getNumeroEspacio(), v3.getPlaca());
        repository.crearRegistroParqueo(v3, e3.getNumeroEspacio(), cajero);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/styles.css", new StaticHandler());
        server.createContext("/app.js", new StaticHandler());

        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/ingreso", new IngresoHandler());
        server.createContext("/api/salida", new SalidaHandler());
        server.createContext("/api/activos", new ActivosHandler());
        server.createContext("/api/tarifas", new TarifasHandler());
        server.createContext("/api/tarifa", new TarifaUpdateHandler());
        server.createContext("/api/tablero", new TableroHandler());
        server.createContext("/api/historial", new HistorialHandler());
        server.createContext("/api/buscar", new BuscarHandler());
        server.createContext("/api/reporte", new ReporteHandler());
        server.createContext("/api/anular", new AnularHandler());
        server.createContext("/api/usuarios", new UsuariosHandler());
        server.createContext("/api/configcap", new ConfigCapHandler());

        server.setExecutor(null);
        System.out.println("Servidor iniciado en http://localhost:8080");
        // Intentar abrir el navegador por defecto (no crítico si falla)
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("http://localhost:8080"));
            }
        } catch (Exception ignored) { }
        server.start();
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            Path file = Path.of("web" + path);
            if (!Files.exists(file)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] bytes = Files.readAllBytes(file);
            String type = guessContentType(path);
            exchange.getResponseHeaders().add("Content-Type", type);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    static String guessContentType(String path){
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        return "application/octet-stream";
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            Map<String,String> q = queryToMap(query);
            String user = q.getOrDefault("user","");
            String pass = q.getOrDefault("pass","");
            Usuario u = repository.obtenerUsuario(user);
            Map<String,Object> resp = new HashMap<>();
            if (u == null){ resp.put("success", false); resp.put("message","Usuario no encontrado"); }
            else if (!u.getContrasena().equals(pass)){ resp.put("success", false); resp.put("message","Contraseña inválida"); }
            else { resp.put("success", true); resp.put("user", Map.of("nombreUsuario",u.getNombreUsuario(),"rol",u.getRol().name())); }
            writeJson(exchange, resp);
        }
    }

    static class IngresoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String,String> data = jsonToMap(body);
            String placa = data.get("placa");
            String tipo = data.get("tipo");
            String usuario = data.get("usuario");
            Usuario u = repository.obtenerUsuario(usuario);
            ParqueaderoService.ResultadoOperacion res = service.registrarIngreso(placa, TipoVehiculo.valueOf(tipo), u);
            Map<String,Object> r = new HashMap<>();
            r.put("success", res.isExitoso()); r.put("message", res.getMensaje());
            writeJson(exchange, r);
        }
    }

    static class SalidaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String,String> data = jsonToMap(body);
            String placa = data.get("placa");
            String usuario = data.get("usuario");
            Usuario u = repository.obtenerUsuario(usuario);
            ParqueaderoService.ResultadoOperacionPago res = service.registrarSalidaYPago(placa, u);
            Map<String,Object> r = new HashMap<>();
            r.put("success", res.isExitoso()); r.put("message", res.getMensaje()); r.put("monto", res.getMontoPagado());
            writeJson(exchange, r);
        }
    }

    static class ActivosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Collection<Vehiculo> activos = repository.obtenerVehiculosActivos();
            List<Map<String,Object>> list = activos.stream().map(v -> {
                Map<String,Object> m = new HashMap<>();
                m.put("placa", v.getPlaca());
                m.put("tipo", v.getTipoVehiculo().name());
                m.put("horaIngreso", v.getHoraIngreso().toString());
                m.put("observaciones", v.getObservaciones());
                return m;
            }).collect(Collectors.toList());
            writeJson(exchange, list);
        }
    }

    static class TarifasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            writeJson(exchange, repository.obtenerTodasTarifas());
        }
    }

    static class TarifaUpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String,String> data = jsonToMap(body);
            String tipo = data.get("tipo");
            String valor = data.get("valor");
            String usuario = data.get("usuario");
            Usuario u = repository.obtenerUsuario(usuario);
            Map<String,Object> r = new HashMap<>();
            if (u == null || u.getRol() != RolUsuario.ADMINISTRADOR) { r.put("success", false); r.put("message","Acceso denegado"); writeJson(exchange, r); return; }
            try {
                double v = Double.parseDouble(valor);
                repository.establecerTarifa(tipo, v);
                r.put("success", true); r.put("message","Tarifa actualizada");
            } catch (Exception e){ r.put("success", false); r.put("message","Valor inválido"); }
            writeJson(exchange, r);
        }
    }

    static class TableroHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            ParqueaderoService.TableroOcupacion t = service.obtenerTableroOcupacion();
            Map<String,Object> m = Map.of(
                    "totalCupos", t.getTotalCupos(),
                    "ocupados", t.getCuposOcupados(),
                    "disponibles", t.getCuposDisponibles(),
                    "porcentaje", t.getPorcentajeOcupacion()
            );
            writeJson(exchange, m);
        }
    }

    static class HistorialHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            writeJson(exchange, repository.obtenerHistorialCompleto());
        }
    }

    // Nuevo: búsqueda por placa (usa servicio recursivo)
    static class BuscarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String q = exchange.getRequestURI().getQuery();
            Map<String,String> map = queryToMap(q);
            String placa = map.getOrDefault("placa","");
            List<RegistroParqueo> res = service.obtenerHistorialVehiculo(placa);
            writeJson(exchange, res);
        }
    }

    // Nuevo: reporte diario recursivo
    static class ReporteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<RegistroParqueo> registros = repository.obtenerHistorialCompleto();
            ParqueaderoService.ReporteDiario rep = service.generarReporteDiarioRecursivo(registros, 0, 0.0, 0, 0);
            writeJson(exchange, Map.of("totalRecaudado", rep.getTotalRecaudado(), "ingresos", rep.getTotalIngresos(), "salidas", rep.getTotalSalidas()));
        }
    }

    // Nuevo: anular operación (solo ADMIN)
    static class AnularHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String,String> data = jsonToMap(body);
            String idStr = data.get("id");
            String just = data.get("justificacion");
            String usuario = data.get("usuario");
            Map<String,Object> r = new HashMap<>();
            Usuario u = repository.obtenerUsuario(usuario);
            if (u == null || u.getRol() != RolUsuario.ADMINISTRADOR) {
                r.put("success", false); r.put("message","Acceso denegado: requiere ADMINISTRADOR"); writeJson(exchange, r); return;
            }
            try {
                Long id = Long.parseLong(idStr);
                boolean ok = service.anularOperacion(id, just, u);
                r.put("success", ok); r.put("message", ok?"Operación anulada":"No se pudo anular");
            } catch (Exception e){ r.put("success", false); r.put("message","ID inválido"); }
            writeJson(exchange, r);
        }
    }

    // Nuevo: gestión de usuarios (GET lista, POST crear, DELETE por query)
    static class UsuariosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                writeJson(exchange, repository.obtenerTodosUsuarios());
                return;
            }
            if (method.equalsIgnoreCase("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Map<String,String> data = jsonToMap(body);
                String nombre = data.get("nombre");
                String pass = data.get("pass");
                String rol = data.get("rol");
                String usuario = data.get("usuario");
                Map<String,Object> r = new HashMap<>();
                Usuario u = repository.obtenerUsuario(usuario);
                if (u == null || u.getRol() != RolUsuario.ADMINISTRADOR) { r.put("success", false); r.put("message","Acceso denegado"); writeJson(exchange, r); return; }
                int idNew = new Random().nextInt(10000)+100;
                Usuario nuevo = new Usuario(idNew, nombre, pass, RolUsuario.valueOf(rol), true);
                boolean ok = repository.crearUsuario(nuevo);
                r.put("success", ok); r.put("message", ok?"Usuario creado":"No se pudo crear"); writeJson(exchange, r); return;
            }
            // DELETE via query ?eliminar=nombre&usuario=admin
            if (method.equalsIgnoreCase("DELETE") || (method.equalsIgnoreCase("GET") && exchange.getRequestURI().getQuery()!=null && exchange.getRequestURI().getQuery().contains("eliminar="))) {
                String q = exchange.getRequestURI().getQuery();
                Map<String,String> map = queryToMap(q);
                String target = map.get("eliminar");
                String usuario = map.get("usuario");
                Map<String,Object> r = new HashMap<>();
                Usuario u = repository.obtenerUsuario(usuario);
                if (u == null || u.getRol() != RolUsuario.ADMINISTRADOR) { r.put("success", false); r.put("message","Acceso denegado"); writeJson(exchange, r); return; }
                boolean ok = repository.eliminarUsuario(target);
                r.put("success", ok); r.put("message", ok?"Usuario eliminado":"No existe"); writeJson(exchange, r); return;
            }
            exchange.sendResponseHeaders(405, -1);
        }
    }

    // Nuevo: reconfigurar capacidad
    static class ConfigCapHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String,String> data = jsonToMap(body);
            int car = Integer.parseInt(data.getOrDefault("car","0"));
            int mot = Integer.parseInt(data.getOrDefault("mot","0"));
            int bic = Integer.parseInt(data.getOrDefault("bic","0"));
            String usuario = data.get("usuario");
            Map<String,Object> r = new HashMap<>();
            Usuario u = repository.obtenerUsuario(usuario);
            if (u == null || u.getRol() != RolUsuario.ADMINISTRADOR) { r.put("success", false); r.put("message","Acceso denegado"); writeJson(exchange, r); return; }
            boolean ok = repository.configurarCapacidad(car, mot, bic);
            r.put("success", ok); r.put("message", ok?"Capacidad actualizada":"No se puede reducir por debajo de ocupados"); writeJson(exchange, r);
        }
    }

    // Utilidades simples JSON (no biblioteca externa)
    static void writeJson(HttpExchange exchange, Object obj) throws IOException {
        String s = toJson(obj);
        exchange.getResponseHeaders().add("Content-Type","application/json; charset=UTF-8");
        byte[] bs = s.getBytes();
        exchange.sendResponseHeaders(200, bs.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bs); }
    }

    static String toJson(Object o){
        // Nota: implementación muy básica para mapas, listas y strings/numbers
        if (o == null) return "null";
        if (o instanceof Map) {
            Map<?,?> m = (Map<?,?>) o;
            return "{" + m.entrySet().stream()
                    .map(e -> "\"" + escape(e.getKey().toString()) + "\":" + toJson(e.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }
        if (o instanceof Collection) {
            Collection<?> c = (Collection<?>) o; return "[" + c.stream().map(WebServer::toJson).collect(Collectors.joining(",")) + "]";
        }
        if (o instanceof String) return '"' + escape((String)o) + '"';
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        // fallback: use toString
        return '"' + escape(o.toString()) + '"';
    }

    static String escape(String s){ return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n"); }

    static Map<String,String> queryToMap(String query){
        if (query == null) return Collections.emptyMap();
        return Arrays.stream(query.split("&")).map(p->p.split("=",2)).collect(Collectors.toMap(a->a[0], a-> a.length>1?a[1]:""));
    }

    static Map<String,String> jsonToMap(String json){
        // implementación simple para objetos con valores string
        Map<String,String> m = new HashMap<>();
        String s = json.trim();
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length()-1).trim();
            if (s.isEmpty()) return m;
            String[] parts = s.split(",");
            for (String p: parts) {
                String[] kv = p.split(":",2);
                if (kv.length<2) continue;
                String key = kv[0].trim().replaceAll("^\"|\"$","" );
                String val = kv[1].trim().replaceAll("^\"|\"$","" );
                m.put(key,val);
            }
        }
        return m;
    }
}
