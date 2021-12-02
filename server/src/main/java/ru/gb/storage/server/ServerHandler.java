package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.DownloadFileRequestMessage;
import message.FileMessage;
import message.Message;
import message.TextMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final int BUFFER_SIZE =1024*64;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("Text message from client: " + message.getText());
            SendFile(message.getText(),ctx);

//            ctx.writeAndFlush(msg);
        }


        if (msg instanceof DownloadFileRequestMessage){
            DownloadFileRequestMessage message = (DownloadFileRequestMessage) msg;
            try (RandomAccessFile accessFile = new RandomAccessFile(message.getPath(), "r")){
                final FileMessage fileMessage = new FileMessage();
                byte[] content = new byte[(int) accessFile.length()];
                accessFile.read(content);
                fileMessage.setContent(content);
                ctx.writeAndFlush(fileMessage);

            } catch (FileNotFoundException e ) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void SendFile(String FileName, ChannelHandlerContext ctx){

        final FileMessage fileMessage = new FileMessage();
//        System.out.println("Path of workDir:" + fileMessage.getWorkDir());
        try (RandomAccessFile accessFile = new RandomAccessFile(fileMessage.getWorkDir() + File.separator + FileName, "r")){
            byte[] content = new byte[(int) accessFile.length()];
            accessFile.read(content);
            fileMessage.setContent(content);
            ctx.writeAndFlush(fileMessage);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("client disconnect");
    }
}
