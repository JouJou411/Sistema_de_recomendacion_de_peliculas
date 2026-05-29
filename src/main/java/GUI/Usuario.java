package GUI;

import java.util.LinkedList;

class Usuario extends Entidad {

    private int                  edad;
    private String               pais;
    private Pila<Pelicula>       historialVistas;
    private Cola<Pelicula>       watchlist;
    private LinkedList<Pelicula> peliculasFavoritas;

    public Usuario(String cve, String nombre, int edad, String pais) {
        super(cve, nombre);
        this.edad              = edad;
        this.pais              = pais;
        this.historialVistas   = new Pila<>();
        this.watchlist         = new Cola<>();
        this.peliculasFavoritas = new LinkedList<>();
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    /**
     * Pila de historial de películas vistas
     */
    public Pila<Pelicula> getHistorialVistas() { return historialVistas; }
    public void setHistorialVistas(Pila<Pelicula> historialVistas) {
        this.historialVistas = historialVistas;
    }

    /**
     * Cola de películas pendientes por ver
     */
    public Cola<Pelicula> getWatchlist() { return watchlist; }
    public void setWatchlist(Cola<Pelicula> watchlist) {
        this.watchlist = watchlist;
    }

    /**
     * Lista enlazada de películas favoritas.
     * Representa las aristas del usuario en el grafo de multilista.
     */
    public LinkedList<Pelicula> getPeliculasFavoritas() { return peliculasFavoritas; }
    public void setPeliculasFavoritas(LinkedList<Pelicula> peliculasFavoritas) {
        this.peliculasFavoritas = peliculasFavoritas;
    }
}
