/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.api.request.proxy;

import static org.mule.extension.http.api.request.proxy.HttpProxyConfig.HttpNtlmProxyConfig;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

/**
 * A Proxy configuration for NTLM authentication proxies.
 *
 * @since 1.0
 */
@Alias("ntlm-proxy")
@TypeDsl(allowTopLevelDefinition = true)
public class DefaultNtlmProxyConfig extends DefaultProxyConfig implements HttpNtlmProxyConfig {

  /**
   * The domain to authenticate against the proxy.
   */
  @Parameter
  @DisplayName("NTLM Domain")
  private String ntlmDomain;

  public String getNtlmDomain() {
    return ntlmDomain;
  }

  public void setNtlmDomain(String ntlmDomain) {
    this.ntlmDomain = ntlmDomain;
  }

}
