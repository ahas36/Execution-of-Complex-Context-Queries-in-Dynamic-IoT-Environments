/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.entitysimulator;

/**
 *
 * @author ali
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Random;

/**
 *
 * @author ali
 */
public class LocationHelper {

    private static Random random = new Random(System.currentTimeMillis());

    public static GeoCordinate getLocation(GeoCordinate center, Double radius) {

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(center.getLatitude()));

        double foundLongitude = new_x + center.getLongitude();
        double foundLatitude = y + center.getLatitude();
        //   System.out.println("Longitude: " + foundLongitude + "  Latitude: " + foundLatitude);
        return new GeoCordinate(foundLatitude, foundLongitude);
    }

    public static double distance(GeoCordinate poin1, GeoCordinate point2) {

        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(point2.getLatitude() - poin1.getLatitude());
        double lonDistance = Math.toRadians(point2.getLongitude() - poin1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(poin1.getLatitude())) * Math.cos(Math.toRadians(point2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public static void main(String[] args) {
        GeoCordinate center = new GeoCordinate(-37.8770, 145.0443);
        for (int i = 0; i < 10; i++) {
            GeoCordinate location = getLocation(center, 500.0);
            double distance = distance(location, center);
//            System.out.println(distance);

            System.out.println("\""+location.getLatitude() + "," + location.getLongitude()+"\""+",");
        }

    }
}
