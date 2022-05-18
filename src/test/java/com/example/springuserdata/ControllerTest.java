package com.example.springuserdata;

import com.example.springuserdata.models.SearchUser;
import com.example.springuserdata.models.User;
import com.example.springuserdata.repository.AccountsRepository;
import com.example.springuserdata.service.AccountsService;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest {

    @Autowired
    AccountsRepository accountsRepository;

    @Autowired
    private MockMvc mockMvc;


    /* Тестируем вызов главной страницы "/"
    *  Ожидаемый статут ответа: 200 */
    @Test
    public void pageHome() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home.html"));
    }

    /* Тестируем вызов страницы авторизации "/login"
    *  Ожидаемый статус ответа: 200 */
    @Test
    public void pageLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("authorization.html"));
    }

    /* Тестируем вызов страницы регистрации "/registration"
     *  Ожидаемый статус ответа: 200 */
    @Test
    public void pageRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration.html"))
                .andExpect(model().attribute("user", instanceOf(User.class)));
    }

    /* Тестируем проверку авторизации, если клиент не вошел в систему,
     * его должно перебрасывать на "/login"
     * Ожидаемый статус ответа: 302 */
    @Test
    public void pageSearchRedirectToLogin() throws Exception {
        mockMvc.perform(get("/search"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }


    /* Тестируем проверку авторизации, если клиент неправильно ввел логин и пароль,
     * его должно перебрасывать на "/login?error"
     * Ожидаемый статус ответа: 302 */
    @Test
    public void pageLoginError() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("noadmin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }


    /* Тестируем проверку авторизации, если клиент правильно ввел логин и пароль,
     * его должно перебрасывать на главную страницу "/"
     * Ожидаемый статус ответа: 302 */
    @Test
    public void pageLoginSuccess() throws Exception {
        System.out.println(accountsRepository.findByUsername("admin"));
        mockMvc.perform(formLogin().user("admin").password("admin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    
    /* Тестируем проверку авторизации, если клиент правильно ввел регистрационные данные, то
     * его должно перебрасывать на "/login"
     * Ожидаемый статус ответа: 302 */
    @Test
    public void pageRegistrationSuccess() throws Exception {
        User user = createUser("test");
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "information".getBytes());

        if (accountsRepository.findByUsername(user.getUsername()) != null) {
            System.out.println("The " + user.getUsername() + " user exists");
        } else {
            mockMvc.perform(MockMvcRequestBuilders.multipart("/registration").file(file)
                            .with(MockMvcRequestBuilderUtils.form(user)))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

    }

    /* Создание клиента без ошибок */
    private User createUser(String name) {
        User user = new User();
        user.setUsername(name);
        user.setPassword(name);
        user.setPasswordRe(name);
        user.setFirstName(name);
        user.setLastName(name);
        user.setGender("мужской");
        user.setAge("25");
        user.setPassportSeries("2222");
        user.setPassportNumber("333333");
        user.setPhoneNumber("79850000000");

        return user;
    }

    /* Тестируем проверку авторизации, если клиент правильно ввел регистрационные данные, то
     * его должно перебрасывать на "/login"
     * Ожидаемый статус ответа: 200 */
    @Test
    public void PageRegistrationError() throws Exception {
        User user = createUser("Max"); // Имя пользователя меньше 4 символов
        MockMultipartFile file = new MockMultipartFile("file", "Maxx.pdf", "application/pdf", "information".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/registration").file(file)
                        .with(MockMvcRequestBuilderUtils.form(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration.html"));
    }


    /* Тестируем проверку поиска клиента, если клиент правильно ввел данные для поиска и клиент нашелся, то
     * он остается на той же странице и ему демонстрируется искомый клиент
     * Ожидаемый статус ответа: 200 */
    @Test
    @WithMockUser
    public void pageSearchFindUserSuccess() throws Exception {
        SearchUser user = new SearchUser();
        String userFirstName = "admin";
        user.setPassportSeries("1111");
        user.setPassportNumber("111111");

        mockMvc.perform(post("/search").param("passportSeries", user.getPassportSeries())
                                                .param("passportNumber", user.getPassportNumber()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("search.html"))
                .andExpect(content().string(containsString(userFirstName)));
    }


    /* Тестируем проверку поиска клиента, если клиент неправильно ввел данные для поиска, то
     * он остается на той же странице и ему демонстрируется ошибка
     * Ожидаемый статус ответа: 200 */
    @Test
    @WithMockUser
    public void pageSearchFindUserError() throws Exception {
        SearchUser user = new SearchUser();
        user.setPassportSeries("111");  // серия заполнена на 3/4
        user.setPassportNumber("11111");    // номер заполнен на 5/6

        mockMvc.perform(post("/search").param("passportSeries", user.getPassportSeries())
                        .param("passportNumber", user.getPassportNumber()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("search.html"))
                .andExpect(content().string(containsString("В серии паспорта должно быть 4 цифры")))
                .andExpect(content().string(containsString("В номере паспорта должно быть 6 цифры")));
    }
}
