package com.test.dao;

import com.test.pojo.Book;

import java.util.List;

/**
 * 描述：待描述
 * </p>
 *
 * @author QinLiNa
 * @data 2019/1/20
 */
public interface BookDao {
    /**
     * 查询所有的book数据
     *
     * @return
     */
    public List<Book> queryBookList();

}
