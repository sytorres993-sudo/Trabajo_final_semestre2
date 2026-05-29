/**
 * Clase que representa un Usuario en el sistema de parqueadero.
 * RF-01, RF-16, RF-17: Gestión de usuarios y autenticación
 */
@SuppressWarnings("unused")
public class Usuario {
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private RolUsuario rol;
    private boolean activo;

    /**
     * Constructor vacío del Usuario.
     */
    public Usuario() {
        this.activo = true;
    }

    /**
     * Constructor con parámetros del Usuario.
     *
     * @param id ID único del usuario
     * @param nombreUsuario Nombre de usuario para login
     * @param contrasena Contraseña del usuario
     * @param rol Rol asignado (ADMINISTRADOR o CAJERO_OPERADOR)
     * @param activo Estado del usuario (activo/inactivo)
     */
    public Usuario(int id, String nombreUsuario, String contrasena, RolUsuario rol, boolean activo) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters y Setters con encapsulamiento

    /**
     * Obtiene el ID del usuario.
     *
     * @return ID del usuario
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del usuario.
     *
     * @param id ID a asignar
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param nombreUsuario Nombre de usuario a asignar
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param contrasena Contraseña a asignar
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el rol del usuario.
     *
     * @return Rol asignado
     */
    public RolUsuario getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param rol Rol a asignar
     */
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    /**
     * Verifica si el usuario está activo.
     *
     * @return true si está activo, false si está inactivo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado del usuario.
     *
     * @param activo true para activar, false para desactivar
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Representación en texto del usuario.
     *
     * @return String con la información del usuario
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                '}';
    }
}
