const productosPorPagina = 8;
let paginaActual = 1;

document.addEventListener("DOMContentLoaded", () => {
    // 1. Sincronizar contador apenas carga
    if (typeof actualizarContadorCarrito === "function") actualizarContadorCarrito();

    // 2. Eventos de Filtro
    document.getElementById("busqueda").addEventListener("input", aplicarFiltros);
    document.getElementById("soloStock").addEventListener("change", aplicarFiltros);
    document.getElementById("precioRange").addEventListener("input", (e) => {
        document.getElementById("precioActual").textContent = `Hasta S/. ${e.target.value}`;
        aplicarFiltros();
    });

    document.querySelectorAll(".categoriaCheck").forEach(check => {
        check.addEventListener("change", aplicarFiltros);
    });

    // 3. Eventos de botones (Integración Premium)
    document.querySelectorAll(".btn-agregar").forEach(btn => {
        btn.addEventListener("click", agregarAlCarrito);
    });

    document.querySelectorAll(".btn-detalle").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            // Aquí puedes dejar tu lógica de login o redirección
            window.location.href = `/detalle.html?id=${id}`;
        });
    });

    aplicarFiltros();
});

// --- LÓGICA DE AGREGAR AL CARRITO INTEGRADA ---
function agregarAlCarrito(event) {
    const card = event.target.closest(".card");
    const producto = {
        id: card.dataset.id,
        nombre: card.dataset.nombre,
        precio: parseFloat(card.dataset.precio),
        imagen: card.dataset.imagen,
        stock: parseInt(card.dataset.stock),
        cantidad: 1
    };

    // Usamos la misma clave que en carrito.js
    let carrito = JSON.parse(localStorage.getItem("techstore_carrito")) || [];
    const existente = carrito.find(p => p.id === producto.id);

    if (existente) {
        if (existente.cantidad < producto.stock) {
            existente.cantidad++;
        } else {
            if (typeof mostrarNotificacionPremium === "function") {
                mostrarNotificacionPremium("⚠️ No hay más stock disponible.");
            }
            return;
        }
    } else {
        carrito.push(producto);
    }

    localStorage.setItem("techstore_carrito", JSON.stringify(carrito));

    // Actualizar contador visual y lanzar notificación premium
    if (typeof actualizarContadorCarrito === "function") actualizarContadorCarrito();
    if (typeof mostrarNotificacionPremium === "function") {
        mostrarNotificacionPremium(`✅ Se agregó <b>${producto.nombre}</b> al carrito.`);
    }
}

// --- LÓGICA DE FILTROS ---
function aplicarFiltros() {
    const textoBusqueda = document.getElementById("busqueda").value.toLowerCase();
    const soloStock = document.getElementById("soloStock").checked;
    const precioMaximo = parseFloat(document.getElementById("precioRange").value);
    const categoriasSeleccionadas = [...document.querySelectorAll(".categoriaCheck:checked")].map(c => parseInt(c.value));

    const cards = document.querySelectorAll(".card");
    let visibles = 0;

    cards.forEach(card => {
        const nombre = card.dataset.nombre.toLowerCase();
        const precio = parseFloat(card.dataset.precio);
        const categoria = parseInt(card.dataset.categoria);
        const stock = parseInt(card.dataset.stock);

        const coincide = nombre.includes(textoBusqueda) &&
                        precio <= precioMaximo &&
                        (!soloStock || stock > 0) &&
                        (categoriasSeleccionadas.length === 0 || categoriasSeleccionadas.includes(categoria));

        card.dataset.filtrado = coincide ? "true" : "false";
        if (coincide) visibles++;
    });

    document.getElementById("contadorProductos").textContent = `Mostrando ${visibles} productos`;
    paginaActual = 1;
    generarPaginacion();
    mostrarPagina();
}

function mostrarPagina() {
    const cards = [...document.querySelectorAll(".card")];
    const cardsFiltradas = cards.filter(card => card.dataset.filtrado === "true");

    cards.forEach(card => card.style.display = "none");

    const inicio = (paginaActual - 1) * productosPorPagina;
    cardsFiltradas.slice(inicio, inicio + productosPorPagina).forEach(card => card.style.display = "flex");
}

function generarPaginacion() {
    const cardsVisibles = [...document.querySelectorAll(".card")].filter(card => card.dataset.filtrado === "true");
    const totalPaginas = Math.ceil(cardsVisibles.length / productosPorPagina);
    const paginacion = document.getElementById("paginacion");
    paginacion.innerHTML = "";

    if (totalPaginas <= 1) return;

    for (let i = 1; i <= totalPaginas; i++) {
        const boton = document.createElement("button");
        boton.textContent = i;
        if (i === paginaActual) boton.classList.add("pagina-activa");
        boton.addEventListener("click", () => {
            paginaActual = i;
            mostrarPagina();
            document.querySelectorAll("#paginacion button").forEach(b => b.classList.remove("pagina-activa"));
            boton.classList.add("pagina-activa");
        });
        paginacion.appendChild(boton);
    }
}


