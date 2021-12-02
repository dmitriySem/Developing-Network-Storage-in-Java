package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import json.JsonDecoder;
import json.JsonEncoder;
import message.TextMessage;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        new Client().start(9000);
    }

    public void start(int port) {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new ClientHendler()
                            );
                        }
                    });

            System.out.println("Client started");
            Scanner scanner = new Scanner(System.in);

            ChannelFuture channelFuture = bootstrap.connect("localhost", port).sync();

//            while (channelFuture.channel().isActive()) {
//                final DownloadFileRequestMessage message = new DownloadFileRequestMessage();
//                message.setPath("C:\\Users\\dsemenov\\Desktop\\Geekbrains_homeworks\\NettyServer\\textFile");
//                channelFuture.channel().writeAndFlush(message);
//                System.out.println("Enter message to server: ");
            TextMessage textMessage = new TextMessage();
            textMessage.setText("textFile");
//                textMessage.setText(scanner.nextLine());
//                System.out.println("Try to send message: " + textMessage.getText());
//
            channelFuture.channel().writeAndFlush(textMessage);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
