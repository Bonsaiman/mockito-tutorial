package idv.chy.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Test08Spies {

    private BookingService bookingService;
    private PaymentService paymentServiceMock;
    private RoomService roomServiceMock;
    private BookingDAO bookingDAOMock;
    private MailSender mailSenderMock;

    @BeforeEach
    void setup() {

        this.paymentServiceMock = mock(PaymentService.class);
        this.roomServiceMock = mock(RoomService.class);
        this.bookingDAOMock = Mockito.spy(BookingDAO.class);
        this.mailSenderMock = mock(MailSender.class);

        this.bookingService = new BookingService(paymentServiceMock, roomServiceMock, bookingDAOMock, mailSenderMock);
    }


    @Test
    void should_MakeBooking_When_InputOK() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);

        // act
        String bookingId = bookingService.makeBooking(bookingRequest);

        // assert
        verify(bookingDAOMock).save(bookingRequest);
        System.out.println("bookingId = " + bookingId);
    }

    @Test
    void should_CancelBooking_When_InputOK() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);
        bookingRequest.setRoomId("1.3");
        String bookingId = "1";

        doReturn(bookingRequest).when(bookingDAOMock).get(bookingId);

        // act
        bookingService.cancelBooking(bookingId);


        // assert
    }

}
