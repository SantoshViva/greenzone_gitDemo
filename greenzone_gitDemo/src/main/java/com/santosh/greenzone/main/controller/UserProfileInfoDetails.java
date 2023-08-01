package com.santosh.greenzone.main.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PostRemove;
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

import com.santosh.greenzone.model.ToneInfo;
import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.services.impl.PostClientServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;

@CrossOrigin
@RestController
public class UserProfileInfoDetails {

	private static final Logger logger = LogManager.getLogger(UserProfileInfoDetails.class);
	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/getUserProfileQuery", method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,
			@RequestParam("serviceId") String serviceId, @RequestParam(required = false) String packOffer,
			HttpServletRequest req, HttpServletResponse res) {

		logger.info("getUserProfileQuery|aparty=" + aparty + "|bparty=" + bparty + "|serviceId=" + serviceId + "|offer="
				+ packOffer);
		// System.out.println("getUserProfileInfoDetails|aparty=" +
		// aparty+"|bparty="+bparty);
//		System.out.println("Query=" + env.getProperty("SQL25_USER_PROFILE_INFO"));
//		System.out.println("IVR_DEFAULT_LANG=" + env.getProperty("IVR_DEFAULT_LANG"));

		// Replace Table Index & aparty
		String profileQuery = ChatUtils.getQuery(env.getProperty("SQL25_USER_PROFILE_INFO"), aparty);

		logger.trace("final profileCheck Query=" + profileQuery);

		String responseString = new String();
		String dbError = "N";
		String numberBlackList = "N";
		// responseString =
		// responseString.concat("RBT_PROFIL_QUERY_RES.dbError=\'"+"N"+"\';");
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(profileQuery);

			if (queryForList.isEmpty()) {
				logger.info("No Record Found in SQL|aparty=" + aparty);

				responseString = responseString
						.concat("RBT_PROFIL_QUERY_RES.ivrLang=\'" + env.getProperty("IVR_DEFAULT_LANG") + "\';");
				responseString = responseString.concat("RBT_PROFIL_QUERY_RES.subStatus=\'" + "N" + "\';");
				responseString = responseString.concat("RBT_PROFIL_QUERY_RES.ivrLangSet=\'" + "N" + "\';");

			} else {
				for (Map<String, Object> row : queryForList) {
					responseString = responseString
							.concat("RBT_PROFIL_QUERY_RES.ivrLang=\'" + row.get("ivr_langId") + "\';");

					if (row.get("subscriber_status") == null) {
						responseString = responseString.concat("RBT_PROFIL_QUERY_RES.subStatus=\'" + "N" + "\';");
					} else {
						responseString = responseString
								.concat("RBT_PROFIL_QUERY_RES.subStatus=\'" + row.get("subscriber_status") + "\';");
					}
					if (row.get("ivr_langId") == null) {
						responseString = responseString.concat(
								"RBT_PROFIL_QUERY_RES.ivrLang=\'" + env.getProperty("IVR_DEFAULT_LANG") + "\';");
						responseString = responseString.concat("RBT_PROFIL_QUERY_RES.ivrLangSet=\'" + "N" + "\';");
					} else {
						responseString = responseString
								.concat("RBT_PROFIL_QUERY_RES.ivrLang=\'" + row.get("ivr_langId") + "\';");
						responseString = responseString.concat("RBT_PROFIL_QUERY_RES.ivrLangSet=\'" + "Y" + "\';");
					}

					logger.info("QueryResult|subscriberId=" + bparty + "|ivrLang=" + row.get("ivr_langId").toString()
							+ "|");
				}
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e + "Query=" + profileQuery);
			logger.error("No Row Found");
			responseString = responseString
					.concat("RBT_PROFIL_QUERY_RES.ivrLang=\'" + env.getProperty("IVR_DEFAULT_LANG") + "\';");
			responseString = responseString.concat("RBT_PROFIL_QUERY_RES.subStatus=\'" + "N" + "\';");
			// responseString =
			// responseString.concat("RBT_PROFIL_QUERY_RES.dbError=\'"+"Y"+"\';");
			dbError = "Y";
			e.printStackTrace();
		}

		/** HTTP URL Hit for Third Party **/
		/** THIRD_PARTY_PROFILE_URL_DISABLE = Y for disable & N for enable */
		String postClientRes = "";
		if (env.getProperty("THIRD_PARTY_PROFILE_URL_DISABLE").equalsIgnoreCase("N")) {
			if (serviceId.compareToIgnoreCase("crbt") == 0) {
				String coreEngineProfileUrl = env.getProperty("CORE_ENGINE_PROFILE_URL");
				logger.info("coreEngineProfileUrl" + coreEngineProfileUrl);
				String jsonBodyCrbtData = "{\r\n" + " \"msisdn\": \"" + aparty + "\",\r\n"
						+ " \"action\" :\"ProfileQuery\"\r\n" +

						"}";
				logger.info("jsonBodyCrbtData=" + jsonBodyCrbtData);
				PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
				postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineProfileUrl, jsonBodyCrbtData,
						"check-subscription");
				logger.info("CoreEngine Subscriber Profile Status=" + postClientRes);
				responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'" + postClientRes + "\';");

//				if(postClientRes.equalsIgnoreCase("Y"))
//				{
//					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'"+"Y"+"\';");
//				}else if(postClientRes.equalsIgnoreCase("N"))
//				{
//					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'"+"N"+"\';");
//				}

			} else {

				String thirdPartyProfileUrl = env.getProperty("THIRD_PARTY_PROFILE_URL");
				String authKey = env.getProperty("THIRD_PARTY_AUTH_KEY");
				// System.out.print("thirdPartyUrl="+env.getProperty("THIRD_PARTY_PROFILE_URL"));
				String offer = env.getProperty("OFFER");
				if (packOffer != null && packOffer.isEmpty() != true) {
					offer = packOffer;
				}
				String jsonBodyData = "{\r\n" + "  \"msisdn\": \"" + aparty + "\",\r\n" + "  \"offer\": \"" + offer
						+ "\"\r\n" + "}";

				logger.info("jsonBodyData=" + jsonBodyData);
				PostClientServiceImpl postClientService = new PostClientServiceImpl();
				if(env.getProperty("THIRD_PARTY_PROFILE_STATUS").equalsIgnoreCase("Y"))
				{	
					postClientRes = postClientService.sendPostClientReq(thirdPartyProfileUrl, jsonBodyData,
						"check-subscription-status", authKey);
				}else
				{
					postClientRes = postClientService.sendPostClientReq(thirdPartyProfileUrl, jsonBodyData,
							"check-subscription", authKey);
				}
				logger.info("third Party Profile Response=" + postClientRes);

				if (postClientRes.equalsIgnoreCase("Y")) {
					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'" + "Y" + "\';");
				} else if (postClientRes.equalsIgnoreCase("L")) {
					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'" + "L" + "\';");
				} else if (postClientRes.equalsIgnoreCase("N")) {
					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'" + "N" + "\';");
				} else {
					responseString = responseString.concat("RBT_PROFIL_QUERY_RES.data=\'" + "N" + "\';");
				}
			}
		}

		if (env.getProperty("SERVICE_NAME").equalsIgnoreCase("CHAT")) {
			String blackQuery = ChatUtils.getQuery(env.getProperty("SQL48_SELECT_BACKLIST_NUMBER"), aparty);
			logger.trace("blackQuery=" + blackQuery);
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(blackQuery);

				if (queryForList.isEmpty()) {
					logger.info("Number is not backlist number|aparty=" + aparty);
					numberBlackList = "N";

				} else {
					for (Map<String, Object> row : queryForList) {

						if (row.get("subscriber_id") == null || row.get("subscriber_id").toString().isEmpty()) {
							logger.info("Something is wrong in database|aparty=" + aparty);
						} else {
							logger.info("Number is backlist number|aparty=" + aparty);
							numberBlackList = "Y";
						}

					}
				}
			} catch (Exception e) {
				logger.error("SQL Exception" + e + "Query=" + profileQuery);
				logger.error("No Row Found");
				numberBlackList = "N";
				e.printStackTrace();
			}

			/** Check CHAT subscriber profile */
			String chatProfileQuery = ChatUtils.getQuery(env.getProperty("SQL48_CHAT_PROFILE_INFO"), aparty);
			logger.info("SDF response postClientRes=" + postClientRes + "|chatProfileQuery=" + chatProfileQuery);
			String insertQueryFlag = "N";
			if (postClientRes.equalsIgnoreCase("Y")) {
				try {
					List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(chatProfileQuery);

					if (queryForList.isEmpty()) {
						logger.info("Number is not in chat profile|aparty=" + aparty);
						insertQueryFlag = "Y";

					} else {
						for (Map<String, Object> row : queryForList) {

							if (row.get("subscriber_status") == null
									|| row.get("subscriber_status").toString().isEmpty()) {
								logger.info("Something is wrong in database|aparty=" + aparty);
							} else {
								logger.info("Chat Subscriber Status|" + row.get("subscriber_status").toString()
										+ "|aparty=" + aparty);
								//numberBlackList = "Y";
							}

						}
					}
					/**inserting in chat profile table**/
					if(insertQueryFlag.equalsIgnoreCase("Y"))
					{
						String insertQuery=ChatUtils.getQuery(env.getProperty("SQL48_CHAT_PROFILE_INSERT"), aparty);
						int insertQueryResult= jdbcTemplate.update(insertQuery);
							if(insertQueryResult <= 0)
							{
								logger.error("Failed to insert in chat profile|query="+insertQueryResult);
							}else {
								logger.info("Successfully to data insert in  backlist|resultChangesRow="+insertQueryResult);
							}					
					}
					
				} catch (Exception e) {
					logger.error("SQL Exception" + e + "Query=" + profileQuery);
					logger.error("No Row Found");
					numberBlackList = "N";
					e.printStackTrace();
				}
				
			}

		}

		/****/
		responseString = responseString.concat("RBT_PROFIL_QUERY_RES.blackList=\'" + numberBlackList + "\';");
		responseString = responseString.concat("RBT_PROFIL_QUERY_RES.dbError=\'" + dbError + "\';");
		Date date = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		logger.info("CDR Report|" + DateFor.format(date) + "|aparty=" + aparty + "|bparty=" + bparty + "|response="
				+ responseString + "|");
		return responseString;

	}

}
