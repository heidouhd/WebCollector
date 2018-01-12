package com.py.liup;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;

public class DemoTypeCrawler extends RamCrawler {

    /*
        该教程是DemoMetaCrawler的简化版

        该Demo爬虫需要应对豆瓣图书的三种页面：
        1）标签页（taglist，包含图书列表页的入口链接）
        2）列表页（booklist，包含图书详情页的入口链接）
        3）图书详情页（content）

        另一种常用的遍历方法可参考TutorialCrawler
     */
    @Override
    public void visit(Page page, CrawlDatums next) {

        if(page.matchType("taglist")){
            //如果是列表页，抽取内容页链接
            //将内容页链接的type设置为content，并添加到后续任务中
            next.add(page.links("table.tagCol td>a"),"booklist");
        }else if(page.matchType("booklist")){
            next.add(page.links("div.info>h2>a"),"content");
        }else if(page.matchType("content")){
            //处理内容页，抽取书名和豆瓣评分
            String title=page.select("h1>span").first().text();
            String score=page.select("strong.ll.rating_num").first().text();
            System.out.println("title:"+title+"\tscore:"+score);
        }

    }

    public static void main(String[] args) throws Exception {
        DemoTypeCrawler crawler = new DemoTypeCrawler();
        crawler.addSeed("https://book.douban.com/tag/","taglist");


        /*可以设置每个线程visit的间隔，这里是毫秒*/
        //crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
        //crawler.setRetryInterval(1000);
        crawler.setThreads(30);
        crawler.start(3);
    }
}
