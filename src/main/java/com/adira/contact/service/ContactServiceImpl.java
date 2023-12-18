package com.adira.contact.service;

import com.adira.contact.exception.CustomNotFoundException;
import com.adira.contact.pojo.Contact;
import com.adira.contact.repository.ContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    @Override
    public List<Contact> getContactsByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact updateContact(Long id, Contact contact) {
        Optional<Contact> existingContactOptional = contactRepository.findById(id);

        if (existingContactOptional.isPresent()) {
            Contact existingContact = existingContactOptional.get();
            existingContact.setAccountNumber(contact.getAccountNumber());
            existingContact.setBankName(contact.getBankName());
            existingContact.setContactName(contact.getContactName());
            existingContact.setUser(contact.getUser());

            return contactRepository.save(existingContact);
        } else {
            throw new CustomNotFoundException("Contact with id " + id + " not found");
        }
    }

    @Override
    public boolean deleteContact(Long id) {

        try {
            contactRepository.deleteById(id);
            return true; // or false
        } catch (Exception e) {
            // Handle any exceptions or errors during deletion
            return false;
        }

    }

    @Override
    public boolean doesContactExistById(Long id) {
        return contactRepository.existsById(id);
    }
}
