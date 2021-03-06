/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.http.internal.listener;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mule.extension.http.api.streaming.HttpStreamingType.AUTO;
import static org.mule.runtime.api.metadata.DataType.INPUT_STREAM;
import static org.mule.runtime.http.api.HttpHeaders.Names.CONTENT_LENGTH;
import static org.mule.test.http.AllureConstants.HttpFeature.HTTP_EXTENSION;
import org.mule.extension.http.api.listener.builder.HttpListenerResponseBuilder;
import org.mule.extension.http.internal.listener.HttpResponseFactory;
import org.mule.extension.http.internal.listener.intercepting.NoInterception;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.tck.junit4.AbstractMuleTestCase;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

@Feature(HTTP_EXTENSION)
@Story("Issues")
public class HttpResponseFactoryTestCase extends AbstractMuleTestCase {

  private static final String EXAMPLE_STRING = "exampleString";
  private static final String WRONG_CONTENT_LENGTH = "12";

  @Test
  @Description("Verifies that the correct Content-Length is sent even when a wrong one is set as header.")
  public void testContentLengthIsOverridden() throws Exception {
    HttpListenerResponseBuilder listenerResponseBuilder = mock(HttpListenerResponseBuilder.class);
    TypedValue<Object> payload = new TypedValue<>(new ByteArrayInputStream(EXAMPLE_STRING.getBytes(UTF_8)), INPUT_STREAM);
    when(listenerResponseBuilder.getBody()).thenReturn(payload);
    MultiMap<String, String> headers = new MultiMap<>();
    headers.put(CONTENT_LENGTH, WRONG_CONTENT_LENGTH);
    when(listenerResponseBuilder.getHeaders()).thenReturn(headers);
    when(listenerResponseBuilder.getStatusCode()).thenReturn(200);
    HttpResponseFactory httpResponseBuilder = new HttpResponseFactory(AUTO, mock(TransformationService.class));

    HttpResponse httpResponse =
        httpResponseBuilder.create(HttpResponse.builder(), new NoInterception(), listenerResponseBuilder, true);
    assertThat(httpResponse.getHeaderValue(CONTENT_LENGTH), is(String.valueOf(EXAMPLE_STRING.length())));
  }

}
