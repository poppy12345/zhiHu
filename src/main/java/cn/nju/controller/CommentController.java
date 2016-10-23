package cn.nju.controller;

import cn.nju.model.Comment;
import cn.nju.model.EntityType;
import cn.nju.model.HostHolder;
import cn.nju.service.CommentService;
import cn.nju.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by 黄锐鸿 on 2016/10/21.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId")int questionId, @RequestParam("content")String content){
        try {
            if(hostHolder.getUser()==null){
                return "redirect:/relogin";
            }
            Comment comment=new Comment();
            comment.setContent(content);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setCreateDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.Entity_Question);
            comment.setStatus(0);

            commentService.addComment(comment);
            int count= commentService.getCommentCount(questionId,EntityType.Entity_Question);
            questionService.updateCommentCount(questionId, count);

        }catch (Exception e){
            logger.error("发布问题失败！");
        }
        return "redirect:/question/"+questionId;
    }
}
