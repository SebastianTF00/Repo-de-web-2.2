const STORAGE_KEY = "techstore_carrito";

// --- FUNCIONES CORE ---
function obtenerCarrito() {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
}

function guardarCarrito(carrito) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(carrito));
}

function formatearPrecio(valor) {
    return "S/ " + Number(valor).toFixed(2);
}

function actualizarContadorCarrito() {
    const carrito = obtenerCarrito(); // Usamos la función centralizada
    const totalItems = carrito.reduce((total, item) => total + item.cantidad, 0);
    const badge = document.getElementById('cart-count');
    if (badge) {
        badge.innerText = totalItems;
    }
}

// --- AGREGAR DESDE LA TIENDA ---
function agregarAlCarritoDesdeBoton(boton) {
    // 1. Capturamos todos los datos exactos que necesita el renderizador
    const producto = {
        id: boton.getAttribute('data-id'),
        nombre: boton.getAttribute('data-nombre'),
        descripcion: boton.getAttribute('data-descripcion'),
        precio: parseFloat(boton.getAttribute('data-precio')),
        stock: parseInt(boton.getAttribute('data-stock')) || 10,
        imagenUrl: boton.getAttribute('data-imagen'), // Corregido a imagenUrl
        cantidad: 1
    };

    // 2. Lógica limpia con el LocalStorage unificado
    let carrito = obtenerCarrito();
    const index = carrito.findIndex(item => item.id === producto.id);

    if (index !== -1) {
        // Controlamos que no agregue más del stock disponible
        if (carrito[index].cantidad < producto.stock) {
            carrito[index].cantidad++;
        }
    } else {
        carrito.push(producto);
    }

    guardarCarrito(carrito);

    // 3. Actualizamos el globito rojo del contador
    actualizarContadorCarrito();

    mostrarNotificacionPremium(`Se agregó <b>${producto.nombre}</b> al carrito.`);

    // 4. Efecto Visual Pro
    const textoOriginal = boton.innerHTML;
    const fondoOriginal = boton.style.background || '';

    boton.innerHTML = '<span class="material-symbols-outlined" style="font-size: 1.2rem; vertical-align: middle;">check_circle</span> ¡Agregado!';
    boton.style.background = '#10b981'; // Verde de éxito
    boton.style.color = 'white';
    boton.style.border = 'none';

    setTimeout(() => {
        boton.innerHTML = textoOriginal;
        boton.style.background = fondoOriginal;
    }, 1500);
}

