package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Car {
    public static final Gson PRETTY_PRINTER = new GsonBuilder().setPrettyPrinting().create();
    public static final JsonParser PARSER = new JsonParser();
    public static final int HTTP_TIMEOUT = 10000; // milliseconds to cancel connect/read of http request
    
    private String token; // should last 3 months

    public String option_codes;
    public String id;
    public int vehicle_id;

    public Car(String token) {
        this.token = token;
        System.out.println("Access token: " + token);
        try {
            JsonObject json = getVehicleProperty(null);
            System.out.println("Vehicle info: " + PRETTY_PRINTER.toJson(json));
            
            id = json.get("id").getAsString();
            vehicle_id = json.get("vehicle_id").getAsInt();
            option_codes = json.get("option_codes").getAsString();
            
            addCar();
        } catch (IOException ex) {
            Main.logError(ex);
        }
    }

    private void addCar() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("vid", "" + vehicle_id);
        params.put("codes", option_codes);
        
        String result = HttpHelper.post("https://evtripplanner.com/planner/tracker/addCar.php", params);

        System.out.println("addCar: " + result);
    }

    public final JsonObject getVehicleProperty(String prop) throws IOException {
        final String API_TARGET = "https://owner-api.teslamotors.com/api/1/vehicles/";
        if (prop == null) { // list available vehicles
            JsonArray vehicle_listing = getProperty(API_TARGET).get("response").getAsJsonArray();
            if (vehicle_listing.size() > 1)
                System.out.println("More than 1 vehicle available. Using the first.");
            return vehicle_listing.get(0).getAsJsonObject();
        } else {
            JsonObject result = getProperty(API_TARGET + id + "/data_request/" + prop).get("response").getAsJsonObject();
            return result;
        }
    }
    
    public JsonObject getProperty(String urlStr) throws IOException {
        return getProperty(urlStr, 10, 500);
    }

    public JsonObject getProperty(final String urlStr, final int maxRetries, final int MIN_TIMEOUT)
            throws IOException {

        URL urlObj = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.addRequestProperty("Authorization", "Bearer " + token);
        
        // Trying to prevent hung connections
        connection.setConnectTimeout(HTTP_TIMEOUT);
        connection.setReadTimeout(HTTP_TIMEOUT);

        String result = HttpHelper.readAll(connection.getInputStream());

        return PARSER.parse(result).getAsJsonObject();
    }

    @Override
    public String toString() {
        return "VIN: " + vehicle_id;
    }
}

/*
    Option code mapping
      {
        "MS01" => "base",
        "RENA" => "region_us",
        "RECA" => "region_canada",
        "TM00" => "standard_trim",
        "TM02" => "signature_trim",
        "DRLH" => "left_hand_drive",
        "DRRH" => "right_hand_drive",
        "PF00" => "no_performance_model",
        "PF01" => "performance_model",
        "BT85" => "battery_85",
        "BT60" => "battery_60",
        "BT40" => "battery_40",
        "PBSB" => "paint_black",
        "PBSW" => "paint_solid_white",
        "PMSS" => "paint_silver",
        "PMTG" => "paint_dolphin_gray_metalic",
        "PMAB" => "paint_metalic_brown",
        "PMMB" => "paint_metalic_blue",
        "PSW"  => "paint_pearl_white",
        "PSR"  => "paint_signature_red",
        "RFBC" => "roof_body_color",
        "RFPO" => "roof_panorama",
        "WT19" => "wheel_silver_19",
        "WT21" => "wheel_silver_21",
        "WTSP" => "wheel_gray_21",
        "IBSB" => "seats_base_textile",
        "IZMB" => "seats_black_leather",
        "IZMG" => "seats_gray_leather",
        "IPMB" => "seats_performance_black_leather",
        "IDPB" => "interior_piano_black",
        "IDLW" => "interior_lacewood",
        "IDOM" => "interior_obeche_wood_matte",
        "IDCF" => "interior_carbon_fiber",
        "IPMG" => "interior_performance_leather",
        "TR00" => "no_third_row_seating",
        "TR01" => "third_row_seating",
        "SU00" => "no_air_suspension",
        "SU01" => "air_suspension",
        "SC00" => "no_supercharger",
        "SC01" => "supercharger",
        "AU00" => "no_audio_upgrade",
        "AU01" => "audio_upgrade",
        "CH00" => "no_second_charger",
        "CH01" => "second_charger",
        "HP00" => "no_hpwc_ordered",
        "HP01" => "hpwc_ordered",
        "PA00" => "no_paint_armor",
        "PA01" => "pait_armor",
        "PS00" => "no_parcel_shelf",
        "PS01" => "parcel_shelf",
        "TP00" => "no_tech_package",
        "TP01" => "tech_package",
        "AD02" => "power_adapter_nema_14-50",
        "X001" => "power_lift_gate",
        "X003" => "navigation",
        "X007" => "premium_exterior_lighting",
        "X011" => "homelink",
        "X013" => "satellite_radio",
        "X014" => "standard_radio",
        "X019" => "performance_exterior",
        "X020" => "no_performance_exterior",
        "X024" => "performance_powertrain",
        "X025" => "no_performance_powertrain",
      }
*/