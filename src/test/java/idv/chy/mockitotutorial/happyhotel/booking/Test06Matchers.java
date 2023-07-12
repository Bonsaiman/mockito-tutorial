package idv.chy.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Test06Matchers {

    private BookingService bookingService;
    private PaymentService paymentServiceMock;
    private RoomService roomServiceMock;
    private BookingDAO bookingDAOMock;
    private MailSender mailSenderMock;

    @BeforeEach
    void setup() {

        this.paymentServiceMock = mock(PaymentService.class);
        this.roomServiceMock = mock(RoomService.class);
        this.bookingDAOMock = mock(BookingDAO.class);
        this.mailSenderMock = mock(MailSender.class);

        this.bookingService = new BookingService(paymentServiceMock, roomServiceMock, bookingDAOMock, mailSenderMock);
    }


    @Test
    void should_NotCompleteBooking_When_PriceTooHigh() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "2",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);

        when(this.paymentServiceMock.pay(any(), anyDouble()))
                .thenThrow(BusinessException.class);

        // act
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // assert
        assertThrows(BusinessException.class, executable);
    }

}
