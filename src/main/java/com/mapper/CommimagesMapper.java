package com.mapper;

import com.entity.Commimages;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommimagesMapper {
    /**插入商品的其他图*/
    void InsertGoodImages(List<Commimages> list);
    /**查询某个商品得其他图*/
    List<String> LookGoodImages(String commid);
    /**删除某个商品得其他图*/
    void DelGoodImages(String commid);
}
