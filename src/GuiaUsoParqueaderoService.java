/**
 * GUÍA DE USO - ParqueaderoService
 * ================================
 *
 * Esta clase contiene la lógica de negocio del sistema de parqueadero.
 * Utiliza RecursiónParqueaderoRepository para acceder a datos y aplica
 * RECURSIVIDAD en métodos críticos.
 *
 *
 * INICIALIZACIÓN:
 * ===============
 *
 * // 1. Crear repositorio
 * ParqueaderoRepository repo = new ParqueaderoRepository(20, 15, 10);
 *
 * // 2. Crear servicio
 * ParqueaderoService service = new ParqueaderoService(repo);
 *
 * // 3. Configurar sistema
 * Usuario operador = new Usuario(1, "operador1", "pass", RolUsuario.CAJERO_OPERADOR, true);
 * repo.crearUsuario(operador);
 * repo.establecerTarifa("CARRO", 5000.0);
 * repo.establecerTarifa("MOTO", 3000.0);
 * repo.establecerTarifa("BICICLETA", 1000.0);
 *
 *
 * MÉTODOS RECURSIVOS:
 * ===================
 *
 * 1. asignarEspacioRecursivo()
 *    ─────────────────────────
 *    Busca recursivamente un espacio disponible del tipo especificado.
 *
 *    Uso:
 *    ────
 *    List<EspacioParqueo> espacios = new ArrayList<>(repo.obtenerTodosEspacios());
 *    EspacioParqueo espacio = service.asignarEspacioRecursivo(espacios, 0, TipoVehiculo.CARRO);
 *
 *    if (espacio != null) {
 *        System.out.println("Espacio asignado: #" + espacio.getNumeroEspacio());
 *    } else {
 *        System.out.println("No hay espacios disponibles");
 *    }
 *
 *    Parámetros:
 *    ───────────
 *    - espacios: List<EspacioParqueo> - Lista de todos los espacios
 *    - index: int - Comienza en 0 (primer elemento)
 *    - tipo: TipoVehiculo - CARRO, MOTO o BICICLETA
 *
 *    Retorna:
 *    ────────
 *    - EspacioParqueo: primer espacio disponible del tipo
 *    - null: si no hay disponible
 *
 *
 * 2. buscarVehiculoHistorialRecursivo()
 *    ──────────────────────────────────
 *    Busca todas las coincidencias de una placa en el historial.
 *
 *    Uso:
 *    ────
 *    List<RegistroParqueo> historial = service.obtenerHistorialVehiculo("ABC-001");
 *
 *    for (RegistroParqueo registro : historial) {
 *        System.out.println("Registro #" + registro.getId());
 *        System.out.println("  Espacio: " + registro.getNumeroEspacioAsignado());
 *        System.out.println("  Pago: $" + registro.getValorPagado());
 *        System.out.println("  Estado: " + registro.getEstadoPago());
 *    }
 *
 *    O directamente con el método recursivo:
 *    ────────────────────────────────────────
 *    List<RegistroParqueo> historial = service.buscarVehiculoHistorialRecursivo(
 *        repo.obtenerHistorialCompleto(),
 *        0,  // índice inicial
 *        "ABC-001",  // placa a buscar
 *        new ArrayList<>()  // acumulador de resultados
 *    );
 *
 *    Parámetros:
 *    ───────────
 *    - historial: List<RegistroParqueo> - Historial completo
 *    - index: int - Comienza en 0
 *    - placa: String - Placa exacta a buscar
 *    - resultados: List<RegistroParqueo> - Acumulador (pasar vacío)
 *
 *    Retorna:
 *    ────────
 *    - List<RegistroParqueo>: todos los registros de esa placa
 *
 *    Casos de uso:
 *    ─────────────
 *    // Auditoría: ver todo el historial de un vehículo
 *    List<RegistroParqueo> registros = service.obtenerHistorialVehiculo("XYZ-789");
 *
 *    // Investigación: ¿cuántas veces vino un vehículo?
 *    int visitas = service.obtenerHistorialVehiculo("ABC-001").size();
 *
 *    // Facturación: ¿cuánto debe un cliente?
 *    double total = service.obtenerHistorialVehiculo("DEF-555")
 *        .stream()
 *        .filter(r -> r.getEstadoPago() == EstadoPago.PENDIENTE)
 *        .mapToDouble(RegistroParqueo::getValorPagado)
 *        .sum();
 *
 *
 * 3. calcularTarifaRecursiva()
 *    ─────────────────────────
 *    Calcula el costo total de forma recursiva por fracciones de tiempo.
 *
 *    Uso:
 *    ────
 *    long minutosParqueo = 150;  // 2.5 horas
 *    double tarifaPorHora = 5000.0;  // $5000 por hora
 *    long intervalo = 60;  // cobrar por cada 60 minutos
 *
 *    double costo = service.calcularTarifaRecursiva(minutosParqueo, tarifaPorHora, intervalo);
 *    System.out.println("Costo total: $" + costo);  // Imprime: $15000.0
 *
 *    Parámetros:
 *    ───────────
 *    - minutosRestantes: long - Minutos a cobrar
 *    - tarifaFraccion: double - Precio por intervalo
 *    - intervaloFraccion: long - Duración del intervalo (minutos)
 *
 *    Retorna:
 *    ────────
 *    - double: costo total acumulado
 *
 *    Tabla de ejemplos (tarifa $5000 por hora):
 *    ───────────────────────────────────────────
 *    Minutos → Bloques → Costo
 *    15        1        $5000
 *    30        1        $5000
 *    60        1        $5000
 *    61        2        $10000
 *    90        2        $10000
 *    120       2        $10000
 *    121       3        $15000
 *    150       3        $15000
 *
 *
 * MÉTODOS DE NEGOCIO:
 * ===================
 *
 * registrarIngreso()
 * ──────────────────
 * Registra la entrada completa de un vehículo al parqueadero.
 *
 * Parámetros:
 *   - placa: String - Placa del vehículo
 *   - tipo: TipoVehiculo - CARRO, MOTO, BICICLETA
 *   - operador: Usuario - Usuario que registra
 *
 * Retorna:
 *   - ResultadoOperacion con:
 *     * exitoso: boolean
 *     * mensaje: String (descriptivo)
 *     * dato: RegistroParqueo (si fue exitoso)
 *
 * Ejemplo:
 * ────────
 * ParqueaderoService.ResultadoOperacion resultado = service.registrarIngreso(
 *     "ABC-001",
 *     TipoVehiculo.CARRO,
 *     operador
 * );
 *
 * if (resultado.isExitoso()) {
 *     RegistroParqueo registro = (RegistroParqueo) resultado.getDato();
 *     System.out.println("✓ " + resultado.getMensaje());
 *     System.out.println("Registro #" + registro.getId());
 * } else {
 *     System.out.println("✗ " + resultado.getMensaje());
 * }
 *
 * Validaciones realizadas:
 *   ✓ Placa no esté ya activa
 *   ✓ Haya cupos disponibles para el tipo
 *   ✓ Se asigne espacio correctamente
 *   ✓ Se registre vehículo como activo
 *
 *
 * registrarSalidaYPago()
 * ──────────────────────
 * Registra la salida de un vehículo, calcula pago y libera espacio.
 *
 * Parámetros:
 *   - placa: String - Placa del vehículo a salir
 *   - operador: Usuario - Usuario que registra
 *
 * Retorna:
 *   - ResultadoOperacionPago con:
 *     * exitoso: boolean
 *     * mensaje: String
 *     * montoPagado: double (monto cobrado)
 *
 * Ejemplo:
 * ────────
 * ParqueaderoService.ResultadoOperacionPago pago = service.registrarSalidaYPago(
 *     "ABC-001",
 *     operador
 * );
 *
 * System.out.println(pago);  // Imprime estado y monto
 * if (pago.isExitoso()) {
 *     System.out.println("Monto pagado: $" + pago.getMontoPagado());
 * }
 *
 * Proceso interno:
 *   1. Busca vehículo activo
 *   2. Busca registro activo recursivamente
 *   3. Calcula duración (entrada a salida)
 *   4. Calcula tarifa recursivamente
 *   5. Actualiza vehículo y registro
 *   6. Libera espacio
 *   7. Marca pago como PAGADO
 *
 *
 * MÉTODOS DE CONSULTA:
 * ====================
 *
 * obtenerEstadoParqueadero()
 * ──────────────────────────
 * Obtiene el estado actual completo.
 *
 * Retorna: EstadoParqueadero con:
 *   - ocupados: int
 *   - capacidadTotal: int
 *   - disponiblesCarros, disponiblesMotos, disponiblesBicicletas: int
 *   - capacidadCarros, capacidadMotos, capacidadBicicletas: int
 *   - porcentajeOcupacion: double
 *
 * Uso:
 * ────
 * ParqueaderoService.EstadoParqueadero estado = service.obtenerEstadoParqueadero();
 * System.out.println(estado);  // Imprime tabla formateada
 *
 *
 * reporteIngresosPorTipo()
 * ────────────────────────
 * Cuenta ingresos totales por cada tipo de vehículo.
 *
 * Retorna: Map<TipoVehiculo, Integer>
 *
 * Uso:
 * ────
 * Map<TipoVehiculo, Integer> reporte = service.reporteIngresosPorTipo();
 * reporte.forEach((tipo, cantidad) ->
 *     System.out.println(tipo + ": " + cantidad + " vehículos")
 * );
 *
 *
 * reporteIngresosTotales()
 * ────────────────────────
 * Suma total recaudado de todos los pagos completados.
 *
 * Retorna: double (total en dinero)
 *
 * Uso:
 * ────
 * double recaudado = service.reporteIngresosTotales();
 * System.out.printf("Total recaudado: $%.2f%n", recaudado);
 *
 *
 * reportePendientes()
 * ───────────────────
 * Lista registros aún pendientes de pago.
 *
 * Retorna: List<RegistroParqueo> con estado PENDIENTE
 *
 * Uso:
 * ────
 * List<RegistroParqueo> pendientes = service.reportePendientes();
 * for (RegistroParqueo reg : pendientes) {
 *     System.out.println(reg.getVehiculo().getPlaca() + ": $" + reg.getValorPagado());
 * }
 *
 *
 * FLUJO TÍPICO DE OPERACIÓN:
 * ==========================
 *
 * INGRESO:
 * ────────
 * 1. Operador llega con vehículo: "ABC-001"
 * 2. service.registrarIngreso("ABC-001", TipoVehiculo.CARRO, operador)
 *    - Valida placa única
 *    - Busca espacio recursivamente
 *    - Crea vehículo y registro
 *    - Retorna resultado exitoso
 * 3. Sistema imprime comprobante con espacio asignado
 *
 * ESTANCIA:
 * ─────────
 * [El vehículo permanece en el parqueadero]
 *
 * SALIDA:
 * ───────
 * 1. Operador llega con comprobante
 * 2. service.registrarSalidaYPago("ABC-001", operador)
 *    - Busca vehículo activo
 *    - Busca registro recursivamente
 *    - Calcula duración
 *    - Calcula tarifa recursivamente
 *    - Registra pago
 *    - Libera espacio
 *    - Retorna monto a pagar
 * 3. Operador recibe comprobante con monto total
 * 4. Cliente paga
 * 5. Vehículo sale
 *
 *
 * MANEJO DE ERRORES:
 * ==================
 *
 * Placa duplicada:
 * ────────────────
 * ParqueaderoService.ResultadoOperacion resultado = service.registrarIngreso(...);
 * if (!resultado.isExitoso() && resultado.getMensaje().contains("ya está")) {
 *     System.out.println("El vehículo ya está dentro");
 * }
 *
 * Parqueadero lleno:
 * ──────────────────
 * if (!resultado.isExitoso() && resultado.getMensaje().contains("No hay espacios")) {
 *     System.out.println("Capacidad máxima alcanzada");
 * }
 *
 * Vehículo no encontrado en salida:
 * ─────────────────────────────────
 * ParqueaderoService.ResultadoOperacionPago pago = service.registrarSalidaYPago(...);
 * if (!pago.isExitoso() && pago.getMensaje().contains("no está")) {
 *     System.out.println("Vehículo no está en el parqueadero");
 * }
 *
 *
 * CLASES INTERNAS:
 * ================
 *
 * ResultadoOperacion
 * ──────────────────
 * public static class ResultadoOperacion {
 *     boolean exitoso;
 *     String mensaje;
 *     Object dato;
 * }
 *
 * ResultadoOperacionPago
 * ──────────────────────
 * public static class ResultadoOperacionPago {
 *     boolean exitoso;
 *     String mensaje;
 *     double montoPagado;
 * }
 *
 * EstadoParqueadero
 * ─────────────────
 * public static class EstadoParqueadero {
 *     int ocupados;
 *     int capacidadTotal;
 *     int disponiblesCarros;
 *     int disponiblesMotos;
 *     int disponiblesBicicletas;
 *     // ... más campos
 * }
 */
public class GuiaUsoParqueaderoService {
}

