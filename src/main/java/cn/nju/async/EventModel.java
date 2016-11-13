package cn.nju.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 黄锐鸿 on 2016/11/11.
 */
public class EventModel { //JSON序列化必须提供所有属性的setter和getter方法，否则会出现属性丢失
    private EventType type; //事件类型
    private int actorId;   //事件触发者
    private int entityType; //实体类型
    private int entityId;   //实体标识
    private int entityOwnerId; //实体所有者



    private Map<String,String> exts=new HashMap<>();  //额外字段
    //实际上上边所有字段都可以存储在map中，但是比较常用，单独列出，方便使用
    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {  //所有setter的返回类型设为EventModel,方便链式调用
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key,String value) {
        exts.put(key,value);
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }

}
