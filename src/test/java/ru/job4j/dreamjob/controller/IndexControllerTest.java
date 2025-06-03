package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IndexControllerTest {

    private final IndexController indexController = new IndexController();

    @ParameterizedTest
    @ValueSource(strings = {"/", "/index"})
    public void whenRequestAnyValidPathThenGetIndexPage(String path) {
        String view = indexController.getIndex();
        assertThat(view).isEqualTo("index");
    }

    @Test
    public void whenCallGetIndexThenReturnIndexViewName() {
        String result = indexController.getIndex();
        assertThat(result).isEqualTo("index");
    }
}