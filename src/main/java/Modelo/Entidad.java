package Modelo;

import java.io.Serializable;

// Clase base abstracta para serializacion y control de claves
public abstract class Entidad implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String cve;
    protected String nombre;

    public Entidad(String cve, String nombre) {
        this.cve = cve;
        this.nombre = nombre;
    }

    public String getCve() {
        return cve;
    }
    public String getNombre() {
        return nombre;
    }
}