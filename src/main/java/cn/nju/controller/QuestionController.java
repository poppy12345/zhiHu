package cn.nju.controller;

import cn.nju.dao.QuestionDao;
import cn.nju.model.HostHolder;
import cn.nju.model.Question;
import cn.nju.service.QuestionService;
import cn.nju.service.UserService;
import cn.nju.util.DevelopmentUtil;
import org.apache.ibatis.javassist.compiler.MemberResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by 黄锐鸿 on 2016/10/17.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title")String title,@RequestParam("content")String content){
        try{
            Question question=new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreateDate(new Date());
            question.setCommentCount(0);

            if(hostHolder.getUser()==null){
                return DevelopmentUtil.getJsonString(999);
            }
            question.setUserId(hostHolder.getUser().getId());

            if(questionService.addQuestion(question)>0){
                return DevelopmentUtil.getJsonString(0);
            }


        }catch (Exception e){
            logger.error("发表问题失败！"+e.getMessage());

        }
        return DevelopmentUtil.getJsonString(1,"发布失败！");
    }

    @RequestMapping(value = "/question/{qid}",method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable(value = "qid")int qid){
        Question question=questionService.getQuestionDetail(qid);
        model.addAttribute("question",question);
        model.addAttribute("user",userService.getUser(question.getUserId()));
        return "detail";
    }
}
