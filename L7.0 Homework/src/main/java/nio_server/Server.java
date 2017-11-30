package nio_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
 * todo https://stepik.org/lesson/13019/step/9?unit=3263 выложить
 * http://tutorials.jenkov.com/java-nio/index.html
 * https://www.programcreek.com/java-api-examples/index.php?source_dir=btpka3.github.com-master/java/jdk/TestJDK/src/main/java/me/test/jdk/java/nio/NioEchoServer.java
 * https://github.com/GoesToEleven/Java_NewCircle_training/blob/master/code/Networking/net/NioEchoServer.java
 * https://examples.javacodegeeks.com/core-java/nio/java-nio-echo-server-tutorial/
 **/

public class Server implements Runnable {

    public static void main(String... args) throws InterruptedException {

        final int SERVER_PORT = 5050;

        Thread thread = new Thread(new Server(SERVER_PORT));
        thread.start();

        Thread.sleep(115000);
        System.out.println("Прерывание работы сервера");
        thread.interrupt();
    }

    private static final int BUFFER_SIZE = 16;
    private final int port;

    public Server(int port) {
        this.port = port;
    }


    /**
     *
     * Про закрытие ресурсов:
     *
     * ServerSocketChannel и Selector закрываются благодоря блоку try-w-r.
     * ServerSocket внутри ServerSocketChannel закрывается вместе с ним.
     *
     * Открытые в методе onAccept() соединения SocketChannel (а точнее Socket внутри) закрываются в методе onRead(),
     * когда channel.read(buffer) возвращает -1, т.е. когда клиент завершил соединение, закрыв сокет методом close().
     *
     * Если клиент _разорвал_ соединение, а сервер следом попытается прочитать/записать данные,
     *  -> IOException: Удаленный хост принудительно разорвал существующее подключение
     * и в catch блоке канал закрывается.
     *
     * Если вызван interrupt() на потоке, в котором работает сервер, сервер сразу же прекращает работу, но соединения,
     * которые обычно закрываются в методе onRead(), тоже будут закрыты. У клиента в этом случает будет SocketException.
     * todo переслать уже полученные данные при интерапте
     * todo parse data для закрытия сокета
     *
     * */
    @Override
    public void run() {
        System.out.println("Server started");

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


            System.out.println("Ожидание соединения..."); // первый вызов selector.select() todo надпись при блокирующем ожидании соединения

            while (!Thread.currentThread().isInterrupted()) {   // для остановки: Thread.currentThread().interrupt();

                int readyChannels = selector.select();
                if (readyChannels > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                onAccept(key);
                            } else if (key.isReadable()) {
                                onRead(key);
                            } else if (key.isWritable()) {
                                onWrite(key);
                            } else {
                                System.out.println("Это. Не. Можыд. Быт.");
                            }
                        } else {
                            System.out.println("Невалидный SelectionKey");
                        }
                    }
                } else {
                    System.out.println("Нет готовых каналов");
                }
                //sleep(333); // для экспериментов можно поставить паузу
            }
            close(selector);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Работа сервера завершена");
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
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>) selectionKey.attachment();

            if (Objects.isNull(buffers)) {
                buffers = new LinkedList<>();
                System.out.println("Список буфферов создан");
                selectionKey.attach(buffers);
            }
//      if (!buffers.isEmpty() &&) // todo частично заполненный буффер вытаскивать

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);


            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) { // когда клиент сам закрыл сокет методом close()
                System.out.println("Завершение соединения с " + channel.getRemoteAddress());
                channel.close();   // + происходит удаление регистрации у селектора
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
                System.out.println("Канал закрыт");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void onWrite(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("Запись...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>) selectionKey.attachment();

            if (Objects.isNull(buffers)) {
                System.out.println("Список буфферов не создан");
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
            }
            if (buffers.isEmpty()) {
                System.out.println("Список буфферов пуст");
                key.interestOps(SelectionKey.OP_READ);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                key.channel().close();
                System.out.println("Канал закрыт");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void close(Selector selector) {
        selector.keys()
                .forEach((SelectionKey selectionKeys) -> {
                    int interestOps = selectionKeys.interestOps();
                    int keyRW = SelectionKey.OP_READ | SelectionKey.OP_WRITE; // битовое сложение

                      /*Если ключ заинтересован только в операциях OP_READ и/или OP_WRITE (но не OP_ACCEPT).*/
                    if ((keyRW & interestOps) > 0) {
                      /*Можно, конечно, было это и не делать, а закрывать все зарегистрированные у селектора каналы
                        нескомтря на то, что канал с OP_ACCEPT находится в try-w-r, т.е. по-любому будет закрыт.*/
                        try {
                              /*Если работа сервера прекращена при активных соединениях, то
                                    если со стороны сервера сокет (не) закрыть, у клиента будет:
                                - Если не закрыть: java.net.SocketException: Connection reset
                                - Если закрыть:    java.net.SocketException: Software caused connection abort: recv failed
                                                                        (Исключение из метода BufferedReader.readLine())*/
                            selectionKeys.channel().close();
                            System.out.println("Канал закрыт.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Либо просто вот так. (см. метод close())
     */
    private void closeSimple(Selector selector) {
        selector.keys().forEach(selectionKeys -> {
            try {
                selectionKeys.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Поток прерыван во время sleep(" + millis + ")");
//          e.printStackTrace();
        }
    }
}
