# Sistema de Gestión de Parqueadero - Repositorio de Datos

## 📋 Descripción General

Este proyecto implementa una **capa de persistencia en memoria** para un sistema de gestión de parqueadero usando **Java Collections Framework**. Proporciona almacenamiento eficiente y validación de consistencia para todas las entidades del sistema.

---

## 🏗️ Arquitectura del Sistema

### Entidades Principales

```
ParqueaderoRepository (Contenedor Central)
├── vehiculosActivos: Map<String, Vehiculo>        → Búsqueda rápida por placa
├── historialParqueo: List<RegistroParqueo>        → Auditoría completa
├── tarifasPorTipo: Map<String, Double>            → Precios configurables
├── espacios: Map<Integer, EspacioParqueo>        → Control de espacios
├── capacidadPorTipo: Map<TipoVehiculo, Integer>  → Límites por tipo
└── usuariosRegistrados: Map<String, Usuario>     → Gestión de operadores
```

---

## 📊 Estructuras de Datos Utilizadas

### 1. **vehiculosActivos** (HashMap) - RF-08
- **Propósito**: Búsqueda O(1) de vehículos por placa
- **Contenido**: Todos los vehículos activos en el parqueadero
- **Validación**: No permite placas duplicadas
- **Métodos**:
  - `registrarVehiculoActivo()` - Añade con validación
  - `obtenerVehiculoActivo()` - Búsqueda rápida
  - `removerVehiculoActivo()` - Elimina al salir
  - `obtenerCantidadVehiculosActivos()` - Conteo

### 2. **historialParqueo** (ArrayList) - RF-18
- **Propósito**: Auditoría inmutable de todas las transacciones
- **Contenido**: Registro histórico completo
- **Característica**: Nunca se borra (trazabilidad)
- **Métodos**:
  - `crearRegistroParqueo()` - Crea con ID autoincremental
  - `actualizarRegistroParqueo()` - Modifica existente
  - `obtenerHistorialCompleto()` - Lista inmutable
  - `obtenerRegistrosPorUsuario()` - Filtrado
  - `obtenerRegistrosPorEstadoPago()` - Filtrado

### 3. **tarifasPorTipo** (HashMap) - RF-14
- **Propósito**: Gestión flexible de precios por tipo
- **Claves**: "CARRO", "MOTO", "BICICLETA"
- **Características**: Actualización en tiempo real
- **Métodos**:
  - `establecerTarifa()` - Configura precios
  - `obtenerTarifa()` - Consulta valor
  - `obtenerTodasTarifas()` - Listado completo

### 4. **espacios** (LinkedHashMap) - RF-04, RF-15
- **Propósito**: Control de disponibilidad y asignación
- **Claves**: Número secuencial del espacio
- **Por Espacio**:
  - `numeroEspacio` - Identificador único
  - `tipoVehiculo` - CARRO, MOTO o BICICLETA
  - `disponible` - true/false
  - `placaVehiculoActual` - Vehículo ocupante
- **Métodos**:
  - `obtenerEspacioDisponible()` - Busca libre
  - `ocuparEspacio()` - Marca como usado
  - `liberarEspacio()` - Libera espacio
  - `obtenerEspaciosDisponiblesPorTipo()` - Conteo
  - `obtenerCapacidadPorTipo()` - Límite máximo

### 5. **capacidadPorTipo** (HashMap)
- **Propósito**: Control de límites máximos
- **Función**: Validar no exceder capacidad
- **Contenido**:
  - CARRO → 50 espacios (configurable)
  - MOTO → 30 espacios (configurable)
  - BICICLETA → 20 espacios (configurable)

### 6. **usuariosRegistrados** (HashMap)
- **Propósito**: Gestión de usuarios del sistema
- **Validación**: No permite nombres duplicados
- **Roles**: ADMINISTRADOR, CAJERO_OPERADOR

---

## 🔐 Validaciones de Consistencia

