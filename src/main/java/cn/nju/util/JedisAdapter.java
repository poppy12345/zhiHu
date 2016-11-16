package cn.nju.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    public long lpush(String key,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis(){
        try{
            return pool.getResource();
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }

        return null;
    }

    public Transaction multi(Jedis jedis){
        if(jedis!=null){
            return jedis.multi();
        }
        return null;
    }

    public List<Object>  exec(Transaction tx,Jedis jedis){

        try{
            return tx.exec();
        }catch (Exception e){
            logger.error("事务执行异常！"+e.getMessage());
        }finally {
            try {
                tx.close();
            }catch (IOException ioe){
                logger.error("事务关闭失败！"+ioe.getMessage());
            }
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;

    }

    public long zcard(String key){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zrevrange(key,start,end);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }

    public Double zscore(String key,String value){
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zscore(key,value);
        }catch (Exception e){
            logger.error("获取redis连接失败"+e.getMessage());
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }


}
