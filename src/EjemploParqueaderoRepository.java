import java.time.LocalDateTime;

/**
 * Clase de ejemplo que demuestra el uso práctico de ParqueaderoRepository.
 * Incluye casos de uso completos del sistema.
 */
public class EjemploParqueaderoRepository {

    public static void main(String[] args) {
        // ==================== INICIALIZACIÓN ====================
        System.out.println("=== INICIALIZANDO PARQUEADERO ===");

        // Crear repositorio con capacidades específicas
        ParqueaderoRepository repo = new ParqueaderoRepository(50, 30, 20);
        System.out.println("✓ Parqueadero creado");
        System.out.println("  - Capacidad total: " + repo.obtenerCapacidadTotal());
        System.out.println("  - Carros: " + repo.obtenerCapacidadPorTipo(TipoVehiculo.CARRO));
        System.out.println("  - Motos: " + repo.obtenerCapacidadPorTipo(TipoVehiculo.MOTO));
        System.out.println("  - Bicicletas: " + repo.obtenerCapacidadPorTipo(TipoVehiculo.BICICLETA));

        // ==================== CREAR USUARIOS ====================
        System.out.println("\n=== CREANDO USUARIOS ===");

        Usuario admin = new Usuario(1, "admin", "admin123", RolUsuario.ADMINISTRADOR, true);
        Usuario cajero1 = new Usuario(2, "cajero1", "pass123", RolUsuario.CAJERO_OPERADOR, true);
        Usuario cajero2 = new Usuario(3, "cajero2", "pass456", RolUsuario.CAJERO_OPERADOR, true);

        repo.crearUsuario(admin);
        repo.crearUsuario(cajero1);
        repo.crearUsuario(cajero2);

        System.out.println("✓ Usuarios creados: " + repo.obtenerTodosUsuarios().size());

        // Intentar crear usuario duplicado
        Usuario duplicado = new Usuario(4, "admin", "otrapwd", RolUsuario.CAJERO_OPERADOR, true);
        boolean resultado = repo.crearUsuario(duplicado);
        System.out.println("  - Intento de crear usuario duplicado: " + (resultado ? "EXITOSO" : "RECHAZADO (validación correcta)"));

        // ==================== CONFIGURAR TARIFAS ====================
        System.out.println("\n=== CONFIGURANDO TARIFAS ===");

        repo.establecerTarifa("CARRO", 5000.0);
        repo.establecerTarifa("MOTO", 3000.0);
        repo.establecerTarifa("BICICLETA", 1000.0);

        System.out.println("✓ Tarifas configuradas:");
        for (String tipo : new String[]{"CARRO", "MOTO", "BICICLETA"}) {
            System.out.println("  - " + tipo + ": $" + repo.obtenerTarifa(tipo));
        }

        // ==================== REGISTRAR VEHÍCULOS ====================
        System.out.println("\n=== REGISTRANDO VEHÍCULOS ===");

        // Vehículo 1: Carro
        Vehiculo carro1 = new Vehiculo(
            "ABC-123",
            TipoVehiculo.CARRO,
            LocalDateTime.of(2026, 5, 28, 8, 30),
            EstadoVehiculo.ACTIVO,
            "Vehículo en buen estado"
        );

        // Vehículo 2: Moto
        Vehiculo moto1 = new Vehiculo(
            "XYZ-789",
            TipoVehiculo.MOTO,
            LocalDateTime.of(2026, 5, 28, 9, 15),
            EstadoVehiculo.ACTIVO,
            "Sin observaciones"
        );

        // Vehículo 3: Bicicleta
        Vehiculo bicicleta1 = new Vehiculo(
            "BIK-001",
            TipoVehiculo.BICICLETA,
            LocalDateTime.of(2026, 5, 28, 10, 0),
            EstadoVehiculo.ACTIVO,
            "Bicicleta de montaña"
        );

        repo.registrarVehiculoActivo(carro1);
        repo.registrarVehiculoActivo(moto1);
        repo.registrarVehiculoActivo(bicicleta1);

        System.out.println("✓ Vehículos registrados como activos:");
        System.out.println("  - Total activos: " + repo.obtenerCantidadVehiculosActivos());

        // Intentar registrar placa duplicada
        Vehiculo carrodup = new Vehiculo("ABC-123", TipoVehiculo.CARRO, LocalDateTime.now(),
                                         EstadoVehiculo.ACTIVO, "Intento duplicado");
        boolean regdup = repo.registrarVehiculoActivo(carrodup);
        System.out.println("  - Intento de registrar placa duplicada: " + (regdup ? "EXITOSO" : "RECHAZADO (validación correcta)"));

        // ==================== ASIGNAR ESPACIOS ====================
        System.out.println("\n=== ASIGNANDO ESPACIOS ===");

        // Buscar y ocupar espacio para carro
        EspacioParqueo espacioCarro = repo.obtenerEspacioDisponible(TipoVehiculo.CARRO);
        if (espacioCarro != null) {
            repo.ocuparEspacio(espacioCarro.getNumeroEspacio(), carro1.getPlaca());
            System.out.println("✓ Carro " + carro1.getPlaca() + " asignado al espacio " + espacioCarro.getNumeroEspacio());
        }

        // Buscar y ocupar espacio para moto
        EspacioParqueo espacioMoto = repo.obtenerEspacioDisponible(TipoVehiculo.MOTO);
        if (espacioMoto != null) {
            repo.ocuparEspacio(espacioMoto.getNumeroEspacio(), moto1.getPlaca());
            System.out.println("✓ Moto " + moto1.getPlaca() + " asignada al espacio " + espacioMoto.getNumeroEspacio());
        }

        // Buscar y ocupar espacio para bicicleta
        EspacioParqueo espacioBicicleta = repo.obtenerEspacioDisponible(TipoVehiculo.BICICLETA);
        if (espacioBicicleta != null) {
            repo.ocuparEspacio(espacioBicicleta.getNumeroEspacio(), bicicleta1.getPlaca());
            System.out.println("✓ Bicicleta " + bicicleta1.getPlaca() + " asignada al espacio " + espacioBicicleta.getNumeroEspacio());
        }

        System.out.println("\n  Espacios disponibles por tipo:");
        System.out.println("  - Carros: " + repo.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.CARRO));
        System.out.println("  - Motos: " + repo.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.MOTO));
        System.out.println("  - Bicicletas: " + repo.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.BICICLETA));

        // ==================== CREAR REGISTROS DE PARQUEO ====================
        System.out.println("\n=== CREANDO REGISTROS DE PARQUEO ===");

        RegistroParqueo registro1 = repo.crearRegistroParqueo(carro1, espacioCarro.getNumeroEspacio(), cajero1);
        registro1.setValorPagado(5000.0);
        repo.actualizarRegistroParqueo(registro1);

        RegistroParqueo registro2 = repo.crearRegistroParqueo(moto1, espacioMoto.getNumeroEspacio(), cajero2);
        registro2.setValorPagado(3000.0);
        repo.actualizarRegistroParqueo(registro2);

        RegistroParqueo registro3 = repo.crearRegistroParqueo(bicicleta1, espacioBicicleta.getNumeroEspacio(), cajero1);
        registro3.setValorPagado(1000.0);
        repo.actualizarRegistroParqueo(registro3);

        System.out.println("✓ Registros creados: " + repo.obtenerCantidadRegistros());
        System.out.println("  - Registro #" + registro1.getId() + ": " + carro1.getPlaca() + " ($" + registro1.getValorPagado() + ")");
        System.out.println("  - Registro #" + registro2.getId() + ": " + moto1.getPlaca() + " ($" + registro2.getValorPagado() + ")");
        System.out.println("  - Registro #" + registro3.getId() + ": " + bicicleta1.getPlaca() + " ($" + registro3.getValorPagado() + ")");

        // ==================== CONSULTAS Y REPORTES ====================
        System.out.println("\n=== ESTADO DEL PARQUEADERO ===");

        System.out.println("✓ Ocupación:");
        System.out.println("  - Espacios ocupados: " + repo.obtenerEspaciosOcupados());
        System.out.println("  - Capacidad total: " + repo.obtenerCapacidadTotal());
        System.out.println("  - Porcentaje de ocupación: " + String.format("%.2f%%", repo.obtenerPorcentajeOcupacion()));
        System.out.println("  - Parqueadero lleno: " + (repo.estaParqueaderoLleno() ? "SÍ" : "NO"));

        // ==================== PROCESAR SALIDA ====================
        System.out.println("\n=== PROCESANDO SALIDA DE VEHÍCULO ===");

        // Simular salida del carro
        carro1.setHoraSalida(LocalDateTime.of(2026, 5, 28, 12, 30));
        carro1.setEstado(EstadoVehiculo.FINALIZADO);
        repo.removerVehiculoActivo(carro1.getPlaca());
        repo.liberarEspacio(espacioCarro.getNumeroEspacio());

        registro1.setEstadoPago(EstadoPago.PAGADO);
        registro1.setNovedades("Pago realizado sin problemas");
        repo.actualizarRegistroParqueo(registro1);

        System.out.println("✓ Carro " + carro1.getPlaca() + " salió del parqueadero");
        System.out.println("  - Hora entrada: " + carro1.getHoraIngreso());
        System.out.println("  - Hora salida: " + carro1.getHoraSalida());
        System.out.println("  - Estado pago: " + registro1.getEstadoPago());
        System.out.println("  - Vehículos activos ahora: " + repo.obtenerCantidadVehiculosActivos());

        // ==================== FILTRAR REGISTROS ====================
        System.out.println("\n=== CONSULTAS DE AUDITORÍA ===");

        System.out.println("✓ Registros por usuario:");
        for (RegistroParqueo reg : repo.obtenerRegistrosPorUsuario(cajero1)) {
            System.out.println("  - Registros de " + reg.getUsuarioResponsable().getNombreUsuario() +
                             ": " + reg.getVehiculo().getPlaca());
        }

        System.out.println("✓ Registros por estado de pago (PAGADO):");
        for (RegistroParqueo reg : repo.obtenerRegistrosPorEstadoPago(EstadoPago.PAGADO)) {
            System.out.println("  - " + reg.getVehiculo().getPlaca() + " ($" + reg.getValorPagado() + ")");
        }

        System.out.println("\n✓ Historial completo (" + repo.obtenerCantidadRegistros() + " registros):");
        for (RegistroParqueo reg : repo.obtenerHistorialCompleto()) {
            System.out.println("  - ID: " + reg.getId() + " | Placa: " + reg.getVehiculo().getPlaca() +
                             " | Estado: " + reg.getEstadoPago() + " | Usuario: " +
                             reg.getUsuarioResponsable().getNombreUsuario());
        }

        // ==================== RESUMEN FINAL ====================
        System.out.println("\n=== RESUMEN FINAL ===");
        System.out.println("✓ Usuarios en sistema: " + repo.obtenerTodosUsuarios().size());
        System.out.println("✓ Vehículos activos: " + repo.obtenerCantidadVehiculosActivos());
        System.out.println("✓ Registros totales: " + repo.obtenerCantidadRegistros());
        System.out.println("✓ Ocupación: " + String.format("%.2f%%", repo.obtenerPorcentajeOcupacion()));
    }
}

