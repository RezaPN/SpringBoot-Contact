package com.adira.contact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adira.contact.entity.Contact;
import com.adira.contact.entity.User;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Additional method to find contacts by user ID and contact IDs
    Optional<Contact> findByIdAndUser(Long userId, User user);

    @Query("SELECT c FROM Contact c WHERE " +
            "(:userId IS NULL OR c.user.id = :userId) AND " +
            "(:bankName IS NULL OR c.bankName ILIKE %:bankName%) AND " +
            "(:accountNumber IS NULL OR c.accountNumber ILIKE %:accountNumber%) AND " +
            "(:contactName IS NULL OR c.contactName ILIKE %:contactName%)")
    List<Contact> findByCriteria(
            @Param("userId") Long userId,
            @Param("bankName") String bankName,
            @Param("accountNumber") String accountNumber,
            @Param("contactName") String contactName);

    List<Contact> findByUserId(Long userId);

    Optional<Contact> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);
}
