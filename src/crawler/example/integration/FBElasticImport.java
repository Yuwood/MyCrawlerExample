package crawler.example.integration;

import com.github.abola.crawler.CrawlerPack;
import com.mashape.unirest.http.Unirest;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Abola Lee on 2016/7/10.
 */
public class FBElasticImport {

    static String elasticHost = "192.168.152.128" ;
    static String elasticPort = "9200" ;
    static String elasticIndex = "fb";
    static String elasticIndexType = "data";
    static String pageName = "iamball0228";
    static long start = 1491696000;
    static int days = 7;

    public static void main(String[] args) {
        for (long datatime = start ; datatime > start-86400*days ;datatime-=3600*8) {
            String uri =
                    "https://graph.facebook.com/v2.6"
                            + "/"+pageName +"/feed?fields=message,comments.limit(0).summary(true),likes.limit(0).summary(true),created_time&since="+(datatime-3600*8)+"&until="+datatime+"&limit=100"
                            + "&access_token=204620726697053%7Cc115418baec73ca230041cba5cdab724";


            try {

                Elements elems =
                        CrawlerPack.start()
                                .getFromJson(uri)
                                .select("data:has(created_time)");
//              System.out.println(elems);
//              String output = "id,名稱,按讚數,討論人數\n";
                System.out.println(elems.size());
                // 遂筆處理
                for (Element data : elems) {


                    String created_time = data.select("created_time").text();
                    String id = data.select("id").text();
                    String message = data.select("message").text();
                    String likes = data.select("likes > summary > total_count").text();
                    String comments = data.select("comments > summary > total_count").text();
//                  String name = data.select("name").text();
//                  String likes = data.select("likes").text();
//                  String talking_about_count = data.select("talking_about_count").text();
//
//                  output += id+",\""+name+"\","+likes+","+talking_about_count+"\n";
//                    System.out.println(created_time);
//                    System.out.println(id);
//                    System.out.println(message);
//                    System.out.println(likes);
                    // Elasticsearch data format


                    String elasticJson = "{" +
                            "\"created_time\":\"" + created_time + "\"" +
                            ",\"message\":\"" + message + "\"" +
                            ",\"likes\":" + likes +
                            ",\"id\":\"" + id + "\"" +
                            ",\"pagename\":\"" + pageName + "\"" +
                            ",\"comments\":" + comments +
                            "}";


                    System.out.println(
                            // curl -XPOST http://localhost:9200/pm25/data -d '{...}'
                            sendPost("http://" + elasticHost + ":" + elasticPort
                                            + "/" + elasticIndex + "/" + elasticIndexType
                                    , elasticJson));
                }
            }catch(Exception e){}

        }
    }



    /***
     * NVL
     * if arg0 isNull then return arg1
     */
    static public <T> T nvl(T arg0, T arg1) {
        return (arg0 == null)?arg1:arg0;
    }

    static String sendPost(String url, String body){
        try{
            return Unirest.post(url)
                    .header("content-type", "text/plain")
                    .header("cache-control", "no-cache")
                    .body(body)
                    .asString().getBody();

        }catch(Exception e){return "Error:" + e.getMessage();}
    }
}