// Mock local de la API para que la UI funcione sin servidor
const _originalFetch = window.fetch.bind(window);

// Usuarios de ejemplo (contraseñas en claro solo para pruebas locales)
const mockUsers = [
  { nombreUsuario: 'admin', password: 'admin', rol: 'ADMINISTRADOR' },
  { nombreUsuario: 'cajero', password: 'cajero', rol: 'CAJERO_OPERADOR' }
];

// Estado simulado del sistema
const state = {
  tarifas: [ { tipo: 'CARRO', valor: 500 }, { tipo: 'MOTO', valor: 300 }, { tipo: 'BICICLETA', valor: 100 } ],
  usuarios: mockUsers.map(u => ({ nombreUsuario: u.nombreUsuario, rol: u.rol })),
  activos: [],
  historial: [],
  capacidad: { car: 50, mot: 50, bic: 50 },
  nextId: 1
};

function makeResponse(obj){
  return { ok:true, json: async ()=> JSON.parse(JSON.stringify(obj)) };
}

function parseQuery(qs){
  const obj = {};
  if (!qs) return obj;
  qs.replace(/^\?/, '').split('&').forEach(pair=>{
    const [k,v] = pair.split('='); if (!k) return; obj[decodeURIComponent(k)] = decodeURIComponent(v||'');
  });
  return obj;
}

