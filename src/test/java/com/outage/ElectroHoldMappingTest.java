package com.outage;

import com.outage.scrapers.ElectroHold;
import com.outage.scrapers.ElectroHoldEventDTO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
public class ElectroHoldMappingTest {
    @Test
    void mapToDtoTest() {
        ElectroHold electroHold = new ElectroHold();
        String text = "Населено място: ТОМПСЪН Тип прекъсване: непланирано Начало на прекъсването: 09.05.2023 18:13 Очаквано време за възстановяване на захранването: 09.05.2023 19:30";

        ElectroHoldEventDTO dto = null;
        try {
            dto = electroHold.convertTextToDTO(text);
        } catch (Exception ignored) {}

        if (dto == null)
            Assertions.fail("Throws Exception");

        Assertions.assertEquals(dto.getText(), text);
    }
}
