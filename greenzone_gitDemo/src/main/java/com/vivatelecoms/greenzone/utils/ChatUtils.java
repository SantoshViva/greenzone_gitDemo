package com.vivatelecoms.greenzone.utils;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.vivatelecoms.greenzone.main.controller.IvrLanguageSet;

import java.io.InputStream;

public class ChatUtils {

	private static final Logger logger = LogManager.getLogger(ChatUtils.class);
	
	public static String getQuery(String query, String msisdn) {
		String lastDigit = String.valueOf(msisdn.charAt(msisdn.length() - 1));
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{aparty}", msisdn);
		finalQuery = finalQuery.replace("{table_index}", lastDigit);
		finalQuery = finalQuery.replace("{userId}", msisdn);
		finalQuery = finalQuery.replace("{categoryId}", msisdn);
		finalQuery = finalQuery.replace("{messageId}", msisdn);
		//System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	public static String getApprovalChatQuery(String query, String messageId, String status,String userId) {
		String finalQuery = query.replace("{messageId}", messageId);
		finalQuery = finalQuery.replace("{status}",status);
		finalQuery = finalQuery.replace("{userId}",userId);
		return finalQuery;
	}
	public static String getBlackListChatQuery(String query, String msisdn, String userId) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		
		finalQuery = finalQuery.replace("{userId}",userId);
		return finalQuery;
	}
	public static String getCheckChatProfileQuery(String query, String msisdn, String subscriptionDuration,String blackListDuration) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		
		finalQuery = finalQuery.replace("{aparty}",msisdn);
		finalQuery = finalQuery.replace("{subscriptioDuration}",subscriptionDuration);
		finalQuery = finalQuery.replace("{blackListDuration}",blackListDuration);
		return finalQuery;
	}
	
	public static String getCatSubCatQuery(String query, String categoryId, String subCategoryId) {
		String finalQuery = query.replace("{categoryId}", categoryId);
		finalQuery = finalQuery.replace("{subCategoryId}", subCategoryId);
		finalQuery = finalQuery.replace("{lessongId}", subCategoryId);
		return finalQuery;
	}
	public static String getQuizContentDetailQuery(String query, String date)
	{
		String finalQuery = query.replace("{date}", date);
		return finalQuery;
	}
	public static String getChatGroupInsertQuery(String query, String aparty , String chatGroupId)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery= finalQuery.replace("{chatGroupId}", chatGroupId);
		finalQuery= finalQuery.replace("{msisdn}", aparty);
		finalQuery= finalQuery.replace("{visitedMessageId}", chatGroupId);
		return finalQuery;
	}
	public static String insertChatMessageQuery(String query, String aparty , String chatGroupId,String status,String chatDuration,String recordingFilePath)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery= finalQuery.replace("{subscriberId}", aparty);
		finalQuery= finalQuery.replace("{chatGroupId}", chatGroupId);
		finalQuery= finalQuery.replace("{status}", status);
		finalQuery= finalQuery.replace("{duration}", chatDuration);
		finalQuery= finalQuery.replace("{recordingPath}", recordingFilePath);
		return finalQuery;
	}
	public static String getQueryReplaceOneString(String query, String replaceString)
	{
		String finalQuery = query.replace("{aparty}",replaceString);
		return finalQuery;
	}
	public static String getQueryChatMessage(String query, String chatGroupId,String messageCount,String messageId)
	{
		String finalQuery =query.replace("{chatGroupId}", chatGroupId);
		finalQuery=finalQuery.replace("{messageCount}", messageCount);
		finalQuery=finalQuery.replace("{messageId}", messageId);
		return finalQuery;
	}
	public static String getSmsText(String query, String smsId) {
		String finalQuery = query.replace("{id}", smsId);
		return finalQuery;
	}
	public static String getServiceAllowQuery(String query, String aparty,String serviceId) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{serviceId}", serviceId);
		return finalQuery;
	}
	public static String getAccountInsertQuery(String query, String msisdn,String userId,String gender,String ageGroup,String operator, String defaultNotificationOpt) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{table_index}", String.valueOf(msisdn.charAt(msisdn.length() - 1)));
		finalQuery = finalQuery.replace("{userId}", userId);
		finalQuery = finalQuery.replace("{gender}", gender);
		finalQuery = finalQuery.replace("{ageGroup}", ageGroup);
		finalQuery = finalQuery.replace("{operator}", operator);
		finalQuery = finalQuery.replace("{defaultNotificationOpt}", defaultNotificationOpt);
		return finalQuery;
	}
	
	public static String getTonePlayerDtmfInsertQuery(String query, String aparty, String bparty, String toneId, String digits, String startTime,
			String endTime, String starToCopy) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		finalQuery = finalQuery.replace("{tone_id}", toneId);
		finalQuery = finalQuery.replace("{digits}", digits);
		finalQuery = finalQuery.replace("{start_time}",startTime );
		finalQuery = finalQuery.replace("{end_time}",endTime );
		finalQuery = finalQuery.replace("{star_to_copy}",starToCopy );
		return finalQuery;
	}
	public static String getTonePlayerCDRQuery(String query, String aparty, String bparty,String playStatus,String toneType, String toneId, String playStartTime,
			String playEndTime) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		finalQuery = finalQuery.replace("{playStatus}", playStatus);
		finalQuery = finalQuery.replace("{reason}", playStatus);
		finalQuery = finalQuery.replace("{toneType}", toneType);
		finalQuery = finalQuery.replace("{duration}", toneType);
		finalQuery = finalQuery.replace("{toneId}", toneId);
		finalQuery = finalQuery.replace("{lastNode}", toneId);
		finalQuery = finalQuery.replace("{playStartTime}", playStartTime);
		finalQuery = finalQuery.replace("{callStartTime}", playStartTime);
		finalQuery = finalQuery.replace("{playEndTime}",playEndTime );
		finalQuery = finalQuery.replace("{callEndTime}",playEndTime );
		return finalQuery;
	}
	public static String getQuizCDRQuery(String query,String aparty, String bparty, String playStartTime, String playEndTime,String duration,String quizStatus,String quizCat,String quizScore,String quizTitle)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		finalQuery = finalQuery.replace("{quizCat}", quizCat);
		finalQuery = finalQuery.replace("{quizScore}", quizScore);
		finalQuery = finalQuery.replace("{quizTitle}", quizTitle);
		finalQuery = finalQuery.replace("{duration}", duration);
		finalQuery = finalQuery.replace("{duration}", duration);
		finalQuery = finalQuery.replace("{callStartTime}", playStartTime);
		finalQuery = finalQuery.replace("{callEndTime}",playEndTime);
		finalQuery = finalQuery.replace("{quizStatus}",quizStatus);
		return finalQuery;
	}
	public static String getDynamicQuery(String query, String parameter,String value)
	{
		String finalQuery = query.replace(parameter, value);
		return finalQuery;
	}
	public static String getIvrLangInsertQuery(String query, String aparty, String bparty, String ivrLang)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{ivrLang}", ivrLang);
		return finalQuery;
		
	}
	public static String getIvrPromptDetailsQuery(String query,String serviceName)
	{
		String finalQuery = query.replace("{serviceName}", serviceName);
		
		return finalQuery;
		
	}
	public static String getMwomanDashboardQuery(String query,String fromDate,String toDate)
	{
		String finalQuery = query.replace("{fromDate}", fromDate);
		finalQuery = finalQuery.replace("{toDate}", toDate);
		finalQuery = finalQuery.replace("{startIndex}", fromDate);
		finalQuery = finalQuery.replace("{endIndex}", toDate);
		return finalQuery;
		
	}
	public static String getMsisdnChatQuery(String query,String fromDate,String toDate,String msisdn)
	{
		String finalQuery = query.replace("{fromDate}", fromDate);
		finalQuery = finalQuery.replace("{aparty}", msisdn);
		finalQuery = finalQuery.replace("{startIndex}", fromDate);
		finalQuery = finalQuery.replace("{endIndex}", toDate);
		return finalQuery;
	}
	public static String getFetchMessageQuery(String query,String startIndex,String endIndex,String msisdn,String status,String chatGroupId)
	{
		String finalQuery = query.replace("{status}", status);
		finalQuery = finalQuery.replace("{aparty}", msisdn);
		finalQuery = finalQuery.replace("{chatGroupId}", chatGroupId);
		finalQuery = finalQuery.replace("{startIndex}", startIndex);
		finalQuery = finalQuery.replace("{endIndex}", endIndex);
		return finalQuery;
	}
	public static String insertToneInfoQuery(String query, String aparty, String toneType, String toneTypeIdx,String callingParty, String toneId,String status,String songName)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{toneType}", toneType);
		finalQuery = finalQuery.replace("{toneTypeIdx}", toneTypeIdx);
		finalQuery = finalQuery.replace("{callingParty}", callingParty);
		finalQuery = finalQuery.replace("{toneId}", toneId);
		finalQuery = finalQuery.replace("{status}", status);
		finalQuery = finalQuery.replace("{songName}", songName);
		return finalQuery;
	}
	
	
	public static String updateSubProfileQuery(String query, String aparty, String bparty, String sub_status, String offer)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{sub_status}", sub_status);
		finalQuery = finalQuery.replace("{offer}", offer);
		return finalQuery;
		
	}
	
	
	public static String getQuery(String query, String msisdn,String tableIndex, String searchText) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{searchText}", searchText);
		finalQuery = finalQuery.replace("{get_table_index}", tableIndex);
		System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	public static String getCatSubCatQuery(String query, String categoryId,String subCategoryId,String contentCount) {
		String finalQuery = query.replace("{categoryId}", categoryId);
		finalQuery = finalQuery.replace("{subCategoryId}", subCategoryId);
		finalQuery = finalQuery.replace("{contentCount}", contentCount);
		System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	
	
	public static String getAllTableQuery(String query, String index) {
		String finalQuery = query.replace("{all_table_index}", String.valueOf(index));
		return finalQuery;
	}
	
	public static String getGender(String query, String msisdn,JdbcTemplate jdbcTemplate) {
		String genderQuery = query.replace("{msisdn}",msisdn);
		genderQuery = genderQuery.replace("{table_index}", String.valueOf(msisdn.charAt(msisdn.length() - 1)));
		try {
			return jdbcTemplate.queryForObject(genderQuery, String.class);
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+query);
			e.printStackTrace();
			return "";
		}
		
	}
	public static String runLinuxCommand(String command) {
		
		try {
			Process child = Runtime.getRuntime().exec(command);
		}catch(Exception e)
		{
			return "exception occurred";
		}
//		InputStream in = child.getInputStream();
//		int c;
//		while ((c = in.read()) != -1) {
//		      System.out.println((char) c);
//		    }
//		in.close();

		 return "";
	}
	
	public static boolean isNumber(String input) {
		for(int i=0; i<input.length();i++) {
			if(Character.isDigit(input.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	
}
