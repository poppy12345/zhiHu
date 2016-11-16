package cn.nju.controller;

import cn.nju.model.*;
import cn.nju.service.CommentService;
import cn.nju.service.FollowService;
import cn.nju.service.QuestionService;
import cn.nju.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 黄锐鸿 on 2016/9/12.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getQuestions(int userId, int offset, int limit){
        List<Question> questionList=questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject> vos=new ArrayList<>();

        for(Question question:questionList){
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("followCount",followService.getFollowerCount(EntityType.Entity_Question,question.getId()));
            vo.set("user",userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }


    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @RequestParam(value="pop",defaultValue ="0")int pop){
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"},method ={RequestMethod.GET,RequestMethod.POST})
    public String user(Model model, @PathVariable("userId")int userId){
        User user = userService.getUser(userId);
        if(user!=null) {
            model.addAttribute("vos",getQuestions(userId, 0, 10));

            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(userId));
            vo.set("followerCount", followService.getFollowerCount(EntityType.Entity_User, userId));
            vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.Entity_User));
            if (hostHolder.getUser() != null) {
                vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.Entity_User, userId));
            } else {
                vo.set("followed", false);
            }
            model.addAttribute("profileUser", vo);
            return "profile";
        }else{
            return "notFound";
        }

    }

}
