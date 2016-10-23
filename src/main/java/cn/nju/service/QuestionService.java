package cn.nju.service;

import cn.nju.dao.QuestionDao;
import cn.nju.model.Question;
import org.apache.commons.digester.annotations.rules.AttributeCallParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.tags.HtmlEscapeTag;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/9/12.
 */
@Service
public class QuestionService {
    private static final Logger logger= LoggerFactory.getLogger(QuestionService.class);
    @Autowired
    QuestionDao questionDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit){
        return questionDao.selectLatestQuestions(userId,offset,limit);
    }

    public int addQuestion(Question question){
        //html过滤转义
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return  questionDao.addQuestion(question)>0?question.getId():0;
    }

    public Question getQuestionDetail(int questionId){
        return questionDao.selectQuestionById(questionId);
    }

    public int updateCommentCount(int questionId,int commentCount){
        return questionDao.updateCommentCount(questionId,commentCount);
    }

}
