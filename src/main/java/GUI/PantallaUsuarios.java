package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static GUI.VentanaPrincipal.*;
import static GUI.PantallaMotor.*;

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

        JButton btnEditar    = buildSecondaryBtn("✏  Editar");
        JButton btnEliminar  = buildSecondaryBtn("🗑  Eliminar");
        JButton btnAgrFav    = buildSecondaryBtn("★  Agregar favorita");
        JButton btnQuitarFav = buildSecondaryBtn("✕  Quitar favorita");
        JButton btnProcWL    = buildSecondaryBtn("▶  Procesar watchlist");

        btnEditar.addActionListener(e -> editarUsuarioSeleccionado());
        btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());
        btnAgrFav.addActionListener(e -> agregarFavorita());
        btnQuitarFav.addActionListener(e -> quitarFavorita());
        btnProcWL.addActionListener(e -> procesarWatchlist());

        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        btnPanel.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 24)); }});
        btnPanel.add(btnAgrFav);
        btnPanel.add(btnQuitarFav);
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

        llenarFavoritasConBoton();
        llenarListaDetalle(panelDetWL,    wlNames,   "⏳", CYAN);
        llenarListaDetalle(panelDetHist,  histNames, "✔", TEXT_MUT);

        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    /**
     * Rellena un panel vertical con items de texto simple (watchlist, historial).
     */
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

    /**
     * Rellena el panel de favoritas con una fila por película:
     * icono + nombre a la izquierda  y  botón [×] a la derecha para quitar.
     */
    private void llenarFavoritasConBoton() {
        panelDetFavs.removeAll();
        if (usuarioSeleccionado == null
                || usuarioSeleccionado.getPeliculasFavoritas().isEmpty()) {
            JLabel empty = makeLabel("(vacío)", FONT_SMALL, TEXT_MUT);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelDetFavs.add(empty);
        } else {
            // Iteramos sobre una copia para poder modificar la lista original
            List<Pelicula> copia = new ArrayList<>(usuarioSeleccionado.getPeliculasFavoritas());
            for (Pelicula p : copia) {
                JPanel fila = new JPanel(new BorderLayout(4, 0));
                fila.setOpaque(false);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
                fila.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel lbl = makeLabel("★  " + p.getNombre(), FONT_SMALL, ACCENT);
                fila.add(lbl, BorderLayout.CENTER);

                JButton btnX = new JButton("×");
                btnX.setFont(new Font("SansSerif", Font.BOLD, 11));
                btnX.setForeground(new Color(200, 80, 80));
                btnX.setBackground(new Color(200, 80, 80, 22));
                btnX.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
                btnX.setFocusable(false);
                btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnX.setToolTipText("Quitar de favoritas");
                btnX.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(ventana,
                            "¿Quitar '" + p.getNombre() + "' de las favoritas de "
                                    + usuarioSeleccionado.getNombre() + "?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        usuarioSeleccionado.getPeliculasFavoritas().remove(p);
                        llenarFavoritasConBoton();
                        panelDetalle.revalidate();
                        panelDetalle.repaint();
                        refrescarTabla();
                    }
                });
                fila.add(btnX, BorderLayout.EAST);
                panelDetFavs.add(fila);
                panelDetFavs.add(Box.createVerticalStrut(3));
            }
        }
        panelDetFavs.revalidate();
        panelDetFavs.repaint();
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
        if (usuarioSeleccionado == null) {
            VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return;
        }
        if (sistema.getListaPeliculas().isEmpty()) {
            VentanaPrincipal.mostrarError("No hay películas registradas."); return;
        }
        abrirDialogoAgregarFavorita();
    }

    /**
     * Diálogo propio con tabla, búsqueda en tiempo real y doble clic.
     * Muestra todas las películas y marca cuáles ya son favoritas del usuario.
     */
    private void abrirDialogoAgregarFavorita() {
        JDialog dialog = new JDialog(ventana,
                "Agregar favorita — " + usuarioSeleccionado.getNombre(), true);
        dialog.setSize(560, 420);
        dialog.setLocationRelativeTo(ventana);
        dialog.getContentPane().setBackground(BG_PANEL);
        dialog.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBackground(BG_PANEL);
        top.setBorder(new EmptyBorder(16, 20, 10, 20));
        JLabel titulo = makeLabel(
                "Selecciona pelicula para  ★  " + usuarioSeleccionado.getNombre(),
                FONT_HEAD, TEXT_PRI);
        top.add(titulo, BorderLayout.WEST);

        // Campo busqueda
        JTextField txtBuscar = new JTextField(14);
        txtBuscar.setFont(FONT_BODY);
        txtBuscar.setBackground(BG_CARD);
        txtBuscar.setForeground(TEXT_PRI);
        txtBuscar.setCaretColor(TEXT_PRI);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                new EmptyBorder(5, 10, 5, 10)));
        top.add(txtBuscar, BorderLayout.EAST);
        dialog.add(top, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"CVE", "Titulo", "Genero", "Anno", "Director", "Cal.", "Favorita"};
        DefaultTableModel modeloDlg = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) { return c == 6 ? Boolean.class : String.class; }
        };
        for (Pelicula p : sistema.getListaPeliculas()) {
            boolean yaFav = usuarioSeleccionado.getPeliculasFavoritas().contains(p);
            modeloDlg.addRow(new Object[]{
                    p.getCve(), p.getNombre(), p.getGenero(),
                    String.valueOf(p.getAnio()), p.getDirector(),
                    String.format("%.1f", p.getCalificacionPromedio()), yaFav
            });
        }
        JTable tablaDlg = getTablaDlg(modeloDlg, 232, 197, 104);

        // Render columna Favorita
        tablaDlg.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                boolean esFav = Boolean.TRUE.equals(val);
                JLabel l = new JLabel(esFav ? "★  Si" : "—");
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                l.setForeground(esFav ? ACCENT : TEXT_MUT);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(232,197,104,50) : BG_CARD);
                l.setBorder(new EmptyBorder(0, 4, 0, 4));
                return l;
            }
        });
        int[] anchos = {50, 150, 75, 45, 115, 40, 80};
        for (int i = 0; i < anchos.length; i++)
            tablaDlg.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JTableHeader header = tablaDlg.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setForeground(TEXT_MUT);
        header.setBackground(BG_PANEL);

        JScrollPane sp = new JScrollPane(tablaDlg);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COL));
        dialog.add(sp, BorderLayout.CENTER);

        // Filtro en tiempo real
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloDlg);
        tablaDlg.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void filtrar() {
                String txt = txtBuscar.getText().trim();
                try { sorter.setRowFilter(txt.isEmpty() ? null : RowFilter.regexFilter("(?i)" + txt)); }
                catch (Exception ignored) {}
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        // Pie
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(BG_PANEL);
        footer.setBorder(new EmptyBorder(10, 20, 14, 20));
        JLabel hint = makeLabel("Doble clic o boton Agregar para confirmar", FONT_SMALL, TEXT_MUT);
        footer.add(hint, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnCancelar = buildSecondaryBtn("Cancelar");
        JButton btnAgregar  = buildAccentBtn("★  Agregar favorita");
        btnCancelar.addActionListener(e -> dialog.dispose());

        Runnable accionAgregar = () -> {
            int viewRow = tablaDlg.getSelectedRow();
            if (viewRow < 0) { VentanaPrincipal.mostrarError("Selecciona una pelicula primero."); return; }
            int modelRow = tablaDlg.convertRowIndexToModel(viewRow);
            String cve = (String) modeloDlg.getValueAt(modelRow, 0);
            Pelicula p = sistema.buscarPelicula(cve);
            if (p == null) return;
            if (usuarioSeleccionado.getPeliculasFavoritas().contains(p)) {
                VentanaPrincipal.mostrarInfo("'" + p.getNombre() + "' ya es favorita.");
                return;
            }
            usuarioSeleccionado.getPeliculasFavoritas().add(p);
            modeloDlg.setValueAt(Boolean.TRUE, modelRow, 6);
            actualizarDetalle();
            refrescarTabla();
            VentanaPrincipal.mostrarInfo("'" + p.getNombre() + "' agregada a favoritas  ★");
        };

        btnAgregar.addActionListener(e -> accionAgregar.run());
        tablaDlg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) accionAgregar.run();
            }
        });

        btnRow.add(btnCancelar);
        btnRow.add(btnAgregar);
        footer.add(btnRow, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Abre un diálogo con las películas favoritas actuales del usuario
     * para seleccionar cuál quitar. Alternativa al botón × del panel lateral.
     */
    private void quitarFavorita() {
        if (usuarioSeleccionado == null) {
            VentanaPrincipal.mostrarError("Selecciona un usuario primero."); return;
        }
        if (usuarioSeleccionado.getPeliculasFavoritas().isEmpty()) {
            VentanaPrincipal.mostrarInfo("Este usuario no tiene favoritas."); return;
        }

        JDialog dialog = new JDialog(ventana,
                "Quitar favorita — " + usuarioSeleccionado.getNombre(), true);
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(ventana);
        dialog.getContentPane().setBackground(BG_PANEL);
        dialog.setLayout(new BorderLayout(0, 0));

        // Header
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PANEL);
        top.setBorder(new EmptyBorder(16, 20, 10, 20));
        top.add(makeLabel("Selecciona la favorita a quitar  ✕", FONT_HEAD, TEXT_PRI),
                BorderLayout.WEST);
        dialog.add(top, BorderLayout.NORTH);

        // Tabla con favoritas actuales
        String[] cols = {"CVE", "Título", "Género", "Año", "Director", "Cal."};
        DefaultTableModel modeloDlg = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Pelicula p : usuarioSeleccionado.getPeliculasFavoritas()) {
            modeloDlg.addRow(new Object[]{
                    p.getCve(), p.getNombre(), p.getGenero(),
                    String.valueOf(p.getAnio()), p.getDirector(),
                    String.format("%.1f", p.getCalificacionPromedio())
            });
        }
        JTable tablaDlg = getTablaDlg(modeloDlg, 200, 80, 80);

        JTableHeader header = tablaDlg.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setForeground(TEXT_MUT);
        header.setBackground(BG_PANEL);
        int[] anchos = {50, 160, 75, 45, 120, 40};
        for (int i = 0; i < anchos.length; i++)
            tablaDlg.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JScrollPane sp = new JScrollPane(tablaDlg);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_COL));
        dialog.add(sp, BorderLayout.CENTER);

        // Pie
        JPanel footer = new JPanel(new BorderLayout(10, 0));
        footer.setBackground(BG_PANEL);
        footer.setBorder(new EmptyBorder(10, 20, 14, 20));
        footer.add(makeLabel("Doble clic o botón Quitar para confirmar", FONT_SMALL, TEXT_MUT),
                BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnCancelar = buildSecondaryBtn("Cancelar");
        JButton btnQuitar   = new JButton("✕  Quitar favorita");
        btnQuitar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnQuitar.setBackground(new Color(180, 50, 50));
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setBorderPainted(false);
        btnQuitar.setFocusable(false);
        btnQuitar.setBorder(new EmptyBorder(8, 18, 8, 18));
        btnCancelar.addActionListener(e -> dialog.dispose());

        Runnable accionQuitar = () -> {
            int viewRow = tablaDlg.getSelectedRow();
            if (viewRow < 0) { VentanaPrincipal.mostrarError("Selecciona una película primero."); return; }
            int modelRow = tablaDlg.convertRowIndexToModel(viewRow);
            String cve = (String) modeloDlg.getValueAt(modelRow, 0);
            Pelicula p = sistema.buscarPelicula(cve);
            if (p == null) return;
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "¿Quitar '" + p.getNombre() + "' de las favoritas de "
                            + usuarioSeleccionado.getNombre() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            usuarioSeleccionado.getPeliculasFavoritas().remove(p);
            modeloDlg.removeRow(modelRow);
            llenarFavoritasConBoton();
            panelDetalle.revalidate();
            panelDetalle.repaint();
            refrescarTabla();
            if (usuarioSeleccionado.getPeliculasFavoritas().isEmpty()) dialog.dispose();
        };

        btnQuitar.addActionListener(e -> accionQuitar.run());
        tablaDlg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) accionQuitar.run();
            }
        });

        btnRow.add(btnCancelar);
        btnRow.add(btnQuitar);
        footer.add(btnRow, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static JTable getTablaDlg(DefaultTableModel modeloDlg, int r, int g, int g1) {
        JTable tablaDlg = new JTable(modeloDlg);
        tablaDlg.setFont(FONT_BODY);
        tablaDlg.setForeground(TEXT_PRI);
        tablaDlg.setBackground(BG_CARD);
        tablaDlg.setGridColor(BORDER_COL);
        tablaDlg.setRowHeight(32);
        tablaDlg.setSelectionBackground(new Color(r, g, g1, 50));
        tablaDlg.setSelectionForeground(TEXT_PRI);
        tablaDlg.setShowVerticalLines(false);
        tablaDlg.setFillsViewportHeight(true);
        tablaDlg.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tablaDlg;
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