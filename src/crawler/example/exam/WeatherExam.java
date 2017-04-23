package crawler.example.exam;

import com.github.abola.crawler.CrawlerPack;
import org.apache.commons.logging.impl.SimpleLog;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sun.text.resources.iw.FormatData_iw_IL;

import java.io.*;


/**
 * 爬蟲包程式的全貌，就只有這固定的模式
 * 
 * @author Abola Lee
 *
 */
public class WeatherExam {
	// commit test// My commit & push Test
	public static void main(String[] args) throws IOException {

		// set to debug level
		//CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_DEBUG);

		// turn off logging
		CrawlerPack.setLoggerLevel(SimpleLog.LOG_LEVEL_OFF);

		// 遠端資料路徑
		for(int i =1  ; i<=31 ; i++){
			String uri = "http://e-service.cwb.gov.tw/HistoryDataQuery/DayDataController.do?command=viewMain&station=466920&stname=%25E8%2587%25BA%25E5%258C%2597&datepicker=";
			String uri2 = String.format("2016-01-%02d",i);
			uri = uri +uri2;



			Elements jsoup =CrawlerPack.start()

					// 參數設定
					//.addCookie("over18","1")	// 設定cookie
					//.setRemoteEncoding("big5")// 設定遠端資料文件編碼

					// 選擇資料格式 (三選一)
					//.getFromJson(uri)
					.getFromHtml(uri)
					//.getFromXml(uri)

					// 這兒開始是 Jsoup Document 物件操作
					//.select("div#main-content.bbs-screen.bbs-content");
					.select("#MyTable > tbody tr td");
			// .select("div.push > .push-tag:contains(推)")
			//);
			jsoup.select(".first_tr.second_tr").remove();
			String output = "觀測時間 (LST) ObsTime,測站氣壓 (hPa) StnPres, 海平面氣壓 (hPa) SeaPres, 氣溫 (℃) Temperature, 露點溫度 (℃) Td dew point, 相對溼度 (%) RH, 風速 (m/s) WS, 風向 (360degree) WD, 最大陣風 (m/s) WSGust, 最大陣風風向 (360degree) WDGust, 降水量 (mm) Precp, 降水時數 (hr) PrecpHour, 日照時數 (hr) SunShine, 全天空日射量 (MJ/㎡) GloblRad, 能見度 (km) Visb\n";
			String outdata = "";
			int row = 1;
			for(Element data: jsoup){
				String tds = data.select("td").text();
				outdata += tds+",";
				if(row % 15 ==0) outdata += "\n";
				row += 1;
			}
			File file1 =new File("C:\\Users\\jason\\Documents\\test"+i+".csv");

			FileWriter fw1 = new FileWriter(file1);
			fw1.write(output);
			fw1.write(outdata);
			fw1.close();


			//System.out.println(output);
			//System.out.println(outdata);
		}





	}
}
