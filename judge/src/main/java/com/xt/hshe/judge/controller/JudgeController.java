package com.xt.hshe.judge.controller;

import javax.servlet.http.HttpServletRequest;

import com.xt.hshe.judge.pojo.HttpMsg;
import com.xt.hshe.judge.service.JudgeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class JudgeController {
    @Autowired
    JudgeService judgeService;
    
    @GetMapping ("/judge/{id}")
    public ResponseEntity<?> judgeWithId(@PathVariable Long id) throws Exception {
        System.out.println("recived judge req with id:" + id);
        int ret = judgeService.judge(id);
        return ResponseEntity.ok(ret);
    }
}
