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
import java.util.Set;

import static java.util.Objects.isNull;


/**
 * todo https://stepik.org/lesson/13019/step/9?unit=3263 выложить
 * http://tutorials.jenkov.com/java-nio/index.html
 * https://www.programcreek.com/java-api-examples/index.php?source_dir=btpka3.github.com-master/java/jdk/TestJDK/src/main/java/me/test/jdk/java/nio/NioEchoServer.java
 * https://github.com/GoesToEleven/Java_NewCircle_training/blob/master/code/Networking/net/NioEchoServer.java
 * https://examples.javacodegeeks.com/core-java/nio/java-nio-echo-server-tutorial/
 **/

public class Server implements Runnable {

    /*
    999 2299 0 999
    Сервер прочел половину.


     999 999 1999 999
     сразу же закрыл


     */
    //todo регить n-й коннект у другого селектора в другом треде


    private static final int SERVER_PAUSE_AFTER = 999;
    private static final int SERVER_LIFE_TIME   = 12299;
    public  static final int CLIENT_PAUSE1      = 0;
    public  static final int CLIENT_PAUSE2      = 999;

    public  static final boolean CLIENT_PRINT_WRITER = false;
    private static final boolean SERVER_CLOSE_SOCKET = true;

    private static final int BUFFER_SIZE = 64;

    public static void main(String... args) throws InterruptedException {

        final int SERVER_PORT = 5050;


        Thread client = new Thread(() -> {
            try {
                //sleep(1115);
                System.out.println("=client1");
                Client.main();
                System.out.println("=client2");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        //client.setDaemon(true);
        client.start();// run client

        Thread server = new Thread(new Server(SERVER_PORT));
        //server.setDaemon(true);
        server.start();

        Thread.sleep(SERVER_LIFE_TIME);
        System.out.println("=Прерывание работы сервера");
        server.interrupt();
        //client.join();
//        Thread.sleep(4444);
//        client.interrupt();
        System.out.println("=end of main");
    }

    private final int port;

    public Server(int port) {
        this.port = port;
    }


    /**
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
     *
     * interrupt(), в частности, прерывает блокирующий метод selector.select()
     *
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
                              /*Прерывание работы сервера
                                Нет готовых каналов
                                Поток прерыван во время sleep(333)
                                Канал закрыт.
                                Работа сервера завершена*/
                        }
                    }
                } else { // если интерапт на блокирующем selector.select();
                    System.out.println("Нет готовых каналов");
                }
                sleep(SERVER_PAUSE_AFTER); // для экспериментов можно поставить паузу

            }
            if (SERVER_CLOSE_SOCKET) {
                close(selector);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("!Работа сервера завершена");
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
            System.err.println("onAccept()");
            e.printStackTrace();
        }
    }

    // todo циклик onWrite и onRead. хотя, надо и ограничение оставить, иначе может долго читать один канал и т.д.
    // хотя тогда проще буффер увеличить же



    @SuppressWarnings("unchecked")
    private void onRead(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("Чтение...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            System.out.println("___"+selectionKey.isValid());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>) selectionKey.attachment();

            if (isNull(buffers)) {
                buffers = new LinkedList<>();
                System.out.println("Список буфферов создан");
                selectionKey.attach(buffers);
            }
//      if (!buffers.isEmpty() &&) // todo частично заполненный буффер вытаскивать

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            System.out.println("Чтение в буффер");
            int bytesRead = channel.read(buffer);
            System.out.println("Чтение в буффер завершено");
            if (bytesRead == -1) { // когда клиент сам закрыл сокет методом close()
                System.out.println("Завершение соединения с " + channel.getRemoteAddress() + " в onRead");
                channel.close();   // + происходит удаление регистрации у селектора
                // + SelectionKey становится невалидным (isValid()), и если на нем вызвать, например, метод interestOps(),
                // то CancelledKeyException. "Заверншенный" SelectionKey удаляется при вызове selector.select()
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
                System.out.println("Канал закрыт в методе onRead()");
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
            System.out.println("___"+selectionKey.isValid());

            if (isNull(buffers)) {
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
                System.out.println("Канал закрыт в методе onWrite()");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void close(Selector selector) {
        System.out.println(selector.keys().size());
        selector.keys()
                .forEach((SelectionKey selectionKey) -> {
                    System.out.println(selectionKey.isValid());
                    if (selectionKey.isValid()) {  /* Иначе может быть, что .interestOps() -> java.nio.channels.CancelledKeyException,
                                                       если ключ быз cancel() при close() его канала, но еще не удален из селектора,
                                                       т.к. не был вызван метод select(). */
                        int interestOps = selectionKey.interestOps();
                        int keyRW = SelectionKey.OP_READ | SelectionKey.OP_WRITE; // битовое сложение

                        /*Если ключ заинтересован только в операциях OP_READ и/или OP_WRITE (но не OP_ACCEPT).*/
                        if ((keyRW & interestOps) > 0) {
                            /*Можно, конечно, было это и не делать, а закрывать _все_ зарегистрированные у селектора каналы
                             *несмотря на то, что канал с OP_ACCEPT находится в try-w-r, т.е. по-любому будет закрыт.*/
                            try {
                                // todo сделать набор разных BrokeServer(с паузами и прекращеями работ в разных местах) для тестирования клиента
                                selectionKey.channel().close();
                                System.out.println("Канал закрыт. В методе close()");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("ключ обработан");
                    }
                });
    }

    /**
     * Либо просто вот так. (см. метод close(Selector selector) чуть выше)
     * {@link nio_server.Server#close(Selector selector)}
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
        if (millis > 0) { // а то интерапт эксепшены даже при 0 могут быть
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток прерыван во время sleep(" + millis + ")");
//          e.printStackTrace();
            }
        }
    }
}
