# Sistema de Gestión de Parqueadero - Repositorio de Datos

## 📋 Descripción general

Este proyecto implementa una capa de persistencia en memoria para un sistema de gestión de parqueadero usando el Java Collections Framework. Proporciona almacenamiento eficiente y validaciones de consistencia para las entidades del sistema.

---

## 🏗️ Arquitectura del sistema

### Entidades principales

```
ParqueaderoRepository (contenedor central)
├── vehiculosActivos: Map<String, Vehiculo>       → Búsqueda rápida por placa
├── historialParqueo: List<RegistroParqueo>       → Auditoría completa
├── tarifasPorTipo: Map<String, Double>           → Precios configurables
├── espacios: Map<Integer, EspacioParqueo>        → Control de espacios
├── capacidadPorTipo: Map<TipoVehiculo, Integer>  → Límites por tipo
└── usuariosRegistrados: Map<String, Usuario>     → Gestión de operadores
```

---

## 📊 Estructuras de datos utilizadas

### 1. `vehiculosActivos` (HashMap) — RF-08
- Propósito: búsqueda O(1) de vehículos por placa.
- Contenido: vehículos activos en el parqueadero.
- Validación: no permite placas duplicadas activas.
- Métodos relevantes:
  - `registrarVehiculoActivo()` — añade con validación.
  - `obtenerVehiculoActivo()` — búsqueda rápida.
  - `removerVehiculoActivo()` — elimina al salir.
  - `obtenerCantidadVehiculosActivos()` — conteo.

### 2. `historialParqueo` (ArrayList) — RF-18
- Propósito: auditoría inmutable de todas las transacciones.
- Contenido: registros históricos completos.
- Característica: por trazabilidad, los registros no se eliminan.
- Métodos relevantes:
  - `crearRegistroParqueo()` — crea con ID autoincremental.
  - `actualizarRegistroParqueo()` — modifica existente.
  - `obtenerHistorialCompleto()` — devuelve copia del historial.
  - `obtenerRegistrosPorUsuario()` — filtrado por usuario.
  - `obtenerRegistrosPorEstadoPago()` — filtrado por estado de pago.

### 3. `tarifasPorTipo` (HashMap) — RF-14
- Propósito: gestión flexible de precios por tipo.
- Claves: `"CARRO"`, `"MOTO"`, `"BICICLETA"`.
- Métodos:
  - `establecerTarifa()` — configura o actualiza precio.
  - `obtenerTarifa()` — consultar valor.
  - `obtenerTodasTarifas()` — listado completo.

### 4. `espacios` (LinkedHashMap) — RF-04, RF-15
- Propósito: control de disponibilidad y asignación por número de espacio.
- Por espacio:
  - `numeroEspacio` — identificador único.
  - `tipoVehiculo` — CARRO, MOTO o BICICLETA.
  - `disponible` — true/false.
  - `placaVehiculoActual` — placa del vehículo ocupante.
- Métodos:
  - `obtenerEspacioDisponible()` — busca un espacio libre para un tipo.
  - `ocuparEspacio()` — marca como ocupado.
  - `liberarEspacio()` — libera el espacio.
  - `obtenerEspaciosDisponiblesPorTipo()` — conteo por tipo.
  - `obtenerCapacidadPorTipo()` — capacidad máxima configurada por tipo.

### 5. `capacidadPorTipo` (HashMap)
- Propósito: definir y validar límites máximos por tipo.
- Ejemplo (configurable): CARRO → 50, MOTO → 30, BICICLETA → 20.

### 6. `usuariosRegistrados` (HashMap)
- Propósito: gestión de usuarios del sistema.
- Validación: no permite nombres de usuario duplicados.
- Roles: `ADMINISTRADOR`, `CAJERO_OPERADOR`.

---

## 🔐 Validaciones de consistencia

