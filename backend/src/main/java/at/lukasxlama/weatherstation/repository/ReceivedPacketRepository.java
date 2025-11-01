package at.lukasxlama.weatherstation.repository;

import at.lukasxlama.weatherstation.models.dao.ReceivedPacket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReceivedPacketRepository extends JpaRepository<ReceivedPacket, Long>
{
    List<ReceivedPacket> findByTimestampBetweenOrderByTimestampAsc(OffsetDateTime from, OffsetDateTime to);

    java.util.Optional<ReceivedPacket> findTopByOrderByTimestampDesc();
}
