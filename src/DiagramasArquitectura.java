/**
 * DIAGRAMA DE ARQUITECTURA - ParqueaderoRepository
 *
 * ┌────────────────────────────────────────────────────────────────┐
 * │              ParqueaderoRepository (SINGLETON)                 │
 * └────────────────────────────────────────────────────────────────┘
 *                              │
 *        ┌─────────────────────┼─────────────────────┐
 *        │                     │                     │
 *        ▼                     ▼                     ▼
 *   [USUARIOS]          [VEHÍCULOS]           [ESPACIOS]
 *   ─────────────       ──────────────       ──────────────
 *   HashMap<String,     HashMap<String,      LinkedHashMap<
 *     Usuario>            Vehiculo>          Integer,
 *                                            EspacioParqueo>
 *   - admin              - ABC-123            - 1: CARRO (libre)
 *   - cajero1            - XYZ-789            - 2: CARRO (ocupado)
 *   - cajero2            - BIK-001            - 51: MOTO (libre)
 *                                             - 81: BICI (ocupado)
 *        │                     │                     │
 *        │                     │                     │
 *        └─────────────────────┼─────────────────────┘
 *                              │
 *                         ▼ ▼ ▼ (Relaciones)
 *    ┌──────────────────────────────────────────────┐
 *    │    ArrayList<RegistroParqueo> historial      │
 *    │    (Auditoría - nunca se borra)              │
 *    └──────────────────────────────────────────────┘
 *    │ 1: ABC-123 | PAGADO | $5000 | cajero1 |
 *    │ 2: XYZ-789 | PENDIENTE | $3000 | cajero2 |
 *    │ 3: BIK-001 | PENDIENTE | $1000 | cajero1 |
 *    └──────────────────────────────────────────────┘
 *
 * ┌──────────────────────────────────────────────────┐
 * │    HashMap<String, Double> tarifasPorTipo        │
 * │    (Configuración dinámica)                      │
 * ├──────────────────────────────────────────────────┤
 * │ "CARRO"     → 5000.0                            │
 * │ "MOTO"      → 3000.0                            │
 * │ "BICICLETA" → 1000.0                            │
 * └──────────────────────────────────────────────────┘
 *
 * ┌──────────────────────────────────────────────────┐
 * │ HashMap<TipoVehiculo, Integer> capacidadPorTipo  │
 * │ (Control de límites)                             │
 * ├──────────────────────────────────────────────────┤
 * │ CARRO     → 50 espacios máx                      │
 * │ MOTO      → 30 espacios máx                      │
 * │ BICICLETA → 20 espacios máx                      │
 * │ TOTAL     → 100 espacios máx                     │
 * └──────────────────────────────────────────────────┘
 *
 *
 * FLUJO DE OPERACIONES:
 * ====================
 *
 * 1. ENTRADA DE VEHÍCULO:
 *    ┌──────────────┐
 *    │ Recibir Auto │
 *    └──────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ ¿Placa duplicada activa?    │
 *    └─────────────────────────────┘
 *      SI ─→ RECHAZAR
 *      NO ─┐
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Registrar en vehiculosActivos│
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────────────┐
 *    │ Buscar espacio disponible por tipo  │
 *    └─────────────────────────────────────┘
 *      NO HAY ─→ PARQUEADERO LLENO
 *      HAY ───┐
 *            ▼
 *    ┌─────────────────────────────────────┐
 *    │ Ocupar espacio + crear RegistroParqueo
 *    └─────────────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Guardar en historial        │
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌──────────────────┐
 *    │ ENTRADA EXITOSA  │
 *    └──────────────────┘
 *
 * 2. SALIDA DE VEHÍCULO:
 *    ┌──────────────┐
 *    │ Recibir Placa│
 *    └──────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Buscar en vehiculosActivos  │
 *    └─────────────────────────────┘
 *      NO EXISTE ─→ ERROR
 *      EXISTE ───┐
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Obtener RegistroParqueo     │
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Procesar pago + novedades   │
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Actualizar registro historial│
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Liberar espacio             │
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌─────────────────────────────┐
 *    │ Remover de vehiculosActivos │
 *    └─────────────────────────────┘
 *            │
 *            ▼
 *    ┌──────────────────┐
 *    │ SALIDA EXITOSA   │
 *    └──────────────────┘
 *
 *
 * GARANTÍAS DE CONSISTENCIA:
 * ==========================
 *
 * Invariante 1: Placa Única
 * ─────────────────────────
 * ∀ registro ∈ historialParqueo,
 * placa única en vehiculosActivos O estado = FINALIZADO
 *
 * Invariante 2: Espacios Coherentes
 * ──────────────────────────────────
 * ∀ espacio ∈ espacios,
 * (disponible = true ⟺ placaVehiculoActual = null)
 *
 * Invariante 3: Usuario Válido
 * ─────────────────────────────
 * ∀ registro ∈ historialParqueo,
 * usuarioResponsable ∈ usuariosRegistrados
 *
 * Invariante 4: ID Único
 * ──────────────────────
 * ∀ registro ∈ historialParqueo,
 * ID único y contadorRegistros siempre incrementa
 *
 * Invariante 5: Capacidad Respetada
 * ──────────────────────────────────
 * ocupados(tipoVehiculo) ≤ capacidadPorTipo[tipoVehiculo]
 *
 *
 * EJEMPLO DE DATOS EN MEMORIA:
 * ============================
 *
 * vehiculosActivos = {
 *   "ABC-123" → Vehiculo(placa="ABC-123", tipo=CARRO, ...),
 *   "XYZ-789" → Vehiculo(placa="XYZ-789", tipo=MOTO, ...)
 * }
 *
 * espacios = {
 *   1 → EspacioParqueo(1, CARRO, disponible=false, "ABC-123"),
 *   2 → EspacioParqueo(2, CARRO, disponible=true, null),
 *   ...
 *   51 → EspacioParqueo(51, MOTO, disponible=false, "XYZ-789"),
 *   ...
 * }
 *
 * historialParqueo = [
 *   RegistroParqueo(id=1, vehiculo=ABC-123, espacio=1, valor=5000, PAGADO, ...),
 *   RegistroParqueo(id=2, vehiculo=XYZ-789, espacio=51, valor=3000, PENDIENTE, ...),
 *   RegistroParqueo(id=3, vehiculo=BIK-001, espacio=81, valor=1000, PENDIENTE, ...)
 * ]
 *
 * tarifasPorTipo = {
 *   "CARRO" → 5000.0,
 *   "MOTO" → 3000.0,
 *   "BICICLETA" → 1000.0
 * }
 */
public class DiagramasArquitectura {
}

