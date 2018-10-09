
package com.levin.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.levin.common.base.BaseEntity;
import com.levin.common.utils.Collections3;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 用户Entity
 */
@Entity
@Table(name = "t_sys_user")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseEntity<User> {
    private static final long serialVersionUID = 1L;
    /**
     * 归属部门
     */
    private Organization organization;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 密码
     */
    private String password;
    /**
     * 原始密码
     */
    private String plainPass;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 工号
     */
    private String no;
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 最后登陆IP
     */
    private String loginIp;
    /**
     * 最后登陆日期
     */
    private Date loginDate;

    /**
     * 拥有角色列表
     */
    private List<Role> roleList = Lists.newArrayList();

    public User() {
        super();
    }

    public User(String id) {
        this();
        this.id = id;
    }

    public User(Organization organization, String loginName, String plainPass, String phone) {
        this.organization = organization;
        this.loginName = loginName;
        this.plainPass = plainPass;
        this.phone = phone;
    }

    @ManyToOne
    @JoinColumn(name = "org_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    @NotNull(message = "归属部门不能为空")
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Length(min = 1, max = 100)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @JsonIgnore
    @Length(min = 1, max = 100)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Length(min = 1, max = 100)
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    @Length(min = 1, max = 100)
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Email
    @Length(min = 0, max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(min = 0, max = 200)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Transient
    public String getRemarks() {
        return remarks;
    }

    @Length(min = 0, max = 100)
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Transient
    public Date getCreateDate() {
        return createDate;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "t_sys_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    @OrderBy("id")
    @Fetch(FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    @Transient
    @JsonIgnore
    public List<String> getRoleIdList() {
        List<String> roleIdList = Lists.newArrayList();
        for (Role role : roleList) {
            roleIdList.add(role.getId());
        }
        return roleIdList;
    }

    @Transient
    public void setRoleIdList(List<String> roleIdList) {
        roleList = Lists.newArrayList();
        for (String roleId : roleIdList) {
            Role role = new Role();
            role.setId(roleId);
            roleList.add(role);
        }
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    @Transient
    public String getRoleNames() {
        return Collections3.extractToString(roleList, "name", ", ");
    }

    @Transient
    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    @Transient
    public static boolean isAdmin(String id) {
        return id != null && id.equals("1");
    }

    @Transient
    public String getPlainPass() {
        return plainPass;
    }

    public void setPlainPass(String plainPass) {
        this.plainPass = plainPass;
    }
}