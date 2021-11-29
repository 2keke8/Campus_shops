package com.mapper;

import com.entity.Collect;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface CollectMapper {
    /**添加收藏*/
    Integer insertCollect(Collect collect);
    /**分页查看所有收藏内容*/
    List<Collect> queryAllCollect(@Param("page") Integer page, @Param("count") Integer count, @Param("couserid") String couserid);
    /**修改收藏状态*/
    Integer updateCollect(Collect collect);
    /**查询商品是否被用户收藏*/
    Collect queryCollectStatus(Collect collect);
    /**查询我的收藏的总数*/
    Integer queryCollectCount(String couserid);
}
