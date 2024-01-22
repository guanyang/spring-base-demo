package org.gy.demo.webflux;

import lombok.Data;

import java.util.*;

public class Dijkstra01 {

    private final Map<Vertex, Map<Vertex, Integer>> graph; // 图的邻接表表示
    private final Map<Vertex, Integer> distances; // 起始顶点到其他所有顶点的最短距离
    private final Map<Vertex,Vertex> previousVertices; // 记录每个顶点的前一个顶点

    private final Set<Vertex> visited; // 记录每个顶点是否被访问过

    private final PriorityQueue<Vertex> queue; // 优先队列，使用优先队列来选择当前距离最小的未访问顶点

    public Dijkstra01(Map<Vertex, Map<Vertex, Integer>> graph) {
        this.graph = graph;
        distances = new HashMap<>();
        previousVertices = new HashMap<>();
        visited = new HashSet<>();
        queue = new PriorityQueue<>((v1, v2) -> distances.get(v1) - distances.get(v2));
    }

    public Map<Vertex, Integer> computeShortestPaths(Vertex startVertex) {
        // 初始化
        distances.put(startVertex, 0);
        for (Vertex vertex : graph.keySet()) {
            if (vertex != startVertex) {
                distances.put(vertex, Integer.MAX_VALUE);
            }
        }

        // 使用优先队列来选择当前距离最小的未访问顶点
        queue.add(startVertex);

        // 主循环
        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            // 如果当前顶点已经被访问过，则跳过
            if (visited.contains(currentVertex)) {
                continue;
            }
            // 标记当前顶点已访问过
            visited.add(currentVertex);

            // 遍历当前顶点的所有相邻顶点
            for (Map.Entry<Vertex, Integer> entry : graph.get(currentVertex).entrySet()) {
                int distanceToNeighbor = distances.get(currentVertex) + entry.getValue();
                if (distanceToNeighbor < distances.get(entry.getKey())) {
                    distances.put(entry.getKey(), distanceToNeighbor);
                    previousVertices.put(entry.getKey(), currentVertex);
                    queue.add(entry.getKey());
                }
            }
        }

        return distances;
    }

    public List<Vertex> getPath(Vertex startVertex, Vertex endVertex) {
        List<Vertex> path = new ArrayList<>();
        Vertex currentVertex = endVertex;
        while (currentVertex != null) {
            path.add(currentVertex);
            currentVertex = previousVertices.get(currentVertex);
        }
        Collections.reverse(path);
        return path;
    }


    //时间复杂度：Dijkstra算法的时间复杂度可以达到O(E log V)，其中E是图中边的条数，V是图中顶点的个数
    //空间复杂度：Dijkstra算法的额外空间复杂度为O(V)，其中V是图中顶点的个数
    public static void main(String[] args) {
        // 创建一个图的邻接表表示
        Map<Vertex, Map<Vertex, Integer>> graph = new HashMap<>();
        Vertex A = new Vertex("A");
        Vertex B = new Vertex("B");
        Vertex C = new Vertex("C");
        Vertex D = new Vertex("D");
        Vertex E = new Vertex("E");
        graph.put(A, new HashMap<>());
        graph.get(A).put(B, 1);
        graph.get(A).put(C, 4);
        graph.put(B, new HashMap<>());
        graph.get(B).put(C, 2);
        graph.get(B).put(D, 5);
        graph.put(C, new HashMap<>());
        graph.get(C).put(D, 8);
        graph.get(C).put(E, 6);
        graph.put(D, new HashMap<>());
        graph.get(D).put(E, 3);
        graph.put(E, new HashMap<>());

        // 创建Dijkstra算法对象
        Dijkstra01 dijkstra = new Dijkstra01(graph);

        // 计算从顶点A到其他所有顶点的最短路径
        Map<Vertex, Integer> distances = dijkstra.computeShortestPaths(A);

        // 打印结果
        for (Vertex vertex : distances.keySet()) {
            System.out.println("最短路径从A到" + vertex + "的距离为：" + distances.get(vertex));
        }

        // 打印从顶点A到顶点E的最短路径
        List<Vertex> path = dijkstra.getPath(A, E);
        System.out.println("最短路径从A到E为：" + path);
    }

    @Data
    static class Vertex {

        private final String name;

        public Vertex(String name) {
            this.name = name;
        }
    }
}
