package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static GUI.VentanaPrincipal.*;
import static GUI.PantallaMotor.*;

/**
 * PantallaUsuarios — CRUD completo de usuarios.
 * Muestra tabla de usuarios, panel de detalle con favoritas y watchlist,
 * y formulario modal para crear / editar usuarios.
 */
public class PantallaUsuarios extends JPanel {

    private final SistemaRecomendacion sistema;
    private final VentanaPrincipal     ventana;

    private JTable          tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JPanel          panelDetalle;
    private Usuario         usuarioSeleccionado;

    // Labels del detalle
    private JLabel lblDetNombre, lblDetCve, lblDetPais, lblDetEdad;
    private JPanel panelDetFavs, panelDetWL, panelDetHist;

    public PantallaUsuarios(SistemaRecomendacion sistema, VentanaPrincipal ventana) {
        this.sistema  = sistema;
        this.ventana  = ventana;
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        setBorder(new EmptyBorder(28, 30, 28, 30));

        add(buildHeader(),        BorderLayout.NORTH);
        add(buildContenido(),     BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel t = makeLabel("Gestión de Usuarios", new Font("SansSerif", Font.BOLD, 26), TEXT_PRI);
        JLabel s = makeLabel("Administra los perfiles y sus conexiones en el grafo", FONT_SMALL, TEXT_MUT);
        titles.add(t); titles.add(Box.createVerticalStrut(4)); titles.add(s);
        h.add(titles, BorderLayout.WEST);

        JButton btnNuevo = buildAccentBtn("+ Nuevo usuario");
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        h.add(btnNuevo, BorderLayout.EAST);
        return h;
    }

    // ── Contenido principal ───────────────────────────────────────────────
    private JPanel buildContenido() {
        JPanel p = new JPanel(new BorderLayout(20, 0));
        p.setOpaque(false);
        p.add(buildTablaPanel(),   BorderLayout.CENTER);
        p.add(buildDetallePanel(), BorderLayout.EAST);
        return p;
    }

    // ── Tabla ─────────────────────────────────────────────────────────────
    private JPanel buildTablaPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);

