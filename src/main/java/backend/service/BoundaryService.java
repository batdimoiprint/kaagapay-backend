package backend.service;

import org.springframework.stereotype.Service;

/**
 * Ray Casting point-in-polygon check for Barangay Sta. Lucia boundary.
 */
@Service
public class BoundaryService {

    // Barangay Sta. Lucia polygon (13 vertices, in order)
    private static final double[] POLYGON_LATS = {
            14.705546742995335,
            14.705671271884409,
            14.709874090333209,
            14.710112761835896,
            14.710133507607654,
            14.708224081730991,
            14.705121196604228,
            14.70452968681381,
            14.704259946944985,
            14.702246716085053,
            14.702277847007846,
            14.704218433164224,
            14.704145790170763
    };

    private static final double[] POLYGON_LNGS = {
            121.0458561371346,
            121.046800278232,
            121.04670371406284,
            121.04930013143554,
            121.05037303700637,
            121.05481475202718,
            121.05706777968568,
            121.05690683319395,
            121.0510918089481,
            121.04935373500939,
            121.04786244086915,
            121.04707922743417,
            121.04634966517708
    };

    /**
     * Ray Casting algorithm — checks if (lat, lng) is inside the polygon.
     * Casts a ray from the point to the right (+lng direction) and counts
     * how many polygon edges it crosses. Odd = inside, even = outside.
     */
    public boolean isWithinBoundary(double lat, double lng) {
        int n = POLYGON_LATS.length;
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = POLYGON_LATS[i], xi = POLYGON_LNGS[i];
            double yj = POLYGON_LATS[j], xj = POLYGON_LNGS[j];

            if ((yi > lat) != (yj > lat)) {
                double xIntersect = (xj - xi) * (lat - yi) / (yj - yi) + xi;
                if (lng < xIntersect) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }
}
