package com.py.liup;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TutorialCrawler extends BreadthCrawler {

    public TutorialCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    /*
        可以往next中添加希望后续爬取的任务，任务可以是URL或者CrawlDatum
        爬虫不会重复爬取任务，从2.20版之后，爬虫根据CrawlDatum的key去重，而不是URL
        因此如果希望重复爬取某个URL，只要将CrawlDatum的key设置为一个历史中不存在的值即可
        例如增量爬取，可以使用 爬取时间+URL作为key。

        新版本中，可以直接通过 page.select(css选择器)方法来抽取网页中的信息，等价于
        page.getDoc().select(css选择器)方法，page.getDoc()获取到的是Jsoup中的
        Document对象，细节请参考Jsoup教程
    */
    @Override
    public void visit(Page page, CrawlDatums next) {
        if (page.matchUrl("http://blog.csdn.net/.*/article/details/.*")) {
            String title = "";
            String content = "";
            String htmlcontent = "";
            if(page.select(".csdn_top") != null && page.select(".csdn_top").text() != null && !"".equals(page.select(".csdn_top").text())){
                title=page.select(".csdn_top").text();
            }else if(page.select(".article_title") != null && page.select(".article_title").text() != null && !"".equals(page.select(".article_title").text())){
                title=page.select(".article_title").text();
            }else {
                title=page.select(".link_title").text();
            }

            if(page.select(".article_content") != null){
                content = page.select(".article_content").text();
                htmlcontent = page.select(".article_content").html();
            }else {
                content = page.select(".article_content").text();
                htmlcontent = page.select(".article_content").html();
            }

            String url = page.url();


            System.out.println("title:" + title + "\tcontent:" + content);
            String path = "D:\\ceshi\\"+title+".txt";

//            Elements hrefs = page.select("a[href]");
//            if(hrefs != null){
//                for(int i=0;i<hrefs.size();i++){
//                    Element element = hrefs.get(i);
//                    String url = element.attr("href");
//                    try {
//                        startCrawler(url);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

            insertContent(title,content,htmlcontent,url);
//            try {
//                insertContent(title,author);
//                FileWriter fileWriter = new FileWriter(new File(path));
//                fileWriter.write(author);
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public static void main(String[] args) throws Exception {
        String url ="http://blog.csdn.net/";


        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);

        startCrawler(url);

    }
    private static String regex="http://blog.csdn.net/.*/article/details/.*";

    public static void startCrawler(String url ) throws Exception {
        if(url == null || "".equals(url)){
            return;
        }
        TutorialCrawler crawler = new TutorialCrawler("crawler", true);
        crawler.addSeed(url);
        crawler.addRegex(regex);

        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);

        crawler.setThreads(30);
        crawler.start(3);
    }

    private static JdbcTemplate jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
            "jdbc:mysql://localhost/testdb?useUnicode=true&characterEncoding=utf8",
            "root", "123qwe", 5, 30);

    public static void insertContent(String title,String content,String htmlContent,String url){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creatTime = sdf.format(new Date());
        try{
            if (jdbcTemplate != null) {
                int updates=jdbcTemplate.update("insert into content"
                                +" (title,content,htmlcode,url,creatTime) value(?,?,?,?,?)",
                        title, content,htmlContent,url, creatTime);
                if(updates==1){
                    System.out.println("mysql插入成功");
                }
            }

        } catch (Exception ex) {
            jdbcTemplate = null;
            System.out.println("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确!");
        }
    }
}
