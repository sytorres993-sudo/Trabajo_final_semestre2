/**
 * ARQUITECTURA COMPLETA - SISTEMA DE PARQUEADERO
 * ===============================================
 *
 * CAPAS DEL SISTEMA:
 *
 *
 *        ┌─────────────────────────────────────┐
 *        │   CAPA DE PRESENTACIÓN/CONTROLADOR  │  (Futura: API REST, Interfaz gráfica)
 *        └──────────────┬──────────────────────┘
 *                       │ Coordina operaciones
 *                       ▼
 *        ┌─────────────────────────────────────┐
 *        │  ParqueaderoService (LÓGICA)        │  ◄── NUEVA IMPLEMENTACIÓN
 *        │  ================================    │
 *        │  - registrarIngreso()               │
 *        │  - registrarSalidaYPago()           │
 *        │  - asignarEspacioRecursivo()        │ ◄── RECURSIVIDAD RF-06
 *        │  - buscarVehiculoHistorialRecursivo()  ◄── RECURSIVIDAD RF-08,18
 *        │  - calcularTarifaRecursiva()        │ ◄── RECURSIVIDAD RF-10
 *        │  - obtenerEstadoParqueadero()       │
 *        │  - reportes y auditoría             │
 *        └──────────────┬──────────────────────┘
 *                       │ Consume datos
 *                       ▼
 *        ┌─────────────────────────────────────┐
 *        │  ParqueaderoRepository (DATOS)      │
 *        │  ================================    │
 *        │  vehiculosActivos: HashMap          │ ◄── RF-08 búsqueda O(1)
 *        │  historialParqueo: ArrayList        │ ◄── RF-18 auditoría
 *        │  tarifasPorTipo: HashMap            │ ◄── RF-14 tarifas
 *        │  espacios: LinkedHashMap            │ ◄── RF-04,15 capacidad
 *        │  usuariosRegistrados: HashMap       │ ◄── RF-01 usuarios
 *        └──────────────┬──────────────────────┘
 *                       │ Gestiona entidades
 *                       ▼
 *        ┌─────────────────────────────────────┐
 *        │  MODELOS DE DATOS (Entidades)       │
 *        │  ================================    │
 *        │  - Usuario                          │
 *        │  - Vehiculo                         │
 *        │  - RegistroParqueo                  │
 *        │  - EspacioParqueo                   │
 *        │  - Enumeraciones (Roles, Tipos, Estados)
 *        └─────────────────────────────────────┘
 *
 *
 * FLUJO DE DATOS:
 *
 *
 * INGRESO:
 * ┌─────┐         ┌──────────┐         ┌────────────┐         ┌─────────┐
 * │User │         │Operador  │         │Parqueadero │         │Repository
 * └──┬──┘         └────┬─────┘         │  Service   │         └────┬────┘
 *    │                 │               └─────┬──────┘              │
 *    │ Vehículo ingresa│                     │                    │
 *    ├────────────────>│                     │                    │
 *    │                 │ registrarIngreso()  │                    │
 *    │                 ├────────────────────>│                    │
 *    │                 │                     │ Valida placa       │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │                     │ Busca espacio      │
 *    │                 │                     │ (RECURSIVO)        │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │                     │ Crea registro      │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │ Resultado exitoso   │                    │
 *    │                 │<────────────────────┤                    │
 *    │ Comprobante     │                     │                    │
 *    │<────────────────┤                     │                    │
 *    │                 │                     │                    │
 *    ▼                 ▼                     ▼                    ▼
 *
 *
 * SALIDA Y PAGO:
 * ┌─────┐         ┌──────────┐         ┌────────────┐         ┌─────────┐
 * │User │         │Operador  │         │Parqueadero │         │Repository
 * └──┬──┘         └────┬─────┘         │  Service   │         └────┬────┘
 *    │                 │               └─────┬──────┘              │
 *    │ Sale            │                     │                    │
 *    ├────────────────>│                     │                    │
 *    │                 │ registrarSalidaYPago()│                  │
 *    │                 ├────────────────────>│                    │
 *    │                 │                     │ Busca vehículo     │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │                     │ Busca registro     │
 *    │                 │                     │ (RECURSIVO)        │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │                     │ Calcula tarifa     │
 *    │                 │                     │ (RECURSIVO)        │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │                     │ Registra pago      │
 *    │                 │                     ├───────────────────>│
 *    │                 │                     │<───────────────────┤
 *    │                 │ Monto a pagar       │                    │
 *    │                 │<────────────────────┤                    │
 *    │ Comprobante     │                     │                    │
 *    │<────────────────┤                     │                    │
 *    │                 │                     │                    │
 *    ▼                 ▼                     ▼                    ▼
 *
 *
 * MÉTODOS RECURSIVOS EXPLICACIÓN VISUAL:
 *
 *
 * 1. asignarEspacioRecursivo(espacios, index, tipo)
 *    ─────────────────────────────────────────────
 *
 *    Espacios = [E1(CARRO,lib), E2(MOTO,lib), E3(CARRO,ocu), E4(CARRO,lib)]
 *    Busca: CARRO
 *
 *    Llamada 1: index=0
 *    ├─ E1(CARRO,lib) ✓ COINCIDE
 *    └─ RETORNA E1
 *
 *    Si E1 no fuera disponible:
 *
 *    Llamada 1: index=0
 *    ├─ E1(CARRO,ocu) ✗ NO (ocupado)
 *    └─ Llamar recursivamente con index=1
 *
 *    Llamada 2: index=1
 *    ├─ E2(MOTO,lib) ✗ NO (tipo diferente)
 *    └─ Llamar recursivamente con index=2
 *
 *    Llamada 3: index=2
 *    ├─ E3(CARRO,ocu) ✗ NO (ocupado)
 *    └─ Llamar recursivamente con index=3
 *
 *    Llamada 4: index=3
 *    ├─ E4(CARRO,lib) ✓ COINCIDE
 *    └─ RETORNA E4
 *
 *
 * 2. buscarVehiculoHistorialRecursivo(historial, index, placa, resultados)
 *    ──────────────────────────────────────────────────────────────────────
 *
 *    Historial = [R1(ABC), R2(XYZ), R3(ABC), R4(DEF), R5(ABC)]
 *    Busca: "ABC"
 *
 *    Llamada 1: index=0, resultados=[]
 *    ├─ R1(ABC) ✓ COINCIDE → resultados=[R1]
 *    └─ Llamar recursivamente con index=1
 *
 *    Llamada 2: index=1, resultados=[R1]
 *    ├─ R2(XYZ) ✗ NO coincide
 *    └─ Llamar recursivamente con index=2
 *
 *    Llamada 3: index=2, resultados=[R1]
 *    ├─ R3(ABC) ✓ COINCIDE → resultados=[R1,R3]
 *    └─ Llamar recursivamente con index=3
 *
 *    Llamada 4: index=3, resultados=[R1,R3]
 *    ├─ R4(DEF) ✗ NO coincide
 *    └─ Llamar recursivamente con index=4
 *
 *    Llamada 5: index=4, resultados=[R1,R3]
 *    ├─ R5(ABC) ✓ COINCIDE → resultados=[R1,R3,R5]
 *    └─ Llamar recursivamente con index=5
 *
 *    Llamada 6: index=5 >= historial.size(5)
 *    └─ RETORNA [R1,R3,R5]
 *
 *
 * 3. calcularTarifaRecursiva(minutos, tarifa, intervalo)
 *    ─────────────────────────────────────────────────────
 *
 *    Datos: 150 minutos, $5000/60min
 *
 *    Llamada 1: minutos=150
 *    ├─ 150 > 60 (hay más de un intervalo)
 *    ├─ Cobrar: $5000
 *    └─ Sumar: 5000 + calcularTarifaRecursiva(90, 5000, 60)
 *
 *    Llamada 2: minutos=90
 *    ├─ 90 > 60 (hay más de un intervalo)
 *    ├─ Cobrar: $5000
 *    └─ Sumar: 5000 + calcularTarifaRecursiva(30, 5000, 60)
 *
 *    Llamada 3: minutos=30
 *    ├─ 30 ≤ 60 (menos de intervalo, pero > 0)
 *    ├─ Cobrar: $5000 (fracción completa)
 *    └─ RETORNA 5000
 *
 *    Acumulación:
 *    Llamada 1: 5000 + (resultado Llamada 2)
 *    Llamada 2: 5000 + (resultado Llamada 3)
 *    Llamada 3: 5000
 *
 *    TOTAL: 5000 + 5000 + 5000 = $15000
 *
 *
 * VALIDACIONES POR MÉTODO:
 *
 *
 * registrarIngreso():
 * ├─ ¿Placa ya activa? → NO (rechazar si SÍ)
 * ├─ ¿Hay cupos del tipo? → SÍ (rechazar si NO)
 * ├─ ¿Se asignó espacio? → SÍ (rechazar si NO)
 * └─ ✓ Registrar vehículo
 *
 *
 * registrarSalidaYPago():
 * ├─ ¿Vehículo activo existe? → SÍ (rechazar si NO)
 * ├─ ¿Hay registro del vehículo? → SÍ (rechazar si NO)
 * ├─ ¿Se calculó tarifa? → SÍ (rechazar si NO)
 * ├─ ¿Se liberó espacio? → SÍ (rechazar si NO)
 * └─ ✓ Registrar pago PAGADO
 *
 *
 * INTEGRACIÓN CON REPOSITORY:
 *
 *
 * ParqueaderoService usa:
 *
 * De ParqueaderoRepository:
 * ├─ registrarVehiculoActivo()
 * ├─ obtenerVehiculoActivo()
 * ├─ removerVehiculoActivo()
 * ├─ crearRegistroParqueo()
 * ├─ actualizarRegistroParqueo()
 * ├─ obtenerHistorialCompleto()
 * ├─ obtenerEspaciosDisponiblesPorTipo()
 * ├─ ocuparEspacio()
 * ├─ liberarEspacio()
 * ├─ obtenerTodosEspacios()
 * ├─ obtenerCapacidadPorTipo()
 * ├─ obtenerCapacidadTotal()
 * ├─ obtenerTarifa()
 * ├─ obtenerEspaciosOcupados()
 * ├─ obtenerPorcentajeOcupacion()
 * ├─ obtenerRegistrosPorEstadoPago()
 * └─ obtenerRegistrosPorUsuario()
 *
 *
 * ESTRUCTURA DE DATOS EN TIEMPO DE EJECUCIÓN:
 *
 *
 * ParqueaderoRepository:
 * {
 *   vehiculosActivos: {
 *     "ABC-001": Vehiculo(placa, CARRO, 08:30, null, ACTIVO, ...)
 *     "XYZ-789": Vehiculo(placa, MOTO, 09:15, null, ACTIVO, ...)
 *   },
 *
 *   espacios: {
 *     1: EspacioParqueo(1, CARRO, false, "ABC-001"),
 *     2: EspacioParqueo(2, CARRO, true, null),
 *     ...
 *     21: EspacioParqueo(21, MOTO, false, "XYZ-789"),
 *     ...
 *   },
 *
 *   historialParqueo: [
 *     RegistroParqueo(1, Vehiculo(ABC-001), 1, 5000, PENDIENTE, Operador, ...),
 *     RegistroParqueo(2, Vehiculo(XYZ-789), 21, 3000, PENDIENTE, Operador, ...),
 *   ],
 *
 *   tarifasPorTipo: {
 *     "CARRO": 5000.0,
 *     "MOTO": 3000.0,
 *     "BICICLETA": 1000.0
 *   },
 *
 *   capacidadPorTipo: {
 *     CARRO: 20,
 *     MOTO: 15,
 *     BICICLETA: 10
 *   },
 *
 *   usuariosRegistrados: {
 *     "operador1": Usuario(1, "operador1", "pass", CAJERO_OPERADOR, true)
 *   }
 * }
 *
 *
 * VENTAJAS DE LA ARQUITECTURA:
 *
 *
 * 1. SEPARACIÓN DE RESPONSABILIDADES
 *    - Repository: Solo acceso a datos
 *    - Service: Solo lógica de negocio
 *    - Modelos: Solo datos
 *
 * 2. RECURSIVIDAD PURA
 *    - Sin estado mutable en métodos recursivos
 *    - Fácil de entender y mantener
 *    - Fácil de probar
 *
 * 3. VALIDACIONES CENTRALIZADAS
 *    - Service valida antes de cambiar datos
 *    - Repository también valida por seguridad
 *    - Garantía de consistencia
 *
 * 4. AUDITORÍA COMPLETA
 *    - Historial inmutable (nunca se borra)
 *    - Trazabilidad de todas las operaciones
 *    - Cumplimiento regulatorio
 *
 * 5. ESCALABILIDAD
 *    - Service puede extenderse con nueva lógica
 *    - Repository puede cambiar a BD sin modificar Service
 *    - Fácil agregar nuevas entidades
 *
 * 6. TESTABILIDAD
 *    - Métodos recursivos puro funcionales
 *    - Inyección de dependencias (Repository)
 *    - Resultados estructurados facilitan testing
 *
 *
 * FLUJO DE RECURSIÓN EN registrarSalidaYPago():
 *
 *
 * Entrada: placa="ABC-001"
 *
 * 1. service.registrarSalidaYPago("ABC-001", operador)
 *    ├─ Buscar vehículo activo
 *    │  └─ repo.obtenerVehiculoActivo("ABC-001") → Vehiculo(...)
 *    │
 *    ├─ Buscar registro en historial (RECURSIVO)
 *    │  └─ buscarVehiculoHistorialRecursivo(historial, 0, "ABC-001", [])
 *    │     ├─ Itera recursivamente todo historial
 *    │     └─ Retorna todos los registros de ABC-001
 *    │
 *    ├─ Buscar registro activo (estado = ACTIVO)
 *    │  └─ Filtrar resultados recursivos por estado ACTIVO
 *    │
 *    ├─ Calcular duración
 *    │  └─ 139 minutos
 *    │
 *    ├─ Calcular tarifa (RECURSIVO)
 *    │  └─ calcularTarifaRecursiva(139, 5000, 60)
 *    │     ├─ Itera recursivamente por intervalos
 *    │     └─ Retorna $15000
 *    │
 *    ├─ Actualizar estado
 *    │  ├─ vehiculo.setHoraSalida()
 *    │  ├─ vehiculo.setEstado(FINALIZADO)
 *    │  ├─ registro.setEstadoPago(PAGADO)
 *    │  ├─ registro.setValorPagado(15000)
 *    │  └─ repo.actualizarRegistroParqueo(registro)
 *    │
 *    ├─ Liberar espacio
 *    │  └─ repo.liberarEspacio(numeroEspacio)
 *    │
 *    ├─ Remover de activos
 *    │  └─ repo.removerVehiculoActivo("ABC-001")
 *    │
 *    └─ Retornar ResultadoOperacionPago(true, "✓ ...", 15000)
 *
 * 2. Resultado a Operador
 *    └─ "✓ Vehículo ABC-001 salió correctamente. Tiempo: 139 min. Pago: $15000"
 *
 *
 * ESTADO FINAL DESPUÉS DE SALIDA:
 *
 *
 * ANTES (activo):
 * vehiculosActivos = {"ABC-001": Vehiculo(...)}
 * espacios[1] = EspacioParqueo(1, CARRO, false, "ABC-001")  ← ocupado
 * historialParqueo[0] = RegistroParqueo(..., PENDIENTE, 0)
 *
 * DESPUÉS (inactivo):
 * vehiculosActivos = {}  ← VACÍO, removido
 * espacios[1] = EspacioParqueo(1, CARRO, true, null)  ← libre
 * historialParqueo[0] = RegistroParqueo(..., PAGADO, 15000)  ← actualizado
 */
public class ArquitecturaCompleta {
}

