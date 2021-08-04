import java.util.*;
import java.io.*;

public class PathFinder {

  private UnweightedGraph graph;
  private Map<String, Integer> articles;
  private List<String> solution;
  private Queue<Integer> path;
  private List<Integer> visited;

  public PathFinder(String nodeFile, String edgeFile) {
      graph = new MysteryUnweightedGraphImplementation (true);
      this.articles = new HashMap<String, Integer>();
      this.solution = new ArrayList<String>();
      this.path = new LinkedList<Integer>();
      this.visited  = new ArrayList<Integer>();
  }

  // create a map that stores vertex and the number they correspond with. The number starts with 0.
  public Map<String, Integer> getMap(String nodeFile){
      try{
          Scanner scanner = new Scanner(new File(nodeFile));
          int n = 0;
          while (scanner.hasNext()) {
              String next = scanner.nextLine();
              if (!next.startsWith("#") && next.trim().length() > 0) {
                  articles.put(next, n);
                  n++; 
                  //System.out.println(next);
              }
          }
          scanner.close();
          //System.out.println(articles.get("Fish"));
          return articles;
      } 
      catch (FileNotFoundException e) {
          System.err.println("nodeFile not found.");
          return articles;
      }
  }

  // return the key by value from the articles map
  public String getKeyByValue(Map<String, Integer> map, int value) {
      String result = " ";
      for (Map.Entry<String, Integer> e : map.entrySet()) {
          if (e.getValue().equals(value)) {
              result = e.getKey();
              return result;
          }
      }
      return result;
  }

  // create an adjacent list with all the edges
  public UnweightedGraph getAdjacencyList(String edgeFile) {
      try{
          Scanner scanner = new Scanner(new File(edgeFile));
          for (int i = 0; i < articles.size(); i++) {
              graph.addVertex();
          }
          while (scanner.hasNext()) {
              String next = scanner.nextLine();
              //System.out.println(next);
              if (!next.startsWith("#") && next.trim().length() > 0) {
                  String[] eachLine = next.split("\t");
                  //System.out.println(eachLine[0] + " " + eachLine[1]);
                  graph.addEdge(articles.get(eachLine[0]), articles.get(eachLine[1]));
              }
          }
          scanner.close();
          return graph;
      }
      catch (FileNotFoundException e) {
          System.err.println("edgeFile not found.");
          return graph;
      }
  }

  // generate a list of all the neighbors of a vertice
  public List<Integer> getNeighborsList(int v) {
        List<Integer> neighbors = new ArrayList<Integer>();
        Iterator<Integer> neighborsIterator = this.graph.getNeighbors(v).iterator();
        while (neighborsIterator.hasNext()) {
            neighbors.add(neighborsIterator.next());
        }
        return neighbors;
  }

  //get the solution path by using hasEdge(). Iterate backward in the visited list, find which integer that cur has edge with, and repeat the same procedure until going back to the starting vertex
  public List<String> getTheSolution1(int origin, int end) {
      Stack<Integer> intSolution = new Stack<Integer>();
      intSolution.push(end);
      int cur = end;
      int index = -1;
      for (int i = 0; i < this.visited.size(); i++) {
          if (this.visited.get(i) == end) {
              index = i;
          }
      }
      while (cur != origin) {
          for (int j = 0; j < index; j++) {
              if (graph.hasEdge(this.visited.get(j), cur)) {
                  intSolution.push(this.visited.get(j));
                  cur = this.visited.get(j);
                  index = j;
              }
          }
      }
      while (!intSolution.isEmpty()) {
          this.solution.add(getKeyByValue(articles, intSolution.pop()));
      }
      //System.out.println(solution);
      return this.solution;
  }

  public List<String> getShortestPath(String node1, String node2) {
      int origin = articles.get(node1); 
      int end = articles.get(node2);
      this.path.add(origin);
      this.visited.add(origin);

      while (!this.path.isEmpty()) {
          int cur = this.path.peek();
          if (cur == end) {
              return getTheSolution1(origin, end);
          }
          cur = this.path.poll();
          List<Integer> neighbors = getNeighborsList(cur);
          for (int i = 0; i < neighbors.size(); i++) {
              if (!this.visited.contains(neighbors.get(i))) {
                  this.visited.add(neighbors.get(i));
                  this.path.add(neighbors.get(i));
              }
          }
      }
      //System.out.println(this.solution);
      System.out.println("There is no path between these two vertex!");
      return this.solution;
  }

  public int getShortestPathLength(String node1, String node2) {
      //System.out.println(this.solution.size()-1);
      return this.solution.size()-1;
  }

  // return a solution path when user inputs an intermediate vertice. This method uses getShortestPath(String node1, String node2) to find out if there is a path between origin and intermediate, and intermediate and end. If so, combine the two paths and return it
  public List<String> getTheSolution2 (String origin, String intermediate, String end) {
      Boolean intermediateEnd = false;
      Boolean originIntermediate = false;
      this.path.clear();
      this.visited.clear();

      List<String> intermediateEndList = new ArrayList<String>(getShortestPath(intermediate, end));
      if (!intermediateEndList.isEmpty()) {
          intermediateEnd = true;
          this.solution.clear();
          this.path.clear();
          this.visited.clear();
      }
      List<String> originIntermediateList = new ArrayList<String>(getShortestPath(origin, intermediate));
      if (!originIntermediateList.isEmpty()) {
          originIntermediate = true;
          this.solution.clear();
          this.path.clear();
          this.visited.clear();
      }
      if (intermediateEnd == true && originIntermediate == true) {
          this.solution = originIntermediateList;
          for (int i = 1; i < intermediateEndList.size(); i++) {
              this.solution.add(intermediateEndList.get(i));
          }
          //System.out.println(this.solution);
          return this.solution;
      }
      //System.out.println(this.solution);
      System.out.println("Sorry, no such path exist");
      return this.solution;
  }

  public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
      int origin = articles.get(node1); 
      int end = articles.get(node2);
      int intermediate = articles.get(intermediateNode);
      boolean reachIntermediate = false;

      this.path.add(origin);
      this.visited.add(origin);

      while (!this.path.isEmpty()) {
          int cur = this.path.peek();
          if (this.visited.contains(end) && reachIntermediate == true) {
              return getTheSolution2(node1, intermediateNode, node2);
          }
          cur = this.path.poll();
          List<Integer> neighbors = getNeighborsList(cur);
          for (int i = 0; i < neighbors.size(); i++) {
              if (!this.visited.contains(neighbors.get(i))) {
                  this.visited.add(neighbors.get(i));
                  this.path.add(neighbors.get(i));
                  if (neighbors.get(i) == intermediate) {
                      reachIntermediate = true;
                  }
              }
          }
      }
      System.out.println("There is no path between these two vertex!");
      return this.solution;
  }

  public static void main(String[] args) { 
      // doesn't take in parentheses. Type \ in front of each parenthesis. 
      String nodeFile = args[0];
      String edgeFile = args[1];
      String startVertex = args[2];

      PathFinder path = new PathFinder(nodeFile, edgeFile);
      path.getMap(nodeFile);
      path.getAdjacencyList(edgeFile);

      if (args.length == 4) {
          String endVertex = args[3];
          System.out.println(path.getShortestPath(startVertex, endVertex));
          System.out.println(path.getShortestPathLength(startVertex, endVertex));
      }

      else if (args.length == 5) {
          String intermediateVertex = args[3];
          String endVertex = args[4];
          System.out.println(path.getShortestPath(startVertex, intermediateVertex, endVertex));
          System.out.println(path.getShortestPathLength(startVertex, endVertex));
      }
  }
}