package de.uol.swp.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class ClientHandlerTest {

    static MockedStatic<LogManager> logManagerMockedStatic;
    static Logger loggerMock = mock(Logger.class);
    ClientConnection clientConnectionMock = mock(ClientConnection.class);
    Channel channelMock = mock(Channel.class);
    ChannelHandlerContext channelHandlerContextMock = mock(ChannelHandlerContext.class);
    private ClientHandler clientHandler;

    @AfterAll
    static void afterAll() {
        logManagerMockedStatic.close();
    }

    @BeforeAll
    static void beforeAll() {
        logManagerMockedStatic = mockStatic(LogManager.class);
        logManagerMockedStatic.when(() -> LogManager.getLogger(ClientHandler.class)).thenReturn(loggerMock);
    }

    @BeforeEach
    protected void setUp() {
        clientHandler = new ClientHandler(clientConnectionMock);
    }

    @Test
    void channelActive() {
        doReturn(channelMock).when(channelHandlerContextMock).channel();
        doNothing().when(clientConnectionMock).fireConnectionEstablished(isA(Channel.class));

        clientHandler.channelActive(channelHandlerContextMock);

        verify(loggerMock).debug("Connected to server: {}", channelHandlerContextMock);
        verify(channelHandlerContextMock).channel();
        verify(clientConnectionMock).fireConnectionEstablished(channelMock);
    }
}