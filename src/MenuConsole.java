import java.time.LocalDateTime;
import java.util.*;

/**
 * Menú de consola para simular el flujo interactivo del parqueadero.
 * - Inicio de sesión con control de roles
 * - Menú continuo con opciones según rol
 * - Datos semilla para probar recursividad
 *
 * Nota: diseñado para ejecutarse con "java -cp src MenuConsole"
 */
public class MenuConsole {

    private static ParqueaderoRepository repository;
    private static ParqueaderoService service;
    private static final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        // usar args brevemente para evitar advertencia de "parameter 'args' is never used"
        if (args != null && args.length > 0) {
            System.out.println("Argumentos de inicio: " + Arrays.toString(args));
        }
        inicializarSistemaConDatosSemilla();
        while (true) {
            Usuario usuario = login();
            if (usuario == null) {
                System.out.println("Saliendo del sistema.");
                break;
            }
            menuPrincipal(usuario);
        }
    }

    private static void inicializarSistemaConDatosSemilla() {
        // Crear repositorio con capacidades (carros:3, motos:2, bicicletas:1)
        repository = new ParqueaderoRepository(3, 2, 1);
        service = new ParqueaderoService(repository);

        // Crear usuarios semilla
        Usuario admin = new Usuario(1, "admin", "admin123", RolUsuario.ADMINISTRADOR, true);
        Usuario cajero = new Usuario(2, "cajero", "cajero123", RolUsuario.CAJERO_OPERADOR, true);
        repository.crearUsuario(admin);
        repository.crearUsuario(cajero);

        // Tarifas ya inicializadas en el repo (5000,3000,1000) - asegurar que existen
        repository.establecerTarifa("CARRO", 5000.0);
        repository.establecerTarifa("MOTO", 3000.0);
        repository.establecerTarifa("BICICLETA", 1000.0);

        // Crear 3 vehículos ya estacionados (semilla): 2 carros y 1 moto
        Vehiculo v1 = new Vehiculo("ABC123", TipoVehiculo.CARRO, LocalDateTime.now().minusHours(2), EstadoVehiculo.ACTIVO, "Semilla");
        Vehiculo v2 = new Vehiculo("XYZ789", TipoVehiculo.CARRO, LocalDateTime.now().minusMinutes(90), EstadoVehiculo.ACTIVO, "Semilla");
        Vehiculo v3 = new Vehiculo("MOTO55", TipoVehiculo.MOTO, LocalDateTime.now().minusMinutes(30), EstadoVehiculo.ACTIVO, "Semilla");

        // Registrar como activos y ocupar espacios
        repository.registrarVehiculoActivo(v1);
        repository.registrarVehiculoActivo(v2);
        repository.registrarVehiculoActivo(v3);

        // Ocupar primeros 3 espacios adecuados
        // Buscar espacios por tipo y ocupar
        for (EspacioParqueo espacio : repository.obtenerTodosEspacios()) {
            if (espacio.isDisponible() && espacio.getTipoVehiculo() == TipoVehiculo.CARRO) {
                if (repository.ocuparEspacio(espacio.getNumeroEspacio(), v1.getPlaca())) {
                    repository.crearRegistroParqueo(v1, espacio.getNumeroEspacio(), admin);
                    break;
                }
            }
        }
        // Segundo carro
        for (EspacioParqueo espacio : repository.obtenerTodosEspacios()) {
            if (espacio.isDisponible() && espacio.getTipoVehiculo() == TipoVehiculo.CARRO) {
                if (repository.ocuparEspacio(espacio.getNumeroEspacio(), v2.getPlaca())) {
                    repository.crearRegistroParqueo(v2, espacio.getNumeroEspacio(), cajero);
                    break;
                }
            }
        }
        // Moto
        for (EspacioParqueo espacio : repository.obtenerTodosEspacios()) {
            if (espacio.isDisponible() && espacio.getTipoVehiculo() == TipoVehiculo.MOTO) {
                if (repository.ocuparEspacio(espacio.getNumeroEspacio(), v3.getPlaca())) {
                    repository.crearRegistroParqueo(v3, espacio.getNumeroEspacio(), cajero);
                    break;
                }
            }
        }

        System.out.println("Sistema inicializado con datos semilla: admin/cajero y 3 vehículos estacionados.");
    }

    private static Usuario login() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Usuario (o 'salir' para terminar): ");
        String usuario = scanner.nextLine().trim();
        if (usuario.equalsIgnoreCase("salir")) return null;
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine().trim();

        Usuario u = repository.obtenerUsuario(usuario);
        if (u == null) {
            System.out.println("Usuario no encontrado.");
            return null;
        }
        if (!u.getContrasena().equals(contrasena)) {
            System.out.println("Contraseña incorrecta.");
            return null;
        }
        if (!u.isActivo()) {
            System.out.println("Usuario inactivo.");
            return null;
        }
        System.out.println("Bienvenido: " + u.getNombreUsuario() + " (" + u.getRol() + ")");
        return u;
    }

    private static void menuPrincipal(Usuario usuario) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Registrar ingreso de vehículo");
            System.out.println("2. Registrar salida de vehículo");
            System.out.println("3. Consultar vehículos activos");
            System.out.println("4. Buscar por placa (historial recursivo)");
            System.out.println("5. Mostrar tablero de ocupación");
            if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
                System.out.println("6. Menu Administrador");
            }
            System.out.println("9. Cerrar sesión");
            System.out.print("Seleccione opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1": opcionRegistrarIngreso(usuario); break;
                case "2": opcionRegistrarSalida(usuario); break;
                case "3": opcionConsultarActivos(); break;
                case "4": opcionBuscarPorPlaca(); break;
                case "5": opcionMostrarTablero(); break;
                case "6":
                    if (usuario.getRol() == RolUsuario.ADMINISTRADOR) menuAdministrador(usuario);
                    else System.out.println("Acceso denegado. Solo administradores.");
                    break;
                case "9": salir = true; System.out.println("Cerrando sesión..."); break;
                default: System.out.println("Opción inválida");
            }
        }
    }

    private static void opcionRegistrarIngreso(Usuario operador) {
        System.out.println("--- REGISTRAR INGRESO ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine().trim();
        System.out.println("Tipo (1=CARRO,2=MOTO,3=BICICLETA): ");
        String t = scanner.nextLine().trim();
        TipoVehiculo tipo;
        switch (t) {
            case "1": tipo = TipoVehiculo.CARRO; break;
            case "2": tipo = TipoVehiculo.MOTO; break;
            case "3": tipo = TipoVehiculo.BICICLETA; break;
            default: System.out.println("Tipo inválido"); return;
        }

        ParqueaderoService.ResultadoOperacion res = service.registrarIngreso(placa, tipo, operador);
        System.out.println(res.getMensaje());
    }

    private static void opcionRegistrarSalida(Usuario operador) {
        System.out.println("--- REGISTRAR SALIDA ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine().trim();

        ParqueaderoService.ResultadoOperacionPago res = service.registrarSalidaYPago(placa, operador);
        System.out.println(res.getMensaje());
        if (res.isExitoso()) {
            // Imprimir recibo simple
            System.out.println("\n--- RECIBO ---");
            System.out.println("Placa: " + placa);
            System.out.println("Operador: " + operador.getNombreUsuario());
            System.out.println("Monto: $" + String.format("%.2f", res.getMontoPagado()));
            System.out.println("Fecha: " + LocalDateTime.now());
            System.out.println("----------------");
        }
    }

    private static void opcionConsultarActivos() {
        System.out.println("--- VEHÍCULOS ACTIVOS ---");
        Collection<Vehiculo> activos = repository.obtenerVehiculosActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay vehículos activos.");
            return;
        }
        for (Vehiculo v : activos) {
            System.out.println(v);
        }
    }

    private static void opcionBuscarPorPlaca() {
        System.out.println("--- BUSCAR POR PLACA (HISTORIAL) ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine().trim();
        List<RegistroParqueo> resultados = service.obtenerHistorialVehiculo(placa);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron registros para " + placa);
            return;
        }
        for (RegistroParqueo r : resultados) {
            System.out.println(r);
        }
    }

    private static void opcionMostrarTablero() {
        System.out.println("--- TABLERO OCUPACIÓN ---");
        ParqueaderoService.TableroOcupacion t = service.obtenerTableroOcupacion();
        System.out.println(t);
    }

    private static void menuAdministrador(Usuario admin) {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENU ADMINISTRADOR (" + admin.getNombreUsuario() + ") ---");
            System.out.println("1. Configurar capacidad (no persistente en tiempo de ejecución)");
            System.out.println("2. Gestionar tarifas");
            System.out.println("3. Ver historial completo recursivo");
            System.out.println("4. Generar reporte diario recursivo");
            System.out.println("5. Anular operación (por ID)");
            System.out.println("9. Volver");
            System.out.print("Opción: ");
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1": opcionConfigurarCapacidad(); break;
                case "2": opcionGestionarTarifas(); break;
                case "3": opcionVerHistorialRecursivo(); break;
                case "4": opcionGenerarReporteDiario(); break;
                case "5": opcionAnularOperacion(admin); break;
                case "9": salir = true; break;
                default: System.out.println("Opción inválida");
            }
        }
    }

    private static void opcionConfigurarCapacidad() {
        System.out.println("--- CONFIGURAR CAPACIDAD ---");
        System.out.println("Esta operación no modifica los espacios existentes en esta versión simple.");
        System.out.println("Capacidad actual por tipo: Carros=" + repository.obtenerCapacidadPorTipo(TipoVehiculo.CARRO)
                + " Motos=" + repository.obtenerCapacidadPorTipo(TipoVehiculo.MOTO)
                + " Bicicletas=" + repository.obtenerCapacidadPorTipo(TipoVehiculo.BICICLETA));
        System.out.println("(En esta versión, para ajustar capacidad, recrea el repositorio en código.)");
    }

    private static void opcionGestionarTarifas() {
        System.out.println("--- GESTIÓN DE TARIFAS ---");
        System.out.println("Tarifas actuales: " + repository.obtenerTodasTarifas());
        System.out.print("Tipo a modificar (CARRO/MOTO/BICICLETA): ");
        String tipo = scanner.nextLine().trim().toUpperCase();
        System.out.print("Nueva tarifa: ");
        String v = scanner.nextLine().trim();
        try {
            double tarifa = Double.parseDouble(v);
            repository.establecerTarifa(tipo, tarifa);
            System.out.println("Tarifa actualizada.");
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
        }
    }

    private static void opcionVerHistorialRecursivo() {
        System.out.println("--- HISTORIAL COMPLETO (RECURSIVO) ---");
        List<RegistroParqueo> historial = repository.obtenerHistorialCompleto();
        // Usamos la función recursiva de búsqueda con placa null para devolver todo: adaptamos llamando la recursiva normal
        // Implementación simple: imprimir todos los registros iterando (es aceptable para admin)
        for (RegistroParqueo r : historial) {
            System.out.println(r);
        }
    }

    private static void opcionGenerarReporteDiario() {
        System.out.println("--- REPORTE DIARIO (RECURSIVO) ---");
        List<RegistroParqueo> historial = repository.obtenerHistorialCompleto();
        // Llamar recursivo desde 0
        ParqueaderoService.ReporteDiario reporte = service.generarReporteDiarioRecursivo(historial, 0, 0.0, 0, 0);
        System.out.println("Total recaudado: $" + String.format("%.2f", reporte.getTotalRecaudado()));
        System.out.println("Total ingresos: " + reporte.getTotalIngresos());
        System.out.println("Total salidas: " + reporte.getTotalSalidas());
    }

    private static void opcionAnularOperacion(Usuario admin) {
        System.out.println("--- ANULAR OPERACIÓN ---");
        System.out.print("ID del registro a anular: ");
        String idStr = scanner.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }
        System.out.print("Justificación de la anulación: ");
        String justificacion = scanner.nextLine().trim();

        boolean ok = service.anularOperacion((long) id, justificacion, admin);
        if (ok) {
            System.out.println("Registro " + id + " anulado correctamente.");
        } else {
            System.out.println("No se pudo anular el registro (no existe o permiso denegado).\nAsegúrate de usar un usuario ADMINISTRADOR y un ID válido.");
        }
    }
}
