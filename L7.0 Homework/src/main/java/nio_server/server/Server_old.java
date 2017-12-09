/*
package nio_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server_old {

    private static final int BUFFER_SIZE = 12;
    private static final int SERVER_PORT = 5050;

    public static void main(String[] args) throws IOException, InterruptedException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

//            serverSocketChannel.configureBlocking(false);
//            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));

            new Thread(() -> {
                try {
                    Thread.sleep(122);
                    Client.main();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while(true){
                System.out.println("Ожидание соединения...");
                try (SocketChannel socketChannel = serverSocketChannel.accept()) {
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);


                    System.out.println("Работа с соединением " + socketChannel.getRemoteAddress());
                    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    // если слишком маленький, после заполнения, канал будет бесконечно готов к чтению оставшехся данных,
                    // но не к записи.

                    boolean connectionOpen = true; // пришлось завести, а хотелось бы,
                    while (connectionOpen) {       // чтобы было так "while (socketChannel.isOpen())"

                        int readyChannels = selector.select();
                        if (readyChannels != 0) {
                            Set<SelectionKey> selectedKeys = selector.selectedKeys();
                            //System.out.println(selectedKeys.size());
                            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                            while (keyIterator.hasNext()) {
                                SelectionKey key = keyIterator.next();
                                SocketChannel channel = (SocketChannel) key.channel();

                                if (key.isReadable()) {
                                    int bytesRead = channel.read(buffer);
                                    if (bytesRead == -1) {
                                        connectionOpen = false;
                                        System.out.println(selector.keys().size());
                                        //key.cancel(); // The key will be removed from* all of the selector's key sets during the next selection operation.
                                    }
                                    //System.out.println(buffer.position());
                                    //if (bytesRead > 0) {
                                    if (buffer.position() == BUFFER_SIZE) {
                                        //buffer.flip();
                                        //buffer.compact();
                                    }
                                    System.out.println("Прочитано " + bytesRead + " байт");

                                } else if (key.isWritable()) {
                                    buffer.flip();
                                    int bytesWritten = channel.write(buffer);
                                    System.out.println("Записано " + bytesWritten + " байт");
                                    buffer.compact();
                                }

                                // это не выводится, т.к. всегда все true
                                if (!channel.isConnected() || !channel.isOpen()) {
                                    System.out.println("\n" + channel.isConnected());
                                    System.out.println(channel.isOpen() + "\n");
                                }

                                keyIterator.remove();
                            }
                        }

                        Thread.sleep(220);
                    }
                }
            }
        }
    }
}
*/
