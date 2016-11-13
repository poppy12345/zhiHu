package cn.nju.async;

/**
 * Created by 黄锐鸿 on 2016/11/11.
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value){this.value=value;}  //构造函数，枚举类型为私有

    public int getValue(){
        return this.value;
    }
}
