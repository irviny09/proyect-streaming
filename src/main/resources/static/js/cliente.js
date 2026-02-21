const mostrarURL = "http://localhost:7890/api/operacional/mostrar";
const movieBody = document.getElementById("movieBody");

const cargarPeliculas = async () => {
    movieBody.innerHTML = ""; 

    try {
        const response = await fetch(mostrarURL);
        const data = await response.json();
        
        // FILTRO: Solo dejamos pasar los que tengan Pelicula_Activo en true
        const activos = data.filter(movie => movie.Pelicula_Activo === true || movie.Pelicula_Activo === 1);
        
        // Ahora recorremos solo la lista de "activos"
        activos.forEach(movie => {
            movieBody.innerHTML += `
                <tr>
                    <td>
                        <img src="${movie.Imagen_URL}" class="movie-img" style="width: 100px;" alt="${movie.Pelicula_Nombre}">
                    </td>
                    <td class="fw-bold">${movie.Pelicula_Nombre}</td>
                    <td class="text-muted text-start">${movie.Pelicula_Descripcion}</td>
                    <td> 
                        <button class="btn btn-success px-4" 
                                onclick="abrirTrailer('${movie.Pelicula_Trailer}', '${movie.Pelicula_Nombre}')">
                            Ver Película
                        </button>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Error al cargar películas:", error);
    }
};

// Función para manejar el modal y el video
const abrirTrailer = (url, nombre) => {
    const iframe = document.getElementById('trailerVideo');
    document.getElementById('trailerTitle').innerText = `Tráiler: ${nombre}`;

    // Esta lógica es vital para evitar el error de acceso denegado
    let embedUrl = url;
    
    if (url.includes("watch?v=")) {
        // Convierte watch?v=XYZ a /embed/XYZ
        embedUrl = url.replace("watch?v=", "embed/");
    } else if (url.includes("youtu.be/")) {
        // Convierte youtu.be/XYZ a youtube.com/embed/XYZ
        embedUrl = url.replace("youtu.be/", "www.youtube.com/embed/");
    }

    // Si el link ya tiene parámetros extras (como &t=10s), límpialos para el embed
    embedUrl = embedUrl.split("&")[0];

    iframe.src = embedUrl;
    
    const modal = new bootstrap.Modal(document.getElementById('trailerModal'));
    modal.show();
};

document.addEventListener("DOMContentLoaded", cargarPeliculas);