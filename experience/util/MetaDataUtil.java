package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.Product;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class MetaDataUtil {

	static final XLogger logger = XLoggerFactory.getXLogger(MetaDataUtil.class);

	public String getProductId(MetaData metaData) throws ApiException {
		SubApplicationInfo subApplicationInfo = getSubApplicationInfo(metaData);
		String productId = subApplicationInfo.getProductId();
		return productId;
	}

	public String getSubApplicationId(MetaData metaData) throws ApiException {
		SubApplicationInfo subApplicationInfo = getSubApplicationInfo(metaData);
		String subApplicationId = subApplicationInfo.getSubApplicationId();
		return subApplicationId;
	}

	private SubApplicationInfo getSubApplicationInfo(MetaData metaData) throws ApiException{
		List<SubApplicationInfo> subApplicationList = metaData.getSubApplicationList();
		Iterator<SubApplicationInfo> iter = subApplicationList.iterator();
		SubApplicationInfo subApplicationInfo = iter.next();
		return subApplicationInfo;
	}

	@SuppressWarnings("unchecked")
	public MetaData populateMetaDataBeforeSession(Map<String, Object> parameters) {
		MetaData metaData = new MetaData();
		//TODO - hardcoded to make questions call work.
		metaData.setChannel((String)parameters.get(SessionUtil.SESSION_KEY_CHANNEL_ID));
		List<SubApplicationInfo> subApplicationList = new ArrayList<SubApplicationInfo>();
		metaData.setProductId((String)parameters.get(SessionUtil.SESSION_KEY_PRODUCT_ID));

		for (String aProductId : (List<String>) parameters.get(SessionUtil.SESSION_KEY_PRODUCTS)) {
			SubApplicationInfo subApplication = new SubApplicationInfo();
			subApplication.setProductId(aProductId);
			subApplicationList.add(subApplication);
		}

		metaData.setSubApplicationList(subApplicationList);
		return metaData;
	}

	/**
	 * Description: Common method to populate meta data across experience.
	 * @param connectId
	 * @param esoJsonData
	 * @param stage
	 * @return MetaData
	 */
	public MetaData populateMetaData(String connectId,EsoJsonData esoJsonData,String stage){
    Integer applicationId = null;
    if (esoJsonData.get(SessionUtil.SESSION_KEY_APPLICATION_ID) != null) {
      applicationId = Integer.parseInt(esoJsonData.get(SessionUtil.SESSION_KEY_APPLICATION_ID).toString());
    }
		String channel = (String) esoJsonData.get(SessionUtil.SESSION_KEY_CHANNEL);
		String flowId = (String)esoJsonData.get(SessionUtil.SESSION_KEY_FLOW_ID);
		String sessionId = (String)esoJsonData.get(SessionUtil.SESSION_KEY_SESSION_ID);
    String samlCode = (String) esoJsonData.get(SessionUtil.DI_INBOUND_SAML_CODE);
    String valetKey = (String) esoJsonData.get(SessionUtil.VALET_KEY);


		MetaData metaData = new MetaData();
		metaData.setApplicationId(applicationId);
		metaData.setChannel(channel);
		metaData.setConnectId(connectId);
		metaData.setFlowId(flowId);
		metaData.setSessionId(sessionId);
		metaData.setProductId((String)esoJsonData.get(SessionUtil.SESSION_KEY_PRODUCT_ID));
		metaData.setStage(stage);
		metaData.setInboundSamlCode(samlCode);
    metaData.setValetKey(valetKey);
		metaData.setPrimaryPartyId((String) esoJsonData.get(SessionUtil.SESSION_KEY_PARTY_ID));
		List<SubApplicationInfo> subApplicationInfoList = populateSubApplicationList(esoJsonData);
		metaData.setSubApplicationList(subApplicationInfoList);
		return metaData;
	}

	@SuppressWarnings("unchecked")
	private List<SubApplicationInfo> populateSubApplicationList(EsoJsonData esoJsonData) {
		List<Long> subApplicationList = (List<Long>)esoJsonData.get(SessionUtil.SESSION_KEY_SUBAPPLICATION_IDS);
		// subApplicationList should have 1 row only as of Aug 24, 2017
		String subAppId = (subApplicationList != null && !subApplicationList.isEmpty()) ? subApplicationList.get(0).toString(): null;

		List<String> products = (List<String>)esoJsonData.get(SessionUtil.SESSION_KEY_PRODUCTS);
		if (products == null || products.isEmpty()) {
			products = new ArrayList<String>();
			products.add((String)esoJsonData.get(SessionUtil.SESSION_KEY_PRODUCT_ID)); // this will be replaced by the product selector page
		}
		List<SubApplicationInfo> subApplicationInfoList = new ArrayList<SubApplicationInfo>();
		for (String productId : products) { // products has at least the synthetic product
			SubApplicationInfo subApplicationInfo = new SubApplicationInfo();
			subApplicationInfo.setProductId(productId);
			subApplicationInfo.setSubApplicationId(subAppId);
			subApplicationInfoList.add(subApplicationInfo);
		}
		return subApplicationInfoList;
	}

	private String getSubAppId(List<SubApplicationInfo> subApplicationList) {
		return (subApplicationList != null && !subApplicationList.isEmpty())?subApplicationList.get(0).getSubApplicationId().toString(): null;
	}

	public void updateSubApplicationList(MetaData metaData, WealthClientMasterInfo wcm) {
		List<SubApplicationInfo> subApplicationInfoList = new ArrayList<SubApplicationInfo>();
		String subAppId = getSubAppId(metaData.getSubApplicationList());
		if (wcm.getProducts().isEmpty()) { // no product selected yet
			SubApplicationInfo subApplicationInfo = new SubApplicationInfo();
			subApplicationInfo.setProductId(metaData.getProductId()); // put the synthetic product there for now because it's required by the event API
			subApplicationInfo.setSubApplicationId(subAppId);
			subApplicationInfoList.add(subApplicationInfo);
		} else {
			for (Product product : wcm.getProducts()) {
				SubApplicationInfo subApplicationInfo = new SubApplicationInfo();
				subApplicationInfo.setProductId(product.getProductId());
				subApplicationInfo.setSubApplicationId(subAppId);

				subApplicationInfoList.add(subApplicationInfo);
			}
		}
		metaData.setSubApplicationList(subApplicationInfoList);

	}
}
