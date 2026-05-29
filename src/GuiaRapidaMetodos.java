/**
 * GUÍA RÁPIDA - ParqueaderoRepository
 * ==================================
 *
 * MÉTODOS MÁS UTILIZADOS:
 *
 * ┌─ VEHÍCULOS ACTIVOS ────────────────────────────────────┐
 * │ registrarVehiculoActivo(Vehiculo)                       │
 * │   → Añade vehículo | O(1) | Valida placa única         │
 * │                                                          │
 * │ obtenerVehiculoActivo(String placa)                    │
 * │   → Busca vehículo | O(1) | Retorna null si no existe  │
 * │                                                          │
 * │ removerVehiculoActivo(String placa)                    │
 * │   → Retira vehículo | O(1) | Al salir del parqueadero  │
 * │                                                          │
 * │ obtenerVehiculosActivos()                              │
 * │   → Lista todos | O(n) | Retorna copia defensiva       │
 * │                                                          │
 * │ obtenerCantidadVehiculosActivos()                      │
 * │   → Conteo | O(1) | Rápido                            │
 * └────────────────────────────────────────────────────────┘
 *
 * ┌─ ESPACIOS ─────────────────────────────────────────────┐
 * │ obtenerEspacioDisponible(TipoVehiculo tipo)           │
 * │   → Busca libre | O(n) | Para asignar lugar           │
 * │                                                          │
 * │ ocuparEspacio(int numero, String placa)               │
 * │   → Marca ocupado | O(1) | Devuelve true/false        │
 * │                                                          │
 * │ liberarEspacio(int numero)                            │
 * │   → Libera | O(1) | Al salir el vehículo              │
 * │                                                          │
 * │ obtenerEspaciosDisponiblesPorTipo(TipoVehiculo tipo) │
 * │   → Cuenta libres | O(n) | Para mostrar disponibilidad│
 * │                                                          │
 * │ obtenerCapacidadPorTipo(TipoVehiculo tipo)           │
 * │   → Límite máximo | O(1) | Capacidad total del tipo   │
 * │                                                          │
 * │ obtenerCapacidadTotal()                               │
 * │   → Total espacios | O(1) | Parqueadero completo      │
 * │                                                          │
 * │ obtenerPorcentajeOcupacion()                          │
 * │   → % ocupado | O(n) | Para reportes                  │
 * └────────────────────────────────────────────────────────┘
 *
 * ┌─ TARIFAS ──────────────────────────────────────────────┐
 * │ establecerTarifa(String tipo, double valor)           │
 * │   → Configura | O(1) | "CARRO", "MOTO", "BICICLETA"  │
 * │                                                          │
 * │ obtenerTarifa(String tipo)                            │
 * │   → Consulta | O(1) | Valor por hora o día           │
 * │                                                          │
 * │ obtenerTodasTarifas()                                 │
 * │   → Lista | O(1) | Map completo de tarifas           │
 * └────────────────────────────────────────────────────────┘
 *
 * ┌─ REGISTROS (AUDITORÍA) ────────────────────────────────┐
 * │ crearRegistroParqueo(Vehiculo, int espacio, Usuario) │
 * │   → Crea | O(1) | ID autoincremental, en historial    │
 * │                                                          │
 * │ actualizarRegistroParqueo(RegistroParqueo)           │
 * │   → Modifica | O(m) | m = cantidad de registros       │
 * │                                                          │
 * │ obtenerHistorialCompleto()                            │
 * │   → Todos | O(m) | Lista inmutable para auditoría     │
 * │                                                          │
 * │ obtenerRegistrosPorUsuario(Usuario usuario)          │
 * │   → Filtra | O(m) | Por operador responsable         │
 * │                                                          │
 * │ obtenerRegistrosPorEstadoPago(EstadoPago estado)     │
 * │   → Filtra | O(m) | PAGADO, PENDIENTE, CANCELADO     │
 * │                                                          │
 * │ obtenerCantidadRegistros()                            │
 * │   → Conteo | O(1) | Total transacciones              │
 * └────────────────────────────────────────────────────────┘
 *
 * ┌─ USUARIOS ─────────────────────────────────────────────┐
 * │ crearUsuario(Usuario)                                 │
 * │   → Añade | O(1) | Valida nombre único               │
 * │                                                          │
 * │ obtenerUsuario(String nombre)                        │
 * │   → Busca | O(1) | Para login                        │
 * │                                                          │
 * │ actualizarUsuario(Usuario)                           │
 * │   → Modifica | O(1) | Solo si existe                 │
 * │                                                          │
 * │ eliminarUsuario(String nombre)                       │
 * │   → Elimina | O(1) | Del sistema                     │
 * │                                                          │
 * │ obtenerTodosUsuarios()                               │
 * │   → Lista | O(1) | Copia defensiva                   │
 * └────────────────────────────────────────────────────────┘
 *
 * ┌─ UTILIDADES ───────────────────────────────────────────┐
 * │ estaParqueaderoLleno()                                │
 * │   → Verifica | O(n) | Si todos espacios ocupados     │
 * │                                                          │
 * │ obtenerEspaciosOcupados()                             │
 * │   → Conteo | O(n) | Espacios en uso                  │
 * │                                                          │
 * │ limpiar()                                             │
 * │   → Reset | O(n) | Borra todos los datos             │
 * └────────────────────────────────────────────────────────┘
 *
 *
 * FLUJO TÍPICO DE OPERACIONES:
 * ============================
 *
 * ENTRADA DE VEHÍCULO:
 * 1. vehiculo = new Vehiculo(placa, tipo, entrada, ...)
 * 2. repo.registrarVehiculoActivo(vehiculo)    [Validar placa única]
 * 3. espacio = repo.obtenerEspacioDisponible(tipo)
 * 4. repo.ocuparEspacio(espacio.numero, placa)
 * 5. registro = repo.crearRegistroParqueo(vehiculo, numero, usuario)
 *
 * CONSULTA DE VEHÍCULO:
 * 1. vehiculo = repo.obtenerVehiculoActivo(placa)
 * 2. if (vehiculo != null) { Mostrar info }
 *
 * SALIDA DE VEHÍCULO:
 * 1. vehiculo = repo.obtenerVehiculoActivo(placa)
 * 2. vehiculo.setHoraSalida(LocalDateTime.now())
 * 3. repo.liberarEspacio(numeroEspacio)
 * 4. repo.removerVehiculoActivo(placa)
 * 5. registro.setEstadoPago(EstadoPago.PAGADO)
 * 6. repo.actualizarRegistroParqueo(registro)
 *
 * REPORTES:
 * 1. ocupacion = repo.obtenerPorcentajeOcupacion()
 * 2. disponibles = repo.obtenerEspaciosDisponiblesPorTipo(tipo)
 * 3. registros = repo.obtenerHistorialCompleto()
 * 4. porUsuario = repo.obtenerRegistrosPorUsuario(usuario)
 * 5. porPago = repo.obtenerRegistrosPorEstadoPago(estado)
 *
 *
 * VALIDACIONES AUTOMÁTICAS:
 * =========================
 *
 * ✓ registrarVehiculoActivo():
 *   → Si placa existe: retorna false
 *
 * ✓ ocuparEspacio():
 *   → Si espacio no existe: retorna false
 *   → Si espacio ocupado: retorna false
 *
 * ✓ liberarEspacio():
 *   → Si espacio no existe: retorna false
 *   → Si ya está libre: retorna false
 *
 * ✓ crearUsuario():
 *   → Si nombre existe: retorna false
 *
 * ✓ establecerTarifa():
 *   → Si tarifa ≤ 0: no actualiza
 *
 *
 * CASOS DE ERROR Y MANEJO:
 * ========================
 *
 * PLACA DUPLICADA:
 *   boolean result = repo.registrarVehiculoActivo(vehiculo);
 *   if (!result) { System.out.println("Placa ya existe"); }
 *
 * PARQUEADERO LLENO:
 *   EspacioParqueo espacio = repo.obtenerEspacioDisponible(tipo);
 *   if (espacio == null) { System.out.println("No hay lugar"); }
 *
 * VEHÍCULO NO ENCONTRADO:
 *   Vehiculo v = repo.obtenerVehiculoActivo(placa);
 *   if (v == null) { System.out.println("No existe"); }
 *
 * USUARIO NO ENCONTRADO:
 *   Usuario u = repo.obtenerUsuario(nombre);
 *   if (u == null) { System.out.println("Usuario no existe"); }
 *
 * ACTUALIZACIÓN FALLIDA:
 *   boolean updated = repo.actualizarRegistroParqueo(registro);
 *   if (!updated) { System.out.println("ID no existe"); }
 *
 *
 * CONSEJOS DE RENDIMIENTO:
 * ========================
 *
 * ⚡ RÁPIDO O(1):
 *   - Buscar vehículo por placa
 *   - Ocupar/liberar espacio
 *   - Crear/actualizar registro
 *   - Crear/buscar usuario
 *   - Consultar tarifa
 *
 * ⚡ MODERADO O(n):
 *   - Buscar espacio disponible → Mejor hacer primero si n es grande
 *   - Contar espacios disponibles → Cachear si se consulta frecuente
 *   - Calcular ocupación → Cachear entre consultas
 *
 * ⚡ MODERADO O(m):
 *   - Filtrar registros → Considerar índices si m es muy grande
 *   - Listar historial → Considerar paginación si m > 10000
 *
 *
 * INICIALIZACIÓN TÍPICA:
 * ======================
 *
 * // Crear con capacidades específicas
 * ParqueaderoRepository repo = new ParqueaderoRepository(50, 30, 20);
 *
 * // Crear admin
 * Usuario admin = new Usuario(1, "admin", "pass",
 *                              RolUsuario.ADMINISTRADOR, true);
 * repo.crearUsuario(admin);
 *
 * // Configurar tarifas iniciales
 * repo.establecerTarifa("CARRO", 5000.0);
 * repo.establecerTarifa("MOTO", 3000.0);
 * repo.establecerTarifa("BICICLETA", 1000.0);
 *
 * // Sistema listo para usar
 */
public class GuiaRapidaMetodos {
}

