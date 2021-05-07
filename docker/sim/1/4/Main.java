import java.util.HashMap;
import java.util.Arrays;

class Main {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> m = new HashMap<>();
for(int a=0; a<nums.length; a++) {
    int b = m.getOrDefault(target-nums[a],-1);
    if (b>=0) {
        return new int[]{a,b};
    } else {
        m.put(nums[a],a);
    }
}
        return null;
    }
    // 
    /* */
    public static void main(String[] args) {
    }
}


