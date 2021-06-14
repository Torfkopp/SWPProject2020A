package de.uol.swp.common.chat.dto;

import de.uol.swp.common.I18nWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class SystemMessageDTOTest {

    private final ResourceBundle mockedResourceBundle = mock(ResourceBundle.class);

    @Test
    void testToString() {
        String returnString = "test string";
        when(mockedResourceBundle.getString(isA(String.class))).thenReturn(returnString);

        try (MockedStatic<I18nWrapper> mockedWrapper = mockStatic(I18nWrapper.class)) {
            try {
                Field resourceBundleField = I18nWrapper.class.getDeclaredField("resourceBundle");
                resourceBundleField.setAccessible(true);
                resourceBundleField.set(mockedWrapper, mockedResourceBundle);
                I18nWrapper wrapper = new I18nWrapper("");

                SystemMessageDTO messageDTO = new SystemMessageDTO(wrapper);

                assertEquals(returnString, messageDTO.toString());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
            }
        }
    }
}