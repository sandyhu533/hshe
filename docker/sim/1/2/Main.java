import java.util.HashMap;
import java.util.Arrays;

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
    }
}


