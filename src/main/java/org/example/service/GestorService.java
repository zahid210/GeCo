package org.example.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.crypto.CifradoAES;
import org.example.crypto.Hashing;
import org.example.model.CredencialRecord;
import org.example.repository.ArchivoVault;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GestorService {
    private final ArchivoVault archivoVault;
    private final Gson gson;

    private final Map<String, CredencialRecord> miBaulEnMemoria;

    private String passwordMaestraCache;

    public GestorService(){
        this.archivoVault = new ArchivoVault();
        this.gson = new Gson();
        this.miBaulEnMemoria = new HashMap<>();
    }

    public boolean esPrimerUso(){
        return !archivoVault.existeVault();
    }

    public void registrarPrimerUso(String passwordMaestra) throws Exception {
        this.passwordMaestraCache = passwordMaestra;
        String masterHash = Hashing.generarHash(passwordMaestra);

        String datosCifradosVacios = CifradoAES.encriptar("{}", passwordMaestra);
        archivoVault.guardar(masterHash, datosCifradosVacios);
    }

    public boolean iniciarSesion(String passwordMaestra) throws Exception {
        if(esPrimerUso()) return false;

        Map<String, String> datosRaw = archivoVault.cargar();
        String hashAlmacenado = datosRaw.get("masterHash");

        if(Hashing.verificarPassword(passwordMaestra, hashAlmacenado)){
            this.passwordMaestraCache = passwordMaestra;

            String datosCifradosAES = datosRaw.get("encryptedData");
            String jsonDesencriptado = CifradoAES.desencriptar(datosCifradosAES, passwordMaestra);

            Type tipoMapa = new TypeToken<Map<String,CredencialRecord>>() {}.getType();
            Map<String, CredencialRecord> mapeoClonado = gson.fromJson(jsonDesencriptado, tipoMapa);

            if(mapeoClonado != null){
                this.miBaulEnMemoria.putAll(mapeoClonado);
            }
            return true;
        }
        return false;
    }

    public void agregarCredencial(String red, String username, String password){
        CredencialRecord nuevaCredencial = new CredencialRecord(red,username,password);
        miBaulEnMemoria.put(red.toLowerCase(), nuevaCredencial);
    }

    public CredencialRecord buscarCredencial(String red){
        return miBaulEnMemoria.get(red.toLowerCase());
    }

    public Set<String> listarRedes(){
        return miBaulEnMemoria.keySet();
    }

    public void guardarCambios() throws Exception {
        String masterHash = Hashing.generarHash(passwordMaestraCache);
        String jsonCredenciales = gson.toJson(miBaulEnMemoria);
        String datosCifradosAES = CifradoAES.encriptar(jsonCredenciales, passwordMaestraCache);
        archivoVault.guardar(masterHash, datosCifradosAES);
    }
}