// --- RENDERIZAR LA PÁGINA DEL CARRITO (/carrito) ---
function renderizarCarrito() {
    const contenedor = document.getElementById("cart-items");
    const carritoVacio = document.getElementById("empty-cart");
    const subtotalTexto = document.getElementById("subtotal-carrito");
    const totalTexto = document.getElementById("total-carrito");
    const botonFinalizar = document.getElementById("btn-finalizar");

    if (!contenedor) return; // Si no estamos en la página del carrito, aborta aquí

    const carrito = obtenerCarrito();
    contenedor.innerHTML = "";

    if (carrito.length === 0) {
        if(carritoVacio) carritoVacio.classList.remove("d-none");
        if(subtotalTexto) subtotalTexto.textContent = formatearPrecio(0);
        if(totalTexto) totalTexto.textContent = formatearPrecio(0);
        if(botonFinalizar) botonFinalizar.classList.add("disabled");
        return;
    }

    if(carritoVacio) carritoVacio.classList.add("d-none");
    if(botonFinalizar) botonFinalizar.classList.remove("disabled");

    carrito.forEach(item => {
        const subtotal = item.precio * item.cantidad;

        const productoHTML = `
            <div class="border-bottom p-3">
                <div class="row align-items-center">
                    <div class="col-md-5">
                        <div class="d-flex align-items-center gap-3">
                            <img src="${item.imagenUrl}"
                                 alt="${item.nombre}"
                                 class="cart-img"
                                 style="width: 80px; height: 80px; object-fit: contain; background: #fff; border-radius: 8px; padding: 5px;"
                                 onerror="this.src='/images/default-hardware.png'">

                            <div>
                                <h6 class="fw-bold mb-1 cart-product-title">${item.nombre}</h6>
                                <small class="text-muted" style="color: #94a3b8 !important;">${item.descripcion || "Componente de hardware"}</small>
                                <br>
                                <span class="badge mt-2" style="background: rgba(16, 185, 129, 0.2); color: #10b981; border: 1px solid #10b981;">EN STOCK</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 text-center mt-3 mt-md-0">
                        <div class="btn-group" role="group">
                            <button class="btn btn-outline-secondary btn-sm quantity-btn"
                                    onclick="disminuirCantidad('${item.id}')">
                                <span class="material-symbols-outlined" style="font-size: 1rem;">remove</span>
                            </button>
                            <span class="btn btn-light btn-sm quantity-btn fw-bold" style="background: transparent; color: white; border: 1px solid #334155; display: flex; align-items: center; padding: 0 1rem;">
                                ${item.cantidad}
                            </span>
                            <button class="btn btn-outline-secondary btn-sm quantity-btn"
                                    onclick="aumentarCantidad('${item.id}')">
                                <span class="material-symbols-outlined" style="font-size: 1rem;">add</span>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-2 text-end mt-3 mt-md-0">
                        <span class="fw-semibold">${formatearPrecio(item.precio)}</span>
                    </div>
                    <div class="col-md-2 text-end mt-3 mt-md-0">
                        <span class="fw-bold" style="color: #3a86ff;">${formatearPrecio(subtotal)}</span>
                    </div>
                    <div class="col-md-1 text-center mt-3 mt-md-0">
                        <button class="btn btn-outline-danger btn-sm"
                                onclick="eliminarProducto('${item.id}')"
                                style="border-color: #ef4444; color: #ef4444; background: transparent;">
                            <span class="material-symbols-outlined" style="font-size: 1.2rem;">delete</span>
                        </button>
                    </div>
                </div>
            </div>
        `;
        contenedor.innerHTML += productoHTML;
    });

    const total = calcularTotal(carrito);
    if(subtotalTexto) subtotalTexto.textContent = formatearPrecio(total);
    if(totalTexto) totalTexto.textContent = formatearPrecio(total);
}

// --- CONTROLES DE LA PÁGINA DEL CARRITO ---
function aumentarCantidad(id) {
    let carrito = obtenerCarrito();
    const producto = carrito.find(item => item.id === id);
    if (producto) {
        if (producto.cantidad < producto.stock) {
            producto.cantidad++;
        } else {
            alert("No hay más stock disponible para este producto.");
            return;
        }
    }
    guardarCarrito(carrito);
    renderizarCarrito();
    actualizarContadorCarrito();
}

function disminuirCantidad(id) {
    let carrito = obtenerCarrito();
    const producto = carrito.find(item => item.id === id);
    if (producto) {
        producto.cantidad--;
        if (producto.cantidad <= 0) {
            carrito = carrito.filter(item => item.id !== id);
        }
    }
    guardarCarrito(carrito);
    renderizarCarrito();
    actualizarContadorCarrito();
}

function eliminarProducto(id) {
    let carrito = obtenerCarrito();
    carrito = carrito.filter(item => item.id !== id);
    guardarCarrito(carrito);
    renderizarCarrito();
    actualizarContadorCarrito();
}

function vaciarCarrito() {
    const carrito = obtenerCarrito();
    if (carrito.length === 0) return;

    if (confirm("¿Seguro que deseas vaciar el carrito?")) {
        localStorage.removeItem(STORAGE_KEY);
        renderizarCarrito();
        actualizarContadorCarrito();
    }
}

function calcularTotal(carrito) {
    return carrito.reduce((total, item) => total + item.precio * item.cantidad, 0);
}

