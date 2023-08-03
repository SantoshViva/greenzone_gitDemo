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
public class ChatGroupManage {
	private static final Logger logger = LogManager.getLogger(ChatGroupManage.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value = "/ivrChatGroupUpdate",method = RequestMethod.GET)
	@ResponseBody
	public String ivrChatGroupUpdate(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("chatGroupId") String chatGroupId,@RequestParam(required=false) String operation,HttpServletRequest req,
			HttpServletResponse res) {
		
		String responseString=new String();
		String dbError="N";
		String queryType="N";
		String insertQueryFleg="N";
		
		String localOperation="N";
		String finalQuery="";
		if(operation == null)
		{
			localOperation="N";
		}
		else
		{
			localOperation=operation;
		}
		logger.info("ivrChatGroupUpdate|aparty=" + aparty+"|bparty="+bparty+"|chatGroupId="+chatGroupId+"|operation="+operation+"|localOperation="+localOperation);
		
		if(localOperation.equalsIgnoreCase("delete"))
		{
			finalQuery=ChatUtils.getChatGroupInsertQuery(env.getProperty("SQL48_DELETE_CHAT_GROUP_ID"),aparty,chatGroupId);
			logger.trace("finalQuery="+finalQuery);
			try
			{
				int updateResult=jdbcTemplate.update(finalQuery);
				if(updateResult<=0)
				{
					logger.info("Fail to delete chat group |result="+updateResult);
				}else {
					logger.info("Successful delete chat group|result="+updateResult);
					responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
									
					return responseString;
				}
			}catch(Exception e) {
				logger.error("Exception occurred="+e);
				dbError="Y";
				responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
				
				return responseString;
			}
		}
		String selectQuery = ChatUtils.getQueryReplaceOneString(env.getProperty("SQL48_SELECT_CHAT_GROUP_ID"),aparty);
		logger.trace("selectQuery="+selectQuery);
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			
			if(queryForList.isEmpty())
			{
				/**No record found. Error handling here*/
				queryType="insert";
				
				
				if(localOperation.equalsIgnoreCase("select"))
				{		
					
					responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
					responseString= responseString.concat("Res.chatGroupId='0';");
					logger.info("Res="+responseString);
					return responseString;	
				}
				logger.info("ivrChatGroupUpdate|aparty="+aparty+"|no group available|go for insertion");
			}
			else
			{
				for (Map<String, Object> row : queryForList) {
					if(localOperation.equalsIgnoreCase("select") && (row.get("chat_group_id")==null||row.get("chat_group_id").toString().isEmpty()))
					{
						logger.info("ivrChatGroupUpdate|aparty="+aparty+"|something is wrong in the databse");
						responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
						responseString= responseString.concat("Res.chatGroupId='0';");
						logger.info("Res="+responseString);
						return responseString;
					}
					else
					{
						String currentGroupId=row.get("chat_group_id").toString();
						
						if(localOperation.equalsIgnoreCase("select"))
						{		
							responseString=responseString.concat("Res.dbError=\'"+dbError+"\';");
							responseString= responseString.concat("Res.chatGroupId=\'"+row.get("chat_group_id").toString()+"\';");
							logger.info("Res="+responseString);
							return responseString;	
						}	
						logger.info("ivrChatGroupUpdate|aparty="+aparty+"|already group available|currentGroupId="+currentGroupId+"|updatedChatGroupId="+chatGroupId);
						queryType="update";
					}	
				}
			}
		}catch(Exception e)
		{
			logger.error("SQL Exception" + e +"Query="+selectQuery);
			logger.error("No Row Found");
			
			dbError="Y";
			e.printStackTrace();
			if(localOperation.equalsIgnoreCase("select"))
			{
				responseString=responseString.concat("Res.dbError=\'"+dbError+"\';");
				responseString= responseString.concat("Res.chatGroupId=\'-1\'");
				return responseString;	
			}
		}
		
		
		if(queryType.equalsIgnoreCase("insert") || localOperation.equalsIgnoreCase("insert"))
		{
			finalQuery=ChatUtils.getChatGroupInsertQuery(env.getProperty("SQL48_INSERT_CHAT_GROUP_ID"),aparty,chatGroupId);
			logger.trace("finalQuery="+finalQuery);
			try {
				int insertQueryResult= jdbcTemplate.update(finalQuery);
				if(insertQueryResult <= 0)
				{
					logger.error("Failed to insert|query="+insertQueryResult);
					insertQueryFleg="false";
				}else {
					
					logger.info("Successfully to insert |resultChangesRow="+insertQueryResult);
					insertQueryFleg="true";
				}
				logger.info("insert new tone successfully");
			}catch(Exception e)
			{
				logger.error("Exception occurred insert case|SQL exception="+e);
				//e.printStackTrace();
				insertQueryFleg="false";
				dbError="Y";
				logger.info("insert new tone faild");
			}
		}
		if(queryType.equalsIgnoreCase("update")||localOperation.equalsIgnoreCase("update"))
		{
			finalQuery=ChatUtils.getChatGroupInsertQuery(env.getProperty("SQL48_UPDATE_CHAT_GROUP_ID"),aparty,chatGroupId);
			logger.info("finalQuery="+finalQuery);
			try
			{
				int updateResult=jdbcTemplate.update(finalQuery);
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
		}
		responseString= responseString.concat("Res.dbError=\'"+dbError+"\';");
		
		return responseString;
	}
	
}
