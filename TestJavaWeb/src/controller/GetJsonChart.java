package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

@WebServlet("/GetJsonChart")
public class GetJsonChart extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetJsonChart() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		String Currency = request.getParameter("Currency");
		System.out.println(Currency);
		
		try {
			ServletContext application = getServletContext();
			String exe = application.getRealPath("WEB-INF/lib/chromedriver.exe");
			System.out.println(exe);
			System.setProperty("webdriver.chrome.driver", exe);
			WebDriver driver = new ChromeDriver();
			driver.get("https://www.esunbank.com.tw/bank/personal/deposit/rate/forex/exchange-rate-chart");
			WebElement currencyArrow = driver.findElement(By.cssSelector(".transformSelect li span"));
	   		currencyArrow.click();
	   		
	   		List<WebElement> currencyOptions = driver.findElements(By.cssSelector(".transformSelectDropdown li"));
			Iterator<WebElement> iter = currencyOptions.iterator();
			WebElement currency = null;
			while(iter.hasNext()){
				currency = iter.next().findElement(By.cssSelector("span"));
				if(currency.getText().toString().contains(Currency))
					break;
				System.out.println(currency.getText());
			}
			System.out.println(currency.getText().toString());
			currency.click();
			
			WebElement spotBtn = driver.findElement(By.cssSelector(".radioBtns [for=\"spot\"]"));
	   		spotBtn.click();
	   		
	   		WebElement durationBtn = driver.findElement(By.cssSelector("div [for=\"oneYear\"]"));
	   		durationBtn.click();
	   		
	   		WebElement dataBtn = driver.findElement(By.cssSelector(".radioBtns [for=\"data\"]"));
	   		dataBtn.click();
	   		
	   		ArrayList<String> Date = new ArrayList<String>();
	   		ArrayList<String> BuyingRate = new ArrayList<String>();
	   		ArrayList<String> SellingRate = new ArrayList<String>();
	   		int pages = 0;
	   		try {
				boolean hasMorePages = true;
				while(hasMorePages) {
					List<WebElement> items = driver.findElements(By.cssSelector("#inteTable tbody tr"));
					System.out.println("size : " + items.size());
					Iterator<WebElement> iter_item = items.iterator();
					int rows = 0;
					while(iter_item.hasNext()){
						String[] item = iter_item.next().getText().toString().split(" ");
						System.out.println("Rows : " + rows);
						if(rows>=2) {
							Date.add(item[0]);
							BuyingRate.add(item[1]);
							SellingRate.add(item[2]);
						}
						rows++;
						System.out.println("Rows_Add : " + rows);
					}
					System.out.println("Pages : " + pages);
					System.out.println("Start next page");
					WebElement nextBtn = driver.findElement(By.cssSelector(".pageNumberBlock .down"));
					System.out.println("nextBtn");
					System.out.println(nextBtn.getAttribute("class").contains("active"));
					if(nextBtn.getAttribute("class").contains("active")) {
						nextBtn.click();
						pages++;
					}
					else {
						hasMorePages = false;
						break;
					}
				}
				
				driver.quit();
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Date", Date);
				jsonObject.put("BuyingRate", BuyingRate);
				jsonObject.put("SellingRate", SellingRate);
				System.out.println(jsonObject);
				
				PrintWriter out = response.getWriter();
				out.write(jsonObject.toString());
				out.flush();
	   		} catch (Exception e) {
	   			driver.quit();
	   			System.out.println(e);
	   			e.printStackTrace();
			}
	   		System.out.println("OK");
		}catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
