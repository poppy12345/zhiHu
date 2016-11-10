package cn.nju.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by 黄锐鸿 on 2016/11/9.
 */
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger= LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;
    @Override
    public void afterPropertiesSet() throws Exception {
        pool=new JedisPool("redis://localhost:6379/1");
    }

    public long sadd(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return false;
    }
}
