package com.certimetergroup.smart.travel.utils;


import shared.Coordinates;

public class GeoUtils {

  private static final double EARTH_RADIUS_KM = 6371.0;

  public static double haversineDistance(Coordinates coord1, Coordinates coord2) {
    double lat1 = Math.toRadians(coord1.lat);
    double lon1 = Math.toRadians(coord1.lng);
    double lat2 = Math.toRadians(coord2.lat);
    double lon2 = Math.toRadians(coord2.lng);

    double deltaLat = lat2 - lat1;
    double deltaLng = lon2 - lon1;

    double a = Math.pow(
        Math.sin(deltaLat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(
        Math.sin(deltaLng / 2)
        , 2);

    double distance = 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    // Round to two decimal places
    return Math.round(distance * 100.0) / 100.0;
  }
}