        String[] cols = {"CVE", "Nombre", "Edad", "País", "Favoritas", "Watchlist"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setFont(FONT_BODY);
        tablaUsuarios.setForeground(TEXT_PRI);
        tablaUsuarios.setBackground(BG_CARD);
        tablaUsuarios.setGridColor(BORDER_COL);
        tablaUsuarios.setRowHeight(36);
        tablaUsuarios.setSelectionBackground(new Color(232, 197, 104, 40));
        tablaUsuarios.setSelectionForeground(TEXT_PRI);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setIntercellSpacing(new Dimension(0, 1));
        tablaUsuarios.setFocusable(false);
        tablaUsuarios.setFillsViewportHeight(true);

        // Header
        JTableHeader header = tablaUsuarios.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setForeground(TEXT_MUT);
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        // Anchos de columna
        int[] anchos = {55, 160, 55, 90, 80, 80};
        for (int i = 0; i < anchos.length; i++)
            tablaUsuarios.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSeleccionUsuario();
        });

        JScrollPane sp = new JScrollPane(tablaUsuarios);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        p.add(sp, BorderLayout.CENTER);

        // Botones CRUD inferiores
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnEditar  = buildSecondaryBtn("✏  Editar");
        JButton btnEliminar = buildSecondaryBtn("🗑  Eliminar");
        JButton btnAgrFav  = buildSecondaryBtn("★  Agregar favorita");
        JButton btnProcWL  = buildSecondaryBtn("▶  Procesar watchlist");

        btnEditar.addActionListener(e -> editarUsuarioSeleccionado());
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        btnAgrFav.addActionListener(e -> agregarFavorita());
        btnProcWL.addActionListener(e -> procesarWatchlist());

        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        btnPanel.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 24)); }});
        btnPanel.add(btnAgrFav);
        btnPanel.add(btnProcWL);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    // ── Panel de Detalle ──────────────────────────────────────────────────
    private JPanel buildDetallePanel() {
        panelDetalle = new JPanel();
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBackground(BG_PANEL);
        panelDetalle.setBorder(new EmptyBorder(20, 18, 20, 18));
        panelDetalle.setPreferredSize(new Dimension(240, 0));

        JLabel titulo = makeLabel("Detalle de usuario", FONT_LABEL, TEXT_MUT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(titulo);
        panelDetalle.add(Box.createVerticalStrut(16));

        // Avatar + nombre
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        avatarRow.setOpaque(false);
        avatarRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDetNombre = makeLabel("—", FONT_HEAD, TEXT_PRI);
        avatarRow.add(lblDetNombre);
        panelDetalle.add(avatarRow);

        panelDetalle.add(Box.createVerticalStrut(8));

        // Meta
        lblDetCve  = makeLabel("CVE: —",   FONT_SMALL, TEXT_MUT); lblDetCve.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDetPais = makeLabel("País: —",  FONT_SMALL, TEXT_MUT); lblDetPais.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDetEdad = makeLabel("Edad: —",  FONT_SMALL, TEXT_MUT); lblDetEdad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(lblDetCve); panelDetalle.add(Box.createVerticalStrut(3));
        panelDetalle.add(lblDetPais); panelDetalle.add(Box.createVerticalStrut(3));
        panelDetalle.add(lblDetEdad); panelDetalle.add(Box.createVerticalStrut(14));

        panelDetalle.add(separadorDet("Películas favoritas"));
        panelDetFavs = new JPanel();
        panelDetFavs.setLayout(new BoxLayout(panelDetFavs, BoxLayout.Y_AXIS));
        panelDetFavs.setOpaque(false);
        panelDetFavs.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(panelDetFavs);
        panelDetalle.add(Box.createVerticalStrut(12));

        panelDetalle.add(separadorDet("Watchlist (cola)"));
        panelDetWL = new JPanel();
        panelDetWL.setLayout(new BoxLayout(panelDetWL, BoxLayout.Y_AXIS));
        panelDetWL.setOpaque(false);
        panelDetWL.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(panelDetWL);
        panelDetalle.add(Box.createVerticalStrut(12));

        panelDetalle.add(separadorDet("Historial (pila)"));
        panelDetHist = new JPanel();
        panelDetHist.setLayout(new BoxLayout(panelDetHist, BoxLayout.Y_AXIS));
        panelDetHist.setOpaque(false);
        panelDetHist.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalle.add(panelDetHist);

        return panelDetalle;
    }

    private JLabel separadorDet(String texto) {
        JLabel l = makeLabel(texto.toUpperCase(), new Font("SansSerif", Font.BOLD, 9), TEXT_MUT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 6, 0));
        return l;
    }

    // ── Eventos ───────────────────────────────────────────────────────────
    private void onSeleccionUsuario() {
        int row = tablaUsuarios.getSelectedRow();
        if (row < 0 || row >= sistema.getListaUsuarios().size()) return;
        usuarioSeleccionado = sistema.getListaUsuarios().get(row);
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        if (usuarioSeleccionado == null) return;
        Usuario u = usuarioSeleccionado;
        lblDetNombre.setText(u.getNombre());
        lblDetCve.setText("CVE:  " + u.getCve());
        lblDetPais.setText("País:  " + u.getPais());
        lblDetEdad.setText("Edad:  " + u.getEdad() + " años");

        List<String> favNames  = new ArrayList<>();
        for (Pelicula p : u.getPeliculasFavoritas()) favNames.add(p.getNombre());
        List<String> wlNames   = new ArrayList<>();
        for (Pelicula p : u.getWatchlist())          wlNames.add(p.getNombre());
        List<String> histNames = new ArrayList<>();
        for (Pelicula p : u.getHistorialVistas())    histNames.add(p.getNombre());

        llenarListaDetalle(panelDetFavs,  favNames,  "★", ACCENT);
        llenarListaDetalle(panelDetWL,    wlNames,   "⏳", CYAN);
        llenarListaDetalle(panelDetHist,  histNames, "✔", TEXT_MUT);

        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    private void llenarListaDetalle(JPanel panel, List<String> items, String icon, Color color) {
        panel.removeAll();
        if (items.isEmpty()) {
            JLabel empty = makeLabel("(vacío)", FONT_SMALL, TEXT_MUT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(empty);
        } else {
            for (String item : items) {
                JLabel l = makeLabel(icon + "  " + item, FONT_SMALL, color);
                l.setAlignmentX(Component.LEFT_ALIGNMENT);
                l.setBorder(new EmptyBorder(0, 0, 4, 0));
                panel.add(l);
            }
        }
    }

    private void editarUsuarioSeleccionado() {
        if (usuarioSeleccionado == null) { VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return; }
        abrirFormulario(usuarioSeleccionado);
    }

    private void eliminarUsuarioSeleccionado() {
        if (usuarioSeleccionado == null) { VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al usuario " + usuarioSeleccionado.getNombre() + "?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            sistema.getListaUsuarios().remove(usuarioSeleccionado);
            usuarioSeleccionado = null;
            refrescar();
        }
    }

    private void agregarFavorita() {
        if (usuarioSeleccionado == null) { VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return; }
        List<Pelicula> pelis = sistema.getListaPeliculas();
        if (pelis.isEmpty()) { VentanaPrincipal.mostrarError("No hay películas registradas."); return; }

        String[] opciones = pelis.stream().map(p -> p.getCve() + " — " + p.getNombre()).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this,
            "Selecciona una película para agregar a favoritas de " + usuarioSeleccionado.getNombre() + ":",
            "Agregar favorita", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;

        String cve = sel.split(" — ")[0];
        Pelicula p = sistema.buscarPelicula(cve);
        if (p != null && !usuarioSeleccionado.getPeliculasFavoritas().contains(p)) {
            usuarioSeleccionado.getPeliculasFavoritas().add(p);
            actualizarDetalle();
            refrescarTabla();
        }
    }

    private void procesarWatchlist() {
        if (usuarioSeleccionado == null) { VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return; }
        if (usuarioSeleccionado.getWatchlist().isEmpty()) { VentanaPrincipal.mostrarInfo("La watchlist está vacía."); return; }
        sistema.procesarSiguienteWatchlist(usuarioSeleccionado.getCve());
        actualizarDetalle();
        refrescarTabla();
        VentanaPrincipal.mostrarInfo("Película procesada y añadida al historial y favoritas.");
    }

    // ── Formulario Modal ──────────────────────────────────────────────────
    private void abrirFormulario(Usuario usuarioEditar) {
        boolean esNuevo = (usuarioEditar == null);
        JDialog dialog = new JDialog(ventana, esNuevo ? "Nuevo usuario" : "Editar usuario", true);
        dialog.setSize(400, 290);
        dialog.setLocationRelativeTo(ventana);
        dialog.getContentPane().setBackground(BG_PANEL);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_PANEL);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtNombre = buildField(esNuevo ? "" : usuarioEditar.getNombre());
        JTextField txtEdad   = buildField(esNuevo ? "" : String.valueOf(usuarioEditar.getEdad()));
        JTextField txtPais   = buildField(esNuevo ? "" : usuarioEditar.getPais());

        Object[][] campos = {
            {"Nombre:", txtNombre},
            {"Edad:",   txtEdad},
            {"País:",   txtPais}
        };
        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel l = makeLabel((String) campos[i][0], FONT_LABEL, TEXT_MUT);
            form.add(l, gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            form.add((JComponent) campos[i][1], gbc);
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        }
        dialog.add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botones.setBackground(BG_PANEL);
        JButton btnCancelar = buildSecondaryBtn("Cancelar");
        JButton btnGuardar  = buildAccentBtn("Guardar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String paisTxt = txtPais.getText().trim();
            int edad;
            try { edad = Integer.parseInt(txtEdad.getText().trim()); }
            catch (NumberFormatException ex) { VentanaPrincipal.mostrarError("Edad debe ser un número entero."); return; }
            if (nombre.isEmpty() || paisTxt.isEmpty()) { VentanaPrincipal.mostrarError("Nombre y país son obligatorios."); return; }

            if (esNuevo) {
                sistema.registrarUsuario(nombre, edad, paisTxt);
            } else {
                usuarioEditar.setEdad(edad);
                usuarioEditar.setPais(paisTxt);
                // Nota: el nombre no se cambia para preservar la clave
            }
            refrescar();
            dialog.dispose();
        });
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        dialog.add(botones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Refrescos ─────────────────────────────────────────────────────────
    public void refrescar() {
        refrescarTabla();
        actualizarDetalle();
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Usuario u : sistema.getListaUsuarios()) {
            modeloTabla.addRow(new Object[]{
                u.getCve(),
                u.getNombre(),
                u.getEdad(),
                u.getPais(),
                u.getPeliculasFavoritas().size(),
                u.getWatchlist().size()
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JTextField buildField(String value) {
        JTextField f = new JTextField(value, 18);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRI);
        f.setCaretColor(TEXT_PRI);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    static JButton buildAccentBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(ACCENT);
        btn.setForeground(new Color(20, 14, 0));
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    static JButton buildSecondaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_PRI);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL),
            new EmptyBorder(6, 14, 6, 14)
        ));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
