document.addEventListener('DOMContentLoaded', function () {
    // Fa la connexió amb websocket al programa
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/ChatEncriptadoUDP/chat`;
    const ws = new WebSocket(wsUrl);

    const missatgeClientPropiConnectat = "$%&MSG-SYSTEM-CLIENT-PROPI-CONNECTAT&%$";
    const missatgeNouClient = "$%&MSG-SYSTEM-NOU-CLIENT&%$";
    const missatgeClientDesconectat = "$%&MSG-SYSTEM-CLIENT-DESCONECTAT&%$";
    const missatgeSistemaNom = "$%&MSG-SYSTEM-NOM&%$";
    const separador = "#$=/&%";

    let nomUsuari;
    let id;
    let estaConectat = false;

    ws.onopen = function () {
        console.log("WebSocket connectat");
        estaConectat = true;
    };

    ws.onclose = function () {
        console.log("WebSocket desconectat");
        estaConectat = false;
    };

    ws.onerror = function (error) {
        console.error("Error en WebSocket:", error);
    };

    // Missatges rebuts desde el servidor
    ws.onmessage = function (event) {
        if (!event.data) return;

        const parts = event.data.split(separador);
        console.log(parts);

        if (parts[0] === missatgeClientPropiConnectat) {
            afegirMissatge(missatgeClientPropiConnectat, parts[1], parts[2], parts[3]);
        } else if (parts[0] === missatgeNouClient) {
            afegirMissatge(missatgeNouClient, parts[1], parts[2]);
        } else if (parts[0] === missatgeClientDesconectat) {
            afegirMissatge(missatgeClientDesconectat, parts[1], parts[2]);
        } else {
            afegirMissatge(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
    };

    // Afegeix missatges al DOM
    function afegirMissatge(remitent, idRemitent, missatge, temps, esMissatgeTCP) {
        const contenidor = document.getElementById('chat-general');
        const nouContenidor = document.createElement('div');

        // Diferencia entre missatges enviats per el client o per un altre
        if (remitent === missatgeNouClient) { // Missatge per detectar a nous clients connectats
            const contenidorClients = document.getElementById('llista-usuaris');
            const nouLabel = document.createElement('label');
            const nouInput = document.createElement('input');
            const nouSpan = document.createElement('span');
            const nouSpan2 = document.createElement('span');

            nouLabel.classList.add('user-item');
            nouLabel.id = idRemitent;
            nouInput.setAttribute('type', 'checkbox');
            nouSpan.classList.add('avatar', 'online');
            nouSpan.textContent = missatge.charAt(0);
            nouSpan2.textContent = missatge;

            nouLabel.appendChild(nouInput);
            nouLabel.appendChild(nouSpan);
            nouLabel.appendChild(nouSpan2);
            contenidorClients.appendChild(nouLabel);
            return;
        } else if (remitent === missatgeClientDesconectat) {
            document.getElementById(idRemitent).remove();
        } else if (remitent === missatgeClientPropiConnectat) { // Missatge "Connectat com "
            nouContenidor.classList.add('message', 'received');
            nouContenidor.innerHTML = `<em>Connectat com ${missatge}</em><br>`;
            nouContenidor.style.backgroundColor = '#2e7d32';
            nouContenidor.innerHTML += `<i>${temps}</i>`

            id = idRemitent;

        } else if (id === idRemitent) { // Missatges del client
            nouContenidor.classList.add('message', 'sent');

            console.log("M Cliente:", esMissatgeTCP, esMissatgeTCP === "true");

            nouContenidor.innerHTML = `<strong>Tú: ${esMissatgeTCP === "true" ? "TCP" : "UDP"}</strong><br> ${missatge}<br>`;
            nouContenidor.innerHTML += `<i class="msg_client">${temps}</i>`
        } else { // Missatges d'un altre client
            nouContenidor.classList.add('message', 'received');

            console.log("M otro:", esMissatgeTCP, esMissatgeTCP === "true");

            nouContenidor.innerHTML = `<strong>${remitent}: ${esMissatgeTCP === "true" ? "TCP" : "UDP"}</strong><br> ${missatge}<br>`;
            nouContenidor.innerHTML += `<i class="msg_remitent">${temps}</i>`
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

        const seleccionats = document.querySelectorAll('#llista-usuaris input[type="checkbox"]:checked');
        let clientsSeleccionats = "";
        if (seleccionats.length != 0) {
            seleccionats.forEach(cb => {
                const label = cb.parentElement; 
                clientsSeleccionats += label.id + separador;
            });
        }

        // Envia el missatge al servidor
        ws.send(clientsSeleccionats + missatge);

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
        ws.send(missatgeSistemaNom + nomUsuari);
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