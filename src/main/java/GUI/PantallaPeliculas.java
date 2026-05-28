package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static GUI.VentanaPrincipal.*;
import static GUI.PantallaMotor.*;
import static GUI.PantallaUsuarios.*;

/**
 * PantallaPeliculas — Catálogo completo de películas con CRUD.
 * Tabla de películas | panel de detalle con póster | formulario modal.
 */
public class PantallaPeliculas extends JPanel {

    private final SistemaRecomendacion sistema;
    private final VentanaPrincipal     ventana;

    private JTable              tabla;
    private DefaultTableModel   modelo;
    private Pelicula            peliculaSeleccionada;

    // Detalle
    private JLabel lblDetTitulo, lblDetCve, lblDetGenero, lblDetAnio,
                   lblDetDirector, lblDetCal, lblDetEstado;
    private JLabel lblPoster;
    private JButton btnToggleEstado;

    public PantallaPeliculas(SistemaRecomendacion sistema, VentanaPrincipal ventana) {
        this.sistema = sistema;
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        setBorder(new EmptyBorder(28, 30, 28, 30));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildContenido(), BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(makeLabel("Catálogo de Películas", new Font("SansSerif", Font.BOLD, 26), TEXT_PRI));
        titles.add(Box.createVerticalStrut(4));
        titles.add(makeLabel("Gestiona el catálogo · Activa / desactiva licencias", FONT_SMALL, TEXT_MUT));
        h.add(titles, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton btnBuscar = buildSecondaryBtn("🔍 Buscar");
        btnBuscar.addActionListener(e -> buscarPelicula());

        JButton btnNueva = buildAccentBtn("+ Nueva película");
        btnNueva.addActionListener(e -> abrirFormulario(null));

        btnRow.add(btnBuscar);
        btnRow.add(btnNueva);
        h.add(btnRow, BorderLayout.EAST);
        return h;
    }

    // ── Contenido ─────────────────────────────────────────────────────────
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

        String[] cols = {"CVE", "Título", "Género", "Año", "Director", "Cal.", "Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setFont(FONT_BODY);
        tabla.setForeground(TEXT_PRI);
        tabla.setBackground(BG_CARD);
        tabla.setGridColor(BORDER_COL);
        tabla.setRowHeight(36);
        tabla.setSelectionBackground(new Color(78, 205, 196, 40));
        tabla.setSelectionForeground(TEXT_PRI);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFocusable(false);
        tabla.setFillsViewportHeight(true);

        // Render especial para columna Estado
        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(Boolean.TRUE.equals(val) ? "● Activa" : "○ Inactiva");
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setForeground(Boolean.TRUE.equals(val) ? new Color(78, 205, 100) : new Color(180, 60, 60));
                lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
                lbl.setOpaque(true);
                lbl.setBackground(sel ? new Color(78, 205, 196, 40) : BG_CARD);
                return lbl;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setForeground(TEXT_MUT);
        header.setBackground(BG_PANEL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        int[] anchos = {50, 170, 80, 50, 130, 45, 75};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSeleccion();
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        p.add(sp, BorderLayout.CENTER);

        // Botones CRUD
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnEditar   = buildSecondaryBtn("✏  Editar");
        JButton btnEliminar = buildSecondaryBtn("🗑  Eliminar");
        JButton btnWL       = buildSecondaryBtn("+ Agregar a watchlist");

        btnEditar.addActionListener(e -> editarSeleccionada());
        btnEliminar.addActionListener(e -> eliminarSeleccionada());
        btnWL.addActionListener(e -> agregarAWatchlist());

        btnPanel.add(btnEditar);
        btnPanel.add(btnEliminar);
        btnPanel.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 24)); }});
        btnPanel.add(btnWL);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    // ── Panel Detalle ──────────────────────────────────────────────────────
    private JPanel buildDetallePanel() {
        JPanel det = new JPanel();
        det.setLayout(new BoxLayout(det, BoxLayout.Y_AXIS));
        det.setBackground(BG_PANEL);
        det.setBorder(new EmptyBorder(20, 18, 20, 18));
        det.setPreferredSize(new Dimension(240, 0));

        // Etiqueta superior
        JLabel titulo = makeLabel("Detalle de película", FONT_LABEL, TEXT_MUT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        det.add(titulo);
        det.add(Box.createVerticalStrut(14));

        // Póster placeholder
        lblPoster = new JLabel("Sin póster", SwingConstants.CENTER);
        lblPoster.setFont(FONT_SMALL);
        lblPoster.setForeground(TEXT_MUT);
        lblPoster.setHorizontalAlignment(SwingConstants.CENTER);
        lblPoster.setBackground(BG_CARD);
        lblPoster.setOpaque(true);
        lblPoster.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        lblPoster.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPoster.setPreferredSize(new Dimension(200, 130));
        lblPoster.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        det.add(lblPoster);
        det.add(Box.createVerticalStrut(14));

        // Campos
        lblDetTitulo   = fieldLabel("—");
        lblDetCve      = metaLabel("CVE: —");
        lblDetGenero   = metaLabel("Género: —");
        lblDetAnio     = metaLabel("Año: —");
        lblDetDirector = metaLabel("Director: —");
        lblDetCal      = metaLabel("Calificación: —");

        det.add(lblDetTitulo); det.add(Box.createVerticalStrut(6));
        det.add(lblDetCve);    det.add(Box.createVerticalStrut(3));
        det.add(lblDetGenero); det.add(Box.createVerticalStrut(3));
        det.add(lblDetAnio);   det.add(Box.createVerticalStrut(3));
        det.add(lblDetDirector); det.add(Box.createVerticalStrut(3));
        det.add(lblDetCal);    det.add(Box.createVerticalStrut(12));

        // Estado badge + botón toggle
        lblDetEstado = metaLabel("Estado: —");
        lblDetEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        det.add(lblDetEstado);
        det.add(Box.createVerticalStrut(8));

        btnToggleEstado = buildSecondaryBtn("Cambiar estado");
        btnToggleEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnToggleEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnToggleEstado.addActionListener(e -> toggleEstado());
        det.add(btnToggleEstado);

        return det;
    }

    private JLabel fieldLabel(String t) {
        JLabel l = makeLabel(t, FONT_HEAD, TEXT_PRI);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel metaLabel(String t) {
        JLabel l = makeLabel(t, FONT_SMALL, TEXT_MUT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Lógica ────────────────────────────────────────────────────────────
    private void onSeleccion() {
        int row = tabla.getSelectedRow();
        if (row < 0 || row >= sistema.getListaPeliculas().size()) return;
        peliculaSeleccionada = sistema.getListaPeliculas().get(row);
        actualizarDetalle();
    }

    private void actualizarDetalle() {
        if (peliculaSeleccionada == null) return;
        Pelicula p = peliculaSeleccionada;
        lblDetTitulo.setText(p.getNombre());
        lblDetCve.setText("CVE: " + p.getCve());
        lblDetGenero.setText("Género: " + p.getGenero());
        lblDetAnio.setText("Año: " + p.getAnio());
        lblDetDirector.setText("Director: " + p.getDirector());
        lblDetCal.setText("Calificación: " + String.format("%.1f", p.getCalificacionPromedio()) + " / 5 ⭐");

        boolean activa = p.isActiva();
        lblDetEstado.setText("Estado: " + (activa ? "● Activa" : "○ Inactiva"));
        lblDetEstado.setForeground(activa ? new Color(78, 205, 100) : new Color(200, 80, 80));
        btnToggleEstado.setText(activa ? "Desactivar" : "Activar");

        // Poster
        File img = new File(p.getRutaImagen());
        if (img.exists()) {
            ImageIcon ic = new ImageIcon(p.getRutaImagen());
            Image scaled = ic.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
            lblPoster.setIcon(new ImageIcon(scaled));
            lblPoster.setText(null);
        } else {
            lblPoster.setIcon(null);
            lblPoster.setText("<html><center>Sin póster<br><small>" + p.getNombre() + "</small></center></html>");
        }
    }

    private void editarSeleccionada() {
        if (peliculaSeleccionada == null) { VentanaPrincipal.mostrarError("Selecciona una película primero."); return; }
        abrirFormulario(peliculaSeleccionada);
    }

    private void eliminarSeleccionada() {
        if (peliculaSeleccionada == null) { VentanaPrincipal.mostrarError("Selecciona una película primero."); return; }
        int c = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la película '" + peliculaSeleccionada.getNombre() + "'?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            sistema.getListaPeliculas().remove(peliculaSeleccionada);
            peliculaSeleccionada = null;
            refrescar();
        }
    }

    private void toggleEstado() {
        if (peliculaSeleccionada == null) return;
        sistema.cambiarEstatusPelicula(peliculaSeleccionada.getCve(), !peliculaSeleccionada.isActiva());
        actualizarDetalle();
        refrescarTabla();
    }

    private void agregarAWatchlist() {
        if (peliculaSeleccionada == null) { VentanaPrincipal.mostrarError("Selecciona una película primero."); return; }
        List<Usuario> usuarios = sistema.getListaUsuarios();
        if (usuarios.isEmpty()) { VentanaPrincipal.mostrarError("No hay usuarios registrados."); return; }

        String[] opciones = usuarios.stream().map(u -> u.getCve() + " — " + u.getNombre()).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this,
            "Agregar '" + peliculaSeleccionada.getNombre() + "' a la watchlist de:",
            "Agregar a Watchlist", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (sel == null) return;

        String cve = sel.split(" — ")[0];
        Usuario u = sistema.buscarUsuario(cve);
        if (u != null && !u.getWatchlist().contains(peliculaSeleccionada)) {
            u.getWatchlist().add(peliculaSeleccionada);
            VentanaPrincipal.mostrarInfo("'" + peliculaSeleccionada.getNombre() + "' agregada a la watchlist de " + u.getNombre());
        } else {
            VentanaPrincipal.mostrarInfo("La película ya está en la watchlist de ese usuario.");
        }
    }

    private void buscarPelicula() {
        String texto = JOptionPane.showInputDialog(this, "Buscar película por CVE o nombre:", "Buscar", JOptionPane.PLAIN_MESSAGE);
        if (texto == null || texto.isBlank()) return;
        String lower = texto.trim().toLowerCase();

        for (int i = 0; i < sistema.getListaPeliculas().size(); i++) {
            Pelicula p = sistema.getListaPeliculas().get(i);
            if (p.getCve().toLowerCase().contains(lower) || p.getNombre().toLowerCase().contains(lower)) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
                return;
            }
        }
        VentanaPrincipal.mostrarInfo("No se encontró ninguna película con ese criterio.");
    }

    // ── Formulario Modal ──────────────────────────────────────────────────
    private void abrirFormulario(Pelicula editar) {
        boolean esNueva = (editar == null);
        JDialog dialog = new JDialog(ventana, esNueva ? "Nueva película" : "Editar película", true);
        dialog.setSize(460, 410);
        dialog.setLocationRelativeTo(ventana);
        dialog.getContentPane().setBackground(BG_PANEL);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_PANEL);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField txtNombre    = buildField(esNueva ? "" : editar.getNombre(), 22);
        JTextField txtGenero    = buildField(esNueva ? "" : editar.getGenero(), 22);
        JTextField txtAnio      = buildField(esNueva ? "" : String.valueOf(editar.getAnio()), 22);
        JTextField txtDirector  = buildField(esNueva ? "" : editar.getDirector(), 22);
        JTextField txtCal       = buildField(esNueva ? "" : String.valueOf(editar.getCalificacionPromedio()), 22);
        JLabel     lblImgPath   = makeLabel(esNueva ? "(ninguna imagen seleccionada)" : editar.getRutaImagen(), FONT_SMALL, TEXT_MUT);
        final String[] rutaImg  = { esNueva ? "" : editar.getRutaImagen() };

        JButton btnSelImg = buildSecondaryBtn("Seleccionar imagen...");
        btnSelImg.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg","jpeg","png","gif","webp"));
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File archivo = fc.getSelectedFile();
                // Si hay CVE disponible (editando), copiar con GestorImagenes
                String cveTmp = esNueva ? "TEMP" : editar.getCve();
                try {
                    rutaImg[0] = GestorImagenes.guardarImagenPelicula(archivo, cveTmp);
                    lblImgPath.setText(rutaImg[0]);
                } catch (IOException ex) {
                    rutaImg[0] = archivo.getAbsolutePath();
                    lblImgPath.setText(archivo.getName());
                }
            }
        });

        Object[][] campos = {
            {"Título:",        txtNombre},
            {"Género:",        txtGenero},
            {"Año:",           txtAnio},
            {"Director:",      txtDirector},
            {"Calificación:",  txtCal},
            {"Imagen:",        btnSelImg},
        };
        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(makeLabel((String) campos[i][0], FONT_LABEL, TEXT_MUT), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            form.add((JComponent) campos[i][1], gbc);
            gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        }
        // Mostrar ruta seleccionada
        gbc.gridx = 1; gbc.gridy = campos.length;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(lblImgPath, gbc);

        dialog.add(form, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botones.setBackground(BG_PANEL);
        JButton btnCancelar = buildSecondaryBtn("Cancelar");
        JButton btnGuardar  = buildAccentBtn("Guardar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                String nombre   = txtNombre.getText().trim();
                String genero   = txtGenero.getText().trim();
                String director = txtDirector.getText().trim();
                int    anio     = Integer.parseInt(txtAnio.getText().trim());
                double cal      = Double.parseDouble(txtCal.getText().trim());
                if (nombre.isEmpty()) { VentanaPrincipal.mostrarError("El título es obligatorio."); return; }
                if (cal < 0 || cal > 5) { VentanaPrincipal.mostrarError("La calificación debe estar entre 0 y 5."); return; }

                if (esNueva) {
                    Pelicula nueva = sistema.registrarPelicula(nombre, genero, anio, director, cal, rutaImg[0]);
                    // Si la imagen era temporal, renombrarla con la CVE correcta
                    if (!rutaImg[0].isEmpty() && rutaImg[0].contains("TEMP")) {
                        try {
                            File orig = new File(rutaImg[0]);
                            if (orig.exists())
                                nueva.setRutaImagen(GestorImagenes.guardarImagenPelicula(orig, nueva.getCve()));
                        } catch (IOException ignored) {}
                    }
                } else {
                    editar.setGenero(genero);
                    editar.setAnio(anio);
                    editar.setDirector(director);
                    editar.setCalificacionPromedio(cal);
                    if (!rutaImg[0].isEmpty()) editar.setRutaImagen(rutaImg[0]);
                }
                refrescar();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                VentanaPrincipal.mostrarError("Año y calificación deben ser números válidos.");
            }
        });
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        dialog.add(botones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ── Refrescos ─────────────────────────────────────────────────────────
    public void refrescar() {
        refrescarTabla();
        if (peliculaSeleccionada != null) actualizarDetalle();
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Pelicula p : sistema.getListaPeliculas()) {
            modelo.addRow(new Object[]{
                p.getCve(), p.getNombre(), p.getGenero(), p.getAnio(),
                p.getDirector(), String.format("%.1f", p.getCalificacionPromedio()),
                p.isActiva()
            });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private JTextField buildField(String value, int cols) {
        JTextField f = new JTextField(value, cols);
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
}
