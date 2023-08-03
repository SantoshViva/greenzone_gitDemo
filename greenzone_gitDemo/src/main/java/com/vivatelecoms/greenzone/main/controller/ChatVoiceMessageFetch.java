package com.vivatelecoms.greenzone.main.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.utils.ChatUtils;



@CrossOrigin
@RestController
public class ChatVoiceMessageFetch {
	
	private static final Logger logger = LogManager.getLogger(ChatVoiceMessageFetch.class);
	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/fetchVoiceChatMessage", method = RequestMethod.GET)
	@ResponseBody
	public String fetchVoiceChatMessage(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("chatGroupId") String chatGroupId,@RequestParam("messageCount") String messageCount,@RequestParam("messageId") String messageId,HttpServletRequest req,
			HttpServletResponse res)
	{
		if(messageId == null || messageId.toString().isEmpty())
		{
			messageId="0";
		}
		logger.info("fetchChatMessage|aparty="+aparty+"|bparty="+bparty+"|chatGroupId="+chatGroupId+"|messageCount="+messageCount+"|messageId="+messageId);
		
		String selectQuery= ChatUtils.getQueryChatMessage(env.getProperty("SQL48_SELECT_CHAT_MESSAGE"),chatGroupId,messageCount,messageId);
		logger.info("final Query="+selectQuery);
		String responseString = new String();
		String dbError ="N";
		int counter=0;
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				
				responseString = responseString.concat("Res.messageCount=\'"+"0"+"\';");
											
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					Integer intCounterLocal = new Integer(counter);
					
					if(row.get("subscriber_id")==null||row.get("recording_path")== null||row.get("recording_path").toString().isEmpty()||row.get("subscriber_id").toString().isEmpty())
					{
						logger.error("subscriber_id or recording_path is null in database|aparty="+aparty);
						continue;
					}else
					{	
						logger.info("id="+Integer.parseInt(row.get("id").toString())+"|msisdn="+row.get("subscriber_id")+"|recordingPath="+row.get("recording_path"));
						responseString = responseString.concat("Res.voiceMessageId["+intCounterLocal.toString()+"]"+"=\'"+Integer.parseInt(row.get("id").toString())+"\';");
						responseString = responseString.concat("Res.msisdn["+intCounterLocal.toString()+"]"+"=\'"+row.get("subscriber_id")+"\';");
						responseString = responseString.concat("Res.recordingPath["+intCounterLocal.toString()+"]"+"=\'"+row.get("recording_path")+"\';");
						
					}
					counter++;				
				}
				Integer intCounter = new Integer(counter);
				responseString = responseString.concat("Res.messageCount=\'"+intCounter.toString()+"\';");
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+selectQuery);
			logger.error("No Row Found");
			
			responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.messageCount=\'"+"0"+"\';");
			dbError="Y";
			e.printStackTrace();
		}
		responseString = responseString.concat("Res.dbError=\'"+dbError+"\';");
		return responseString;
	}
	
}
