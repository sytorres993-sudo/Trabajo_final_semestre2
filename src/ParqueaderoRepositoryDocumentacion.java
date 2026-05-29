/**
 * DOCUMENTACIÓN - ParqueaderoRepository
 * =====================================
 *
 * Esta clase implementa el patrón Repository para gestionar todos los datos
 * del sistema de parqueadero usando Java Collections Framework.
 *
 * ESTRUCTURAS DE DATOS UTILIZADAS:
 * ================================
 *
 * 1. Map<String, Vehiculo> vehiculosActivos (RF-08)
 *    - Propósito: Búsqueda O(1) de vehículos por placa
 *    - Implementación: HashMap
 *    - Clave: Placa del vehículo (String)
 *    - Valor: Objeto Vehiculo
 *    - Ventaja: Acceso inmediato sin iterar
 *
 * 2. List<RegistroParqueo> historialParqueo (RF-18)
 *    - Propósito: Auditoría completa de ingresos/salidas
 *    - Implementación: ArrayList
 *    - Función: Mantener cronología de todos los eventos
 *    - Acceso: Iterable para reportes y filtros
 *
 * 3. Map<String, Double> tarifasPorTipo (RF-14)
 *    - Propósito: Gestión flexible de tarifas por tipo
 *    - Implementación: HashMap
 *    - Clave: Tipo de vehículo ("CARRO", "MOTO", "BICICLETA")
 *    - Valor: Tarifa configurada (double)
 *    - Ventaja: Actualización en tiempo real de precios
 *
 * 4. Map<Integer, EspacioParqueo> espacios (RF-04, RF-15)
 *    - Propósito: Gestión de capacidad y disponibilidad
 *    - Implementación: LinkedHashMap (mantiene orden)
 *    - Clave: Número del espacio (Integer)
 *    - Valor: Objeto EspacioParqueo con estado
 *    - Ventaja: Fácil asignación y liberación de espacios
 *
 * 5. Map<TipoVehiculo, Integer> capacidadPorTipo
 *    - Propósito: Controlar límites por tipo de vehículo
 *    - Implementación: HashMap
 *    - Ventaja: Validación rápida de capacidad
 *
 * 6. Map<String, Usuario> usuariosRegistrados
 *    - Propósito: Gestión de usuarios del sistema
 *    - Implementación: HashMap
 *    - Validación: Sin duplicar nombres de usuario
 *
 * MÉTODOS PRINCIPALES:
 * ===================
 *
 * CRUD USUARIOS:
 * - crearUsuario(Usuario): Crear con validación de duplicidad
 * - obtenerUsuario(String): Buscar por nombre
 * - actualizarUsuario(Usuario): Actualizar datos
 * - eliminarUsuario(String): Eliminar usuario
 * - obtenerTodosUsuarios(): Listar todos
 *
 * CRUD VEHÍCULOS ACTIVOS:
 * - registrarVehiculoActivo(Vehiculo): Registrar con placa única
 * - obtenerVehiculoActivo(String): Buscar por placa (O(1))
 * - removerVehiculoActivo(String): Retirar al salir
 * - obtenerVehiculosActivos(): Listar todos activos
 * - obtenerCantidadVehiculosActivos(): Contar
 *
 * CRUD TARIFAS:
 * - establecerTarifa(String, double): Crear/actualizar tarifa
 * - obtenerTarifa(String): Consultar tarifa
 * - obtenerTodasTarifas(): Listar todas
 *
 * GESTIÓN ESPACIOS:
 * - obtenerEspacioDisponible(TipoVehiculo): Buscar libre O(n)
 * - ocuparEspacio(int, String): Marcar como usado
 * - liberarEspacio(int): Liberar al salir
 * - obtenerEspaciosDisponiblesPorTipo(TipoVehiculo): Contar
 * - obtenerCapacidadPorTipo(TipoVehiculo): Límite máximo
 * - obtenerCapacidadTotal(): Total parqueadero
 *
 * REGISTROS DE PARQUEO:
 * - crearRegistroParqueo(...): Crear con ID autoincremental
 * - actualizarRegistroParqueo(RegistroParqueo): Actualizar
 * - obtenerRegistroParqueo(int): Buscar por ID
 * - obtenerHistorialCompleto(): Auditoría completa
 * - obtenerRegistrosPorUsuario(Usuario): Filtrar por operador
 * - obtenerRegistrosPorEstadoPago(EstadoPago): Filtrar por pago
 *
 * VALIDACIONES:
 * =============
 * - No permite duplicar placas activas
 * - No permite duplicar nombres de usuario
 * - Valida placa no nula antes de registrar
 * - Valida tarifas positivas
 * - Verifica disponibilidad de espacio
 * - Controla que no se supere capacidad máxima
 *
 * GARANTÍAS DE CONSISTENCIA:
 * ==========================
 * - Transaccionalidad implícita (sin concurrencia en este momento)
 * - IDs únicos para registros (contador incrementable)
 * - Espacios no pueden ser ocupados dos veces
 * - Un vehículo activo = una placa única
 * - Historial inmutable (no se borran, solo se marcan estados)
 *
 * COMPLEJIDAD TEMPORAL:
 * ====================
 * - Buscar vehículo por placa: O(1)
 * - Buscar espacio disponible: O(n) donde n = total espacios
 * - Ocupar/Liberar espacio: O(1)
 * - Filtrar registros: O(m) donde m = total registros
 * - Contar disponibles: O(n)
 *
 * EJEMPLO DE USO:
 * ===============
 *
 * // Crear repositorio con capacidad específica
 * ParqueaderoRepository repo = new ParqueaderoRepository(50, 30, 20);
 *
 * // Crear usuario
 * Usuario admin = new Usuario(1, "admin", "123", RolUsuario.ADMINISTRADOR, true);
 * repo.crearUsuario(admin);
 *
 * // Actualizar tarifas
 * repo.establecerTarifa("CARRO", 6000.0);
 *
 * // Registrar entrada
 * Vehiculo auto = new Vehiculo("ABC123", TipoVehiculo.CARRO, LocalDateTime.now(),
 *                              EstadoVehiculo.ACTIVO, "Sin observaciones");
 * repo.registrarVehiculoActivo(auto);
 *
 * // Asignar espacio
 * EspacioParqueo espacio = repo.obtenerEspacioDisponible(TipoVehiculo.CARRO);
 * repo.ocuparEspacio(espacio.getNumeroEspacio(), "ABC123");
 *
 * // Crear registro
 * RegistroParqueo registro = repo.crearRegistroParqueo(auto, espacio.getNumeroEspacio(), admin);
 *
 * // Procesar salida
 * repo.liberarEspacio(espacio.getNumeroEspacio());
 * repo.removerVehiculoActivo("ABC123");
 * registro.setHoraSalida(LocalDateTime.now());
 * registro.setEstadoPago(EstadoPago.PAGADO);
 * repo.actualizarRegistroParqueo(registro);
 *
 * // Consultas
 * System.out.println(repo.obtenerPorcentajeOcupacion());
 * System.out.println(repo.obtenerHistorialCompleto());
 */
public class ParqueaderoRepositoryDocumentacion {
}

