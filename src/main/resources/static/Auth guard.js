// ============================================
// auth-guard.js - Protección de rutas por rol
// Incluir en admin.html, farmaceutico.html, delivery.html
// ============================================

const API = 'http://localhost:8081/api';

// Verificar que el usuario tiene sesión y el rol correcto
function verificarAcceso(rolRequerido) {
    const token  = localStorage.getItem('token');
    const rol    = localStorage.getItem('rol');
    const nombre = localStorage.getItem('nombre');

    if (!token || !rol) {
        // No hay sesión, redirigir al login
        window.location.href = '/login.html';
        return false;
    }

    if (rolRequerido && rol !== rolRequerido) {
        // Rol incorrecto, redirigir según su rol real
        alert(`Acceso denegado. Tu rol es ${rol}.`);
        redirigirSegunRol(rol);
        return false;
    }

    return true;
}

// Obtener headers con el token para fetch
function getHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// Cerrar sesión
function cerrarSesion() {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('nombre');
    localStorage.removeItem('email');
    window.location.href = '/login.html';
}

// Mostrar nombre del usuario en la interfaz
function mostrarUsuario() {
    const nombre = localStorage.getItem('nombre') || 'Usuario';
    const rol    = localStorage.getItem('rol') || '';
    const el = document.getElementById('usuarioNombre');
    if (el) el.textContent = `${nombre} (${rol})`;
}

function redirigirSegunRol(rol) {
    switch (rol) {
        case 'ADMIN':        window.location.href = '/admin.html'; break;
        case 'FARMACEUTICO': window.location.href = '/farmaceutico.html'; break;
        case 'DELIVERY':     window.location.href = '/delivery.html'; break;
        default:             window.location.href = '/index.html'; break;
    }
}