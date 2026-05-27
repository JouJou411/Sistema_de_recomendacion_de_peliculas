package GUI;

import com.formdev.flatlaf.FlatDarkLaf; // Requiere la librería FlatLaf en el classpath
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    // Conexión con el Backend centralizado
    private SistemaRecomendacion sistema;
    private Usuario usuarioActual;

    // Componentes de la interfaz que se actualizarán dinámicamente
    private JPanel panelContenedorTarjetas;
    private JPanel panelListaWatchlist;
    private JLabel lblNombreUsuario;
    private JLabel lblCveUsuario;

    public VentanaPrincipal(SistemaRecomendacion sistema) {
        this.sistema = sistema;

        // Configuración estética inicial de la ventana
        setTitle("Movie Dashboard - Sistema de Recomendación Cinematográfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Selector por defecto para pruebas (Carga el usuario U001 si existe)
        if (!sistema.getListaUsuarios().isEmpty()) {
            this.usuarioActual = sistema.getListaUsuarios().get(0);
        }

        // --- 1. BARRA LATERAL IZQUIERDA (NAVEGACIÓN) ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(22, 22, 35));
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel lblLogo = new JLabel("Movie Dashboard", SwingConstants.CENTER);
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblLogo.setForeground(new Color(130, 110, 250));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(40));

        String[] opcionesMenu = {"Usuarios", "Películas", "Motor de Recomendación"};
        for (String opcion : opcionesMenu) {
            JButton btnMenu = getJButton(opcion);
            sidebar.add(btnMenu);
            sidebar.add(Box.createVerticalStrut(10));
        }
        add(sidebar, BorderLayout.WEST);

        // --- 2. PANEL CENTRAL (CONTENIDO DE RECOMENDACIONES) ---
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setBackground(new Color(14, 14, 23));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Encabezado del Panel Central
        JLabel lblTituloSeccion = new JLabel("Sistema de Recomendación Cinematográfica");
        lblTituloSeccion.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTituloSeccion.setForeground(Color.WHITE);
        panelCentral.add(lblTituloSeccion, BorderLayout.NORTH);

        // Tarjeta de perfil del Usuario Actual (Marty Renna U001)
        JPanel panelPerfilUser = new JPanel(new BorderLayout(15, 10));
        panelPerfilUser.setBackground(new Color(22, 22, 35));
        panelPerfilUser.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel infoUserTexto = new JPanel(new GridLayout(2, 1, 0, 5));
        infoUserTexto.setOpaque(false);
        lblNombreUsuario = new JLabel(usuarioActual != null ? usuarioActual.getNombre() : "No hay usuarios");
        lblNombreUsuario.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNombreUsuario.setForeground(Color.WHITE);
        lblCveUsuario = new JLabel(usuarioActual != null ? usuarioActual.getCve() : "U000");
        lblCveUsuario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblCveUsuario.setForeground(Color.GRAY);
        infoUserTexto.add(lblNombreUsuario);
        infoUserTexto.add(lblCveUsuario);

        panelPerfilUser.add(infoUserTexto, BorderLayout.CENTER);

        // Panel del Grid de Recomendaciones ("Collaborative Filtering Recommendation")
        JPanel panelRecomendaciones = new JPanel(new BorderLayout(10, 10));
        panelRecomendaciones.setOpaque(false);

        JLabel lblSubtitulo = new JLabel("Collaborative Filtering Recommendation");
        lblSubtitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSubtitulo.setForeground(Color.LIGHT_GRAY);
        panelRecomendaciones.add(lblSubtitulo, BorderLayout.NORTH);

        // Grid dinámico de 1 fila x 4 columnas para colocar las tarjetas de películas
        panelContenedorTarjetas = new JPanel(new GridLayout(1, 4, 15, 0));
        panelContenedorTarjetas.setOpaque(false);
        panelRecomendaciones.add(panelContenedorTarjetas, BorderLayout.CENTER);

        // Botón principal de ejecución del algoritmo BFS
        JButton btnEjecutarBFS = getJButton();
        panelRecomendaciones.add(btnEjecutarBFS, BorderLayout.SOUTH);

        // Ensamblado central
        JPanel contenedorCentro = new JPanel(new BorderLayout(20, 20));
        contenedorCentro.setOpaque(false);
        contenedorCentro.add(panelPerfilUser, BorderLayout.NORTH);
        contenedorCentro.add(panelRecomendaciones, BorderLayout.CENTER);
        panelCentral.add(contenedorCentro, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // --- 3. BARRA LATERAL DERECHA (WATCHLIST / COLA DE PENDIENTES) ---
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        panelDerecho.setBackground(new Color(22, 22, 35));
        panelDerecho.setPreferredSize(new Dimension(180, 700));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblWatchlistTitulo = new JLabel("Watchlist (Queue)", SwingConstants.CENTER);
        lblWatchlistTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblWatchlistTitulo.setForeground(Color.WHITE);
        panelDerecho.add(lblWatchlistTitulo, BorderLayout.NORTH);

        // Lista de elementos verticales de la cola
        panelListaWatchlist = new JPanel();
        panelListaWatchlist.setLayout(new BoxLayout(panelListaWatchlist, BoxLayout.Y_AXIS));
        panelListaWatchlist.setOpaque(false);

        JScrollPane scrollWatchlist = new JScrollPane(panelListaWatchlist);
        scrollWatchlist.setBorder(null);
        scrollWatchlist.setOpaque(false);
        scrollWatchlist.getViewport().setOpaque(false);
        panelDerecho.add(scrollWatchlist, BorderLayout.CENTER);

        add(panelDerecho, BorderLayout.EAST);

        // Renderizado inicial de la interfaz con los datos existentes
        cargarDatosDeUsuario();
    }

    private JButton getJButton() {
        JButton btnEjecutarBFS = new JButton("Ejecutar BFS");
        btnEjecutarBFS.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnEjecutarBFS.setBackground(new Color(0, 210, 230)); // Color cian de la imagen
        btnEjecutarBFS.setForeground(Color.BLACK);
        btnEjecutarBFS.setPreferredSize(new Dimension(0, 45));
        btnEjecutarBFS.setFocusable(false);

        // Evento que conecta el botón de la interfaz con el algoritmo analítico del backend
        btnEjecutarBFS.addActionListener(e -> calcularYDesplegarRecomendaciones());
        return btnEjecutarBFS;
    }

    private static JButton getJButton(String opcion) {
        JButton btnMenu = new JButton(opcion);
        btnMenu.setMaximumSize(new Dimension(190, 40));
        btnMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMenu.setFocusable(false);
        // Resaltar la pestaña activa del diseño
        if (opcion.equals("Motor de Recomendación")) {
            btnMenu.setBackground(new Color(35, 35, 60));
            btnMenu.setForeground(new Color(0, 210, 230));
        } else {
            btnMenu.setBorderPainted(false);
            btnMenu.setContentAreaFilled(false);
            btnMenu.setForeground(Color.LIGHT_GRAY);
        }
        return btnMenu;
    }

    /**
     * Extrae la información del usuario actual, limpia la interfaz y dibuja
     * los elementos correspondientes a su Watchlist (Cola).
     */
    private void cargarDatosDeUsuario() {
        if (usuarioActual == null) return;

        lblNombreUsuario.setText(usuarioActual.getNombre());
        lblCveUsuario.setText(usuarioActual.getCve());

        // Limpiar elementos previos de la cola visual
        panelListaWatchlist.removeAll();

        // Recorrer la estructura de Cola (Queue) del usuario sin vaciarla
        for (Pelicula p : usuarioActual.getWatchlist()) {
            JLabel lblItem = new JLabel();
            lblItem.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblItem.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            // Cargar imagen en miniatura para la barra lateral de la cola
            File f = new File(p.getRutaImagen());
            if (f.exists()) {
                ImageIcon imgOriginal = new ImageIcon(p.getRutaImagen());
                Image imgEscalada = imgOriginal.getImage().getScaledInstance(130, 180, Image.SCALE_SMOOTH);
                lblItem.setIcon(new ImageIcon(imgEscalada));
            } else {
                lblItem.setText("[" + p.getNombre() + "]");
                lblItem.setForeground(Color.GRAY);
            }
            panelListaWatchlist.add(lblItem);
        }

        panelListaWatchlist.revalidate();
        panelListaWatchlist.repaint();
    }

    /**
     * Invoca el algoritmo BFS del backend sobre la estructura del grafo y dibuja
     * las tarjetas de películas recomendadas en el panel central.
     */
    private void calcularYDesplegarRecomendaciones() {
        if (usuarioActual == null) return;

        // Limpiar el contenedor de recomendaciones previas
        panelContenedorTarjetas.removeAll();

        // Llamada directa al algoritmo BFS pasándole la lista del grafo
        List<Pelicula> recomendadas = MotorRecomendacion.obtenerRecomendaciones(usuarioActual, sistema.getListaUsuarios());

        // Simular o mapear porcentajes ficticios de concordancia basados en la calificación
        // para emular fielmente los valores "96% Match" expuestos en el diseño de la imagen
        int elementosAMostrar = Math.min(recomendadas.size(), 4);

        for (int i = 0; i < elementosAMostrar; i++) {
            Pelicula p = recomendadas.get(i);

            // Cálculo para simular el Score de compatibilidad visual
            int matchPercent = 90 + (int)(p.getCalificacionPromedio() * 1);
            if (matchPercent > 100) matchPercent = 100;

            TarjetaPelicula tarjeta = new TarjetaPelicula(
                    p.getNombre(),
                    String.valueOf(p.getCalificacionPromedio()),
                    matchPercent + "%",
                    p.getRutaImagen()
            );
            panelContenedorTarjetas.add(tarjeta);
        }

        // Rellenar espacios vacíos si el algoritmo devuelve menos de 4 recomendaciones
        if (elementosAMostrar < 4) {
            for (int i = elementosAMostrar; i < 4; i++) {
                JPanel placeholder = new JPanel();
                placeholder.setOpaque(false);
                panelContenedorTarjetas.add(placeholder);
            }
        }

        panelContenedorTarjetas.revalidate();
        panelContenedorTarjetas.repaint();
    }

    /**
     * Método Main: Inicializador de la aplicación aplicando el Look and Feel oscuro.
     */
    public static void main(String[] args) {
        // Inicializamos el entorno gráfico con FlatDarkLaf para heredar el estilo moderno oscuro
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Error al inicializar el entorno gráfico FlatLaf.");
        }

        // --- Datos de Prueba para validar que renderice idéntico a tu diseño ---
        SistemaRecomendacion sistemaPrueba = new SistemaRecomendacion();

        // 1. Registro de Películas con sus rutas de imagen relativas fijadas
        sistemaPrueba.registrarPelicula("The Matrix", "Sci-Fi", 1999, "Wachowskis", 4.8, "assets/imgMovies/matrix.jpg");
        sistemaPrueba.registrarPelicula("The Godfather", "Crime", 1972, "F. F. Coppola", 4.8, "assets/imgMovies/godfather.jpg");
        sistemaPrueba.registrarPelicula("Interstellar", "Sci-Fi", 2014, "C. Nolan", 4.8, "assets/imgMovies/interstellar.jpg");
        sistemaPrueba.registrarPelicula("Parasite", "Thriller", 2019, "Bong Joon-ho", 4.8, "assets/imgMovies/parasite.jpg");

        // Películas adicionales para la Watchlist lateral
        Pelicula pInception = sistemaPrueba.registrarPelicula("Inception", "Sci-Fi", 2010, "C. Nolan", 4.7, "assets/imgMovies/inception.jpg");
        Pelicula pDune = sistemaPrueba.registrarPelicula("Dune", "Sci-Fi", 2021, "D. Villeneuve", 4.6, "assets/imgMovies/dune.jpg");
        Pelicula pBlade = sistemaPrueba.registrarPelicula("Blade Runner 2049", "Sci-Fi", 2017, "D. Villeneuve", 4.5, "assets/imgMovies/bladerunner.jpg");

        // 2. Registro de Usuarios del grafo
        Usuario u1 = sistemaPrueba.registrarUsuario("Marty Renna", 23, "México"); // Usuario Objetivo
        Usuario u2 = sistemaPrueba.registrarUsuario("Vecino 1", 25, "Usa");
        Usuario u3 = sistemaPrueba.registrarUsuario("Vecino 2", 30, "Canadá");

        // 3. Crear las Aristas en la Multilista para forzar las coincidencias en el BFS
        // Compartimos "The Matrix" y "The Godfather" entre usuarios para cumplir la regla analítica (mínimo 2)
        List<Pelicula> pelis = sistemaPrueba.getListaPeliculas();
        u1.getPeliculasFavoritas().add(pelis.get(0));
        u1.getPeliculasFavoritas().add(pelis.get(1));

        u2.getPeliculasFavoritas().add(pelis.get(0));
        u2.getPeliculasFavoritas().add(pelis.get(1));
        u2.getPeliculasFavoritas().add(pelis.get(2)); // Sugerirá Interstellar

        u3.getPeliculasFavoritas().add(pelis.get(0));
        u3.getPeliculasFavoritas().add(pelis.get(1));
        u3.getPeliculasFavoritas().add(pelis.get(3)); // Sugerirá Parasite

        // 4. Llenar la Cola (Watchlist) del usuario objetivo
        u1.getWatchlist().add(pInception);
        u1.getWatchlist().add(pDune);
        u1.getWatchlist().add(pBlade);

        // Desplegar la Ventana
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal(sistemaPrueba).setVisible(true);
        });
    }
}
