package com.company.enroller.controllers;

import java.util.Collection;
import java.util.List;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping (value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return ResponseEntity.ok(meetings);
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingById(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping (value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(
            @RequestBody Meeting meeting,
            @RequestParam(value = "key", required = false) String key)

    {
        if (meetingService.findByTitle(meeting.getTitle()) != null) {
            return new ResponseEntity<String>(
                    "Unable to create. A meeting with title " + meeting.getTitle() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping (value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setTitle(updatedMeeting.getTitle());
        meeting.setDescription(updatedMeeting.getDescription());
        meeting.setDate(updatedMeeting.getDate());
        meetingService.update(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByTitle(@RequestParam("title") String title) {
        Meeting meeting = meetingService.findByTitle(title);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value ="/{id}/participants/{login}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>("Participant not found", HttpStatus.NOT_FOUND);
        }
        if (meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("Participant already added to meeting", HttpStatus.CONFLICT);
        }
        meeting.getParticipants().add(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping (value ="/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity<>("Participant not found", HttpStatus.NOT_FOUND);
        }
        if (!meeting.getParticipants().contains(participant)) {
            return new ResponseEntity<>("Participant not in meeting", HttpStatus.NOT_FOUND);
        }
        meeting.getParticipants().remove(participant);
        meetingService.update(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping (value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipantsFromMeeting(@PathVariable("id") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting.getParticipants(), HttpStatus.OK);
    }
}