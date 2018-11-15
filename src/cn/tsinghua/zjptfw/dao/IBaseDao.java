package cn.tsinghua.zjptfw.dao;

import java.util.List;

public interface IBaseDao<T> {

    int save(T t);

    int[] save(List<T> ts);

    /**
     * 按照对象的id进行更新
     * @param t
     * @return
     */
    int updateRowById(T t);

    int deleteById(Long id);

    T findById(Long id);

    List<T> findByConditions(T t);

    List<T> findAll();
}
