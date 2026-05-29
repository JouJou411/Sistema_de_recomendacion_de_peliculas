package GUI;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * VentanaPrincipal — Contenedor raiz con sidebar de navegacion y CardLayout
 */
public class VentanaPrincipal extends JFrame {

    // ── Paleta ──────────────────────────────────────────────────────────────
    static final Color BG_DEEP    = new Color(10,  10,  15);
    static final Color BG_PANEL   = new Color(17,  17,  26);
    static final Color BG_CARD    = new Color(24,  24,  36);
    static final Color ACCENT     = new Color(232, 197, 104);
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
    private final CardLayout cardLayout    = new CardLayout();
    private final JPanel     panelCentral  = new JPanel(cardLayout);
    private JButton[]        navBtns;

    // ── Pantallas ────────────────────────────────────────────────────────────
    private PantallaMotor     pantallaMotor;
    private PantallaUsuarios  pantallaUsuarios;
    private PantallaPeliculas pantallaPeliculas;

    // ── Constructor ──────────────────────────────────────────────────────────
    public VentanaPrincipal(SistemaRecomendacion sistema) {
        this.sistema = sistema;

        setTitle("CineMatch — Sistema de Recomendación Cinematográfica");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Controlamos el cierre nosotros
        setSize(1280, 760);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);
        setLayout(new BorderLayout());

        // ── Auto-guardar al cerrar ────────────────────────────────────────
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sistema.guardarEstadoAuto();
                System.out.println("[CineMatch] Aplicación cerrada correctamente.");
                System.exit(0);
            }
        });

        // ── Pantallas ─────────────────────────────────────────────────────
        pantallaMotor     = new PantallaMotor(sistema);
        pantallaUsuarios  = new PantallaUsuarios(sistema, this);
        pantallaPeliculas = new PantallaPeliculas(sistema, this);

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

        // Navegación principal
        sidebar.add(sectionLabel("Navegación"));

        String[][] items = {
            {"motor",     "⚡  Motor BFS"},
            {"usuarios",  "👥  Usuarios"},
            {"peliculas", "🎬  Películas"},
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

        // Sección persistencia (solo exportar manualmente)
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sectionLabel("Datos"));

        JButton btnExportar = buildNavBtn("💾  Exportar copia", false);
        btnExportar.addActionListener(e -> exportarCopia());
        sidebar.add(wrapNav(btnExportar));
        sidebar.add(Box.createVerticalStrut(2));

        JButton btnImportar = buildNavBtn("📂  Importar copia", false);
        btnImportar.addActionListener(e -> importarCopia());
        sidebar.add(wrapNav(btnImportar));

        sidebar.add(Box.createVerticalGlue());

        // Indicador de auto-guardado
        JPanel autoSaveIndicator = getAutoSaveIndicator();
        sidebar.add(autoSaveIndicator);

        JLabel ver = new JLabel("v2.1 — BFS Engine");
        ver.setFont(FONT_SMALL);
        ver.setForeground(TEXT_MUT);
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        ver.setBorder(new EmptyBorder(4, 18, 18, 18));
        sidebar.add(ver);

        return sidebar;
    }

    private static JPanel getAutoSaveIndicator() {
        JPanel autoSaveIndicator = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        autoSaveIndicator.setOpaque(false);
        autoSaveIndicator.setBorder(new EmptyBorder(0, 14, 4, 14));
        autoSaveIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dotVerde = new JLabel("●");
        dotVerde.setFont(new Font("SansSerif", Font.PLAIN, 9));
        dotVerde.setForeground(new Color(78, 205, 100));
        JLabel autoLbl = new JLabel("Auto-guardado activo");
        autoLbl.setFont(new Font("SansSerif", Font.PLAIN, 9));
        autoLbl.setForeground(TEXT_MUT);
        autoSaveIndicator.add(dotVerde);
        autoSaveIndicator.add(autoLbl);
        return autoSaveIndicator;
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
        for (int i = 0; i < navBtns.length; i++)
            styleNavBtn(navBtns[i], keys[i].equals(key));

        switch (key) {
            case "motor"     -> pantallaMotor.refrescar();
            case "usuarios"  -> pantallaUsuarios.refrescar();
            case "peliculas" -> pantallaPeliculas.refrescar();
        }
    }

    // ── Exportar / Importar manual (copia de seguridad) ───────────────────────
    private void exportarCopia() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Exportar copia de seguridad");
        fc.setSelectedFile(new java.io.File("cinematch_backup.dat"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".dat")) path += ".dat";
                sistema.guardarEstado(path);
                mostrarInfo("Copia exportada en:\n" + path);
            } catch (Exception ex) {
                mostrarError("Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void importarCopia() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Importar copia de seguridad");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                SistemaRecomendacion s2 = SistemaRecomendacion.cargarEstado(
                    fc.getSelectedFile().getAbsolutePath());
                mostrarInfo("Copia importada correctamente.");
                dispose();
                SwingUtilities.invokeLater(() -> new VentanaPrincipal(s2).setVisible(true));
            } catch (Exception ex) {
                mostrarError("Error al importar: " + ex.getMessage());
            }
        }
    }

    // ── Helpers de diálogo ───────────────────────────────────────────────────
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

        // Carga automática desde resources/archivos/cinematch.dat
        // Si no existe el archivo, cargarOInicializar() devuelve datos de ejemplo.
        SistemaRecomendacion sistema = SistemaRecomendacion.cargarOInicializar();

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema).setVisible(true));
    }
}
