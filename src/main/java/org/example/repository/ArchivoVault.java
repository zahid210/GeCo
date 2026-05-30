package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArchivoVault {
    private static final String NOMBRE_ARCHIVO = "vaul.json";
    private final Path rutaArchivo;
    private final Gson gson;

    public ArchivoVault(){
        this.rutaArchivo = Paths.get(NOMBRE_ARCHIVO);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public boolean existeVault(){
        return Files.exists(rutaArchivo);
    }

    public void guardar(String masterHash, String datosCifradosAES) throws IOException {
        Map<String, String> estructuraVault = new HashMap<>();
        estructuraVault.put("masterHash", masterHash);
        estructuraVault.put("encryptedData", datosCifradosAES);

        String jsonTexto = gson.toJson(estructuraVault);

        Files.writeString(rutaArchivo, jsonTexto);
    }

    public Map<String, String> cargar() throws IOException {
        if (!existeVault()){
            return new HashMap<>();
        }

        String jsonTexto = Files.readString(rutaArchivo);

        Type tipoMapa = new TypeToken<Map<String, String>>() {}.getType();

        return gson.fromJson(jsonTexto, tipoMapa);
    }

}
