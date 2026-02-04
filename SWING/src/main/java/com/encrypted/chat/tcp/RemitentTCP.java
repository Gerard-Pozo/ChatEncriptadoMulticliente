package com.encrypted.chat.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.encrypted.chat.models.MissatgeDesencriptat;
import com.encrypted.chat.models.MissatgeEncriptat;
import com.encrypted.chat.models.MissatgeEncriptat.TipusMissatge;
import com.encrypted.chat.models.Persona;
import com.encrypted.chat.security.DiffieHellman;
import com.encrypted.chat.utils.Utils;

public class RemitentTCP {

    public static void enviarMissatge(MissatgeDesencriptat missatge) {
        System.out.println("Enviando mensaje...");
        Socket socket = new Socket();
        Persona p = missatge.getDestinatari();
        try {
            // S'intenta connectar a un client, si en 1 segon no dona resposta tanca
            socket.connect(new InetSocketAddress(p.getIpv4(), Utils.PORT_TCP), 1000);
            // Es genera una clau AES que s'utilitzará per encriptar el text
            System.out.println("El mensaje se envia a " + p.getIpv4());
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey clauSessio = keyGen.generateKey();

            // S'encripta el missatge amb la clau de la sessio
            String textEncriptat = DiffieHellman.encriptarMissatge(missatge.getMissatge(), clauSessio);

            MissatgeEncriptat missatgeEncriptat = new MissatgeEncriptat(TipusMissatge.ENVIAMENT, textEncriptat,
                    missatge.getEmisor());

            // Encripta la clau per desxifrar el missatge amb la privada del remitent i la
            // publica del destinatari
            SecretKey clauDH = DiffieHellman.generarClauCompartidaAES(missatge.getEmisor().getPrivada(),
                    p.getPublica());

            // Xifra la clau de la sessio
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, clauDH);

            // Encripta la clau de sessio amb la clau AES generada anteriorment
            byte[] clauSessioEncriptada = cipher.doFinal(clauSessio.getEncoded());
            String clauSessioXifrada = Base64.getEncoder().encodeToString(clauSessioEncriptada);

            missatgeEncriptat.afegirDestinataris(p.getId(), clauSessioXifrada);

            ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());

            sortida.writeObject(missatgeEncriptat);
            sortida.flush();

            System.out.println("Mensaje enviado " + missatgeEncriptat);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
