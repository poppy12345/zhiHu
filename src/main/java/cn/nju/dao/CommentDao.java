package cn.nju.dao;

import cn.nju.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/10/21.
 */
@Mapper
public interface CommentDao {
    String TABLE_NAME="comment";
    String INSERT_FIELDS="content,user_id,create_date,entity_id,entity_type,status";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,") values(#{content},#{userId},#{createDate}," +
            "#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    List<Comment> selectCommentByEntity(@Param("entityId")int entityId,@Param("entityType")int entityType);

    @Select({"select count(id) from ",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId")int entityId,@Param("entityType")int entityType);

    @Update({"update ",TABLE_NAME," set status=#{status} where id=#{id}"})
    int updateStatus(@Param("status")int status,@Param("id")int id);

}
