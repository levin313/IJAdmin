
package com.levin.modules.sys.service;

import com.levin.common.base.BaseService;
import com.levin.modules.sys.dao.AreaDao;
import com.levin.modules.sys.entity.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 区域Service
 */
@Service
@Transactional
public class AreaService extends BaseService<Area, String> {

    @Autowired
    public void setBaseDao(AreaDao areaDao) {
        super.setBaseDao(areaDao);
    }
}
