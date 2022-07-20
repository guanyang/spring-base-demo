package org.gy.demo.log.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/1 15:09
 */
public class NioServerHandler implements Runnable {

    private SelectionKey selectionKey;

    public NioServerHandler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        try {
            if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer);
                buffer.flip();
                System.out.println(
                    "收到客户端" + socketChannel.socket().getInetAddress().getHostName() + "的数据：" + new String(
                        buffer.array()));
                //将数据添加到key中
                ByteBuffer outBuffer = ByteBuffer.wrap(buffer.array());
                socketChannel.write(outBuffer);// 将消息回送给客户端
                selectionKey.cancel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
