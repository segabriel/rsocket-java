/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rsocket.examples.transport.tcp.requestresponse;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public final class HelloWorldClient {

  public static void main(String[] args) {
    RSocketFactory.receive()
        .acceptor(
            (setupPayload, reactiveSocket) ->
                Mono.just(
                    new AbstractRSocket() {
                        int counter = 0;
                      @Override
                      public Mono<Payload> requestResponse(Payload p) {
//                          return Mono.empty();
                          return Mono.just(p);
//                          switch (++counter) {
//                              case 1: return Mono.error(new Throwable());
//                              case 2: return Mono.just(p);
//                              default: return Mono.empty();
//                          }
                      }
                    }))
        .transport(TcpServerTransport.create("localhost", 7000))
        .start()
        .subscribe();

    RSocket socket =
        RSocketFactory.connect()
            .transport(TcpClientTransport.create("localhost", 7000))
            .start()
            .block();

    socket
        .requestResponse(DefaultPayload.create("Hello"))
        .map(Payload::getDataUtf8)
        .onErrorReturn("error")
        .doOnSuccess(System.out::println)
        .doOnError(System.err::println)
        .block();

//    socket
//        .requestResponse(DefaultPayload.create("Hello"))
//        .map(Payload::getDataUtf8)
//        .onErrorReturn("error")
//        .doOnSuccess(System.out::println)
//        .doOnError(System.err::println)
//        .block();
//
//    socket
//        .requestResponse(DefaultPayload.create("Hello"))
//        .map(Payload::getDataUtf8)
//        .onErrorReturn("error")
//        .doOnSuccess(System.out::println)
//        .doOnError(System.err::println)
//        .block();

    socket.dispose();
  }
}
