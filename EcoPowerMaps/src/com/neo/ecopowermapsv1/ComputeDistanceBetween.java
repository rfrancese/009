package com.neo.ecopowermapsv1;

public class ComputeDistanceBetween {
	public double distance(double lat1, double lat2, double lon1, double lon2) {
		final int R = 6371; 
	    
		double latDistance = deg2rad(lat2 - lat1);
	    double lonDistance = deg2rad(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; 
	    distance = Math.pow(distance, 2);
	    
	    return Math.sqrt(distance);
	}

	public double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
}
