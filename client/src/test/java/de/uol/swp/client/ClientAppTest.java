package de.uol.swp.client;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.chat.IChatService;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.game.IGameService;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.user.IUserService;
import javafx.application.Application;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ClientAppTest {

    Injector mockedInjector = mock(Injector.class);

    @Test
    void main() {
        MockedStatic<Application> mockedApplication = null;
        try (MockedStatic<Guice> mockedGuice = mockStatic(Guice.class)) {
            mockedGuice.when(() -> Guice.createInjector(isA(ClientModule.class))).thenReturn(mockedInjector);
            Field field = ClientApp.class.getDeclaredField("injector");
            field.setAccessible(true);
            field.set(null, mockedInjector);

            when(mockedInjector.getInstance(IUserService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            when(mockedInjector.getInstance(ILobbyService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            when(mockedInjector.getInstance(IChatService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            when(mockedInjector.getInstance(IGameService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            when(mockedInjector.getInstance(ITradeService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            when(mockedInjector.getInstance(ISoundService.class)).thenAnswer(Answers.RETURNS_DEFAULTS);
            mockedApplication = mockStatic(Application.class);
            mockedApplication.when(Application::launch).thenAnswer(Answers.RETURNS_DEFAULTS);

            ClientApp.main(new String[]{});

            mockedGuice.verify(() -> Guice.createInjector(isA(ClientModule.class)));
            verify(mockedInjector).getInstance(IUserService.class);
            verify(mockedInjector).getInstance(ILobbyService.class);
            verify(mockedInjector).getInstance(IChatService.class);
            verify(mockedInjector).getInstance(IGameService.class);
            verify(mockedInjector).getInstance(ITradeService.class);
            verify(mockedInjector).getInstance(ISoundService.class);
            mockedApplication.verify(Application::launch);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e.getMessage());
        } finally {
            if (mockedApplication != null) mockedApplication.close();
        }
    }
}