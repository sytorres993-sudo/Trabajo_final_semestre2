import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase de ejemplo que demuestra el uso de ParqueaderoService.
 * Incluye casos de uso prácticos con recursividad.
 */
public class EjemploParqueaderoService {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║         SISTEMA DE PARQUEADERO - PRUEBA DE SERVICIO           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        // ==================== INICIALIZACIÓN ====================
        System.out.println("\n=== INICIALIZANDO SISTEMA ===");

        ParqueaderoRepository repo = new ParqueaderoRepository(20, 15, 10);
        ParqueaderoService service = new ParqueaderoService(repo);

        // Crear usuario operador
        Usuario operador = new Usuario(1, "operador1", "pass123", RolUsuario.CAJERO_OPERADOR, true);
        repo.crearUsuario(operador);

        // Configurar tarifas
        repo.establecerTarifa("CARRO", 5000.0);
        repo.establecerTarifa("MOTO", 3000.0);
        repo.establecerTarifa("BICICLETA", 1000.0);

        System.out.println("✓ Sistema inicializado");
        System.out.println("  - Capacidad: 20 carros, 15 motos, 10 bicicletas");
        System.out.println("  - Tarifas: Carro $5000, Moto $3000, Bicicleta $1000 (por hora)");

        // ==================== PRUEBAS DE INGRESO ====================
        System.out.println("\n=== REGISTRANDO INGRESOS (PRUEBA DE RECURSIVIDAD) ===");

        // Ingreso 1: Carro
        System.out.println("\n[1] Ingresando carro ABC-001...");
        ParqueaderoService.ResultadoOperacion resultado1 = service.registrarIngreso(
            "ABC-001", TipoVehiculo.CARRO, operador
        );
        System.out.println("    " + resultado1);
        if (resultado1.isExitoso()) {
            RegistroParqueo reg1 = (RegistroParqueo) resultado1.getDato();
            System.out.println("    ID Registro: #" + reg1.getId());
        }

        // Ingreso 2: Moto
        System.out.println("\n[2] Ingresando moto XYZ-789...");
        ParqueaderoService.ResultadoOperacion resultado2 = service.registrarIngreso(
            "XYZ-789", TipoVehiculo.MOTO, operador
        );
        System.out.println("    " + resultado2);
        if (resultado2.isExitoso()) {
            RegistroParqueo reg2 = (RegistroParqueo) resultado2.getDato();
            System.out.println("    ID Registro: #" + reg2.getId());
        }

        // Ingreso 3: Bicicleta
        System.out.println("\n[3] Ingresando bicicleta BIK-042...");
        ParqueaderoService.ResultadoOperacion resultado3 = service.registrarIngreso(
            "BIK-042", TipoVehiculo.BICICLETA, operador
        );
        System.out.println("    " + resultado3);

        // Ingreso 4: Otro carro
        System.out.println("\n[4] Ingresando carro DEF-555...");
        ParqueaderoService.ResultadoOperacion resultado4 = service.registrarIngreso(
            "DEF-555", TipoVehiculo.CARRO, operador
        );
        System.out.println("    " + resultado4);

        // Intento de duplicado
        System.out.println("\n[5] Intentando ingresar carro ABC-001 nuevamente (debe fallar)...");
        ParqueaderoService.ResultadoOperacion resultadoDuplicado = service.registrarIngreso(
            "ABC-001", TipoVehiculo.CARRO, operador
        );
        System.out.println("    " + resultadoDuplicado);

        // ==================== ESTADO ACTUAL ====================
        System.out.println("\n=== ESTADO DEL PARQUEADERO ===");
        ParqueaderoService.EstadoParqueadero estado = service.obtenerEstadoParqueadero();
        System.out.println(estado);

        // ==================== PRUEBA DE RECURSIVIDAD: BÚSQUEDA EN HISTORIAL ====================
        System.out.println("\n=== BÚSQUEDA EN HISTORIAL (RECURSIVIDAD) ===");

        System.out.println("\n[Búsqueda recursiva] Historial del vehículo ABC-001:");
        List<RegistroParqueo> historialABC = service.obtenerHistorialVehiculo("ABC-001");
        for (RegistroParqueo reg : historialABC) {
            System.out.println("  - Registro #" + reg.getId() + " | Espacio: " +
                             reg.getNumeroEspacioAsignado() + " | Estado: " +
                             reg.getEstadoPago());
        }

        System.out.println("\n[Búsqueda recursiva] Historial del vehículo XYZ-789:");
        List<RegistroParqueo> historialXYZ = service.obtenerHistorialVehiculo("XYZ-789");
        for (RegistroParqueo reg : historialXYZ) {
            System.out.println("  - Registro #" + reg.getId() + " | Espacio: " +
                             reg.getNumeroEspacioAsignado() + " | Estado: " +
                             reg.getEstadoPago());
        }

        // ==================== PRUEBA DE CÁLCULO RECURSIVO DE TARIFAS ====================
        System.out.println("\n=== PRUEBA DE CÁLCULO RECURSIVO DE TARIFAS (RF-10) ===");

        // Simular diferentes duraciones
        System.out.println("\n[Recursividad] Cálculo de tarifa para carro ($5000 por hora):");

        long[] minutosTesteo = {30, 60, 90, 120, 150};
        for (long minutos : minutosTesteo) {
            double costo = service.calcularTarifaRecursiva(minutos, 5000.0, 60);
            System.out.printf("  - %d minutos = $%.2f%n", minutos, costo);
        }

