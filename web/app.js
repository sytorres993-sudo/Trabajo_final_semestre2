let currentUser = null;

function qs(id){return document.getElementById(id)}

qs('btnLogin').addEventListener('click', async ()=>{
  const user = qs('username').value;
  const pass = qs('password').value;
  const res = await fetch(`/api/login?user=${encodeURIComponent(user)}&pass=${encodeURIComponent(pass)}`);
  const data = await res.json();
  if (data.success){
    currentUser = { nombreUsuario: data.user.nombreUsuario, rol: data.user.rol };
    qs('userInfo').textContent = `Conectado: ${currentUser.nombreUsuario} (${currentUser.rol})`;
    qs('acciones').classList.remove('hidden');
    qs('btnLogout').classList.remove('hidden');
    qs('btnLogin').classList.add('hidden');
    // Añadir clase según el rol para mover la imagen
    if (currentUser.rol === 'ADMINISTRADOR') {
      document.body.classList.add('admin-logged');
      document.querySelectorAll('.admin').forEach(e=>e.classList.remove('hidden'));
    } else if (currentUser.rol === 'CAJERO_OPERADOR') {
      document.body.classList.add('cajero-logged');
    }
    cargarTarifas();
    cargarUsuarios();
  } else {
    alert('Login fallido: ' + data.message);
  }
});

qs('btnLogout').addEventListener('click', ()=>{
  currentUser = null;
  qs('userInfo').textContent = '';
  qs('acciones').classList.add('hidden');
  qs('btnLogout').classList.add('hidden');
  qs('btnLogin').classList.remove('hidden');
  // Remover ambas clases al cerrar sesión
  document.body.classList.remove('admin-logged');
  document.body.classList.remove('cajero-logged');
  document.querySelectorAll('.admin').forEach(e=>e.classList.add('hidden'));
});

qs('btnIngreso').addEventListener('click', async ()=>{
  const placa = qs('ingresaPlaca').value;
  const tipo = qs('ingresaTipo').value;
  const res = await fetch('/api/ingreso',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({placa,tipo,usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  qs('resIngreso').textContent = data.message;
});

qs('btnSalida').addEventListener('click', async ()=>{
  const placa = qs('salidaPlaca').value;
  const res = await fetch('/api/salida',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({placa,usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  qs('resSalida').textContent = data.message + (data.monto?('\nMonto: '+data.monto):'');
});

qs('btnActivos').addEventListener('click', async ()=>{
  const res = await fetch('/api/activos');
  const data = await res.json();
  qs('activosList').textContent = JSON.stringify(data, null, 2);
});

qs('btnTablero').addEventListener('click', async ()=>{
  const res = await fetch('/api/tablero');
  const data = await res.json();
  qs('tablero').textContent = JSON.stringify(data, null, 2);
});

qs('btnTarifa').addEventListener('click', async ()=>{
  const tipo = qs('tarTipo').value;
  const valor = qs('tarValor').value;
  const res = await fetch('/api/tarifa',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({tipo,valor,usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  qs('resTarifa').textContent = data.message;
  cargarTarifas();
});

qs('btnHistorial')?.addEventListener('click', async ()=>{
  const res = await fetch('/api/historial');
  const data = await res.json();
  qs('historialList').textContent = JSON.stringify(data, null, 2);
});

qs('btnBuscar').addEventListener('click', async ()=>{
  const placa = qs('buscarPlaca').value;
  const res = await fetch(`/api/buscar?placa=${encodeURIComponent(placa)}`);
  const data = await res.json();
  qs('buscarRes').textContent = JSON.stringify(data, null, 2);
});

qs('btnReporte')?.addEventListener('click', async ()=>{
  const res = await fetch('/api/reporte');
  const data = await res.json();
  qs('reporteRes').textContent = JSON.stringify(data, null, 2);
});

qs('btnAnular')?.addEventListener('click', async ()=>{
  const id = qs('anularId').value;
  const just = qs('anularJust').value;
  const res = await fetch('/api/anular',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({id,justificacion:just,usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  qs('resAnular').textContent = data.message;
});

qs('btnCrearUsuario')?.addEventListener('click', async ()=>{
  const nombre = qs('uNombre').value;
  const pass = qs('uPass').value;
  const rol = qs('uRol').value;
  const res = await fetch('/api/usuarios',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({nombre,pass,rol,usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  cargarUsuarios();
  alert(data.message);
});

qs('btnEliminarUsuario')?.addEventListener('click', async ()=>{
  const nombre = qs('uEliminar').value;
  const res = await fetch('/api/usuarios?eliminar='+encodeURIComponent(nombre)+'&usuario='+encodeURIComponent(currentUser?currentUser.nombreUsuario:''));
  const data = await res.json();
  cargarUsuarios();
  alert(data.message);
});

qs('btnConfigCap')?.addEventListener('click', async ()=>{
  const car = parseInt(qs('capCarros').value||0);
  const mot = parseInt(qs('capMotos').value||0);
  const bic = parseInt(qs('capBicis').value||0);
  const res = await fetch('/api/configcap',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({car, mot, bic, usuario:currentUser?currentUser.nombreUsuario:'anon'})});
  const data = await res.json();
  qs('resConfig').textContent = data.message;
});

async function cargarTarifas(){
  const res = await fetch('/api/tarifas');
  const data = await res.json();
  qs('tarifasList').textContent = JSON.stringify(data, null, 2);
}

async function cargarUsuarios(){
  const res = await fetch('/api/usuarios');
  const data = await res.json();
  qs('usuariosList').textContent = JSON.stringify(data, null, 2);
}
