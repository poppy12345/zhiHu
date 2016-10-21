package cn.nju.model;

import org.springframework.stereotype.Component;

/**
 * Created by 黄锐鸿 on 2016/10/15.
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users=new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
