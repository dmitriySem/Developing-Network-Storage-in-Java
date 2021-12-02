package ru.gb.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.FileMessage;
import message.Message;
import message.TextMessage;

import java.io.RandomAccessFile;

public class ClientHendler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("Message from server: " + message.getText());
        }

        if (msg instanceof FileMessage){
            FileMessage message = (FileMessage) msg;
            try (RandomAccessFile randomAccessFile = new RandomAccessFile("File","rw")) {
                randomAccessFile.write(message.getContent());
            }

            ctx.close();
        }
    }
}
