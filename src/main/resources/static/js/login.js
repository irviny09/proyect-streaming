const apiAuthUrl = 'http://localhost:7890/auth/login';

document.getElementById('loginForm').addEventListener('submit', async (e)=>{
    e.preventDefault();

    const loginData = {
        email : document.getElementById('email').value,
        password: document.getElementById('password').value
    };

    try{
        const reponse = await fetch(apiAuthUrl , {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify(loginData)
        });

        if(reponse.ok){
            const data = await reponse.json();
            const token = data.access_token;
            if (token) {
            document.cookie = "access_token=" + data.access_token + "; path=/; SameSite=Strict";
            
            const payload = JSON.parse(atob(token.split('.')[1]));
            console.log("Payload del token:", payload);

            const userRole = payload.role; 

            if (userRole === 'ROLE_ADMIN' || userRole === 'ADMIN') {
                window.location.href = '/admin';
            } else {
                window.location.href = '/user';
            }
        } else{
            alert('Credenciales incorrectas');
        }}
    }   catch(error){
        console.error("Error en la autenticación:", error);
    }
});