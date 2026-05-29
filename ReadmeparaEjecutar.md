ReadmeparaEjecutar

Este proyecto simula un sistema de gestión de parqueadero en consola.

Requisitos:
- Java JDK 11+ instalado y accesible desde la línea de comandos.

Compilar:

1. Abrir PowerShell y posicionarse en la carpeta del proyecto:
   cd "C:\Users\Sarit\Desktop\Parqueadero trabajo final"

2. Compilar todos los archivos Java en `src` y dejar clases en la carpeta `out`:

```powershell
javac -d out src\*.java
```

Ejecutar:

1. Ejecutar el menú de consola:

```powershell
java -cp out MenuConsole
```

Credenciales semilla:
- Administrador: usuario `admin`, contraseña `admin123` (puede anular operaciones, ver historial, cambiar tarifas)
- Cajero: usuario `cajero`, contraseña `cajero123` (opera ingresos/salidas, no puede cambiar tarifas ni usuarios)

Flujo principal:
- Ingresar con credenciales.
- Registrar ingresos/salidas desde el menú.
- Para administradores, usar el menú administrador para ver historial, generar reporte diario recursivo y anular operaciones.

Notas:
- Los datos se mantienen en memoria durante la ejecución y se inicializan con datos semilla (3 vehículos ya estacionados) para poder probar recursividad.
- Para cambiar capacidades y persistencia, el repositorio debería extenderse a lectura/escritura en archivo o base de datos.

Contacto:
- Este README fue generado automáticamente junto con las clases del sistema.

