package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by Anusuya on 10/01/2021.
 * 
 *  This class file contains common functions and reusability
 *  
 *  
 */

public class BaseClass {
	
	/**
	 * Get Trade RestEndPointUrl from Configuration properties
	 * 
	 * @param read from Configuration properties
	 *
	 * @return REST end point URL 
	 *
	 * @throws Exception 
	 * 
	 */
	public String getTradeRestEndpoinUrl() throws IOException {
		
		String restEndPointUrl = getConfigPropertyValue("RestEndPointUrl");
		String instrument = getConfigPropertyValue("Instrument");
		String URL = "https://"+restEndPointUrl+"/v2/public/get-trades?instrument_name="+instrument;
		return URL;
	}
	
	/**
	 * Get Candlestick RestEndPointUrl from Configuration properties
	 * 
	 * @param read from Configuration properties
	 * 
	 * @return REST end point URL 
	 *
	 * @throws Exception 
	 * 
	 */
	public String getCandleStickRestEndpoinUrl() throws IOException {
		
		String restEndPointUrl = getConfigPropertyValue("RestEndPointUrl");
		String instrument = getConfigPropertyValue("Instrument");
		String duration = getConfigPropertyValue("Duration");
		String URL = "https://"+restEndPointUrl+"/v2/public/get-candlestick?instrument_name="+instrument+"&timeframe="+duration;
		return URL;
	}
	
	/**
	 * Get a value for a Key from Configuration properties
	 * 
	 * @param Key of the Configuration Properties
	 * 
	 * @return value of the Configuration Properties
	 * 
	 * @throws Exception 
	 */
	public String getConfigPropertyValue(String object) throws IOException{
		Properties properties = new Properties();		
		FileInputStream input= new FileInputStream (".\\Data\\configuration.properties");
		properties.load(input);	
		String value = properties.getProperty(object);
		return value;
	}

	

}
