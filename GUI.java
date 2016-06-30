package app;

import java.awt.Desktop;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringJoiner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 *
 * @author Ben Hannel
 */
public class GUI extends javax.swing.JFrame {

    private double lat;
    private double lon;
    private int vid;
    private ArrayList<Point2D.Double> points = new ArrayList<>();
    private int pointIndex = 0;
    private int numPoints = 0;

    /** Creates new form GUI */
    public GUI() {
        initComponents();
    }

    public void setSpeed(int speed) {
        this.speed.setText(speed + " mph");
    }

    public void setRange(double range) {
        this.range.setText(range + " miles");
    }

    public void setLatLon(double lat, double lon) {
        points.add(new Point2D.Double(lat, lon));

        if (points.size() > 20)
            points.remove(0);

        this.lat = lat;
        this.lon = lon;
        this.coords.setText(lat + ", " + lon);
        
        numPoints++;
        numPointsLabel.setText("" + numPoints);
        
        if (numPoints == 1) {
            viewLocationActionPerformed(null);
        }
    }

    public void setLastUpdate(Date date) {
        lastUpdate.setValue(date);
    }

    public void setVid(int vid) {
        this.vid = vid;
        vidLabel.setText("" + vid);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        mapLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        speed = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        range = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        coords = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        lastUpdate = new javax.swing.JFormattedTextField();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        vidLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        numPointsLabel = new javax.swing.JLabel();
        viewLocation = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(250, 350));
        setPreferredSize(new java.awt.Dimension(500, 700));

        jPanel2.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 300));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(mapLabel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(0, 2));

        jLabel1.setText("Speed");
        jPanel1.add(jLabel1);

        speed.setText("NA");
        jPanel1.add(speed);

        jLabel4.setText("Range");
        jPanel1.add(jLabel4);

        range.setText("NA");
        jPanel1.add(range);

        jLabel5.setText("Latitude, Longitude");
        jPanel1.add(jLabel5);

        coords.setText("NA");
        jPanel1.add(coords);

        jLabel6.setText("Last Update Time");
        jPanel1.add(jLabel6);

        lastUpdate.setBackground(new java.awt.Color(238, 238, 238));
        lastUpdate.setBorder(null);
        lastUpdate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance())));
        lastUpdate.setFocusable(false);
        jPanel1.add(lastUpdate);

        jLabel7.setText("Vehicle ID");
        jPanel1.add(jLabel7);

        vidLabel.setText("NA");
        jPanel1.add(vidLabel);

        jLabel8.setText("Data Points Recorded");
        jPanel1.add(jLabel8);

        numPointsLabel.setText("NA");
        jPanel1.add(numPointsLabel);

        viewLocation.setText("View Location");
        viewLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLocationActionPerformed(evt);
            }
        });
        jPanel1.add(viewLocation);

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });
        jPanel1.add(aboutButton);

        jPanel2.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLocationActionPerformed
        StringJoiner pathStr = new StringJoiner("|");
        for (Point2D point : points) {
            pathStr.add(point.getX() + "," + point.getY());
        }
        try {
            String urlStr = "http://maps.googleapis.com/maps/api/staticmap?sensor=false"
                    + "&size=" + mapLabel.getWidth() + "x" + mapLabel.getHeight()
                    + "&markers=" + lat + "," + lon
                    + "&path=color:0x0000ff|weight:5" + pathStr.toString();
            URL url = new URL(urlStr);
            BufferedImage img = ImageIO.read(url);
            final ImageIcon icon = new ImageIcon(img);
            new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    mapLabel.setIcon(icon);
                    return null;
                }
            }.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
            Main.logError(ex, "Static map update failed");
        }
    }//GEN-LAST:event_viewLocationActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        try {
            Desktop.getDesktop().browse(
                    new URI("http://evtripplanner.com/tracker_about.php"));
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
            Main.logError(ex);
        }
    }//GEN-LAST:event_aboutButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JLabel coords;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JFormattedTextField lastUpdate;
    private javax.swing.JLabel mapLabel;
    private javax.swing.JLabel numPointsLabel;
    private javax.swing.JLabel range;
    private javax.swing.JLabel speed;
    private javax.swing.JLabel vidLabel;
    private javax.swing.JButton viewLocation;
    // End of variables declaration//GEN-END:variables
}
