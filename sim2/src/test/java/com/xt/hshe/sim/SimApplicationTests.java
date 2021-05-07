package com.xt.hshe.sim;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RunWith(SpringRunner.class)
public class SimApplicationTests {

	@Test
	public void calSimTest() {
		try {
			System.out.printf("*****cal sim res*****: %d\n", calSim("java", 1l, 7l, 8l));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static final Logger LOGGER = LogManager.getLogger(SimApplicationTests.class);

    // @Value("${sim.shell.path}")
    private String shFile = "/Users/sandyhu/Developer/hshe3/docker/sim/cyvis_analyse.sh";

    // @Value("${judge.sys.cyvis.jar-path}")
    private String cyvisJarPath = "/Users/sandyhu/Developer/hshe3/docker/sim/cyvis-0.9/cyvis-0.9.jar";

    // @Value("${judge.sys.subs-path}")
    private String subsPath = "/Users/sandyhu/Developer/hshe3/docker/sim";

        /**
     * 调用计算相似程度,脚本输出的值即相似度,无输出代表不相似.
     * @param lang - 语言类型(扩展名)
     * @param pid - 题目id
     * @param sid1 - 第一个提交id
     * @param sid2 - 第二个提交id
     * @return int - 两个提交的相似度百分数(省略百分号)
     */
    public int calSim(String lang, Long pid, Long sid1, Long sid2) throws IOException {
        // 属性度量
        List<Integer> ppt_list_1 = callShPptResult(lang, pid, sid1);
        List<Integer> ppt_list_2 = callShPptResult(lang, pid, sid2);
        int ppt_sim = normalize(calDis(ppt_list_1, ppt_list_2));
		System.out.println("ppt_sim:" + ppt_sim);
        // 结构度量
		List<Long> fgp_1 = calFingerPrint(getSrcContent(lang, pid, sid1));
		List<Long> fgp_2 = calFingerPrint(getSrcContent(lang, pid, sid2));
		int lsc = LSC(fgp_1, fgp_2);
		int lsc_sim =  (int)(lsc * 10000 / Math.min(fgp_1.size(), fgp_2.size()));
		System.out.println("lsc_sim:" + lsc_sim);
        return ppt_sim > lsc_sim ? ppt_sim : lsc_sim;
    }
	
    private List<Integer> callShPptResult(String lang, Long pid, Long sid) throws IOException {
        String command = String.format("%s %s %s %s %s", shFile, cyvisJarPath, subsPath, pid.toString(), sid.toString());
        System.out.println("Property Command: "+command);
        Process process = Runtime.getRuntime().exec(command);
        List<Integer> ppt_list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String result = br.readLine();
            while(result != null && !"".equals(result)) {
                ppt_list.add(Integer.parseInt(result));
                result = br.readLine();
            }
            System.out.println("Call result: "+ppt_list.toString());
        }
        return ppt_list;
    }

	private String getSrcContent(String lang, Long pid, Long sid) throws IOException {
		String filePath = subsPath + "/" + pid + "/" + sid + "/Main.java";
		StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        return contentBuilder.toString();
	}

    private int calDis(List<Integer> list1, List<Integer> list2) {
        if(list1.size() != list2.size()) return Integer.MAX_VALUE;
        int dis = 0;
        for (int i = 0; i < list1.size(); i++) {
            dis += Math.pow(list2.get(i) - list1.get(i), 2);
        }
        dis = (int)Math.pow(dis, 0.5);
		System.out.println("dis: "+dis);
        return dis;
    }

    private int normalize(int val) {
		if (val == 0) return 10000;
        return (int)((10000 / val));
    }

	private int fgpK = 4;

	private int winnowingW = 4;

	private int primeNum = 37;

	private int LSC(List<Long> arr1, List<Long> arr2) {
		if (arr1 == null || arr2 == null || arr1.size() == 0 || arr2.size() == 0) return 0;
		int len1 = arr1.size(), len2 = arr2.size();
		int[][] dp = new int[len1 + 1][len2 + 1];
		for (int i = 0; i <= len1; i++) for (int j = 0; j <= len2; j++) dp[i][j] = 0;
		for (int i = 1; i <= len1; i++) {
			for(int j = 1; j <= len2; j++) {
				int val = dp[i - 1][j] > dp[i][j - 1] ? dp[i - 1][j] : dp[i][j - 1];
				if (arr1.get(i - 1).equals(arr2.get(j - 1))) {
					if (dp[i - 1][j - 1] + 1 > val) {
						val = dp[i - 1][j - 1] + 1;
					}
				}
				dp[i][j] = val;
			}
		}
		System.out.println("LSC:"+dp[len1][len2]);
		return dp[len1][len2];
	}

	private List<Long> calFingerPrint(String srcContent) {
		if (srcContent.length() < fgpK * winnowingW) return null;

		ArrayDeque<Long> hashVal = new ArrayDeque<>();
		long val = 0;
		// 第一个hash
		for (int i = 0; i < fgpK; i++) {
			val *= primeNum;
			val += srcContent.charAt(i);
		}
		hashVal.add(val);
		// 后面的hash
		for (int i = fgpK; i < srcContent.length(); i++) {
			val -= (srcContent.charAt(i - fgpK)) * (long)Math.pow(primeNum, fgpK - 1);
			val *= primeNum;
			val += srcContent.charAt(i);
			hashVal.add(val);
		}

		if (hashVal.size() < winnowingW) return null;

		// winnowing，在窗口winnowingW中取最小
		PriorityQueue<Long> que = new PriorityQueue<>();
		List<Long> fingerPrint = new ArrayList<>();
		long preVal = 0;
		for (int i = 0; i < winnowingW - 1; i++) {
			que.add(hashVal.pollFirst());
		}
		while(hashVal.size() > 0) {
			que.add(hashVal.pollFirst());
			long cur = que.poll();
			if (cur != preVal) {
				fingerPrint.add(cur);
				preVal = cur;
			}
		}

		// System.out.println(fingerPrint);
		return fingerPrint;
	}

}
