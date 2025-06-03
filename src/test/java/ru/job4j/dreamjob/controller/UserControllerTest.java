package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenGetRegisterPageThenReturnRegisterView() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenGetLoginPageThenReturnLoginView() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRegisterNewUserThenRedirectToIndex() {
        var user = new User("name", "email", "password");
        when(userService.save(user)).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);

        assertThat(view).isEqualTo("redirect:/index");
    }

    @Test
    public void whenRegisterExistingUserThenReturnError() {
        var user = new User("name", "email", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var errorMessage = model.getAttribute("errorMessage");

        assertThat(view).isEqualTo("errors/500");
        assertThat(errorMessage).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    public void whenRegisterWithExceptionThenReturnErrorWithStackTrace() {
        var user = new User("name", "email", "password");
        var exception = new RuntimeException("Test exception");
        when(userService.save(user)).thenThrow(exception);

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var errorMessage = model.getAttribute("errorMessage");
        var stackTrace = model.getAttribute("stackTrace");

        assertThat(view).isEqualTo("errors/500");
        assertThat(errorMessage).isEqualTo("Test exception");
        assertThat(stackTrace).isNotNull();
    }

    @Test
    public void whenLoginSuccessThenRedirectToVacancies() {
        var user = new User("name", "email", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        var view = userController.loginUser(user, model, request);

        verify(session).setAttribute("user", user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginFailedThenReturnLoginPageWithError() {
        var user = new User("name", "wrong@email", "wrong");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var view = userController.loginUser(user, model, request);
        var error = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(error).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenLogoutThenInvalidateSessionAndRedirectToLogin() {
        var session = mock(HttpSession.class);
        var view = userController.logout(session);

        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenRegisterUserThenPassSameUserToService() {
        var user = new User("name", "email", "password");
        var userCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        userController.register(user, model);
        var actualUser = userCaptor.getValue();

        assertThat(actualUser).isEqualTo(user);
    }
}