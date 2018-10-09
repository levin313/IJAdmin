
package com.levin.common.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.levin.common.utils.idgen.IdGenerate;
import com.levin.modules.sys.entity.User;
import com.levin.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity支持类
 *
 * @author Levin
 */
@MappedSuperclass
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;        // 编号
    protected String remarks;    // 备注
    protected User createBy;    // 创建者
    protected Date createDate;  // 创建日期
    protected User updateBy;    // 更新者
    protected Date updateDate;  // 更新日期
    protected Integer delFlag;    //状态


    @PrePersist
    public void prePersist() {
        this.id = IdGenerate.uuid();

        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getId())) {
            this.updateBy = user;
            this.createBy = user;
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;
    }

    @PreUpdate
    public void preUpdate() {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getId())) {
            this.updateBy = user;
        }
        this.updateDate = new Date();
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Length(min = 0, max = 255)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}
