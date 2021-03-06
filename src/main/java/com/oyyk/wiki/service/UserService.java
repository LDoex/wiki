package com.oyyk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyyk.wiki.domain.User;
import com.oyyk.wiki.domain.UserExample;
import com.oyyk.wiki.exception.BusinessException;
import com.oyyk.wiki.exception.BusinessExceptionCode;
import com.oyyk.wiki.mapper.UserMapper;
import com.oyyk.wiki.req.UserLoginReq;
import com.oyyk.wiki.req.UserQueryReq;
import com.oyyk.wiki.req.UserResetPasswordReq;
import com.oyyk.wiki.req.UserSaveReq;
import com.oyyk.wiki.resp.PageResp;
import com.oyyk.wiki.resp.UserLoginResp;
import com.oyyk.wiki.resp.UserQueryResp;
import com.oyyk.wiki.util.CopyUtil;
import com.oyyk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
        if (ObjectUtils.isEmpty(req.getId())) {
            User userDB = selectByLoginName(req.getLoginName());
            if (ObjectUtils.isEmpty(userDB)) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新
            user.setLoginName(null);
            user.setPassword(null);
            //Selective只更新有值的字段，将LoginName置空后就不会被修改
            userMapper.updateByPrimaryKeySelective(user);
        }
    }
    /**
     * 重置密码
     * @param req
     */
    public void resetPassword(UserResetPasswordReq req){
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 登录
     * @param req
     */
    public UserLoginResp login(UserLoginReq req){
        User userDb = selectByLoginName(req.getLoginName());
        if(ObjectUtils.isEmpty(userDb)){
            LOG.info("用户名不存在,{}", req.getLoginName());
            //用户名不存在
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        } else{
            if(userDb.getPassword().equals(req.getPassword())){
                //登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(userDb, UserLoginResp.class);
                return userLoginResp;
            } else{
                LOG.info("密码错误,输入密码：{}，数据库密码：{}", req.getPassword(), userDb.getPassword());
                //密码不对
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if(CollectionUtils.isEmpty(userList)) {
            return null;
        } else{
            return userList.get(0);
        }
    }

}