// --- SISTEMA DE CHECKOUT Y BOLETA ---
// --- SISTEMA DE CHECKOUT Y BOLETA ---
function finalizarCompra() {
    const carrito = obtenerCarrito();
    if (carrito.length === 0) return;

    // 1. Crear Modal de Confirmación Oscuro
    const overlayConfirm = document.createElement('div');
    overlayConfirm.id = 'techstore-confirm-modal';
    overlayConfirm.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(11, 15, 23, 0.85); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 10000;';

    const totalCompra = formatearPrecio(calcularTotal(carrito));

    overlayConfirm.innerHTML = `
        <div style="background: #1a2333; border: 1px solid #2d3748; border-radius: 12px; padding: 2rem; width: 90%; max-width: 400px; text-align: center; box-shadow: 0 20px 40px rgba(0,0,0,0.6);">
            <span class="material-symbols-outlined" style="font-size: 3rem; color: #3a86ff; margin-bottom: 1rem;">shopping_bag</span>
            <h3 style="color: #fff; margin-bottom: 0.5rem; font-weight: 700;">¿Finalizar Compra?</h3>
            <p style="color: #94a3b8; margin-bottom: 1.5rem; font-size: 0.95rem;">Estás a punto de procesar tu pedido por <b>${totalCompra}</b>.</p>
            <div style="display: flex; gap: 1rem; justify-content: center;">
                <button id="btn-cancel-buy" style="background: transparent; border: 1px solid #475569; color: #cbd5e1; padding: 0.6rem 1.2rem; border-radius: 6px; cursor: pointer; font-weight: bold; width: 50%;">Cancelar</button>
                <button id="btn-confirm-buy" style="background: #10b981; border: none; color: white; padding: 0.6rem 1.2rem; border-radius: 6px; cursor: pointer; font-weight: bold; width: 50%;">Confirmar</button>
            </div>
        </div>
    `;

    document.body.appendChild(overlayConfirm);

    // Lógica de botones del Modal
    document.getElementById('btn-cancel-buy').onclick = () => overlayConfirm.remove();
    document.getElementById('btn-confirm-buy').onclick = () => {
        overlayConfirm.remove();
        generarBoleta(carrito, calcularTotal(carrito));
    };
}

