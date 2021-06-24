package com.oyyk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyyk.wiki.domain.User;
import com.oyyk.wiki.domain.UserExample;
import com.oyyk.wiki.mapper.UserMapper;
import com.oyyk.wiki.req.UserQueryReq;
import com.oyyk.wiki.req.UserSaveReq;
import com.oyyk.wiki.resp.UserQueryResp;
import com.oyyk.wiki.resp.PageResp;
import com.oyyk.wiki.util.CopyUtil;
import com.oyyk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        //动态sql，name不为空才进行模糊匹配
        //动态sql，name不为空才进行模糊匹配
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }

        PageHelper.startPage(req.getPage(),req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<UserQueryResp> respList = new ArrayList<>();
//        for(User user: userList){
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
        //对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }

        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public PageResp<UserQueryResp> home(UserQueryReq req) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        //动态sql，name不为空才进行模糊匹配
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<UserQueryResp> respList = new ArrayList<>();
//        for(User user: userList){
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
        //对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }

        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        } else {
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

}
