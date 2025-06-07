package core; /**
 * Created by Anusuya on 10/01/2021.
 * 
 *  This class file contains function definition
 *  
 *  
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DefintionClass {
	
	BaseClass baseObject = new BaseClass();
		
	/**
	 * Get a Trade response from Rest End Point URL -e.g https://api.crypto.com/v2/public/get-trades?instrument_name=BTC_USDT
	 * 
	 * @param output TradeResponse file name from Configuration Properties
	 * 
	 * @return response stored in output folder with name specified in Configuration Properties
	 * 
	 * @throws Exception 
	 */

	public void GetTradeRestRepsonse() throws IOException {
		
		Response response = RestAssured.get(baseObject.getTradeRestEndpoinUrl());
		String tradeResponseOutputFile = baseObject.getConfigPropertyValue("TradeResponsefile");
		FileWriter write = new FileWriter(tradeResponseOutputFile);
		PrintWriter printWriter = new PrintWriter(write);
		printWriter.printf("%s"+"%n", response.getBody().asPrettyString());
		printWriter.close();
		System.out.println("Trade response Fetched from End Point URL:"+baseObject.getTradeRestEndpoinUrl());
		System.out.println("Trade response stored to location :"+tradeResponseOutputFile);
	}
	
	/**
	 * Get a CandleStick response from Rest End Point URL -e.g "https://api.crypto.com/v2/public/get-candlestick?instrument_name=BTC_USDT&timeframe=1m"
	 * 
	 * @param output CandleStick Response file name from Configuration Properties
	 * 
	 * @return response stored in output folder with name specified in Configuration Properties
	 * 
	 * @throws Exception 
	 */

	public void GetCandleStickRestRepsonse() throws IOException {
		
		Response response = RestAssured.get(baseObject.getCandleStickRestEndpoinUrl());
		String candleStickResponsefile = baseObject.getConfigPropertyValue("CandleStickResponsefile");
		FileWriter write = new FileWriter(candleStickResponsefile);
		PrintWriter printWriter = new PrintWriter(write);
		printWriter.printf("%s"+"%n", response.getBody().asPrettyString());
		printWriter.close();
		System.out.println("Candle Stick response Fetched from End Point URL:"+baseObject.getCandleStickRestEndpoinUrl());
		System.out.println("Candle Stick response stored to location :"+candleStickResponsefile);
	}
	
	/**
	 * Read a Trade response from local folder
	 * 
	 * @param input Trade file name from Configuration Properties. Also ensure CandleStick file is in the respective folder
	 * 
	 * @return Trade file values are copied to String
	 * 
	 * @throws Exception 
	 */
	public String[] readTradeRestResponse() throws IOException {
		String[] rawTradeData = null;
		String tradeResponse = baseObject.getConfigPropertyValue("TradeResponse");
		File file = new File(tradeResponse);
		Scanner scanner = new Scanner(file);
		List<String> tradearray = new ArrayList<String>();
		while(scanner.hasNext()) {
			tradearray.add(scanner.nextLine());
			rawTradeData = tradearray.toArray(new String[0]);		
		}
		scanner.close();		
		return rawTradeData;
	}
	
	/**
	 * Read a CandleStick response from local folder
	 * 
	 * @param input CandleStick file name from Configuration Properties. Also ensure CandleStick file is in the respective folder
	 * 
	 * @return CandleStick file values are copied to String
	 * 
	 * @throws Exception 
	 */
	public String[] readCandleStickRestResponse() throws IOException {
		String[] rawCandleStickData = null;
		String candleStickResponse = baseObject.getConfigPropertyValue("CandleStickResponse");
		File file = new File(candleStickResponse);
		Scanner scanner = new Scanner(file);
		List<String> tradearray = new ArrayList<String>();
		while(scanner.hasNext()) {
			tradearray.add(scanner.nextLine());
			rawCandleStickData = tradearray.toArray(new String[0]);		
		}
		scanner.close();		
		return rawCandleStickData;
	}
	
	
	
	/**
	 * Manipulate a Trade response
	 * 
	 * @param Trade response in string array format
	 * 
	 * @return Time date and its Open, Close, High, Low Price and Volume / Quantity for particular minute
	 * 
	 * @throws Exception 
	 */
	public String manipulateTradeDetail(String rawTradeData[]) {
		String manipulatedTradeData;
		List<String> foramatedTradeData = new ArrayList<String>();
		List<String> requiredtradeData = new ArrayList<String>();
		ArrayList<Float> PriceArray = new ArrayList<Float>();	
		/**
		 * 
		 * Iterate Trade response and fetch only required data i.e Time stamp, Price and volume
		 * 
		 */
		for(int iteraterawTradeData = 0; iteraterawTradeData< rawTradeData.length; iteraterawTradeData++) {
			rawTradeData[iteraterawTradeData] = rawTradeData[iteraterawTradeData].trim();
			if(rawTradeData[iteraterawTradeData].contains("dataTime")) {
				String dataTime = rawTradeData[iteraterawTradeData].split(":")[1].trim().replace(",","");
				String price = rawTradeData[iteraterawTradeData+3].split(":")[1].trim().replace(",","");
				String volume = rawTradeData[iteraterawTradeData+4].split(":")[1].trim().replace(",","");				
				foramatedTradeData.add(dataTime+","+price+","+volume);
			}
		}
		
		/**
		 * 
		 * From Trade response and latest Trade Time stamp and round to nearest minute and fetch all trade for that minute
		 * 
		 */
		
		float QuantityArray = 0;
		long receivedLatestTradeTime = Long.parseLong(foramatedTradeData.get(0).split(",")[0]);		
		long TradeEndTime = Instant.ofEpochMilli(receivedLatestTradeTime).truncatedTo( ChronoUnit.MINUTES ).toEpochMilli();
		
		long TradeStartTime = TradeEndTime - (60*1000);		
		for(int i = 0; i<foramatedTradeData.size(); i++) {
			String tradeTime= foramatedTradeData.get(i).split(",")[0].trim();
			long convertTradetime = Long.parseLong(tradeTime);
			long convertTradeEndtime = TradeEndTime;
			long convertTradeStarttime = TradeStartTime;
			if(convertTradetime <= convertTradeEndtime && convertTradetime >= convertTradeStarttime) {
				requiredtradeData.add(foramatedTradeData.get(i));
				PriceArray.add(Float. parseFloat(foramatedTradeData.get(i).split(",")[1].trim()));
				QuantityArray = QuantityArray + (Float. parseFloat(foramatedTradeData.get(i).split(",")[2].trim()));
			}
		}
		
		/**
		 * 
		 * Calculate openPrice, closePrice, maxPice, minPrice, volume
		 * 
		 */
		
		String StartTime = String.valueOf(TradeStartTime);
		String openPrice = requiredtradeData.get(requiredtradeData.size()-1).split(",")[1];
		String closePrice = requiredtradeData.get(0).split(",")[1];
		Float maxPice = Collections.max(PriceArray);
		Float minPrice = Collections.min(PriceArray);
		System.out.println("----------------------Trade Price----------------------------");
		System.out.println("Calculated Trade Price of:- from_time :"+StartTime+ " to_time "+TradeEndTime);
		System.out.println("Opening Price of:- from_time ("+StartTime+ ") to_time ("+TradeEndTime+"): " + openPrice);
		System.out.println("Highest Price of:- from_time ("+StartTime+ ") to_time ("+TradeEndTime+"): " + maxPice);
		System.out.println("Lowest Price of:- from_time ("+StartTime+ ") to_time ("+TradeEndTime+"): " + minPrice);
		System.out.println("Closing Price of:- from_time ("+StartTime+ ") to_time ("+TradeEndTime+"): " + closePrice);		
		System.out.println("Volume/Quantity of:- from_time ("+StartTime+ ") to_time ("+TradeEndTime+"): " + QuantityArray);
		
		manipulatedTradeData= StartTime +","+openPrice+","+maxPice.toString() + "," + minPrice.toString() +","+closePrice+","+Float.toString(QuantityArray); 
		return manipulatedTradeData;
	}
	
	/**
	 * Fetch a Candle Stick price
	 * 
	 * @param Time Stamp for which Candle Stick price is needed
	 * 
	 * @return  Open, Close, High, Low Price and Volume / Quantity for particular Time Stamp 
	 * 
	 * @throws Exception 
	 */
	@SuppressWarnings("null")
	public String fetchActualCandleStickPrice(String rawCandleStickData[], String TradePriceTime) {
		String CandleStickPrice = null;
		for(int iterateCandleStickData = 0; iterateCandleStickData< rawCandleStickData.length; iterateCandleStickData++) {
			rawCandleStickData[iterateCandleStickData] = rawCandleStickData[iterateCandleStickData].trim();
			if(rawCandleStickData[iterateCandleStickData].contains(TradePriceTime)){				
				
				String candleStickStartTime = rawCandleStickData[iterateCandleStickData].split(":")[1].trim().replace(",","");
				String candleStickOpenPrice = rawCandleStickData[iterateCandleStickData+1].split(":")[1].trim().replace(",","");
				String candleStickHighPrice = rawCandleStickData[iterateCandleStickData+2].split(":")[1].trim().replace(",","");
				String candleStickLowPrice = rawCandleStickData[iterateCandleStickData+3].split(":")[1].trim().replace(",","");
				String candleStickClosePrice = rawCandleStickData[iterateCandleStickData+4].split(":")[1].trim().replace(",","");
				String candleStickVolume = rawCandleStickData[iterateCandleStickData+5].split(":")[1].trim().replace(",","");
				
				System.out.println("-------------------CandleStick Price--------------------");
				System.out.println("CandleStick Price for Time : "+candleStickStartTime);
				System.out.println("Opening Price for Time : "+candleStickOpenPrice);
				System.out.println("Highest Price for Time : "+candleStickHighPrice);
				System.out.println("Lowest Price for Time  : "+candleStickLowPrice);
				System.out.println("Closing Price for Time : "+candleStickClosePrice);		
				System.out.println("Volume/Quantity for Time : "+candleStickVolume);	
				CandleStickPrice= candleStickStartTime +","+candleStickOpenPrice + "," + candleStickHighPrice +","+candleStickLowPrice+","+candleStickClosePrice+","+candleStickVolume; 
				break;
			}
		}
		return CandleStickPrice;
		
	}
	
	/**
	 * Trade Vs Candle Stick price
	 * 
	 * @param  Open, Close, High, Low Price and Volume / Quantity for particular Time Stamp for both Trade and Candle Stick price
	 * 
	 * @return  status and comparison result
	 * 
	 * @throws Exception 
	 */
	public boolean ValidateExpectedTradePriceVsActualCandlePrice(String TradePrice, String CandleStickPrice) {
		String[] expected = TradePrice.split(",");
		String[] actual = CandleStickPrice.split(",");
		String[] parameter = {"","O", "H","L","C","v"};
		System.out.println("-------- Trade Vs CandleStick Price comparison ---------");
		for (int i =1; i<expected.length; i++) {
			if(expected[i].equals(actual[i])) {
				System.out.println("Trade_"+parameter[i]+": "+expected[i]+" is equal to Candlestick_"+parameter[i]+": "+actual[i]);
			}else {
				System.out.println(expected[i]+" is not equal to "+actual[i]);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * To verify consistency of the candle stick response
	 * 
	 * @param  Duration manipulated from the configuration properties
	 * 
	 * @return  Status and comparison result
	 * 
	 * @throws Exception 
	 */
	public void verifyCandleStickRestConsistency() throws IOException{
		/**
		 * 
		 * Fetch candle stick response 
		 * 
		 */
		Response response = RestAssured.get(baseObject.getCandleStickRestEndpoinUrl());
		Scanner scanner = new Scanner(response.getBody().asPrettyString().trim());
		String rawCandleStickData[] = null;		
		List<String> tradearray = new ArrayList<String>();
		List<String> foramatedTradeData = new ArrayList<String>();
		while(scanner.hasNext()) {
			tradearray.add(scanner.nextLine());
			rawCandleStickData = tradearray.toArray(new String[0]);				
		}
		scanner.close();
		
		/**
		 * 
		 * Read all Times stamp from the candle stick 
		 * 
		 */
		int counter = 0;
		String duration = baseObject.getConfigPropertyValue("Duration").substring(0,1);	
		long candleStickDuration= Long.parseLong(duration);
		for(int iterateCandleStickData = 8; iterateCandleStickData< rawCandleStickData.length-8; iterateCandleStickData = iterateCandleStickData+8) {
			rawCandleStickData[iterateCandleStickData] = rawCandleStickData[iterateCandleStickData].trim();
			String dataTime = rawCandleStickData[iterateCandleStickData].split(":")[1].trim().replace(",","");
			foramatedTradeData.add(dataTime);
			counter++;
			if(iterateCandleStickData>rawCandleStickData.length) {
				break;
			}
		}
		
		/**
		 * 
		 * Get any random record and prev and next record 
		 * 
		 */
		
		int randomEntry = (int) (Math.random() * (counter - 2)) + 2;	
		String timeStampCandleStickNthEnty = foramatedTradeData.get(randomEntry);
		String timeStampCandleStickNthEnty_Prev = foramatedTradeData.get(randomEntry-1);
		String timeStampCandleStickNthEnty_Next = foramatedTradeData.get(randomEntry+1);
		String radomCandleStickconsecutive = timeStampCandleStickNthEnty_Prev +","+timeStampCandleStickNthEnty+","+timeStampCandleStickNthEnty_Next;
		/**
		 * 
		 * Manipulate any random record and prev and next time duration 
		 * 
		 */
		String value1 = foramatedTradeData.get(randomEntry).replaceAll("\"", "");
		String value2 = foramatedTradeData.get(randomEntry).replaceAll("\"", "");

		String manupulateTimeStampCandleStickNthEnty_Prev = Long.toString((long)(Double.parseDouble(value1) - (candleStickDuration * 60 * 1000)));
		String manupulateTimeStampCandleStickNthEnty = timeStampCandleStickNthEnty;
		String manupulateTimeStampCandleStickNthEnty_Next = Long.toString((long)(Double.parseDouble(value1) - (candleStickDuration * 60 * 1000)));
		String manupulateCandleStickconsecutive = manupulateTimeStampCandleStickNthEnty_Prev +","+manupulateTimeStampCandleStickNthEnty+","+manupulateTimeStampCandleStickNthEnty_Next;
		
		String[] expected = radomCandleStickconsecutive.split(",");
		String[] actual = manupulateCandleStickconsecutive.split(",");
		
		/**
		 * 
		 * Compare manipulated and random selected record have same time stamp
		 * 
		 */
		System.out.println("-------- CandleStick Time Stamp validation ---------");
		randomEntry = randomEntry -1;
		for (int i =0; i<expected.length; i++) {
			
			if(expected[i].equals(actual[i])) {
				System.out.println("Candle Stick Time Stamp of "+randomEntry+" th record "+expected[i]+" is equal to "+actual[i]);
			}else {
				System.out.println("Candle Stick Time Stamp of "+randomEntry+" th record "+expected[i]+" is not equal to "+actual[i]);
			}
			randomEntry = randomEntry +1;
		}
	}
	
}
