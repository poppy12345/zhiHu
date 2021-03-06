package cn.nju.dao;

import cn.nju.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by 黄锐鸿 on 2016/9/18.
 */
@Mapper
public interface LoginTicketDao {
    String TABLE_NAME="login_ticket";
    String INSERT_FIELDS="user_id,ticket,expired,status";
    String SELECT_FIELDS="id,"+INSERT_FIELDS;

    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{userId},#{ticket}," +
            "#{expired},#{status})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME," where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ",TABLE_NAME," set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("status") int status, @Param("ticket") String ticket);//多个参数使用@Param注解
}
