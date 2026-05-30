package org.example.ui;

import org.example.model.CredencialRecord;
import org.example.service.GestorService;

import java.util.Scanner;
import java.util.Set;

public class MenuConsola {
    private final GestorService gestorService;
    private final Scanner scanner;

    public MenuConsola() {
        this.gestorService = new GestorService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("========================================");
        System.out.println("  BIENVENIDO AL GESTOR DE CONTRASEÑAS   ");
        System.out.println("========================================");

        try {
            if (gestorService.esPrimerUso()) {
                configurarPrimerUso();
            } else {
                manejarInicioSesion();
            }

            bucleMenuPrincipal();

        } catch (Exception e) {
            System.out.println("\nError crítico en la aplicación: " + e.getMessage());
        } finally {
            scanner.close();
        }

    }

    private void configurarPrimerUso() throws Exception {
        System.out.println("\n[DETECTADO PRIMER USO]");
        System.out.print("Defina su Contraseña Maestra de acceso: ");
        String clave = scanner.nextLine();

        gestorService.registrarPrimerUso(clave);
        System.out.println("Contraseña Maestra configurada y baúl inicializado con éxito.");
    }

    private void manejarInicioSesion() throws Exception {
        int intentos = 3;
        while (intentos > 0) {
            System.out.print("\nIngrese su Contraseña Maestra: ");
            String clave = scanner.nextLine();

            if (gestorService.iniciarSesion(clave)) {
                System.out.println("Cargando credenciales...");
                return;
            } else {
                intentos--;
                System.out.println("Contraseña incorrecta. Intentos restantes: " + intentos);
            }
        }
        throw new SecurityException("Demasiados intentos fallidos. Aplicación bloqueada.");
    }

    private void bucleMenuPrincipal() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Agregar nueva contraseña");
            System.out.println("2. Buscar una contraseña");
            System.out.println("3. Listar todos los sitios guardados");
            System.out.println("4. Guardar cambios y Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> registrarNuevaCredencial();
                case "2" -> buscarCredencial();
                case "3" -> listarSitios();
                case "4" -> {
                    guardarYSalir();
                    continuar = false;
                }
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void registrarNuevaCredencial() {
        System.out.println("\n--- REGISTRAR NUEVA RED ---");
        System.out.print("Red: ");
        String red = scanner.nextLine();
        System.out.print("Nombre de Usuario / Correo: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        try {
            gestorService.agregarCredencial(red, username, password);
            System.out.println("Credencial agregada temporalmente a la memoria.");
        } catch (IllegalArgumentException e) {
            // Atrapamos las validaciones que configuramos en el Record de la Fase 1
            System.out.println("\nNo se pudo agregar: " + e.getMessage());
        }
    }

    private void buscarCredencial() {
        System.out.println("\n--- BUSCAR CREDENCIAL ---");
        System.out.print("Ingrese el nombre de la red: ");
        String red = scanner.nextLine();

        CredencialRecord cuenta = gestorService.buscarCredencial(red);

        if (cuenta != null) {
            System.out.println("\nDatos encontrados:");
            System.out.println("Sitio: " + cuenta.red());
            System.out.println("Usuario: " + cuenta.username());
            System.out.println("Contraseña: " + cuenta.password());
        } else {
            System.out.println("No se encontraron credenciales para: " + red);
        }
    }

    private void listarSitios() {
        System.out.println("\n--- REDES REGISTRADOS ---");
        Set<String> redes = gestorService.listarRedes();

        if (redes.isEmpty()) {
            System.out.println("El baúl está vacío.");
        } else {
            redes.forEach(sitio -> System.out.println("• " + sitio));
        }
    }

    private void guardarYSalir() {
        try {
            System.out.println("\nEncapsulando y cifrando datos con AES...");
            gestorService.guardarCambios();
            System.out.println("Cambios guardados en vault.json.");
        } catch (Exception e) {
            System.out.println("Error al intentar guardar los datos: " + e.getMessage());
        }
    }

}
