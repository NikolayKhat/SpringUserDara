package com.example.springuserdata.controller;

import com.example.springuserdata.models.User;
import com.example.springuserdata.service.AccountsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AllControllerTest {

    @MockBean
    AccountsService accountsService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${upload.path}")
    private String uploadPath;

    /* Тестируем вызов страницы home */
    @Test
    public void homeTest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    /* Тестируем вызов страницу login */
    @Test
    public void loginTest() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("authorization"));
    }

    /* Тестируем вызов страницу registration */
    @Test
    public void registrationGetTest() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("user", instanceOf(User.class)));
    }
}
