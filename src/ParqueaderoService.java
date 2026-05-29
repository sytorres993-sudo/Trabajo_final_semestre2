import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Clase ParqueaderoService que implementa la lógica de negocio del sistema.
 * Consume el ParqueaderoRepository para manejar operaciones de ingreso, salida,
 * auditoría y cálculo de tarifas.
 *
 * Utiliza RECURSIVIDAD para:
 * - Búsqueda de espacios disponibles (RF-06)
 * - Búsqueda en historial de auditoría (RF-08, RF-18)
 * - Cálculo de tarifa por fracciones de tiempo (RF-10)
 */
public class ParqueaderoService {

    private ParqueaderoRepository repository;

    /**
     * Constructor del servicio de parqueadero.
     *
     * @param repository Instancia del repositorio
     */
    public ParqueaderoService(ParqueaderoRepository repository) {
        this.repository = repository;
    }

    // ==================== MÉTODOS RECURSIVOS ====================

    /**
     * Busca recursivamente un espacio disponible del tipo especificado.
     * Utiliza recursividad para iterar sobre la lista de espacios sin usar ciclos.
     *
     * RF-06: Asignación de espacios de parqueo
     *
     * @param espacios Colección de espacios del parqueadero
     * @param index Índice actual en la iteración (comienza en 0)
     * @param tipo Tipo de vehículo buscado
     * @return EspacioParqueo disponible o null si no hay
     */
    public EspacioParqueo asignarEspacioRecursivo(List<EspacioParqueo> espacios, int index, TipoVehiculo tipo) {
        // Caso base: se alcanzó el final de la lista
        if (index >= espacios.size()) {
            return null;
        }

        EspacioParqueo espacioActual = espacios.get(index);

        // Si encontramos un espacio disponible del tipo correcto
        if (espacioActual.isDisponible() && espacioActual.getTipoVehiculo() == tipo) {
            return espacioActual;
        }

        // Caso recursivo: continuar buscando en el siguiente índice
        return asignarEspacioRecursivo(espacios, index + 1, tipo);
    }

    /**
     * Busca recursivamente en el historial de parqueo acumulando coincidencias exactas por placa.
     * Utiliza recursividad para iterar sin ciclos, acumulando resultados en una lista.
     *
     * RF-08, RF-18: Auditoría y búsqueda en historial
     *
     * @param historial Lista del historial de parqueo
     * @param index Índice actual en la iteración
     * @param placa Placa del vehículo a buscar
     * @param resultados Lista acumuladora de coincidencias
     * @return Lista con todos los registros que coinciden con la placa
     */
    public List<RegistroParqueo> buscarVehiculoHistorialRecursivo(List<RegistroParqueo> historial,
                                                                   int index, String placa,
                                                                   List<RegistroParqueo> resultados) {
        // Caso base: se alcanzó el final del historial
        if (index >= historial.size()) {
            return resultados;
        }

        RegistroParqueo registroActual = historial.get(index);

        // Si la placa coincide exactamente, añadir a resultados
        if (registroActual.getVehiculo() != null &&
            registroActual.getVehiculo().getPlaca().equals(placa)) {
            resultados.add(registroActual);
        }

        // Caso recursivo: continuar buscando en el siguiente índice
        return buscarVehiculoHistorialRecursivo(historial, index + 1, placa, resultados);
    }

    /**
     * Calcula el costo total de forma recursiva por bloques/fracciones de tiempo.
     * Descuenta el intervalo en cada recursión hasta que los minutos sean <= 0.
     *
     * RF-10: Cálculo de tarifas con fracciones de tiempo
     *
     * @param minutosRestantes Minutos a cobrar (disminuye en cada recursión)
     * @param tarifaFraccion Tarifa por cada fracción (bloque de tiempo)
     * @param intervaloFraccion Duración de cada fracción en minutos
     * @return Costo total acumulado
     */
    public double calcularTarifaRecursiva(long minutosRestantes, double tarifaFraccion,
                                          long intervaloFraccion) {
        // Caso base: no hay más minutos para cobrar
        if (minutosRestantes <= 0) {
            return 0.0;
        }

        // Si los minutos restantes son menores que el intervalo,
        // cobrar una fracción completa (redondeo hacia arriba)
        if (minutosRestantes <= intervaloFraccion) {
            return tarifaFraccion;
        }

        // Caso recursivo: cobrar una fracción y sumar el resto recursivamente
        // Descartamos el intervalo de esta fracción y recursionamos
        return tarifaFraccion + calcularTarifaRecursiva(
            minutosRestantes - intervaloFraccion,
            tarifaFraccion,
            intervaloFraccion
        );
    }

