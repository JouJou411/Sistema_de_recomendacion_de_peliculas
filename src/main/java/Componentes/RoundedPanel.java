package Componentes;

import javax.swing.JPanel;
import java.awt.*;

/**
 * RoundedPanel — JPanel con esquinas redondeadas y fondo plano.
 * Se usa en TarjetaPelicula, PantallaMotor y cualquier panel que requiera
 * esquinas redondeadas sin depender de un Look&Feel externo.
 */
public class RoundedPanel extends JPanel {

    private final int   arc;
    private final Color bg;

    public RoundedPanel(int arc, Color bg) {
        this.arc = arc;
        this.bg  = bg;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() { return false; }
}
