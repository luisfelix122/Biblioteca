package com.universidad.biblioteca.vista.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedShadowPanel extends JPanel {
    private int shadowSize = 16;
    private float shadowOpacity = 0.2f;
    private int cornerRadius = 12;

    public RoundedShadowPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int shadowOffset = 4;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2.setColor(new Color(0, 0, 0, (int) (255 * shadowOpacity)));
        g2.fill(new RoundRectangle2D.Double(shadowOffset, shadowOffset, width - shadowOffset * 2, height - shadowOffset * 2, cornerRadius, cornerRadius));

        // Background
        Shape clip = new RoundRectangle2D.Double(0, 0, width - shadowOffset, height - shadowOffset, cornerRadius, cornerRadius);
        g2.setClip(clip);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);
        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 0, shadowSize, shadowSize);
    }
}