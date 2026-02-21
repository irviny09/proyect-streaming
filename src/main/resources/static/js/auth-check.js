(function() {
    const token = localStorage.getItem('accessToken');
    
    // Si no hay token, al login de inmediato
    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Decodificar el Payload para ver el rol
    const payload = JSON.parse(atob(token.split('.')[1]));
    const userRole = payload.role; 

    const currentPath = window.location.pathname;
    
    if (currentPath.includes('admin.html') && userRole !== 'ROLE_ADMIN') {
        alert("Acceso denegado: Se requieren permisos de Administrador");
        window.location.href = '/dashboard-user';
    }
})();