package com.santosh.greenzone.main.controller;

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

import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class FetchAdvanceEnglishContents {
	private static final Logger logger = LogManager.getLogger(FetchAdvanceEnglishContents.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/fetchLearnEnglishContents",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("contentType") String contentType,@RequestParam("lessonId") String lessonId, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("checkVoiceSmsDetails|aparty="+aparty+"|bparty="+bparty+"|contentType="+contentType+"|lessonId="+lessonId);
		
		String selectQuery= ChatUtils.getCatSubCatQuery(env.getProperty("SQL37_SELECT_LEARN_ENGLISH_CONTENTS"), contentType,lessonId);
		logger.info("final selectQuery Query="+selectQuery);
		String responseString = new String();
		String dbError ="N";
		int counter=0;
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				
				responseString = responseString.concat("LE_CONTENT_RES.contentCount=\'"+"0"+"\';");
											
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					Integer intCounterLocal = new Integer(counter);
					
					if(row.get("path")==null||row.get("path").toString().isEmpty())
					{
						logger.error("subscriber_id or recording_path is null in database|aparty="+aparty);
						continue;
					}else
					{	
						responseString = responseString.concat("LE_CONTENT_RES.smsId["+intCounterLocal.toString()+"]"+"=\'"+Integer.parseInt(row.get("id").toString())+"\';");
						if(row.get("lessonid").toString()==null ||row.get("lessonid").toString().isEmpty())
						{
							responseString.concat("LE_CONTENT_RES.lessonId["+intCounterLocal.toString()+"]"+"=\'"+0+"\';");
						}else
						{
							responseString = responseString.concat("LE_CONTENT_RES.lessonId["+intCounterLocal.toString()+"]"+"=\'"+Integer.parseInt(row.get("lessonid").toString())+"\';");
						}
						responseString = responseString.concat("LE_CONTENT_RES.contentPath["+intCounterLocal.toString()+"]"+"=\'"+row.get("path")+"\';");
											
					}
					counter++;				
				}
				Integer intCounter = new Integer(counter);
				responseString = responseString.concat("LE_CONTENT_RES.contentCount=\'"+intCounter.toString()+"\';");
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+selectQuery);
			logger.error("No Row Found");
			
			responseString = responseString.concat("LE_CONTENT_RES.contentCount=\'"+"0"+"\';");
			dbError="Y";
			e.printStackTrace();
		}
		responseString = responseString.concat("LE_CONTENT_RES.dbError=\'"+dbError+"\';");
		logger.info("Response="+responseString);
		return responseString;
	}
}