| Validación | Descripción |
|-----------:|:------------|
| ✓ Placa única | No permite placas duplicadas activas |
| ✓ Usuario único | No permite nombres de usuario duplicados |
| ✓ Espacio coherente | Un espacio está ocupado o disponible (no ambos) |
| ✓ Capacidad máxima | No se excede el límite por tipo |
| ✓ Tarifa positiva | Todas las tarifas deben ser > 0 |
| ✓ ID único | Contador autoincremental en registros |
| ✓ Usuario válido | Usuario debe estar registrado y activo |
| ✓ Historial inmutable | No se eliminan transacciones (trazabilidad) |

---

## ⚡ Complejidad temporal (resumen)

| Operación | Complejidad | Nota |
|-----------|------------:|:----|
| Registrar vehículo | O(1) | inserción en HashMap |
| Buscar por placa | O(1) | HashMap |
| Remover vehículo | O(1) | HashMap |
| Ocupar / Liberar espacio | O(1) | acceso por clave |
| Buscar espacio disponible | O(n) | n = total de espacios |
| Contar disponibles | O(n) | recorre espacios |
| Crear registro | O(1) | ArrayList.add() |
| Filtrar registros | O(m) | m = nº de registros |
| Crear usuario | O(1) | HashMap |

---

## 📝 Ejemplo de uso (API en código)

Ver archivo `EjemploParqueaderoRepository.java` para ejemplo completo de uso.

**Flujo resumido**:

1. Crear repositorio con capacidades iniciales.
2. Crear usuarios con roles específicos.
3. Configurar tarifas por tipo de vehículo.
4. Registrar entrada de vehículos (asignar espacio automáticamente).
5. Registrar salida y procesar pagos.
6. Consultar ocupación, historial y estadísticas.

---

## 📂 Estructura del proyecto

```
src/
├── ParqueaderoRepository.java
├── EspacioParqueo.java
├── Usuario.java
├── Vehiculo.java
├── RegistroParqueo.java
├── RolUsuario.java
├── TipoVehiculo.java
├── EstadoVehiculo.java
├── EstadoPago.java
├── EjemploParqueaderoRepository.java
├── ParqueaderoRepositoryDocumentacion.java
├── ResumenImplementacion.java
├── DiagramasArquitectura.java
└── MenuConsole.java
```

---

## ✅ Requisitos cumplidos

(Resumen de RFs implementados — ver código para detalles)

---

## 🎯 Características principales

- Eficiencia: búsqueda O(1) para vehículos y acceso por clave a espacios.
- Consistencia: validaciones en puntos de entrada para evitar duplicados y sobrecapacidad.
- Flexibilidad: tarifas configurables en tiempo de ejecución.
- Seguridad: encapsulamiento y copias defensivas en getters cuando procede.

---

## 🧪 Pruebas incluidas

El archivo `EjemploParqueaderoRepository.java` contiene casos de uso demostrativos.

---

## 🚀 Para compilar y ejecutar (PowerShell — Windows)

1. Compilar todos los archivos Java (generará las clases en la carpeta `out`):

```powershell
javac -d out src\*.java
```

2. Ejecutar el menú interactivo (recomendado):

```powershell
java -cp out MenuConsole
```

3. (Alternativa) Ejecutar el ejemplo no interactivo:

```powershell
java -cp out EjemploParqueaderoRepository
```

Credenciales semilla (para pruebas):
- Administrador: usuario `admin` / contraseña `admin123`
- Cajero: usuario `cajero` / contraseña `cajero123`

---

## 📖 Documentación adicional

- `ParqueaderoRepositoryDocumentacion.java` — guía detallada de métodos.
- `ResumenImplementacion.java` — resumen de la implementación.
- `DiagramasArquitectura.java` — diagramas y notas.
- `EjemploParqueaderoRepository.java` — caso de uso práctico.

---

## 🔄 Mejoras futuras posibles

- Sincronización thread-safe para acceso concurrente.
- Persistencia en base de datos o archivos.
- Exportación de reportes (CSV, PDF).
- Exposición como API REST.

---

**Última actualización**: 29 de mayo de 2026
**Versión**: 1.0
**Autor**: Sistema de Gestión de Parqueadero