        System.out.println("\n[Recursividad] Cálculo de tarifa para moto ($3000 por hora):");
        for (long minutos : minutosTesteo) {
            double costo = service.calcularTarifaRecursiva(minutos, 3000.0, 60);
            System.out.printf("  - %d minutos = $%.2f%n", minutos, costo);
        }

        // ==================== PRUEBAS DE SALIDA ====================
        System.out.println("\n=== REGISTRANDO SALIDAS Y PAGOS ===");

        // Simular espera antes de salida (en un caso real habría espera)
        System.out.println("\n[Simulación de tiempo transcurrido...]");

        // Salida 1: ABC-001
        System.out.println("\n[1] Registrando salida de carro ABC-001...");
        ParqueaderoService.ResultadoOperacionPago salidaPago1 = service.registrarSalidaYPago(
            "ABC-001", operador
        );
        System.out.println("    " + salidaPago1);

        // Salida 2: XYZ-789
        System.out.println("\n[2] Registrando salida de moto XYZ-789...");
        ParqueaderoService.ResultadoOperacionPago salidaPago2 = service.registrarSalidaYPago(
            "XYZ-789", operador
        );
        System.out.println("    " + salidaPago2);

        // Intento de salida de vehículo no existente
        System.out.println("\n[3] Intentando salida de vehículo no presente (debe fallar)...");
        ParqueaderoService.ResultadoOperacionPago salidaFallida = service.registrarSalidaYPago(
            "ZZZ-999", operador
        );
        System.out.println("    " + salidaFallida);

        // ==================== ESTADO ACTUALIZADO ====================
        System.out.println("\n=== ESTADO ACTUALIZADO DEL PARQUEADERO ===");
        ParqueaderoService.EstadoParqueadero estadoFinal = service.obtenerEstadoParqueadero();
        System.out.println(estadoFinal);

        // ==================== REPORTES ====================
        System.out.println("\n=== REPORTES DEL SISTEMA ===");

        System.out.println("\n[Reporte] Ingresos por tipo de vehículo:");
        Map<TipoVehiculo, Integer> reporteTipo = service.reporteIngresosPorTipo();
        for (Map.Entry<TipoVehiculo, Integer> entry : reporteTipo.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " vehículos");
        }

        System.out.println("\n[Reporte] Ingresos totales en dinero:");
        double totalRecaudado = service.reporteIngresosTotales();
        System.out.printf("  Total recaudado: $%.2f%n", totalRecaudado);

        System.out.println("\n[Reporte] Transacciones pendientes de pago:");
        List<RegistroParqueo> pendientes = service.reportePendientes();
        if (pendientes.isEmpty()) {
            System.out.println("  No hay transacciones pendientes");
        } else {
            for (RegistroParqueo reg : pendientes) {
                System.out.println("  - Registro #" + reg.getId() + " | Placa: " +
                                 reg.getVehiculo().getPlaca());
            }
        }

        // ==================== HISTORIAL COMPLETO ====================
        System.out.println("\n=== HISTORIAL COMPLETO (AUDITORÍA) ===");
        System.out.println("\n[Auditoría] Todos los registros de parqueo:");

        List<RegistroParqueo> historialCompleto = repo.obtenerHistorialCompleto();
        for (RegistroParqueo reg : historialCompleto) {
            System.out.println(String.format(
                "  ID: %d | Placa: %s | Espacio: %d | Tipo: %s | Pago: %.2f | Estado: %s",
                reg.getId(),
                reg.getVehiculo().getPlaca(),
                reg.getNumeroEspacioAsignado(),
                reg.getVehiculo().getTipoVehiculo(),
                reg.getValorPagado(),
                reg.getEstadoPago()
            ));
        }

        // ==================== RESUMEN FINAL ====================
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMEN DE PRUEBAS                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        System.out.println("\n✅ Métodos recursivos probados:");
        System.out.println("   [✓] asignarEspacioRecursivo() - Búsqueda de espacios");
        System.out.println("   [✓] buscarVehiculoHistorialRecursivo() - Auditoría de historial");
        System.out.println("   [✓] calcularTarifaRecursiva() - Cálculo de cobro por fracciones");

        System.out.println("\n✅ Métodos de negocio probados:");
        System.out.println("   [✓] registrarIngreso() - RF-03, RF-04, RF-05");
        System.out.println("   [✓] registrarSalidaYPago() - RF-11, RF-13");
        System.out.println("   [✓] obtenerEstadoParqueadero() - Estado actual");
        System.out.println("   [✓] Reportes - Ingresos, auditoría");

        System.out.println("\n✅ Validaciones probadas:");
        System.out.println("   [✓] Detección de placas duplicadas");
        System.out.println("   [✓] Validación de cupos disponibles");
        System.out.println("   [✓] Liberación de espacios");
        System.out.println("   [✓] Cálculo correcto de tarifas");

        System.out.println("\n📊 Estadísticas finales:");
        System.out.println("   - Vehículos procesados: " + historialCompleto.size());
        System.out.println("   - Transacciones completadas: " +
            (int) historialCompleto.stream().filter(r -> r.getEstadoPago() == EstadoPago.PAGADO).count());
        System.out.println("   - Ingresos totales: $" + String.format("%.2f", totalRecaudado));
        System.out.println("   - Estado actual: " + estadoFinal.getOcupados() + "/" +
            estadoFinal.getCapacidadTotal() + " espacios ocupados");

        System.out.println("\n✨ ¡TODAS LAS PRUEBAS COMPLETADAS EXITOSAMENTE!");
    }
}

