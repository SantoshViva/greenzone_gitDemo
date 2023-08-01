package com.santosh.greenzone.main.controller;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;







@CrossOrigin
@RestController
public class SendSmsController {

	private static final Logger logger = LogManager.getLogger(SendSmsController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/sendSmsReq",method = RequestMethod.GET)
	@ResponseBody
	public String eventBaseBillingController(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("serviceId") String serviceId,@RequestParam("productId") String productId,@RequestParam("smsId") String smsId,HttpServletRequest req,
			HttpServletResponse res) {
		
			logger.info("sendSmsReq|aparty="+aparty+"|bparty="+bparty+"|serviceId="+serviceId+"|productId="+productId+"|smsId="+smsId);
			
			String responseString="RESPONSE.result='200 Ok'";						
			/**Fetch SMS from Database**/
			String smsText="Default";
			
			String selectQuery= env.getProperty("SQL44_SELECT_SMS_TEXT");
			selectQuery=selectQuery.replace("{smsId}", smsId);
			logger.info("final selectQuery Query="+selectQuery);
			/**send SMS to User**/
				try {
					List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
					if(queryForList.isEmpty())
					{
						logger.info("No Record Found in SQL|aparty="+aparty);
						return responseString;
					}else {
						for (Map<String, Object> row : queryForList)
						{
							smsText=row.get("smstext").toString();
							break;
						}
						logger.info("smsText="+smsText);
					}
					
				}catch(Exception e) {
					logger.error("SQL Exception" + e +"Query="+selectQuery);
					logger.error("No Row Found");
					return responseString;
				}
				/**Find date & time*/
			
			
				/**Hit RestFul Api*/
				String smsUrl = env.getProperty("SMS_SEND_URL");
				smsText=smsText.replace(" ", "+");
				smsUrl = smsUrl +"&to=%2B252"+aparty+"&text="+smsText ;
				logger.info("smsUrl="+smsUrl);
				
				try {
					URI smsUri = new URI(smsUrl);
					
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> smsUrlResult = restTemplate.getForEntity(smsUri, String.class);
					HttpStatus statusCode= smsUrlResult.getStatusCode();
					logger.info("reuslt="+smsUrlResult);
					logger.info("statusCode="+statusCode+"|"+smsUrlResult.getStatusCodeValue());
					if(smsUrlResult.getStatusCodeValue()==202||smsUrlResult.getStatusCodeValue()==200) {
						logger.info("send SMS successfully|aparty="+aparty+"|smsText="+smsText);
					}
				}catch(Exception e) {
					logger.error("Exception="+e);
				}
			/****/
			
			
						
			return responseString;
		
	}
	
	
}
