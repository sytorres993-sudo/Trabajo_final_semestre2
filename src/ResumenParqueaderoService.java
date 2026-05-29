/**
 * RESUMEN EJECUTIVO - ParqueaderoService
 * ======================================
 *
 *
 * ¿QUÉ ES ParqueaderoService?
 * ===========================
 *
 * Clase que implementa la LÓGICA DE NEGOCIO del sistema de parqueadero.
 * Consume ParqueaderoRepository para acceder a datos.
 * Aplica RECURSIVIDAD PURA en tres métodos críticos.
 *
 *
 * CARACTERÍSTICAS PRINCIPALES:
 * ===========================
 *
 * ✅ RECURSIVIDAD SIN CICLOS
 *    - asignarEspacioRecursivo() - búsqueda de espacios
 *    - buscarVehiculoHistorialRecursivo() - filtrado de historial
 *    - calcularTarifaRecursiva() - cobro por fracciones
 *
 * ✅ VALIDACIONES AUTOMÁTICAS
 *    - Placa no duplicada activa
 *    - Cupos disponibles verificados
 *    - Espacios asignados correctamente
 *    - Pagos procesados correctamente
 *
 * ✅ LÓGICA DE NEGOCIO COMPLETA
 *    - Ingreso de vehículos
 *    - Salida y cálculo de cobro
 *    - Auditoría y reportes
 *    - Estado del parqueadero
 *
 * ✅ RESULTADOS ESTRUCTURADOS
 *    - ResultadoOperacion: éxito + mensaje + dato
 *    - ResultadoOperacionPago: éxito + mensaje + monto
 *    - EstadoParqueadero: estado completo del sistema
 *
 *
 * MÉTODOS RECURSIVOS (REQUERIMIENTO CRÍTICO):
 * ============================================
 *
 * 1. asignarEspacioRecursivo() - RF-06
 *    ────────────────────────────────
 *
 *    public EspacioParqueo asignarEspacioRecursivo(
 *        List<EspacioParqueo> espacios,
 *        int index,
 *        TipoVehiculo tipo
 *    )
 *
 *    Busca recursivamente el primer espacio disponible del tipo especificado.
 *
 *    CASO BASE: if (index >= espacios.size()) return null;
 *    CASO RECURSIVO: if (coincide) return; else llamar(index+1);
 *
 *    Ejemplos:
 *    - Buscar espacio para CARRO: asignarEspacioRecursivo(espacios, 0, CARRO)
 *    - Buscar espacio para MOTO: asignarEspacioRecursivo(espacios, 0, MOTO)
 *
 *    Complejidad: O(n) peor caso, O(1) mejor caso
 *    ✓ Sin ciclos for/while
 *
 *
 * 2. buscarVehiculoHistorialRecursivo() - RF-08, RF-18
 *    ──────────────────────────────────────────────────
 *
 *    public List<RegistroParqueo> buscarVehiculoHistorialRecursivo(
 *        List<RegistroParqueo> historial,
 *        int index,
 *        String placa,
 *        List<RegistroParqueo> resultados
 *    )
 *
 *    Busca recursivamente TODAS las coincidencias de una placa.
 *    Acumula resultados en parámetro resultados (acumulador).
 *
 *    CASO BASE: if (index >= historial.size()) return resultados;
 *    CASO RECURSIVO:
 *    - if (coincide) resultados.add();
 *    - llamar(index+1)
 *
 *    Ejemplos:
 *    - Historial de ABC-001: buscarVehiculoHistorialRecursivo(hist, 0, "ABC-001", [])
 *    - Retorna TODAS las transacciones de esa placa
 *
 *    Complejidad: O(m) donde m = cantidad de registros
 *    ✓ Sin ciclos for/while
 *    ✓ Acumula correctamente todas las coincidencias
 *
 *
 * 3. calcularTarifaRecursiva() - RF-10
 *    ──────────────────────────────────
 *
 *    public double calcularTarifaRecursiva(
 *        long minutosRestantes,
 *        double tarifaFraccion,
 *        long intervaloFraccion
 *    )
 *
 *    Calcula costo recursivamente cobrando por bloques de tiempo.
 *    Descuenta intervalo en cada llamada hasta minutos <= 0.
 *
 *    CASO BASE: if (minutosRestantes <= 0) return 0.0;
 *    SUBCASO: if (minutosRestantes <= intervalo) return tarifaFraccion;
 *    CASO RECURSIVO: return tarifa + llamar(minutos - intervalo);
 *
 *    Ejemplos:
 *    - 30 min a $5000/hora: calcularTarifaRecursiva(30, 5000, 60) = $5000
 *    - 150 min a $5000/hora: calcularTarifaRecursiva(150, 5000, 60) = $15000
 *
 *    Tabla:
 *    Minutos → Bloques → Costo ($5000/hora)
 *    1-60       1         $5000
 *    61-120     2         $10000
 *    121-180    3         $15000
 *
 *    Complejidad: O(n) donde n = ceil(minutos/intervalo)
 *    ✓ Sin ciclos for/while
 *    ✓ Acumula correctamente la tarifa total
 *
 *
 * MÉTODOS DE LÓGICA DE NEGOCIO:
 * =============================
 *
 * registrarIngreso()
 * ──────────────────
 * RF-03, RF-04, RF-05: Registra entrada de vehículo
 *
 * Firma:
 * public ResultadoOperacion registrarIngreso(String placa, TipoVehiculo tipo, Usuario operador)
 *
 * Proceso:
 * 1. Valida placa no activa
 * 2. Valida cupos disponibles
 * 3. Busca espacio RECURSIVAMENTE
 * 4. Crea vehículo y registro
 * 5. Retorna resultado con datos
 *
 * Ejemplo:
 * ParqueaderoService.ResultadoOperacion res = service.registrarIngreso("ABC-001", CARRO, op);
 * if (res.isExitoso()) { RegistroParqueo reg = (RegistroParqueo) res.getDato(); }
 *
 *
 * registrarSalidaYPago()
 * ──────────────────────
 * RF-11, RF-13: Registra salida y procesa pago
 *
 * Firma:
 * public ResultadoOperacionPago registrarSalidaYPago(String placa, Usuario operador)
 *
 * Proceso:
 * 1. Valida vehículo existe
 * 2. Busca registro RECURSIVAMENTE
 * 3. Calcula duración
 * 4. Calcula tarifa RECURSIVAMENTE
 * 5. Registra pago como PAGADO
 * 6. Libera espacio
 * 7. Retorna monto cobrado
 *
 * Ejemplo:
 * ParqueaderoService.ResultadoOperacionPago pago = service.registrarSalidaYPago("ABC-001", op);
 * System.out.println("Monto: $" + pago.getMontoPagado());
 *
 *
 * MÉTODOS DE CONSULTA:
 * ====================
 *
 * obtenerHistorialVehiculo()
 * ──────────────────────────
 * Wrapper que usa buscarVehiculoHistorialRecursivo() para auditoría.
 *
 * Retorna: List<RegistroParqueo> de una placa específica
 *
 * obtenerEstadoParqueadero()
 * ──────────────────────────
 * Retorna: EstadoParqueadero con ocupación actual
 *
 * reporteIngresosPorTipo()
 * ───────────────────────
 * Retorna: Map<TipoVehiculo, Integer> con conteos
 *
 * reporteIngresosTotales()
 * ────────────────────────
 * Retorna: double con dinero recaudado
 *
 * reportePendientes()
 * ──────────────────
 * Retorna: List<RegistroParqueo> con pagos pendientes
 *
 *
 * FLUJO DE OPERACIÓN COMPLETO:
 * =============================
 *
 * INGRESO:
 * ├─ Operador: "Tengo carro ABC-001"
 * ├─ Sistema: registrarIngreso("ABC-001", CARRO, operador)
 * │  ├─ ¿Placa duplicada? NO ✓
 * │  ├─ ¿Hay cupos carros? SÍ (19 disponibles) ✓
 * │  ├─ Buscar espacio (RECURSIVO)
 * │  │  └─ Itera espacios hasta encontrar CARRO disponible → Espacio #5
 * │  ├─ Crear Vehiculo(ABC-001, CARRO, now, ACTIVO, ...)
 * │  ├─ Crear RegistroParqueo(1, vehiculo, 5, usuario, PENDIENTE)
 * │  └─ Retorna: ResultadoOperacion(true, "✓ Vehículo ABC-001 ingresó en espacio #5", registro)
 * ├─ Operador recibe comprobante con espacio #5
 * └─ Vehículo estaciona en Espacio #5
 *
 * [TIEMPO TRANSCURRE - VEHÍCULO EN PARQUEADERO]
 *
 * SALIDA:
 * ├─ Operador: "Quiero salir, placa ABC-001"
 * ├─ Sistema: registrarSalidaYPago("ABC-001", operador)
 * │  ├─ Buscar vehículo activo → Encontrado ✓
 * │  ├─ Buscar registro (RECURSIVO) en historial
 * │  │  └─ Itera historial hasta encontrar placa ABC-001 estado ACTIVO → Registro #1
 * │  ├─ Calcular duración: 2026-05-28 12:34 - 2026-05-28 10:15 = 139 minutos
 * │  ├─ Obtener tarifa: $5000 por hora
 * │  ├─ Calcular costo (RECURSIVO)
 * │  │  └─ calcularTarifaRecursiva(139, 5000, 60)
 * │  │     ├─ 139 > 60 → 5000 + calcularTarifaRecursiva(79, 5000, 60)
 * │  │     ├─ 79 > 60 → 5000 + calcularTarifaRecursiva(19, 5000, 60)
 * │  │     └─ 19 <= 60 → 5000
 * │  │     TOTAL: 5000 + 5000 + 5000 = $15000
 * │  ├─ Actualizar Registro #1: estado=PAGADO, valor=15000
 * │  ├─ Liberar Espacio #5
 * │  ├─ Remover ABC-001 de activos
 * │  └─ Retorna: ResultadoOperacionPago(true, "✓ Vehículo ABC-001 salió... Pago: $15000", 15000)
 * ├─ Operador recibe comprobante: $15000 a pagar
 * ├─ Cliente paga
 * └─ Vehículo sale del parqueadero
 *
 * AUDITORÍA:
 * ├─ Administrador: "¿Cuántas veces vino ABC-001?"
 * ├─ Sistema: obtenerHistorialVehiculo("ABC-001")
 * │  └─ buscarVehiculoHistorialRecursivo(historial, 0, "ABC-001", [])
 * │     ├─ Itera recursivamente todo el historial
 * │     ├─ Acumula TODAS las coincidencias
 * │     └─ Retorna: [Registro#1, Registro#5, Registro#12, ...]
 * └─ Reporte: "ABC-001 visitó 3 veces, recaudó $45000"
 *
 *
 * VALIDACIONES Y ERRORES:
 * =======================
 *
 * Placa duplicada:
 * ├─ Intento: registrarIngreso("ABC-001", CARRO, op) [cuando ya está]
 * └─ Resultado: false, "Vehículo con placa ABC-001 ya está en el parqueadero"
 *
 * Parqueadero lleno:
 * ├─ Intento: registrarIngreso("NEW-001", CARRO, op) [cuando 0 cupos]
 * └─ Resultado: false, "No hay espacios disponibles para CARRO"
 *
 * Vehículo no existe:
 * ├─ Intento: registrarSalidaYPago("ZZZ-999", op)
 * └─ Resultado: false, "Vehículo con placa ZZZ-999 no está en el parqueadero"
 *
 *
 * CLASES INTERNAS:
 * ================
 *
 * ResultadoOperacion
 * ├─ boolean exitoso
 * ├─ String mensaje
 * ├─ Object dato
 * └─ toString() → "✓/✗ mensaje"
 *
 * ResultadoOperacionPago
 * ├─ boolean exitoso
 * ├─ String mensaje
 * ├─ double montoPagado
 * └─ toString() → "✓/✗ mensaje | Monto: $X.XX"
 *
 * EstadoParqueadero
 * ├─ int ocupados, capacidadTotal
 * ├─ int disponiblesCarros, disponiblesMotos, disponiblesBicicletas
 * ├─ int capacidadCarros, capacidadMotos, capacidadBicicletas
 * ├─ double porcentajeOcupacion
 * └─ toString() → tabla formateada
 *
 *
 * REQUISITOS CUMPLIDOS:
 * =====================
 *
 * ✅ RF-03: registrarIngreso() - Registro de entrada
 * ✅ RF-04: Validación de cupos en registrarIngreso()
 * ✅ RF-05: Soporte tipos CARRO, MOTO, BICICLETA
 * ✅ RF-06: asignarEspacioRecursivo() - Asignación de espacios
 * ✅ RF-08: buscarVehiculoHistorialRecursivo() - Búsqueda por placa
 * ✅ RF-10: calcularTarifaRecursiva() - Cálculo de tarifa por fracciones
 * ✅ RF-11: registrarSalidaYPago() - Gestión de pagos
 * ✅ RF-13: Liberación de cupos en registrarSalidaYPago()
 * ✅ RF-18: buscarVehiculoHistorialRecursivo() - Auditoría historial
 *
 * ✅ RECURSIVIDAD PURA (sin ciclos for/while/streams)
 * ✅ CASO BASE y CASO RECURSIVO en cada método
 * ✅ ACUMULADORES para agregar resultados
 * ✅ REDUCCIÓN de parámetros hacia caso base
 *
 *
 * COMPARACIÓN: CON/SIN RECURSIVIDAD
 * ==================================
 *
 * SIN RECURSIVIDAD (con ciclo):
 * ─────────────────────────────
 * for (int i = 0; i < espacios.size(); i++) {
 *     EspacioParqueo esp = espacios.get(i);
 *     if (esp.isDisponible() && esp.getTipoVehiculo() == tipo) {
 *         return esp;
 *     }
 * }
 * return null;
 *
 * CON RECURSIVIDAD (como implementado):
 * ──────────────────────────────────────
 * if (index >= espacios.size()) return null;
 * EspacioParqueo esp = espacios.get(index);
 * if (esp.isDisponible() && esp.getTipoVehiculo() == tipo) return esp;
 * return asignarEspacioRecursivo(espacios, index + 1, tipo);
 *
 * VENTAJAS RECURSIVAS:
 * ├─ Más funcional y declarativo
 * ├─ Sin estado mutable (índice)
 * ├─ Cada paso es una transformación
 * └─ Fácil seguir el flujo mental
 *
 *
 * RESUMEN FINAL:
 * ==============
 *
 * ParqueaderoService es una clase de lógica de negocio que:
 *
 * ✓ Implementa RECURSIVIDAD PURA en 3 métodos críticos
 * ✓ NO utiliza ciclos for/while en métodos recursivos
 * ✓ Valida automáticamente la consistencia
 * ✓ Maneja la operación completa (ingreso → salida → pago)
 * ✓ Proporciona auditoría y reportes
 * ✓ Retorna resultados estructurados con detalles
 * ✓ Cumple todos los requisitos funcionales (RF-*)
 *
 * Ejemplo de uso:
 *
 * ParqueaderoRepository repo = new ParqueaderoRepository(20, 15, 10);
 * ParqueaderoService service = new ParqueaderoService(repo);
 * Usuario op = new Usuario(1, "op1", "pass", CAJERO_OPERADOR, true);
 * repo.crearUsuario(op);
 * repo.establecerTarifa("CARRO", 5000.0);
 *
 * // Ingreso
 * var ingreso = service.registrarIngreso("ABC-001", CARRO, op);
 * System.out.println(ingreso);  // ✓ Vehículo ABC-001 ingresó en espacio #1
 *
 * // Salida
 * var salida = service.registrarSalidaYPago("ABC-001", op);
 * System.out.println(salida);  // ✓ Vehículo ABC-001 salió. Pago: $5000 | Monto: $5000
 *
 * // Auditoría
 * var historial = service.obtenerHistorialVehiculo("ABC-001");
 * System.out.println(historial);  // [Registro#1 (PAGADO, $5000)]
 */
public class ResumenParqueaderoService {
}