function generarBoleta(carrito, total) {
    // 2. Crear Modal de la Boleta de Venta
    const overlayBoleta = document.createElement('div');
    overlayBoleta.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(11, 15, 23, 0.95); backdrop-filter: blur(8px); display: flex; justify-content: center; align-items: center; z-index: 10000;';

    // Listar los productos comprados
    let itemsHTML = '';
    carrito.forEach(item => {
        itemsHTML += `
            <div style="display: flex; justify-content: space-between; border-bottom: 1px dashed #2d3748; padding: 0.8rem 0; font-size: 0.9rem;">
                <div style="color: #e2e8f0; width: 65%; text-align: left; line-height: 1.4;">
                    <span style="color: #3a86ff; font-weight: bold;">${item.cantidad}x</span> ${item.nombre}
                </div>
                <div style="color: #94a3b8; text-align: right;">${formatearPrecio(item.precio * item.cantidad)}</div>
            </div>
        `;
    });

    const numPedido = Math.floor(Math.random() * 90000) + 10000;
    const fecha = new Date().toLocaleDateString('es-PE', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });

    overlayBoleta.innerHTML = `
        <div style="background: #1a2333; border: 1px solid #2d3748; border-radius: 12px; padding: 0; width: 90%; max-width: 450px; text-align: center; box-shadow: 0 25px 50px rgba(0,0,0,0.7); overflow: hidden;">

            <div style="background: #0b0f17; padding: 1.5rem; border-bottom: 2px dashed #2d3748;">
                <h2 style="color: #fff; margin: 0 0 0.5rem 0; font-weight: 800; font-size: 1.5rem;">TechStore</h2>
                <p style="color: #10b981; font-weight: bold; margin: 0; font-size: 1rem;"><span class="material-symbols-outlined" style="font-size: 1.2rem; vertical-align: middle;">receipt_long</span> Boleta de Venta</p>
            </div>

            <div style="padding: 1.5rem 1.5rem 0.5rem 1.5rem;">
                <div style="display: flex; justify-content: space-between; color: #94a3b8; font-size: 0.85rem; margin-bottom: 1rem; border-bottom: 1px solid #2d3748; padding-bottom: 1rem;">
                    <span style="text-align: left;"><b>Pedido:</b> #${numPedido}<br><b>Fecha:</b> ${fecha}</span>
                    <span style="text-align: right;"><b>Cliente:</b> Invitado<br><b>Pago:</b> Tarjeta de Crédito</span>
                </div>

                <div style="max-height: 220px; overflow-y: auto; margin-bottom: 1rem; padding-right: 5px;">
                    ${itemsHTML}
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; padding: 1rem 0; border-top: 2px solid #2d3748;">
                    <span style="color: #cbd5e1; font-size: 1.1rem; font-weight: bold;">TOTAL PAGADO</span>
                    <span style="color: #10b981; font-size: 1.5rem; font-weight: 900;">${formatearPrecio(total)}</span>
                </div>
            </div>

            <div style="padding: 1.5rem; background: #0b0f17;">
                <button id="btn-close-receipt" style="width: 100%; background: #3a86ff; border: none; color: white; padding: 0.9rem; border-radius: 8px; font-weight: bold; font-size: 1rem; cursor: pointer;">
                    Cerrar y Volver a la Tienda
                </button>
            </div>
        </div>
    `;

    document.body.appendChild(overlayBoleta);

    // Vaciar el carrito lógicamente
    localStorage.removeItem(STORAGE_KEY);
    actualizarContadorCarrito();
    if (typeof renderizarCarrito === "function") renderizarCarrito();

    // Lógica para cerrar y redirigir
    document.getElementById('btn-close-receipt').onclick = () => {
        overlayBoleta.remove();
        window.location.href = "/";
    };
}

// Inicializadores
document.addEventListener('DOMContentLoaded', () => {
    actualizarContadorCarrito();
    renderizarCarrito();
});
// --- SISTEMA DE NOTIFICACIONES PREMIUM ---
function mostrarNotificacionPremium(mensaje) {
    // 1. Si ya hay una notificación en pantalla, la borramos para no amontonarlas
    const viejaNotificacion = document.getElementById('techstore-toast');
    if (viejaNotificacion) viejaNotificacion.remove();

    // 2. Creamos un nuevo div flotante
    const toast = document.createElement('div');
    toast.id = 'techstore-toast';

    // 3. Le damos la estructura HTML y el ícono
    toast.innerHTML = `
        <span class="material-symbols-outlined" style="color: #10b981; font-size: 1.5rem;">check_circle</span>
        <span style="color: #f8fafc; font-size: 0.95rem; font-weight: 500;">${mensaje}</span>
    `;

    // 4. Le inyectamos los estilos oscuros directamente desde JS
    Object.assign(toast.style, {
        position: 'fixed',
        bottom: '30px',
        right: '30px',
        backgroundColor: '#131a26',
        border: '1px solid #1e293b',
        borderLeft: '4px solid #10b981', /* Línea verde de éxito */
        padding: '15px 25px',
        borderRadius: '8px',
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
        boxShadow: '0 10px 25px rgba(0,0,0,0.5)',
        zIndex: '9999',
        opacity: '0',
        transform: 'translateY(20px)',
        transition: 'all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55)' /* Efecto de rebote */
    });

    // 5. Lo metemos a la página
    document.body.appendChild(toast);

    // 6. Animamos su entrada
    setTimeout(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateY(0)';
    }, 10);

    // 7. Lo desaparecemos automáticamente después de 3 segundos
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}