import java.util.HashMap;
import java.util.Arrays;

class Main {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> m = new HashMap<>();
        int i = 0;
        while(i < nums.length) {
            int a = m.getOrDefault(target-nums[i],-1);
        if (a>=0) {
            return new int[]{i,a};
        } else {
            m.put(nums[i],i);
        }
            i++;
        }
        return null;
    }
    // 
    /* */
    public static void main(String[] args) {
    }
}


