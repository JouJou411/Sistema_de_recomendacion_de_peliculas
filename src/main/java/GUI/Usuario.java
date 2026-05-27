package GUI;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Usuario extends Entidad {
    private int edad;
    private String pais;
    private Stack<Pelicula> historialVistas; // Pila de "Últimas vistas"
    private Queue<Pelicula> watchlist;       // Cola de pendientes
    private LinkedList<Pelicula> peliculasFavoritas; // Sub-lista de la multilista (Aristas)

    public Usuario(String cve, String nombre, int edad, String pais) {
        super(cve, nombre);
        this.edad = edad;
        this.pais = pais;
        this.historialVistas = new Stack<>();
        this.watchlist = new LinkedList<>();
        this.peliculasFavoritas = new LinkedList<>();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Stack<Pelicula> getHistorialVistas() {
        return historialVistas;
    }

    public void setHistorialVistas(Stack<Pelicula> historialVistas) {
        this.historialVistas = historialVistas;
    }

    public Queue<Pelicula> getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Queue<Pelicula> watchlist) {
        this.watchlist = watchlist;
    }

    public LinkedList<Pelicula> getPeliculasFavoritas() {
        return peliculasFavoritas;
    }

    public void setPeliculasFavoritas(LinkedList<Pelicula> peliculasFavoritas) {
        this.peliculasFavoritas = peliculasFavoritas;
    }
}