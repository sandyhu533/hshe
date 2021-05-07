import java.util.HashMap;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


class Main {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> m = new HashMap<>();
for(int i=0; i<nums.length; i++) {
    int j = m.getOrDefault(target-nums[i],-1);
    if (j>=0) {
        return new int[]{i,j};
    } else {
        m.put(nums[i],i);
    }
}
return null;
    }
    // 
    /* */
    public static void main(String[] args) {
        Scanner cin = new Scanner(System.in);
        int n = cin.nextInt();
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = cin.nextInt();
        }
        int target = cin.nextInt();
        
        int[] res = twoSum(nums, target);
        System.out.println(res[1] + " " + res[0]);
    }
}


