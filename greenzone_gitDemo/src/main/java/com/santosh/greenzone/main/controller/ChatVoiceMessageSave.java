package com.santosh.greenzone.main.controller;

import com.santosh.greenzone.utils.ChatUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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


@CrossOrigin
@RestController
public class ChatVoiceMessageSave {

	private static final Logger logger = LogManager.getLogger(ChatVoiceMessageSave.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/saveVoiceChatMessage",method = RequestMethod.GET)
	@ResponseBody
	public String saveVoiceChatMessage(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam(required =false) String messageId,@RequestParam("chatGroupId") String chatGroupId,@RequestParam("recordingFilePath") String recordingFilePath,@RequestParam("chatDuration") String chatDuration ,@RequestParam(required =false) String status,HttpServletRequest req,
			HttpServletResponse res)
	{
		logger.info("saveVoiceChatMessage|aparty="+aparty+"|bparty="+bparty+"|messageId="+messageId+"|chatGroupId="+chatGroupId+"|chatDuration="+chatDuration+"|recordingFilePath="+recordingFilePath+"|status="+status);
		
		/**Check recording file path*/
		String insertStatus="N";
		String newRecordingFilePath="";
		String responseString = new String();
		String srcFilePath=recordingFilePath;
		srcFilePath=srcFilePath.replace(env.getProperty("TS_RECORDING_HTTP_PATH"), env.getProperty("TS_RECORDING_BASE_PATH"));
		
		Date currDate = new Date();
		String basePath=env.getProperty("RECORDING_BASE_PATH");
		DateFormat currYearFormat = new SimpleDateFormat("yy");
		String currYear = currYearFormat.format(currDate);
		DateFormat currMonthFormat = new SimpleDateFormat("MM");
		String currMonth= currMonthFormat.format(currDate);
		DateFormat currDateFormat = new SimpleDateFormat("dd");
		String todayDate= currDateFormat.format(currDate);
		DateFormat currDateTimeFormat= new SimpleDateFormat("ddHHmmss");
		String currDateTime = currDateTimeFormat.format(currDate);
		logger.info("currYear="+currYear+"|currMonth="+currMonth+"|currDate="+currDate+"|currDateTime="+currDateTime);
		String recordingFileName=aparty.substring(aparty.length()-4)+currYear+currMonth+currDateTime+".wav";
		logger.info("aa="+aparty.substring(aparty.length()-4)+"|vm="+aparty.substring(aparty.length()-4)+"|dd="+(Integer.parseInt(todayDate.toString())/10)+"|recordingFileName="+recordingFileName);
		String destFilePath= basePath+"/"+currYear+"/"+currMonth+"/"+(Integer.parseInt(todayDate.toString())/10)+"/"+recordingFileName;
		String command = "cp " + srcFilePath +" " + destFilePath;
		logger.info("srcFilePath="+srcFilePath+"|destFilePath="+destFilePath+"|command="+command);
		newRecordingFilePath= destFilePath;
		newRecordingFilePath=newRecordingFilePath.replace(basePath, env.getProperty("RECORDING_HTTP_PATH"));
		logger.info("newRecordingFilePath="+newRecordingFilePath);
		String returnLinuxCommandResult="-1";
		try {
			returnLinuxCommandResult=ChatUtils.runLinuxCommand(command);
			logger.info("command="+command+"|result="+returnLinuxCommandResult);
		}catch(Exception e) {
			logger.error("Error to execute linux command|Not Successfully copy recording file|Exception="+e);
		}
		
		/**End copy recording in local path*/
		String messageStatus="A";
		if(status == null) 
		{
			messageStatus="A";
		}else if(status.isEmpty())
		{
			messageStatus="A";
		}else {
			messageStatus = status;
		}
		/**chat message active on the basis of some rule**/
		String subscriptionDuration = env.getProperty("SUBSCRIPTION_DURATION");
		String blackListDuration = env.getProperty("BLACKLIST_DURATION");
		String checkChatProfile = ChatUtils.getCheckChatProfileQuery(env.getProperty("SQL48_CHECK_CHAT_PROFILE"), aparty,subscriptionDuration,blackListDuration);
		logger.info("checkChatProfile=" + checkChatProfile);
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(checkChatProfile);

			if (queryForList.isEmpty()) {
				logger.info("Number is not follow  chat subscriber profile logic|aparty=" + aparty+"|subscriptionDuration="+subscriptionDuration+"|blacklistDuration="+blackListDuration);
				} else {
				for (Map<String, Object> row : queryForList) {

					if (row.get("subscriber_id") == null || row.get("subscriber_id").toString().isEmpty()) {
						logger.info("Something is wrong in database|aparty=" + aparty);
					} else {
						logger.info("Number is follow chat profile logic so that this message will be active|aparty=" + aparty+"|subscriptionDuration="+subscriptionDuration+"|blacklistDuration="+blackListDuration);
						messageStatus="A";
					}

				}
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e + "Query=" + checkChatProfile);
			logger.error("No Row Found");
			e.printStackTrace();
		}
		
		/****/
		String dbError ="N";
		String insertChatMessageQuery=ChatUtils.insertChatMessageQuery(env.getProperty("SQL48_INSERT_CHAT_MESSAGE"),aparty,chatGroupId,messageStatus,chatDuration,newRecordingFilePath);
		logger.info("Query="+insertChatMessageQuery);
		try {
			int insertQueryResult= jdbcTemplate.update(insertChatMessageQuery);
			if(insertQueryResult <= 0)
			{
				logger.error("Failed to insert|query="+insertQueryResult);
			}else {
				insertStatus="Y";
				logger.info("Successfully to data insert in  chat message |resultChangesRow="+insertQueryResult);
			}
			
		}catch(Exception e) {
			logger.error("Exception occurred|Query="+insertChatMessageQuery+"|SQL exception="+e);
			e.printStackTrace();
			dbError="Y";
		}
		if(insertStatus.equalsIgnoreCase("Y"))
		{
			String updateQuery=ChatUtils.insertChatMessageQuery(env.getProperty("SQL48_UPDATE_MESSAGE_STATUS"),aparty,chatGroupId,messageStatus,chatDuration,newRecordingFilePath);
			try {
				int updateResult=jdbcTemplate.update(updateQuery);
				if(updateResult<=0)
				{
					logger.info("Fail to update |result="+updateResult);
				}else {
					logger.info("Successful update|result="+updateResult);
				}
			}catch(Exception e) {
				logger.error("Exception occurred|Query="+insertChatMessageQuery+"|SQL exception="+e);
				e.printStackTrace();
				
			}
		}
		responseString = responseString.concat("Res.dbError=\'"+dbError+"\';");
		return responseString;
	}
	
}
