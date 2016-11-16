package cn.nju.service;

import cn.nju.util.JedisAdapter;
import cn.nju.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by 黄锐鸿 on 2016/11/14.
 */
@Service
public class FollowService { //关注服务

    @Autowired
    JedisAdapter jedisAdapter;

    //添加关注
    public boolean follow(int userId,int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();
        Jedis jedis=jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret=jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0; //返回值大于0才成功

    }
    //取消关注
    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);

        Jedis jedis=jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret=jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }

    private List<Integer> transform(Set<String> sets){
        List<Integer> results=new ArrayList<>();
        for(String s:sets){
            results.add(Integer.parseInt(s));
        }

        return results;
    }
    //获取粉丝列表
    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);

        return transform(jedisAdapter.zrevrange(followerKey,0,count));
    }
    //获取粉丝列表，分页
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);

        return transform(jedisAdapter.zrevrange(followerKey,offset,count));
    }
    //获取关注列表
    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);

        return transform(jedisAdapter.zrevrange(followeeKey,0,count));
    }
    //获取关注列表，分页
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){
        String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);

        return transform(jedisAdapter.zrevrange(followeeKey,offset,count));
    }
    //获取粉丝数
    public long getFollowerCount(int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    //获取关注数
    public long getFolloweeCount(int userId,int entityType){
        String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    //判断某个人是否是某个实体的粉丝
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);

        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }


}
