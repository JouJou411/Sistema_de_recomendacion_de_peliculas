package GUI;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SistemaRecomendacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Usuario> listaUsuarios;   // Lista principal de usuarios
    private List<Pelicula> listaPeliculas; // Lista principal de películas

    // Contadores para autogenerar claves
    private int contadorUsuarios = 1;
    private int contadorPeliculas = 1;

    public SistemaRecomendacion() {
        this.listaUsuarios = new ArrayList<>();
        this.listaPeliculas = new ArrayList<>();
    }

    // --- CRUD Y BÚSQUEDAS SECUENCIALES ---

    public Usuario registrarUsuario(String nombre, int edad, String pais) {
        String cve = String.format("U%03d", contadorUsuarios++);
        Usuario nuevo = new Usuario(cve, nombre, edad, pais);
        listaUsuarios.add(nuevo);
        return nuevo;
    }

    public Pelicula registrarPelicula(String nombre, String genero, int anio, String director, double calificacion, String rutaImagen) {
        String cve = String.format("P%03d", contadorPeliculas++);
        Pelicula nueva = new Pelicula(cve, nombre, genero, anio, director, calificacion, rutaImagen);
        listaPeliculas.add(nueva);
        return nueva;
    }

    public Usuario buscarUsuario(String cve) {
        for (Usuario u : listaUsuarios) {
            if (u.getCve().equalsIgnoreCase(cve)) return u;
        }
        return null; // No encontrado
    }

    public Pelicula buscarPelicula(String cve) {
        for (Pelicula p : listaPeliculas) {
            if (p.getCve().equalsIgnoreCase(cve)) return p;
        }
        return null; // No encontrado
    }

    // --- LÓGICA DE NEGOCIO ---

    // Baja de catálogo (Modificación lógica de estatus)
    public void cambiarEstatusPelicula(String cve, boolean activa) {
        Pelicula p = buscarPelicula(cve);
        if (p != null) {
            p.setActiva(activa);
        }
    }

    // Procesar la Watchlist (Cola -> Multilista/Grafo)
    public void procesarSiguienteWatchlist(String cveUsuario) {
        Usuario u = buscarUsuario(cveUsuario);
        if (u != null && !u.getWatchlist().isEmpty()) {
            // Saca la película al tope de la cola
            Pelicula p = u.getWatchlist().poll();

            // Si está activa, se agrega a sus gustos (crea la arista en la multilista)
            if (p.isActiva() && !u.getPeliculasFavoritas().contains(p)) {
                u.getPeliculasFavoritas().add(p);
                u.getHistorialVistas().push(p); // Se añade también a su historial
            }
        }
    }

    // --- PERSISTENCIA BINARIA (SERIALIZACIÓN) ---

    public void guardarEstado(String nombreArchivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(this);
        }
    }

    public static SistemaRecomendacion cargarEstado(String nombreArchivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            return (SistemaRecomendacion) ois.readObject();
        }
    }

    // Getters para la GUI
    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
    public List<Pelicula> getListaPeliculas() { return listaPeliculas; }
}