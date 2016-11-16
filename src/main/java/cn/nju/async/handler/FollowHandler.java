package cn.nju.async.handler;

import cn.nju.async.EventHandler;
import cn.nju.async.EventModel;
import cn.nju.async.EventType;
import cn.nju.model.EntityType;
import cn.nju.model.Message;
import cn.nju.model.User;
import cn.nju.service.MessageService;
import cn.nju.service.UserService;
import cn.nju.util.DevelopmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/11/14.
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message=new Message();
        message.setFromId(DevelopmentUtil.SYSTEM_USER);
        message.setToId(eventModel.getEntityOwnerId());
        message.setConversationId(DevelopmentUtil.SYSTEM_USER,eventModel.getEntityOwnerId());
        message.setCreateDate(new Date());
        User user=userService.getUser(eventModel.getActorId());
        if(eventModel.getEntityType()== EntityType.Entity_User){
            message.setContent("用户"+user.getName()+"关注了你，http://127.0.0.1:8080/user/"
                    +eventModel.getActorId());
        }else if(eventModel.getEntityType()==EntityType.Entity_Question){
            message.setContent("用户"+user.getName()+"关注了你的问题，http://127.0.0.1:8080/question/"
                    +eventModel.getEntityId());
        }


        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
