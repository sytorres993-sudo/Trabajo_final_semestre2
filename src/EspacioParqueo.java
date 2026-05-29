/**
 * Clase que representa un espacio de estacionamiento en el parqueadero.
 * RF-04, RF-15: Gestión de espacios y capacidad
 */
public class EspacioParqueo {
    private int numeroEspacio;
    private TipoVehiculo tipoVehiculo;
    private boolean disponible;
    private String placaVehiculoActual;

    /**
     * Constructor del Espacio de Parqueo.
     *
     * @param numeroEspacio Número único del espacio
     * @param tipoVehiculo Tipo de vehículo que puede usar este espacio
     */
    public EspacioParqueo(int numeroEspacio, TipoVehiculo tipoVehiculo) {
        this.numeroEspacio = numeroEspacio;
        this.tipoVehiculo = tipoVehiculo;
        this.disponible = true;
        this.placaVehiculoActual = null;
    }

    // Getters y Setters

    /**
     * Obtiene el número del espacio.
     *
     * @return Número del espacio
     */
    public int getNumeroEspacio() {
        return numeroEspacio;
    }

    /**
     * Obtiene el tipo de vehículo permitido en este espacio.
     *
     * @return Tipo de vehículo
     */
    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    /**
     * Verifica si el espacio está disponible.
     *
     * @return true si está disponible, false si está ocupado
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * Establece el estado de disponibilidad del espacio.
     *
     * @param disponible true para disponible, false para ocupado
     */
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Obtiene la placa del vehículo que ocupa el espacio.
     *
     * @return Placa del vehículo o null si está vacío
     */
    public String getPlacaVehiculoActual() {
        return placaVehiculoActual;
    }

    /**
     * Establece la placa del vehículo que ocupa el espacio.
     *
     * @param placaVehiculoActual Placa a asignar o null para liberar
     */
    public void setPlacaVehiculoActual(String placaVehiculoActual) {
        this.placaVehiculoActual = placaVehiculoActual;
    }

    /**
     * Representación en texto del espacio de parqueo.
     *
     * @return String con la información del espacio
     */
    @Override
    public String toString() {
        return "EspacioParqueo{" +
                "numeroEspacio=" + numeroEspacio +
                ", tipoVehiculo=" + tipoVehiculo +
                ", disponible=" + disponible +
                ", placaVehiculoActual='" + placaVehiculoActual + '\'' +
                '}';
    }
}

