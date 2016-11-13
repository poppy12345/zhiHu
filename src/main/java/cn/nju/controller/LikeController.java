package cn.nju.controller;

import cn.nju.async.EventConsumer;
import cn.nju.async.EventModel;
import cn.nju.async.EventProducer;
import cn.nju.async.EventType;
import cn.nju.model.Comment;
import cn.nju.model.EntityType;
import cn.nju.model.HostHolder;
import cn.nju.service.CommentService;
import cn.nju.service.LikeService;
import cn.nju.util.DevelopmentUtil;
import cn.nju.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 黄锐鸿 on 2016/11/9.
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/like",method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId")int commentId){
        if(hostHolder.getUser()==null){
            return DevelopmentUtil.getJsonString(999); //跳转到登录
        }

        long likeCount=likeService.like(hostHolder.getUser().getId(),EntityType.Entity_Comment,commentId);
        EventModel eventModel=new EventModel();
        Comment comment=commentService.getCommentById(commentId);
        if(hostHolder.getUser().getId()!=comment.getUserId()) { //自己给自己的回答点赞不发站内信通知
            eventModel.setType(EventType.LIKE).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.Entity_Comment).setEntityId(commentId)
                    .setEntityOwnerId(comment.getUserId()).setExt("questionId",
                    String.valueOf(comment.getEntityId()));
            eventProducer.fireEvent(eventModel);
        }
        return DevelopmentUtil.getJsonString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike",method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId")int commentId){
        if(hostHolder.getUser()==null){
            return DevelopmentUtil.getJsonString(999); //跳转到登录
        }
        long likeCount=likeService.dislike(hostHolder.getUser().getId(),EntityType.Entity_Comment,commentId);
        return DevelopmentUtil.getJsonString(0,String.valueOf(likeCount));
    }
}
