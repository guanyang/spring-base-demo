package org.gy.demo.webflux;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AlgorithmTest {

    public static void main(String[] args) {
//        int[] ints = randomIntArray(10, 30, 3);
//        printArray(ints);
//        bubbleSort(ints);
//        quickSort(ints, 0, ints.length - 1);
//        mergeSort(ints, 0, ints.length - 1);
//        heapSort(ints);
//        printArray(ints);

//        System.out.println(climbingStairsDFSMem(10));
//        System.out.println(climbingStairsDP(10));

//        int index = binarySearchLastValue(ints, 11);
//        System.out.println(index == -1 ? "无" : ints[index] + "\tindex=" + index);

//        //动态规划-背包问题
//        int cap = 10;
//        int[] wgt = {1, 2, 3, 4, 5};
//        int[] val = {6, 10, 12, 16, 20};
//        //构建最优解二维数组
//        int[][] dp = unboundedKnapsackDP(wgt, val, cap);
//        //打印最优解数组
//        printArray(dp);
//        //最优解回溯，如果装了第i个物品，则int[i]=1，否则int[i]=0
//        int[] items = unboundedKnapsackDPBacktrace(dp, wgt, val);
//        printArray(items);

        //动态规划-硬币兑换
        int max  = 100;
        int amt = 27;
        int[] coins = {1, 5, 10, 25};
        //最优解数组
        int[][] dp = coinChangeDP(coins, amt, max);
        //打印最优解数组
        printArray(dp);
        //最优解回溯，如果使用第i种硬币，则int[i]=1，否则int[i]=0
        int[] items = coinChangeDPBacktrace(dp, coins, max);
        printArray(items);

    }



    public static int[] coinChangeDPBacktrace(int[][] dp, int[] coins, int maxValue) {
        int n = dp.length;
        int[] items = new int[n];
        int j = dp[0].length - 1;
        for (int i = n - 1; i >= 1; i--) {
            if (dp[i][j] == dp[i - 1][j]) {
                items[i] = 0;
            } else {
                while (j - coins[i - 1] >= 0 && dp[i][j] == dp[i][j - coins[i - 1]] + 1) {
                    items[i] = items[i] + 1;
                    j = j - coins[i - 1];
                }
            }
        }
        return items;
    }

    /* 零钱兑换：空间优化后的动态规划 */
    int coinChangeDPComp(int[] coins, int amt,int maxValue) {
        int n = coins.length;
        // 初始化 dp 表
        int[] dp = new int[amt + 1];
        Arrays.fill(dp, maxValue);
        dp[0] = 0;
        // 状态转移
        for (int i = 1; i <= n; i++) {
            for (int a = 1; a <= amt; a++) {
                if (coins[i - 1] > a) {
                    // 若超过目标金额，则不选硬币 i
                    dp[a] = dp[a];
                } else {
                    // 不选和选硬币 i 这两种方案的较小值
                    dp[a] = Math.min(dp[a], dp[a - coins[i - 1]] + 1);
                }
            }
        }
        return dp[amt] != maxValue ? dp[amt] : -1;
    }

    /* 零钱兑换：动态规划 */
    //状态定义：dp[i][j]表示使用前i种硬币凑成金额j的最少硬币数
    //状态转移方程：dp[i][j] = min(dp[i-1][j], dp[i][j-coins[i-1]] + 1)
    public static int[][] coinChangeDP(int[] coins, int amt, int maxValue) {
        int n = coins.length;
        // 初始化 dp 表，用于存储子问题的解
        int[][] dp = new int[n + 1][amt + 1];
        int MAX = maxValue;
        // 状态转移：首行
        for (int a = 1; a <= amt; a++) {
            dp[0][a] = MAX;
        }
        // 状态转移：其余行和列
        for (int i = 1; i <= n; i++) {
            for (int a = 1; a <= amt; a++) {
                if (coins[i - 1] > a) {
                    // 若超过目标金额，则不选硬币 i
                    dp[i][a] = dp[i - 1][a];
                } else {
                    // 不选和选硬币 i 这两种方案的较小值
                    dp[i][a] = Math.min(dp[i - 1][a], dp[i][a - coins[i - 1]] + 1);
                }
            }
        }
        return dp;
    }

    //完全背包问题最优解回溯
    //1. 定义int[]数组，如果装了第i个物品，则int[i]=1，如果有多个，则累加计算，否则int[i]=0
    //2. 根据状态转移方程推导，如果dp[i][j] = dp[i - 1][j]，说明没有选择第i个商品，int[i]=0，则回到dp[i-1,j];
    //3. 如果dp[i][j] = dp[i][j - wgt[i - 1]] + val[i - 1]，说明装了第i个商品，该商品是最优解组成的一部分，int[i]=1，随后我们得回到装该商品之前，即回到dp[i,j-w(i-1)]，再循环处理；
    public static int[] unboundedKnapsackDPBacktrace(int[][] dp, int[] wgt, int[] val) {
        int n = dp.length;
        //定义int[]数组，如果装了第i个物品，则int[i]=1，出现多次则累加，否则int[i]=0
        int[] item = new int[n];
        int j = dp[0].length - 1;
        for (int i = n - 1; i >= 1; i--) {
            if (dp[i][j] == dp[i - 1][j]) {
                //说明没有选择第i个商品，int[i]=0，则回到dp[i-1,j];
                item[i] = 0;
            } else {
                //如果dp[i][j] = dp[i][j - wgt[i - 1]] + val[i - 1]，说明装了第i个商品，该商品是最优解组成的一部分，int[i]=1，随后我们得回到装该商品之前，即回到dp[i,j-w(i-1)]，再循环处理；
                while (j - wgt[i - 1] >= 0&& dp[i][j] == dp[i][j - wgt[i - 1]] + val[i - 1]) {
                    item[i] = item[i] + 1;
                    j = j - wgt[i - 1];
                }
            }
        }
        return item;
    }

    /* 完全背包：空间优化后的动态规划 */
    int unboundedKnapsackDPComp(int[] wgt, int[] val, int cap) {
        int n = wgt.length;
        // 初始化 dp 表
        int[] dp = new int[cap + 1];
        // 状态转移
        for (int i = 1; i <= n; i++) {
            for (int c = 1; c <= cap; c++) {
                if (wgt[i - 1] > c) {
                    // 若超过背包容量，则不选物品 i
                    dp[c] = dp[c];
                } else {
                    // 不选和选物品 i 这两种方案的较大值
                    dp[c] = Math.max(dp[c], dp[c - wgt[i - 1]] + val[i - 1]);
                }
            }
        }
        return dp[cap];
    }

    /* 完全背包：动态规划 */
    //状态转移方程：dp[i][j] = max(dp[i-1][j], dp[i][j-wgt[i-1]] + val[i-1])
    public static int[][] unboundedKnapsackDP(int[] wgt, int[] val, int cap) {
        int n = wgt.length;
        // 初始化 dp 表
        int[][] dp = new int[n + 1][cap + 1];
        // 状态转移
        for (int i = 1; i <= n; i++) {
            for (int c = 1; c <= cap; c++) {
                if (wgt[i - 1] > c) {
                    // 若超过背包容量，则不选物品 i
                    dp[i][c] = dp[i - 1][c];
                } else {
                    // 不选和选物品 i 这两种方案的较大值
                    dp[i][c] = Math.max(dp[i - 1][c], dp[i][c - wgt[i - 1]] + val[i - 1]);
                }
            }
        }
        return dp;
    }

    //0-1 背包：动态规划-最优解回溯，思路如下：
    //1. 定义int[]数组，如果装了第i个物品，则int[i]=1，否则int[i]=0
    //2. 根据状态转移方程推导，如果dp[i][j] = dp[i - 1][j]，说明没有选择第i个商品，int[i]=0，则回到dp[i-1,j];
    //3. 如果dp[i][j] = dp[i - 1][j - wgt[i - 1]] + val[i - 1]，说明装了第i个商品，该商品是最优解组成的一部分，int[i]=1，随后我们得回到装该商品之前，即回到dp[i-1,j-w(i-1)]；
    public static int[] knapsackDPBacktrace(int[][] dp, int[] wgt, int[] val) {
        int n = dp.length;
        //定义int[]数组，如果装了第i个物品，则int[i]=1，否则int[i]=0
        int[] item = new int[n];
        int j = dp[0].length - 1;
        for (int i = n - 1; i >= 1; i--) {
            if (dp[i][j] == dp[i - 1][j]) {
                //说明没有选择第i个商品，int[i]=0，则回到dp[i-1,j];
                item[i] = 0;
            } else if (j - wgt[i - 1] >= 0 && dp[i][j] == dp[i - 1][j - wgt[i - 1]] + val[i - 1]) {
                //说明装了第i个商品，该商品是最优解组成的一部分，int[i]=1，随后我们得回到装该商品之前，即回到dp[i-1,j-w(i-1)]；
                item[i] = 1;
                j = j - wgt[i - 1];
            }
        }
        return item;
    }

    /* 0-1 背包：空间优化后的动态规划 */
    int knapsackDPComp(int[] wgt, int[] val, int cap) {
        int n = wgt.length;
        // 初始化 dp 表
        int[] dp = new int[cap + 1];
        // 状态转移
        for (int i = 1; i <= n; i++) {
            // 倒序遍历
            for (int c = cap; c >= 1; c--) {
                if (wgt[i - 1] <= c) {
                    // 不选和选物品 i 这两种方案的较大值
                    dp[c] = Math.max(dp[c], dp[c - wgt[i - 1]] + val[i - 1]);
                }
            }
        }
        return dp[cap];
    }

    /* 0-1 背包：动态规划 */
    //1. 定义状态[i,j]，建立dp表：dp[i,j]表示容量为j时，放入第i件物品的最大价值
    //2. 确定最优子结构，推导状态转移方程，即dp[i,j] = max(dp[i-1,j], dp[i-1,j-wgt[i-1]]+val[i-1])
    //3. 确定边界条件，即dp[0,j] = 0, dp[i,0] = 0
    //4. 确定状态转移顺序, 状态[i,j]只能从上方[i-1,j]和左上方[i-1,j-wgt[i-1]]转移过来
    //5. 物品重量价值定义：wgt[i-1]表示第i件物品的重量，val[i-1]表示第i件物品的价值
    //6. 问题最优解，即dp[n,cap]
    public static int[][] knapsackDP(int[] wgt, int[] val, int cap) {
        int n = wgt.length;
        // 初始化 dp 表，用于存储子问题的解
        int[][] dp = new int[n + 1][cap + 1];
//        dp[0][0] = 0;
//        //初始化边界条件，即dp[0,j] = 0, dp[i,0] = 0
//        for (int i = 1; i <= n; i++) {
//            dp[i][0] = 0;
//        }
//        for (int j = 1; j <= cap; j++) {
//            dp[0][j] = 0;
//        }
        // 状态转移
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= cap; j++) {
                if (wgt[i - 1] > j) {
                    // 若超过背包容量，则不选物品 i
                    dp[i][j] = dp[i - 1][j];
                } else {
                    // 若没超过背包容量，则选物品 i, 则取两种情况中的最大值
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - wgt[i - 1]] + val[i - 1]);
                }
            }
        }
        return dp;
    }

    /* 爬楼梯：空间优化后的动态规划 */
    //在动态规划问题中，当前状态往往仅与前面有限个状态有关，这时我们可以只保留必要的状态，通过“降维”来节省内存空间。这种空间优化技巧被称为“滚动变量”或“滚动数组”。
   public static int climbingStairsDPComp(int n) {
        if (n == 1 || n == 2)
            return n;
        int a = 1, b = 2;
        for (int i = 3; i <= n; i++) {
            int tmp = b;
            b = a + b;
            a = tmp;
        }
        return b;
    }


    /* 爬楼梯：动态规划 */
    public static int climbingStairsDP(int n) {
        if (n == 1 || n == 2)
            return n;
        // 初始化 dp 表，用于存储子问题的解
        int[] dp = new int[n + 1];
        // 初始状态：预设最小子问题的解
        dp[1] = 1;
        dp[2] = 2;
        // 状态转移：从较小子问题逐步求解较大子问题
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /* 爬楼梯：记忆化搜索 */
    public static int climbingStairsDFSMem(int n) {
        // mem[i] 记录爬到第 i 阶的方案总数，-1 代表无记录
        int[] mem = new int[n + 1];
        Arrays.fill(mem, -1);
        return dfs(n, mem);
    }

    /* 记忆化搜索 */
    private static int dfs(int i, int[] mem) {
        // 已知 dp[1] 和 dp[2] ，返回之
        if (i == 1 || i == 2)
            return i;
        // 若存在记录 dp[i] ，则直接返回之
        if (mem[i] != -1)
            return mem[i];
        // dp[i] = dp[i-1] + dp[i-2]
        int count = dfs(i - 1, mem) + dfs(i - 2, mem);
        // 记录 dp[i]
        mem[i] = count;
        return count;
    }

    /* 堆排序 */
    public static void heapSort(int[] nums) {
        // 建堆操作：堆化除叶节点以外的其他所有节点
        for (int i = nums.length / 2 - 1; i >= 0; i--) {
            siftDown(nums, nums.length, i);
        }
        // 从堆中提取最大元素，循环 n-1 轮
        for (int i = nums.length - 1; i > 0; i--) {
            // 交换根节点与最右叶节点（交换首元素与尾元素）
            swap(nums, 0, i);
            // 以根节点为起点，从顶至底进行堆化
            siftDown(nums, i, 0);
        }
    }

    /* 堆的长度为 n ，从节点 i 开始，从顶至底堆化 */
    private static void siftDown(int[] nums, int n, int i) {
        while (true) {
            // 判断节点 i, l, r 中值最大的节点，记为 ma
            int l = 2 * i + 1;
            int r = 2 * i + 2;
            int ma = i;
            if (l < n && nums[l] > nums[ma]) ma = l;
            if (r < n && nums[r] > nums[ma]) ma = r;
            // 若节点 i 最大或索引 l, r 越界，则无须继续堆化，跳出
            if (ma == i) break;
            // 交换两节点
            swap(nums, i, ma);
            // 循环向下堆化
            i = ma;
        }
    }

    /* 归并排序 */
    public static void mergeSort(int[] nums, int left, int right) {
        // 终止条件
        if (left >= right) return; // 当子数组长度为 1 时终止递归
        // 划分阶段
        int mid = (left + right) / 2; // 计算中点
        mergeSort(nums, left, mid); // 递归左子数组
        mergeSort(nums, mid + 1, right); // 递归右子数组
        // 合并阶段
        merge(nums, left, mid, right);
    }

    /* 合并左子数组和右子数组 */
    private static void merge(int[] nums, int left, int mid, int right) {
        // 左子数组区间 [left, mid], 右子数组区间 [mid+1, right]
        // 创建一个临时数组 tmp ，用于存放合并后的结果
        int[] tmp = new int[right - left + 1];
        // 初始化左子数组和右子数组的起始索引
        int i = left, j = mid + 1, k = 0;
        // 当左右子数组都还有元素时，比较并将较小的元素复制到临时数组中
        while (i <= mid && j <= right) {
            if (nums[i] <= nums[j]) tmp[k++] = nums[i++];
            else tmp[k++] = nums[j++];
        }
        // 将左子数组和右子数组的剩余元素复制到临时数组中
        while (i <= mid) {
            tmp[k++] = nums[i++];
        }
        while (j <= right) {
            tmp[k++] = nums[j++];
        }
        // 将临时数组 tmp 中的元素复制回原数组 nums 的对应区间
        for (k = 0; k < tmp.length; k++) {
            nums[left + k] = tmp[k];
        }
    }


    //实现快速排序算法
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);
            quickSort(arr, low, pivot - 1);
            quickSort(arr, pivot + 1, high);
        }
    }

    /* 哨兵划分 */
    private static int partition(int[] nums, int left, int right) {
        // 以 nums[left] 为基准数
        int i = left, j = right;
        while (i < j) {
            while (i < j && nums[j] >= nums[left]) j--;          // 从右向左找首个小于基准数的元素
            while (i < j && nums[i] <= nums[left]) i++;          // 从左向右找首个大于基准数的元素
            swap(nums, i, j); // 交换这两个元素
        }
        swap(nums, i, left);  // 将基准数交换至两子数组的分界线
        return i;             // 返回基准数的索引
    }

    /**
     * 查找第一个大于给定值的元素
     *
     * @param nums   数组
     * @param length 数组的长度
     * @param value  给定的值
     * @return
     */
    public static int bserachFirstOverVlaue(int[] nums, int value) {
        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (nums[mid] > value) {
                // 判断当前是第一个元素或者前一个元素小于等于给定值，则返回下标，如果前一个元素大于给定的值，则继续往前查找。
                if ((mid == 0) || nums[mid - 1] <= value) return mid;
                else high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    /**
     * 查找第一个等于给定值的元素
     *
     * @param nums
     * @param length
     * @param value
     * @return
     */
    public static int bsearchFirstValue(int[] nums, int value) {
        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (nums[mid] > value) {
                high = mid - 1;
            } else if (nums[mid] < value) {
                low = mid + 1;
            } else {
                // 判断当前是第一个元素或者前一个元素不等于要查找的值，则返回下标，如果前一个元素也等于要查找的值，则继续往前查找。
                if ((mid == 0) || (nums[mid - 1] != value)) return mid;
                else high = mid - 1;
            }
        }
        return -1;
    }

    //二分查找-查找最后一个小于等于给定值的索引，使用场景：二分查找索引文件
    public static int binarySearchLastValue(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = (left + right)/2;
            if (nums[mid] > target) {
                right = mid - 1;
            } else {
                // 判断当前是最后一个元素或者后一个元素大于要查找的值，则返回下标，如果后一个元素也等于要查找的值，则继续往后查找。
                if ((mid == nums.length - 1) || (nums[mid + 1] > target)) return mid;
                else left = mid + 1;
            }
        }
        return -1;
    }

    //二分查找
    public static int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }


    //冒泡排序
    public static int[] bubbleSort(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
        return arr;
    }


    //斐波拉契数列实现
    //数列中的每个数字是前两个数字的和，即f（n）= f（n-1）+ f（n-2）
    public static int fibonacci(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    //算法实现n的阶乘
    public static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    //随机生成int数组
    private static int[] randomIntArray(int len, int max) {
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(max);
        }
        return arr;
    }

    private static int[] randomIntArray(int len, int max, int sameLimit) {
        int[] arr = new int[len + sameLimit];
        for (int i = 0; i < len; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(max);
        }
        int sameInt = ThreadLocalRandom.current().nextInt(max);
        for (int i = 0; i < sameLimit; i++) {
            arr[len + i] = sameInt;
        }
        return arr;
    }

    //交换数组中的两个元素
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private static void printArray(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            printArray(arr[i]);
        }
    }

    //打印数组元素
    private static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
}
