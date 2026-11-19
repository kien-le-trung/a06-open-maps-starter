# Final Project Write-up

**Name:** Kien Le
**Onyen:** kientrle
**BRIDGES URL:** http://bridges-cs.herokuapp.com/assignments/6/kientrle
**Destination (DESTINATION_NAME in Main.java):**

Four questions, 20 points, plus 5 points for a BRIDGES URL that shows all four pictures. A paragraph or two each. Use the numbers your program printed
for your destination, and where a question asks for a number, show where it
came from.

---

## Question 1: Fewest intersections or fewest meters (5 points)

Paste the two lines your program printed for the BFS route and the Dijkstra
route.

Picture 3 shows both routes: BFS in orange, Dijkstra in red, and shared
intersections in purple. Where do the two routes split, and where do they meet
again? Describe it in terms of the map. A road name from openstreetmap.org
helps.

Explain why BFS chose its route and why Dijkstra chose its own. Each route is
the best one by some measure. Name the measure for each.

If your two routes are the same, explain how that can happen. Then pick a
second destination where they differ, and report its numbers too.

```
BFS route:      54 steps, 7220 m (4059 intersections reachable)
Dijkstra route: 55 steps, 6496 m

The two routes split right at Sitterson, and meet again at Raleigh Road

BFS minimizes the number of steps we take (54 < 55). Dijkstra minimizes the number of meters we have to walk (6496 < 7220)
BFS measure: Number of edges
Dijkstra measure: Meters
```

---

## Question 2: Right answers, wrong algorithm (5 points)

This is the shortest path code from the original BRIDGES version of this
assignment, with its ordering written as a `compareTo` on `Pair`. Run it on
Chapel Hill and every distance matches yours.

```java
class Pair<K, V extends Comparable<V>> implements Comparable<Pair<K, V>> {
  // the key, the value, getKey(), and getValue(), the same as your Pair

  public int compareTo(Pair<K, V> other) {
    if (getValue().compareTo(other.getValue()) < 0)
      return 1;
    else if (getValue().compareTo(other.getValue()) > 0)
      return -1;
    return 0;
  }
}

  public static void dijkstra (GraphAdjList<Integer, OsmVertex, Double> gr,
                               int source,
                               HashMap<Integer, Double> distance,
                               HashMap<Integer, Integer> parent) {
    for (Map.Entry<Integer, Element<OsmVertex> > pai : gr.getVertices().entrySet())
      distance.put (pai.getKey(), Double.MAX_VALUE);
    distance.put(source, 0.);

    PriorityQueue<Pair<Integer, Double>> pq = new PriorityQueue<Pair<Integer, Double>> (100);
    pq.add(new Pair<Integer, Double>(source, distance.get(source)));

    while (!pq.isEmpty()) {
      int from = pq.peek().getKey();
      pq.remove();
      double d_from = distance.get(from);

      SLelement<Edge<Integer, Double>> list = gr.getAdjacencyList(from);
      while (list != null) {
        int  to = list.getValue().getTo();
        double edgelength = list.getValue().getEdgeData();
        double pathlength = d_from + edgelength;
        if (pathlength < distance.get(to)) {
          distance.put(to, pathlength);
          pq.add(new Pair<Integer, Double>(to, pathlength));
          parent.put(to, from);
        }
        list = list.getNext();
      }
    }
  }
```

Explain why this is not Dijkstra's algorithm. In what order does it take
vertices out of the queue, and what does it do that your `dijkstra` does not?

Suppose you added your two checks to this loop. Which one fails first, and
why?

Last, explain why a test that compares only the final distances cannot tell
this code apart from yours.

```
This is not Dijkstra since the queue stores the larger distances before smaller distances due to the way compareTo is
written. It processes all the stale entries instead of skipping them like my Dijkstra, which is also a consequence of
the reverse priority queue.

Check 1 would fail first. Check 1 dictates that the current distance must be larger or equal to distance in the previous
iteration. However, in this max-PQ, the later distances will be smaller than previous distances. Thus, check 1 fails.

This Dijkstra still updates the distance HashMap if it finds smaller distances. Hence, given the same graph, it would
also end up with the same shortest distance result, just with more iterations. Hence, comparing final results would 
not differentiate the two versions.
```

---

## Question 3: Settled means final (5 points)

Draw your `negativeEdgeGraph` (ASCII art is fine), with every edge weight.

Walk through your `dijkstra` on it from vertex 0. List the vertices in the
order they are settled, and stop at the moment check 2 fails. Paste the
message your exception printed.

What promise does the settled-set invariant make, and how does the negative
edge break it? Road lengths are never negative. Name one other kind of edge
weight where a negative value could show up.

```
source ----2.0----> Node 1
     -                   ^
      -                  |
       5.0              -4.0
         -             |
           - > Node 2 |
           
source, Node 1, Node 2
"Check 2 fails. 1 is already in settled"

The settled-set invariant promises that any settled vertex already has the shortest distance. The negative edge
breaks this by decreasing the distance when moving from Node 2 to Node 1. If all edges are non-negative, the
total distance can only go up from 5 (which is already larger than 2), and Node 1 can never have a shorter distance
than 2.

If we use the edge weights to indicate profits for delivery of orders. A negative edge might come up a road with higher
tax or a very long road.
```

---

## Question 4: Your feature (Part 3) (5 points)

What does your feature show? Explain why someone in Chapel Hill might want
to know it.

Which data structures did you use? For each one, name another structure
from the course you could have used instead, and explain why you chose the
one you did.

Describe one design decision you made and one approach you tried or
considered and rejected.

If you used AI for this part, say what you asked for and what you changed
in what it gave you.

```
My feature shows the shortest route in part 2, and the shortest route if we make a detour to grab a coffee from
the closest coffee shop on campus, and the estimated additional walking time needed. Many students buy coffee
between classes everyday, so it would be useful to know if we can make it to Starbucks, then to the next class
within our schedule.

ArrayList to store list of coffee shops. Array would be another choice, but ArrayList is resizable in case more
coffee shops, or other shops, are added to the list.
ArrayList to store routes. Array would be another choice, but ArrayList is resizable and can accommodate routes
of different lengths, such as in merging two routes together.

Design: I created a private nested class CoffeeShop, with a helper to retrieve the nearest vertex given lat, lon. This
is cleaner and easier to trace as all information is packaged and function is suitably named.
Initially, I stored distances through all coffee shops in a list, and return the min of that list. However, I
realized I only need the shortest one, so I just kept some variables before the loop and overwrote them.

I didn't use AI for this part. I came to it with question and it gave me back more questions, so I got frustrated.
```
