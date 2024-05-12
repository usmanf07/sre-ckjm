package gr.spinellis.ckjm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A container of class metrics mapping class names to their metrics.
 * This class contains the metrics for all classes during the filter's
 * operation. Some metrics need to be updated as the program processes
 * other classes, so the class's metrics will be recovered from this
 * container to be updated.
 *
 * @version $Revision: 1.9 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class ClassMetricsContainer {
    /** The map from class names to the corresponding metrics */
    private final Map<String, ClassMetrics> metricsMap = new HashMap<>();

    /**
     * Return a class's metrics.
     * If the metrics don't exist, create new metrics and add them to the map.
     */
    public ClassMetrics getMetrics(String name) {
        return metricsMap.computeIfAbsent(name, key -> new ClassMetrics());
    }

    /**
     * Print the metrics of all the visited classes.
     * Only print metrics for classes that are visited and meet the inclusion criteria.
     */
    public void printMetrics(CkjmOutputHandler handler) {
        for (Map.Entry<String, ClassMetrics> entry : metricsMap.entrySet()) {
            String className = entry.getKey();
            ClassMetrics classMetrics = entry.getValue();
            if (classMetrics.isVisited() && (MetricsFilter.includeAll() || classMetrics.isPublic())) {
                handler.handleClass(className, classMetrics);
            }
        }
    }
}
