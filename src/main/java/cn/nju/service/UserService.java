package cn.nju.service;


import cn.nju.dao.LoginTicketDao;
import cn.nju.dao.UserDao;
import cn.nju.model.LoginTicket;
import cn.nju.model.User;
import cn.nju.util.DevelopmentUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by 黄锐鸿 on 2016/9/10.
 */
@Service
public class UserService {
    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User selectUserByName(String name){
        return userDao.selectByName(name);
    }

    public Map<String,Object> register(String username, String password){
        Map<String,Object> map=new HashMap<String,Object>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空!");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空!");
            return map;
        }

        User user=userDao.selectByName(username);

        if(user!=null){
            map.put("msg","用户名已经被注册！");
            return map;
        }

        user=new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head=String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(DevelopmentUtil.MD5(password+user.getSalt()));

        userDao.addUser(user);

        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,Object> login(String username,String password){
        Map<String,Object> map=new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空!");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空!");
            return map;
        }

        User user=userDao.selectByName(username);
        if(user==null){
            map.put("msg","用户不存在！");
            return map;
        }

        if(!DevelopmentUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误！");
            return map;
        }

        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket=new LoginTicket();
        ticket.setUserId(userId);
        Date date=new Date();
        long liveTime=1000L*3600*24*30;
//        System.out.print(liveTime);
        date.setTime(date.getTime()+liveTime);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-",""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(1,ticket);
    }
}
