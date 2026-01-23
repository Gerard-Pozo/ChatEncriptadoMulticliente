document.addEventListener('DOMContentLoaded', function () {
    // Usar window.location para obtener la URL dinámica
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/ChatEncriptadoUDP/chat`;
    
    console.log("Conectando a WebSocket:", wsUrl);
    const ws = new WebSocket(wsUrl);

    let nomUsuari;
    let estaConectado = false;

    // Manejar conexión WebSocket
    ws.onopen = function() {
        console.log("WebSocket conectado");
        estaConectado = true;
    };

    ws.onclose = function() {
        console.log("WebSocket desconectado");
        estaConectado = false;
    };

    ws.onerror = function(error) {
        console.error("Error en WebSocket:", error);
    };

    // Afegeix missatges al DOM
    function afegirMissatge(remitent, missatge) {
        const contenidor = document.getElementById('chat-general');
        const nouContenidor = document.createElement('div');

        // Diferencia entre missatges enviats per mi o per un altre
        if (remitent === nomUsuari) {
            nouContenidor.classList.add('message', 'sent');
            nouContenidor.innerHTML = `<strong>Tú:</strong> ${missatge}`;
        } else if (remitent === 'SERVIDOR') {
            nouContenidor.classList.add('message', 'received');
            nouContenidor.innerHTML = `<em>${missatge}</em>`;
            nouContenidor.style.backgroundColor = '#2e7d32';
        } else {
            nouContenidor.classList.add('message', 'received');
            nouContenidor.innerHTML = `<strong>${remitent}:</strong> ${missatge}`;
        }

        contenidor.appendChild(nouContenidor);
        // Scroll automático al final
        contenidor.scrollTop = contenidor.scrollHeight;
    }

    function actualitzarLlistat(parts) {
        // Si necesitas mostrar la lista de usuarios conectados
        console.log("Usuarios conectados:", parts);
    }

    // Missatges rebuts desde el servidor
    ws.onmessage = function (event) {
        console.log("Mensaje recibido del servidor:", event.data);
        
        if (!event.data) return;

        if (event.data.includes("!#ActualitzarLlistat")) {
            const parts = event.data.split('_');
            parts.shift();
            actualitzarLlistat(parts);
            return;
        }

        // Separem el format en el que ve el missatge
        const parts = event.data.split('_');
        if (parts.length >= 2) {
            const remitent = parts[0];
            const missatge = parts.slice(1).join('_');
            afegirMissatge(remitent, missatge);
        } else {
            afegirMissatge('SERVIDOR', event.data);
        }
    };

    // Envia els missatges
    document.getElementById('btn-enviar').addEventListener('click', () => {
        if (!estaConectado) {
            alert("No estás conectado al chat");
            return;
        }
        
        const input = document.getElementById('missatge');
        const missatge = input.value.trim();
        if (missatge.length === 0) return;

        // Envia el missatge al servidor
        ws.send(missatge);

        // Mostra el missatge al dom (será reenviado por el servidor)
        input.value = '';
        input.focus();
    });

    // Estableix el nom del client
    document.getElementById('btn-entrar').addEventListener('click', () => {
        const input = document.getElementById('nom-usuari');
        nomUsuari = input.value.trim();
        if (nomUsuari.length === 0) return;

        document.getElementById('username-modal').style.display = 'none';
        // Informa al servidor del nom del client
        ws.send("NOM" + nomUsuari);
    });

    // Permitir enviar con Enter
    document.getElementById('missatge').addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && nomUsuari) {
            document.getElementById('btn-enviar').click();
        }
    });
    
    // Permitir enviar nombre con Enter también
    document.getElementById('nom-usuari').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            document.getElementById('btn-entrar').click();
        }
    });
});