import java.util.*;

/**
 * Clase que gestiona el almacenamiento en memoria de todos los datos del parqueadero.
 * Implementa el patrón Repository usando Java Collections Framework.
 *
 * RF-04: Gestión de espacios del parqueadero
 * RF-08: Búsqueda rápida de vehículos activos por placa
 * RF-14: Gestión de tarifas configurables
 * RF-15: Control de capacidad máxima y por tipo
 * RF-18: Auditoría mediante historial de ingresos y salidas
 */
public class ParqueaderoRepository {

    // ==================== Estructuras de datos ====================

    /** Map para buscar vehículos activos por placa (O(1) búsqueda) - RF-08 */
    private Map<String, Vehiculo> vehiculosActivos;

    /** Lista que mantiene el historial de todos los registros de parqueo - RF-18 */
    private List<RegistroParqueo> historialParqueo;

    /** Map para almacenar tarifas por tipo de vehículo - RF-14 */
    private Map<String, Double> tarifasPorTipo;

    /** Map para almacenar los espacios por número - RF-04, RF-15 */
    private Map<Integer, EspacioParqueo> espacios;

    /** Map para contar espacios disponibles por tipo de vehículo */
    private Map<TipoVehiculo, Integer> capacidadPorTipo;

    /** Map para usuarios del sistema */
    private Map<String, Usuario> usuariosRegistrados;

    /** Capacidad total del parqueadero */
    private int capacidadTotal;

    /** Contador para IDs únicos */
    private int contadorRegistros;

    // ==================== Constructores ====================

    /**
     * Constructor del repositorio del parqueadero.
     * Inicializa las estructuras de datos con capacidad especificada.
     *
     * @param capacidadCarros Cantidad de espacios para carros
     * @param capacidadMotos Cantidad de espacios para motos
     * @param capacidadBicicletas Cantidad de espacios para bicicletas
     */
    public ParqueaderoRepository(int capacidadCarros, int capacidadMotos, int capacidadBicicletas) {
        // Inicializar mapas y listas
        this.vehiculosActivos = new HashMap<>();
        this.historialParqueo = new ArrayList<>();
        this.tarifasPorTipo = new HashMap<>();
        this.espacios = new LinkedHashMap<>();
        this.capacidadPorTipo = new HashMap<>();
        this.usuariosRegistrados = new HashMap<>();
        this.contadorRegistros = 1;

        // Configurar capacidades
        this.capacidadPorTipo.put(TipoVehiculo.CARRO, capacidadCarros);
        this.capacidadPorTipo.put(TipoVehiculo.MOTO, capacidadMotos);
        this.capacidadPorTipo.put(TipoVehiculo.BICICLETA, capacidadBicicletas);
        this.capacidadTotal = capacidadCarros + capacidadMotos + capacidadBicicletas;

        // Crear espacios del parqueadero
        crearEspaciosParqueo(capacidadCarros, capacidadMotos, capacidadBicicletas);

        // Inicializar tarifas por defecto
        inicializarTarifas();
    }

    // ==================== Métodos privados de inicialización ====================

