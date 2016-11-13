package cn.nju.async;

import cn.nju.util.JedisAdapter;
import cn.nju.util.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 黄锐鸿 on 2016/11/11.
 */
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private Map<EventType,List<EventHandler>> config=new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);//获取所有handler
        if(beans!=null) {
            for (Map.Entry<String, EventHandler> bean : beans.entrySet()) {
                List<EventType> eventTypes = bean.getValue().getSupportEventTypes();

                for (EventType type : eventTypes){
                    if(!config.containsKey(type)){ //config不存在该事件类型，添加
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(bean.getValue());
                }
            }
        }

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key= RedisKeyUtil.getEventQueueKey();
                    List<String> events=jedisAdapter.brpop(0,key);//timeout设置为0，获取不到数据会一直阻塞，等待下去
                    for (String json:events){
                        if(key.equals(json)){
                            continue;
                        }
                        EventModel event= JSON.parseObject(json,EventModel.class);//反序列化得到EventModel

                        if (!config.containsKey(event.getType())){
                            logger.error("非法的事件类型!");
                            continue;
                        }
                        List<EventHandler> handlers=config.get(event.getType());
                        for(EventHandler handler:handlers){
                            handler.doHandle(event);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext; //获取上下文,初始化
    }
}
