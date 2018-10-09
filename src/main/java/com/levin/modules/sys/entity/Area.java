
package com.levin.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.levin.common.base.BaseEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 区域Entity
 */
@Entity
@Table(name = "t_sys_area")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Area extends BaseEntity<Area> {

    private static final long serialVersionUID = 1L;
    private Area parent;    // 父级编号
    private String parentIds; // 所有父级编号
    private String code;    // 区域编码
    private String name;    // 区域名称
    private String type;    // 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）

    private List<Organization> organizationList = Lists.newArrayList(); // 部门列表
    private List<Area> childList = Lists.newArrayList();    // 拥有子区域列表

    public Area() {
        super();
    }

    public Area(String id) {
        this();
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @NotNull
    @JsonIgnore
    public Area getParent() {
        return parent;
    }

    public void setParent(Area parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    @OrderBy(value = "code")
    @Fetch(FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy(value = "code")
    @Fetch(FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Area> getChildList() {
        return childList;
    }

    public void setChildList(List<Area> childList) {
        this.childList = childList;
    }


    @Length(min = 1, max = 255)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 1, max = 1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Length(min = 0, max = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Transient
    public static void sortList(List<Area> list, List<Area> sourcelist, String parentId) {
        for (int i = 0; i < sourcelist.size(); i++) {
            Area e = sourcelist.get(i);
            if (e.getParent() != null && e.getParent().getId() != null
                    && e.getParent().getId().equals(parentId)) {
                list.add(e);
                // 判断是否还有子节点, 有则继续获取子节点
                for (int j = 0; j < sourcelist.size(); j++) {
                    Area childe = sourcelist.get(j);
                    if (childe.getParent() != null && childe.getParent().getId() != null
                            && childe.getParent().getId().equals(e.getId())) {
                        sortList(list, sourcelist, e.getId());
                        break;
                    }
                }
            }
        }
    }

    @Transient
    @JsonIgnore
    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    @Transient
    @JsonIgnore
    public static boolean isAdmin(String id) {
        return id != null && id.equals("1");
    }
}