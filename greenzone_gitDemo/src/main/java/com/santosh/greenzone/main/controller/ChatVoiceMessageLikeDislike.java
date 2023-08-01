package com.santosh.greenzone.main.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class ChatVoiceMessageLikeDislike {

	private static final Logger logger = LogManager.getLogger(ChatVoiceMessageLikeDislike.class);
	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/likeDislikeVoiceChatMessage", method = RequestMethod.GET)
	@ResponseBody
	public String likeDislikeVoiceChatMessage(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("messageId") String messageId,@RequestParam(required=false) String chatGroupId,@RequestParam(required=false) String chatMsisdn,@RequestParam("likeDisLikeFlag") String likeDisLikeFlag,HttpServletRequest req,
			HttpServletResponse res)
	{
		String dbError="N";
		String isSmsSend="N";
		String responseString=new String();
		String smsText="";
		logger.info("likeDislikeVoiceChatMessage|aparty="+aparty+"|bparty="+bparty+"|messageId="+messageId+"|chatGroupId="+chatGroupId+"|chatMsisdn="+chatMsisdn+"|likeDisLikeFlag="+likeDisLikeFlag);
		
		String query="";
		if(likeDisLikeFlag.equalsIgnoreCase("1"))
		{
			query=ChatUtils.getSmsText(env.getProperty("SQL48_UPDATE_CHAT_MESSAGE_LIKE"),messageId);
			smsText=env.getProperty("LIKE_SMS_TEXT");
		}
		else
		{
			query=ChatUtils.getSmsText(env.getProperty("SQL48_UPDATE_CHAT_MESSAGE_DISLIKE"),messageId);
			smsText=env.getProperty("DISLIKE_SMS_TEXT");
			
		}
		//logger.info("query="+query);
		try
		{
			int updateResult=jdbcTemplate.update(query);
			if(updateResult<=0)
			{
				logger.info("Fail to update |result="+updateResult);
			}else {
				logger.info("Successful update|result="+updateResult);
			}
		}catch(Exception e) {
			logger.error("Exception occurred="+e);
			dbError="Y";
		}
		/***Check like dislike count*/
		
			query=ChatUtils.getSmsText(env.getProperty("SQL48_SELECT_CHAT_MESSAGE_DISLIKE"),messageId); 
			
			String disLikeCount="";
			String disLikeSubscriberId="";
			String likeCount="";
			try {
				logger.info("query=" + query);
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
				if (queryForList.isEmpty()) {
					logger.info("No record found");
				} else {
					logger.info("for loop");
					for (Map<String, Object> row : queryForList) {
						if (row.get("chat_dislike") == null || row.get("chat_dislike").toString().isEmpty()) {
							logger.info("null record found");
						} else {
							likeCount = row.get("chat_like").toString();
							disLikeCount = row.get("chat_dislike").toString();
							disLikeSubscriberId = row.get("subscriber_id").toString();
							logger.info("likeCount=" + likeCount + "|disLike Count =" + disLikeCount
									+ "|disLikeSubscriberId=" + disLikeSubscriberId);
						}

					}
				}
				/** insert black list and send Blacklist SMS */
				logger.info("conf like=" + env.getProperty("LIKE_SMS_COUNT") + "|conf dislike="
						+ env.getProperty("DISLIKE_BLOCK_COUNT"));
				//for dislike case
				if (likeDisLikeFlag.equalsIgnoreCase("0")) {
					logger.info("Dislike message|a=" + Integer.parseInt(disLikeCount) + "|b="
							+ Integer.parseInt(env.getProperty("DISLIKE_BLOCK_COUNT")));
					if (Integer.parseInt(disLikeCount) >= Integer.parseInt(env.getProperty("DISLIKE_BLOCK_COUNT"))) {
						logger.info("if case");
						String insertBlackListFlag ="N";
						// Blacklist the number 
						String insertBlackQuery = ChatUtils.getBlackListChatQuery(
								env.getProperty("SQL48_INSERT_BACKLIST_NUMBER"), disLikeSubscriberId, "SYSTEM");
						logger.info("insertBlackQuery=" + insertBlackQuery);
						String chatMessageDisactive = ChatUtils
								.getSmsText(env.getProperty("SQL48_UPDATE_CHAT_MESSAGE_INACTIVE"), messageId);
						
						try {
							logger.info("insertBlackQuery=" + insertBlackQuery);
							int insertQueryResult = jdbcTemplate.update(insertBlackQuery);
							if (insertQueryResult <= 0) {
								logger.error("Failed to insert|query=" + insertQueryResult);
							} else {
								insertBlackListFlag="Y";
								logger.info("Successfully to data insert in blacklist by system |resultChangesRow="
										+ insertQueryResult);
							}
							if(insertBlackListFlag.equalsIgnoreCase("Y"))
							{
								String updateChatProfile = ChatUtils.getBlackListChatQuery(env.getProperty("SQL48_CHAT_PROFILE_UPDATE_BLACKLIST"), aparty,"SYSTEM");
								int updateResult= jdbcTemplate.update(updateChatProfile);
								if(updateResult<=0)
								{
									logger.info("Fail to update chat profile for blacklist |result="+updateResult);
								}else
								{
									logger.info("Successful update chat profile for blacklist|result="+updateResult);
								}
							}

						} catch (Exception ex) {
							logger.error("Exception occurred 1|Query=" + insertBlackQuery + "|SQL exception=" + ex);
							ex.printStackTrace();

						}
						try {
							//Disactive the chat message 
							logger.info("chatMessageDisactive=" + chatMessageDisactive);
							int updateResult = jdbcTemplate.update(chatMessageDisactive);
							if (updateResult <= 0) {
								logger.info("Fail to update |result=" + updateResult);
							} else {
								logger.info("Successful update|result=" + updateResult);
								isSmsSend = "Y";
							}
						}catch (Exception uex) {
							logger.error("Exception occurred 2|Query=" + insertBlackQuery + "|SQL exception=" + uex);
							uex.printStackTrace();

						}
					}
					logger.info("disLikeCount=" + disLikeCount + "|configDisLikeCount="
							+ env.getProperty("DISLIKE_BLOCK_COUNT"));
				} else {
					logger.info("like the message|like count="+likeCount+"|config Like Count="+env.getProperty("LIKE_SMS_COUNT"));
					int modValue= Integer.parseInt(likeCount)%Integer.parseInt(env.getProperty("LIKE_SMS_COUNT"));
					if(modValue == 0)
					{
						logger.info("modValue="+modValue);
						isSmsSend = "Y";
						smsText=smsText.replace("{likeCount}", likeCount);
						logger.info("final smsText="+smsText);
					}
				}
				if(isSmsSend.equalsIgnoreCase("Y"))
				{
					logger.info("send SMS|text="+smsText);
					String smsUrl = env.getProperty("CHAT_SMS_SEND_URL");
					smsText=smsText.replace(" ", "+");
					smsUrl = smsUrl +"&to=%2B252"+disLikeSubscriberId+"&text="+smsText ;
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
					}catch(Exception smse) {
						logger.error("Exception="+smse);
					}
				}

			}catch(Exception e)
			{
				logger.info("Error occurred");
			}
		
		responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
		
		return responseString;
	}
}
