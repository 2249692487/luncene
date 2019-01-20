package com.test.test;

import com.test.dao.BookDao;
import com.test.dao.impl.BookDaoImpl;
import com.test.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：待描述
 * </p>
 *
 * @author QinLiNa
 * @data 2019/1/20
 */
public class TestIndexManager {
    @Test
    public void testCreateIndexAndDocument() throws Exception {
        //1. 查询数据库表中所有数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> books = bookDao.queryBookList();
        List<Document> docList = new ArrayList<>();

        //2. 遍历数据, 将数据放到域中, 将域放入文档中, 将文档放到文档集合中
        for (Book book : books) {
            Integer id = book.getId();
            String name = book.getName();
            String pic = book.getPic();
            Float price = book.getPrice();
            String desc = book.getDesc();

            //创建域对象, 第一个参数: 域名, 第二个参数: 域值, 第三个参数:是否存储, 这里都写yes
//            Field idField = new TextField("id", String.valueOf(id), Field.Store.YES);
//            Field nameField = new TextField("name", name, Field.Store.YES);
//            Field picField = new TextField("pic", pic, Field.Store.YES);
//            Field priceField = new TextField("price", String.valueOf(price), Field.Store.YES);
//            Field descField = new TextField("desc", desc, Field.Store.YES);

            /**
             * 是否分词: 否, 虽然需要根据id查询, 但是id是一个整体分词后无意义
             * 是否索引: 是, 因为需要根据id主键查询
             * 是否存储: 是, 因为以后去数据库获取其他内容的时候还要取出来使用
             */
            Field idField = new StringField("id", String.valueOf(id), Field.Store.YES);

            /**
             * 是否分词: 是, 因为需要根据它查询, 并且不是一个整体, 分词后有意义
             * 是否索引: 是, 因为需要根据名称查询
             * 是否存储: 是, 因为查询列表页面需要展示名称
             */
            Field nameField = new TextField("name", name, Field.Store.YES);

            /**
             * 是否分词: 否, 不查询, 不索引就不需要分词
             * 是否索引: 否, 因为根据图片名称查询没有意义, 因为图片名都是自动重命名过的, 就是一串随机字符串
             * 是否存储: 是, 因为查询列表页面需要展示图片
             */
            Field picField = new StoredField("pic", pic);

            /**
             * 是否分词: 是, 因为lucene底层算法规定向根据数值范围查询必须分词
             * 是否索引: 是, 因为需要根据价格查询
             * 是否存储: 是, 因为查询列表页面需要展示价格
             */
            Field priceField = new FloatField("price",price, Field.Store.YES);

            /**
             * 是否分词: 是, 因为需要查询并且不是一个整体分词后有意义
             * 是否索引: 是, 需要根据描述查询
             * 是否存储: 否, 因为查询列表页面不需要展示描述内容
             */
            Field descField = new TextField("desc", desc, Field.Store.NO);

            //创建文档对象, 将域放入文档中
            Document doc = new Document();
            doc.add(idField);
            doc.add(nameField);
            doc.add(picField);
            doc.add(priceField);
            doc.add(descField);

            //将文档放入文档集合中
            docList.add(doc);
        }
        //3. 创建分词器对象, StandardAnalyzer标准分词器, 对英文分词效果很好, 对中文叫做单字分词, 就是一个字就是一个词
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //4. 指定索引库的位置, 将索引和文档输出到指定的索引库中, FSDirectory file System directory文件系统目录
        //存入到硬盘上, RAMDirectory将数据放入内存中
        Directory dir = FSDirectory.open(new File("E:\\dic"));
        //5. 创建输出流的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //6. 创建输出流
        IndexWriter indexWriter = new IndexWriter(dir, config);
        //7. 遍历文档集合
        for (Document document : docList) {
            //8. 将文档输出到指定的索引库
            indexWriter.addDocument(document);
        }


        //9. 提交
        indexWriter.commit();
        //10. 关闭流
        indexWriter.close();
    }

    @Test
    public void testUpdateIndexAndDocument() throws Exception {
        //3. 创建分词器对象, StandardAnalyzer标准分词器, 对英文分词效果很好, 对中文叫做单字分词, 就是一个字就是一个词
        Analyzer analyzer = new StandardAnalyzer();
        //4. 指定索引库的位置, 将索引和文档输出到指定的索引库中, FSDirectory file System directory文件系统目录
        //存入到硬盘上, RAMDirectory将数据放入内存中
        Directory dir = FSDirectory.open(new File("E:\\dic"));
        //5. 创建输出流的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //6. 创建输出流
        IndexWriter indexWriter = new IndexWriter(dir, config);

        //定义更改后的内容
        Field idField = new StringField("id", "3", Field.Store.YES);
        Field nameField = new TextField("name", "xxxx", Field.Store.YES);
        Field picField = new StoredField("pic", "xxxx.jpg");
        Field priceField = new FloatField("price", 12.12f, Field.Store.YES);
        Field descField = new TextField("desc", "asdfasfdasdf", Field.Store.NO);

        //创建文档对象, 将域放入文档中
        Document doc = new Document();
        doc.add(idField);
        doc.add(nameField);
        doc.add(picField);
        doc.add(priceField);
        doc.add(descField);

        //更新, 第一个参数是更新条件, 第二个参数是更新内容
        indexWriter.updateDocument(new Term("id","3"), doc);

        //9. 提交
        indexWriter.commit();
        //10. 关闭流
        indexWriter.close();
    }

    /**
     * 删除
     * 根据条件删除: 索引不变, 指定的文档被删除
     * 全部删除: 索引和文档全部都删除掉
     * @throws Exception
     */
    @Test
    public void testDeleteIndexAndDocument() throws Exception {
        //3. 创建分词器对象, StandardAnalyzer标准分词器, 对英文分词效果很好, 对中文叫做单字分词, 就是一个字就是一个词
        Analyzer analyzer = new StandardAnalyzer();
        //4. 指定索引库的位置, 将索引和文档输出到指定的索引库中, FSDirectory file System directory文件系统目录
        //存入到硬盘上, RAMDirectory将数据放入内存中
        Directory dir = FSDirectory.open(new File("E:\\dic"));
        //5. 创建输出流的初始化对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //6. 创建输出流
        IndexWriter indexWriter = new IndexWriter(dir, config);

        //根据条件删除
        //indexWriter.deleteDocuments(new Term("id","5"));
        //全部删除
        indexWriter.deleteAll();

        //9. 提交
        indexWriter.commit();
        //10. 关闭流
        indexWriter.close();
    }
}
