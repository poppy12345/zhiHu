package cn.nju.controller;

import cn.nju.model.HostHolder;
import cn.nju.model.Message;
import cn.nju.model.User;
import cn.nju.model.ViewObject;
import cn.nju.service.MessageService;
import cn.nju.service.UserService;
import cn.nju.util.DevelopmentUtil;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/22.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST})
    @ResponseBody //POST请求必须使用这个注解，否则会默认为GET请求
    public String addMessage(@RequestParam("toName")String toName,@RequestParam("content")String content){
        try{
            if(hostHolder.getUser()==null){
                return DevelopmentUtil.getJsonString(999);
            }
            User user=userService.selectUserByName(toName);
            if(user==null){
                return DevelopmentUtil.getJsonString(1,"用户不存在！");
            }
            Message message=new Message();
            message.setContent(content);
            message.setCreateDate(new Date());
            message.setConversationId(hostHolder.getUser().getId(),user.getId());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setHasRead(0);

            messageService.addMessage(message);

            return DevelopmentUtil.getJsonString(0);
        }catch (Exception e){
            logger.error("发送站内信失败！");
            return DevelopmentUtil.getJsonString(1,"发送站内信失败！");
        }
    }

    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String getConversationList(Model model){
        try {
            int localUserId=hostHolder.getUser().getId();
            List<Message> messageList=messageService.getConversationList(localUserId,0,10);
            List<ViewObject> conversations=new ArrayList<>();
            for (Message message:messageList){
                ViewObject vo=new ViewObject();
                int targetUserId=message.getFromId()==localUserId?message.getToId():message.getFromId();
                vo.set("user",userService.getUser(targetUserId));
                vo.set("conversation",message);
                vo.set("unread",messageService.getUnreadCount(localUserId,message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);

        }catch (Exception e){
             logger.error("打开私信列表失败！"+e.getMessage());
        }

        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,@RequestParam("conversationId")String conversationId){
        try {
            List<Message> messageList=messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages=new ArrayList<>();
            for(Message message:messageList){
                ViewObject vo=new ViewObject();
                vo.set("message",message);
                messageService.updateMessageReadStatus(hostHolder.getUser().getId(),message.getConversationId(),1);
                vo.set("user",userService.getUser(message.getFromId()));
                messages.add(vo);
            }

            model.addAttribute("messages",messages);

        }catch (Exception e){

            logger.error("查看私信详情失败！"+e.getMessage());

        }

        return "letterDetail";
    }
}
