package Logica;

import Modelo.Pelicula;
import Modelo.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SistemaRecomendacion
 *
 * Persistencia automática:
 *   - Ruta fija: resources/archivos/cinematch.dat
 *   - cargarOInicializar() → carga el .dat si existe; si no, devuelve un sistema nuevo con datos de ejemplo.
 *   - guardarEstadoAuto()  → serializa siempre en la ruta fija.
 *   - Los métodos guardarEstado(path) / cargarEstado(path) se mantienen para
 *     compatibilidad con cualquier exportación manual desde la GUI.
 */
public class SistemaRecomendacion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Ruta fija de persistencia automática. */
    public static final String RUTA_AUTO = "resources/archivos/cinematch.dat";

    // ── Colecciones principales ───────────────────────────────────────────
    private List<Usuario>  listaUsuarios;
    private List<Pelicula> listaPeliculas;

    // Contadores para auto-generación de claves
    private int contadorUsuarios  = 1;
    private int contadorPeliculas = 1;

    // ── Constructor ───────────────────────────────────────────────────────
    public SistemaRecomendacion() {
        this.listaUsuarios  = new ArrayList<>();
        this.listaPeliculas = new ArrayList<>();
    }

    // ── CRUD y búsquedas ──────────────────────────────────────────────────

    public Usuario registrarUsuario(String nombre, int edad, String pais) {
        String cve    = String.format("U%03d", contadorUsuarios++);
        Usuario nuevo = new Usuario(cve, nombre, edad, pais);
        listaUsuarios.add(nuevo);
        return nuevo;
    }

    public Pelicula registrarPelicula(String nombre, String genero, int anio,
                                      String director, double calificacion,
                                      String rutaImagen) {
        String  cve   = String.format("P%03d", contadorPeliculas++);
        Pelicula nueva = new Pelicula(cve, nombre, genero, anio, director,
                                      calificacion, rutaImagen);
        listaPeliculas.add(nueva);
        return nueva;
    }

    public Usuario buscarUsuario(String cve) {
        for (Usuario u : listaUsuarios)
            if (u.getCve().equalsIgnoreCase(cve)) return u;
        return null;
    }

    public Pelicula buscarPelicula(String cve) {
        for (Pelicula p : listaPeliculas)
            if (p.getCve().equalsIgnoreCase(cve)) return p;
        return null;
    }

    // ── Lógica de negocio ─────────────────────────────────────────────────

    /** Cambia el estatus activo/inactivo de una película por su CVE. */
    public void cambiarEstatusPelicula(String cve, boolean activa) {
        Pelicula p = buscarPelicula(cve);
        if (p != null) p.setActiva(activa);
    }

    /**
     * Procesa la siguiente película de la watchlist (Cola) del usuario:
     * la extrae, y si está activa y no es ya favorita, la añade
     * a favoritas y al historial.
     */
    public void procesarSiguienteWatchlist(String cveUsuario) {
        Usuario u = buscarUsuario(cveUsuario);
        if (u == null || u.getWatchlist().isEmpty()) return;

        Pelicula p = u.getWatchlist().poll();           // Desencola
        if (p != null && p.isActiva()
                && !u.getPeliculasFavoritas().contains(p)) {
            u.getPeliculasFavoritas().add(p);
            u.getHistorialVistas().push(p);             // Apila
        }
    }

    // ── Persistencia ──────────────────────────────────────────────────────

    /**
     * Guarda el estado en la ruta indicada
     * Crea el directorio destino si no existe.
     *
     * @param rutaArchivo Ruta completa del archivo .dat
     * @throws IOException Si ocurre un error de escritura
     */
    public void guardarEstado(String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        // Crear directorios intermedios si no existen
        File directorio = archivo.getParentFile();
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }
        try (ObjectOutputStream oos =
                 new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(this);
        }
    }

    /**
     * Guarda el estado automáticamente en la ruta fija RUTA_AUTO.
     * Llamado al cerrar la aplicación.
     */
    public void guardarEstadoAuto() {
        try {
            guardarEstado(RUTA_AUTO);
            System.out.println("[CineMatch] Estado guardado en: " + RUTA_AUTO);
        } catch (IOException ex) {
            System.err.println("[CineMatch] No se pudo guardar el estado: " + ex.getMessage());
        }
    }

    /**
     * Carga un estado desde la ruta indicada.
     *
     * @param rutaArchivo Ruta completa del archivo .dat
     * @return El SistemaRecomendacion deserializado
     * @throws IOException            Si ocurre error de lectura
     * @throws ClassNotFoundException Si el archivo es incompatible
     */
    public static SistemaRecomendacion cargarEstado(String rutaArchivo)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois =
                 new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (SistemaRecomendacion) ois.readObject();
        }
    }

    /**
     * Intenta cargar el estado desde RUTA_AUTO.
     * Si el archivo no existe o hay un error, devuelve un sistema nuevo
     * con datos de ejemplo listos para usar.
     *
     * @return SistemaRecomendacion cargado o recién inicializado
     */
    public static SistemaRecomendacion cargarOInicializar() {
        File archivo = new File(RUTA_AUTO);

        if (archivo.exists()) {
            try {
                SistemaRecomendacion s = cargarEstado(RUTA_AUTO);
                System.out.println("[CineMatch] Estado cargado desde: " + RUTA_AUTO);
                return s;
            } catch (Exception ex) {
                System.err.println("[CineMatch] No se pudo deserializar el archivo existente "
                        + "(" + ex.getMessage() + "). Se inicializa con datos de ejemplo.");
            }
        } else {
            System.out.println("[CineMatch] Archivo '" + RUTA_AUTO
                    + "' no encontrado. Iniciando con datos de ejemplo.");
        }

        // ── Datos de ejemplo para el primer arranque ──────────────────────
        return crearSistemaEjemplo();
    }

    /**
     * Construye y retorna un SistemaRecomendacion con datos de ejemplo.
     * Usado en el primer arranque o cuando el .dat está corrupto.
     */
    private static SistemaRecomendacion crearSistemaEjemplo() {
        SistemaRecomendacion s = new SistemaRecomendacion();

        // Películas
        s.registrarPelicula("The Matrix",        "Sci-Fi",   1999, "Wachowskis",    4.8, "assets/imgMovies/matrix.jpg");
        s.registrarPelicula("The Godfather",     "Crime",    1972, "F. F. Coppola", 4.9, "assets/imgMovies/godfather.jpg");
        s.registrarPelicula("Interstellar",      "Sci-Fi",   2014, "C. Nolan",      4.8, "assets/imgMovies/interstellar.jpg");
        s.registrarPelicula("Parasite",          "Thriller", 2019, "Bong Joon-ho",  4.7, "assets/imgMovies/parasite.jpg");
        Pelicula pInception = s.registrarPelicula("Inception",         "Sci-Fi",   2010, "C. Nolan",      4.7, "assets/imgMovies/inception.jpg");
        Pelicula pDune      = s.registrarPelicula("Dune",              "Sci-Fi",   2021, "D. Villeneuve", 4.6, "assets/imgMovies/dune.jpg");
        Pelicula pBlade     = s.registrarPelicula("Blade Runner 2049", "Sci-Fi",   2017, "D. Villeneuve", 4.5, "assets/imgMovies/bladerunner.jpg");
        s.registrarPelicula("The Dark Knight",   "Action",   2008, "C. Nolan",      4.9, "assets/imgMovies/darkknight.jpg");
        s.registrarPelicula("Pulp Fiction",      "Crime",    1994, "Q. Tarantino",  4.7, "assets/imgMovies/pulpfiction.jpg");

        // Usuarios
        Usuario u1 = s.registrarUsuario("Marty Renna",  23, "México");
        Usuario u2 = s.registrarUsuario("Luis Montoya", 28, "Colombia");
        Usuario u3 = s.registrarUsuario("Ana Fuentes",  25, "Argentina");

        // Aristas (favoritas compartidas — crean conexiones en el grafo BFS)
        List<Pelicula> pelis = s.getListaPeliculas();
        u1.getPeliculasFavoritas().add(pelis.get(0)); // Matrix
        u1.getPeliculasFavoritas().add(pelis.get(1)); // Godfather

        u2.getPeliculasFavoritas().add(pelis.get(0));
        u2.getPeliculasFavoritas().add(pelis.get(1));
        u2.getPeliculasFavoritas().add(pelis.get(2)); // Interstellar
        u2.getPeliculasFavoritas().add(pelis.get(7)); // Dark Knight

        u3.getPeliculasFavoritas().add(pelis.get(0));
        u3.getPeliculasFavoritas().add(pelis.get(1));
        u3.getPeliculasFavoritas().add(pelis.get(3)); // Parasite
        u3.getPeliculasFavoritas().add(pelis.get(8)); // Pulp Fiction

        // Watchlist inicial de Marty (Cola FIFO)
        u1.getWatchlist().add(pInception);
        u1.getWatchlist().add(pDune);
        u1.getWatchlist().add(pBlade);

        return s;
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public List<Usuario>  getListaUsuarios()  {
        return listaUsuarios;
    }
    public List<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }
}
