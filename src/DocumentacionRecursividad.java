/**
 * DOCUMENTACIÓN TÉCNICA - MÉTODOS RECURSIVOS
 * ============================================
 *
 * Este documento explica la implementación y el funcionamiento de los
 * tres métodos recursivos críticos en ParqueaderoService.
 *
 *
 * 1. asignarEspacioRecursivo()
 * ============================
 *
 * Firma:
 *   public EspacioParqueo asignarEspacioRecursivo(List<EspacioParqueo> espacios,
 *                                                  int index, TipoVehiculo tipo)
 *
 * RF-06: Asignación de espacios de parqueo
 *
 * Propósito:
 *   Buscar recursivamente el primer espacio disponible del tipo especificado
 *   en la lista de espacios del parqueadero, sin usar ciclos for/while.
 *
 * Lógica:
 *   - Parámetro index representa la posición actual en la iteración
 *   - Parámetro tipo especifica qué tipo de vehículo se busca
 *
 *   CASO BASE:
 *   └─ Si index >= espacios.size(): hemos revisado toda la lista sin encontrar
 *      → Retornar null (no hay espacio disponible)
 *
 *   CASO RECURSIVO:
 *   ├─ Obtener espacio en posición index: espacios.get(index)
 *   ├─ Si está disponible (disponible=true) Y es del tipo correcto:
 *   │  └─ Retornar este espacio (encontrado)
 *   └─ Si no coincide:
 *      └─ Llamar recursivamente con index+1 para continuar búsqueda
 *
 * Ejemplo de ejecución:
 *   asignarEspacioRecursivo([Espacio1(CARRO), Espacio2(MOTO), Espacio3(CARRO)], 0, CARRO)
 *
 *   Llamada 1: index=0
 *   ├─ Espacio1.disponible=true Y Espacio1.tipo=CARRO
 *   └─ ✓ RETORNA Espacio1
 *
 *   Si Espacio1 no coincidiera:
 *
 *   Llamada 2: index=1
 *   ├─ Espacio2.disponible=true PERO Espacio2.tipo=MOTO (no coincide)
 *   └─ Llamar recursivamente con index=2
 *
 *   Llamada 3: index=2
 *   ├─ Espacio3.disponible=true Y Espacio3.tipo=CARRO
 *   └─ ✓ RETORNA Espacio3
 *
 * Complejidad:
 *   O(n) en peor caso (n = cantidad de espacios)
 *   O(1) en mejor caso (primer espacio coincide)
 *
 * Validaciones:
 *   ✓ No usa ciclos for/while
 *   ✓ Utiliza recursividad pura
 *   ✓ Detiene búsqueda al encontrar coincidencia
 *   ✓ Retorna null si no hay disponible
 *
 *
 * 2. buscarVehiculoHistorialRecursivo()
 * ======================================
 *
 * Firma:
 *   public List<RegistroParqueo> buscarVehiculoHistorialRecursivo(
 *       List<RegistroParqueo> historial,
 *       int index,
 *       String placa,
 *       List<RegistroParqueo> resultados)
 *
 * RF-08, RF-18: Auditoría y búsqueda en historial
 *
 * Propósito:
 *   Buscar recursivamente TODAS las coincidencias de una placa específica
 *   en el historial de parqueo, acumulando resultados sin usar ciclos.
 *
 * Lógica:
 *   - Parámetro historial: lista completa de registros de parqueo
 *   - Parámetro index: posición actual en la iteración
 *   - Parámetro placa: placa a buscar (criterio de búsqueda)
 *   - Parámetro resultados: ACUMULADOR de coincidencias encontradas
 *
 *   CASO BASE:
 *   └─ Si index >= historial.size(): hemos revisado todo
 *      → Retornar lista de resultados acumulados
 *
 *   CASO RECURSIVO:
 *   ├─ Obtener registro en posición index
 *   ├─ Si registro.vehiculo.placa == placa (coincidencia exacta):
 *   │  └─ AÑADIR a lista resultados
 *   ├─ Llamar recursivamente con index+1
 *   └─ Retornar resultado de recursión
 *
 * Ejemplo de ejecución:
 *   historial = [
 *     Registro1(ABC-001, PAGADO),
 *     Registro2(XYZ-789, PENDIENTE),
 *     Registro3(ABC-001, PENDIENTE),  ← duplicado
 *     Registro4(DEF-555, PAGADO),
 *     Registro5(ABC-001, PAGADO)      ← duplicado
 *   ]
 *
 *   buscarVehiculoHistorialRecursivo(historial, 0, "ABC-001", [])
 *
 *   Llamada 1: index=0, resultados=[]
 *   ├─ Registro1.placa = "ABC-001" ✓ COINCIDE
 *   ├─ Añadir Registro1 a resultados → ["Registro1"]
 *   └─ Llamar recursivamente con index=1
 *
 *   Llamada 2: index=1, resultados=["Registro1"]
 *   ├─ Registro2.placa = "XYZ-789" ✗ NO COINCIDE
 *   └─ Llamar recursivamente con index=2
 *
 *   Llamada 3: index=2, resultados=["Registro1"]
 *   ├─ Registro3.placa = "ABC-001" ✓ COINCIDE
 *   ├─ Añadir Registro3 a resultados → ["Registro1", "Registro3"]
 *   └─ Llamar recursivamente con index=3
 *
 *   Llamada 4: index=3, resultados=["Registro1", "Registro3"]
 *   ├─ Registro4.placa = "DEF-555" ✗ NO COINCIDE
 *   └─ Llamar recursivamente con index=4
 *
 *   Llamada 5: index=4, resultados=["Registro1", "Registro3"]
 *   ├─ Registro5.placa = "ABC-001" ✓ COINCIDE
 *   ├─ Añadir Registro5 a resultados → ["Registro1", "Registro3", "Registro5"]
 *   └─ Llamar recursivamente con index=5
 *
 *   Llamada 6: index=5 >= historial.size(5)
 *   └─ RETORNA ["Registro1", "Registro3", "Registro5"]
 *
 * Complejidad:
 *   O(m) donde m = cantidad de registros en historial
 *   (siempre revisa todos los registros)
 *
 * Validaciones:
 *   ✓ No usa ciclos for/while
 *   ✓ Acumula TODAS las coincidencias
 *   ✓ Utiliza parámetro resultados como acumulador
 *   ✓ Búsqueda exacta (igualdad de placa)
 *   ✓ Mantiene orden original del historial
 *
 * Casos de uso:
 *   - Auditoría: ver historial completo de un vehículo
 *   - Investigación: encontrar todos los ingresos de una placa
 *   - Reportes: estadísticas de vehículos recurrentes
 *
 *
 * 3. calcularTarifaRecursiva()
 * ============================
 *
 * Firma:
 *   public double calcularTarifaRecursiva(long minutosRestantes,
 *                                         double tarifaFraccion,
 *                                         long intervaloFraccion)
 *
 * RF-10: Cálculo de tarifas con fracciones de tiempo
 *
 * Propósito:
 *   Calcular el costo total de forma recursiva cobrando por bloques/fracciones
 *   de tiempo, acumulando hasta que los minutos se agoten.
 *
 * Lógica:
 *   - Parámetro minutosRestantes: minutos a cobrar (DISMINUYE en cada llamada)
 *   - Parámetro tarifaFraccion: precio por cada bloque de tiempo
 *   - Parámetro intervaloFraccion: duración de cada bloque en minutos
 *
 *   CASO BASE:
 *   └─ Si minutosRestantes <= 0: no hay más tiempo para cobrar
 *      → Retornar 0.0 (ya se cobró todo)
 *
 *   SUBCASO:
 *   └─ Si minutosRestantes <= intervaloFraccion:
 *      → Cobrar una fracción completa (redondeo hacia arriba)
 *      → Retornar tarifaFraccion
 *
 *   CASO RECURSIVO:
 *   ├─ Cobrar una fracción: sumar tarifaFraccion
 *   ├─ Restar intervaloFraccion de minutosRestantes
 *   ├─ Llamar recursivamente con (minutosRestantes - intervaloFraccion)
 *   └─ Acumular resultado
 *
 * Ejemplo de ejecución:
 *   calcularTarifaRecursiva(150, 5000.0, 60)
 *
 *   Objetivo: cobrar por 150 minutos a $5000 por cada 60 minutos
 *
 *   Llamada 1: minutos=150, tarifa=5000.0, intervalo=60
 *   ├─ 150 > 60 (hay más de un intervalo)
 *   ├─ Cobrar 5000.0
 *   ├─ Restar intervalo: 150 - 60 = 90 minutos
 *   └─ Retornar: 5000.0 + calcularTarifaRecursiva(90, 5000.0, 60)
 *
 *   Llamada 2: minutos=90, tarifa=5000.0, intervalo=60
 *   ├─ 90 > 60 (hay más de un intervalo)
 *   ├─ Cobrar 5000.0
 *   ├─ Restar intervalo: 90 - 60 = 30 minutos
 *   └─ Retornar: 5000.0 + calcularTarifaRecursiva(30, 5000.0, 60)
 *
 *   Llamada 3: minutos=30, tarifa=5000.0, intervalo=60
 *   ├─ 30 <= 60 (menos de un intervalo completo)
 *   ├─ Pero es > 0, así que se cobra el intervalo completo
 *   └─ Retornar: 5000.0
 *
 *   Resultado final: 5000.0 + 5000.0 + 5000.0 = 15000.0
 *
 * Tabla de ejemplos:
 *   Minutos | Tarifa $5000 | Cálculo | Resultado
 *   ────────┼──────────────┼─────────┼──────────
 *      30   │    5000      │    1×   │  $5000
 *      60   │    5000      │    1×   │  $5000
 *      61   │    5000      │    2×   │ $10000
 *      90   │    5000      │    2×   │ $10000
 *      91   │    5000      │    2×   │ $10000 (falta 31 min = 1 bloque)
 *     120   │    5000      │    2×   │ $10000
 *     150   │    5000      │    3×   │ $15000
 *     151   │    5000      │    3×   │ $15000 (falta 31 min = 1 bloque)
 *
 * Complejidad:
 *   O(n) donde n = ceil(minutosRestantes / intervaloFraccion)
 *   Es decir, proporcional a la cantidad de bloques a cobrar
 *
 * Validaciones:
 *   ✓ No usa ciclos for/while
 *   ✓ Descuenta intervalo en cada recursión
 *   ✓ Redondea hacia arriba en última fracción
 *   ✓ Acumula correctamente el total
 *   ✓ Detiene cuando minutos <= 0
 *
 * Ventajas del enfoque recursivo:
 *   - Claridad: cada paso representa un bloque de cobro
 *   - Auditabilidad: fácil ver de dónde viene cada cargo
 *   - Flexibilidad: se pueden añadir lógica especial por bloque
 *   - Precisión: no hay riesgo de errores con variables contador
 *
 *
 * COMPARACIÓN: ITERATIVO vs RECURSIVO
 * ====================================
 *
 * Método 1: asignarEspacioRecursivo()
 * │
 * ├─ ITERATIVO (usando ciclo):
 * │  for (int i = 0; i < espacios.size(); i++) {
 * │      EspacioParqueo espacio = espacios.get(i);
 * │      if (espacio.isDisponible() && espacio.getTipoVehiculo() == tipo) {
 * │          return espacio;
 * │      }
 * │  }
 * │  return null;
 * │
 * ├─ RECURSIVO (sin ciclo):
 * │  private EspacioParqueo asignarEspacioRecursivo(List<EspacioParqueo> espacios,
 * │                                                  int index, TipoVehiculo tipo) {
 * │      if (index >= espacios.size()) return null;
 * │      EspacioParqueo actual = espacios.get(index);
 * │      if (actual.isDisponible() && actual.getTipoVehiculo() == tipo) return actual;
 * │      return asignarEspacioRecursivo(espacios, index + 1, tipo);
 * │  }
 * │
 * └─ VENTAJA RECURSIVA: Más funcional, sin estado mutable
 *
 *
 * REQUISITOS CUMPLIDOS
 * ====================
 *
 * ✅ RF-06: asignarEspacioRecursivo() busca espacio para vehículo
 * ✅ RF-08: buscarVehiculoHistorialRecursivo() filtra por placa
 * ✅ RF-10: calcularTarifaRecursiva() cobra por fracciones
 * ✅ RF-18: buscarVehiculoHistorialRecursivo() auditoría completa
 *
 * ✅ SIN CICLOS for/while/streams en métodos recursivos
 * ✅ PURA RECURSIVIDAD con caso base y caso recursivo
 * ✅ ACUMULADORES para agregar resultados
 * ✅ REDUCCIÓN en parámetros hacia el caso base
 */
public class DocumentacionRecursividad {
}