    /**
     * Crea los espacios de parqueo según la capacidad especificada.
     *
     * @param capacidadCarros Espacios para carros
     * @param capacidadMotos Espacios para motos
     * @param capacidadBicicletas Espacios para bicicletas
     */
    private void crearEspaciosParqueo(int capacidadCarros, int capacidadMotos, int capacidadBicicletas) {
        int numeroEspacio = 1;

        // Crear espacios para carros
        for (int i = 0; i < capacidadCarros; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.CARRO));
            numeroEspacio++;
        }

        // Crear espacios para motos
        for (int i = 0; i < capacidadMotos; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.MOTO));
            numeroEspacio++;
        }

        // Crear espacios para bicicletas
        for (int i = 0; i < capacidadBicicletas; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.BICICLETA));
            numeroEspacio++;
        }
    }

    /**
     * Inicializa las tarifas por defecto para cada tipo de vehículo.
     */
    private void inicializarTarifas() {
        tarifasPorTipo.put("CARRO", 5000.0);
        tarifasPorTipo.put("MOTO", 3000.0);
        tarifasPorTipo.put("BICICLETA", 1000.0);
    }

    // ==================== CRUD de Usuarios ====================

    /**
     * Crea un nuevo usuario en el sistema.
     * Valida que no exista otro usuario con el mismo nombre.
     *
     * @param usuario Usuario a crear
     * @return true si se creó correctamente, false si ya existe
     */
    public boolean crearUsuario(Usuario usuario) {
        if (usuariosRegistrados.containsKey(usuario.getNombreUsuario())) {
            return false; // Usuario ya existe
        }
        usuariosRegistrados.put(usuario.getNombreUsuario(), usuario);
        return true;
    }

    /**
     * Obtiene un usuario por nombre de usuario.
     *
     * @param nombreUsuario Nombre del usuario a buscar
     * @return El usuario si existe, null si no existe
     */
    public Usuario obtenerUsuario(String nombreUsuario) {
        return usuariosRegistrados.get(nombreUsuario);
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param usuario Usuario con datos actualizados
     * @return true si se actualizó, false si no existe
     */
    public boolean actualizarUsuario(Usuario usuario) {
        if (!usuariosRegistrados.containsKey(usuario.getNombreUsuario())) {
            return false;
        }
        usuariosRegistrados.put(usuario.getNombreUsuario(), usuario);
        return true;
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param nombreUsuario Nombre del usuario a eliminar
     * @return true si se eliminó, false si no existe
     */
    public boolean eliminarUsuario(String nombreUsuario) {
        return usuariosRegistrados.remove(nombreUsuario) != null;
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Colección de usuarios
     */
    public Collection<Usuario> obtenerTodosUsuarios() {
        return new ArrayList<>(usuariosRegistrados.values());
    }

    // ==================== CRUD de Vehículos Activos ====================

    /**
     * Registra un vehículo como activo en el parqueadero.
     * Valida que no exista otro vehículo con la misma placa activa.
     *
     * @param vehiculo Vehículo a registrar
     * @return true si se registró correctamente, false si ya existe
     */
    public boolean registrarVehiculoActivo(Vehiculo vehiculo) {
        if (vehiculosActivos.containsKey(vehiculo.getPlaca())) {
            return false; // Vehículo ya existe
        }
        vehiculosActivos.put(vehiculo.getPlaca(), vehiculo);
        return true;
    }

    /**
     * Obtiene un vehículo activo por su placa.
     *
     * @param placa Placa del vehículo
     * @return El vehículo si existe y está activo, null si no existe
     */
    public Vehiculo obtenerVehiculoActivo(String placa) {
        return vehiculosActivos.get(placa);
    }

    /**
     * Elimina un vehículo de los activos (cuando sale del parqueadero).
     *
     * @param placa Placa del vehículo a eliminar
     * @return true si se eliminó, false si no existe
     */
    public boolean removerVehiculoActivo(String placa) {
        return vehiculosActivos.remove(placa) != null;
    }

    /**
     * Obtiene todos los vehículos activos.
     *
     * @return Colección de vehículos activos
     */
    public Collection<Vehiculo> obtenerVehiculosActivos() {
        return new ArrayList<>(vehiculosActivos.values());
    }

    /**
     * Obtiene la cantidad de vehículos activos.
     *
     * @return Cantidad de vehículos
     */
    public int obtenerCantidadVehiculosActivos() {
        return vehiculosActivos.size();
    }

    // ==================== CRUD de Tarifas ====================

    /**
     * Establece o actualiza la tarifa para un tipo de vehículo.
     *
     * @param tipoVehiculo Tipo de vehículo (CARRO, MOTO, BICICLETA)
     * @param tarifa Valor de la tarifa
     */
    public void establecerTarifa(String tipoVehiculo, double tarifa) {
        if (tarifa > 0) {
            tarifasPorTipo.put(tipoVehiculo, tarifa);
        }
    }

    /**
     * Obtiene la tarifa para un tipo de vehículo.
     *
     * @param tipoVehiculo Tipo de vehículo
     * @return Tarifa configurada, 0 si no existe
     */
    public double obtenerTarifa(String tipoVehiculo) {
        return tarifasPorTipo.getOrDefault(tipoVehiculo, 0.0);
    }

    /**
     * Obtiene todas las tarifas configuradas.
     *
     * @return Map con tarifas por tipo
     */
    public Map<String, Double> obtenerTodasTarifas() {
        return new HashMap<>(tarifasPorTipo);
    }

    // ==================== Gestión de Espacios ====================

    /**
     * Obtiene un espacio disponible para un tipo de vehículo específico.
     *
     * @param tipoVehiculo Tipo de vehículo
     * @return Espacio disponible, null si no hay
     */
    public EspacioParqueo obtenerEspacioDisponible(TipoVehiculo tipoVehiculo) {
        for (EspacioParqueo espacio : espacios.values()) {
            if (espacio.isDisponible() && espacio.getTipoVehiculo() == tipoVehiculo) {
                return espacio;
            }
        }
        return null;
    }

    /**
     * Ocupa un espacio con un vehículo.
     *
     * @param numeroEspacio Número del espacio
     * @param placa Placa del vehículo
     * @return true si se ocupó, false si no existe o ya está ocupado
     */
    public boolean ocuparEspacio(int numeroEspacio, String placa) {
        EspacioParqueo espacio = espacios.get(numeroEspacio);
        if (espacio == null || !espacio.isDisponible()) {
            return false;
        }
        espacio.setDisponible(false);
        espacio.setPlacaVehiculoActual(placa);
        return true;
    }

    /**
     * Libera un espacio ocupado.
     *
     * @param numeroEspacio Número del espacio
     * @return true si se liberó, false si no existe o ya estaba libre
     */
    public boolean liberarEspacio(int numeroEspacio) {
        EspacioParqueo espacio = espacios.get(numeroEspacio);
        if (espacio == null || espacio.isDisponible()) {
            return false;
        }
        espacio.setDisponible(true);
        espacio.setPlacaVehiculoActual(null);
        return true;
    }

    /**
     * Obtiene un espacio por su número.
     *
     * @param numeroEspacio Número del espacio
     * @return El espacio si existe, null si no
     */
    public EspacioParqueo obtenerEspacio(int numeroEspacio) {
        return espacios.get(numeroEspacio);
    }

    /**
     * Obtiene todos los espacios del parqueadero.
     *
     * @return Colección de espacios
     */
    public Collection<EspacioParqueo> obtenerTodosEspacios() {
        return new ArrayList<>(espacios.values());
    }

    /**
     * Obtiene la cantidad de espacios disponibles para un tipo de vehículo.
     *
     * @param tipoVehiculo Tipo de vehículo
     * @return Cantidad de espacios disponibles
     */
    public int obtenerEspaciosDisponiblesPorTipo(TipoVehiculo tipoVehiculo) {
        int disponibles = 0;
        for (EspacioParqueo espacio : espacios.values()) {
            if (espacio.isDisponible() && espacio.getTipoVehiculo() == tipoVehiculo) {
                disponibles++;
            }
        }
        return disponibles;
    }

    /**
     * Obtiene la capacidad máxima para un tipo de vehículo.
     *
     * @param tipoVehiculo Tipo de vehículo
     * @return Capacidad máxima
     */
    public int obtenerCapacidadPorTipo(TipoVehiculo tipoVehiculo) {
        return capacidadPorTipo.getOrDefault(tipoVehiculo, 0);
    }

    /**
     * Obtiene la capacidad total del parqueadero.
     *
     * @return Capacidad total
     */
    public int obtenerCapacidadTotal() {
        return capacidadTotal;
    }

    // ==================== Gestión de Registros de Parqueo ====================

    /**
     * Crea un nuevo registro de parqueo y lo añade al historial.
     *
     * @param vehiculo Vehículo del registro
     * @param numeroEspacio Número del espacio asignado
     * @param usuarioResponsable Usuario que atiende
     * @return El registro creado con ID asignado
     */
    public RegistroParqueo crearRegistroParqueo(Vehiculo vehiculo, int numeroEspacio, Usuario usuarioResponsable) {
        RegistroParqueo registro = new RegistroParqueo();
        registro.setId(contadorRegistros++);
        registro.setVehiculo(vehiculo);
        registro.setNumeroEspacioAsignado(numeroEspacio);
        registro.setUsuarioResponsable(usuarioResponsable);
        registro.setEstadoPago(EstadoPago.PENDIENTE);
        historialParqueo.add(registro);
        return registro;
    }

    /**
     * Actualiza un registro de parqueo existente.
     *
     * @param registro Registro con datos actualizados
     * @return true si se actualizó, false si no existe
     */
    public boolean actualizarRegistroParqueo(RegistroParqueo registro) {
        for (int i = 0; i < historialParqueo.size(); i++) {
            if (historialParqueo.get(i).getId() == registro.getId()) {
                historialParqueo.set(i, registro);
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene un registro de parqueo por su ID.
     *
     * @param id ID del registro
     * @return El registro si existe, null si no
     */
    public RegistroParqueo obtenerRegistroParqueo(int id) {
        for (RegistroParqueo registro : historialParqueo) {
            if (registro.getId() == id) {
                return registro;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los registros del historial.
     *
     * @return Lista del historial completo
     */
    public List<RegistroParqueo> obtenerHistorialCompleto() {
        return new ArrayList<>(historialParqueo);
    }

    /**
     * Obtiene registros del historial por usuario responsable.
     *
     * @param usuario Usuario a filtrar
     * @return Lista de registros del usuario
     */
    public List<RegistroParqueo> obtenerRegistrosPorUsuario(Usuario usuario) {
        List<RegistroParqueo> registros = new ArrayList<>();
        for (RegistroParqueo registro : historialParqueo) {
            if (registro.getUsuarioResponsable().getId() == usuario.getId()) {
                registros.add(registro);
            }
        }
        return registros;
    }

    /**
     * Obtiene registros del historial por estado de pago.
     *
     * @param estado Estado de pago a filtrar
     * @return Lista de registros con ese estado
     */
    public List<RegistroParqueo> obtenerRegistrosPorEstadoPago(EstadoPago estado) {
        List<RegistroParqueo> registros = new ArrayList<>();
        for (RegistroParqueo registro : historialParqueo) {
            if (registro.getEstadoPago() == estado) {
                registros.add(registro);
            }
        }
        return registros;
    }

    /**
     * Obtiene la cantidad total de registros en el historial.
     *
     * @return Cantidad de registros
     */
    public int obtenerCantidadRegistros() {
        return historialParqueo.size();
    }

    // ==================== Métodos de utilidad ====================

    /**
     * Verifica si el parqueadero está lleno.
     *
     * @return true si no hay espacios disponibles
     */
    public boolean estaParqueaderoLleno() {
        for (EspacioParqueo espacio : espacios.values()) {
            if (espacio.isDisponible()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene la cantidad de espacios ocupados.
     *
     * @return Cantidad de espacios ocupados
     */
    public int obtenerEspaciosOcupados() {
        int ocupados = 0;
        for (EspacioParqueo espacio : espacios.values()) {
            if (!espacio.isDisponible()) {
                ocupados++;
            }
        }
        return ocupados;
    }

    /**
     * Obtiene el porcentaje de ocupación del parqueadero.
     *
     * @return Porcentaje (0-100)
     */
    public double obtenerPorcentajeOcupacion() {
        int ocupados = obtenerEspaciosOcupados();
        return (ocupados * 100.0) / capacidadTotal;
    }

    /**
     * Limpia completamente el repositorio (para resetear).
     */
    public void limpiar() {
        vehiculosActivos.clear();
        historialParqueo.clear();
        usuariosRegistrados.clear();
        for (EspacioParqueo espacio : espacios.values()) {
            espacio.setDisponible(true);
            espacio.setPlacaVehiculoActual(null);
        }
        contadorRegistros = 1;
    }

    /**
     * Configura la capacidad del parqueadero por tipo. No permite reducir la capacidad
     * por debajo del número de espacios actualmente ocupados. Si la nueva capacidad es
     * válida, recrea la estructura de espacios y preserva las ocupaciones actuales.
     *
     * @param capacidadCarros nueva capacidad para carros
     * @param capacidadMotos nueva capacidad para motos
     * @param capacidadBicicletas nueva capacidad para bicicletas
     * @return true si la reconfiguración fue exitosa, false si no se puede aplicar
     */
    public synchronized boolean configurarCapacidad(int capacidadCarros, int capacidadMotos, int capacidadBicicletas) {
        int nuevaTotal = capacidadCarros + capacidadMotos + capacidadBicicletas;
        int ocupadosActuales = obtenerEspaciosOcupados();
        if (nuevaTotal < ocupadosActuales) {
            // No podemos reducir por debajo de lo ocupado actualmente
            return false;
        }

        // Recolectar placas ocupadas junto con su tipo
        List<String> placasOcupadas = new ArrayList<>();
        Map<String, TipoVehiculo> tipoPorPlaca = new HashMap<>();
        for (EspacioParqueo esp : espacios.values()) {
            if (!esp.isDisponible() && esp.getPlacaVehiculoActual() != null) {
                placasOcupadas.add(esp.getPlacaVehiculoActual());
                tipoPorPlaca.put(esp.getPlacaVehiculoActual(), esp.getTipoVehiculo());
            }
        }

        // Reconstruir espacios
        this.espacios.clear();
        int numeroEspacio = 1;
        for (int i = 0; i < capacidadCarros; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.CARRO));
            numeroEspacio++;
        }
        for (int i = 0; i < capacidadMotos; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.MOTO));
            numeroEspacio++;
        }
        for (int i = 0; i < capacidadBicicletas; i++) {
            espacios.put(numeroEspacio, new EspacioParqueo(numeroEspacio, TipoVehiculo.BICICLETA));
            numeroEspacio++;
        }

        // Reasignar ocupaciones existentes a los primeros espacios del mismo tipo
        for (String placa : placasOcupadas) {
            TipoVehiculo tipo = tipoPorPlaca.getOrDefault(placa, TipoVehiculo.CARRO);
            for (EspacioParqueo esp : espacios.values()) {
                if (esp.isDisponible() && esp.getTipoVehiculo() == tipo) {
                    esp.setDisponible(false);
                    esp.setPlacaVehiculoActual(placa);
                    break;
                }
            }
        }

        // Actualizar capacidad por tipo y total
        this.capacidadPorTipo.put(TipoVehiculo.CARRO, capacidadCarros);
        this.capacidadPorTipo.put(TipoVehiculo.MOTO, capacidadMotos);
        this.capacidadPorTipo.put(TipoVehiculo.BICICLETA, capacidadBicicletas);
        this.capacidadTotal = nuevaTotal;

        return true;
    }
}
