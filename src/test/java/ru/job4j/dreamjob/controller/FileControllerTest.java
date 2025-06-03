package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenGetExistingFileThenReturnFileContent() {
        var fileDto = new FileDto("test.txt", new byte[]{1, 2, 3});
        when(fileService.getFileById(anyInt())).thenReturn(Optional.of(fileDto));

        var result = fileController.getById(1);
        var actualContent = result.getBody();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(actualContent).isEqualTo(fileDto.getContent());
    }

    @Test
    public void whenGetNonExistingFileThenReturnNotFound() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.empty());

        var result = fileController.getById(1);

        assertThat(result.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void whenGetFileByIdThenCallServiceWithSameId() {
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(idCaptor.capture())).thenReturn(Optional.empty());

        fileController.getById(5);
        var actualId = idCaptor.getValue();

        assertThat(actualId).isEqualTo(5);
    }
}