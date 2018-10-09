
package com.levin.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.levin.common.base.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

/**
 * 角色Entity
 */
@Entity
@Table(name = "t_sys_role")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseEntity<Role> {
	
	private static final long serialVersionUID = 1L;
	private Organization organization;	 // 归属机构
	private String name; 	             // 角色名称
	private String dataScope;            // 数据范围

	private List<User> userList = Lists.newArrayList();                 // 拥有用户列表
	private List<Menu> menuList = Lists.newArrayList();                 // 拥有菜单列表
	private List<Organization> organizationList = Lists.newArrayList(); // 按明细设置数据范围

	// 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
	public static final String DATA_SCOPE_ALL = "1";
	public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";
	public static final String DATA_SCOPE_COMPANY = "3";
	public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";
	public static final String DATA_SCOPE_OFFICE = "5";
	public static final String DATA_SCOPE_SELF = "8";
	public static final String DATA_SCOPE_CUSTOM = "9";
	
	public Role() {
		super();
		this.dataScope = DATA_SCOPE_CUSTOM;
	}

	public Role(String id, String name) {
		this();
		this.id = id;
		this.name = name;
	}
	
	@ManyToOne
	@JoinColumn(name="office_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataScope() {
		return dataScope;
	}

	public void setDataScope(String dataScope) {
		this.dataScope = dataScope;
	}

	@ManyToMany(mappedBy = "roleList", fetch=FetchType.LAZY)
	@OrderBy("id") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnore
	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	@Transient
	public List<String> getUserIdList() {
		List<String> nameIdList = Lists.newArrayList();
		for (User user : userList) {
			nameIdList.add(user.getId());
		}
		return nameIdList;
	}

	@Transient
	public String getUserIds() {
		List<String> nameIdList = Lists.newArrayList();
		for (User user : userList) {
			nameIdList.add(user.getId());
		}
		return StringUtils.join(nameIdList, ",");
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "t_sys_role_menu", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "menu_id") })
	
	@OrderBy("id") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Menu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Menu> menuList) {
		this.menuList = menuList;
	}

	@Transient
	public List<String> getMenuIdList() {
		List<String> menuIdList = Lists.newArrayList();
		for (Menu menu : menuList) {
			menuIdList.add(menu.getId());
		}
		return menuIdList;
	}

	@Transient
	public void setMenuIdList(List<String> menuIdList) {
		menuList = Lists.newArrayList();
		for (String menuId : menuIdList) {
			Menu menu = new Menu();
			menu.setId(menuId);
			menuList.add(menu);
		}
	}

	@Transient
	public String getMenuIds() {
		List<String> nameIdList = Lists.newArrayList();
		for (Menu menu : menuList) {
			nameIdList.add(menu.getId());
		}
		return StringUtils.join(nameIdList, ",");
	}
	
	@Transient
	public void setMenuIds(String menuIds) {
		menuList = Lists.newArrayList();
		if (menuIds != null){
			String[] ids = StringUtils.split(menuIds, ",");
			for (String menuId : ids) {
				Menu menu = new Menu();
				menu.setId(menuId);
				menuList.add(menu);
			}
		}
	}
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_sys_role_org", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "office_id") })
	
	@OrderBy("id") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Organization> getOrganizationList() {
		return organizationList;
	}

	public void setOrganizationList(List<Organization> organizationList) {
		this.organizationList = organizationList;
	}


	@Transient
	public List<String> getOfficeIdList() {
		List<String> officeIdList = Lists.newArrayList();
		for (Organization organization : organizationList) {
			officeIdList.add(organization.getId());
		}
		return officeIdList;
	}

	@Transient
	public void setOfficeIdList(List<String> officeIdList) {
		organizationList = Lists.newArrayList();
		for (String officeId : officeIdList) {
			Organization organization = new Organization();
			organization.setId(officeId);
			organizationList.add(organization);
		}
	}

	@Transient
	public String getOfficeIds() {
		List<String> nameIdList = Lists.newArrayList();
		for (Organization organization : organizationList) {
			nameIdList.add(organization.getId());
		}
		return StringUtils.join(nameIdList, ",");
	}
	
	@Transient
	public void setOfficeIds(String officeIds) {
		organizationList = Lists.newArrayList();
		if (officeIds != null){
			String[] ids = StringUtils.split(officeIds, ",");
			for (String officeId : ids) {
				Organization organization = new Organization();
				organization.setId(officeId);
				organizationList.add(organization);
			}
		}
	}

	
	/**
	 * 获取权限字符串列表
	 */
	@Transient
	public List<String> getPermissions() {
		List<String> permissions = Lists.newArrayList();
		for (Menu menu : menuList) {
			if (menu.getPermission()!=null && !"".equals(menu.getPermission())){
				permissions.add(menu.getPermission());
			}
		}
		return permissions;
	}

	@Transient
	public boolean isAdmin(){
		return isAdmin(this.id);
	}
	
	@Transient
	public static boolean isAdmin(String id){
		return id != null && id.equals("1");
	}

}
