package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Vector icon renderer for Swing UI components.
 * Draws clean, scalable icons using Java2D (matching SVG designs in resources/icons/).
 */
public class IconUtils {

    private static final Map<String, Icon> CACHE = new ConcurrentHashMap<>();

    // Standard icon sizes
    public static final int SIZE_SMALL = 16;
    public static final int SIZE_MEDIUM = 20;
    public static final int SIZE_LARGE = 32;

    // Colors
    private static final Color COLOR_PRIMARY = new Color(0x1A73E8);
    private static final Color COLOR_GREEN = new Color(0x34A853);
    private static final Color COLOR_RED = new Color(0xEA4335);
    private static final Color COLOR_WHITE = Color.WHITE;

    private static final Color ICON_PRIMARY = new Color(0x2563EB);
    private static final Color ICON_LIGHT = new Color(0xDBEAFE);

    public static Icon getPersonIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float cx = s / 2f;
                float cy = s / 2f;

                float ringR = s * 0.46f;
                g2.setColor(ICON_LIGHT);
                g2.fill(new Ellipse2D.Float(cx - ringR, cy - ringR, ringR * 2, ringR * 2));

                float headR = s * 0.14f;
                float headY = cy - s * 0.12f;
                g2.setColor(ICON_PRIMARY);
                g2.fill(new Ellipse2D.Float(cx - headR, headY - headR, headR * 2, headR * 2));

                float shoulderWidth = s * 0.40f;
                float shoulderTop = headY + headR;
                float shoulderBot = cy + s * 0.22f;
                float neckInset = s * 0.06f;

