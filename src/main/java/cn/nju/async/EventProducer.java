package cn.nju.async;

import cn.nju.util.JedisAdapter;
import cn.nju.util.RedisKeyUtil;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 黄锐鸿 on 2016/11/11.
 */
@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try {
            String key= RedisKeyUtil.getEventQueueKey();
            String event= JSONObject.toJSONString(eventModel) ;//序列化EventModel
            jedisAdapter.lpush(key,event);
            return true;
        }catch (Exception e){
            logger.error("添加事件失败！"+e.getMessage());
            return false;
        }
    }

}