    // ==================== MÉTODOS DE LÓGICA DE NEGOCIO ====================

    /**
     * Registra el ingreso de un vehículo al parqueadero.
     * Valida cupos disponibles, busca espacio, crea vehículo y registro.
     *
     * RF-03: Registro de entrada de vehículos
     * RF-04: Validación de cupos disponibles
     * RF-05: Tipos de vehículos
     *
     * @param placa Placa del vehículo
     * @param tipo Tipo de vehículo (CARRO, MOTO, BICICLETA)
     * @param operador Usuario que registra el ingreso
     * @return Objeto con resultado de operación (éxito, mensaje, registro)
     */
    public ResultadoOperacion registrarIngreso(String placa, TipoVehiculo tipo, Usuario operador) {
        // Validar que no exista vehículo activo con esa placa
        if (repository.obtenerVehiculoActivo(placa) != null) {
            return new ResultadoOperacion(false,
                "Vehículo con placa " + placa + " ya está en el parqueadero", null);
        }

        // Validar que hay cupos disponibles para este tipo
        int espaciosDisponibles = repository.obtenerEspaciosDisponiblesPorTipo(tipo);
        if (espaciosDisponibles == 0) {
            return new ResultadoOperacion(false,
                "No hay espacios disponibles para " + tipo, null);
        }

        // Buscar espacio usando recursividad
        List<EspacioParqueo> listEspacios = new ArrayList<>(repository.obtenerTodosEspacios());
        EspacioParqueo espacioAsignado = asignarEspacioRecursivo(listEspacios, 0, tipo);

        if (espacioAsignado == null) {
            return new ResultadoOperacion(false,
                "Error al buscar espacio disponible", null);
        }

        // Crear vehículo
        Vehiculo vehiculo = new Vehiculo(
            placa,
            tipo,
            LocalDateTime.now(),
            EstadoVehiculo.ACTIVO,
            "Ingreso registrado por " + operador.getNombreUsuario()
        );

        // Registrar vehículo como activo
        if (!repository.registrarVehiculoActivo(vehiculo)) {
            return new ResultadoOperacion(false,
                "Error al registrar vehículo activo", null);
        }

        // Ocupar el espacio
        if (!repository.ocuparEspacio(espacioAsignado.getNumeroEspacio(), placa)) {
            repository.removerVehiculoActivo(placa);
            return new ResultadoOperacion(false,
                "Error al ocupar espacio", null);
        }

        // Crear registro de parqueo
        RegistroParqueo registro = repository.crearRegistroParqueo(vehiculo,
            espacioAsignado.getNumeroEspacio(), operador);

        String mensaje = String.format(
            "✓ Vehículo %s (%s) ingresó en espacio #%d",
            placa, tipo, espacioAsignado.getNumeroEspacio()
        );

        return new ResultadoOperacion(true, mensaje, registro);
    }

