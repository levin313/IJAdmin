package com.levin.common.persistence;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.levin.common.persistence.Order.*;
import com.levin.common.persistence.Filter.*;

/**
 * Dao - 基类
 */
public abstract class BaseDao<T, ID extends Serializable> {

    /**
     * 实体类类型
     */
    private Class<T> entityClass;

    /**
     * 别名数
     */
    private static volatile long aliasCount = 0;

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public BaseDao() {
        Type type = getClass().getGenericSuperclass();
        Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
        entityClass = (Class<T>) parameterizedType[0];
    }

    public T find(ID id) {
        if (id != null) {
            return entityManager.find(entityClass, id);
        }
        return null;
    }

    public T find(ID id, LockModeType lockModeType) {
        if (id != null) {
            if (lockModeType != null) {
                return entityManager.find(entityClass, id, lockModeType);
            } else {
                return entityManager.find(entityClass, id);
            }
        }
        return null;
    }

    public List<T> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return findList(criteriaQuery, first, count, filters, orders);
    }

    public Page<T> findPage(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return findPage(criteriaQuery, pageable);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findBysql(String jpql) {
        return entityManager.createNativeQuery(jpql)
                .setFlushMode(FlushModeType.COMMIT).getResultList();
    }

    @SuppressWarnings("unchecked")
    public Page<List<Object[]>> findPageBysql(String sql, Pageable pageable) {
        Query q = entityManager.createNativeQuery(sql).setFlushMode(FlushModeType.COMMIT);
        long total = q.getResultList().size();
        List<Object[]> lst = q.setFlushMode(FlushModeType.COMMIT)
                .setFirstResult((pageable.getPage() - 1) * pageable.getRows())
                .setMaxResults(pageable.getRows()).getResultList();
        return new Page<List<Object[]>>(lst, total, pageable, 0);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findBysql(String jpql, Map<String, Object> params) {
        Query q = entityManager.createNativeQuery(jpql);
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                q.setParameter(key, params.get(key));
            }
        }
        return q.setFlushMode(FlushModeType.COMMIT).getResultList();
    }

    public Page<T> findPage(Pageable pageable, T t) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        List<Filter> filters = pageable.getFilters();
        filters.addAll(getFilters(t));
        pageable.setFilters(filters);
        return findPage(criteriaQuery, pageable);
    }

    private List<Filter> getFilters(T t) {
        List<Filter> filters = new ArrayList<Filter>();
        Field[] fields = t.getClass().getDeclaredFields(); // 获取对象的所有属性对象：type，name，value
        for (Field fd : fields) {
            String name = fd.getName();
            if (name.equals("serialVersionUID"))
                continue;
            if (fd.getType().toString().equalsIgnoreCase("interface java.util.Set") ||
                    fd.getType().toString().equalsIgnoreCase("interface java.util.List"))
                continue;
            Object value = getFieldValueByName(name, t);
            if (value != null) {
                Filter fl = new Filter();
                fl.setProperty(name);
                fl.setValue(value);
                if (fd.getType().toString().equalsIgnoreCase("class java.lang.string")) {
                    if (((String) value).trim().equals(""))
                        continue;
                    fl.setOperator(Operator.like);
                } else
                    fl.setOperator(Operator.eq);
                filters.add(fl);
            }
        }
        return filters;
    }

    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    public long count(Filter... filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return count(criteriaQuery, filters != null ? Arrays.asList(filters) : null);
    }

    public void save(T entity) {
        entityManager.persist(entity);
    }

    public T update(T entity) {
        return entityManager.merge(entity);
    }

    public void delete(T entity) {
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public void refresh(T entity) {
        if (entity != null) {
            entityManager.refresh(entity);
        }
    }

    public void refresh(T entity, LockModeType lockModeType) {
        if (entity != null) {
            if (lockModeType != null) {
                entityManager.refresh(entity, lockModeType);
            } else {
                entityManager.refresh(entity);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ID getIdentifier(T entity) {
        return (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

    public boolean isManaged(T entity) {
        return entityManager.contains(entity);
    }

    public void detach(T entity) {
        entityManager.detach(entity);
    }

    public void lock(T entity, LockModeType lockModeType) {
        if (entity != null && lockModeType != null) {
            entityManager.lock(entity, lockModeType);
        }
    }

    public void clear() {
        entityManager.clear();
    }

    public void flush() {
        entityManager.flush();
    }

    protected List<T> findList(CriteriaQuery<T> criteriaQuery, Integer first, Integer count, List<Filter> filters, List<Order> orders) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Root<T> root = getRoot(criteriaQuery);
        addRestrictions(criteriaQuery, filters);
        addOrders(criteriaQuery, orders);
        if (criteriaQuery.getOrderList().isEmpty()) {
            if (OrderEntity.class.isAssignableFrom(entityClass)) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get(OrderEntity.ORDER_PROPERTY_NAME)));
            } else {
                //criteriaQuery.orderBy(criteriaBuilder.desc(root.get(OrderEntity.CREATE_DATE_PROPERTY_NAME)));
            }
        }
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
        if (first != null) {
            query.setFirstResult(first);
        }
        if (count != null) {
            query.setMaxResults(count);
        }
        return query.getResultList();
    }

    protected Page<T> findPage(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        if (pageable == null) {
            pageable = new Pageable();
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Root<T> root = getRoot(criteriaQuery);
        addRestrictions(criteriaQuery, pageable);
        addOrders(criteriaQuery, pageable);
        if (criteriaQuery.getOrderList().isEmpty()) {
            if (OrderEntity.class.isAssignableFrom(entityClass)) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get(OrderEntity.ORDER_PROPERTY_NAME)));
            } else {
                //criteriaQuery.orderBy(criteriaBuilder.desc(root.get(OrderEntity.CREATE_DATE_PROPERTY_NAME)));
            }
        }
        long total = count(criteriaQuery, null);
        //int totalPages = (int) Math.ceil((double) total / (double) pageable.getRows());
        //if (totalPages < pageable.getPage()) {
        //	pageable.setPage(totalPages);
        //}
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
        query.setFirstResult((pageable.getPage() - 1) * pageable.getRows());
        query.setMaxResults(pageable.getRows());
        return new Page<T>(query.getResultList(), total, pageable);
    }

    protected Long count(CriteriaQuery<T> criteriaQuery, List<Filter> filters) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        addRestrictions(criteriaQuery, filters);

        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        for (Root<?> root : criteriaQuery.getRoots()) {
            Root<?> dest = countCriteriaQuery.from(root.getJavaType());
            dest.alias(getAlias(root));
            copyJoins(root, dest);
        }

        Root<?> countRoot = getRoot(countCriteriaQuery, criteriaQuery.getResultType());
        countCriteriaQuery.select(criteriaBuilder.count(countRoot));

        if (criteriaQuery.getGroupList() != null) {
            countCriteriaQuery.groupBy(criteriaQuery.getGroupList());
        }
        if (criteriaQuery.getGroupRestriction() != null) {
            countCriteriaQuery.having(criteriaQuery.getGroupRestriction());
        }
        if (criteriaQuery.getRestriction() != null) {
            countCriteriaQuery.where(criteriaQuery.getRestriction());
        }
        return entityManager.createQuery(countCriteriaQuery).setFlushMode(FlushModeType.COMMIT).getSingleResult();
    }

    private synchronized String getAlias(Selection<?> selection) {
        if (selection != null) {
            String alias = selection.getAlias();
            if (alias == null) {
                if (aliasCount >= 1000) {
                    aliasCount = 0;
                }
                alias = "szyGeneratedAlias" + aliasCount++;
                selection.alias(alias);
            }
            return alias;
        }
        return null;
    }

    private Root<T> getRoot(CriteriaQuery<T> criteriaQuery) {
        if (criteriaQuery != null) {
            return getRoot(criteriaQuery, criteriaQuery.getResultType());
        }
        return null;
    }

    private Root<T> getRoot(CriteriaQuery<?> criteriaQuery, Class<T> clazz) {
        if (criteriaQuery != null && criteriaQuery.getRoots() != null && clazz != null) {
            for (Root<?> root : criteriaQuery.getRoots()) {
                if (clazz.equals(root.getJavaType())) {
                    return (Root<T>) root.as(clazz);
                }
            }
        }
        return null;
    }

    private void copyJoins(From<?, ?> from, From<?, ?> to) {
        for (Join<?, ?> join : from.getJoins()) {
            Join<?, ?> toJoin = to.join(join.getAttribute().getName(), join.getJoinType());
            toJoin.alias(getAlias(join));
            copyJoins(join, toJoin);
        }
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(fetch.getAttribute().getName());
            copyFetches(fetch, toFetch);
        }
    }

    private void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(fetch.getAttribute().getName());
            copyFetches(fetch, toFetch);
        }
    }

    private void addRestrictions(CriteriaQuery<T> criteriaQuery, List<Filter> filters) {
        if (criteriaQuery == null || filters == null || filters.isEmpty()) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate restrictions = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        for (Filter filter : filters) {
            if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
                continue;
            }
            // mark
            String aa = "";
            if (filter.getValue() != null)
                aa = filter.getValue().getClass().getSimpleName();
            if (filter.getOperator() == Operator.eq && filter.getValue() != null) {
                if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && filter.getValue() instanceof String) {
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(criteriaBuilder.lower(root.<String>get(filter.getProperty())), ((String) filter.getValue()).toLowerCase()));
                } else {
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(filter.getProperty()), filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.ne && filter.getValue() != null) {
                if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && filter.getValue() instanceof String) {
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(criteriaBuilder.lower(root.<String>get(filter.getProperty())), ((String) filter.getValue()).toLowerCase()));
                } else {
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get(filter.getProperty()), filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.gt && filter.getValue() != null) {
//				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.gt(root.<Number> get(filter.getProperty()), (Number) filter.getValue()));
                //mark
                if (aa.equalsIgnoreCase("string"))
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                else
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.gt(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
            } else if (filter.getOperator() == Operator.lt && filter.getValue() != null) {
//				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lt(root.<Number> get(filter.getProperty()), (Number) filter.getValue()));
                //mark
                if (aa.equalsIgnoreCase("string"))
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                else
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lt(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
            } else if (filter.getOperator() == Operator.ge && filter.getValue() != null) {
//				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number> get(filter.getProperty()), (Number) filter.getValue()));
                //mark
                if (aa.equalsIgnoreCase("string"))
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                else
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
            } else if (filter.getOperator() == Operator.le && filter.getValue() != null) {
//				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number> get(filter.getProperty()), (Number) filter.getValue()));
                //mark
                if (aa.equalsIgnoreCase("string"))
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                else
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
            } else if (filter.getOperator() == Operator.like && filter.getValue() != null && filter.getValue() instanceof String) {
                restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String>get(filter.getProperty()), "%%" + filter.getValue() + "%%"));
            } else if (filter.getOperator() == Operator.in && filter.getValue() != null) {
                restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).in(filter.getValue()));
            } else if (filter.getOperator() == Operator.isNull) {
                restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNull());
            } else if (filter.getOperator() == Operator.isNotNull) {
                restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNotNull());
            }
        }
        criteriaQuery.where(restrictions);
    }

    private void addRestrictions(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        if (criteriaQuery == null || pageable == null) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate restrictions = criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder.conjunction();
        if (StringUtils.isNotEmpty(pageable.getSearchProperty()) && StringUtils.isNotEmpty(pageable.getSearchValue())) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.like(root.<String>get(pageable.getSearchProperty()), "%" + pageable.getSearchValue() + "%"));
        }
        if (pageable.getFilters() != null) {
            for (Filter filter : pageable.getFilters()) {
                if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
                    continue;
                }
                String aa = "";
                if (filter.getValue() != null)
                    aa = filter.getValue().getClass().getSimpleName();
                if (filter.getOperator() == Operator.eq && filter.getValue() != null) {
                    if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && filter.getValue() instanceof String) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(criteriaBuilder.lower(root.<String>get(filter.getProperty())), ((String) filter.getValue()).toLowerCase()));
                    } else {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(filter.getProperty()), filter.getValue()));
                    }
                } else if (filter.getOperator() == Operator.ne && filter.getValue() != null) {
                    if (filter.getIgnoreCase() != null && filter.getIgnoreCase() && filter.getValue() instanceof String) {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(criteriaBuilder.lower(root.<String>get(filter.getProperty())), ((String) filter.getValue()).toLowerCase()));
                    } else {
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(root.get(filter.getProperty()), filter.getValue()));
                    }
                } else if (filter.getOperator() == Operator.gt && filter.getValue() != null) {
                    if (aa.equalsIgnoreCase("string"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                    else if (aa.equalsIgnoreCase("date"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThan(root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                    else
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.gt(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.lt && filter.getValue() != null) {
                    if (aa.equalsIgnoreCase("string"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                    else if (aa.equalsIgnoreCase("date"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThan(root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                    else
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lt(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.ge && filter.getValue() != null) {
                    if (aa.equalsIgnoreCase("string"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                    else if (aa.equalsIgnoreCase("date"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                    else
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.le && filter.getValue() != null) {
                    if (aa.equalsIgnoreCase("string"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<String>get(filter.getProperty()), (String) filter.getValue()));
                    else if (aa.equalsIgnoreCase("date"))
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                    else
                        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number>get(filter.getProperty()), (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.like && filter.getValue() != null && filter.getValue() instanceof String) {
                    restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
                            .like(root.<String>get(filter.getProperty()), (String) "%" + filter.getValue() + "%"));
                } else if (filter.getOperator() == Operator.in && filter.getValue() != null) {
                    restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).in(filter.getValue()));
                } else if (filter.getOperator() == Operator.isNull) {
                    restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNull());
                } else if (filter.getOperator() == Operator.isNotNull) {
                    restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNotNull());
                }
            }
        }
        criteriaQuery.where(restrictions);
    }

    private void addOrders(CriteriaQuery<T> criteriaQuery, List<Order> orders) {
        if (criteriaQuery == null || orders == null || orders.isEmpty()) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<javax.persistence.criteria.Order> orderList = new ArrayList<javax.persistence.criteria.Order>();
        if (!criteriaQuery.getOrderList().isEmpty()) {
            orderList.addAll(criteriaQuery.getOrderList());
        }
        for (Order order : orders) {
            if (order.getDirection() == Direction.asc) {
                orderList.add(criteriaBuilder.asc(root.get(order.getProperty())));
            } else if (order.getDirection() == Direction.desc) {
                orderList.add(criteriaBuilder.desc(root.get(order.getProperty())));
            }
        }
        criteriaQuery.orderBy(orderList);
    }

    private void addOrders(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        if (criteriaQuery == null || pageable == null) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<javax.persistence.criteria.Order> orderList = new ArrayList<javax.persistence.criteria.Order>();
        if (!criteriaQuery.getOrderList().isEmpty()) {
            orderList.addAll(criteriaQuery.getOrderList());
        }
        if (StringUtils.isNotEmpty(pageable.getOrderProperty()) && pageable.getOrderDirection() != null) {
            if (pageable.getOrderDirection() == Direction.asc) {
                orderList.add(criteriaBuilder.asc(root.get(pageable.getOrderProperty())));
            } else if (pageable.getOrderDirection() == Direction.desc) {
                orderList.add(criteriaBuilder.desc(root.get(pageable.getOrderProperty())));
            }
        }
        if (pageable.getOrders() != null) {
            for (Order order : pageable.getOrders()) {
                if (order.getDirection() == Direction.asc) {
                    orderList.add(criteriaBuilder.asc(root.get(order.getProperty())));
                } else if (order.getDirection() == Direction.desc) {
                    orderList.add(criteriaBuilder.desc(root.get(order.getProperty())));
                }
            }
        }
        if (pageable.getSort() != null) {
            if (pageable.getOrder() != null && pageable.getOrder().equalsIgnoreCase("asc"))
                orderList.add(criteriaBuilder.asc(root.get(pageable.getSort())));
            else
                orderList.add(criteriaBuilder.desc(root.get(pageable.getSort())));
        }
        criteriaQuery.orderBy(orderList);
    }


    /**
     * 按HQL查询对象列表.
     *
     * @param hql
     * @param values 数量可变的参数,按顺序绑定.
     * @return 结果集合
     */
    @SuppressWarnings("unchecked")
    public <X> List<X> find(final String hql, final Object... values) {
        return createQuery(hql, values).getResultList();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param hql
     * @param values 命名参数,按名称绑定.
     * @return 对象集合
     */
    @SuppressWarnings("unchecked")
    public <X> List<X> find(final String hql, final Map<String, ?> values) {
        return createQuery(hql, values).getResultList();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param hql
     * @param values 数量可变的参数,按顺序绑定.
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <X> X findUnique(final String hql, final Object... values) {
        List<X> resultList = createQuery(hql, values).getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param hql
     * @param values 命名参数,按名称绑定.
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <X> X findUnique(final String hql, final Map<String, ?> values) {
        List<X> resultList = createQuery(hql, values).getResultList();
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     *
     * @param hql
     * @param values 数量可变的参数,按顺序绑定.
     * @return 更新记录数.
     */
    public int batchExecute(final String hql, final Object... values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     *
     * @param hql
     * @param values 命名参数,按名称绑定.
     * @return 更新记录数.
     */
    public int batchExecute(final String hql, final Map<String, ?> values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     *
     * @param queryString
     * @param values      数量可变的参数,按顺序绑定.
     * @return Query
     */
    public Query createQuery(final String queryString, final Object... values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = entityManager.createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(String.valueOf(i), values[i]);
            }
        }
        return query;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     * 与find()函数可进行更加灵活的操作.
     *
     * @param queryString
     * @param values      命名参数,按名称绑定.
     * @return Query
     */
    public Query createQuery(final String queryString, final Map<String, ?> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = entityManager.createQuery(queryString);
        if (values != null) {
            for (String key : values.keySet()) {
                query.setParameter(key, values.get(key));
            }
        }
        return query;
    }

    /**
     * 根据查询SQL与参数列表创建Query对象.
     *
     * @param queryString
     * @param values      数量可变的参数,按顺序绑定.
     * @return SQLQuery
     */
    public Query createSQLQuery(final String queryString, final Object... values) {
        Query sqlQuery = entityManager.createNativeQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                sqlQuery.setParameter(String.valueOf(i), values[i]);
            }
        }
        return sqlQuery;
    }

    /**
     * 根据查询SQL与参数列表创建Query对象.
     *
     * @param queryString
     * @param values      命名参数,按名称绑定.
     * @return SQLQuery
     */
    public Query createSQLQuery(final String queryString, final Map<String, ?> values) {
        Query sqlQuery = entityManager.createNativeQuery(queryString);
        if (values != null) {
            for (String key : values.keySet()) {
                sqlQuery.setParameter(key, values.get(key));
            }
        }
        return sqlQuery;
    }

}