| Validación | Descripción |
|-----------|-------------|
| ✓ Placa Única | No permite placas duplicadas activas |
| ✓ Usuario Único | No permite nombres de usuario duplicados |
| ✓ Espacio Coherente | Ocupado XOR Disponible |
| ✓ Capacidad Máxima | No excede límite por tipo |
| ✓ Tarifa Positiva | Todas las tarifas > 0 |
| ✓ ID Único | Autoincremento en registros |
| ✓ Usuario Válido | Registrado en sistema |
| ✓ Historial Inmutable | No se borran transacciones |

---

## ⚡ Complejidad Temporal

| Operación | Complejidad | Nota |
|-----------|------------|------|
| Registrar vehículo | O(1) | HashMap |
| Buscar por placa | O(1) | HashMap - búsqueda rápida |
| Remover vehículo | O(1) | HashMap |
| Ocupar espacio | O(1) | HashMap |
| Liberar espacio | O(1) | HashMap |
| Buscar espacio disponible | O(n) | n = total espacios |
| Contar disponibles | O(n) | Itera todos espacios |
| Crear registro | O(1) | ArrayList.add() |
| Filtrar registros | O(m) | m = total registros |
| Crear usuario | O(1) | HashMap |

---

## 📝 Ejemplo de Uso

```java
// 1. CREAR REPOSITORIO
ParqueaderoRepository repo = new ParqueaderoRepository(50, 30, 20);

// 2. CREAR USUARIOS
Usuario admin = new Usuario(1, "admin", "123456", RolUsuario.ADMINISTRADOR, true);
repo.crearUsuario(admin);

// 3. CONFIGURAR TARIFAS
repo.establecerTarifa("CARRO", 5000.0);
repo.establecerTarifa("MOTO", 3000.0);

// 4. REGISTRAR ENTRADA
Vehiculo auto = new Vehiculo(
    "ABC-123",
    TipoVehiculo.CARRO,
    LocalDateTime.now(),
    EstadoVehiculo.ACTIVO,
    "Sin daños"
);
repo.registrarVehiculoActivo(auto);

// 5. ASIGNAR ESPACIO
EspacioParqueo espacio = repo.obtenerEspacioDisponible(TipoVehiculo.CARRO);
repo.ocuparEspacio(espacio.getNumeroEspacio(), auto.getPlaca());

// 6. CREAR REGISTRO DE PARQUEO
RegistroParqueo registro = repo.crearRegistroParqueo(auto, espacio.getNumeroEspacio(), admin);

// 7. REGISTRAR SALIDA
auto.setHoraSalida(LocalDateTime.now());
auto.setEstado(EstadoVehiculo.FINALIZADO);
repo.removerVehiculoActivo(auto.getPlaca());
repo.liberarEspacio(espacio.getNumeroEspacio());

// 8. PROCESAR PAGO
registro.setEstadoPago(EstadoPago.PAGADO);
registro.setValorPagado(5000.0);
repo.actualizarRegistroParqueo(registro);

// 9. CONSULTAS
System.out.println("Ocupación: " + repo.obtenerPorcentajeOcupacion() + "%");
System.out.println("Vehículos activos: " + repo.obtenerCantidadVehiculosActivos());
System.out.println("Historial: " + repo.obtenerHistorialCompleto());
```

---

## 📂 Estructura del Proyecto

```
src/
├── ParqueaderoRepository.java           ← CLASE PRINCIPAL
├── EspacioParqueo.java                  ← Entidad de espacio
├── Usuario.java                          ← Entidad usuario
├── Vehiculo.java                         ← Entidad vehículo
├── RegistroParqueo.java                 ← Entidad registro
├── RolUsuario.java                       ← Enum roles
├── TipoVehiculo.java                     ← Enum tipos
├── EstadoVehiculo.java                   ← Enum estados
├── EstadoPago.java                       ← Enum pagos
├── EjemploParqueaderoRepository.java     ← Ejemplo de uso
├── ParqueaderoRepositoryDocumentacion.java
├── ResumenImplementacion.java
├── DiagramasArquitectura.java
└── Main.java                             ← Punto entrada
```

