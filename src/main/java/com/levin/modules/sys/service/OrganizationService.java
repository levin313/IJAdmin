package com.levin.modules.sys.service;

import com.levin.common.base.BaseService;
import com.levin.modules.sys.dao.OrganizationDao;
import com.levin.modules.sys.entity.Organization;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TODO 完善注释
 *
 * @auther: lwt
 * @Date: 2018-10-04 22:26
 */
@Service
public class OrganizationService extends BaseService<Organization, String> {
    @Resource
    public void setBaseDao(OrganizationDao organizationDao) {
        super.setBaseDao(organizationDao);
    }
}
