package app;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Main {
    
    public static final int ACTIVE_POLL_DELAY = 10000; // milliseconds between pings when the car is moving
    public static final int STOPPED_POLL_DELAY = 120000; // milliseconds between pings when the car is stopped

    private static GUI gui;
    private static PrintStream dataWriter;
    private static PrintStream errorWriter;
    private static Car tesla;

    public static void main(String[] args) {
        // fixes SSL certificate quirk
        // https://community.qualys.com/thread/13532
        System.setProperty("jsse.enableSNIExtension", "false");
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui = new GUI();
                gui.setVisible(false);

                final Login inputs = new Login(gui, false);
                inputs.setCallback(new LoginCallback() {

                    @Override
                    public void submit(final String token) {
                        try {
                            inputs.setStatus("Authenticating login...");
                            new SwingWorker() {

                                @Override
                                protected Void doInBackground() {
                                    tesla = new Car(token);
                                    System.out.println(tesla);
                                    return null;
                                }

                                @Override
                                public void done() {
                                    inputs.setStatus("Login Authenticated!");
                                    
                                    inputs.setVisible(false);
                                    inputs.dispose();
                                    
                                    gui.setVisible(true);
                                    while (tesla == null);
                                    gui.setVid(tesla.vehicle_id);
                                    new SwingWorker() {

                                        @Override
                                        protected Void doInBackground() {
                                            try {
                                                startTracking(tesla);
                                            } catch (IOException ex) {
                                                logError(ex);
                                            }
                                            return null;
                                        }
                                    }.execute();
                                }
                            }.execute();
                        } catch (IllegalArgumentException e) {
                            inputs.setStatus("Invalid username and/or password");
                        }
                    }
                });
                inputs.setVisible(true);
            }
        });
    }

    private static void startTracking(final Car tesla) throws IOException {

        File data = new File("data.txt");
        dataWriter = new PrintStream(new FileOutputStream(data, true));
        if (!data.exists())
            dataWriter.println("date,timestamp,speed,odometer,soc,elevation,est_heading,est_lat,est_lng,power,range");

        File error = new File("error.txt");
        errorWriter = new PrintStream(new FileOutputStream(error, true));

        new SwingWorker() {

            @Override
            @SuppressWarnings("SleepWhileInLoop")
            protected Void doInBackground() {
                while (true) {
                    int speed = -1;
                    try {
                        System.out.println("\nPolling...");

                        JsonObject drive_state = tesla.getVehicleProperty("drive_state");
                        System.out.println(drive_state);
                        double latitude = drive_state.get("latitude").getAsDouble();
                        double longitude = drive_state.get("longitude").getAsDouble();
                        gui.setLatLon(latitude, longitude);
                        
                        JsonElement speedObj = drive_state.get("speed");
                        speed = speedObj.isJsonNull() ? 0 : speedObj.getAsInt();
                        gui.setSpeed(speed);

                        JsonObject charge_state = tesla.getVehicleProperty("charge_state");
                        System.out.println(charge_state);
                        gui.setRange(charge_state.get("ideal_battery_range").getAsDouble());

                        gui.setLastUpdate(new Date());
                        
                        JsonObject climate_state = tesla.getVehicleProperty("climate_state");
                        System.out.println(climate_state);

                        JsonObject fullData = new JsonObject();
                        fullData.add("drive_state", drive_state);
                        fullData.add("charge_state", charge_state);
                        fullData.add("climate_state", climate_state);
                        
                        addPoint(tesla.vehicle_id, fullData);
                        
                    } catch (IOException ex) {
                        if (ex instanceof SocketTimeoutException) {
                            System.err.println("Connection timed out, " + ex.getLocalizedMessage());
                        } else {
                            logError(ex);
                        }
                    }

                    try {
                        Thread.sleep(speed == 0 ? STOPPED_POLL_DELAY : ACTIVE_POLL_DELAY);
                    } catch (InterruptedException ex) {
                        logError(ex);
                    }
                }
            }
        }.execute();
        System.out.println("Swing worker started");
    }

    public static void addPoint(int vid, JsonObject data) throws IOException {
        System.out.println("Adding point");
        Map<String, String> params = new HashMap<>();
        params.put("full_data", Car.PRETTY_PRINTER.toJson(data));
        String result = HttpHelper.post("https://evtripplanner.com/planner/tracker/addPoint.php?vid=" + vid, params);
        
        System.out.println("addPoint: " + result);
    }

    public static void logError(Throwable error) {
        logError(error, "Something's gone wrong");
    }

    public static void logError(Throwable error, String message) {
        System.err.println(message);
        error.printStackTrace();
        
        errorWriter.println(new Date());
        errorWriter.append(message);
        error.printStackTrace(errorWriter);  
    }
}
