package idv.chy.mockitotutorial.happyhotel.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class BookingServiceTest {

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

//        System.out.println("List returned " + roomServiceMock.getAvailableRooms());
//        System.out.println("Object returned " + roomServiceMock.findAvailableRoomId(null));
//        System.out.println("Primitive returned " + roomServiceMock.getRoomCount());
    }


    // Step 1 first mock
    @Test
    @DisplayName("Step 1 first mock")
    void should_CalculateCorrectPrice_When_CorrectInput() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                false);

        double expected = 4 * 2 * 50.0;

        // act
        double actual = bookingService.calculatePrice(bookingRequest);

        // assert
        assertEquals(expected, actual);
    }


    // Step 2 default return values
    @Test
    @DisplayName("Step 2 default return values")
    void should_CountAvailablePlaces() {
        // arrange
        int expected = 0;

        // act
        int actual = bookingService.getAvailablePlaceCount();

        // assert
        assertEquals(expected, actual);
    }


    // Step 3 returning custom values
    @Test
    @DisplayName("Step 3.1 returning custom values")
    void should_CountAvailablePlaces_When_OneRoomAvailable() {
        // arrange
        when(this.roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 2)));

        int expected = 2;

        // act
        int actual = bookingService.getAvailablePlaceCount();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Step 3.2 returning custom values")
    void should_CountAvailablePlaces_When_MultipleRoomsAvailable() {
        // arrange
        List<Room> rooms = Arrays.asList(new Room("Room 1", 2), new Room("Room 2", 5));
        when(this.roomServiceMock.getAvailableRooms())
                .thenReturn(rooms);

        int expected = 7;

        // act
        int actual = bookingService.getAvailablePlaceCount();

        // assert
        assertEquals(expected, actual);
    }


    // Step 4 multiple then return calls
    @Test
    @DisplayName("Step 4 multiple then return calls")
    void should_CountAvailablePlaces_When_CalledMultipleTimes() {
        // arrange
        when(this.roomServiceMock.getAvailableRooms())
                .thenReturn(Collections.singletonList(new Room("Room 1", 2)))
                .thenReturn(Collections.emptyList());

        int expectedFirstCall = 2;
        int expectedSecondCall = 0;


        // act
        int actualFirst = bookingService.getAvailablePlaceCount();
        int actualSecond = bookingService.getAvailablePlaceCount();


        // assert
        assertAll(
                () -> assertEquals(expectedFirstCall, actualFirst),
                () -> assertEquals(expectedSecondCall, actualSecond)
        );

    }


    // Step 5 throwing exception
    @Test
    @DisplayName("Step 5 throwing exception")
    void should_ThrowException_When_NoRoomAvailable() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                false);

        when(this.roomServiceMock.findAvailableRoomId(bookingRequest))
                .thenThrow(BusinessException.class);

        // act
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // assert
        assertThrows(BusinessException.class, executable);
    }

    // Step 6 matchers
    @Test
    @DisplayName("Step 6 matchers")
    void should_NotCompleteBooking_When_PriceTooHigh() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "2",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                true);

        when(this.paymentServiceMock.pay(any(), eq(400.0)))
                .thenThrow(BusinessException.class);

        // act
        Executable executable = () -> bookingService.makeBooking(bookingRequest);

        // assert
        assertThrows(BusinessException.class, executable);
    }


    // Step 7 verifying behavior
    @Test
    @DisplayName("Step 7.1 verifying behavior")
    void should_InvokePayment_When_Prepaid() {
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
        verify(paymentServiceMock, times(1)).pay(bookingRequest, 400.0);
        verifyNoMoreInteractions(paymentServiceMock);

    }
    @Test
    @DisplayName("Step 7.2 verifying behavior")
    void should_NOTInvokePayment_When_NotPrepaid() {
        // arrange
        BookingRequest bookingRequest = new BookingRequest(
                "1",
                LocalDate.of(2022, 12, 12),
                LocalDate.of(2022, 12, 16),
                2,
                false);

        // act
        bookingService.makeBooking(bookingRequest);

        // assert
        verify(paymentServiceMock, never()).pay(any(), anyDouble());

    }
}