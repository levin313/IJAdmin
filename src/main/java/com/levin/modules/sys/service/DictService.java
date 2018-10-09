
package com.levin.modules.sys.service;

import com.levin.common.base.BaseService;
import com.levin.modules.sys.dao.DictDao;
import com.levin.modules.sys.entity.Dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典Service
 */
@Service
@Transactional
public class DictService extends BaseService<Dict,String> {

    @Autowired
    public void setBaseDao(DictDao dictDao) {
        super.setBaseDao(dictDao);
    }
}
