package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static GUI.VentanaPrincipal.*;

/**
 * PantallaMotor — Pantalla del Motor de Recomendación (BFS).
 * Layout: selector de usuario + perfil | grid de tarjetas | watchlist lateral.
 */
public class PantallaMotor extends JPanel {

    private final SistemaRecomendacion sistema;
    private Usuario usuarioActual;

    // Componentes dinámicos
    private JComboBox<String>  cboUsuario;
    private JLabel             lblNombre, lblCve, lblPais, lblEdad;
    private JPanel             panelFavoritas;
    private JPanel             panelTarjetas;
    private JPanel             panelWatchlist;
    private JLabel             lblEstadoBFS;

    public PantallaMotor(SistemaRecomendacion sistema) {
        this.sistema = sistema;
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);

        // Inicializar usuario
        if (!sistema.getListaUsuarios().isEmpty())
            usuarioActual = sistema.getListaUsuarios().get(0);

        // ── Layout principal: [contenido central | watchlist derecha] ──
        JPanel contenidoCentral = buildContenidoCentral();
        JPanel watchlistPanel   = buildWatchlistPanel();

        add(contenidoCentral, BorderLayout.CENTER);
        add(watchlistPanel,   BorderLayout.EAST);
    }

    // ── Panel Central ──────────────────────────────────────────────────────
    private JPanel buildContenidoCentral() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(28, 30, 28, 20));

        // Header
        p.add(buildHeader(), BorderLayout.NORTH);

        // Scroll con perfil + recomendaciones
        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setOpaque(false);

        scroll.add(buildPerfilCard());
        scroll.add(Box.createVerticalStrut(22));
        scroll.add(buildRecsSection());
        scroll.add(Box.createVerticalStrut(22));
        scroll.add(buildFavoritasSection());
        scroll.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);

        JLabel lbl = new JLabel("Motor de Recomendación");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        lbl.setForeground(TEXT_PRI);

        JLabel sub = new JLabel("Collaborative Filtering · Breadth-First Search");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(lbl);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);
        h.add(left, BorderLayout.WEST);

        return h;
    }

    // ── Tarjeta de Perfil ─────────────────────────────────────────────────
    private JPanel buildPerfilCard() {
        JPanel card = new RoundedPanel(14, BG_PANEL);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Avatar
        JLabel avatar = buildAvatar("MR", 50);
        card.add(avatar, BorderLayout.WEST);

        // Info usuario
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        lblNombre = makeLabel("Marty Renna", FONT_HEAD, TEXT_PRI);
        lblCve    = makeLabel("U001", FONT_SMALL, TEXT_MUT);
        info.add(lblNombre);
        info.add(Box.createVerticalStrut(3));

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        metaRow.setOpaque(false);
        lblPais = makeLabel("México", FONT_SMALL, TEXT_MUT);
        lblEdad = makeLabel("23 años", FONT_SMALL, TEXT_MUT);
        metaRow.add(lblCve);
        metaRow.add(sepMeta());
        metaRow.add(lblPais);
        metaRow.add(sepMeta());
        metaRow.add(lblEdad);
        info.add(metaRow);

        card.add(info, BorderLayout.CENTER);

        // Selector de usuario
        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
        selectorPanel.setOpaque(false);

        JLabel selLbl = makeLabel("Usuario activo", new Font("SansSerif", Font.BOLD, 10), TEXT_MUT);
        selectorPanel.add(selLbl);
        selectorPanel.add(Box.createVerticalStrut(6));

        cboUsuario = new JComboBox<>();
        cboUsuario.setFont(FONT_BODY);
        cboUsuario.setBackground(BG_CARD);
        cboUsuario.setForeground(TEXT_PRI);
        cboUsuario.setPreferredSize(new Dimension(180, 32));
        cboUsuario.setMaximumSize(new Dimension(180, 32));
        actualizarCombo();
        cboUsuario.addActionListener(e -> onUsuarioSeleccionado());
        selectorPanel.add(cboUsuario);

        card.add(selectorPanel, BorderLayout.EAST);
        return card;
    }

    // ── Sección Recomendaciones ────────────────────────────────────────────
    private JPanel buildRecsSection() {
        JPanel sec = new JPanel(new BorderLayout(0, 12));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sub-header con botón
        JPanel subHead = new JPanel(new BorderLayout());
        subHead.setOpaque(false);

        JPanel leftHead = new JPanel();
        leftHead.setLayout(new BoxLayout(leftHead, BoxLayout.Y_AXIS));
        leftHead.setOpaque(false);
        JLabel titulo = makeLabel("Recomendaciones", FONT_HEAD, TEXT_PRI);
        JLabel sub    = makeLabel("Vecinos con ≥ 2 películas en común", FONT_SMALL, TEXT_MUT);
        leftHead.add(titulo);
        leftHead.add(Box.createVerticalStrut(3));
        leftHead.add(sub);
        subHead.add(leftHead, BorderLayout.WEST);

        // Botón BFS
        JButton btnBFS = new JButton("▶  Ejecutar BFS");
        btnBFS.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBFS.setBackground(ACCENT);
        btnBFS.setForeground(new Color(20, 14, 0));
        btnBFS.setBorderPainted(false);
        btnBFS.setFocusable(false);
        btnBFS.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBFS.setBorder(new EmptyBorder(9, 20, 9, 20));
        btnBFS.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnBFS.setBackground(new Color(240, 210, 120)); }
            public void mouseExited (MouseEvent e) { btnBFS.setBackground(ACCENT); }
        });
        btnBFS.addActionListener(e -> ejecutarBFS(btnBFS));
        subHead.add(btnBFS, BorderLayout.EAST);
        sec.add(subHead, BorderLayout.NORTH);

        // Estado / label BFS
        lblEstadoBFS = makeLabel("Presiona 'Ejecutar BFS' para calcular recomendaciones.", FONT_SMALL, TEXT_MUT);
        sec.add(lblEstadoBFS, BorderLayout.CENTER);

        // Grid de tarjetas
        panelTarjetas = new JPanel(new GridLayout(1, 4, 14, 0));
        panelTarjetas.setOpaque(false);
        panelTarjetas.setPreferredSize(new Dimension(0, 320));
        mostrarPlaceholders();
        sec.add(panelTarjetas, BorderLayout.SOUTH);

        return sec;
    }

    // ── Sección Favoritas ─────────────────────────────────────────────────
    private JPanel buildFavoritasSection() {
        JPanel sec = new JPanel(new BorderLayout(0, 10));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        sec.add(makeLabel("Películas en su perfil", FONT_HEAD, TEXT_PRI), BorderLayout.NORTH);

        panelFavoritas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFavoritas.setOpaque(false);
        sec.add(panelFavoritas, BorderLayout.CENTER);
        return sec;
    }

    // ── Panel Watchlist Derecho ────────────────────────────────────────────
    private JPanel buildWatchlistPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PANEL);
        p.setPreferredSize(new Dimension(175, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COL));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(22, 12, 22, 12));

        JLabel titulo = makeLabel("Watchlist", FONT_HEAD, TEXT_PRI);
        JLabel sub    = makeLabel("Cola de pendientes", new Font("SansSerif", Font.PLAIN, 10), TEXT_MUT);
        inner.add(titulo);
        inner.add(Box.createVerticalStrut(2));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(16));

        panelWatchlist = new JPanel();
        panelWatchlist.setLayout(new BoxLayout(panelWatchlist, BoxLayout.Y_AXIS));
        panelWatchlist.setOpaque(false);

        JScrollPane sp = new JScrollPane(panelWatchlist);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        inner.add(sp);
        p.add(inner, BorderLayout.CENTER);

        // Botón procesar watchlist
        JButton btnProcesar = new JButton("Procesar siguiente");
        btnProcesar.setFont(FONT_SMALL);
        btnProcesar.setBackground(BG_CARD);
        btnProcesar.setForeground(CYAN);
        btnProcesar.setBorderPainted(false);
        btnProcesar.setFocusable(false);
        btnProcesar.setBorder(new EmptyBorder(8, 10, 8, 10));
        btnProcesar.addActionListener(e -> procesarWatchlist());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_PANEL);
        footer.setBorder(new EmptyBorder(8, 12, 12, 12));
        footer.add(btnProcesar, BorderLayout.CENTER);
        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    // ── Lógica ────────────────────────────────────────────────────────────
    private void ejecutarBFS(JButton btn) {
        if (usuarioActual == null) return;
        btn.setEnabled(false);
        btn.setText("Calculando...");

        SwingWorker<List<Pelicula>, Void> worker = new SwingWorker<>() {
            protected List<Pelicula> doInBackground() throws Exception {
                Thread.sleep(600); // Simular latencia visual
                return MotorRecomendacion.obtenerRecomendaciones(usuarioActual, sistema.getListaUsuarios());
            }
            protected void done() {
                try {
                    List<Pelicula> recs = get();
                    desplegarTarjetas(recs);
                    int n = Math.min(recs.size(), 4);
                    lblEstadoBFS.setText("BFS completado · " + n + " recomendación(es) encontradas.");
                    lblEstadoBFS.setForeground(CYAN);
                } catch (Exception ex) {
                    lblEstadoBFS.setText("Error al ejecutar BFS: " + ex.getMessage());
                    lblEstadoBFS.setForeground(Color.RED);
                } finally {
                    btn.setEnabled(true);
                    btn.setText("▶  Ejecutar BFS");
                }
            }
        };
        worker.execute();
    }

    private void desplegarTarjetas(List<Pelicula> recs) {
        panelTarjetas.removeAll();
        int mostrar = Math.min(recs.size(), 4);

        for (int i = 0; i < 4; i++) {
            if (i < mostrar) {
                Pelicula p = recs.get(i);
                int match = Math.min(100, 88 + (int)(p.getCalificacionPromedio() * 2));
                panelTarjetas.add(new TarjetaPelicula(
                    p.getNombre(),
                    String.format("%.1f", p.getCalificacionPromedio()),
                    match + "%",
                    p.getRutaImagen()
                ));
            } else {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                panelTarjetas.add(empty);
            }
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private void mostrarPlaceholders() {
        panelTarjetas.removeAll();
        for (int i = 0; i < 4; i++) {
            JPanel ph = new RoundedPanel(12, BG_CARD);
            ph.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("Sin datos", SwingConstants.CENTER);
            lbl.setForeground(TEXT_MUT);
            lbl.setFont(FONT_SMALL);
            ph.add(lbl, BorderLayout.CENTER);
            panelTarjetas.add(ph);
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private void procesarWatchlist() {
        if (usuarioActual == null) return;
        if (usuarioActual.getWatchlist().isEmpty()) {
            VentanaPrincipal.mostrarInfo("La watchlist está vacía.");
            return;
        }
        sistema.procesarSiguienteWatchlist(usuarioActual.getCve());
        refrescarWatchlist();
        refrescarFavoritas();
    }

    private void onUsuarioSeleccionado() {
        int idx = cboUsuario.getSelectedIndex();
        if (idx >= 0 && idx < sistema.getListaUsuarios().size()) {
            usuarioActual = sistema.getListaUsuarios().get(idx);
            refrescarPerfil();
            refrescarWatchlist();
            refrescarFavoritas();
            mostrarPlaceholders();
            lblEstadoBFS.setText("Usuario cambiado. Vuelve a ejecutar BFS.");
            lblEstadoBFS.setForeground(TEXT_MUT);
        }
    }

    // ── Refrescos ─────────────────────────────────────────────────────────
    public void refrescar() {
        actualizarCombo();
        if (!sistema.getListaUsuarios().isEmpty() && usuarioActual == null)
            usuarioActual = sistema.getListaUsuarios().get(0);
        refrescarPerfil();
        refrescarWatchlist();
        refrescarFavoritas();
    }

    private void actualizarCombo() {
        if (cboUsuario == null) return;
        cboUsuario.removeAllItems();
        for (Usuario u : sistema.getListaUsuarios())
            cboUsuario.addItem(u.getNombre() + " (" + u.getCve() + ")");
    }

    private void refrescarPerfil() {
        if (usuarioActual == null) return;
        lblNombre.setText(usuarioActual.getNombre());
        lblCve.setText(usuarioActual.getCve());
        lblPais.setText(usuarioActual.getPais());
        lblEdad.setText(usuarioActual.getEdad() + " años");
    }

    private void refrescarWatchlist() {
        if (panelWatchlist == null) return;
        panelWatchlist.removeAll();
        if (usuarioActual == null || usuarioActual.getWatchlist().isEmpty()) {
            JLabel empty = makeLabel("Watchlist vacía", FONT_SMALL, TEXT_MUT);
            empty.setBorder(new EmptyBorder(10, 0, 0, 0));
            panelWatchlist.add(empty);
        } else {
            for (Pelicula p : usuarioActual.getWatchlist()) {
                panelWatchlist.add(buildWLItem(p));
                panelWatchlist.add(Box.createVerticalStrut(8));
            }
        }
        panelWatchlist.revalidate();
        panelWatchlist.repaint();
    }

    private JPanel buildWLItem(Pelicula p) {
        JPanel item = new RoundedPanel(8, BG_CARD);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBorder(new EmptyBorder(8, 8, 8, 8));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel nombre = makeLabel(p.getNombre(), FONT_SMALL, TEXT_PRI);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel meta   = makeLabel(p.getGenero() + " · " + p.getAnio(), new Font("SansSerif", Font.PLAIN, 10), TEXT_MUT);

        item.add(nombre);
        item.add(Box.createVerticalStrut(3));
        item.add(meta);
        return item;
    }

    private void refrescarFavoritas() {
        if (panelFavoritas == null) return;
        panelFavoritas.removeAll();
        if (usuarioActual != null) {
            for (Pelicula p : usuarioActual.getPeliculasFavoritas()) {
                JLabel pill = new JLabel("★ " + p.getNombre());
                pill.setFont(FONT_SMALL);
                pill.setForeground(ACCENT);
                pill.setBackground(new Color(232, 197, 104, 22));
                pill.setOpaque(true);
                pill.setBorder(new EmptyBorder(4, 10, 4, 10));
                panelFavoritas.add(pill);
            }
        }
        panelFavoritas.revalidate();
        panelFavoritas.repaint();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    static JLabel buildAvatar(String iniciales, int size) {
        JLabel av = new JLabel(iniciales, SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("SansSerif", Font.BOLD, size / 3));
        av.setForeground(new Color(20, 14, 0));
        av.setPreferredSize(new Dimension(size, size));
        av.setMinimumSize(new Dimension(size, size));
        av.setMaximumSize(new Dimension(size, size));
        av.setOpaque(false);
        return av;
    }

    private JLabel sepMeta() {
        JLabel s = new JLabel("·");
        s.setForeground(new Color(80, 80, 90));
        s.setFont(FONT_SMALL);
        return s;
    }
}
