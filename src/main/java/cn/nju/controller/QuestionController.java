package cn.nju.controller;


import cn.nju.model.*;
import cn.nju.service.*;
import cn.nju.util.DevelopmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/17.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

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

        List<Comment> commentList=commentService.getCommentByEntity(qid, EntityType.Entity_Question);
        List<ViewObject> comments=new ArrayList<>();
        for(Comment comment:commentList){
            ViewObject vo=new ViewObject();
            vo.set("comment",comment);
            vo.set("user",userService.getUser(comment.getUserId()));
            if(hostHolder.getUser()==null){
                vo.set("likeStatus",0);
            }else {
                User user=hostHolder.getUser();
                vo.set("likeStatus",likeService.getLikeStatus(user.getId(),EntityType.Entity_Comment,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.Entity_Comment,comment.getId()));
            comments.add(vo);
        }
        model.addAttribute("comments",comments);
        model.addAttribute("question",question);

        model.addAttribute("user",userService.getUser(question.getUserId()));

        List<ViewObject> followUsers=new ArrayList<>();
        List<Integer> followersId=followService.getFollowers(EntityType.Entity_Question,qid,15);//取得关注该问题的最新十五个人
        for(Integer userId:followersId){
            ViewObject vo=new ViewObject();
            User user=userService.getUser(userId);
            if(user==null){
                continue;
            }
            vo.set("headUrl",user.getHeadUrl());
            vo.set("name",user.getName());
            vo.set("id",userId);
            followUsers.add(vo);
        }
        model.addAttribute("followUsers",followUsers);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.Entity_Question, qid));
        } else {
            model.addAttribute("followed", false);
        }

        return "detail";
    }
}
