import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;


class Device {
  private static final String SERVER_URL = "http://localhost:3000/";

  //Obtained from https://www.epa.gov/pm-pollution/national-ambient-air-quality-standards-naaqs-pm (and some extrapolation)
  private static final double PM1_THRESHOLD = 15;
  private static final double PM2_THRESHOLD = 30;
  private static final double PM4_THRESHOLD = 60;
  private static final double PM10_THRESHOLD = 150;

  private Sensor sensor;
  private String id;

  Device(String id) {
    this.id = id;
    sensor = new Sensor();
    sensor.start();
  }

  void init() {
    Timer timer = new Timer();
    // Data can be read every 1 second according to the datasheet
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Sensor.Measurement measurement = sensor.read();
        if (measurement != null) {
          processRead(measurement);
        }
      }
    }, 0, 1000);
  }

  private void processRead(Sensor.Measurement measurement) {
    sendMeasurement(measurement);

    if (measurement.pm1 >= PM1_THRESHOLD) {
      System.out.println("RED ALERT!! PM1 THRESHOLD EXCEEDED: " + measurement.pm1);
      sendAlert(1, measurement.pm1);
    }
    if (measurement.pm2 >= PM2_THRESHOLD) {
      System.out.println("RED ALERT!! PM2 THRESHOLD EXCEEDED: " + measurement.pm2);
      sendAlert(2, measurement.pm2);
    }
    if (measurement.pm4 >= PM4_THRESHOLD) {
      System.out.println("RED ALERT!! PM4 THRESHOLD EXCEEDED: " + measurement.pm4);
      sendAlert(4, measurement.pm4);
    }
    if (measurement.pm10 >= PM10_THRESHOLD) {
      System.out.println("RED ALERT!! PM10 THRESHOLD EXCEEDED: " + measurement.pm10);
      sendAlert(10, measurement.pm10);
    }
  }

  private void sendAlert(int particleSize, double measurement) {
    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
      String date = dtf.format(LocalDateTime.now());

      URL url = new URL(SERVER_URL + "alert");
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection)con;
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      String json = "{\"device\":\""+ id +"\",\"size\":\""+ particleSize +"\", \"measurement\":\""+ measurement +"\", \"date\":\""+ date + "\"}";
      byte[] out = json.getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      http.setFixedLengthStreamingMode(length);
      http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      http.connect();
      try(OutputStream os = http.getOutputStream()) {
        os.write(out);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendMeasurement(Sensor.Measurement measurement) {
    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
      String date = dtf.format(LocalDateTime.now());

      URL url = new URL(SERVER_URL + "measurement");
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection)con;
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      String json = "{\"device\":\""+ id +"\", \"pm1\":\""+ measurement.pm1 +"\", \"pm2\":\"" + measurement.pm2
          +"\", \"pm4\":\"" + measurement.pm4 + "\", \"pm10\":\"" + measurement.pm10 + "\", \"date\":\""+ date + "\"}";
      byte[] out = json.getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      http.setFixedLengthStreamingMode(length);
      http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      http.connect();
      try(OutputStream os = http.getOutputStream()) {
        os.write(out);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
