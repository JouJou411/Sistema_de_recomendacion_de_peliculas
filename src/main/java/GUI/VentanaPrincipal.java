package GUI;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * VentanaPrincipal — Contenedor raíz con sidebar de navegación y CardLayout.
 * Orquesta la navegación entre: MotorRecomendacion, Usuarios, Películas.
 */
public class VentanaPrincipal extends JFrame {

    // ── Paleta ──────────────────────────────────────────────────────────────
    static final Color BG_DEEP    = new Color(10,  10,  15);
    static final Color BG_PANEL   = new Color(17,  17,  26);
    static final Color BG_CARD    = new Color(24,  24,  36);
    static final Color ACCENT     = new Color(232, 197, 104); // dorado
    static final Color CYAN       = new Color( 78, 205, 196);
    static final Color TEXT_PRI   = new Color(240, 237, 228);
    static final Color TEXT_MUT   = new Color(107, 107, 122);
    static final Color BORDER_COL = new Color(255, 255, 255, 18);

    // ── Fuentes ─────────────────────────────────────────────────────────────
    static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD,  22);
    static final Font FONT_HEAD   = new Font("SansSerif", Font.BOLD,  15);
    static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 13);
    static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN, 11);
    static final Font FONT_LABEL  = new Font("SansSerif", Font.BOLD,  11);

    // ── Estado ───────────────────────────────────────────────────────────────
    private final SistemaRecomendacion sistema;

    // ── Navegación ───────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     panelCentral = new JPanel(cardLayout);
    private JButton[]        navBtns;

    // ── Pantallas ────────────────────────────────────────────────────────────
    private PantallaMotor    pantallaMotor;
    private PantallaUsuarios pantallaUsuarios;
    private PantallaPeliculas pantallaPeliculas;

    public VentanaPrincipal(SistemaRecomendacion sistema) {
        this.sistema = sistema;

        // --- Ventana base ---
        setTitle("CineMatch — Sistema de Recomendación Cinematográfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);
        setLayout(new BorderLayout());

        // --- Construir pantallas ---
        pantallaMotor      = new PantallaMotor(sistema);
        pantallaUsuarios   = new PantallaUsuarios(sistema, this);
        pantallaPeliculas  = new PantallaPeliculas(sistema, this);

        panelCentral.setBackground(BG_DEEP);
        panelCentral.add(pantallaMotor,     "motor");
        panelCentral.add(pantallaUsuarios,  "usuarios");
        panelCentral.add(pantallaPeliculas, "peliculas");

        add(buildSidebar(), BorderLayout.WEST);
        add(panelCentral,   BorderLayout.CENTER);

        mostrarPantalla("motor");
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_PANEL);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(24, 18, 28, 18));
        JLabel logo = new JLabel("CineMatch");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(ACCENT);
        logoPanel.add(logo);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoPanel);

        // Sección label
        sidebar.add(sectionLabel("Navegación"));

        // Botones de nav
        String[][] items = {
            {"motor",     "⚡ Motor BFS"},
            {"usuarios",  "👥 Usuarios"},
            {"peliculas", "🎬 Películas"},
        };
        navBtns = new JButton[items.length];
        for (int i = 0; i < items.length; i++) {
            final String key = items[i][0];
            JButton btn = buildNavBtn(items[i][1], key.equals("motor"));
            btn.addActionListener(e -> mostrarPantalla(key));
            navBtns[i] = btn;
            sidebar.add(wrapNav(btn));
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sectionLabel("Sistema"));

        // Botón guardar/cargar
        JButton btnGuardar = buildNavBtn("💾 Guardar estado", false);
        btnGuardar.addActionListener(e -> guardarEstado());
        sidebar.add(wrapNav(btnGuardar));
        sidebar.add(Box.createVerticalStrut(2));

        JButton btnCargar = buildNavBtn("📂 Cargar estado", false);
        btnCargar.addActionListener(e -> cargarEstado());
        sidebar.add(wrapNav(btnCargar));

        sidebar.add(Box.createVerticalGlue());

        // Footer version
        JLabel ver = new JLabel("v2.0 — BFS Engine");
        ver.setFont(FONT_SMALL);
        ver.setForeground(TEXT_MUT);
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        ver.setBorder(new EmptyBorder(12, 18, 18, 18));
        sidebar.add(ver);

        return sidebar;
    }

    private JButton buildNavBtn(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        styleNavBtn(btn, active);
        return btn;
    }

    private void styleNavBtn(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(232, 197, 104, 30));
            btn.setForeground(ACCENT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT),
                new EmptyBorder(6, 13, 6, 16)
            ));
        } else {
            btn.setBackground(new Color(0, 0, 0, 0));
            btn.setForeground(TEXT_MUT);
            btn.setBorder(new EmptyBorder(6, 16, 6, 16));
            btn.setContentAreaFilled(false);
        }
    }

    private JPanel wrapNav(JButton btn) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 10, 0, 10));
        p.add(btn, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(TEXT_MUT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(8, 18, 6, 18));
        return lbl;
    }

    // ── Navegación ────────────────────────────────────────────────────────────
    public void mostrarPantalla(String key) {
        cardLayout.show(panelCentral, key);

        String[] keys = {"motor", "usuarios", "peliculas"};
        for (int i = 0; i < navBtns.length; i++) {
            styleNavBtn(navBtns[i], keys[i].equals(key));
        }

        // Refrescar datos al entrar a cada pantalla
        switch (key) {
            case "motor"     -> pantallaMotor.refrescar();
            case "usuarios"  -> pantallaUsuarios.refrescar();
            case "peliculas" -> pantallaPeliculas.refrescar();
        }
    }

    // ── Persistencia ──────────────────────────────────────────────────────────
    private void guardarEstado() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar estado del sistema");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".dat")) path += ".dat";
                sistema.guardarEstado(path);
                mostrarInfo("Estado guardado en:\n" + path);
            } catch (Exception ex) {
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void cargarEstado() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Cargar estado del sistema");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Carga y reconstruye la ventana
                SistemaRecomendacion s2 = SistemaRecomendacion.cargarEstado(
                    fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Estado cargado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                // Reemplazar la ventana
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaPrincipal(s2).setVisible(true));
            } catch (Exception ex) {
                mostrarError("Error al cargar: " + ex.getMessage());
            }
        }
    }

    static void mostrarError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── main ─────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); }
        catch (Exception ignored) {}

        SistemaRecomendacion s = new SistemaRecomendacion();

        // Películas
        s.registrarPelicula("The Matrix",       "Sci-Fi",   1999, "Wachowskis",    4.8, "assets/imgMovies/matrix.jpg");
        s.registrarPelicula("The Godfather",    "Crime",    1972, "F. F. Coppola", 4.9, "assets/imgMovies/godfather.jpg");
        s.registrarPelicula("Interstellar",     "Sci-Fi",   2014, "C. Nolan",      4.8, "assets/imgMovies/interstellar.jpg");
        s.registrarPelicula("Parasite",         "Thriller", 2019, "Bong Joon-ho",  4.7, "assets/imgMovies/parasite.jpg");
        Pelicula pInception = s.registrarPelicula("Inception",        "Sci-Fi",   2010, "C. Nolan",      4.7, "assets/imgMovies/inception.jpg");
        Pelicula pDune      = s.registrarPelicula("Dune",             "Sci-Fi",   2021, "D. Villeneuve", 4.6, "assets/imgMovies/dune.jpg");
        Pelicula pBlade     = s.registrarPelicula("Blade Runner 2049","Sci-Fi",   2017, "D. Villeneuve", 4.5, "assets/imgMovies/bladerunner.jpg");
        s.registrarPelicula("The Dark Knight",  "Action",   2008, "C. Nolan",      4.9, "assets/imgMovies/darkknight.jpg");
        s.registrarPelicula("Pulp Fiction",     "Crime",    1994, "Q. Tarantino",  4.7, "assets/imgMovies/pulpfiction.jpg");

        // Usuarios
        Usuario u1 = s.registrarUsuario("Marty Renna",  23, "México");
        Usuario u2 = s.registrarUsuario("Luis Montoya", 28, "Colombia");
        Usuario u3 = s.registrarUsuario("Ana Fuentes",  25, "Argentina");

        // Aristas (favoritas compartidas)
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

        // Watchlist de Marty
        u1.getWatchlist().add(pInception);
        u1.getWatchlist().add(pDune);
        u1.getWatchlist().add(pBlade);

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(s).setVisible(true));
    }
}