                Path2D.Float body = new Path2D.Float();
                body.moveTo(cx - neckInset, shoulderTop);
                body.quadTo(cx - neckInset, shoulderTop + s * 0.04f,
                            cx - shoulderWidth / 2f, shoulderTop + s * 0.06f);
                body.quadTo(cx - shoulderWidth / 2f - s * 0.02f, shoulderBot - s * 0.04f,
                            cx - shoulderWidth / 2f + s * 0.04f, shoulderBot);
                body.quadTo(cx, shoulderBot + s * 0.02f,
                            cx + shoulderWidth / 2f - s * 0.04f, shoulderBot);
                body.quadTo(cx + shoulderWidth / 2f + s * 0.02f, shoulderBot - s * 0.04f,
                            cx + shoulderWidth / 2f, shoulderTop + s * 0.06f);
                body.quadTo(cx + neckInset, shoulderTop + s * 0.04f,
                            cx + neckInset, shoulderTop);
                body.closePath();
                g2.fill(body);
            }
        };
    }

    public static Icon getAddIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PRIMARY);
                int r = s / 2 - 1;
                g2.fill(new Ellipse2D.Float(s / 2f - r, s / 2f - r, r * 2, r * 2));
                g2.setColor(COLOR_WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int barLen = s / 3;
                int cx = s / 2;
                int cy = s / 2;
                g2.draw(new Line2D.Float(cx - barLen / 2f, cy, cx + barLen / 2f, cy));
                g2.draw(new Line2D.Float(cx, cy - barLen / 2f, cx, cy + barLen / 2f));
            }
        };
    }

    public static Icon getEditIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_GREEN);
                int pad = s / 5;
                // Pencil body
                Path2D.Float pencil = new Path2D.Float();
                pencil.moveTo(pad, s - pad);
                pencil.lineTo(pad + s / 6, s - pad - s / 6);
                pencil.lineTo(s - pad - s / 6, pad + s / 6);
                pencil.lineTo(s - pad, pad);
                pencil.closePath();
                // Tip
                float tipX = s - pad;
                float tipY = pad;
                float midX = (pad + tipX) / 2;
                float midY = (s - pad + tipY) / 2;
                g2.draw(pencil);
                g2.draw(new Line2D.Float(tipX, tipY, midX, midY));
            }
        };
    }

    public static Icon getDeleteIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_RED);
                int pad = s / 5;
                int top = pad + s / 8;
                // Lid
                g2.draw(new Line2D.Float(pad, top, s - pad, top));
                // Body
                int bodyTop = top + s / 10;
                int bodyBot = s - pad;
                Path2D.Float body = new Path2D.Float();
                body.moveTo(pad + 2, bodyTop);
                body.lineTo(pad + 2, bodyBot);
                body.lineTo(s - pad - 2, bodyBot);
                body.lineTo(s - pad - 2, bodyTop);
                g2.draw(body);
                // Handle
                float handleWidth = s / 6;
                float handleTop = top - s / 10;
                float handleLeft = s / 2f - handleWidth / 2;
                g2.draw(new Line2D.Float(handleLeft, handleTop, handleLeft + handleWidth, handleTop));
                // X lines inside
                int cx = s / 2;
                int cy = (bodyTop + bodyBot) / 2;
                int cross = s / 8;
                g2.draw(new Line2D.Float(cx - cross, cy - cross, cx + cross, cy + cross));
                g2.draw(new Line2D.Float(cx + cross, cy - cross, cx - cross, cy + cross));
            }
        };
    }

    public static Icon getRefreshIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_PRIMARY);
                int r = s / 3;
                int cx = s / 2;
                int cy = s / 2;
                // Arrowhead at end
                double angle = Math.PI / 4;
                int tipX = (int)(cx + r * Math.cos(angle));
                int tipY = (int)(cy - r * Math.sin(angle));
                // Arrow lines
                int arrowLen = s / 7;
                g2.draw(new Line2D.Float(tipX, tipY, tipX - arrowLen, tipY + arrowLen));
                g2.draw(new Line2D.Float(tipX, tipY, tipX + arrowLen, tipY + arrowLen));
                // Arc
                Arc2D.Float arc = new Arc2D.Float(cx - r, cy - r, r * 2, r * 2, 225, -260, Arc2D.OPEN);
                g2.draw(arc);
            }
        };
    }

    public static Icon getSearchIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_PRIMARY);
                int r = s / 4;
                int cx = s / 2 - 1;
                int cy = s / 2 - 1;
                g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
                // Handle
                int handleLen = s / 5;
                double angle = Math.PI / 4;
                int hx1 = (int)(cx + r * Math.cos(angle));
                int hy1 = (int)(cy + r * Math.sin(angle));
                int hx2 = (int)(hx1 + handleLen * Math.cos(angle));
                int hy2 = (int)(hy1 + handleLen * Math.sin(angle));
                g2.draw(new Line2D.Float(hx1, hy1, hx2, hy2));
            }
        };
    }

    public static Icon getEmployeeIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_PRIMARY);

                float cx = s / 2f;
                float cy = s / 2f;
                float r = s / 2f - 1.5f;

                // 1. Draw Hexagon Outline
                Path2D.Float hex = new Path2D.Float();
                for (int i = 0; i < 6; i++) {
                    double angle = i * Math.PI / 3 - Math.PI / 2;
                    float x = (float)(cx + r * Math.cos(angle));
                    float y = (float)(cy + r * Math.sin(angle));
                    if (i == 0) hex.moveTo(x, y);
                    else hex.lineTo(x, y);
                }
                hex.closePath();
                g2.draw(hex);

                // 2. Draw Stylized Number 6 inside Hexagon
                Path2D.Float path6 = new Path2D.Float();
                float innerR = r * 0.8f;
                
                // Start from the top tail of 6
                path6.moveTo(cx + innerR * 0.35f, cy - innerR * 0.5f);
                // Curve down to the left of the loop
                path6.curveTo(cx - innerR * 0.55f, cy - innerR * 0.45f,
                              cx - innerR * 0.55f, cy + innerR * 0.35f,
                              cx - innerR * 0.1f, cy + innerR * 0.55f);
                // Curve around the bottom and right
                path6.curveTo(cx + innerR * 0.3f, cy + innerR * 0.55f,
                              cx + innerR * 0.45f, cy + innerR * 0.15f,
                              cx + innerR * 0.3f, cy - innerR * 0.15f);
                // Curve back into the center to close the loop
                path6.curveTo(cx + innerR * 0.15f, cy - innerR * 0.35f,
                              cx - innerR * 0.25f, cy - innerR * 0.2f,
                              cx - innerR * 0.4f, cy + innerR * 0.15f);
                
                g2.draw(path6);
            }
        };
    }

    public static Icon getChartIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_PRIMARY);
                int pad = s / 5;
                int barW = s / 7;
                int x1 = pad;
                int x2 = pad + barW * 2;
                int x3 = pad + barW * 4;
                int base = s - pad;
                g2.draw(new RoundRectangle2D.Float(x1, base - s/3f, barW, s/3f, 1, 1));
                g2.draw(new RoundRectangle2D.Float(x2, base - s*0.6f, barW, s*0.6f, 1, 1));
                g2.draw(new RoundRectangle2D.Float(x3, base - s*0.45f, barW, s*0.45f, 1, 1));
            }
        };
    }

    public static Icon getFileIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_GREEN);
                int pad = s / 5;
                Path2D.Float file = new Path2D.Float();
                file.moveTo(pad, pad);
                file.lineTo(s - pad - s/4f, pad);
                file.lineTo(s - pad, pad + s/4f);
                file.lineTo(s - pad, s - pad);
                file.lineTo(pad, s - pad);
                file.closePath();
                g2.draw(file);
                g2.draw(new Line2D.Float(s - pad - s/4f, pad, s - pad - s/4f, pad + s/4f));
                g2.draw(new Line2D.Float(s - pad - s/4f, pad + s/4f, s - pad, pad + s/4f));
            }
        };
    }

    public static Icon getLogoutIcon(int size) {
        return new VectorIcon(size, size) {
            @Override
            protected void paintIcon(Graphics2D g2, int s) {
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_RED);
                int pad = s / 5;
                g2.draw(new Line2D.Float(s/2f, pad, pad, pad));
                g2.draw(new Line2D.Float(pad, pad, pad, s - pad));
                g2.draw(new Line2D.Float(pad, s - pad, s/2f, s - pad));
                g2.draw(new Line2D.Float(s/3f, s/2f, s - pad, s/2f));
                g2.draw(new Line2D.Float(s - pad - s/4f, s/2f - s/6f, s - pad, s/2f));
                g2.draw(new Line2D.Float(s - pad - s/4f, s/2f + s/6f, s - pad, s/2f));
            }
        };
    }

    /**
     * Get a cached icon by name for reuse.
     */
    public static Icon getIcon(String name, int size) {
        String key = name + "_" + size;
        return CACHE.computeIfAbsent(key, k -> {
            switch (name) {
                case "person": return getPersonIcon(size);
                case "add": return getAddIcon(size);
                case "edit": return getEditIcon(size);
                case "delete": return getDeleteIcon(size);
                case "refresh": return getRefreshIcon(size);
                case "search": return getSearchIcon(size);
                case "employee": return getEmployeeIcon(size);
                case "chart": return getChartIcon(size);
                case "file": return getFileIcon(size);
                case "logout": return getLogoutIcon(size);
                default: return getPersonIcon(size);
            }
        });
    }

    public static BufferedImage getAppIconImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        float sF = (float) size;
        float r = sF * 0.18f;

        GradientPaint gradient = new GradientPaint(0, 0, new Color(0x1A73E8),
                sF, sF, new Color(0x0D47A1));
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(0, 0, sF, sF, r, r));

        g2.setStroke(new BasicStroke(sF * 0.015f));
        g2.setColor(new Color(0xFFFFFF, false));
        g2.draw(new Ellipse2D.Float(sF * 0.15f, sF * 0.15f, sF * 0.7f, sF * 0.7f));

        g2.setStroke(new BasicStroke(sF * 0.008f));
        g2.setColor(new Color(0xFFFFFF, true));
        g2.draw(new Ellipse2D.Float(sF * 0.23f, sF * 0.23f, sF * 0.54f, sF * 0.54f));

        float p1cx = sF * 0.38f;
        float p1headR = sF * 0.10f;
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Float(p1cx - p1headR, sF * 0.30f - p1headR,
                p1headR * 2, p1headR * 2));
        g2.fill(new Ellipse2D.Float(p1cx - sF * 0.14f, sF * 0.58f,
                sF * 0.28f, sF * 0.32f));

        float p2cx = sF * 0.66f;
        float p2headR = sF * 0.09f;
        g2.setColor(new Color(0xE8F0FE));
        g2.fill(new Ellipse2D.Float(p2cx - p2headR, sF * 0.33f - p2headR,
                p2headR * 2, p2headR * 2));
        g2.fill(new Ellipse2D.Float(p2cx - sF * 0.12f, sF * 0.60f,
                sF * 0.24f, sF * 0.28f));

        float badgeCx = sF * 0.74f;
        float badgeCy = sF * 0.28f;
        float badgeR = sF * 0.08f;
        g2.setColor(new Color(0x34A853));
        g2.fill(new Ellipse2D.Float(badgeCx - badgeR, badgeCy - badgeR,
                badgeR * 2, badgeR * 2));
        g2.setStroke(new BasicStroke(sF * 0.032f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.WHITE);
        float checkSize = badgeR * 0.55f;
        g2.draw(new Line2D.Float(badgeCx - checkSize * 0.6f, badgeCy,
                badgeCx - checkSize * 0.1f, badgeCy + checkSize * 0.6f));
        g2.draw(new Line2D.Float(badgeCx - checkSize * 0.1f, badgeCy + checkSize * 0.6f,
                badgeCx + checkSize * 0.8f, badgeCy - checkSize * 0.5f));

        g2.dispose();
        return img;
    }

    public static void setFrameIcon(Window frame) {
        if (frame instanceof JFrame) {
            ((JFrame) frame).setIconImage(getAppIconImage(64));
        } else if (frame instanceof JDialog) {
            ((JDialog) frame).setIconImage(getAppIconImage(48));
        }
    }

    public static void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            
            // Custom rounded corners properties for FlatLaf
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);

            Color primary = new Color(0x1A73E8);
            Color primaryDark = new Color(0x1557B0);
            Color primaryLight = new Color(0xE8F0FE);
            Color bgLight = new Color(0xF5F7FA);
            Color surface = new Color(0xFFFFFF);
            Color textPrimary = new Color(0x202124);
            Color borderColor = new Color(0xDADCE0);

            UIManager.put("control", surface);
            UIManager.put("text", textPrimary);
            UIManager.put("textForeground", textPrimary);
            UIManager.put("textHighlight", primaryLight);
            UIManager.put("textHighlightText", primary);
            UIManager.put("nimbusBase", primary);
            UIManager.put("nimbusBlueGrey", new Color(0xD2E3FC));
            UIManager.put("nimbusLightBackground", bgLight);
            UIManager.put("nimbusSelectionBackground", primaryLight);
            UIManager.put("nimbusSelection", primary);
            UIManager.put("nimbusFocus", new Color(0x1A73E880, true));

            // Table styling
            UIManager.put("Table.background", surface);
            UIManager.put("Table.alternateRowColor", new Color(0xF8F9FA));
            UIManager.put("Table.selectionBackground", new Color(0xE8F0FE));
            UIManager.put("Table.selectionForeground", textPrimary);
            UIManager.put("Table.gridColor", borderColor);
            UIManager.put("TableHeader.background", new Color(0xF1F3F4));

            // Button styling
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.background", primary);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.select", primaryDark);
            UIManager.put("Button.textShiftOffset", 0);

            // Text fields
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.background", surface);
            UIManager.put("TextField.foreground", textPrimary);
            UIManager.put("TextField.caretForeground", primary);

            // Labels
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.foreground", textPrimary);

            // Password field
            UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 13));

            // Combo box
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 13));

            // Option pane
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 13));

        } catch (Exception e) {
            System.err.println("Could not set FlatLaf LookAndFeel: " + e.getMessage());
        }
    }

    // --- Abstract vector icon base ---

    private abstract static class VectorIcon implements Icon {
        private final int w, h;

        VectorIcon(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            paintIcon(g2, Math.min(w, h));
            g2.dispose();
        }

        protected abstract void paintIcon(Graphics2D g2, int size);

        @Override
        public int getIconWidth() { return w; }

        @Override
        public int getIconHeight() { return h; }
    }

    private IconUtils() {}
}
