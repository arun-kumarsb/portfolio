package com.arun.portfolio.service;

import com.arun.portfolio.dto.ContactMessageResponse;
import com.arun.portfolio.dto.ContactRequest;
import com.arun.portfolio.dto.ContactResponse;
import com.arun.portfolio.entity.ContactMessage;
import com.arun.portfolio.exception.ResourceNotFoundException;
import com.arun.portfolio.repository.ContactMessageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Layer handling business logic for Contact submissions and message management.
 */
@Service
@Transactional
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    /**
     * Validate and persist contact message into the database.
     */
    public ContactResponse saveMessage(ContactRequest request) {
        ContactMessage message = new ContactMessage(
                request.getName().trim(),
                request.getEmail().trim(),
                request.getMessage().trim(),
                LocalDateTime.now()
        );

        ContactMessage saved = contactMessageRepository.save(message);

        return new ContactResponse(
                saved.getId(),
                "SUCCESS",
                "Thank you, " + saved.getName() + "! Your message has been received successfully.",
                saved.getCreatedAt()
        );
    }

    /**
     * Retrieve all messages ordered by newest first (Admin only).
     */
    @Transactional(readOnly = true)
    public List<ContactMessageResponse> getAllMessages() {
        return contactMessageRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ContactMessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Delete a contact message by ID (Admin only).
     */
    public void deleteMessage(Long id) {
        if (!contactMessageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact message not found with id: " + id);
        }
        contactMessageRepository.deleteById(id);
    }
}
