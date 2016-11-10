package cn.nju.controller;

import cn.nju.model.EntityType;
import cn.nju.model.HostHolder;
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

    @RequestMapping(path = "/like",method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId")int commentId){
        if(hostHolder.getUser()==null){
            return DevelopmentUtil.getJsonString(999); //跳转到登录
        }

        long likeCount=likeService.like(hostHolder.getUser().getId(),EntityType.Entity_Comment,commentId);
        System.out.println(likeCount);
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
