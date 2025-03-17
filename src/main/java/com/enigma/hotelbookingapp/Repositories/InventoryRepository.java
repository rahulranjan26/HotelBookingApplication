package com.enigma.hotelbookingapp.Repositories;

import com.enigma.hotelbookingapp.DTO.InventoryDTO;
import com.enigma.hotelbookingapp.Entities.Hotel;
import com.enigma.hotelbookingapp.Entities.Inventory;
import com.enigma.hotelbookingapp.Entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);


    @Query("""
             SELECT DISTINCT i.hotel
             from Inventory i
             WHERE i.city=:city
             AND i.date BETWEEN :startDate AND :endDate
             AND i.closed=FALSE
             AND (i.totalCount-i.bookedCount-i.reservedCount)>=:roomsCount
             GROUP BY i.hotel,i.room
             HAVING COUNT(i.date)=:dateCount
            """
    )
    Page<Hotel> findHotelBySearchParams(
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    @Query("""
             SELECT  i
             from Inventory i
             WHERE i.room.roomId=:roomId
             AND i.date BETWEEN :startDate AND :endDate
             AND (i.totalCount-i.bookedCount-i.reservedCount)>=:roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("roomsCount") Integer roomsCount

    );


    @Query("""
             SELECT  i
             from Inventory i
             WHERE i.room.roomId=:roomId
             AND i.date BETWEEN :startDate AND :endDate
             AND (i.totalCount-i.bookedCount)>=:roomsCount
             AND i.closed=false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("roomsCount") Integer roomsCount

    );


    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.reservedCount=i.reservedCount - :roomsCount,
            i.bookedCount=i.bookedCount + :roomsCount
            WHERE i.room.roomId=:roomId
            AND i.date BETWEEN :startDate AND :endDate
            AND (i.totalCount-i.bookedCount)>=:roomsCount
            AND i.reservedCount>= :roomsCount
            AND i.closed=false
            """
    )
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("roomsCount") Integer roomsCount);


    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.bookedCount=i.bookedCount + :roomsCount
            WHERE i.room.roomId=:roomId
            AND i.date BETWEEN :startDate AND :endDate
            AND (i.totalCount-i.bookedCount)>=:roomsCount
            AND i.closed=false
            """
    )
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate,
                       @Param("roomsCount") Integer roomsCount);


    @Query(
            """
                    select i
                    from Inventory i
                    where i.room.roomId=:roomId
                    and i.date between :startDate and :endDate
                    """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockUpdateInventory(@Param("roomId") Long roomId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("closed") Boolean closed
    );

    @Modifying
    @Query("""
                            UPDATE Inventory i
                            SET i.surgeFactor=:surgeFactor ,
                            i.closed=:closed
                            WHERE i.room.roomId=:roomId
                            AND i.date BETWEEN :startDate AND :endDate
            
            """)
    void findAndUpdateLockInventory(@Param("roomId") Long roomId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("closed") Boolean closed,
                                    @Param("surgeFactor") BigDecimal surgeFactor
    );
    
    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDateTime dateAfter, LocalDateTime dateBefore);

    List<InventoryDTO> findByRoomOrderByDate(Room room);
}
