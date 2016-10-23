package cn.nju.service;

import cn.nju.dao.MessageDao;
import cn.nju.model.Message;
import org.apache.commons.digester.annotations.rules.AttributeCallParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/22.
 */
@Service
public class MessageService {
    private static final Logger logger= LoggerFactory.getLogger(MessageService.class);

    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDao.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDao.getConversationList(userId,offset,limit);
    }

    public int getUnreadCount(int userId,String conversationId){
        return messageDao.getUnreadCount(userId,conversationId);
    }

    public void updateMessageReadStatus(int userId,String conversationId, int hasRead){
        messageDao.updateMessageReadStatus(userId,conversationId,hasRead);
    }
}
