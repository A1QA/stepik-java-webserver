
________________________________________________________________________________________________________________________

- `channel.accept()`
- `channel.read(buffer)`
- `channel.write(buffer)`
Генерируют `java.nio.channels.ClosedByInterruptException`, если тред был заинтерапчет _перед_ их вызывом
даже несмотря на то, что каналы в неблокирующем режиме. (Видимо потому, что по-умолчанию они в блокирующем режиме.)

Если интератп произошел перед `channel.accept()`:
```
    java.nio.channels.ClosedByInterruptException
        at java.nio.channels.spi.AbstractInterruptibleChannel.end(AbstractInterruptibleChannel.java:202)
        at sun.nio.ch.ServerSocketChannelImpl.accept(ServerSocketChannelImpl.java:257)
        at nio_server.Server.onAccept(Server.java:171)
        at nio_server.Server.run(Server.java:134)
        at java.lang.Thread.run(Thread.java:748)
```
________________________________________________________________________________________________________________________


Если сервер завершил работу, оставив сокеты открытыми (не вызвав метод `close(Selector selector)`), 
эти сокеты останутся открытыми, пока не завершится процесс, в котором был запущен сервер.
В таком случае клиент "зависнет" в ожидании данных, если он читает из сокета. Если же он пишет в сокет, 
то зависнет, после того, как запишет в сокет определенное количество данных. 
Например, при вызове `Writer.write(new char[65536*10+1])` программа (клиент) зависнет, ожидая, 
пока с другой стороны данные начнут читать.

________________________________________________________________________________________________________________________

```java
//todo только запись, только чтение + перепроверить написанно
```

________________________________________________________________________________________________________________________

999 999 1999 999
1. Клиент подсоединился к серверу
2. Соединение было закрыто сервером
3. Клиент якобы "передал" данные серверу - "в холостую". (без исключения)
4. Чтение или запись -- исключения.
    - (сокет закрыт сервером (методом close(selector)))
        - SocketException: Software caused connection abort: recv failed
        - SocketException: Software caused connection abort: socket write error.

1. Клиент подсоединился к серверу
2. Соединение было закрыто сервером
3. Клиент прочел - null (последующие тоже) (т.е. увидел "end of the stream")
4. Все опытки записи -- исключение.
    - SocketException: Software caused connection abort: socket write error.



1. Клиент подсоединился к серверу
2. Соединение было закрыто сервером
3. Клиент попытался прочесть данные -- null



999 2299 0 999
1. Клиент подсоединился к серверу
2. Клиент успешно передал данные серверу
3. Сервер закрыл соединение.
4.  - последующие чтение будет возвращать null
    - первая запись пройдет в холостую, следующие за ней - с исключением
    - SocketException: Software caused connection abort: socket write 

1. Клиент подсоединился к серверу
2. Клиент успешно передал лишь часть данных серверу
3. Сервер закрыл соединение.
4.  - последующие чтение будет возвращать null
    - следующие записи - с исключением
    - SocketException: Software caused connection abort: socket write 



 - в случае использовая ПринтВрайтера исключений при записе не будет, там 
 лишь флаг устанавливается 
 - OutputStreamWriter.flush
 - BufferedReader.readLine
 
 
 
________________________________________________________________________________________________________________________
 
- если сокет не был закрыт сервером (но процесс, в котором был запущен сервер, завершен)
  - (read):  SocketException: Connection reset
  - (write): SocketException: Connection reset by peer: socket write error
- если сокет был закрыт сервером (методом close(selector))
  - (read):  SocketException: Software caused connection abort: recv failed
  - (write): SocketException: Software caused connection abort: socket write error.

________________________________________________________________________________________________________________________
 
```
 java.net.SocketException: Software caused connection abort: socket write error
 	at java.net.SocketOutputStream.socketWrite0(Native Method)
 	at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:111)
 	at java.net.SocketOutputStream.write(SocketOutputStream.java:155)
 	at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
 	at sun.nio.cs.StreamEncoder.implFlushBuffer(StreamEncoder.java:291)
 	at sun.nio.cs.StreamEncoder.implFlush(StreamEncoder.java:295)
 	at sun.nio.cs.StreamEncoder.flush(StreamEncoder.java:141)
 	at java.io.OutputStreamWriter.flush(OutputStreamWriter.java:229)
 	at nio_server.runners.ClientStarter.withWriter(Client.java:53)
 	at nio_server.runners.ClientStarter.main(Client.java:28)
 	at nio_server.Server.lambda$main$0(Server.java:53)
 	at java.lang.Thread.run(Thread.java:748)
 	
 java.net.SocketException: Software caused connection abort: recv failed
 	at java.net.SocketInputStream.socketRead0(Native Method)
 	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
 	at java.net.SocketInputStream.read(SocketInputStream.java:171)
 	at java.net.SocketInputStream.read(SocketInputStream.java:141)
 	at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
 	at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
 	at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
 	at java.io.InputStreamReader.read(InputStreamReader.java:184)
 	at java.io.BufferedReader.fill(BufferedReader.java:161)
 	at java.io.BufferedReader.readLine(BufferedReader.java:324)
 	at java.io.BufferedReader.readLine(BufferedReader.java:389)
 	at nio_server.runners.ClientStarter.withWriter(Client.java:61)
 	at nio_server.runners.ClientStarter.main(Client.java:28)
 	at nio_server.Server.lambda$main$0(Server.java:53)
 	at java.lang.Thread.run(Thread.java:748)

Exception in thread "Thread-1" java.nio.channels.CancelledKeyException
    at sun.nio.ch.SelectionKeyImpl.ensureValid(SelectionKeyImpl.java:73)
    at sun.nio.ch.SelectionKeyImpl.interestOps(SelectionKeyImpl.java:77)
    at nio_server.Server.lambda$close$1(Server.java:281)
    at java.lang.Iterable.forEach(Iterable.java:75)
    at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
    at nio_server.Server.close(Server.java:278)
    at nio_server.Server.run(Server.java:161)
    at java.lang.Thread.run(Thread.java:748)
```

________________________________________________________________________________________________________________________

// старый комментарий, возможны ошибки:

    /*Если работа сервера прекращена при активных соединениях, то
     *     если со стороны сервера сокет (не) закрыть, у клиента будет:
     * - Если не закрыть*: SocketException: Connection reset
     * - Если закрыть**:   SocketException: Software caused connection abort: recv failed
     *                                         (Исключения из метода BufferedReader.readLine())
     *   *вручную, насильно, завершил работу программы, например, из ОС убить процесс
     *   **т.е. interrupt() и вызов метода close(Selector selector), где сокет и закроется
     *
     * Если же interrupt() прервал поток, когда он спал (sleep(SERVER_PAUSE_AFTER)),
     * а не ожидал на selector.select(), у клиента метод readLine()
     * увидит "end of the stream" и будет возвращать null без генерации исключения.
     *
     */
                             

                                     



                                     
