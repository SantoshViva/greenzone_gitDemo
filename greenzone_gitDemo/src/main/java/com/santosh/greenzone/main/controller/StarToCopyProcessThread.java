package com.santosh.greenzone.main.controller;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;

@Component
//@Scope("application")
public class StarToCopyProcessThread extends Thread {

	private static final Logger logger = LogManager.getLogger(StarToCopyProcessThread.class);

	@Autowired
	Environment env;

	@Autowired
	private JdbcTemplate jdbcTemplate;

//   public SmsSendThread(Environment env,JdbcTemplate jdbcTemplate) {
//	   this.env=env;
//	   this.jdbcTemplate=jdbcTemplate;
//   } 
//	private DriverManagerDataSource dataSource;
//	@Autowired
//	public void setDataSource(DriverManagerDataSource dataSource) {
//		this.dataSource=dataSource;
//	}

	@Override
	public void run() {
		String finalQuery = "";
		// this.jdbcTemplate = new JdbcTemplate(dataSource);
		String notificationInterval = env.getProperty("NOTIFICATION_SMS_INTERVAL");
		// CHECK NOTIFCATON ENABLE
		int smsHourStart = Integer.valueOf(env.getProperty("SMS_HOUR_START"));
		int smsHourEnd = Integer.valueOf(env.getProperty("SMS_HOUR_END"));
		int hourSleepDuration = Integer.valueOf(env.getProperty("HOUR_CHECK_SLEEP"));
		
		int qrySleepDuration = Integer.valueOf(env.getProperty("QRY_FINISH_SLEEP"));
		logger.info("NOTIFICATION_SMS_ENABLE=" + env.getProperty("NOTIFICATION_SMS_ENABLE")
				+ "|NOTIFICATION_SMS_INTERVAL=" + env.getProperty("NOTIFICATION_SMS_INTERVAL") + "|smsHourStart="
				+ smsHourStart + "|smsHourEnd=" + smsHourEnd + "|hourSleepDuration=" + hourSleepDuration);
		String smsText = "";
		smsText = env.getProperty("NOTIFICATION_SMS_TEXT");
		String senderMsisdn = "";
		String chatGroupId = "-1";
		String blackHours="N";
		if (env.getProperty("NOTIFICATION_SMS_ENABLE").equalsIgnoreCase("Y")) {
			logger.info("Start Notification");

			while (true) {
				Date currDate = new Date();
				int currHours = currDate.getHours();
				String isSmsSend = "N";
				if (currHours < smsHourStart || currHours > smsHourEnd) {
					logger.info("currHours=" + currHours + "|start sleeping ");
					try {
						sleep(hourSleepDuration);
						
						blackHours="Y";
						logger.info("Black Hours=" + currHours + "|end sleeping|blackHours="+blackHours);
						continue;
					} catch (Exception e) {
						logger.error("Exception occured on sleep|e=" + e);
					}

				} else {
					logger.info("No Black Hours");
					
				}
				String aparty,bparty,toneId,apartyRbtFlag,serviceId,productId,toneServiceId,toneProductId,isToneCharge;
				
				try {
					finalQuery = "select id, aparty,bparty,tone_id,aparty_rbt_flag,service_id,product_id,tone_service_id,tone_product_id,is_tone_charge from CRBT_ASYNC_START_TO_COPY_INFO where status='A' order by start_time ;";
					logger.info("query=" + finalQuery);
					
					try {
						List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(finalQuery);

						
						if (queryForList.isEmpty()) {
							logger.info("No record found");
							sleep(5);
						} else {
							logger.info("for loop started");
							for (Map<String, Object> row : queryForList)
							{
								if (row.get("aparty") == null || row.get("aparty").toString().isEmpty()) 
								{
									logger.info("aparty is empty");
									continue;
								}
								else
								{
									aparty=row.get("aparty").toString();
								}

								if (row.get("bparty") == null || row.get("bparty").toString().isEmpty()) 
								{
									logger.info("bparty is empty");
									continue;
								}
								else
								{
									bparty=row.get("bparty").toString();
								}
								if (row.get("tone_id") == null || row.get("tone_id").toString().isEmpty()) 
								{
									logger.info("tone_id is empty");
									continue;
								}
								else
								{
									toneId=row.get("tone_id").toString();
								}
								if (row.get("aparty_rbt_flag") == null || row.get("aparty_rbt_flag").toString().isEmpty()) 
								{
									logger.info("aparty_rbt_flag is empty");
									continue;
								}
								else
								{
									apartyRbtFlag=row.get("aparty_rbt_flag").toString();
								}
								if (row.get("service_id") == null || row.get("service_id").toString().isEmpty()) 
								{
									logger.info("service_id is empty");
									continue;
								}
								else
								{
									serviceId=row.get("service_id").toString();
								}
								if (row.get("product_id") == null || row.get("product_id").toString().isEmpty()) 
								{
									logger.info("product_id is empty");
									continue;
								}
								else
								{
									productId=row.get("product_id").toString();
								}
								if (row.get("tone_service_id") == null || row.get("tone_service_id").toString().isEmpty()) 
								{
									logger.info("tone_service_id is empty");
									continue;
								}
								else
								{
									toneServiceId=row.get("tone_service_id").toString();
								}
								if (row.get("tone_product_id") == null || row.get("tone_product_id").toString().isEmpty()) 
								{
									logger.info("tone_product_id is empty");
									continue;
								}
								else
								{
									toneProductId=row.get("tone_product_id").toString();
								}
								if (row.get("is_tone_charge") == null || row.get("is_tone_charge").toString().isEmpty()) 
								{
									logger.info("is_tone_charge is empty");
									continue;
								}
								else
								{
									isToneCharge=row.get("is_tone_charge").toString();
								}
								
						
								logger.info("Details|aparty="+aparty+"|bparty="+bparty+"|toneId="+toneId+"|apartyRbtFlag="+apartyRbtFlag+"|serviceId="+serviceId+"|productId="+productId+"|toneServiceId="+toneServiceId+"|toneProductId="+toneProductId+"|isToneCharge="+isToneCharge);
								
								
								
								if(apartyRbtFlag.equalsIgnoreCase("Y")) {
									/**Create Unique Transaction Id**/
									UUID transactionId = UUID.randomUUID(); 
									/**Find the current System date time**/
									SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
									Date now = new Date();
									String strDate= sdfDate.format(now);
									/* System.out.print("Date="+strDate); */
									
									/**Start Find the songName **/
									logger.trace("toneId="+toneId);
									String selectQuery = ChatUtils.getQuery(env.getProperty("SQL35_SELECT_SONG_NAME_DETAIL"), toneId);
									String songName="";
									logger.trace("final Query="+selectQuery);
									try {
										List<Map<String, Object>> queryForListSongName = jdbcTemplate.queryForList(selectQuery);
										if(queryForListSongName.isEmpty())
										{
												songName="mySong";
												logger.error("No song Name is available in the database|songName="+songName);
										}
										else 
										{
											for (Map<String, Object> rowSong : queryForListSongName) {
												
											    songName=rowSong.get("songname").toString();
												logger.info("User Status ="+rowSong.get("songName").toString() );
												break;
											}
										}
										
									}catch(Exception e) {
										e.printStackTrace();
									}
									/**End songName**/
									/**HTTP URL Hit for Third Party**/
									String action="UPDATE";
									String toneType="0";
									String toneTypeIdx="1";
									String callingParty="D";
									String jsonCrbtBodyData = "{\r\n" +
											" \"cpid\": \"CRBT\",\r\n"+
							                " \"msisdn\": \"" +aparty +"\",\r\n" +
											" \"tid\": \""+transactionId+"\",\r\n"+
							                " \"action\" :\"" +action +"\",\r\n"+
							                " \"langid\" :\"en\",\r\n"+
							                " \"interfacename\" :\"IVR\",\r\n"+
							                " \"toneid\" :\"" +toneId +"\",\r\n"+
							                " \"timestamp\" :\""+strDate+"\",\r\n"+
							                " \"oldtonetype\" :\""+toneType+"\",\r\n"+
							                " \"oldtonetypeidx\" :\""+toneTypeIdx+"\",\r\n"+
							                " \"tonetype\" :\"" +toneType +"\",\r\n"+
							                " \"tonetypeidx\" :\"" +toneTypeIdx +"\",\r\n"+
							                " \"tonename\" :\"" +songName +"\",\r\n"+
							                " \"callingpartynumber\" :\"" +callingParty +"\",\r\n"+
							                " \"toneserviceid\" :\"" +toneServiceId +"\",\r\n"+
							                " \"toneproductid\" :\""+toneProductId+"\",\r\n"+                                   	                
							               " \"istonecharge\" :\"" +isToneCharge +"\"\r\n"+
							                "}";
									logger.info("jsonCrbtBodyData="+jsonCrbtBodyData);
									String coreEngineToneChangeUrl = env.getProperty("CORE_ENGINE_TONE_CHANGE_URL");
									logger.trace("toneChangeReq|coreEngineToneChangeUrl"+coreEngineToneChangeUrl);
									PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
									String postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineToneChangeUrl, jsonCrbtBodyData, "tone");
									logger.info("aparty="+aparty+"|postClientRes="+postClientRes);
								}else {
									/**HTTP URL Hit for Third Party**/
									
									String coreEngineSubBillingUrl = env.getProperty("CORE_ENGINE_SUBS_BILL_URL");
									logger.trace("subscriptionBillingReq|coreEngine SubBillingUrl="+coreEngineSubBillingUrl);
									if(isToneCharge.equalsIgnoreCase("N"))
									{
										toneServiceId="TONE_RENTAL";
										toneProductId="TONE_LIFETIME";
										
									}
									//For not CRBT Case
									/**Create Unique Transaction Id**/
									UUID transactionId = UUID.randomUUID(); 
									/**Find the current System date time**/
									SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
									Date now = new Date();
									String strDate= sdfDate.format(now);
									String jsonCrbtBodyData = "{\r\n" +
											" \"cpid\": \"CRBT\",\r\n"+
							                " \"msisdn\": \"" +aparty +"\",\r\n" +
											" \"tid\": \"" +transactionId+ "\",\r\n"+
							                " \"action\" :\"SUBSCRIPTION\",\r\n"+
							                " \"serviceid\" :\"SUBS_RENTAL\",\r\n"+
							                " \"productid\" :\""+productId+"\",\r\n"+
							                " \"langid\" :\"en\",\r\n"+
							                " \"interfacename\" :\"IVR\",\r\n"+
							                " \"timestamp\" :\"" +strDate+ "\",\r\n"+
							                " \"issubcharge\" :\"Y\",\r\n"+
							                " \"toneid\" :\"" +toneId+"\",\r\n"+
							                " \"tonetype\" :\"0\",\r\n"+
							                " \"tonetypeidx\" :\"1\",\r\n"+
							                " \"tonename\" :\"" +env.getProperty("DEFAULT_TONE_NAME")+"\",\r\n"+
							                " \"precrbtflag\" :\"\",\r\n"+
							                " \"callingpartynumber\" :\"D\",\r\n"+
							                " \"toneserviceid\" :\"" +toneServiceId +"\",\r\n"+
							                " \"toneproductid\" :\""+toneProductId+"\",\r\n"+                                   	                
							                " \"istonecharge\" :\"" +isToneCharge +"\"\r\n"+
							                "}";
									
									logger.info("jsonCrbtBodyData="+jsonCrbtBodyData);
									logger.trace("CRBT Case: We are not hitting third party URL");
									PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
									String postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineSubBillingUrl, jsonCrbtBodyData, "subscribe");
								
								}
								//end if
								//Set status flag D
								String updateQuery="update CRBT_ASYNC_START_TO_COPY_INFO set status='D' where aparty='" + aparty + "';";
								try
								{
									int updateResult=jdbcTemplate.update(updateQuery);
									if(updateResult<=0)
									{
										logger.info("Fail to update |result="+updateResult);
									}else {
										logger.info("Successful update|result="+updateResult);
									}
								}catch(Exception e) {
									logger.error("Exception occurred="+e);
									
								}
								//end status flag
								
							}
							//end for loop
							logger.info("For loop finished");
						}
						//end else
					} catch (Exception ex) {
						logger.info("Error occurred1121=" + ex);
						ex.printStackTrace();

					}
					blackHours="N";
					logger.info("Finished query result|blackHours="+blackHours);
					
					
				} catch (Exception e) {
					logger.error("Exception occurred|" + e);
				}
				//end try catch
				try {
					logger.info("Go to sleep");
					sleep(qrySleepDuration);
					logger.info("Exit from sleep");
				}catch(Exception sleepEX) {
					logger.error("Exception occurred|sleepEX=" + sleepEX);
				}			
				logger.info("end while loop");
			}
			//end while loop
		} else {
			logger.info("Shutdown notification");
		} // END IF CHECK
	}
}
