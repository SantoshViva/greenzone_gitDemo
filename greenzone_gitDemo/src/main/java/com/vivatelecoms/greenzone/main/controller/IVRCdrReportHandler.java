package com.vivatelecoms.greenzone.main.controller;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.model.QuizGUICallback;
import com.vivatelecoms.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.vivatelecoms.greenzone.services.impl.PostClientServiceImpl;
import com.vivatelecoms.greenzone.utils.ChatUtils;



@CrossOrigin
@RestController
public class IVRCdrReportHandler {

	private static final Logger logger = LogManager.getLogger(IVRCdrReportHandler.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/ivrCdrReq",method = RequestMethod.GET)
	@ResponseBody
	public String ivrCdrReportHandler(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("reason") String reason,@RequestParam("duration") String duration, @RequestParam("lastNode") String lastNode,@RequestParam("callStartTime") String callStartTime,@RequestParam("callEndTime") String callEndTime,@RequestParam(required = false) String answerKey,@RequestParam(required = false) String quizId,@RequestParam(required = false) String quizStatus,@RequestParam(required = false) String quizCount,@RequestParam(required = false) String quizCat,@RequestParam(required = false) String quizScore,@RequestParam(required = false) String quizTitle,@RequestParam(required = false) String callBack,@RequestParam(required = false) String serviceId,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("ivrCdrReq|aparty=" + aparty+"|bparty="+bparty+"|reason="+reason+"|duration="+duration+"|lastNode="+lastNode+"|callStartTime="+callStartTime+"|callEndTime="+callEndTime+"|quizStatus="+quizStatus+"|quizId="+quizId+"|answer="+answerKey+"|quizCount="+quizCount+"|quizCat="+quizCat+"|quizScore="+quizScore+"|quizTitle="+quizTitle+"|callBack="+callBack+"|serviceId="+serviceId);
		//logger.info("Query=" + env.getProperty("SQL34_INSERT_TP_CDR"));
		
		
		
		// Replace Table Index & bparty 
		String insertQuery = ChatUtils.getTonePlayerCDRQuery(env.getProperty("SQL43_INSERT_IVR_CDR"),aparty,bparty,reason,duration,lastNode,callStartTime,callEndTime);
		
		logger.trace("final SQL Query="+insertQuery);
		
		try {
				int insertQueryResult= jdbcTemplate.update(insertQuery);
				if(insertQueryResult <= 0)
				{
					logger.error("Failed to insert|query="+insertQuery);
				}else {
					logger.info("Successfully to insert|SQL43_INSERT_IVR_CDR|resultChangesRow="+insertQueryResult);
				}
		} catch (Exception e) {
			logger.info("SQL Exception" + e + "Query=" + insertQuery);
			logger.info("No Row Insert into  SQL43_INSERT_IVR_CDR");
			e.printStackTrace();
		}
		if(serviceId!=null && serviceId.equalsIgnoreCase("chat"))
		{
			String updateQuery=ChatUtils.getChatGroupInsertQuery(env.getProperty("SQL48_UPDATE_CHAT_VISIT"),aparty,aparty);
			logger.info("updateQuery="+updateQuery);
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
		}
		
		if(quizStatus.equalsIgnoreCase("Y")||quizStatus.equalsIgnoreCase("N"))
		{
			if(quizTitle==null || quizTitle.isEmpty())
			{
				quizTitle="default";
			}
			String[] quizIdArray=quizId.split(",");
			String[] answerKeyArray=answerKey.split(",");
			String baseQuery=ChatUtils.getQuizCDRQuery(env.getProperty("SQL47_QUIZ_IVR_CDR"),aparty,bparty,callStartTime,callEndTime,duration,quizStatus,quizCat,quizScore,quizTitle);
			logger.info("baseQuery="+baseQuery);
			String quizCol="quizId";
			String answerCol="answerKey";
			int count=1;
			for(String value:quizIdArray) {
				if(count==6)
						break;
				String paramater="{"+quizCol+String.valueOf(count)+"}";
				//logger.info("paramater="+paramater+"|count="+count);
				baseQuery=ChatUtils.getDynamicQuery(baseQuery,paramater,value);
				//logger.info("paramater="+paramater+"|count="+count+"|baseQuery="+baseQuery);
				count++;
				
			}
			count=1;
			for(String value:answerKeyArray) {
				if(count==6)
						break;
				String paramater="{"+answerCol+String.valueOf(count)+"}";
				logger.info("paramater="+paramater+"|count="+count);
				baseQuery=ChatUtils.getDynamicQuery(baseQuery,paramater,value);
				logger.info("paramater="+paramater+"|count="+count+"|baseQuery="+baseQuery);
				count++;
				
			}
			try {
					int insertQueryResult= jdbcTemplate.update(baseQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert|query="+baseQuery);
					}else {
						logger.info("Successfully to insert|SQL43_INSERT_IVR_CDR|resultChangesRow="+insertQueryResult);
					}
			} catch (Exception e) {
						logger.info("SQL Exception" + e + "Query=" + baseQuery);
						logger.info("No Row Insert into  SQL43_INSERT_IVR_CDR");
						e.printStackTrace();
			}
			
			
			/**Third party call back URL Hit**/
			QuizGUICallback quizGuiCallBack = new QuizGUICallback();
			quizGuiCallBack.setCdrcalleridnumber(aparty);
			if(quizStatus.equalsIgnoreCase("Y"))
				quizGuiCallBack.setCdrcallstatus("ANSWERED");
			else
				quizGuiCallBack.setCdrcallstatus("NOT FULL");
			quizGuiCallBack.setCdrduration(duration);
			String encodedStartTime = callStartTime;
			encodedStartTime=encodedStartTime.replace(" ", "%20");
			encodedStartTime=encodedStartTime.replace(":", "%3A");
			quizGuiCallBack.setCdrstarttime(encodedStartTime);
			String encodedEndTime=callEndTime;
			encodedEndTime=encodedEndTime.replace(" ", "%20");
			encodedEndTime=encodedEndTime.replace(":", "%3A");
			quizGuiCallBack.setCdrendtime(encodedEndTime);
			quizGuiCallBack.setCdrid(quizCat);
			quizGuiCallBack.setQuiztitle(quizTitle);
			quizGuiCallBack.setQuiztotalscore(quizScore);
			if(callBack == null || callBack.isEmpty()|| callBack.equalsIgnoreCase("N"))
			{
				logger.info("No GUI Callback Hit");
			}
			else
			{
				String guizGuiCallBackURL = env.getProperty("QUIZ_GUI_CALLBACK_URL");
				String postClientRes="";
				logger.info("quizGuiCallBack="+quizGuiCallBack);
				String jsonBodyQuizData = "{\r\n" +
						" \"cdrid\": \"" +quizCat +"\",\r\n" +
						" \"cdrstarttime\" :\""+encodedStartTime+"\"\r\n"+
						" \"cdrendtime\" :\""+encodedEndTime+"\"\r\n"+
						" \"quiztitle\" :\""+quizTitle+"\"\r\n"+
						" \"quiztotalscore\" :\""+quizScore+"\"\r\n"+
						" \"cdrcalleridnumber\" :\""+aparty+"\"\r\n"+
						" \"cdrduration\" :\""+duration+"\"\r\n"+
						" \"cdrcallstatus\" :\""+quizGuiCallBack.getCdrcallstatus()+"\"\r\n"+ 
		                "}";
				logger.info("jsonBodyQuizData="+jsonBodyQuizData);
				logger.info("quizGuiCallback|guizGuiCallBackURL"+guizGuiCallBackURL);
				PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
				postClientRes = postClientCrbtService.sendPostClientCrbtReq(guizGuiCallBackURL, jsonBodyQuizData, "quiz-gui-callback");
				logger.info("postClientRes="+postClientRes);
				
			}
			
		}
		
		String responseString = new String("CDR_Res.result=\'Ok Accepted\';");
		/*
		 * responseString =
		 * responseString.concat("RBT_RES.toneId=\'"+toneInfoDetails.getToneId()+
		 * ".wav\';"); Date date = new Date(); SimpleDateFormat DateFor = new
		 * SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		 * System.out.println("CDR Report|"+DateFor.format(date)+"|aparty="+aparty+
		 * "|bparty="+bparty+"|status="+toneInfoDetails.getStatus()+"|toneId="+
		 * toneInfoDetails.getToneId()+"|");
		 */
		return responseString;
	}
	
	
}
