/**
* Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
* <p>
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.jboot.components.gateway;

import com.jfinal.log.Log;
import com.jfinal.server.undertow.UndertowServer;
import io.undertow.Undertow;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.servlet.handlers.ServletRequestContext;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import java.io.*;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class GatewayHttpProxy2 extends GatewayHttpProxy{

    private static final Log LOG = Log.getLog(GatewayHttpProxy.class);

    private int readTimeOut = 10000; //10s
    private int connectTimeOut = 5000; //5s
    private int retries = 2;
    private String contentType = JbootGatewayConfig.DEFAULT_PROXY_CONTENT_TYPE;
    public GatewayHttpProxy2(JbootGatewayConfig config) {
        this.readTimeOut = config.getProxyReadTimeout();
        this.connectTimeOut = config.getProxyConnectTimeout();
        this.retries = config.getProxyRetries();
        this.contentType = config.getProxyContentType();
    }
    
    public void sendRequest(String url){

        try{
            URI uri = new URI(url);
            ProxyHandler handler = new ProxyHandler(new ReverseProxyClient(uri), readTimeOut,
                                ResponseCodeHandler.HANDLE_404);
            handler.handleRequest(ServletRequestContext.current().getExchange());
        }catch (Exception e){
            LOG.error(e.toString(), e);
        }
    }
    public class ReverseProxyClient implements ProxyClient {
        private final ProxyTarget TARGET = new ProxyTarget() {};

        private final UndertowClient client;
        URI targetUri;
        public ReverseProxyClient(URI targeturi) {
            targetUri = targeturi;
            this.client = UndertowClient.getInstance();
        }

    @Override
        public ProxyTarget findTarget(HttpServerExchange exchange) {
            return TARGET;
        }

    @Override
        public void getConnection(ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
            
            client.connect(
                    new ConnectNotifier(callback, exchange),
                    targetUri,
                    exchange.getIoThread(),
                    exchange.getConnection().getByteBufferPool(),
                    OptionMap.EMPTY);
        }

    private final class ConnectNotifier implements ClientCallback<ClientConnection> {
            private final ProxyCallback<ProxyConnection> callback;
            private final HttpServerExchange exchange;

            private ConnectNotifier(ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
                this.callback = callback;
                this.exchange = exchange;
            }

        @Override
            public void completed(final ClientConnection connection) {
                final ServerConnection serverConnection = exchange.getConnection();
                serverConnection.addCloseListener(serverConnection1 -> IoUtils.safeClose(connection));
                callback.completed(exchange, new ProxyConnection(connection, "/"));
            }

        @Override
            public void failed(IOException e) {
                callback.failed(exchange);
            }
        }
    }
}