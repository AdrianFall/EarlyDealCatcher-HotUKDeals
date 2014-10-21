package observer;

import gui.MainGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AdvancedObserverThread implements Runnable {

	int iterationCounter = 0;
	private String link;
	public static ArrayList<String> titles = new ArrayList<String>();
	ArrayList<String> temperatures = new ArrayList<String>();
	ArrayList<Date> dates = new ArrayList<Date>();
	static ArrayList<String> hrefs = new ArrayList<String>();
	ArrayList<String> prices = new ArrayList<String>();
	private final int sleepTime = 300000;
	private int countdownNumber;
	private Timer timer;
	
	
	
	public AdvancedObserverThread(String link) {
		this.link = link;
	}

	@Override
	public void run() {

		try {
			
			MainGUI.printToConsole("Bootup Data Extracted and Saved.");

			// Loop infinitely for updates.
			while (true) {
				
				try {
					// Loop over stored items every 5 mins
					obtainData();
					countdownNumber = new Integer(sleepTime);
					// Perform the countdown
					timer = new Timer(1000, new ActionListener() { 
			            @Override
			            public void actionPerformed(ActionEvent e) {
			                if(countdownNumber > 0) {
			                	countdownNumber-= 1000;
			                    MainGUI.setCountdownLabelText("<html>Update Countdown: <br>" + "Seconds left: " + (countdownNumber/1000) + "</html>");
			                } else {
			                	// DONE
			                	MainGUI.setCountdownLabelText("Updating...");
			                	timer.stop();
			                }
			            }
					});
					timer.start();
					
					
					TimeUnit.MILLISECONDS.sleep(sleepTime);
					loopOverStoredItems();
					
					
				} catch (InterruptedException e) {
					MainGUI.printToConsole("Couldn't perform TimeUnit.ms.sleep");
					e.printStackTrace();
				}
				
			}// END while

		} catch (IOException e) {
			MainGUI.printToConsole("Failed on bootup extraction.");
			e.printStackTrace();
		}

	}// end method run

	private void loopOverStoredItems() {
		for (int i = 0; i < hrefs.size(); i++) {
			final String tempHref = hrefs.get(i);

			try {
				Document doc = Jsoup.connect(tempHref).get();
				
				// getting the tag class (deals)
				Elements content = doc
						.getElementsByClass("temperature");
				System.err.println("Content testv1 = " + content);
				String temperature = content.get(0).text();
				
				// Obtain the current Date & Time
				Date currentDate = Calendar.getInstance().getTime();
			
				// Obtain the stored date of item
				Date tempDate = dates.get(i);
				
			
				
				
				int differenceInMinutes = (int)((currentDate.getTime()/60000) - (tempDate.getTime()/60000));
				
				
				int differenceInMultipleOfFive =  (int) (5 * Math.ceil(differenceInMinutes / 5));
				         // 20 / 5
						// switch (GOOD = 4*6.66)  
				
				
			
				int temperatureDifference = (Integer.parseInt(temperature.substring(0, (temperature.length()-1))) - Integer.parseInt(temperatures.get(i).substring(0, (temperatures.get(i).length() -1))));
				/*System.err.println("TEMPERATURE DIFFERENCE = " + temperatureDifference);
				System.err.println("Title - " + titles.get(i));*/
				
				int multiplesOfFive = differenceInMultipleOfFive / 5;
				
				boolean okRating = (temperatureDifference > ((multiplesOfFive) * 6.66)) && (temperatureDifference < ((multiplesOfFive) * 8.33));
				boolean decentRating = (temperatureDifference > ((multiplesOfFive) * 8.33)) && (temperatureDifference < ((multiplesOfFive) * 10));
				boolean goodRating = (temperatureDifference > ((multiplesOfFive) * 10)) && (temperatureDifference < ((multiplesOfFive) * 11.66));
				boolean veryGoodRating = (temperatureDifference > ((multiplesOfFive) * 11.66));
				
				
				if (okRating) {
					                  // status, dif in min, current temp, first temp, temp difference, item title, 
					printStatistics("ok", differenceInMinutes, temperature, temperatures.get(i), temperatureDifference, titles.get(i));
					MainGUI.addElementToList(titles.get(i), 1);
				} else if (decentRating) {
					printStatistics("decent", differenceInMinutes, temperature, temperatures.get(i), temperatureDifference, titles.get(i));
					MainGUI.addElementToList(titles.get(i), 2);
				} else if (goodRating) {
					printStatistics("good", differenceInMinutes, temperature, temperatures.get(i), temperatureDifference, titles.get(i));
					MainGUI.addElementToList(titles.get(i), 3);
				} else if (veryGoodRating) {
					printStatistics("very good", differenceInMinutes, temperature, temperatures.get(i), temperatureDifference, titles.get(i));
					MainGUI.addElementToList(titles.get(i), 4);
				} else {
					MainGUI.checkForExistenceAndRemoveElementFromList(titles.get(i));
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		MainGUI.displayNotification();
	}

	private void printStatistics(String status, int differenceInMinutes,
			String currentTemperature, String firstTemp, int temperatureDifference,
			String title) {
		System.out.println("                          STATUS: " + status + ", ITEM: " + title);
		System.out.println("                             First Temperature: " + firstTemp + ", Current Temperature: " + currentTemperature + ", Difference: " + temperatureDifference);
		System.out.println("                               Over " + differenceInMinutes + " minutes.");
		System.out.println("                   .................... ............... ................... ................. ................ ................ .............");
		
	}
	

	

	private void obtainData() throws IOException {
		// getting the web
		Document doc = Jsoup.connect(link).get();
		// getting the tag class (deals)
		Elements content = doc.getElementsByClass("s-items-listings");
		Element allFirstPageItems = content.get(0);

		for (Element el : allFirstPageItems.getElementsByTag("li")) {

			try {

				Elements headerElements = el.getElementsByTag("h2").get(0)
						.getElementsByTag("a");

				String title = headerElements.attr("title");
				String href = headerElements.attr("href");

				/* System.err.println(el); */

				String spanText = el.getElementsByTag("span").text();
				

				// Determine whether the item was made hot or found
				String itemStatus = "";
				if (spanText.contains("found")) {
					itemStatus = " found ";
				} else if (spanText.contains("made hot")) {
					itemStatus = "made hot ";
				}

				// Bmade hotB // BagoB
				String[] splitSpanText = spanText.split(itemStatus);
				System.out.println("Split span text[0] = " + splitSpanText[0]);
				System.out.println("Split span text[1] = " + splitSpanText[1]);
				if (splitSpanText.length > 1) {
					String price = splitSpanText[0];
					if (price.equals(""))
						price = "£0";

					String remainderSplitSpanText = splitSpanText[1];

					String[] splitRemainderSpanText = remainderSplitSpanText
							.split(" ago");
					String unformatedTime = splitRemainderSpanText[0];
					String temperature = el.getElementsByClass("temperature").get(0).text();
					System.err.println("TEMPERATURE = " + temperature);

				
					// Store the data
					if (!hrefs.contains(href)) {
						hrefs.add(href);
						titles.add(title);
						prices.add(price);
						temperatures.add(temperature.trim());
						
						
				
						// Obtain the current date & time, and store it in the dates array list
						dates.add(Calendar.getInstance().getTime());
						
					}
					// END Store the data

				} else {
					// TODO Expired offer
				}

			} catch (Exception e) {
				System.out.println("One of the li didn't contain a header (advert)");
			}
		} // END FOR
	}

	public static ArrayList<String> getHrefs() {

		return hrefs;

	}

}// end class