    /**
     * Registra la salida de un vehículo, calcula el pago y libera el espacio.
     * Utiliza recursividad para calcular la tarifa por fracciones de tiempo.
     *
     * RF-11: Gestión de pagos
     * RF-13: Liberación de cupos
     * RF-10: Cálculo de tarifa por fracciones
     *
     * @param placa Placa del vehículo que sale
     * @param operador Usuario que registra la salida
     * @return Objeto con resultado de operación (éxito, mensaje, monto pagado)
     */
    public ResultadoOperacionPago registrarSalidaYPago(String placa, Usuario operador) {
        // Buscar vehículo activo
        Vehiculo vehiculo = repository.obtenerVehiculoActivo(placa);
        if (vehiculo == null) {
            return new ResultadoOperacionPago(false,
                "Vehículo con placa " + placa + " no está en el parqueadero", 0.0);
        }

        // Buscar registro en historial usando recursividad
        List<RegistroParqueo> registrosBusca = buscarVehiculoHistorialRecursivo(
            repository.obtenerHistorialCompleto(), 0, placa, new ArrayList<>()
        );

        // El registro más reciente es el que estamos procesando
        RegistroParqueo registroActual = null;
        for (RegistroParqueo reg : registrosBusca) {
            if (reg.getVehiculo().getEstado() == EstadoVehiculo.ACTIVO) {
                registroActual = reg;
                break;
            }
        }

        if (registroActual == null) {
            return new ResultadoOperacionPago(false,
                "No se encontró registro activo para la placa " + placa, 0.0);
        }

        // Calcular duración
        LocalDateTime ahora = LocalDateTime.now();
        long minutosParqueo = ChronoUnit.MINUTES.between(vehiculo.getHoraIngreso(), ahora);

        // Obtener tarifa base
        String tipoVehiculoStr = vehiculo.getTipoVehiculo().toString();
        double tarifaPorHora = repository.obtenerTarifa(tipoVehiculoStr);

        if (tarifaPorHora == 0) {
            return new ResultadoOperacionPago(false,
                "No existe tarifa configurada para " + tipoVehiculoStr, 0.0);
        }

        // Calcular costo usando recursividad
        // Tarifa por cada 60 minutos (1 hora)
        double costoPorFraccion = tarifaPorHora;
        long intervaloMinutos = 60;
        double costoTotal = calcularTarifaRecursiva(minutosParqueo, costoPorFraccion, intervaloMinutos);

        // Actualizar vehículo
        vehiculo.setHoraSalida(ahora);
        vehiculo.setEstado(EstadoVehiculo.FINALIZADO);

        // Actualizar registro
        registroActual.setEstadoPago(EstadoPago.PAGADO);
        registroActual.setValorPagado(costoTotal);
        registroActual.setNovedades(String.format(
            "Salida registrada por %s. Duración: %d minutos. Costo: $%.2f",
            operador.getNombreUsuario(), minutosParqueo, costoTotal
        ));
        repository.actualizarRegistroParqueo(registroActual);

        // Liberar espacio
        int numeroEspacio = registroActual.getNumeroEspacioAsignado();
        if (!repository.liberarEspacio(numeroEspacio)) {
            return new ResultadoOperacionPago(false,
                "Error al liberar espacio", costoTotal);
        }

        // Remover de vehículos activos
        if (!repository.removerVehiculoActivo(placa)) {
            return new ResultadoOperacionPago(false,
                "Error al remover vehículo de activos", costoTotal);
        }

        String mensaje = String.format(
            "✓ Vehículo %s salió correctamente. Tiempo: %d min. Pago: $%.2f",
            placa, minutosParqueo, costoTotal
        );

        return new ResultadoOperacionPago(true, mensaje, costoTotal);
    }

    /**
     * Obtiene el historial completo de un vehículo usando búsqueda recursiva.
     *
     * @param placa Placa del vehículo a auditar
     * @return Lista de todos los registros de ese vehículo
     */
    public List<RegistroParqueo> obtenerHistorialVehiculo(String placa) {
        return buscarVehiculoHistorialRecursivo(
            repository.obtenerHistorialCompleto(), 0, placa, new ArrayList<>()
        );
    }

