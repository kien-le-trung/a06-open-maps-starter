package comp210.a6;

import java.util.Comparator;

/**
 * Orders Dijkstra's priority queue by distance, smallest first, which makes
 * java.util.PriorityQueue a min-heap on distance. The ordering itself lives
 * in Pair.compareTo; this comparator uses it.
 */
class PriorityQueueComparator implements Comparator<Pair<Integer, Double>> {

    /**
     * Returns a negative number when pair1's distance is smaller, a positive
     * number when it is larger, and 0 when they are equal. Use Pair's
     * compareTo.
     */
    @Override
    public int compare(Pair<Integer, Double> pair1, Pair<Integer, Double> pair2) {
        throw new UnsupportedOperationException("Part 2: write PriorityQueueComparator.compare");
    }
}
