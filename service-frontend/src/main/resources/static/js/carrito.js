const STORAGE_KEY = "techstore_carrito";

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
    const carrito = obtenerCarrito();
    const totalItems = carrito.reduce((total, item) => {
        return total + item.cantidad;
    }, 0);
    const contador = document.getElementById("cart-count");
    if (contador) {
        contador.textContent = totalItems;
    }
}

function agregarAlCarritoDesdeBoton(boton) {
    const producto = {
        id: boton.dataset.id,
        nombre: boton.dataset.nombre,
        descripcion: boton.dataset.descripcion,
        precio: Number(boton.dataset.precio),
        imagenUrl: boton.dataset.imagen,
        stock: Number(boton.dataset.stock),
        cantidad: 1
    };

    let carrito = obtenerCarrito();
    const productoExistente = carrito.find(item => item.id === producto.id);

    if (productoExistente) {
        if (productoExistente.cantidad < producto.stock) {
            productoExistente.cantidad++;
        } else {
            alert("No hay más stock disponible para este producto.");
            return;
        }
    } else {
        carrito.push(producto);
    }

    guardarCarrito(carrito);
    actualizarContadorCarrito();
    alert("✅ " + producto.nombre + " añadido correctamente al carrito.");
}

function renderizarCarrito() {
    const contenedor = document.getElementById("cart-items");
    const carritoVacio = document.getElementById("empty-cart");
    const subtotalTexto = document.getElementById("subtotal-carrito");
    const totalTexto = document.getElementById("total-carrito");
    const botonFinalizar = document.getElementById("btn-finalizar");

    if (!contenedor) {
        return;
    }

    const carrito = obtenerCarrito();
    contenedor.innerHTML = "";

    if (carrito.length === 0) {
        carritoVacio.classList.remove("d-none");
        subtotalTexto.textContent = formatearPrecio(0);
        totalTexto.textContent = formatearPrecio(0);
        botonFinalizar.classList.add("disabled");
        return;
    }

    carritoVacio.classList.add("d-none");
    botonFinalizar.classList.remove("disabled");
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
                                 onerror="this.src='https://via.placeholder.com/100x100?text=TechStore'">

                            <div>
                                <h6 class="fw-bold mb-1 cart-product-title">${item.nombre}</h6>
                                <small class="text-muted">${item.descripcion || "Producto tecnológico"}</small>
                                <br>
                                <span class="badge bg-success mt-2">EN STOCK</span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 text-center mt-3 mt-md-0">
                        <div class="btn-group" role="group">
                            <button class="btn btn-outline-secondary btn-sm quantity-btn"
                                    onclick="disminuirCantidad('${item.id}')">
                                <i class="bi bi-dash"></i>
                            </button>
                            <span class="btn btn-light btn-sm quantity-btn">
                                ${item.cantidad}
                            </span>
                            <button class="btn btn-outline-secondary btn-sm quantity-btn"
                                    onclick="aumentarCantidad('${item.id}')">
                                <i class="bi bi-plus"></i>
                            </button>
                        </div>
                    </div>
                    <div class="col-md-2 text-end mt-3 mt-md-0">
                        <span class="fw-semibold">${formatearPrecio(item.precio)}</span>
                    </div>
                    <div class="col-md-2 text-end mt-3 mt-md-0">
                        <span class="fw-bold text-success">${formatearPrecio(subtotal)}</span>
                    </div>
                    <div class="col-md-1 text-center mt-3 mt-md-0">
                        <button class="btn btn-outline-danger btn-sm"
                                onclick="eliminarProducto('${item.id}')">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
        contenedor.innerHTML += productoHTML;
    });

    const total = calcularTotal(carrito);
    subtotalTexto.textContent = formatearPrecio(total);
    totalTexto.textContent = formatearPrecio(total);
}

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

    if (carrito.length === 0) {
        alert("El carrito ya está vacío.");
        return;
    }

    const confirmar = confirm("¿Seguro que deseas vaciar el carrito?");

    if (!confirmar) {
        return;
    }

    localStorage.removeItem(STORAGE_KEY);
    renderizarCarrito();
    actualizarContadorCarrito();
}

function calcularTotal(carrito) {
    return carrito.reduce((total, item) => {
        return total + item.precio * item.cantidad;
    }, 0);
}

function finalizarCompra() {
    const carrito = obtenerCarrito();

    if (carrito.length === 0) {
        alert("Tu carrito está vacío.");
        return;
    }

    const confirmar = confirm("¿Deseas finalizar la compra?");

    if (!confirmar) {
        return;
    }

    alert("✅ Compra finalizada correctamente. Gracias por comprar en TechStore.");

    localStorage.removeItem(STORAGE_KEY);
    renderizarCarrito();
    actualizarContadorCarrito();
}

document.addEventListener("DOMContentLoaded", function () {
    actualizarContadorCarrito();
    renderizarCarrito();
});