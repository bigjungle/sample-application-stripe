package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.StripeApp;

import com.mycompany.myapp.domain.PaypalCompletedPayments;
import com.mycompany.myapp.repository.PaypalCompletedPaymentsRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PaypalCompletedPaymentsResource REST controller.
 *
 * @see PaypalCompletedPaymentsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StripeApp.class)
public class PaypalCompletedPaymentsResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_ID_PAYMENT = "AAAAAAAAAA";
    private static final String UPDATED_ID_PAYMENT = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final Float DEFAULT_AMOUNT = 0F;
    private static final Float UPDATED_AMOUNT = 1F;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private PaypalCompletedPaymentsRepository paypalCompletedPaymentsRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPaypalCompletedPaymentsMockMvc;

    private PaypalCompletedPayments paypalCompletedPayments;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PaypalCompletedPaymentsResource paypalCompletedPaymentsResource = new PaypalCompletedPaymentsResource(paypalCompletedPaymentsRepository);
        this.restPaypalCompletedPaymentsMockMvc = MockMvcBuilders.standaloneSetup(paypalCompletedPaymentsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaypalCompletedPayments createEntity(EntityManager em) {
        PaypalCompletedPayments paypalCompletedPayments = new PaypalCompletedPayments()
            .date(DEFAULT_DATE)
            .idPayment(DEFAULT_ID_PAYMENT)
            .currency(DEFAULT_CURRENCY)
            .amount(DEFAULT_AMOUNT)
            .email(DEFAULT_EMAIL)
            .name(DEFAULT_NAME)
            .status(DEFAULT_STATUS);
        return paypalCompletedPayments;
    }

    @Before
    public void initTest() {
        paypalCompletedPayments = createEntity(em);
    }

    @Test
    @Transactional
    public void createPaypalCompletedPayments() throws Exception {
        int databaseSizeBeforeCreate = paypalCompletedPaymentsRepository.findAll().size();

        // Create the PaypalCompletedPayments
        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isCreated());

        // Validate the PaypalCompletedPayments in the database
        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeCreate + 1);
        PaypalCompletedPayments testPaypalCompletedPayments = paypalCompletedPaymentsList.get(paypalCompletedPaymentsList.size() - 1);
        assertThat(testPaypalCompletedPayments.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPaypalCompletedPayments.getIdPayment()).isEqualTo(DEFAULT_ID_PAYMENT);
        assertThat(testPaypalCompletedPayments.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPaypalCompletedPayments.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testPaypalCompletedPayments.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPaypalCompletedPayments.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPaypalCompletedPayments.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createPaypalCompletedPaymentsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paypalCompletedPaymentsRepository.findAll().size();

        // Create the PaypalCompletedPayments with an existing ID
        paypalCompletedPayments.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        // Validate the PaypalCompletedPayments in the database
        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setDate(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIdPaymentIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setIdPayment(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setCurrency(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setAmount(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setEmail(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setName(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = paypalCompletedPaymentsRepository.findAll().size();
        // set the field null
        paypalCompletedPayments.setStatus(null);

        // Create the PaypalCompletedPayments, which fails.

        restPaypalCompletedPaymentsMockMvc.perform(post("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPaypalCompletedPayments() throws Exception {
        // Initialize the database
        paypalCompletedPaymentsRepository.saveAndFlush(paypalCompletedPayments);

        // Get all the paypalCompletedPaymentsList
        restPaypalCompletedPaymentsMockMvc.perform(get("/api/paypal-completed-payments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paypalCompletedPayments.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].idPayment").value(hasItem(DEFAULT_ID_PAYMENT.toString())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getPaypalCompletedPayments() throws Exception {
        // Initialize the database
        paypalCompletedPaymentsRepository.saveAndFlush(paypalCompletedPayments);

        // Get the paypalCompletedPayments
        restPaypalCompletedPaymentsMockMvc.perform(get("/api/paypal-completed-payments/{id}", paypalCompletedPayments.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(paypalCompletedPayments.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.idPayment").value(DEFAULT_ID_PAYMENT.toString()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPaypalCompletedPayments() throws Exception {
        // Get the paypalCompletedPayments
        restPaypalCompletedPaymentsMockMvc.perform(get("/api/paypal-completed-payments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePaypalCompletedPayments() throws Exception {
        // Initialize the database
        paypalCompletedPaymentsRepository.saveAndFlush(paypalCompletedPayments);

        int databaseSizeBeforeUpdate = paypalCompletedPaymentsRepository.findAll().size();

        // Update the paypalCompletedPayments
        PaypalCompletedPayments updatedPaypalCompletedPayments = paypalCompletedPaymentsRepository.findById(paypalCompletedPayments.getId()).get();
        // Disconnect from session so that the updates on updatedPaypalCompletedPayments are not directly saved in db
        em.detach(updatedPaypalCompletedPayments);
        updatedPaypalCompletedPayments
            .date(UPDATED_DATE)
            .idPayment(UPDATED_ID_PAYMENT)
            .currency(UPDATED_CURRENCY)
            .amount(UPDATED_AMOUNT)
            .email(UPDATED_EMAIL)
            .name(UPDATED_NAME)
            .status(UPDATED_STATUS);

        restPaypalCompletedPaymentsMockMvc.perform(put("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPaypalCompletedPayments)))
            .andExpect(status().isOk());

        // Validate the PaypalCompletedPayments in the database
        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeUpdate);
        PaypalCompletedPayments testPaypalCompletedPayments = paypalCompletedPaymentsList.get(paypalCompletedPaymentsList.size() - 1);
        assertThat(testPaypalCompletedPayments.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPaypalCompletedPayments.getIdPayment()).isEqualTo(UPDATED_ID_PAYMENT);
        assertThat(testPaypalCompletedPayments.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPaypalCompletedPayments.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testPaypalCompletedPayments.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPaypalCompletedPayments.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPaypalCompletedPayments.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingPaypalCompletedPayments() throws Exception {
        int databaseSizeBeforeUpdate = paypalCompletedPaymentsRepository.findAll().size();

        // Create the PaypalCompletedPayments

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaypalCompletedPaymentsMockMvc.perform(put("/api/paypal-completed-payments")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(paypalCompletedPayments)))
            .andExpect(status().isBadRequest());

        // Validate the PaypalCompletedPayments in the database
        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePaypalCompletedPayments() throws Exception {
        // Initialize the database
        paypalCompletedPaymentsRepository.saveAndFlush(paypalCompletedPayments);

        int databaseSizeBeforeDelete = paypalCompletedPaymentsRepository.findAll().size();

        // Delete the paypalCompletedPayments
        restPaypalCompletedPaymentsMockMvc.perform(delete("/api/paypal-completed-payments/{id}", paypalCompletedPayments.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PaypalCompletedPayments> paypalCompletedPaymentsList = paypalCompletedPaymentsRepository.findAll();
        assertThat(paypalCompletedPaymentsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaypalCompletedPayments.class);
        PaypalCompletedPayments paypalCompletedPayments1 = new PaypalCompletedPayments();
        paypalCompletedPayments1.setId(1L);
        PaypalCompletedPayments paypalCompletedPayments2 = new PaypalCompletedPayments();
        paypalCompletedPayments2.setId(paypalCompletedPayments1.getId());
        assertThat(paypalCompletedPayments1).isEqualTo(paypalCompletedPayments2);
        paypalCompletedPayments2.setId(2L);
        assertThat(paypalCompletedPayments1).isNotEqualTo(paypalCompletedPayments2);
        paypalCompletedPayments1.setId(null);
        assertThat(paypalCompletedPayments1).isNotEqualTo(paypalCompletedPayments2);
    }
}
