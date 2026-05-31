package Modelo;

public class Pelicula extends Entidad {
    private String genero;
    private int anio;
    private String director;
    private double calificacionPromedio;
    private boolean activa; // Control de estatus/licencia
    private String rutaImagen;

    public Pelicula(String cve, String nombre, String genero, int anio, String director, double calificacionPromedio, String rutaImagen) {
        super(cve, nombre);
        this.genero = genero;
        this.anio = anio;
        this.director = director;
        this.calificacionPromedio = calificacionPromedio;
        this.activa = true; // Activa por defecto
        this.rutaImagen = rutaImagen;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}