package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import message.Message;

public class JsonEncoder extends MessageToByteEncoder<Message> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
//        System.out.println("Message encode: " + String.valueOf(msg));
        byte[] value = OBJECT_MAPPER.writeValueAsBytes(msg);
        out.writeBytes(value);
    }
}
