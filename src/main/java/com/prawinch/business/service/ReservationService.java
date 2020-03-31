package com.prawinch.business.service;

import com.prawinch.business.domain.RoomReservation;
import com.prawinch.data.entity.Guest;
import com.prawinch.data.entity.Reservation;
import com.prawinch.data.entity.Room;
import com.prawinch.data.repository.GuestRepository;
import com.prawinch.data.repository.ReservationRepository;
import com.prawinch.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReservationService {
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;


    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date) {
       List<RoomReservation> reservationsList = new ArrayList<>();
        Iterable<Room> rooms = this.roomRepository.findAll();

        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });

        Iterable<Reservation> reservations = this.reservationRepository.findByDate((java.sql.Date) date);

        reservations.forEach(reservation -> {
            Optional<Guest> guestResponse = this.guestRepository.findById(reservation.getGuestId());
            if (guestResponse.isPresent()) {
                Guest guest = guestResponse.get();
                RoomReservation roomReservation = roomReservationMap.get(reservation.getId());
                roomReservation.setDate(date);
                roomReservation.setFirstName(guest.getFirstName());
                roomReservation.setLastName(guest.getLastName());
                roomReservation.setGuestId(guest.getId());
            }
        });

        for (Long roomId: roomReservationMap.keySet()) {
            reservationsList.add(roomReservationMap.get(roomId));
        }
        return reservationsList;

    }

}
