package crawler.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.abola.crawler.CrawlerPack;

/**
 * 範例: 使用爬蟲包取得八卦版最後50篇文章的網址
 * 
 * 重點
 * 1. cookie 如何設定
 * 2. 如何從文章特性中提取資料
 * 3. 分頁資料練習
 * 4. 簡易將資料量化，做簡易的資料探索
 *
 * @author Abola Lee
 */
class PttBoard {

	final static String board = "Gossiping";
    final static String pttMainPage = "https://www.ptt.cc/bbs/"+board+"/index.html";
    final static String pttIndexPage = "https://www.ptt.cc/bbs/"+board+"/index%s.html";
    
    // 取得最後幾篇的文章數量(bug?)
    static Integer loadLastPosts = 30;

    public static void main(String[] argv){

    	// 取得前一頁的index
//        data sample
//        ---
//        <div class="action-bar">
//            <div class="btn-group btn-group-dir">
//                <a class="btn selected" href="/bbs/Gossiping/index.html">看板</a>
//                <a class="btn" href="/man/Gossiping/index.html">精華區</a>
//            </div>
//            <div class="btn-group btn-group-paging">
//                <a class="btn wide" href="/bbs/Gossiping/index1.html">最舊</a>
//                <a class="btn wide" href="/bbs/Gossiping/index14940.html">‹ 上頁</a>
//                <a class="btn wide disabled">下頁 ›</a>
//                <a class="btn wide" href="/bbs/Gossiping/index.html">最新</a>
//            </div>
//        </div>
        String prevPage =
            CrawlerPack.start()
                .addCookie("over18","1")                // 八卦版進入需要設定cookie
                .getFromHtml(pttMainPage)               // 遠端資料格式為 HTML
                .select(".action-bar a:matchesOwn(上頁)")  // 取得右上角『上頁』的內容
                .get(0).attr("href")
                .replaceAll("/bbs/"+board+"/index([0-9]+).html", "$1");
        
        
        // 目前最末頁 index 編號
        Integer lastPage = Integer.valueOf(prevPage);

        List<String> lastPostsLink = new ArrayList<>();

        while ( loadLastPosts > lastPostsLink.size() ){
            String currPage = String.format(pttIndexPage, lastPage--);

            Elements links =
                CrawlerPack.start()
                    .addCookie("over18", "1")
                    .getFromHtml(currPage)
                    .select(".title > a");

            for( Element link: links) lastPostsLink.add( link.attr("href") );
        }

        System.out.println("作者,標題,推總數,不重複推,噓總數,不重複噓,總參與人數");
        
        // 個別分頁每一頁資訊
        for(String url : lastPostsLink){
        
        	System.out.println( analyzeFeed(url) );

        	// 重要：為什麼要有這一行？
        	try{Thread.sleep(150);}catch(Exception e){}
        }
    }
    
    /**
     * 分析輸入的文章，簡易統計 
     * 
     * @param url
     * @return
     */
    static String analyzeFeed(String url){
    	
    	// 取得 Jsoup 物件，稍後要做多次 select
        Document feed = 
        	CrawlerPack.start()
		        .addCookie("over18","1")                // 八卦版進入需要設定cookie
		        .getFromHtml("https://www.ptt.cc"+url);           // 遠端資料格式為 HTML
        
        
        // 1. 文章作者
        String feedAuthor = feed.select("span:contains(作者) + span").text();
        
        // 2. 文章標題
        String feedTitle = feed.select("span:contains(標題) + span").text();
        
         
        // 3. 按推總數
        Integer feedLikeCount = 
        		countReply(feed.select(".push-tag:matchesOwn(推) + .push-userid"));
        
        // 4. 不重複推文數
        Integer feedLikeCountNoRep = 
        		countReplyNoRepeat(feed.select(".push-tag:matchesOwn(推) + .push-userid"));

        // 5. 按噓總數
        Integer feedUnLikeCount = 
        		countReply(feed.select(".push-tag:matchesOwn(噓) + .push-userid"));
        	
        // 6. 不重複噓文數
        Integer feedUnLikeCountNoRep = 
        		countReplyNoRepeat(feed.select(".push-tag:matchesOwn(噓) + .push-userid"));

        // 7. 不重複噓文數
        Integer feedReplyCountNoRep = 
        		countReplyNoRepeat(feed.select(".push-tag + .push-userid"));
      
        String output = "\"" + feedAuthor +"\","
        				+ "\"" + feedTitle +"\","
        				+ feedLikeCount +","
        				+ feedLikeCountNoRep +","
        				+ feedUnLikeCount +","
        				+ feedUnLikeCountNoRep +","
        				+ feedReplyCountNoRep;
    	return output;
    }
    
    /**
     * 推文人數總計
     * @param reply
     * @return
     */
    static Integer countReply(Elements reply){
    	return reply.text().split(" ").length;
    }
    
    /**
     * 推文人數總計
     * @param reply
     * @return
     */
    static Integer countReplyNoRepeat(Elements reply){
    	return new HashSet<String>( 
    			Arrays.asList(
    				reply.text().split(" ") 
    			) 
    		).size();    	
    }    
}