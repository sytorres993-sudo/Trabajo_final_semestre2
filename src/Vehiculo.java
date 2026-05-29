import java.time.LocalDateTime;

/**
 * Clase que representa un Vehículo registrado en el parqueadero.
 * RF-03, RF-05: Registro y seguimiento de vehículos
 */
public class Vehiculo {
    private String placa;
    private TipoVehiculo tipoVehiculo;
    private LocalDateTime horaIngreso;
    private LocalDateTime horaSalida;
    private EstadoVehiculo estado;
    private String observaciones;

    /**
     * Constructor vacío del Vehículo.
     */
    public Vehiculo() {
        this.estado = EstadoVehiculo.ACTIVO;
    }

    /**
     * Constructor con parámetros del Vehículo.
     *
     * @param placa Placa del vehículo
     * @param tipoVehiculo Tipo de vehículo (CARRO, MOTO, BICICLETA)
     * @param horaIngreso Hora de entrada al parqueadero
     * @param estado Estado inicial del vehículo
     * @param observaciones Observaciones adicionales
     */
    public Vehiculo(String placa, TipoVehiculo tipoVehiculo, LocalDateTime horaIngreso,
                    EstadoVehiculo estado, String observaciones) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.horaIngreso = horaIngreso;
        this.estado = estado;
        this.observaciones = observaciones;
        this.horaSalida = null;
    }

    // Getters y Setters con encapsulamiento

    /**
     * Obtiene la placa del vehículo.
     *
     * @return Placa del vehículo
     */
    public String getPlaca() {
        return placa;
    }

    /**
     * Establece la placa del vehículo.
     *
     * @param placa Placa a asignar
     */
    public void setPlaca(String placa) {
        this.placa = placa;
    }

    /**
     * Obtiene el tipo de vehículo.
     *
     * @return Tipo de vehículo
     */
    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    /**
     * Establece el tipo de vehículo.
     *
     * @param tipoVehiculo Tipo a asignar
     */
    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    /**
     * Obtiene la hora de ingreso del vehículo.
     *
     * @return Hora de ingreso
     */
    public LocalDateTime getHoraIngreso() {
        return horaIngreso;
    }

    /**
     * Establece la hora de ingreso del vehículo.
     *
     * @param horaIngreso Hora a asignar
     */
    public void setHoraIngreso(LocalDateTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    /**
     * Obtiene la hora de salida del vehículo.
     *
     * @return Hora de salida
     */
    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    /**
     * Establece la hora de salida del vehículo.
     *
     * @param horaSalida Hora a asignar
     */
    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * Obtiene el estado del vehículo.
     *
     * @return Estado del vehículo
     */
    public EstadoVehiculo getEstado() {
        return estado;
    }

    /**
     * Establece el estado del vehículo.
     *
     * @param estado Estado a asignar
     */
    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    /**
     * Obtiene las observaciones del vehículo.
     *
     * @return Observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Establece las observaciones del vehículo.
     *
     * @param observaciones Observaciones a asignar
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Representación en texto del vehículo.
     *
     * @return String con la información del vehículo
     */
    @Override
    public String toString() {
        return "Vehiculo{" +
                "placa='" + placa + '\'' +
                ", tipoVehiculo=" + tipoVehiculo +
                ", horaIngreso=" + horaIngreso +
                ", horaSalida=" + horaSalida +
                ", estado=" + estado +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}

