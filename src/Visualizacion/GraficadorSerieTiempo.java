package Visualizacion;

import ArchivosDatos.RenglonDatos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GraficadorSerieTiempo {

    // Valor centinela numérico usado en RenglonDatos ("NaN")
    private static final double VALOR_CENTINELA_NUM =
            Double.parseDouble(ArchivosDatos.RenglonDatos.VALOR_CENTINELA);

    /**
     * variableOpcion:
     *  1 -> precipitación
     *  2 -> evaporación
     *  3 -> temp. máxima
     *  4 -> temp. mínima
     */
    public static void graficar(RenglonDatos[] datos,
                                int anio,
                                int variableOpcion) {

        List<PuntoSerie> serie = construirSerie(datos, anio, variableOpcion);

        if (serie.isEmpty()) {
            System.out.println("No hay datos para el año " + anio +
                    " con los filtros actuales.");
            return;
        }

        final String nombreVar = nombreVariable(variableOpcion);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Serie de tiempo " + nombreVar +
                    " (" + anio + ")");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            frame.add(new PanelSerieTiempo(serie, nombreVar, anio));
            frame.setVisible(true);
        });
    }

    // ----- Construcción de la serie -----

    private static List<PuntoSerie> construirSerie(RenglonDatos[] datos,
                                                   int anio,
                                                   int variableOpcion) {
        List<PuntoSerie> lista = new ArrayList<>();

        for (RenglonDatos r : datos) {
            /*
            if (r.fecha.getYear() != anio) continue;

            double v;
            switch (variableOpcion) {
                case 1: v = r.precipitacion; break;
                case 2: v = r.evaporacion;   break;
                case 3: v = r.tempMax;       break;
                case 4: v = r.tempMin;       break;
                default: return new ArrayList<>();
            }

            // --- NUEVO: ignorar datos NULO (NaN) ---
            if (Double.compare(v, VALOR_CENTINELA_NUM) == 0) {
                continue;
            }

            lista.add(new PuntoSerie(r.fecha, v));
             */
        }

        lista.sort(Comparator.comparing(p -> p.fecha));
        return lista;
    }


    private static String nombreVariable(int op) {
        switch (op) {
            case 1: return "Precipitación";
            case 2: return "Evaporación";
            case 3: return "Temperatura máxima";
            case 4: return "Temperatura mínima";
            default: return "Desconocida";
        }
    }

    private static class PuntoSerie {
        final LocalDate fecha;
        final double valor;

        PuntoSerie(LocalDate fecha, double valor) {
            this.fecha = fecha;
            this.valor = valor;
        }
    }

    private static class PanelSerieTiempo extends JPanel {
        private final List<PuntoSerie> serie;
        private final String nombreVariable;
        private final int anio;

        PanelSerieTiempo(List<PuntoSerie> serie,
                         String nombreVariable,
                         int anio) {
            this.serie = serie;
            this.nombreVariable = nombreVariable;
            this.anio = anio;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (serie.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int margenIzq = 60;
            int margenDer = 20;
            int margenSup = 40;
            int margenInf = 60;

            int minDia = serie.get(0).fecha.getDayOfYear();
            int maxDia = serie.get(serie.size() - 1).fecha.getDayOfYear();

            double minVal = Double.POSITIVE_INFINITY;
            double maxVal = Double.NEGATIVE_INFINITY;

            for (PuntoSerie p : serie) {
                if (p.valor < minVal) minVal = p.valor;
                if (p.valor > maxVal) maxVal = p.valor;
            }
            if (minVal == maxVal) {
                minVal -= 1.0;
                maxVal += 1.0;
            }

            double grafW = w - margenIzq - margenDer;
            double grafH = h - margenSup - margenInf;

            int x0 = margenIzq;
            int y0 = h - margenInf;

            g2.drawLine(x0, y0, (int)(x0 + grafW), y0);       // eje X
            g2.drawLine(x0, y0, x0, (int)(y0 - grafH));       // eje Y

            String titulo = nombreVariable + " (" + anio + ")";
            g2.drawString(titulo, margenIzq, margenSup - 10);

            g2.drawString(String.format("%.2f", minVal), 5, y0);
            g2.drawString(String.format("%.2f", maxVal), 5, (int)(y0 - grafH));

            g2.drawString(serie.get(0).fecha.toString(), x0, y0 + 20);
            g2.drawString(serie.get(serie.size()-1).fecha.toString(), x0 + (int) grafW - 100, y0 + 20);

            int xPrev = 0;
            int yPrev = 0;

            boolean noPrimero = false;
            for (int i = 0; i < serie.size(); i++) {
                PuntoSerie p = serie.get(i);
                int dia = p.fecha.getDayOfYear();

                double xNorm = (double)(dia - minDia) / (double)(maxDia - minDia);
                double yNorm = (double)(p.valor - minVal) / (double)(maxVal - minVal);

                int x = x0 + (int)(xNorm * grafW);
                int y = y0 - (int)(yNorm * grafH);

                if (noPrimero) {
                    g2.fillOval(x, y, 2, 2);
                }

                xPrev = x;
                yPrev = y;
                noPrimero = true;
            }
        }
    }
}
