package com.toogoodtogo.web.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toogoodtogo.application.security.SignService;
import com.toogoodtogo.domain.security.RefreshTokenRepository;
import com.toogoodtogo.domain.shop.ShopRepository;
import com.toogoodtogo.domain.shop.product.ProductRepository;
import com.toogoodtogo.domain.user.User;
import com.toogoodtogo.domain.user.UserRepository;
import com.toogoodtogo.web.users.sign.TokenDto;
import com.toogoodtogo.web.users.sign.UserLoginReq;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SignService signService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private User user;

    private TokenDto token;

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();

        user = userRepository.save(User.builder()
                .email("email@email.com")
                .password(passwordEncoder.encode("password"))
                .name("name")
                .phone("010-0000-0000")
                .role("ROLE_USER")
                .build());

        token = signService.login(UserLoginReq.builder().email("email@email.com").password("password").build());
    }

    @AfterEach
    public void setDown() {
        signService.logout(user.getId());
        productRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAllInBatch();
    }

    @Test
    public void findUserInfo() throws Exception {
        //given
        ResultActions actions = mockMvc.perform(get("/api/me")
                .header("Authorization", "Bearer " + token.getAccessToken())
                .param("lang", "ko"));
        //then
        //when
        actions
                .andDo(print())
                .andDo(document("users/me",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("data"),
                                fieldWithPath("data.id").description("user id"),
                                fieldWithPath("data.email").description("user email"),
                                fieldWithPath("data.name").description("user name"),
                                fieldWithPath("data.phone").description("user phone"),
                                fieldWithPath("data.role").description("user role")
                        )
                ))
                .andExpect(jsonPath("$.data.email").value("email@email.com"))
                .andExpect(jsonPath("$.data.name").value("name"));
    }

    @Test
    public void updateUser() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(UserUpdateReq.builder()
                .password("new_password")
                .phone("010-1234-5678")
                .build());

        //when
        ResultActions actions = mockMvc.perform(patch("/api/me")
                .content(object)
                .header("Authorization", "Bearer " + token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andDo(document("users/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("data").description("data"),
                                fieldWithPath("data.id").description("user id"),
                                fieldWithPath("data.email").description("user email"),
                                fieldWithPath("data.name").description("user name"),
                                fieldWithPath("data.phone").description("user phone"),
                                fieldWithPath("data.role").description("user role")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"));
    }
}