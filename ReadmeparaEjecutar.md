# ReadmeparaEjecutar

Instrucciones para ejecutar la versión web mínima del Sistema de Gestión de Parqueadero.

Requisitos:
- JDK 11+ (javac/java)
- Windows PowerShell (estas instrucciones están escritas para PowerShell)

Pasos rápidos:
1. Abrir PowerShell en la carpeta del proyecto (donde está `src` y `web`).
2. Compilar las clases Java:
   javac -d out "src\\*.java"
3. Ejecutar el servidor web embebido:
   java -cp out WebServer
4. Abrir un navegador y visitar:
   http://localhost:8080

Credenciales semilla:
- Administrador: usuario `admin`, contraseña `admin123`
- Cajero/Operador: usuario `cajero`, contraseña `cajero123`

Qué hace esta versión web:
- Interfaz estática en `web/index.html` con `styles.css` y `app.js`.
- `WebServer` (en `src/WebServer.java`) sirve la UI y expone endpoints mínimos REST:
  - GET /api/activos -> lista vehículos activos
  - POST /api/ingreso -> registrar ingreso (json: {placa, tipo, usuario})
  - POST /api/salida -> registrar salida (json: {placa, usuario})
  - GET /api/tarifas -> obtener tarifas
  - POST /api/tarifa -> actualizar tarifa (solo ADMIN)
  - GET /api/tablero -> tablero de ocupación
  - GET /api/historial -> historial completo
  - GET /api/login?user=...&pass=... -> login

Notas:
- Los datos semilla se cargan automáticamente al iniciar el servidor (1 admin, 1 cajero, 3 vehículos estacionados y tarifas base).
- La implementación de JSON en `WebServer` es intencionalmente simple para evitar dependencias externas.
- Para pruebas avanzadas se puede usar `MenuConsole` o `WebServer`.

Guía rápida de uso en la UI:
1. Abrir la página, iniciar sesión con `admin/admin123` o `cajero/cajero123`.
2. Registrar ingresos o salidas desde los paneles.
3. Los administradores verán paneles adicionales para gestionar tarifas e historial.

Si quieres que convierta la UI a un app más completa (frameworks, persistencia, autenticación) dime y lo hago.
