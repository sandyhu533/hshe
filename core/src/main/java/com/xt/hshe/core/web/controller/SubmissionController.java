package com.xt.hshe.core.web.controller;

import com.xt.hshe.core.pojo.HttpMsg;
import com.xt.hshe.core.pojo.entity.Problem;
import com.xt.hshe.core.pojo.entity.Submission;
import com.xt.hshe.core.util.Consts;

import org.apache.tomcat.jni.Time;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SubmissionController extends BaseController{

    private static final Logger LOGGER = LogManager.getLogger(SubmissionController.class);

    @GetMapping("/s")
    public HttpMsg submissions(){
        return new HttpMsg<>(Consts.ServerCode.SUCCESS,null, submissionService.findAllSubmissions());
    }


    @GetMapping("/s/{id}")
    public HttpMsg submission(@PathVariable Long id){
        Submission submission = submissionService.find(id);
        if (submission==null){
            return new HttpMsg(Consts.ServerCode.FAILURE,"不存在的~");
        }
        return new HttpMsg<>(Consts.ServerCode.SUCCESS,null, submission);
    }

    @PostMapping("/s")
    public HttpMsg submit(HttpServletRequest request, HttpServletResponse response, @RequestBody Map map){
        String uid = (String) request.getAttribute("user_id");
        Assert.hasText(uid, "请求失败，请重新登录!");
        Integer l = (Integer) map.get("problemId");
        String pid = l.toString();
        Assert.hasText(pid, "请求失败，请重试!");
        String src = (String) map.get("src");
        Assert.hasText(src, "代码不能为空!");
        String lang = (String) map.get("lang");
        Assert.hasText(lang, "请选择编程语言!");
        //作业和题目 目前不是多对多的关系
        if (!submissionService.isSubmittable(Long.parseLong(pid))) {
            return new HttpMsg(Consts.ServerCode.FAILURE, "作业已经结束~");
        }

        Long sid = submissionService.submit(uid, pid, lang, src);//储存Submission到数据库
        if (sid==null) {
            return new HttpMsg(Consts.ServerCode.FAILURE, "提交失败");
        } else {
            // 异步
            // sender.sendToJudge(sid.toString());//发消息给q执行编译判题

            // 同步
            RestTemplate restTemplate = new RestTemplate();
            String fooResourceUrl = "http://judge:9001/judge";
            ResponseEntity<Integer> ret_entity = restTemplate.getForEntity(fooResourceUrl + "/" + sid.toString(), Integer.class);

            int judge_res = ret_entity.getBody();
            int ret = 0;
            
            String retmsg = "提交成功~";
            switch (judge_res) {
                case 0: {ret = Consts.ServerCode.SUCCESS; retmsg = "答案正确"; break;}
                case 1: {ret = Consts.ServerCode.FAILURE; retmsg = "答案错误"; break;}
                case 2: {ret = Consts.ServerCode.FAILURE; retmsg = "时间超限"; break;}
                case 3: {ret = Consts.ServerCode.FAILURE; retmsg = "输出超限"; break;}
                case 4: {ret = Consts.ServerCode.FAILURE; retmsg = "内存超限"; break;}
                case 5: {ret = Consts.ServerCode.FAILURE; retmsg = "运行时错误"; break;}
                case 6: {ret = Consts.ServerCode.FAILURE; retmsg = "格式错误"; break;}
                case 7: {ret = Consts.ServerCode.FAILURE; retmsg = "编译错误"; break;}
                case 8: {ret = Consts.ServerCode.FAILURE; retmsg = "系统错误"; break;}
                default: break;
            }

            return new HttpMsg<>(ret, retmsg, sid);
        }

    }
}
