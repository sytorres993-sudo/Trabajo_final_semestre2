/**
 * Clase que representa un Registro de Parqueo en el sistema.
 * RF-06, RF-11, RF-12, RF-21: Gestión de registros, espacios, pagos y novedades
 *
 * Vincula un vehículo con un espacio de estacionamiento, información de pago
 * y el usuario responsable de la atención.
 */
public class RegistroParqueo {
    private int id;
    private Vehiculo vehiculo;
    private int numeroEspacioAsignado;
    private double valorPagado;
    private EstadoPago estadoPago;
    private Usuario usuarioResponsable;
    private String novedades;

    /**
     * Constructor vacío del Registro de Parqueo.
     */
    public RegistroParqueo() {
        this.estadoPago = EstadoPago.PENDIENTE;
        this.valorPagado = 0.0;
    }

    /**
     * Constructor con parámetros del Registro de Parqueo.
     *
     * @param id ID único del registro
     * @param vehiculo Vehículo asociado al registro
     * @param numeroEspacioAsignado Número del espacio donde se estaciona
     * @param valorPagado Valor pagado por el parqueo
     * @param estadoPago Estado del pago
     * @param usuarioResponsable Usuario que atendió el registro
     * @param novedades Novedades o eventos ocurridos
     */
    public RegistroParqueo(int id, Vehiculo vehiculo, int numeroEspacioAsignado,
                          double valorPagado, EstadoPago estadoPago,
                          Usuario usuarioResponsable, String novedades) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.numeroEspacioAsignado = numeroEspacioAsignado;
        this.valorPagado = valorPagado;
        this.estadoPago = estadoPago;
        this.usuarioResponsable = usuarioResponsable;
        this.novedades = novedades;
    }

    // Getters y Setters con encapsulamiento

    /**
     * Obtiene el ID del registro de parqueo.
     *
     * @return ID del registro
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del registro de parqueo.
     *
     * @param id ID a asignar
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el vehículo asociado al registro.
     *
     * @return Vehículo del registro
     */
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    /**
     * Establece el vehículo del registro.
     *
     * @param vehiculo Vehículo a asignar
     */
    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    /**
     * Obtiene el número del espacio asignado.
     *
     * @return Número del espacio
     */
    public int getNumeroEspacioAsignado() {
        return numeroEspacioAsignado;
    }

    /**
     * Establece el número del espacio asignado.
     *
     * @param numeroEspacioAsignado Número del espacio a asignar
     */
    public void setNumeroEspacioAsignado(int numeroEspacioAsignado) {
        this.numeroEspacioAsignado = numeroEspacioAsignado;
    }

    /**
     * Obtiene el valor pagado.
     *
     * @return Valor pagado
     */
    public double getValorPagado() {
        return valorPagado;
    }

    /**
     * Establece el valor pagado.
     *
     * @param valorPagado Valor a asignar
     */
    public void setValorPagado(double valorPagado) {
        this.valorPagado = valorPagado;
    }

    /**
     * Obtiene el estado del pago.
     *
     * @return Estado del pago
     */
    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    /**
     * Establece el estado del pago.
     *
     * @param estadoPago Estado a asignar
     */
    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    /**
     * Obtiene el usuario responsable del registro.
     *
     * @return Usuario responsable
     */
    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    /**
     * Establece el usuario responsable del registro.
     *
     * @param usuarioResponsable Usuario a asignar
     */
    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    /**
     * Obtiene las novedades registradas.
     *
     * @return Novedades
     */
    public String getNovedades() {
        return novedades;
    }

    /**
     * Establece las novedades.
     *
     * @param novedades Novedades a asignar
     */
    public void setNovedades(String novedades) {
        this.novedades = novedades;
    }

    /**
     * Representación en texto del registro de parqueo.
     *
     * @return String con la información del registro
     */
    @Override
    public String toString() {
        return "RegistroParqueo{" +
                "id=" + id +
                ", vehiculo=" + vehiculo +
                ", numeroEspacioAsignado=" + numeroEspacioAsignado +
                ", valorPagado=" + valorPagado +
                ", estadoPago=" + estadoPago +
                ", usuarioResponsable=" + usuarioResponsable +
                ", novedades='" + novedades + '\'' +
                '}';
    }
}

