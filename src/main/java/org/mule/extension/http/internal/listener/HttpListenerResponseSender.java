/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.internal.listener;

import static org.mule.runtime.http.api.HttpConstants.HttpStatus.INTERNAL_SERVER_ERROR;

import org.mule.extension.http.api.listener.builder.HttpListenerResponseBuilder;
import org.mule.extension.http.api.streaming.HttpStreamingType;
import org.mule.extension.http.internal.listener.intercepting.Interception;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.runtime.source.SourceCompletionCallback;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.mule.runtime.http.api.domain.message.response.HttpResponseBuilder;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;
import org.mule.runtime.http.api.server.async.ResponseStatusCallback;

public class HttpListenerResponseSender {

  private final HttpResponseFactory responseFactory;

  public HttpListenerResponseSender(HttpResponseFactory responseFactory) {
    this.responseFactory = responseFactory;
  }

  public HttpListenerResponseSender(TransformationService transformationService) {
    this.responseFactory = new HttpResponseFactory(HttpStreamingType.NEVER, transformationService);
  }

  public void sendResponse(HttpResponseContext context,
                           HttpListenerResponseBuilder response,
                           SourceCompletionCallback completionCallback)
      throws Exception {
    HttpResponse httpResponse = buildResponse(response, context.getInterception(), context.isSupportStreaming());
    final HttpResponseReadyCallback responseCallback = context.getResponseCallback();
    responseCallback.responseReady(httpResponse, getResponseFailureCallback(responseCallback, completionCallback));
  }

  protected HttpResponse buildResponse(HttpListenerResponseBuilder listenerResponseBuilder, Interception interception,
                                       boolean supportStreaming)
      throws Exception {
    HttpResponseBuilder responseBuilder = HttpResponse.builder();

    return doBuildResponse(responseBuilder, listenerResponseBuilder, interception, supportStreaming);
  }

  protected HttpResponse doBuildResponse(HttpResponseBuilder responseBuilder,
                                         HttpListenerResponseBuilder listenerResponseBuilder,
                                         Interception interception,
                                         boolean supportsStreaming)
      throws Exception {
    return responseFactory.create(responseBuilder, interception, listenerResponseBuilder, supportsStreaming);
  }

  private ResponseStatusCallback getResponseFailureCallback(HttpResponseReadyCallback responseReadyCallback,
                                                            SourceCompletionCallback completionCallback) {
    return new ResponseStatusCallback() {

      @Override
      public void responseSendFailure(Throwable throwable) {
        try {
          responseReadyCallback.responseReady(buildErrorResponse(), this);
          completionCallback.success();
        } catch (Throwable t) {
          completionCallback.error(t);
        }
      }

      @Override
      public void responseSendSuccessfully() {
        // TODO: MULE-9749 Figure out how to handle this. Maybe doing nothing is right since this will be executed later if
        // everything goes right.
        // responseCompletationCallback.responseSentSuccessfully();
        completionCallback.success();
      }
    };
  }

  protected HttpResponse buildErrorResponse() {
    final HttpResponseBuilder errorResponseBuilder = HttpResponse.builder();
    final HttpResponse errorResponse = errorResponseBuilder.statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
        .reasonPhrase(INTERNAL_SERVER_ERROR.getReasonPhrase())
        .build();
    return errorResponse;
  }

}
