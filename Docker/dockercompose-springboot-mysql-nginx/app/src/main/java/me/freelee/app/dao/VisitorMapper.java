package me.freelee.app.dao;

import me.freelee.app.model.Visitor;

public interface VisitorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Visitor record);

    int insertSelective(Visitor record);

    Visitor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Visitor record);

    int updateByPrimaryKey(Visitor record);

    Visitor findByIp(String ip);
}