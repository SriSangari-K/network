package com.college.networkmgmt.repository;

import com.college.networkmgmt.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByIpAddress(String ipAddress);

    boolean existsByIpAddress(String ipAddress);

    boolean existsByIpAddressAndIdNot(String ipAddress, Long id);

    long countByStatus(String status);

    List<Device> findByLabId(Long labId);

    @Query("SELECT d FROM Device d WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(d.deviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(d.ipAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(d.macAddress) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:deviceType IS NULL OR :deviceType = '' OR d.deviceType = :deviceType) " +
           "AND (:status IS NULL OR :status = '' OR d.status = :status) " +
           "AND (:labId IS NULL OR d.lab.id = :labId) " +
           "ORDER BY d.id DESC")
    List<Device> searchDevices(
            @Param("keyword") String keyword,
            @Param("deviceType") String deviceType,
            @Param("status") String status,
            @Param("labId") Long labId
    );

    List<Device> findTop10ByOrderByCreatedAtDesc();
}
