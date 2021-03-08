import java.util.Random;

class Sensor {
  private static final double MIN_MEASUREMENT = 0;
  private static final double MAX_MEASUREMENT = 1000;


  private boolean startedMeasurements = false;

  // According to the data sheet, in order to read, we need to start the measurements first
  void start() {
    startedMeasurements = true;
  }

  public void stop() {
    startedMeasurements = false;
  }

  Measurement read() {
    if (!startedMeasurements) {
      return null;
    }
    // Generate random measures with mean close to the threshold
    double pm1 = generateRandomMeasure(11, 1);
    double pm2 = generateRandomMeasure(25, 1);
    double pm4 = generateRandomMeasure(56, 1);
    double pm10 = generateRandomMeasure(145, 1);
    return new Measurement(pm1, pm2, pm4, pm10);
  }

  private double generateRandomMeasure(double mean, double sd) {
    Random r = new Random();
    return r.nextGaussian() * mean + sd ;
  }

  static class Measurement {
    double pm1, pm2, pm4, pm10;

    Measurement(double pm1, double pm2, double pm4, double pm10) {
      this.pm1 = pm1;
      this.pm2 = pm2;
      this.pm4 = pm4;
      this.pm10 = pm10;
    }
  }

}
