// app.js - Funciones compartidas simples (opcional)

const API_URL = 'http://localhost:8083/api/v1';

// Función para mostrar mensajes simples
function mostrarMensaje(mensaje) {
    alert(mensaje);
}

// Función para confirmar acciones
function confirmar(mensaje) {
    return confirm(mensaje);
}

// Función para formatear fechas
function formatearFecha(fecha) {
    if (!fecha) return '-';
    return new Date(fecha).toLocaleDateString('es-ES');
}

// Función genérica para hacer peticiones
async function hacerPeticion(url, opciones = {}) {
    try {
        const response = await fetch(url, opciones);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error en petición:', error);
        throw error;
    }
}

// Validar RUC básico
function validarRUC(ruc) {
    if (!ruc) return true; // Opcional
    return /^\d{11}$/.test(ruc);
}

// Validar score
function validarScore(score) {
    if (!score) return true; // Opcional
    const num = parseInt(score);
    return num >= 0 && num <= 100;
}