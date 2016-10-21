package cn.nju.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 黄锐鸿 on 2016/9/12.
 */
public class ViewObject {
    private Map<String,Object> objs=new HashMap<>();

    public void set(String key,Object value){
        objs.put(key,value);
    }

    public Object get(String key){
        return  objs.get(key);
    }

}
