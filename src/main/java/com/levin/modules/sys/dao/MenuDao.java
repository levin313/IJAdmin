
package com.levin.modules.sys.dao;


import com.levin.common.persistence.BaseDao;
import com.levin.modules.sys.entity.Dict;
import com.levin.modules.sys.entity.Menu;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单DAO接口
 */
@Repository
public class MenuDao extends BaseDao<Menu, String> {

    public List<Menu> getPermissions(String uid) {
        Map<String, String> para = new HashMap<>();
        return find("", para);
    }
}
