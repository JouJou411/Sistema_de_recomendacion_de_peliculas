package GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TarjetaPelicula extends JPanel {
    public TarjetaPelicula(String titulo, String calificacion, String coincidencia, String rutaImagen) {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(30, 30, 45)); // Fondo oscuro de la tarjeta
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Contenedor del Póster (JLabel con imagen escalada)
        JLabel lblPoster = new JLabel();
        lblPoster.setHorizontalAlignment(JLabel.CENTER);

        // Dimensiones estándar para las tarjetas del panel central
        int ancho = 160;
        int alto = 230;
        lblPoster.setPreferredSize(new Dimension(ancho, alto));

        File file = new File(rutaImagen);
        if (file.exists()) {
            ImageIcon iconoOriginal = new ImageIcon(rutaImagen);
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            lblPoster.setIcon(new ImageIcon(imgEscalada));
        } else {
            // Fondo de respaldo si no encuentra la imagen física en assets/imgMovies/
            lblPoster.setOpaque(true);
            lblPoster.setBackground(new Color(45, 45, 65));
            lblPoster.setText("<html><center>Sin Póster<br>" + titulo + "</center></html>");
            lblPoster.setForeground(Color.LIGHT_GRAY);
        }
        add(lblPoster, BorderLayout.CENTER);

        // 2. Panel de Información Inferior (Metadatos de la película)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRatingTxt = new JLabel("Average Rating");
        lblRatingTxt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRatingTxt.setForeground(Color.GRAY);
        lblRatingTxt.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Renderizado del puntaje (ej: 4.8/5 ⭐)
        JLabel lblRatingVal = new JLabel(calificacion + "/5 ⭐");
        lblRatingVal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRatingVal.setForeground(new Color(235, 170, 50));
        lblRatingVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMatchTxt = new JLabel("Compatibility Score");
        lblMatchTxt.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblMatchTxt.setForeground(Color.GRAY);
        lblMatchTxt.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Renderizado del porcentaje de coincidencia obtenido por el BFS
        JLabel lblMatchVal = new JLabel(coincidencia + " Match");
        lblMatchVal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMatchVal.setForeground(new Color(40, 200, 150));
        lblMatchVal.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblTitulo);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblRatingTxt);
        infoPanel.add(lblRatingVal);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblMatchTxt);
        infoPanel.add(lblMatchVal);

        add(infoPanel, BorderLayout.SOUTH);
    }

    // Sobrescribimos el dibujo del componente para darle los bordes redondeados de la imagen
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}