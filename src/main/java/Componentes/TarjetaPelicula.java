package Componentes;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

import static Vistas.VentanaPrincipal.*;

/**
 * TarjetaPelicula
 * Muestra póster, título, calificación promedio y porcentaje de compatibilidad BFS.
 */
public class TarjetaPelicula extends JPanel {

    public TarjetaPelicula(String titulo, String calificacion, String coincidencia, String rutaImagen) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // ── Contenedor con esquinas redondeadas ──
        RoundedPanel card = new RoundedPanel(14, BG_CARD);
        card.setLayout(new BorderLayout(0, 0));
        setLayout(new BorderLayout());
        add(card, BorderLayout.CENTER);

        // ── Badge de match (esquina superior derecha del póster) ──
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(160, 230));

        // Póster
        JLabel lblPoster = new JLabel();
        lblPoster.setHorizontalAlignment(JLabel.CENTER);
        lblPoster.setBounds(0, 0, 160, 230);

        File file = new File(rutaImagen);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(rutaImagen);
            Image scaled  = icon.getImage().getScaledInstance(160, 230, Image.SCALE_SMOOTH);
            lblPoster.setIcon(new ImageIcon(scaled));
        } else {
            lblPoster.setOpaque(true);
            lblPoster.setBackground(new Color(30, 30, 46));
            lblPoster.setText("<html><center><span style='color:#6b6b7a;font-size:11px'>Sin póster<br>" + titulo + "</span></center></html>");
        }
        layered.add(lblPoster, Integer.valueOf(0));

        // Badge match
        JLabel badge = new JLabel(coincidencia + " match");
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setForeground(new Color(10, 30, 28));
        badge.setBackground(CYAN);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 7, 3, 7));
        badge.setBounds(8, 10, 80, 22);
        layered.add(badge, Integer.valueOf(1));

        card.add(layered, BorderLayout.CENTER);

        // ── Info inferior ──
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(10, 12, 12, 12));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(TEXT_PRI);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRatingTag = new JLabel("Calificación promedio");
        lblRatingTag.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblRatingTag.setForeground(TEXT_MUT);
        lblRatingTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRating = new JLabel(calificacion + " / 5  ⭐");
        lblRating.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRating.setForeground(ACCENT);
        lblRating.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCompatTag = new JLabel("Compatibilidad BFS");
        lblCompatTag.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblCompatTag.setForeground(TEXT_MUT);
        lblCompatTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCompat = new JLabel(coincidencia);
        lblCompat.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblCompat.setForeground(CYAN);
        lblCompat.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(lblTitulo);
        info.add(Box.createVerticalStrut(6));
        info.add(lblRatingTag);
        info.add(lblRating);
        info.add(Box.createVerticalStrut(4));
        info.add(lblCompatTag);
        info.add(lblCompat);

        card.add(info, BorderLayout.SOUTH);
    }

    @Override
    public boolean isOpaque() { return false; }
}
