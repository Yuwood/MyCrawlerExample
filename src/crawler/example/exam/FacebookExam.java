package crawler.example.exam;

import com.github.abola.crawler.CrawlerPack;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 練習：取得任一篇或多篇文章的 Reactions 總數
 *
 *
 * 重點
 * 1. 先利用Graph Api調整出需要的資料
 * 2. 修改程式，使用爬蟲包取得資料
 * 3. 上傳至GitHub
 * 
 * @author Abola Lee
 *
 */
public class FacebookExam {
	
	public static void main(String[] args) {
		
		// 遠端資料路徑

		String uri = 
				"https://graph.facebook.com/v2.8"
				+ "/iamball0228/feed?since=1483228800&until=1491638919&fields=id,created_time,likes.limit(0).summary(total_count),reactions.limit(0).summary(total_count)"
				+ "&access_token=EAACEdEose0cBALKDI0Jpz7Izu18U0dOpsXmujeRE7GwS504eEZB7GIRlnLLXiwbpWOUyb8pmsJ8oznDlPJdAuuOZAzmxkJyZBx4pcDlx0IOcrGPs51IXCkL2bxp5JizZCWuorezKuTUKrCZB57ylZArOUJk4ERLoBbpMHjw3ZCdD629srbHZALjytam2EZAyBvTIJP4u5xuytfwZDZD";


		Elements elems =
				CrawlerPack.start()
				.getFromJson(uri)
				.select("data");
		
		String output = "id,created_Time,reactions,likes\n";

		// 遂筆處理
		for( Element data: elems ){
			String id = data.select("id").text();

			// FIXIT
			String reactions = data.select("reactions total_count").text();
			String created = data.select("created_time").text();
			String likes = data.select("likes total_count").text();


			output += id + ","+created+ "," + likes+","+reactions+ "\n";
		}

		System.out.println( output );
	} 
}
