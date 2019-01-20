package com.test.test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * 描述：待描述
 * </p>
 *
 * @author QinLiNa
 * @data 2019/1/20
 */
public class TestIndexSearch {
    @Test
    public void testIndexSearch() throws Exception {
        //1. 创建分词器对象
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //2. 创建查询语法对象, 如果查询语法中写了域名则从指定的域查询, 如果查询语法中没有写域名则从默认查询域中查询
        QueryParser queryParser = new QueryParser("name", analyzer);
        Query query = queryParser.parse("desc:java");
        //3. 指定索引库位置
        Directory dir = FSDirectory.open(new File("E:\\dic"));
        //4. 创建输入流对象
        IndexReader indexReader = IndexReader.open(dir);
        //5. 创建搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //6. 搜索并返回结果, 第二个参数指定返回多少条数据展示出来
        TopDocs topDocs = indexSearcher.search(query, 2);
        //打印查询到的总记录数
        System.out.println("=====count=====" + topDocs.totalHits);
        //7. 从返回结果中获取查询到的结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8. 遍历结果集
        for (ScoreDoc scoreDoc : scoreDocs) {
            //获取查询到的文档的唯一主键ID, 这个主键是在创建文档的时候lucene自动分配的
            int docID = scoreDoc.doc;
            //通过输入流和文档唯一id, 将指定的文档读取到
            Document doc = indexReader.document(docID);
            //9. 打印查询到的结果
            System.out.println("=====id====" + doc.get("id"));
            System.out.println("=====name====" + doc.get("name"));
            System.out.println("=====pic====" + doc.get("pic"));
            System.out.println("=====price====" + doc.get("price"));
            System.out.println("=====desc====" + doc.get("desc"));
            System.out.println("=====================================================================");
        }

        //10. 关闭流
        indexReader.close();
    }
}
