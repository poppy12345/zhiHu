package cn.nju.async;

import java.util.List;

/**
 * Created by 黄锐鸿 on 2016/11/11.
 */
public interface EventHandler {  //EventHandler接口提供所有handler拥有的方法
    void doHandle(EventModel eventModel); //处理具体的事件
    List<EventType> getSupportEventTypes(); //关注的事件类型列表

}
