package de.uol.swp.client;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class MainTest {

    @Test
    void main() {
        try (MockedStatic<ClientApp> mockedClientApp = mockStatic(ClientApp.class)) {
            mockedClientApp.when(() -> ClientApp.main(new String[]{})).thenAnswer(Answers.RETURNS_DEFAULTS);

            Main.main(new String[]{});

            mockedClientApp.verify(() -> ClientApp.main(new String[]{}));
        }
    }
}
