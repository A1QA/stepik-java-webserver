package nio_server.server;

import nio_server.Sleeper;
import nio_server.context.GlobalContext;
import nio_server.logger.Logger;

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

    //todo регить n-й коннект у другого селектора в другом треде

    //todo сравнить селекшенкей и кей, елс и иф елс тоже
    //todo какие возвращает ключи в джава доке
    //todo проверить количество акссеболов у ключа

    private Logger logger = Logger.getInstance();
    private static final String PREFIX = "Server: ";
    private final int port;
    private final GlobalContext context = GlobalContext.getInstance();

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
     * todo сделать набор разных BrokenServer(с паузами и прекращеями работ в разных местах) для тестирования клиента
     *
     * */
    @Override
    public void run() {

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


            //System.out.println(PREFIX + "Ожидание соединения..."); // первый вызов selector.select() todo надпись при блокирующем ожидании соединения
            logger.log(this, "Ожидание соединения...");

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
                                //System.err.println(PREFIX + "Это. Не. Можыд. Быт."); // OP_CONNECT не использовался
                                logger.log(this, "Это. Не. Можыд. Быт.");
                            }
                        } else {
                            //System.err.println(PREFIX + "Невалидный SelectionKey"); // TODO: При какой ситуации?
                            logger.log(this, "Невалидный SelectionKey");
                        }
                    }
                } else { // если интерапт на блокирующем selector.select();
                    //System.out.println(PREFIX + "Нет готовых каналов");
                    logger.log(this, "Нет готовых каналов");
                }
                Sleeper.sleep(context.getServerContext().pauseAfterIteration(), PREFIX); // для экспериментов можно поставить паузу
            }

            if (context.getServerContext().closeClientsSockets()) {
                close(selector);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(PREFIX + "Работа сервера завершена");
        logger.log(this, "Работа сервера завершена");
    }

    private void onAccept(SelectionKey key) {
        try {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            //System.out.println(PREFIX + "Установка соединения...");
            logger.log(this, "Установка соединения...");
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);

            //System.out.println(PREFIX + "Установлено соединение с " + socketChannel.getRemoteAddress());
            logger.log(this, "Установлено соединение с " + socketChannel.getRemoteAddress());
        } catch (IOException e) {
            //System.err.println(PREFIX + "onAccept()");
            logger.err(this, "onAccept()");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void onRead(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            //System.out.println(PREFIX + "Чтение...");
            logger.log(this, "Чтение...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>) selectionKey.attachment();

            if (isNull(buffers)) {
                buffers = new LinkedList<>();
                //System.out.println(PREFIX + "Список буфферов создан");
                logger.log(this, "Список буфферов создан");
                selectionKey.attach(buffers);
            }
//      if (!buffers.isEmpty() &&) // todo частично заполненный буффер вытаскивать

            ByteBuffer buffer = ByteBuffer.allocate(context.getServerContext().bufferSize());

            //System.out.println(PREFIX + "Чтение в буффер");
            logger.log(this, "Чтение в буффер");
            int bytesRead = channel.read(buffer);
            //System.out.println(PREFIX + "Чтение в буффер завершено");
            logger.log(this, "Чтение в буффер завершено");
            if (bytesRead == -1) { // когда клиент сам закрыл сокет методом close()
                //System.out.println(PREFIX + "Завершение соединения с " + channel.getRemoteAddress() + " в onRead");
                logger.log(this, "Завершение соединения с " + channel.getRemoteAddress() + " в onRead");
                channel.close();   // + происходит удаление регистрации у селектора
                // + SelectionKey становится невалидным (isValid()), и если на нем вызвать, например, метод interestOps(),
                // то CancelledKeyException. "Заверншенный" SelectionKey удаляется при вызове selector.select()
            } else if (bytesRead > 0) {
                buffers.addLast(buffer);
                //System.out.println("Буффер добавлен");
                //System.out.println(PREFIX + "Прочитано " + bytesRead + " байт");
                logger.log(this, "Прочитано " + bytesRead + " байт");
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
//      if (buffer.position() == BUFFER_SIZE) {
//          buffers.addLast(buffer);
//      }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                key.channel().close();
                //System.out.println(PREFIX + "Канал закрыт в методе onRead()");
                logger.log(this, "Канал закрыт в методе onRead()");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void onWrite(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            //System.out.println(PREFIX + "Запись...");
            logger.log(this, "Запись...");

            SelectionKey selectionKey = channel.keyFor(key.selector());
            Deque<ByteBuffer> buffers = (Deque<ByteBuffer>) selectionKey.attachment();

            if (isNull(buffers)) {
                //System.out.println(PREFIX + "Список буфферов не создан");
                logger.log(this, "Список буфферов не создан");
                return;
            }
            if (!buffers.isEmpty()) {
                ByteBuffer buffer = buffers.pollFirst();

                buffer.flip();
                int bytesWritten = channel.write(buffer);
                //System.out.println(PREFIX + "Записано " + bytesWritten + " байт");
                logger.log(this, "Записано " + bytesWritten + " байт");
                buffer.compact();

                if (buffer.position() != 0) {
                    buffers.addFirst(buffer);
                    //System.out.println(PREFIX + "Буффер вернулся назаж");
                    logger.log(this, "Буффер вернулся назаж");
                }
            }
            if (buffers.isEmpty()) {
                //System.out.println(PREFIX + "Список буфферов пуст");
                logger.log(this, "Список буфферов пуст");
                key.interestOps(SelectionKey.OP_READ);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                key.channel().close();
                //System.out.println(PREFIX + "Канал закрыт в методе onWrite()");
                logger.log(this, "Канал закрыт в методе onWrite()");
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
                                //System.out.println(PREFIX + "Канал закрыт. В методе close()");
                                logger.log(this, "Канал закрыт. В методе close()");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //System.out.println(PREFIX + "Ключ обработан");
                        logger.log(this, "Ключ обработан");
                    }
                });
    }

    /**
     * Либо просто вот так. (см. метод close(Selector selector) чуть выше)
     * {@link Server#close(Selector selector)}
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