---

## ✅ Requisitos Cumplidos

| RF | Descripción | Implementación | Estado |
|----|-------------|-----------------|--------|
| RF-01 | Gestión de usuarios | Usuario + roles | ✅ |
| RF-03 | Registro entrada/salida | Vehiculo + RegistroParqueo | ✅ |
| RF-04 | Gestión espacios | EspacioParqueo + espacios Map | ✅ |
| RF-05 | Tipos de vehículos | TipoVehiculo enum | ✅ |
| RF-06 | Registro parqueo | RegistroParqueo | ✅ |
| **RF-08** | **Búsqueda por placa** | **vehiculosActivos HashMap** | **✅** |
| RF-11 | Gestión pagos | EstadoPago enum | ✅ |
| RF-12 | Estado pago | EstadoPago en RegistroParqueo | ✅ |
| **RF-14** | **Tarifas configurables** | **tarifasPorTipo HashMap** | **✅** |
| **RF-15** | **Control capacidad** | **espacios + capacidadPorTipo** | **✅** |
| RF-16 | Gestión roles | RolUsuario enum | ✅ |
| RF-17 | Estado usuario | Usuario.activo boolean | ✅ |
| **RF-18** | **Auditoría historial** | **historialParqueo ArrayList** | **✅** |
| RF-21 | Novedades/eventos | RegistroParqueo.novedades | ✅ |

---

## 🎯 Características Principales

### Eficiencia
- Búsqueda de vehículos en O(1) mediante HashMap
- Acceso a espacios en O(1)
- Operaciones de registro en O(1)

### Consistencia
- Validación de duplicidad en todos los puntos de entrada
- Transaccionalidad implícita (sin concurrencia)
- Auditoría inmutable mediante ArrayList

### Flexibilidad
- Tarifas dinámicas sin reiniciar
- Capacidades configurables por tipo
- Fácil filtrado de registros

### Seguridad
- Copias defensivas en métodos de obtención
- Encapsulamiento completo
- Métodos privados para operaciones internas

---

## 🧪 Pruebas Incluidas

El archivo `EjemploParqueaderoRepository.java` demuestra:
- ✓ Creación de repositorio con capacidades
- ✓ Gestión de usuarios sin duplicados
- ✓ Configuración de tarifas
- ✓ Registro de vehículos
- ✓ Asignación de espacios
- ✓ Creación de registros
- ✓ Procesamiento de entrada/salida
- ✓ Cálculo de ocupación
- ✓ Auditoría y filtrados

---

## 🚀 Para Compilar y Ejecutar

```bash
cd src/
javac *.java
java EjemploParqueaderoRepository
```

---

## 📖 Documentación Adicional

- **ParqueaderoRepositoryDocumentacion.java** - Guía detallada de métodos
- **ResumenImplementacion.java** - Resumen de características
- **DiagramasArquitectura.java** - Diagramas de flujo y relaciones
- **EjemploParqueaderoRepository.java** - Caso de uso práctico

---

## 📝 Notas de Diseño

1. **HashMap vs TreeMap**: Se usó HashMap para búsqueda O(1), no se necesita orden
2. **LinkedHashMap para espacios**: Mantiene orden secuencial de números
3. **ArrayList para historial**: Ideal para auditoría, búsqueda lineal aceptable
4. **ID Autoincremental**: Contador interno garantiza unicidad
5. **Sin frameworks externos**: Compatible con Java puro (no Lombok)

---

## 🔄 Mejoras Futuras Posibles

- [ ] Sincronización thread-safe con Collections.synchronizedMap()
- [ ] Persistencia en base de datos
- [ ] Exportación de reportes (PDF, Excel)
- [ ] API REST
- [ ] Caché de operaciones frecuentes
- [ ] Índices secundarios para búsquedas complejas

---

**Última actualización**: 28 de mayo de 2026
**Versión**: 1.0
**Autor**: Sistema de Gestión de Parqueadero