    /**
     * Obtiene el estado actual del parqueadero.
     *
     * @return Objeto con información de ocupación y disponibilidad
     */
    public EstadoParqueadero obtenerEstadoParqueadero() {
        return new EstadoParqueadero(
            repository.obtenerEspaciosOcupados(),
            repository.obtenerCapacidadTotal(),
            repository.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.CARRO),
            repository.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.MOTO),
            repository.obtenerEspaciosDisponiblesPorTipo(TipoVehiculo.BICICLETA),
            repository.obtenerCapacidadPorTipo(TipoVehiculo.CARRO),
            repository.obtenerCapacidadPorTipo(TipoVehiculo.MOTO),
            repository.obtenerCapacidadPorTipo(TipoVehiculo.BICICLETA),
            repository.obtenerPorcentajeOcupacion()
        );
    }

    /**
     * Reporta ingresos por tipo de vehículo.
     *
     * @return Map con totales por tipo
     */
    public Map<TipoVehiculo, Integer> reporteIngresosPorTipo() {
        Map<TipoVehiculo, Integer> reporte = new HashMap<>();
        reporte.put(TipoVehiculo.CARRO, 0);
        reporte.put(TipoVehiculo.MOTO, 0);
        reporte.put(TipoVehiculo.BICICLETA, 0);

        for (RegistroParqueo registro : repository.obtenerHistorialCompleto()) {
            if (registro.getVehiculo() != null) {
                TipoVehiculo tipo = registro.getVehiculo().getTipoVehiculo();
                reporte.put(tipo, reporte.get(tipo) + 1);
            }
        }

        return reporte;
    }

    /**
     * Reporta ingresos totales en dinero.
     *
     * @return Total recaudado
     */
    public double reporteIngresosTotales() {
        double total = 0.0;

        for (RegistroParqueo registro : repository.obtenerHistorialCompleto()) {
            if (registro.getEstadoPago() == EstadoPago.PAGADO) {
                total += registro.getValorPagado();
            }
        }

        return total;
    }

    /**
     * Reporta transacciones pendientes de pago.
     *
     * @return Lista de registros pendientes
     */
    public List<RegistroParqueo> reportePendientes() {
        return repository.obtenerRegistrosPorEstadoPago(EstadoPago.PENDIENTE);
    }

    // ==================== NUEVOS MÉTODOS ANALÍTICOS Y DE CONTROL ====================

    /**
     * Genera recursivamente el reporte diario: total recaudado, ingresos y salidas.
     * Se asume que la lista de registros contiene únicamente los del día o que el
     * llamador filtra por fecha previa a la invocación.
     *
     * RF-19: Reporte diario recursivo
     *
     * @param registros Lista de registros a procesar
     * @param index Índice actual (comenzar con registros.size() - 1 o 0 según preferencia)
     * @param totalRecaudado Acumulador para total recaudado
     * @param totalIngresos Acumulador para cantidad de ingresos
     * @param totalSalidas Acumulador para cantidad de salidas
     * @return ReporteDiario con los totales acumulados
     */
    public ReporteDiario generarReporteDiarioRecursivo(List<RegistroParqueo> registros,
                                                        int index,
                                                        double totalRecaudado,
                                                        int totalIngresos,
                                                        int totalSalidas) {
        // Validaciones iniciales
        if (registros == null || registros.isEmpty()) {
            return new ReporteDiario(totalRecaudado, totalIngresos, totalSalidas);
        }

        // Caso base: índice fuera de rango (procesados todos)
        if (index >= registros.size()) {
            return new ReporteDiario(totalRecaudado, totalIngresos, totalSalidas);
        }

        RegistroParqueo actual = registros.get(index);

        // Acumular recaudado si el registro está marcado como pagado
        if (actual.getEstadoPago() == EstadoPago.PAGADO) {
            totalRecaudado += actual.getValorPagado();
        }

        // Consideramos que un ingreso es cuando el vehiculo en el registro está en estado ACTIVO
        if (actual.getVehiculo() != null && actual.getVehiculo().getEstado() == EstadoVehiculo.ACTIVO) {
            totalIngresos++;
        }

        // Consideramos que una salida es cuando el vehiculo está FINALIZADO
        if (actual.getVehiculo() != null && actual.getVehiculo().getEstado() == EstadoVehiculo.FINALIZADO) {
            totalSalidas++;
        }

        // Llamada recursiva al siguiente índice
        return generarReporteDiarioRecursivo(registros, index + 1, totalRecaudado, totalIngresos, totalSalidas);
    }

    /**
     * Anula un registro de parqueo guardando la justificación y validando rol de administrador.
     * Solo un usuario con rol ADMINISTRADOR puede anular operaciones.
     *
     * RF-20: Anulación de operaciones con trazabilidad
     *
     * @param idRegistro ID del registro a anular
     * @param justificacion Motivo o justificación de la anulación
     * @param admin Usuario que solicita la anulación (debe ser administrador)
     * @return true si la anulación se realizó, false si no (registro no encontrado o permiso denegado)
     */
    public boolean anularOperacion(Long idRegistro, String justificacion, Usuario admin) {
        if (idRegistro == null || justificacion == null || admin == null) return false;

        // Validar rol estrictamente ADMINISTRADOR
        if (admin.getRol() == null || admin.getRol() != RolUsuario.ADMINISTRADOR) {
            return false;
        }

        // Buscar registro por ID (en Repo ID es int)
        RegistroParqueo registro = repository.obtenerRegistroParqueo(idRegistro.intValue());
        if (registro == null) {
            return false; // no existe
        }

        // Añadir justificación a las novedades y marcar estado de pago si aplica
        StringBuilder sb = new StringBuilder();
        sb.append("ANULACIÓN: ").append(justificacion).append(" | Anulada por: ")
          .append(admin.getNombreUsuario()).append("\n");

        if (registro.getNovedades() != null && !registro.getNovedades().isEmpty()) {
            sb.append("ANTES: ").append(registro.getNovedades());
        }

        registro.setNovedades(sb.toString());

        // Marcar valorPagado a 0 y estadoPago a PENDIENTE (o a un estado específico si existe)
        registro.setValorPagado(0.0);
        registro.setEstadoPago(EstadoPago.PENDIENTE);

        // Actualizar en repositorio
        return repository.actualizarRegistroParqueo(registro);
    }

    /**
     * Obtiene un resumen del tablero de ocupación en tiempo real.
     * Calcula totales, ocupados, disponibles y porcentaje.
     *
     * RF-22: Tablero de ocupación
     *
     * @return TableroOcupacion con métricas de ocupación
     */
    public TableroOcupacion obtenerTableroOcupacion() {
        int totalCupos = repository.obtenerCapacidadTotal();
        int cuposOcupados = repository.obtenerEspaciosOcupados();
        int cuposDisponibles = Math.max(0, totalCupos - cuposOcupados);
        double porcentaje = totalCupos == 0 ? 0.0 : (cuposOcupados * 100.0) / totalCupos;

        return new TableroOcupacion(totalCupos, cuposOcupados, cuposDisponibles, porcentaje);
    }

    // ==================== CLASES INTERNAS PARA RESULTADOS ====================

    /**
     * Clase interna para encapsular resultados de operaciones.
     */
    public static class ResultadoOperacion {
        private boolean exitoso;
        private String mensaje;
        private Object dato;

        public ResultadoOperacion(boolean exitoso, String mensaje, Object dato) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.dato = dato;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public Object getDato() {
            return dato;
        }

        @Override
        public String toString() {
            return (exitoso ? "✓ " : "✗ ") + mensaje;
        }
    }

    /**
     * Clase interna para resultados de operaciones con pago.
     */
    public static class ResultadoOperacionPago {
        private boolean exitoso;
        private String mensaje;
        private double montoPagado;

        public ResultadoOperacionPago(boolean exitoso, String mensaje, double montoPagado) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.montoPagado = montoPagado;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public double getMontoPagado() {
            return montoPagado;
        }

        @Override
        public String toString() {
            return (exitoso ? "✓ " : "✗ ") + mensaje + " | Monto: $" + String.format("%.2f", montoPagado);
        }
    }

    /**
     * Clase interna para estado del parqueadero.
     */
    public static class EstadoParqueadero {
        private int ocupados;
        private int capacidadTotal;
        private int disponiblesCarros;
        private int disponiblesMotos;
        private int disponiblesBicicletas;
        private int capacidadCarros;
        private int capacidadMotos;
        private int capacidadBicicletas;
        private double porcentajeOcupacion;

        public EstadoParqueadero(int ocupados, int capacidadTotal, int disponiblesCarros,
                                 int disponiblesMotos, int disponiblesBicicletas,
                                 int capacidadCarros, int capacidadMotos, int capacidadBicicletas,
                                 double porcentajeOcupacion) {
            this.ocupados = ocupados;
            this.capacidadTotal = capacidadTotal;
            this.disponiblesCarros = disponiblesCarros;
            this.disponiblesMotos = disponiblesMotos;
            this.disponiblesBicicletas = disponiblesBicicletas;
            this.capacidadCarros = capacidadCarros;
            this.capacidadMotos = capacidadMotos;
            this.capacidadBicicletas = capacidadBicicletas;
            this.porcentajeOcupacion = porcentajeOcupacion;
        }

        public int getOcupados() { return ocupados; }
        public int getCapacidadTotal() { return capacidadTotal; }
        public int getDisponiblesCarros() { return disponiblesCarros; }
        public int getDisponiblesMotos() { return disponiblesMotos; }
        public int getDisponiblesBicicletas() { return disponiblesBicicletas; }
        public int getCapacidadCarros() { return capacidadCarros; }
        public int getCapacidadMotos() { return capacidadMotos; }
        public int getCapacidadBicicletas() { return capacidadBicicletas; }
        public double getPorcentajeOcupacion() { return porcentajeOcupacion; }

        @Override
        public String toString() {
            return String.format(
                "Estado Parqueadero:\n" +
                "  Ocupados: %d/%d (%.1f%%)\n" +
                "  Carros: %d/%d disponibles\n" +
                "  Motos: %d/%d disponibles\n" +
                "  Bicicletas: %d/%d disponibles",
                ocupados, capacidadTotal, porcentajeOcupacion,
                disponiblesCarros, capacidadCarros,
                disponiblesMotos, capacidadMotos,
                disponiblesBicicletas, capacidadBicicletas
            );
        }
    }

    // ==================== CLASES DE RESULTADO ADICIONALES ====================

    /**
     * DTO para reporte diario (total recaudado, ingresos y salidas)
     */
    public static class ReporteDiario {
        private final double totalRecaudado;
        private final int totalIngresos;
        private final int totalSalidas;

        public ReporteDiario(double totalRecaudado, int totalIngresos, int totalSalidas) {
            this.totalRecaudado = totalRecaudado;
            this.totalIngresos = totalIngresos;
            this.totalSalidas = totalSalidas;
        }

        public double getTotalRecaudado() { return totalRecaudado; }
        public int getTotalIngresos() { return totalIngresos; }
        public int getTotalSalidas() { return totalSalidas; }

        @Override
        public String toString() {
            return String.format("ReporteDiario{recaudado=$%.2f, ingresos=%d, salidas=%d}",
                totalRecaudado, totalIngresos, totalSalidas);
        }
    }

    /**
     * DTO para tablero de ocupación (total, ocupados, disponibles, porcentaje)
     */
    public static class TableroOcupacion {
        private final int totalCupos;
        private final int cuposOcupados;
        private final int cuposDisponibles;
        private final double porcentajeOcupacion;

        public TableroOcupacion(int totalCupos, int cuposOcupados, int cuposDisponibles, double porcentajeOcupacion) {
            this.totalCupos = totalCupos;
            this.cuposOcupados = cuposOcupados;
            this.cuposDisponibles = cuposDisponibles;
            this.porcentajeOcupacion = porcentajeOcupacion;
        }

        public int getTotalCupos() { return totalCupos; }
        public int getCuposOcupados() { return cuposOcupados; }
        public int getCuposDisponibles() { return cuposDisponibles; }
        public double getPorcentajeOcupacion() { return porcentajeOcupacion; }

        @Override
        public String toString() {
            return String.format("Tablero{total=%d, ocupados=%d, disponibles=%d, porcentaje=%.1f%%}",
                totalCupos, cuposOcupados, cuposDisponibles, porcentajeOcupacion);
        }
    }

}
