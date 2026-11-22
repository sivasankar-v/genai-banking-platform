package com.fincore.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end checks through the real security filter chain and database layer:
 * two customers (Alice, Bob) and an admin.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankingApiTest {

    @Autowired
    MockMvc mvc;

    String alice, bob, admin;
    String aliceCustomerId, bobCustomerId;
    String aliceAccountId, aliceAccountNo, bobAccountId, bobAccountNo;

    @BeforeAll
    void setUp() throws Exception {
        alice = registerAndLogin("Alice@Example.com", "9876500001");
        bob = registerAndLogin("bob@example.com", "9876500002");
        admin = login("admin@test.local", "AdminPass123");

        aliceCustomerId = createCustomer(alice, "Alice", "9876500001");
        bobCustomerId = createCustomer(bob, "Bob", "9876500002");

        String a = openAccount(alice, aliceCustomerId, "1000.00");
        aliceAccountId = JsonPath.read(a, "$.id");
        aliceAccountNo = JsonPath.read(a, "$.accountNumber");
        String b = openAccount(bob, bobCustomerId, "0.00");
        bobAccountId = JsonPath.read(b, "$.id");
        bobAccountNo = JsonPath.read(b, "$.accountNumber");
    }

    @Test
    void requestsWithoutValidTokenAre401() throws Exception {
        mvc.perform(get("/api/v1/accounts/" + aliceAccountId)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/accounts/" + aliceAccountId).header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIs401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bob@example.com\",\"password\":\"WrongPass999\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customersCannotTouchOtherCustomersData() throws Exception {
        perform(bob, get("/api/v1/accounts/" + aliceAccountId)).andExpect(status().isForbidden());
        perform(bob, get("/api/customers/" + aliceCustomerId)).andExpect(status().isForbidden());
        perform(bob, get("/api/v1/transactions/account/" + aliceAccountId)).andExpect(status().isForbidden());
        perform(bob, post("/api/v1/transactions/withdraw"),
                "{\"accountNumber\":\"" + aliceAccountNo + "\",\"amount\":10}").andExpect(status().isForbidden());
        perform(bob, post("/api/v1/transactions/transfer"),
                "{\"fromAccount\":\"" + aliceAccountNo + "\",\"toAccount\":\"" + bobAccountNo + "\",\"amount\":10}")
                .andExpect(status().isForbidden());
        perform(bob, post("/api/v1/accounts"),
                "{\"customerId\":\"" + aliceCustomerId + "\",\"accountType\":\"SAVINGS\",\"currency\":\"INR\",\"initialDeposit\":0}")
                .andExpect(status().isForbidden());
    }

    @Test
    void adminOnlyEndpointsRejectCustomers() throws Exception {
        perform(alice, get("/api/customers")).andExpect(status().isForbidden());
        perform(admin, get("/api/customers")).andExpect(status().isOk());
        perform(alice, get("/api/v1/loans")).andExpect(status().isForbidden());
        perform(alice, patch("/api/v1/accounts/" + aliceAccountId + "/freeze")).andExpect(status().isForbidden());
    }

    @Test
    void meReturnsOwnProfile() throws Exception {
        perform(alice, get("/api/customers/me")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(aliceCustomerId));
    }

    @Test
    void transferMovesMoneyAndOverdraftIsRejected() throws Exception {
        perform(alice, post("/api/v1/transactions/transfer"),
                "{\"fromAccount\":\"" + aliceAccountNo + "\",\"toAccount\":\"" + bobAccountNo + "\",\"amount\":300}")
                .andExpect(status().isCreated());
        perform(alice, get("/api/v1/accounts/" + aliceAccountId)).andExpect(jsonPath("$.balance").value(700.0));
        perform(bob, get("/api/v1/accounts/" + bobAccountId)).andExpect(jsonPath("$.balance").value(300.0));

        // Opening deposit is recorded too, so history matches the balance.
        perform(alice, get("/api/v1/transactions/account/" + aliceAccountId))
                .andExpect(jsonPath("$.length()").value(2));

        perform(alice, post("/api/v1/transactions/withdraw"),
                "{\"accountNumber\":\"" + aliceAccountNo + "\",\"amount\":999999}")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void transferToFrozenAccountIsRejected() throws Exception {
        String c = openAccount(bob, bobCustomerId, "0.00");
        String id = JsonPath.read(c, "$.id");
        String no = JsonPath.read(c, "$.accountNumber");
        perform(admin, patch("/api/v1/accounts/" + id + "/freeze")).andExpect(status().isOk());

        perform(alice, post("/api/v1/transactions/transfer"),
                "{\"fromAccount\":\"" + aliceAccountNo + "\",\"toAccount\":\"" + no + "\",\"amount\":1}")
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Destination account is not active"));
    }

    @Test
    void loanFlowUsesBankRateAndOnlyAdminDecidesOnce() throws Exception {
        String loan = perform(alice, post("/api/v1/loans/apply"),
                "{\"customerId\":\"" + aliceCustomerId + "\",\"loanType\":\"PERSONAL\",\"principal\":100000,\"tenureMonths\":12}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.interest").value(12.0))
                .andExpect(jsonPath("$.emi").value(8884.88))
                .andReturn().getResponse().getContentAsString();
        String loanId = JsonPath.read(loan, "$.id");

        perform(alice, patch("/api/v1/loans/" + loanId + "/approve")).andExpect(status().isForbidden());
        perform(admin, patch("/api/v1/loans/" + loanId + "/approve")).andExpect(status().isOk());
        perform(admin, patch("/api/v1/loans/" + loanId + "/reject")).andExpect(status().isUnprocessableContent());
    }

    // --- helpers ---

    private ResultActions perform(String token, MockHttpServletRequestBuilder req) throws Exception {
        return mvc.perform(req.header("Authorization", "Bearer " + token));
    }

    private ResultActions perform(String token, MockHttpServletRequestBuilder req, String json) throws Exception {
        return perform(token, req.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String registerAndLogin(String email, String mobile) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
                {"firstName":"Test","lastName":"User","email":"%s","password":"Password123","mobileNumber":"%s"}
                """.formatted(email, mobile))).andExpect(status().isCreated());
        // Login with a differently-cased email must still work.
        return login(email.toUpperCase(), "Password123");
    }

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }

    private String createCustomer(String token, String name, String mobile) throws Exception {
        String body = perform(token, post("/api/customers"),
                "{\"firstName\":\"%s\",\"lastName\":\"Tester\",\"mobile\":\"%s\"}".formatted(name, mobile))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    private String openAccount(String token, String customerId, String deposit) throws Exception {
        return perform(token, post("/api/v1/accounts"),
                "{\"customerId\":\"%s\",\"accountType\":\"SAVINGS\",\"currency\":\"inr\",\"initialDeposit\":%s}"
                        .formatted(customerId, deposit))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }
}