window.fetch = async function(url, options){
  // Solo interceptamos rutas que empiezan por /api/
  const u = typeof url === 'string' ? url : (url && url.url) || '';
  if (!u.startsWith('/api/')) return _originalFetch(url, options);

  // Simular pequeña latencia
  await new Promise(r=>setTimeout(r, 150));

  // Login
  if (u.startsWith('/api/login')){
    const q = u.split('?')[1] || '';
    const params = parseQuery(q);
    const user = params.user || '';
    const pass = params.pass || '';
    const found = mockUsers.find(x=>x.nombreUsuario === user && x.password === pass);
    if (found) return makeResponse({ success:true, user: { nombreUsuario: found.nombreUsuario, rol: found.rol } });
    return makeResponse({ success:false, message: 'Usuario o contraseña incorrectos' });
  }

  // Obtener tarifas
  if (u === '/api/tarifas'){
    return makeResponse(state.tarifas);
  }

  // Obtener usuarios
  if (u.startsWith('/api/usuarios')){
    if (options && options.method === 'POST'){
      try{
        const body = JSON.parse(options.body || '{}');
        if (!body.nombre || !body.pass || !body.rol) return makeResponse({ success:false, message: 'Datos incompletos' });
        const exists = state.usuarios.find(x=>x.nombreUsuario === body.nombre);
        if (exists) return makeResponse({ success:false, message: 'Usuario ya existe' });
        state.usuarios.push({ nombreUsuario: body.nombre, rol: body.rol });
        mockUsers.push({ nombreUsuario: body.nombre, password: body.pass, rol: body.rol });
        return makeResponse({ success:true, message: 'Usuario creado' });
      }catch(e){ return makeResponse({ success:false, message: 'Error procesando petición' }); }
    }
    // GET - posibilidad de eliminar por query
    const q = u.split('?')[1] || '';
    const params = parseQuery(q);
    if (params.eliminar){
      const idx = state.usuarios.findIndex(x=>x.nombreUsuario === params.eliminar);
      if (idx!==-1){ state.usuarios.splice(idx,1); const muIdx = mockUsers.findIndex(x=>x.nombreUsuario===params.eliminar); if(muIdx!==-1) mockUsers.splice(muIdx,1); return makeResponse({ success:true, message: 'Usuario eliminado' }); }
      return makeResponse({ success:false, message: 'Usuario no encontrado' });
    }
    return makeResponse(state.usuarios);
  }

  // Registrar ingreso
  if (u === '/api/ingreso' && options && options.method === 'POST'){
    const body = JSON.parse(options.body || '{}');
    const placa = body.placa || '';
    const tipo = body.tipo || 'CARRO';
    if (!placa) return makeResponse({ success:false, message: 'Placa requerida' });
    // verificar si ya está activo
    if (state.activos.find(a=>a.placa === placa)) return makeResponse({ success:false, message: 'Vehículo ya activo' });
    const id = state.nextId++;
    const entry = { id, placa, tipo, ingreso: new Date().toISOString(), usuario: body.usuario||'anon' };
    state.activos.push(entry);
    state.historial.push({ ...entry, salida: null, monto: null, anulada:false });
    return makeResponse({ success:true, message: 'Ingreso registrado', id });
  }

  // Registrar salida
  if (u === '/api/salida' && options && options.method === 'POST'){
    const body = JSON.parse(options.body || '{}');
    const placa = body.placa || '';
    const idx = state.activos.findIndex(a=>a.placa === placa);
    if (idx === -1) return makeResponse({ success:false, message: 'Vehículo no encontrado en activos' });
    const rec = state.activos.splice(idx,1)[0];
    const salida = new Date().toISOString();
    // calcular monto simple: tarifa por tipo * 1 (simulación)
    const tar = state.tarifas.find(t=>t.tipo === rec.tipo) || { valor: 0 };
    const monto = tar.valor;
    // actualizar historial
    const hist = state.historial.find(h=>h.id === rec.id);
    if (hist){ hist.salida = salida; hist.monto = monto; }
    return makeResponse({ success:true, message: 'Salida registrada', monto });
  }

  // Activos
  if (u === '/api/activos'){
    return makeResponse(state.activos);
  }

  // Tablero
  if (u === '/api/tablero'){
    const ocup = { CARRO: state.activos.filter(a=>a.tipo==='CARRO').length, MOTO: state.activos.filter(a=>a.tipo==='MOTO').length, BICICLETA: state.activos.filter(a=>a.tipo==='BICICLETA').length };
    return makeResponse({ capacidad: state.capacidad, ocupacion: ocup });
  }

  // Actualizar tarifa
  if (u === '/api/tarifa' && options && options.method === 'POST'){
    const body = JSON.parse(options.body || '{}');
    const tipo = body.tipo || '';
    const valor = parseFloat(body.valor) || 0;
    const t = state.tarifas.find(x=>x.tipo === tipo);
    if (t) { t.valor = valor; } else { state.tarifas.push({ tipo, valor }); }
    return makeResponse({ success:true, message: 'Tarifa actualizada' });
  }

  // Historial
  if (u === '/api/historial'){
    return makeResponse(state.historial);
  }

  // Reporte diario (resumen simple)
  if (u === '/api/reporte'){
    const total = state.historial.reduce((s,h)=> s + (h.monto||0), 0);
    return makeResponse({ total, registrados: state.historial.length });
  }

  // Anular
  if (u === '/api/anular' && options && options.method === 'POST'){
    const body = JSON.parse(options.body || '{}');
    const id = parseInt(body.id);
    const rec = state.historial.find(h=>h.id === id);
    if (!rec) return makeResponse({ success:false, message: 'Registro no encontrado' });
    rec.anulada = true; rec.justificacion = body.justificacion || '';
    return makeResponse({ success:true, message: 'Operación anulada' });
  }

  // Config capacidad
  if (u === '/api/configcap' && options && options.method === 'POST'){
    const body = JSON.parse(options.body || '{}');
    state.capacidad.car = body.car || state.capacidad.car;
    state.capacidad.mot = body.mot || state.capacidad.mot;
    state.capacidad.bic = body.bic || state.capacidad.bic;
    return makeResponse({ success:true, message: 'Capacidad actualizada' });
  }

  // Buscar
  if (u.startsWith('/api/buscar')){
    const q = u.split('?')[1] || '';
    const params = parseQuery(q);
    const placa = (params.placa||'').toUpperCase();
    const found = state.historial.filter(h=>h.placa && h.placa.toUpperCase().includes(placa));
    return makeResponse(found);
  }

  // Fallback: devolver vacío
  return makeResponse({});
};

// ---------------------------------------------------------------------------------

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
  qs('resSalida').textContent = data.message + (data.monto?("\nMonto: "+data.monto):'');
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
