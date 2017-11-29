package nio_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;


/**
 * http://tutorials.jenkov.com/java-nio/index.html
 * https://www.programcreek.com/java-api-examples/index.php?source_dir=btpka3.github.com-master/java/jdk/TestJDK/src/main/java/me/test/jdk/java/nio/NioEchoServer.java
 * https://github.com/GoesToEleven/Java_NewCircle_training/blob/master/code/Networking/net/NioEchoServer.java
 * https://examples.javacodegeeks.com/core-java/nio/java-nio-echo-server-tutorial/
 **/

public class Server implements Runnable {

    public static void main(String... args) throws IOException, InterruptedException {
        final int SERVER_PORT = 5050;
        new Server(SERVER_PORT).run(); // in this thread
    }

    private static final int BUFFER_SIZE = 32;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


            System.out.println("Ожидание соединения..."); // первый вызов selector.select() todo

            while (!Thread.currentThread().isInterrupted()) {   // для остановки: Thread.currentThread().interrupt(); todo


//              System.out.print("selector.select()");
                int readyChannels = selector.select();
                if (readyChannels != 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    //System.out.println("selectedKeys.size() = " + selectedKeys.size());

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isAcceptable()) {
                            onAccept(key);
                        } else if (key.isReadable()) {
                            onRead(key);
                        } else if (key.isWritable()) {
                            onWrite(key);
                        }
                    }
                }
                Thread.sleep(0);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onAccept(SelectionKey key) {
        try {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            System.out.println("Установка соединения...");
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);

            System.out.println("Установлено соединение с " + socketChannel.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void onRead(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            //System.out.println("Чтение...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>)selectionKey.attachment();

            if (Objects.isNull(buffers)) {
                buffers = new LinkedList<>();
                System.out.println("Лист буфферов создан");
                selectionKey.attach(buffers);
            }
//      if (!buffers.isEmpty() &&) // todo частично заполненный буффер вытаскивать

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);


            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Разорвано соединение с " + channel.getRemoteAddress());
                channel.close();
            } else if (bytesRead > 0) {
                buffers.addLast(buffer);
                //System.out.println("Буффер добавлен");
                System.out.println("Прочитано " + bytesRead + " байт");
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
//      if (buffer.position() == BUFFER_SIZE) {
//          buffers.addLast(buffer);
//      }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                key.channel().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void onWrite(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            //System.out.println("Запись...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>)selectionKey.attachment();

            if (Objects.isNull(buffers)) {
                System.out.println("Лист буфферов не создан");
                return;
            }
            if (!buffers.isEmpty()) {
                ByteBuffer buffer = buffers.pollFirst();

                buffer.flip();
                int bytesWritten = channel.write(buffer);
                System.out.println("Записано " + bytesWritten + " байт");
                buffer.compact();

                if (buffer.position() != 0) {
                    buffers.addFirst(buffer);
                    System.out.println("Буффер вернулся назаж");
                }
            } else {
                System.out.println("Лист буфферов пуст");
                key.interestOps(SelectionKey.OP_READ);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
