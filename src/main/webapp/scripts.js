document.addEventListener('DOMContentLoaded', function () {
    const ws = new WebSocket("ws://localhost:8080/ChatEncriptadoUDP/chat");

    let nomUsuari;

    // Afegeix missatges al DOM
    function afegirMissatge(remitent, missatge) {
        const contenidor = document.getElementById('chat-general');
        const nouContenidor = document.createElement('div');

        // Diferencia entre missatges enviats per mi o per un altre
        if (remitent === nomUsuari) {
            nouContenidor.classList.add('message', 'sent');
        } else {
            nouContenidor.classList.add('message', 'received');
        }

        // Span per el remitent
        const nouSpan = document.createElement('span');
        nouSpan.classList.add('user');
        nouSpan.textContent = remitent;

        // Contingut del missatge
        const nouP = document.createElement('p');
        nouP.textContent = missatge;

        // Afegeix els elements
        nouContenidor.appendChild(nouSpan);
        nouContenidor.appendChild(nouP);
        contenidor.appendChild(nouContenidor);
    }

    // Corregir la función actualizar lista de usuarios
    function actualitzarLlistat(parts) {
        const contenidor = document.getElementById('usuaris-connectats');
        contenidor.innerHTML = ''; // Limpiar lista primero

        // Crear un elemento para cada usuario
        parts.forEach(nomUsuari => {
            const nouLi = document.createElement('li');
            nouLi.textContent = nomUsuari;
            nouLi.classList.add('usuari-item');
            contenidor.appendChild(nouLi);
        });
    }

    // Corregir el procesamiento de mensajes
    ws.onmessage = function (event) {
        if (!event.data) return;

        console.log("Mensaje recibido:", event.data); // Para depuración

        if (event.data.includes("!#ActualitzarLlistat")) {
            const parts = event.data.split('_');
            parts.shift(); // Quitar el marcador
            actualitzarLlistat(parts);
            return; // Salir para no procesar como mensaje normal
        }

        // Separem el format en el que viene el missatge
        const parts = event.data.split('_');
        if (parts.length >= 2) {
            const remitent = parts[0];
            const missatge = parts.slice(1).join('_');
            afegirMissatge(remitent, missatge);
        }
    };

    // Envia els missatges
    document.getElementById('btn-enviar').addEventListener('click', () => {
        const input = document.getElementById('missatge');
        const missatge = input.value.trim();
        if (missatge.length === 0) return;

        // Envia el missatge al servidor
        ws.send(missatge);

        // Mostra el missatge al dom
        afegirMissatge(nomUsuari, missatge);

        // Neteja la barra de escriure
        input.value = '';
    });

    // Estableix el nom del client
    document.getElementById('btn-entrar').addEventListener('click', () => {
        const input = document.getElementById('nom-usuari');
        nomUsuari = input.value.trim();
        if (nomUsuari.length === 0) return;

        document.getElementById('username-modal').remove();
        // Informa al servidor del nom del client
        ws.send("NOM" + nomUsuari);
    });

    // Permitir enviar con Enter
    document.getElementById('missatge').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            document.getElementById('btn-enviar').click();
        }
    });
});
