package cn.nju.dao;

import cn.nju.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/9/12.
 */
@Mapper
public interface QuestionDao {
    String TABLE_NAME="question";
    String INSERT_FIELDS="title,content,user_id,create_date,comment_count";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{title},#{content},#{userId}," +
            "#{createDate},#{commentCount})"})
    int addQuestion(Question question);

   List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select ",INSERT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
    Question selectQuestionById(int id);

}
