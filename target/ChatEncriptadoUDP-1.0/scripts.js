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

    function actualitzarLlistat(parts) {
        const contenidor = document.getElementById('usuaris-connectats');
        // Limpiar la lista actual
        contenidor.innerHTML = '';
        // Agregar cada usuario a la lista
        for (const nom of parts) {
            if (nom && nom.trim() !== '') {
                const nouLi = document.createElement('li');
                nouLi.textContent = nom;
                contenidor.appendChild(nouLi);
            }
        }
    }

    // Missatges rebuts desde el servidor
    ws.onmessage = function (event) {
        if (!event.data) return;

        if (event.data.includes("!#ActualitzarLlistat")) {
            const parts = event.data.split('_');
            parts.shift();

            actualitzarLlistat(parts);
        }

        // Separem el format en el que be el missatge
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

    // Permet enviar missatges amb el enter
    document.getElementById('missatge').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            document.getElementById('btn-enviar').click();
        }
    });
    
});
