package test;

import org.junit.jupiter.api.Test;
import core.BaseClass;
import core.DefintionClass;

import java.io.IOException;


public class TestSuite extends DefintionClass {
	
	BaseClass base = new BaseClass();
	
	/**
	 * Get a Trade response from - https://exchange-docs.crypto.com/#public-get-trades
	 * 
	 * @return response stored in txt file
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testTradeRestRepsonse() throws IOException {
		System.out.println("Test One:");
		GetTradeRestRepsonse();
	}

	/**
	 * Get a Trade response from - https://exchange-docs.crypto.com/#public-get-trades
	 * 
	 * @return response stored in txt file
	 * 
	 * @throws Exception 
	 */
	 @Test
	 public void testCandleStickRestRepsonse() throws IOException {
		System.out.println("Test Two :");
		GetCandleStickRestRepsonse();
	 }
	 
	 /**
	 * Get a Trade response from - https://exchange-docs.crypto.com/#public-get-trades
	 * 
	 * @return response stored in txt file
	 * 
	 * @throws Exception 
	 */
	 @Test
	 public void testCandleStickRestRepsonseConsistency() throws IOException {
		 System.out.println("Test Three:");
		verifyCandleStickRestConsistency();
		
	 }
	 
	 /**
	 * Read a Trade response and Manipulate the CandleStick values and compare with the candleStick response
	 * 
	 * @return Comparison result
	 * 
	 * @throws Exception 
	 */
	 @Test
	 public void testValidateCandleStickValue() throws IOException {
		 System.out.println("Test Four:");
		 String rawTradeData[] =  readTradeRestResponse();
		 String rawCandleStickData[] =  readCandleStickRestResponse();
		 String expectedTradePrice = manipulateTradeDetail(rawTradeData);		
		 String timeStamp = expectedTradePrice.split(",")[0];
		 String actualCandleStickPrice = fetchActualCandleStickPrice(rawCandleStickData,timeStamp);
		 ValidateExpectedTradePriceVsActualCandlePrice(expectedTradePrice, actualCandleStickPrice);
	 }
	 
}
