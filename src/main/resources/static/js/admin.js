const mostrarURL = "http://localhost:7890/api/operacional/mostrar";
const registrarURL = "http://localhost:7890/api/operacional/registrar";
const actualizarURL = "http://localhost:7890/api/operacional/actualizarMovie";
const registrarClienteURL = "http://localhost:7890/auth/register";
const mostrarClientesURL = "http://localhost:7890/api/operacional/mostrarClientes";
const actualizarClienteURL = "http://localhost:7890/api/operacional/actualizarCliente";
const tableAlumnos = document.getElementById("tableMovies");
const tableClientes = document.getElementById("tableClientes");
const btnGuardar = document.getElementById("btnGuardar");
const btnInactivar = document.getElementById("btnInactivar");
const btnRegistrarClientes = document.getElementById("btnRegistrarClientes");

document.addEventListener("DOMContentLoaded", () => {
    const tabButtons = document.querySelectorAll('button[data-bs-toggle="tab"]');
    tabButtons.forEach(button => {
        button.addEventListener('shown.bs.tab', (event) => {
            const targetId = event.target.getAttribute('data-bs-target');
            
            console.log("Pestaña activa:", targetId);
            
            manejarCambioDeTab(targetId);
        });
    });
});

function manejarCambioDeTab(tabId) {
    if (tabId === "#peliculas") {
        tableAlumnos.innerHTML = "";
        cargarPeliculas();
    } else if (tabId === "#clientes") {
        mostrarClientes();
        generarPasswordAleatorio();
    } else if (tabId === "#registrar") {
        registrarPeliculas();
    }
}

const cargarPeliculas = async () => {
    tableAlumnos.innerHTML = "";
    fetch(mostrarURL)
    .then(response => response.json())
    .then(data => {
        data.forEach(movie => {
            const botonEstado = movie.Pelicula_Activo 
            ? `<button onclick="actualizarPelicula('${movie.PeliculaId}', 'inactivar')" class="btn btn-sm btn-danger">Inactivar</button>`
            : `<button onclick="actualizarPelicula('${movie.PeliculaId}', 'activar')" class="btn btn-sm btn-success">Activar</button>`;
            tableAlumnos.innerHTML += `
                <tr>
                    <td><img src="${movie.Imagen_URL}" alt="${movie.Pelicula_Nombre}" class="img-thumbnail" style="width: 100px;"></td>
                    <td>${movie.Pelicula_Nombre}</td>
                    <td>${movie.Genero_Genero}</td>
                    <td>${movie.Pelicula_Descripcion}</td>
                    <td>
                        ${botonEstado}
                    </td>
                </tr>
            `;
        })
    });
};

async function registrarPeliculas(event) {
    event.preventDefault();
    let formData = new FormData();
    formData.append("nombre", document.getElementById("nombre").value);
    formData.append("descripcion", document.getElementById("descripcion").value);
    formData.append("activo", true);
    formData.append("generoId", document.getElementById("genero").value);

    const inputImagen = document.getElementById("url"); 
    if (inputImagen.files.length > 0) {
        formData.append("archivo", inputImagen.files[0]);
    }

    const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('access_token='))
        ?.split('=')[1];

    try {
        const response = await fetch(registrarURL, {
            method: "POST",
            mode: "cors",
            headers: {
                "Authorization": `Bearer ${token}` 
            },
            body: formData
        });

        if (response.ok) {
            alert("Película e imagen registradas exitosamente");
        } else {
            const errorMsg = await response.text();
            alert("Error al registrar: " + errorMsg);
        }
    } catch (error) {
        console.error("Error al registrar película:", error);
        alert("Hubo un error al registrar la película.");
    }
}

async function actualizarPelicula(id, accion) {
    const estadoActivo = (accion === "activar");
    
    const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('access_token='))
        ?.split('=')[1];

    let peliculaActualizar = {
        peliculaId : id,
        activo : estadoActivo
    };

    const response = await fetch(actualizarURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(peliculaActualizar)
    });
    
    if(response.ok){
        alert("Estado Actualizado");
        cargarPeliculas();
    }
}

async function actualizarCliente(id, accion) {
    const estadoActivo = (accion === "activar");
    
    const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('access_token='))
        ?.split('=')[1];

    let clienteActualizar = {
        clienteId : id,
        activo : estadoActivo
    };

    const response = await fetch(actualizarClienteURL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(clienteActualizar)
    });
    
    if(response.ok){
        alert("Estado Actualizado");
        mostrarClientes();
    }
}

async function registrarClientes(event) {
    event.preventDefault();
    const registroData = {
        name: document.getElementById("nombre").value,
        apellidoPat: document.getElementById("apellidoP").value,
        apellidoMat: document.getElementById("apellidoM").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        role: "ROLE_USER"
    }

    const token = document.cookie
        .split('; ')
        .find(row => row.startsWith('access_token='))
        ?.split('=')[1];

    try {
        const response = await fetch(registrarClienteURL, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                "Authorization": `Bearer ${token}` 
            },
            body: JSON.stringify(registroData)
        });

        if (response.ok) {
            alert("Cliente registrado exitosamente");
        } else {
            const errorMsg = await response.text();
            alert("Error al registrar: " + errorMsg);
        }
    } catch (error) {
        console.error("Error al registrar cliente:", error);
        alert("Hubo un error al registrar la cliente.");
    }
}

const mostrarClientes = async () => {
    const headerTable = `<thead>
                        <tr>
                        <th>Nombre completo</th>
                        <th>Correo / Usuario</th>
                        <th>Fecha de Registro</th>
                        <th>Acciones</th>
                        </tr>
                        </thead>
                        `;

    tableClientes.innerHTML = headerTable;
    const token = document.cookie
    .split('; ')
    .find(row => row.startsWith('access_token='))
    ?.split('=')[1];
    try{
        const response = await fetch(mostrarClientesURL, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });
        if (response.ok) {
            const data = await response.json();
            let filas = "<tbody>";
            data.forEach(cliente => {
                const botonAcciones = cliente.Usuario_Activo 
                        ? `<button onclick="actualizarCliente('${cliente.UsuarioId}', 'eliminar')" class="btn btn-sm btn-danger">Eliminar</button>`
                        : `<button onclick="actualizarCliente('${cliente.UsuarioId}', 'activar')" class="btn btn-sm btn-primary">Activar</button>`;
                filas += `
                    <tr>
                        <td>${cliente.Usuario}</td>
                        <td>${cliente.Usuario_Email}</td>
                        <td>${cliente.Usuario_FechaRegistro}</td>
                        <td>${botonAcciones}</td>
                    </tr>
                `;
            });
            filas += "</tbody>";
            tableClientes.innerHTML = headerTable + filas;
        } else {
            console.error("Error al obtener clientes:", response.status);
        }
    } catch (error) {
        console.error("Error de conexión:", error);
    }
};

function generarPasswordAleatorio(){
    const caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let password = "";
    for (let i = 0; i < 8; i++) {
        password += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
    }

    const inputPass = document.getElementById("password");
    if(inputPass) {
        inputPass.value = password;
    }
}

btnGuardar.addEventListener("click", registrarPeliculas);
btnRegistrarClientes.addEventListener("click", registrarClientes);