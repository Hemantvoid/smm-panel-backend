package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.SupportTicket;
import com.example.demo.repository.SupportTicketRepository;

@RestController
@RequestMapping("/support")
@CrossOrigin("*")
public class SupportController {

    @Autowired
    private SupportTicketRepository repo;

    @PostMapping
    public SupportTicket create(

            @RequestBody
            SupportTicket ticket,

            Authentication auth

    ) {

        ticket.setUsername(
                auth.getName()
        );

        ticket.setStatus(
                "OPEN"
        );

        ticket.setCreatedAt(
                LocalDateTime.now()
        );

        return repo.save(ticket);
    }

    @GetMapping
    public List<SupportTicket> myTickets(
            Authentication auth
    ) {
    	System.out.println(auth);

        return repo.findByUsername(
                auth.getName()
        );
    }

    @GetMapping("/admin")
    public List<SupportTicket> allTickets() {

        return repo.findAll();
    }

    @PutMapping("/{id}/close")
    public SupportTicket close(
            @PathVariable Long id
    ) {

        SupportTicket ticket =
                repo.findById(id)
                        .orElseThrow();

        ticket.setStatus(
                "CLOSED"
        );

        return repo.save(ticket);
    }
}