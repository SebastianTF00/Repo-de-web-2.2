const productosPorPagina = 8;
let cardsFiltradas = [];
let paginaActual = 1;

document.addEventListener("DOMContentLoaded", () => {

    actualizarContadorCarrito();

    document
        .getElementById("busqueda")
        .addEventListener("input", aplicarFiltros);

    document
        .getElementById("soloStock")
        .addEventListener("change", aplicarFiltros);

    document
        .getElementById("precioRange")
        .addEventListener("input", () => {

            document.getElementById("precioActual")
                .textContent =
                `Hasta S/. ${document.getElementById("precioRange").value}`;

            aplicarFiltros();
        });

    document
        .querySelectorAll(".categoriaCheck")
        .forEach(check => {
            check.addEventListener(
                "change",
                aplicarFiltros
            );
        });

    document
        .querySelectorAll(".btn-agregar")
        .forEach(btn => {
            btn.addEventListener(
                "click",
                agregarAlCarrito
            );
        });
        document
            .querySelectorAll(".btn-detalle")
            .forEach(btn => {
                btn.addEventListener("click", () => {

                    const logeado =
                        document.body.dataset.logeado === "true";

                    const id =
                        btn.dataset.id;

                    if (!logeado) {

                        window.location.href = "/login";

                    } else {

                        window.location.href =
                            `/detalle.html?id=${id}`;

                    }

                });
            });

    aplicarFiltros();
});


function aplicarFiltros() {

    const textoBusqueda =
        document
            .getElementById("busqueda")
            .value
            .toLowerCase();

    const soloStock =
        document
            .getElementById("soloStock")
            .checked;

    const precioMaximo =
        parseFloat(
            document
                .getElementById("precioRange")
                .value
        );

    const categoriasSeleccionadas =
        [...document.querySelectorAll(".categoriaCheck:checked")]
            .map(c => parseInt(c.value));

    const cards =
        document.querySelectorAll(".card");

    let visibles = 0;

    cards.forEach(card => {

        const nombre =
            card.dataset.nombre.toLowerCase();

        const precio =
            parseFloat(card.dataset.precio);

        const categoria =
            parseInt(card.dataset.categoria);

        const stock =
            parseInt(card.dataset.stock);

        const coincideNombre =
            nombre.includes(textoBusqueda);

        const coincidePrecio =
            precio <= precioMaximo;

        const coincideStock =
            !soloStock || stock > 0;

        const coincideCategoria =
            categoriasSeleccionadas.length === 0
            ||
            categoriasSeleccionadas.includes(categoria);

        const mostrar =
            coincideNombre
            &&
            coincidePrecio
            &&
            coincideStock
            &&
            coincideCategoria;
            card.dataset.filtrado =
            mostrar ? "true" : "false";

        if (mostrar) {
            visibles++;
        }
    });

    document.getElementById("contadorProductos")
        .textContent =
        `Mostrando ${visibles} productos`;

    paginaActual = 1;

    generarPaginacion();
    mostrarPagina();
}


function mostrarPagina() {

    const cards =
        [...document.querySelectorAll(".card")];

    const cardsFiltradas =
        cards.filter(card =>
            card.dataset.filtrado === "true"
        );

    cards.forEach(card => {
        card.style.display = "none";
    });

    const inicio =
        (paginaActual - 1) * productosPorPagina;

    const fin =
        inicio + productosPorPagina;

    cardsFiltradas
        .slice(inicio, fin)
        .forEach(card => {
            card.style.display = "flex";
        });
}


function generarPaginacion() {

const cardsVisibles =
    [...document.querySelectorAll(".card")]
        .filter(card =>
            card.dataset.filtrado === "true"
        );

    const totalPaginas =
        Math.ceil(
            cardsVisibles.length /
            productosPorPagina
        );

    const paginacion =
        document.getElementById("paginacion");

    paginacion.innerHTML = "";

    if (totalPaginas <= 1) {
        return;
    }

    for (let i = 1; i <= totalPaginas; i++) {

        const boton =
            document.createElement("button");

        boton.textContent = i;

        if (i === paginaActual) {
            boton.classList.add("pagina-activa");
        }

        boton.addEventListener("click", () => {

            paginaActual = i;

            mostrarPagina();

            document
                .querySelectorAll("#paginacion button")
                .forEach(b => b.classList.remove("pagina-activa"));

            boton.classList.add("pagina-activa");
        });

        paginacion.appendChild(boton);
    }
}

/*-------------------------------------carrito-*----------------------------------------------*/
function agregarAlCarrito(event) {
    const logeado =
        document.body.dataset.logeado === "true";

    if (!logeado) {
        window.location.href = "/login";
        return;
    }

    const card =
        event.target.closest(".card");

    const producto = {

        id: card.dataset.id,
        nombre: card.dataset.nombre,
        precio: card.dataset.precio,
        imagen: card.dataset.imagen,
        cantidad: 1

    };

    let carrito =
        JSON.parse(
            localStorage.getItem("carrito")
        ) || [];

    const existente =
        carrito.find(
            p => p.id === producto.id
        );

    if (existente) {

        existente.cantidad++;

    } else {

        carrito.push(producto);
    }

    localStorage.setItem(
        "carrito",
        JSON.stringify(carrito)
    );

    actualizarContadorCarrito();


}


function actualizarContadorCarrito() {

    const carrito =
        JSON.parse(
            localStorage.getItem("carrito")
        ) || [];

    let total = 0;

    carrito.forEach(item => {
        total += item.cantidad;
    });

    document.getElementById(
        "contadorCarrito"
    ).textContent = total;
}


