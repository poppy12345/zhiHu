package cn.nju.controller;


import cn.nju.async.EventModel;
import cn.nju.async.EventProducer;
import cn.nju.async.EventType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 黄锐鸿 on 2016/11/14.
 */
@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

   @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/followUser",method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId")int userId){ //关注某人
        if(hostHolder.getUser()==null){ //用户未登录
            return DevelopmentUtil.getJsonString(999);
        }

        User user=userService.getUser(userId);//判断用户是否为空，防止用户直接输入错误的URL来访问
        if(user!=null){
            boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.Entity_User,userId);
            EventModel eventModel=new EventModel();
            eventModel.setType(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.Entity_User).setEntityId(userId).setEntityOwnerId(userId);

            eventProducer.fireEvent(eventModel);
            return DevelopmentUtil.getJsonString(ret?0:1,String.valueOf(followService.getFolloweeCount(
                    hostHolder.getUser().getId(),EntityType.Entity_User)));
        }else{
            return DevelopmentUtil.getJsonString(1,"用户不存在");
        }
    }

    @RequestMapping(path = "/unfollowUser",method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId")int userId){ //对某人取消关注
        if(hostHolder.getUser()==null){ //用户未登录
            return DevelopmentUtil.getJsonString(999);
        }

        User user=userService.getUser(userId);//判断用户是否为空，防止用户直接输入错误的URL来访问
        if(user!=null){
            boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.Entity_User,userId);

            return DevelopmentUtil.getJsonString(ret?0:1,String.valueOf(followService.getFolloweeCount(
                    hostHolder.getUser().getId(),EntityType.Entity_User)));
        }else{
            return DevelopmentUtil.getJsonString(1,"用户不存在");
        }
    }

    @RequestMapping(path = "/followQuestion",method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId")int questionId){ //关注某个问题
        if(hostHolder.getUser()==null){ //用户未登录
            return DevelopmentUtil.getJsonString(999);
        }

        Question question=questionService.getQuestionDetail(questionId);
        if(question!=null){
            boolean ret=followService.follow(hostHolder.getUser().getId(),EntityType.Entity_Question,questionId);
            EventModel eventModel=new EventModel();
            eventModel.setType(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.Entity_Question).setEntityId(questionId).
                    setEntityOwnerId(question.getUserId());

            eventProducer.fireEvent(eventModel);
            Map<String,Object> info=new HashMap<>();
            info.put("headUrl",hostHolder.getUser().getHeadUrl());
            info.put("name",hostHolder.getUser().getName());
            info.put("id",hostHolder.getUser().getId());
            info.put("count",followService.getFollowerCount(EntityType.Entity_Question,questionId));
            return DevelopmentUtil.getJsonString(ret?0:1,info);//注意Map类型数据的json化如何处理
        }else{
            return DevelopmentUtil.getJsonString(1,"问题不存在");
        }
    }

    @RequestMapping(path = "/unfollowQuestion",method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId")int questionId){ //对某个问题取消关注
        if(hostHolder.getUser()==null){ //用户未登录
            return DevelopmentUtil.getJsonString(999);
        }

        Question question=questionService.getQuestionDetail(questionId);
        if(question!=null){
            boolean ret=followService.unfollow(hostHolder.getUser().getId(),EntityType.Entity_Question,questionId);
            Map<String,Object> info=new HashMap<>();
            info.put("id",hostHolder.getUser().getId());
            info.put("count",followService.getFollowerCount(EntityType.Entity_Question,questionId));
            return DevelopmentUtil.getJsonString(ret?0:1,info);
        }else{
            return DevelopmentUtil.getJsonString(1,"问题不存在");
        }
    }

    @RequestMapping(path = "/user/{uid}/followers",method = {RequestMethod.GET})
    public String followers(Model model,@PathVariable("uid")int userId){//查看某个人的粉丝
        List<Integer> followersId=followService.getFollowers(EntityType.Entity_User,userId,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followers",getUserInfo(hostHolder.getUser().getId(),followersId));
        }else{
            model.addAttribute("followers",getUserInfo(0,followersId));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.Entity_User,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followers";
    }

    @RequestMapping(path = "/user/{uid}/followees",method = {RequestMethod.GET})
    public String followees(Model model,@PathVariable("uid")int userId){//查看某个人关注的人
        List<Integer> followeesId=followService.getFollowees(userId,EntityType.Entity_User,10);
        if(hostHolder.getUser()!=null){
            model.addAttribute("followees",getUserInfo(hostHolder.getUser().getId(),followeesId));
        }else{
            model.addAttribute("followees",getUserInfo(0,followeesId));
        }
        model.addAttribute("followeeCount",followService.getFolloweeCount(userId,EntityType.Entity_User));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followees";
    }



    private List<ViewObject> getUserInfo(int localUserId,List<Integer> userIds){ //取出所有粉丝或关注对象的信息
        List<ViewObject> userInfos=new ArrayList<ViewObject>();
        for(int uid:userIds){
            User user=userService.getUser(uid);
            if(user==null){
                continue;
            }
            ViewObject vo=new ViewObject();
            vo.set("user",user);
            vo.set("followerCount",followService.getFollowerCount(EntityType.Entity_User,uid));
            vo.set("followeeCount",followService.getFolloweeCount(uid,EntityType.Entity_User));
            vo.set("commentCount",commentService.getUserCommentCount(uid));
            if(localUserId!=0){//0表示未登录
                //用户是否关注localUser，即是否关注当前浏览者
                vo.set("followed",followService.isFollower(localUserId,EntityType.Entity_User,uid));
            }else{
                vo.set("followed",false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }




}
