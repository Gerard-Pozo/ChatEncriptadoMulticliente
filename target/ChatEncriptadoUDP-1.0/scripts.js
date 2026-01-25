document.addEventListener('DOMContentLoaded', function () {
    // Fa la connexió amb websocket al programa
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/ChatEncriptadoUDP/chat`;
    const ws = new WebSocket(wsUrl);

    let nomUsuari;
    let estaConectat = false;

    ws.onopen = function() {
        console.log("WebSocket connectat");
        estaConectat = true;
    };

    ws.onclose = function() {
        console.log("WebSocket desconectat");
        estaConectat = false;
    };

    ws.onerror = function(error) {
        console.error("Error en WebSocket:", error);
    };

    // Missatges rebuts desde el servidor
    ws.onmessage = function (event) {
        if (!event.data) return;

        if (event.data.includes("$%&MSG_SYSTEM_NOU_CLIENT&%$")) {
            afegirMissatge("NOU_CLIENT", event.data.substring(27, event.data.length));
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

    // Afegeix missatges al DOM
    function afegirMissatge(remitent, missatge) {
        const contenidor = document.getElementById('chat-general');
        const nouContenidor = document.createElement('div');

        // Diferencia entre missatges enviats per el client o per un altre
        if (remitent === nomUsuari) { // Missatges del client
            nouContenidor.classList.add('message', 'sent');
            nouContenidor.innerHTML = `<strong>Tú:</strong><br> ${missatge}`;
        } else if (remitent === 'SERVIDOR') { // Missatge "Connectat com "
            nouContenidor.classList.add('message', 'received');
            nouContenidor.innerHTML = `<em>${missatge}</em>`;
            nouContenidor.style.backgroundColor = '#2e7d32';
        } else if (remitent === 'NOU_CLIENT') {
            const contenidorClients = document.getElementById('user-list');
            const nouLabel = document.createElement('label');
            const nouInput = document.createElement('input');
            const nouDiv = document.createElement('div');
            const nouSpan = document.createElement('span');

            nouLabel.classList.add('user-item');
            nouInput.setAttribute('type', 'checkbox');
            nouDiv.classList.add('avatar', 'online');
            nouSpan.textContent = missatge;
            return;
        } else { // Missatges d'un altre client
            nouContenidor.classList.add('message', 'received');
            nouContenidor.innerHTML = `<strong>${remitent}:</strong><br> ${missatge}`;
        }

        contenidor.appendChild(nouContenidor);
        // Posiciona el scroll al final
        contenidor.scrollTop = contenidor.scrollHeight;
    }

    // Envia els missatges
    document.getElementById('btn-enviar').addEventListener('click', () => {       
        const input = document.getElementById('missatge');
        const missatge = input.value.trim();
        if (missatge.length === 0) return;

        if (!estaConectat) {
            alert("No estas connectat al chat");
            return;
        }

        // Envia el missatge al servidor
        ws.send(missatge);

        // Treu el missatge enviat
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
        ws.send("$%&MSG_SYSTEM_NOM&%$" + nomUsuari);
    });

    // Permitir enviar amb enter
    document.getElementById('missatge').addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && nomUsuari) {
            document.getElementById('btn-enviar').click();
        }
    });
    
    // Permitir enviar el nom amb enter
    document.getElementById('nom-usuari').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            document.getElementById('btn-entrar').click();
        }
    });
});