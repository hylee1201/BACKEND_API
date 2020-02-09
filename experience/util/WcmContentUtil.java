package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.config.ApiConfig;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import com.td.dcts.eso.wcm.content.config.WcmConfig;

public class WcmContentUtil {

  /**
   * Read {@link WcmConfig} object from file system.
   *
   * @return
   */
  static final XLogger logger = XLoggerFactory.getXLogger(WcmContentUtil.class);

  private WcmContentUtil() {
    throw new IllegalAccessError(WcmContentUtil.class.getName());
  }

  public static WcmConfig getWcmConfig() {
    return (WcmConfig) ApiConfig
      .getInstance()
      .getJsonObjectConfig(
        ExperienceConstants.FILENAME_WCM_CONFIG,
        WcmConfig.class);
  }
}
