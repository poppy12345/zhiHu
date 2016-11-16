package cn.nju.service;

import cn.nju.dao.CommentDao;
import cn.nju.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/21.
 */
@Service
public class CommentService {
    private static final Logger logger= LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;


    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment)>0?comment.getId():0;
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public int getUserCommentCount(int userId){
        return commentDao.getUserCommentCount(userId);
    }

    public boolean deleteComment(int commentId){
        return commentDao.updateStatus(1,commentId)>0;
    }

    public Comment getCommentById(int commentId){
        return commentDao.selectCommentById(commentId);
    }

}
