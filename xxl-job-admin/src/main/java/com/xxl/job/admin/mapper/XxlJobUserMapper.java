package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.model.XxlJobUserGroups;
import com.xxl.tool.response.Response;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
@Mapper
public interface XxlJobUserMapper {

    public List<XxlJobUser> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("username") String username,
                                     @Param("role") int role);

    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("username") String username,
                             @Param("role") int role);

    public XxlJobUser loadByUserName(@Param("username") String username);

    public XxlJobUser loadById(@Param("id") int id);

    public int save(XxlJobUser xxlJobUser);

    public int update(XxlJobUser xxlJobUser);

    public int delete(@Param("id") int id);

    public int updateToken(@Param("id") int id, @Param("token") String token);

    void createXxlJobUserPermission();

    List<XxlJobGroup> queryUserGroupList(@Param("userName") String userName, @Param("roleName") String roleName);

    void deleteUserPermission(@Param("userName") String userName);

    void saveUserPermission(@Param("userName") String userName, @Param("groupId") Integer groupId);

}
