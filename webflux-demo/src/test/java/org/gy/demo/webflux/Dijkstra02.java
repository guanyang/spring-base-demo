package org.gy.demo.webflux;

import lombok.Data;

import java.util.*;

public class Dijkstra02 {

    private final Map<Vertex, Map<Vertex, Integer>> graph; // 图的邻接表表示
    private final Map<Vertex, Integer> distances; // 起始顶点到其他所有顶点的最短距离
    private final Map<Vertex, Vertex> previousVertices; // 记录每个顶点的前一个顶点
    private PriorityQueue<Vertex> queue; // 优先队列，使用启发式函数对顶点进行排序

    public Dijkstra02(Map<Vertex, Map<Vertex, Integer>> graph) {
        this.graph = graph;
        distances = new HashMap<>();
        previousVertices = new HashMap<>();
    }

    public Map<Vertex, Integer> computeShortestPaths(Vertex startVertex, Vertex endVertex) {
        // 初始化
        distances.put(startVertex, 0);
        for (Vertex vertex : graph.keySet()) {
            if (vertex != startVertex) {
                distances.put(vertex, Integer.MAX_VALUE);
            }
        }

        // 使用优先队列来选择当前距离最小的未访问顶点
        queue = new PriorityQueue<>(this::getEstimatedDistanceToTarget);

        // 将起始顶点加入队列
        queue.add(startVertex);

        // 主循环
        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();

            // 如果当前顶点是目标顶点，则算法结束
            if (currentVertex == endVertex) {
                break;
            }

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

    private int getEstimatedDistanceToTarget(Vertex vertex,Vertex endVertex) {
        // 使用曼哈顿距离作为启发式函数
        int dx = Math.abs(vertex.getX() - endVertex.getX());
        int dy = Math.abs(vertex.getY() - endVertex.getY());
        return distances.get(vertex) + dx + dy;
    }

    //时间复杂度：Dijkstra算法的时间复杂度可以达到O(E log V)，其中E是图中边的条数，V是图中顶点的个数
    //空间复杂度：Djkstra算法的空间复杂度为O(V)，因为需要存储每个顶点到起始顶点的最短距离
    //算法优化思路：
    //1)使用启发式函数： 在选择当前距离最小的未访问顶点时，可以使用启发式函数来估计从该顶点到目标顶点的最短距离。启发式函数可以帮助算法更快地找到最优路径。例如，在寻路问题中，可以使用曼哈顿距离或欧几里得距离作为启发式函数。
    //2)使用分层图（hierarchical graph）表示： 将图划分为多个层次，然后在每个层次上分别运行Dijkstra算法。这样可以减少算法的时间复杂度。例如，在道路网络中，可以将道路划分为不同的级别，然后在每级道路上分别运行Dijkstra算法。
    //3)并行化算法： 如果有多个处理器可用，可以将算法并行化。例如，可以将图划分为多个子图，然后在每个子图上分别运行Dijkstra算法。
    //4)使用双向搜索： 可以同时从起始顶点和目标顶点开始运行Dijkstra算法，并在两个算法相遇时停止。这样可以减少算法的时间复杂度。例如，在寻路问题中，可以同时从起点和终点开始搜索，并在两条路径相遇时停止。
    //5)使用斐波那契堆（Fibonacci heap）代替优先队列： 斐波那契堆是一种高效的优先队列数据结构，具有更快的插入、删除和合并操作。使用斐波那契堆可以将算法的时间复杂度从O(V^2)优化到O(E + V log V)，其中E是图中边的条数。
    public static void main(String[] args) {
        // 创建一个图的邻接表表示
        Map<Vertex, Map<Vertex, Integer>> graph = new HashMap<>();
        Vertex A = new Vertex("A", 0, 0);
        Vertex B = new Vertex("B", 1, 1);
        Vertex C = new Vertex("C", 2, 2);
        Vertex D = new Vertex("D", 3, 3);
        Vertex E = new Vertex("E", 4, 4);
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
        Dijkstra02 dijkstra = new Dijkstra02(graph);

        // 计算从顶点A到顶点E的最短路径
        Map<Vertex, Integer> distances = dijkstra.computeShortestPaths(A, E);

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

        private String name;
        private int x;
        private int y;

        public Vertex(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
}
