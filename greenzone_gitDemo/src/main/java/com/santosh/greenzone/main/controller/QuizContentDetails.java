package com.santosh.greenzone.main.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class QuizContentDetails {
	private static final Logger logger = LogManager.getLogger(QuizContentDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/quizContentDetails",method = RequestMethod.GET)
	@ResponseBody
	public String quizContentDetails(@RequestParam("serviceId") String serviceId,@RequestParam("quizCategoryId") String quizCategoryId,@RequestParam("contentCount") String contentCount,@RequestParam(required = false) String guiDb,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("quizContentDetails|serviceId="+serviceId+"|quizCategoryId="+quizCategoryId+"|contentCount="+contentCount+"|guiDb="+guiDb);
		String responseString = new String();
		int counter=0;
		String dbError ="N";
		if(contentCount==null)
		{
			contentCount="5";
		}
		if(guiDb == null)
		{
			guiDb="N";
		}
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		
		String strDate="";
		if(Integer.parseInt(quizCategoryId)==1)
		{
			Date currentDate = new Date();
			strDate= sdfDate.format(currentDate);
		}
		if(Integer.parseInt(quizCategoryId)==2)
		{
			LocalDate currentDate = LocalDate.now();
			LocalDate previousDay = currentDate.minusDays(1);
			logger.info("previosDay="+previousDay);
			strDate=previousDay.toString();
		}
		
		logger.info("date="+strDate);
		String selectQuery="" ;
		if(guiDb.equalsIgnoreCase("N"))
			selectQuery= ChatUtils.getQuizContentDetailQuery(env.getProperty("SQL37_SELECT_QUIZ_CONTENTS"),strDate);
		else
			selectQuery= ChatUtils.getQuizContentDetailQuery(env.getProperty("SQL37_SELECT_QUIZ_CONTENTS_GUI"),strDate);
		logger.info("selectQuery="+selectQuery);
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			if(queryForList.isEmpty())
			{
				/**No record found. Error handling here*/
				responseString = responseString.concat("QUIZ_CONTENT_RES.contentCount=\'"+"0"+"\';");
			}
			else
			{
				for (Map<String, Object> row : queryForList) {
					Integer intCounterLocal = new Integer(counter);
					if(guiDb.equalsIgnoreCase("N"))
					{	
						if(row.get("question_id")==null||row.get("prompt_path").toString().isEmpty()||row.get("answer_key")==null)
						{
							logger.error("Category & sub category songs are null in database|");
							continue;
						}else
						{	
										
							logger.info("question_id="+row.get("question_id")+"|prompt_path="+row.get("prompt_path")+"|answer_key="+row.get("answer_key"));
							responseString = responseString.concat("QUIZ_CONTENT_RES.question_id["+intCounterLocal.toString()+"]"+"=\'"+row.get("question_id")+"\';");
							responseString = responseString.concat("QUIZ_CONTENT_RES.prompt_path["+intCounterLocal.toString()+"]"+"=\'"+row.get("prompt_path")+"\';");
							responseString = responseString.concat("QUIZ_CONTENT_RES.answer_key["+intCounterLocal.toString()+"]"+"=\'"+row.get("answer_key")+"\';");
						}
						counter++;
					}
					if(guiDb.equalsIgnoreCase("Y"))
					{
						
						logger.info("question_id="+row.get("id")+"|prompt_path="+row.get("questionid")+"|answer_key="+row.get("answer"));
						if(row.get("id")==null||row.get("questionid")==null||row.get("answer")==null)
						{
							logger.error("ques.id,ques.questionid,ques.answer|");
							logger.info("question_id="+row.get("id")+"|prompt_path="+row.get("questionid")+"|answer_key="+row.get("answer"));
							continue;
						}else
						{	
							
							logger.info("question_id="+row.get("id")+"|prompt_path="+row.get("questionid")+"|answer_key="+row.get("answer"));
							responseString = responseString.concat("QUIZ_CONTENT_RES.question_id["+intCounterLocal.toString()+"]"+"=\'"+row.get("id")+"\';");
							responseString = responseString.concat("QUIZ_CONTENT_RES.prompt_path["+intCounterLocal.toString()+"]"+"=\'"+row.get("questionid")+"\';");
							responseString = responseString.concat("QUIZ_CONTENT_RES.answer_key["+intCounterLocal.toString()+"]"+"=\'"+row.get("answer")+"\';");
						}
						counter++;
						
					}
				   }
					Integer intCounter = new Integer(counter);
					responseString = responseString.concat("QUIZ_CONTENT_RES.contentCount=\'"+intCounter.toString()+"\';");
			}
		}catch(Exception e){
			logger.error("SQL Exception" + e +"Query="+selectQuery);
			logger.error("No Row Found");
			responseString = responseString.concat("QUIZ_CONTENT_RES.songCount=\'"+"0"+"\';");
			dbError="Y";
			e.printStackTrace();
		}
		responseString = responseString.concat("QUIZ_CONTENT_RES.dbError=\'"+dbError+"\';");
		return responseString;
	}

}
