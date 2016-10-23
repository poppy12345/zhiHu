package cn.nju.dao;


import cn.nju.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/22.
 */
@Mapper
public interface MessageDao {
    String TABLE_NAME="message";
    String INSERT_FIELDS="from_id,to_id,content,conversation_id,create_date,has_read";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{fromId},#{toId},#{content}," +
            "#{conversationId},#{createDate},#{hasRead})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where conversation_id=#{conversationId} order by " +
            "create_date limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId")String conversationId,@Param("offset")int offset,
                                        @Param("limit")int limit);

    //select *,count(id) as cnt from (SELECT * FROM zhihu.message order by create_date desc) tmp group by conversation_id order by create_date desc;
    @Select({"select ",INSERT_FIELDS,",count(id) as id from (select ",SELECT_FIELDS," from ",TABLE_NAME," where " +
            "from_id=#{userId} or to_id=#{userId} order by create_date desc) tmp group by conversation_id " +
            "order by create_date desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId")int userId,@Param("offset")int offset,
                                        @Param("limit")int limit);

    @Select({"select count(id) from ",TABLE_NAME," where conversation_id=#{conversationId} and to_id=#{userId} " +
            "and has_read=0 "})
    int getUnreadCount(@Param("userId")int userId,@Param("conversationId")String conversationId);

    @Update({"update ",TABLE_NAME," set has_read=#{hasRead} where conversation_id=#{conversationId} and " +
            "to_id=#{userId}"})
    int updateMessageReadStatus(@Param("userId")int userId,@Param("conversationId")String conversationId,
                                @Param("hasRead")int hasRead);

}
