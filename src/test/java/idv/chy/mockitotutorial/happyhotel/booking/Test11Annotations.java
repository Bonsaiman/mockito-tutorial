package idv.chy.mockitotutorial.happyhotel.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Test11Annotations {

    @InjectMocks
    private BookingService bookingService;
    @Mock
    private PaymentService paymentServiceMock;

    @Mock
    private RoomService roomServiceMock;

    @Spy
    private BookingDAO bookingDAOMock;

    @Mock
    private MailSender mailSenderMock;
    @Captor
    private ArgumentCaptor<Double> doubleCaptor;



    @Test
    void should_PayCorrectPrice_When_InputOK() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);

        // act
        bookingService.makeBooking(bookingRequest);

        // assert
        verify(paymentServiceMock, times(1)).pay(eq(bookingRequest), doubleCaptor.capture());
        double capturedArgument = doubleCaptor.getValue();

        assertEquals(400.0, capturedArgument);
    }


    @Test
    void should_PayCorrectPrice_When_MultipleCalls() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);

        BookingRequest bookingRequest2 = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 13),
                2,
                true);

        List<Double> expectedValues = Arrays.asList(400.0, 100.0);

        // act
        bookingService.makeBooking(bookingRequest);
        bookingService.makeBooking(bookingRequest2);

        // assert
        verify(paymentServiceMock, times(2)).pay(any(), doubleCaptor.capture());
        List<Double> capturedArguments = doubleCaptor.getAllValues();

        assertEquals(expectedValues, capturedArguments);
    }

}
