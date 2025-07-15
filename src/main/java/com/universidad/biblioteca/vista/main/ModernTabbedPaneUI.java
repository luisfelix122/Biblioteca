package com.universidad.biblioteca.vista.main;

import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernTabbedPaneUI extends BasicTabbedPaneUI {

    private static final Color SELECTED_COLOR = new Color(67, 56, 202);
    private static final Color UNSELECTED_COLOR = new Color(243, 244, 246);
    private static final Color BACKGROUND_COLOR = new Color(229, 231, 235);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.BOLD, 14);

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabAreaInsets.left = 20;
        selectedTabPadInsets = new Insets(10, 15, 10, 15);
        tabInsets = new Insets(10, 15, 10, 15);
        contentBorderInsets = new Insets(0, 0, 0, 0);
    }

    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, tabPane.getWidth(), rects[0].height + tabAreaInsets.top + tabAreaInsets.bottom);

        super.paintTabArea(g, tabPlacement, selectedIndex);
        g2d.dispose();
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, w, h, 20, 20);

        if (isSelected) {
            g2d.setColor(SELECTED_COLOR);
        } else {
            g2d.setColor(UNSELECTED_COLOR);
        }

        g2d.fill(rect);
        g2d.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // Do not paint a border
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();


        int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);

        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(insets.left, tabAreaHeight, width - insets.right - insets.left, height - insets.top - insets.bottom - tabAreaHeight);

        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(insets.left, tabAreaHeight, width - insets.right - insets.left - 1, height - insets.top - insets.bottom - tabAreaHeight - 1);

        g2d.dispose();
    }


    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        g.setFont(TAB_FONT);
        FontMetrics fm = g.getFontMetrics(TAB_FONT);
        int x = textRect.x + (textRect.width - fm.stringWidth(title)) / 2;
        int y = textRect.y + (textRect.height + fm.getAscent()) / 2 - 2;

        if (isSelected) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(new Color(55, 65, 81));
        }
        g.drawString(title, x, y);
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 20;
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 20;
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Do not paint focus indicator
    }
}