package com.levin.modules.sys.service;

import com.levin.common.base.BaseService;
import com.levin.common.persistence.Filter;
import com.levin.common.utils.DateUtils;
import com.levin.common.utils.security.Digests;
import com.levin.common.utils.security.Encodes;
import com.levin.modules.sys.dao.UserDao;
import com.levin.modules.sys.entity.User;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * TODO 完善注释
 *
 * @auther: lwt
 * @Date: 2018-10-03 00:19
 */
@Service
public class UserService extends BaseService<User, String> {
    /**
     * 加密方法
     */
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    private static final int SALT_SIZE = 8;    //盐长度

    @Resource
    public void setBaseDao(UserDao userDao) {
        super.setBaseDao(userDao);
    }

    /**
     * 保存用户
     *
     * @param user
     */
    @Transactional(readOnly = false)
    public void save(User user) {
        entryptPassword(user);
        super.save(user);
    }

    /**
     * 修改密码
     *
     * @param user
     */
    @Transactional(readOnly = false)
    public void updatePwd(User user) {
        entryptPassword(user);
        save(user);
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @Transactional(readOnly = false)
    public void delete(String id) {
        if (!isSupervisor(id))
            super.delete(id);
    }

    /**
     * 按登录名查询用户
     *
     * @param loginName
     * @return 用户对象
     */
    public User getUser(String loginName) {
        return findUniqueBy("loginName", loginName);
    }

    /**
     * 判断是否超级管理员
     *
     * @param id
     * @return boolean
     */
    private boolean isSupervisor(String id) {
        return id .equals("1") ;
    }

    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     */
    private void entryptPassword(User user) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        user.setSalt(Encodes.encodeHex(salt));

        byte[] hashPassword = Digests.sha1(user.getPlainPass().getBytes(), salt, HASH_INTERATIONS);
        user.setPassword(Encodes.encodeHex(hashPassword));
    }

    /**
     * 验证原密码是否正确
     *
     * @param user
     * @param oldPassword
     * @return
     */
    public boolean checkPassword(User user, String oldPassword) {
        byte[] salt = Encodes.decodeHex(user.getSalt());
        byte[] hashPassword = Digests.sha1(oldPassword.getBytes(), salt, HASH_INTERATIONS);
        if (user.getPassword().equals(Encodes.encodeHex(hashPassword))) {
            return true;
        } else {
            return false;
        }
    }


    @Test
    public void test() {
        User user = new User();
        user.setPlainPass("123456");
        entryptPassword(user);
        System.out.println(user.getPassword());
        System.out.println(user.getSalt());
    }
}
