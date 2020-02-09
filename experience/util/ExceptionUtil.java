package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ExceptionUtil {

	static final XLogger logger = XLoggerFactory.getXLogger(ExceptionUtil.class);

	public static Status buildServerErrorStatus() {
		AdditionalStatus additionalStatus = ErrorHandler.getInstance().getAdditionalStatusFromErrorCode("ERR6511");
		Status status = new Status(HttpStatus.INTERNAL_SERVER_ERROR.toString(), Severity.Error, additionalStatus);
		return status;
	}

  public static Status buildServerErrorStatus(String detailError) {
    AdditionalStatus addlStatus = new AdditionalStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
      AdditionalSeverity.Error, detailError);
    Status status = new Status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), Severity.Error,
      addlStatus);
    return status;
  }

	public static Status buildErrorStatus(List<String> errorList) {
		List<AdditionalStatus> arrAddlStatus = new ArrayList<AdditionalStatus>();
		for (String error : errorList) {
			AdditionalStatus addlStatus = new AdditionalStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), error,
					AdditionalSeverity.Error, error);
			arrAddlStatus.add(addlStatus);
		}

		Status status = new Status(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), Severity.Error,
				arrAddlStatus.toArray(new AdditionalStatus[arrAddlStatus
						.size()]));
		return status;
	}

	public static Status buildErrorStatus(int statusCode, String statusDescription) {
		Status status = new Status(String.valueOf(statusCode), Severity.Error,
				new AdditionalStatus(statusCode, String.valueOf(statusCode), AdditionalSeverity.Error, statusDescription));
		return status;
	}

	public static Status buildErrorStatusFromApiErrorStatus(ResponseEntity responseEntity) {
        Status status = null;
        Map<Object, Object> responseEntityMap = null;
        if (responseEntity != null) {
        	Object o = responseEntity.getBody();
        	if (o instanceof String) {
        		try {
					responseEntityMap = ApiConfig.getInstance().getMapper().readValue((String)o, Map.class);
				} catch (IOException e) {
					return new Status("500", Severity.Error);
				}
        	} else {
        		responseEntityMap = (Map<Object, Object>) o;
        	}
            //Map<Object, Object> responseEntityMap = (Map<Object, Object>) responseEntity.getBody();
            Map<Object, Object> statusMap = (Map<Object, Object>) responseEntityMap.get("status");
            String serverStatusCode = (String) statusMap.get("serverStatusCode");
            List<Map<Object, Object>> additionalStatusList = (List<Map<Object, Object>>) statusMap.get("additionalStatus");

            if(!CollectionUtils.isEmpty(additionalStatusList)) {
                Map<Object, Object> additionalStatusListItem = additionalStatusList.get(0);
                AdditionalStatus additionalStatus = new AdditionalStatus();
                additionalStatus.setStatusCode((Integer) additionalStatusListItem.get("statusCode"));
                additionalStatus.setServerStatusCode((String) additionalStatusListItem.get("serverStatusCode"));
                additionalStatus.setSeverity(AdditionalSeverity.Error);
                additionalStatus.setStatusDesc((String) additionalStatusListItem.get("statusDesc"));

                status = new Status(String.valueOf(serverStatusCode), Severity.Error, additionalStatus);
            }
        }

        return status;
    }

	public static String getErrorAdditionalStatusCode(ApiException e) {
		Status status = e.getStatus();
		if (status != null && status.getAdditionalStatus() != null) {
			AdditionalStatus[] additionalStatuses = status.getAdditionalStatus();
			if (additionalStatuses.length > 0) {
				return String.valueOf(additionalStatuses[0].getStatusCode());
			}
		}
		return null;
	}
}
