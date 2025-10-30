@file:Suppress("NoWildcardImports")

package websockets

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.ContainerProvider
import jakarta.websocket.OnMessage
import jakarta.websocket.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElizaServerTest {
    @LocalServerPort
    private var port: Int = 0

    @Test
    fun onOpen() {
        logger.info { "This is the test worker" }
        val latch = CountDownLatch(3)
        val list = mutableListOf<String>()

        val client = SimpleClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        assertEquals(3, list.size)
        assertEquals("The doctor is in.", list[0])
    }

    @Test
    fun onChat() {
        logger.info { "Test thread" }
        val latch = CountDownLatch(4)
        val list = mutableListOf<String>()

        val client = ComplexClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        val size = list.size
        // 1. EXPLAIN WHY size = list.size IS NECESSARY
        // La comunicación por WebSocket es asíncrona; si usamos directamente list.size,
        // este valor podría cambiar mientras lo usamos. Al capturar el tamaño final en este momento,
        // evitamos condiciones de carrera si llegan nuevos mensajes más tarde.

        // 2. REPLACE BY assertXXX expression that checks an interval; assertEquals must not be used;
        assertTrue(size in 4..6)

        // 3. EXPLAIN WHY assertEquals CANNOT BE USED AND WHY WE SHOULD CHECK THE INTERVAL
        // No se puede usar assertEquals porque el momento y la cantidad de mensajes puede variar ligeramente
        // debido a la concurrencia y a la latencia de WebSocket. Usar un intervalo asegura que el test sea
        // robusto frente a diferencias de tiempo.

        // 4. COMPLETE assertEquals(XXX, list[XXX])
        assertEquals("The doctor is in.", list[0])
        assertEquals("What's on your mind?", list[1])
        assertEquals("---", list[2])
        assertTrue(
            list.any {
                it.contains("sad", ignoreCase = true) ||
                    it.contains("tell", ignoreCase = true) ||
                    it.contains("feel", ignoreCase = true) ||
                    it.contains("why", ignoreCase = true)
            },
            "Expected Eliza to respond with a doctor-style message",
        )

        logger.info { "Received messages: ${list.size}" }
    }
}

@ClientEndpoint
class SimpleClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(message: String) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
    }
}

@ClientEndpoint
class ComplexClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(
        message: String,
        session: Session,
    ) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
        if (message.contains("What's on your mind?")) {
            session.basicRemote.sendText("I am feeling sad")
        }
        logger.info { "Received messages: ${list.size}" }
    }
}

fun Any.connect(uri: String) {
    ContainerProvider.getWebSocketContainer().connectToServer(this, URI(uri))
}
