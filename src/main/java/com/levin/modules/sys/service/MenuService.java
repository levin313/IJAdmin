package com.levin.modules.sys.service;

import com.levin.common.base.BaseService;
import com.levin.common.persistence.BaseDao;
import com.levin.modules.sys.dao.MenuDao;
import com.levin.modules.sys.entity.Menu;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO 完善注释
 *
 * @auther: lwt
 * @Date: 2018-10-03 00:22
 */
@Service
public class MenuService extends BaseService<Menu, String> {

    @Resource
    public void setBaseDao(MenuDao menuDao) {
        super.setBaseDao(menuDao);
    }

    @Resource
    private MenuDao menuDao;

    public List<Menu> getPermissions(String uid) {
        return menuDao.getPermissions(uid);
    }
}
