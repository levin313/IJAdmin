package com.levin.common.base;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import com.levin.common.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * Service - 基类 其他Service继承此类，实现基本的增删改查
 * 子类需要重写setBaseDao(BaseDao<T, ID> baseDao)方法
 *
 * @author Levin
 */
@Transactional
public class BaseService<T, ID extends Serializable> {

    protected BaseDao<T, ID> baseDao;

    public void setBaseDao(BaseDao<T, ID> baseDao) {
        this.baseDao = baseDao;
    }

    @Transactional(readOnly = true)
    public T find(ID id) {
        return baseDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return findList(null, null, null, null);
    }

    @Transactional(readOnly = true)
    public T findUniqueBy(String key, Object value) {
        List<T> list = findList(null, null, Collections.singletonList(Filter.eq(key, value)), null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<T> findBy(String key, Object value) {
        return findList(null, null, Collections.singletonList(Filter.eq(key, value)), null);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<T> findList(ID... ids) {
        List<T> result = new ArrayList<T>();
        if (ids != null) {
            for (ID id : ids) {
                T entity = find(id);
                if (entity != null) {
                    result.add(entity);
                }
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<T> findList(Integer count, List<Filter> filters, List<Order> orders) {
        return findList(null, count, filters, orders);
    }

    @Transactional(readOnly = true)
    public List<T> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
        return baseDao.findList(first, count, filters, orders);
    }

    @Transactional(readOnly = true)
    public Page<T> findPage(Pageable pageable) {
        return baseDao.findPage(pageable);
    }

    @Transactional(readOnly = true)
    public long count() {
        return count(new Filter[]{});
    }

    @Transactional(readOnly = true)
    public long count(Filter... filters) {
        return baseDao.count(filters);
    }

    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return baseDao.find(id) != null;
    }

    @Transactional(readOnly = true)
    public boolean exists(Filter... filters) {
        return baseDao.count(filters) > 0;
    }

    @Transactional
    public void save(T entity) {
        baseDao.save(entity);
    }

    @Transactional
    public T update(T entity) {
        return baseDao.update(entity);
    }

    @Transactional
    public T update(T entity, String... ignoreProperties) {
        Assert.notNull(entity);
        if (baseDao.isManaged(entity)) {
            throw new IllegalArgumentException("Entity must not be managed");
        }
        T persistant = baseDao.find(baseDao.getIdentifier(entity));
        if (persistant != null) {
            copyProperties(entity, persistant, ignoreProperties);
            return update(persistant);
        } else {
            return update(entity);
        }
    }

    @Transactional
    public void delete(ID id) {
        delete(baseDao.find(id));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void delete(ID... ids) {
        if (ids != null) {
            for (ID id : ids) {
                delete(baseDao.find(id));
            }
        }
    }

    @Transactional
    public void delete(T entity) {
        baseDao.delete(entity);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void copyProperties(Object source, Object target, String[] ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object sourceValue = readMethod.invoke(source);
                        Object targetValue = readMethod.invoke(target);
                        if (sourceValue != null && targetValue != null && targetValue instanceof Collection) {
                            Collection collection = (Collection) targetValue;
                            collection.clear();
                            collection.addAll((Collection) sourceValue);
                        } else {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, sourceValue);
                        }
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy properties from source to target", ex);
                    }
                }
            }
        }
    }

    public Page<T> findPage(Pageable pageable, T t) {
        return baseDao.findPage(pageable, t);
    }

}