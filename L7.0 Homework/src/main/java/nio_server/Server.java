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
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static nio_server.Settings.BUFFER_SIZE;
import static nio_server.Settings.RUN_CLIENT_IN_NEW_PROCESS;
import static nio_server.Settings.SERVER_CLOSES_CLIENT_SOCKETS;
import static nio_server.Settings.SERVER_LIFE_TIME;
import static nio_server.Settings.SERVER_PAUSE_AFTER;
import static nio_server.Settings.SERVER_PORT;


/**
 * todo https://stepik.org/lesson/13019/step/9?unit=3263 выложить
 * http://tutorials.jenkov.com/java-nio/index.html
 * https://www.programcreek.com/java-api-examples/index.php?source_dir=btpka3.github.com-master/java/jdk/TestJDK/src/main/java/me/test/jdk/java/nio/NioEchoServer.java
 * https://github.com/GoesToEleven/Java_NewCircle_training/blob/master/code/Networking/net/NioEchoServer.java
 * https://examples.javacodegeeks.com/core-java/nio/java-nio-echo-server-tutorial/
 **/

public class Server implements Runnable {

    //todo регить n-й коннект у другого селектора в другом треде

    //gc при клиенте зависшем в отдельно потоке
    //сравнить селекшенкей и кей, елс и иф елс тоже
    //какие возвращает ключи в джава доке
    //проверить количество акссеболов
    //у ключа

    public static void main(String... args) {

        runClient(RUN_CLIENT_IN_NEW_PROCESS);

        Thread server = new Thread(new Server(SERVER_PORT));
        server.start();

        Sleeper.sleep(SERVER_LIFE_TIME);
        System.out.println("=Прерывание работы сервера...");
        server.interrupt();
        System.out.println("=Запрос на прерывание работы сервера отправлен.");
    }

    private final int port;

    public Server(int port) {
        this.port = port;
    }


    /**
     * Запуск клиента в отдельном потоке
     *
     * @param inNewProcess есди true, запускает в отдельном процессе.
     */
    private static void runClient(boolean inNewProcess) {

        Thread client = new Thread(() -> {
            Sleeper.sleep(5);
            if (!inNewProcess) {
                try {
                    System.out.println("=Запуск клиента");
                    Client.main();
                    System.out.println("=Клиент завершил работу");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(JavaProcessRunner.CLASS_PATH, JavaProcessRunner.CLASS));
                try {
                    System.out.println("=Запуск клиента в новом процессе");
                    process.runProcess();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Sleeper.sleep(20000);
                try {
                    process.waitFor(20, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();
                System.out.println("=Процесс клиента завершен");
            }
        });
        client.start();
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
     * todo сделать набор разных BrokeServer(с паузами и прекращеями работ в разных местах) для тестирования клиента
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
                                System.err.println("Это. Не. Можыд. Быт."); // OP_CONNECT не использовался
                            }
                        } else {
                            System.err.println("Невалидный SelectionKey"); // TODO: При какой ситуации?
                        }
                    }
                } else { // если интерапт на блокирующем selector.select();
                    System.out.println("Нет готовых каналов");
                }
                Sleeper.sleep(SERVER_PAUSE_AFTER); // для экспериментов можно поставить паузу
            }

            if (SERVER_CLOSES_CLIENT_SOCKETS) {
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

    @SuppressWarnings("unchecked")
    private void onRead(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("Чтение...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
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
        //System.out.println(selector.keys().size());
        selector.keys()
                .forEach((SelectionKey selectionKey) -> {
                    //System.out.println(selectionKey.isValid());
                    if (selectionKey.isValid()) {  /* Иначе может быть, что selectionKey.interestOps() -> java.nio.channels.CancelledKeyException,
                                                    *  если ключ был автоматически cancel() при close() его канала, но еще не удален из селектора,
                                                    *  т.к. не был вызван метод select(). */
                        int interestOps = selectionKey.interestOps();
                        int keyRW = SelectionKey.OP_READ | SelectionKey.OP_WRITE; // Битовое сложение

                        /*Если ключ заинтересован только в операциях OP_READ и/или OP_WRITE (но не OP_ACCEPT).*/
                        if ((keyRW & interestOps) > 0) {
                            /* Можно, конечно, было это и не делать, а закрывать _все_ зарегистрированные у селектора каналы
                             * несмотря на то, что канал с OP_ACCEPT находится в try-w-r, т.е. по-любому будет закрыт.*/
                            try {
                                selectionKey.channel().close();
                                System.out.println("Канал закрыт. В методе close()");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("Ключ обработан");
                    }
                });
    }

    /**
     * Либо просто вот так. (см. метод close(Selector selector) чуть выше)
     * {@link nio_server.Server#close(Selector selector)}
     */
    private void closeSimple(Selector selector) {
        selector.keys().forEach(selectionKey -> {
            try {
                if (selectionKey.isValid()) {
                    selectionKey.channel().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
