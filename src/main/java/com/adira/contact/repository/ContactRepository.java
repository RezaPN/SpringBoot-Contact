package com.adira.contact.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.adira.contact.pojo.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {


    List<Contact> findByUserId(Long userId);
    Optional<Contact> findByAccountNumber(String accountNumber);
    List<Contact> findByBankName(String bankName);
    List<Contact> findByContactName(String contactName);
    List<Contact> findByUserIdAndBankName(Long userId, String bankName);

 
    Optional<Contact> findById(Long id);
    List<Contact> findAll();
    List<Contact> findAllById(Iterable<Long> ids);

    void deleteById(Long id);
    void delete(Contact entity);
    void deleteAll();
}
