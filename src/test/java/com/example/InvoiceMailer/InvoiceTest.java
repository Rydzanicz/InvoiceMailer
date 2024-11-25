package com.example.InvoiceMailer;

import com.example.InvoiceMailer.model.Invoice;
import com.example.InvoiceMailer.model.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvoiceTest {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Test
    public void testShouldBePositive() {
        //given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        //when
        final Invoice invoice = new Invoice(1, "Jan Kowalski", "popowicka 68", "jan.kowalski@example.com", null, ordersDate, orders);

        //then
        assertNotNull(invoice);
        assertEquals("FV/000000001/2024", invoice.getInvoiceId());
        assertEquals("Jan Kowalski", invoice.getBuyerName());
        assertEquals("popowicka 68", invoice.getBuyerAddress());
        assertEquals("jan.kowalski@example.com", invoice.getBuyerAddressEmail());
    }

    @Test
    public void testShouldThrowExceptionForInvalidInvoiceNumber() {
        // given
        final int invoiceNumber = 0;
        final String buyerName = "Jan Kowalski";
        final String buyerAddress = "popowicka 68";
        final String buyerEmail = "jan.kowalski@example.com";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Invoice(invoiceNumber, buyerName, buyerAddress, buyerEmail, null, ordersDate, orders);
        });
        assertEquals("Invoice ID cannot be 0 or less than 0.", thrown.getMessage());
    }

    @Test
    public void testThrowWhenNameIsNull() {
        //given
        final int invoiceNumber = 1;
        final String buyerAddress = "popowicka 68";
        final String buyerEmail = "jan.kowalski@example.com";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(invoiceNumber, null, buyerAddress, buyerEmail, null, ordersDate, orders),
                     "Name cannot be null or empty.");
    }

    @Test
    public void testThrowWhenAddressIsNull() {
        //given
        final int invoiceNumber = 1;
        final String buyerName = "Jan Kowalski";
        final String buyerEmail = "jan.kowalski@example.com";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(invoiceNumber, buyerName, null, buyerEmail, null, ordersDate, orders),
                     "Name cannot be null or empty.");
    }

    @Test
    public void testThrowWhenEmailIsNull() {
        //given
        final int invoiceNumber = 1;
        final String buyerName = "Jan Kowalski";
        final String buyerAddress = "popowicka 68";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(invoiceNumber, buyerName, buyerAddress, null, null, ordersDate, orders),
                     "Email cannot be null or empty.");
    }

    @Test
    public void testThrowWhenOrdersDateIsNull() {
        //given
        final int invoiceNumber = 1;
        final String buyerName = "Jan Kowalski";
        final String buyerAddress = "popowicka 68";
        final String buyerEmail = "jan.kowalski@example.com";
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(invoiceNumber, buyerName, buyerAddress, buyerEmail, null, "", orders),
                     "List of Order cannot be null or empty.");
    }

    @Test
    public void testThrowWhenOrderIsEmpty() {
        //given
        final int invoiceNumber = 1;
        final String buyerName = "Jan Kowalski";
        final String buyerAddress = "popowicka 68";
        final String buyerEmail = "jan.kowalski@example.com";
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);
        final List<Order> orders = new ArrayList<>();

        //when
        //then
        assertThrows(IllegalArgumentException.class,
                     () -> new Invoice(invoiceNumber, buyerName, buyerAddress, buyerEmail, null, ordersDate, orders),
                     "List of Order cannot be null or empty.");
    }


    @Test
    public void testThrowWhenInvoiceEntityIsNull() {
        //given
        //when
        //then
        assertThrows(NullPointerException.class, () -> new Invoice(null), "InvoiceEntity cannot be null.");
    }

    @Test
    public void testShouldThrowExceptionForInvalidInvoiceIdFormat() {
        // given
        final String invalidInvoiceId = "FV/00001/22";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Invoice.validateInvoiceId(invalidInvoiceId);
        });
        assertEquals("Invalid Invoice ID format. Correct format: FV/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testValidateInvalidInvoiceId() {
        // given
        final String invalidInvoiceId = "FV/1/24";

        //when
        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Invoice.validateInvoiceId(invalidInvoiceId);
        });
        assertEquals("Invalid Invoice ID format. Correct format: FV/{number}/{year}, e.g., FV/001/2024", thrown.getMessage());
    }

    @Test
    public void testShouldIncreaseInvoiceNumber() {
        // given
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("Produkt A", "Opis A", 1, 100.0));
        final LocalDateTime ordersDate = LocalDateTime.parse("2024-01-01 14:30:00", formatter);

        final Invoice invoice = new Invoice(900000000,
                                            "Jan Kowalski",
                                            "popowicka 68",
                                            "jan.kowalski@example.com",
                                            null,
                                            ordersDate,
                                            orders);

        // when
        int extractedInvoiceNumber = invoice.extractAndIncreaseInvoiceNumber();

        // then
        assertEquals(900000001, extractedInvoiceNumber);
    }
}

