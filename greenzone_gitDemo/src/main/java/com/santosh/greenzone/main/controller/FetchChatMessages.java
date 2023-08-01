package com.santosh.greenzone.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

import com.santosh.greenzone.model.BlackListDetailsModel;
import com.santosh.greenzone.model.ChatDashboardDetailsModel;
import com.santosh.greenzone.model.ChatMessagesDetails;
import com.santosh.greenzone.model.ResponseDTO;
import com.santosh.greenzone.model.ResponseHeader;
import com.santosh.greenzone.utils.ChatUtils;
/***This is use for GUI*/
@CrossOrigin
@RestController
public class FetchChatMessages {
	
private static final Logger logger = LogManager.getLogger(FetchChatMessages.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;	
	@RequestMapping(value = "/fetchChatMessages", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> operation(@RequestParam("serviceId") String serviceId,@RequestParam("startIndex") String startIndex,@RequestParam("endIndex") String endIndex,
			@RequestParam(required=false) String msisdn,@RequestParam(required =false) String status,@RequestParam(required =false) String chatGroupId,  HttpServletRequest req,HttpServletResponse res) {
		
		if(startIndex==null ||startIndex.toString().isEmpty())
		{
			startIndex="0";
		}
		if(endIndex==null || endIndex.toString().isEmpty())
		{
			endIndex="10";
		}
		if(msisdn==null || msisdn.isEmpty())
		{
			msisdn="Y";
		}
		if(status == null || status.isEmpty())
		{
			status="all";
		}
		if(chatGroupId == null || chatGroupId.isEmpty())
		{
			chatGroupId="all";
		}
		logger.info("fetchChatMessages|serviceId="+serviceId+"|startIndex="+startIndex+"|endIndex="+endIndex+"|status="+status);
		String selectChatMessagesQuery="";
		if(msisdn.equalsIgnoreCase("Y"))
		{
			if(chatGroupId.equalsIgnoreCase("all"))
			{	
				if(status.equalsIgnoreCase("all"))
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_ALL_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}else
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_ALL_STATUS_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}
			}else
			{
				//group wise data
				if(status.equalsIgnoreCase("all"))
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_ALL_GROUP_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}else
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_ALL_GROUP_STATUS_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}
			}
			
		}else
		{
			if(chatGroupId.equalsIgnoreCase("all"))
			{
				if(status.equalsIgnoreCase("all"))
				{	
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_MSISDN_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}else
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_MSISDN_STATUS_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}
			}else
			{
				//group wise data
				if(status.equalsIgnoreCase("all"))
				{	
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_MSISDN_GROUP_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}else
				{
					selectChatMessagesQuery=ChatUtils.getFetchMessageQuery(env.getProperty("SQL48_SELECT_MSISDN_GROUP_STATUS_CHAT_MESSAGES"),startIndex,endIndex,msisdn,status,chatGroupId);
				}
			}
		}
		List<ChatMessagesDetails> chatMessageDetails = new ArrayList<ChatMessagesDetails>();
		ResponseDTO<List<ChatMessagesDetails>> response = new ResponseDTO<>();
		logger.info("query="+selectChatMessagesQuery);
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectChatMessagesQuery);
			if(queryForList.isEmpty()) {
				addHeader.setCode(1);
				addHeader.setMessage("No record found");
			}else {
				addHeader.setCode(0);
				addHeader.setMessage("Success");
				for(Map<String, Object> row : queryForList)
				{
		
					ChatMessagesDetails chatMessageDetail = new ChatMessagesDetails();
					if(row.get("id")!=null)
					{
						chatMessageDetail.setId(row.get("id").toString());
					}
					if(row.get("subscriber_id")!=null)
					{
						chatMessageDetail.setMsisdn(row.get("subscriber_id").toString());
					}
					if(row.get("chat_group_id")!=null)
					{
						chatMessageDetail.setChatGroupId(row.get("chat_group_id").toString());
					}
					if(row.get("status")!=null)
					{
						chatMessageDetail.setStatus(row.get("status").toString());
					}
					if(row.get("chat_dislike")!=null)
					{
						chatMessageDetail.setChatDislikeCount(row.get("chat_dislike").toString());
					}
					else
					{
						chatMessageDetail.setChatDislikeCount("0");
					}
					if(row.get("chat_like")!=null)
					{
						chatMessageDetail.setChatLikeCount(row.get("chat_like").toString());
					}
					else
					{
						chatMessageDetail.setChatLikeCount("0");
					}
					if(row.get("send_date")!=null)
					{
						chatMessageDetail.setRecordingDate(row.get("send_date").toString());
					}
					if(row.get("recording_path")!=null)
					{
						String path=row.get("recording_path").toString();
						//path=path.replace("http://127.0.0.1/","/var/www/html/");
						path=path.replace(env.getProperty("DB_CHAT_REPLACE_PATH"), env.getProperty("PHYSICAL_CHAT_PATH"));
						chatMessageDetail.setRecordingPath(path);
					}
					chatMessageDetails.add(chatMessageDetail);
				}
				
			}
			
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
				
		response.setHeader(addHeader);
		response.setBody(chatMessageDetails);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
