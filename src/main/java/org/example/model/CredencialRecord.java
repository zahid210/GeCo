package org.example.model;

public record CredencialRecord(
        String red,
        String username,
        String password
){
    public CredencialRecord{
        if(red == null || red.isBlank()){
            throw new IllegalArgumentException("Error: La red no puede estar vacia.");
        }

        if(password == null || password.isBlank()){
            throw new IllegalArgumentException("Error : La contraseña no puede estar vacia.");
        }
    }
}
