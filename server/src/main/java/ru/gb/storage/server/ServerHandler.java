package ru.gb.storage.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;

public class ServerHandler extends SimpleChannelInboundHandler<Message> {
    private static final int BUFFER_SIZE =1024*64;
    private final Executor executor;

    public ServerHandler(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            String FileName = message.getText();
            System.out.println("Text message from client: " + FileName);
            SendFile(FileName,ctx);

        }


        if (msg instanceof DownloadFileMessage){
            DownloadFileMessage message = (DownloadFileMessage) msg;
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
        executor.execute(() ->{
            final FileMessage fileMessage = new FileMessage();
            fileMessage.setFileName(FileName);

            try (RandomAccessFile accessFile = new RandomAccessFile(fileMessage.getWorkDir() + File.separator + FileName, "r")){
                long fileLength = accessFile.length();
                do {
                    var position = accessFile.getFilePointer();
                    final long availableBytes = fileLength - position;
                    byte[] content;

                    if (availableBytes >=BUFFER_SIZE){
                        content = new byte[BUFFER_SIZE];
                    } else {
                        content = new byte[(int) availableBytes];
                    }
                    accessFile.read(content);
                    fileMessage.setContent(content);
                    fileMessage.setStartPosition(position);
                    ctx.writeAndFlush(fileMessage).sync();

                } while (accessFile.getFilePointer() < fileLength);

                ctx.writeAndFlush(new EndFileMessage());

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });


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
