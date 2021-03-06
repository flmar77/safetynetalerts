package com.safetynetalerts.api.web.controller;

import com.googlecode.jmapper.JMapper;
import com.safetynetalerts.api.domain.model.Person;
import com.safetynetalerts.api.domain.service.PersonService;
import com.safetynetalerts.api.web.dto.MedicalRecordsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class MedicalRecordsCrudController {

    @Autowired
    private PersonService personService;

    @GetMapping("/medicalRecords")
    public List<MedicalRecordsDto> getAllMedicalRecordsDto() {
        log.info("request to get AllMedicalRecordsDto");

        List<Person> personList = personService.getAllPersons();

        return mapPersonsToMedicalRecordsDto(personList);
    }

    @GetMapping("/medicalRecords/{firstName}&{lastName}")
    public List<MedicalRecordsDto> getMedicalRecordsDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to get MedicalRecordsDto of firstName={} & lastName={}", firstName, lastName);

        List<Person> personList = personService.getPersonsByFirstNameAndLastName(firstName, lastName);

        return mapPersonsToMedicalRecordsDto(personList);
    }

    @PutMapping("/medicalRecords/{firstName}&{lastName}")
    public ResponseEntity<?> updateMedicalRecordsDto(@PathVariable String firstName, @PathVariable String lastName, @RequestBody MedicalRecordsDto medicalRecordsDto) {
        log.info("request to put MedicalRecordsDto : {}", medicalRecordsDto);

        if (firstName == null || medicalRecordsDto.getFirstName() == null
                || firstName.equals("") || medicalRecordsDto.getFirstName().equals("") || !firstName.equals(medicalRecordsDto.getFirstName())
                || lastName == null || medicalRecordsDto.getLastName() == null
                || lastName.equals("") || medicalRecordsDto.getLastName().equals("") || !lastName.equals(medicalRecordsDto.getLastName())) {
            String errorMessage = "error while putting MedicalRecordsDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            Person person = personService.updatePersonMedicalRecords(firstName, lastName, medicalRecordsDto);
            JMapper<MedicalRecordsDto, Person> personToMedicalRecordsDtoMapper = new JMapper<>(MedicalRecordsDto.class, Person.class);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(personToMedicalRecordsDtoMapper.getDestination(person));
        } catch (NoSuchElementException e) {
            String errorMessage = "error while putting MedicalRecordsDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    @DeleteMapping("/medicalRecords/{firstName}&{lastName}")
    public ResponseEntity<?> deleteMedicalRecordsDto(@PathVariable String firstName, @PathVariable String lastName) {
        log.info("request to delete MedicalRecordsDto of firstname={} and lastname={}", firstName, lastName);

        if (firstName == null || firstName.equals("")
                || lastName == null || lastName.equals("")) {
            String errorMessage = "error while deleting MedicalRecordsDto because of wrong firstName=" + firstName + " and/or lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(errorMessage);
        }

        try {
            personService.deletePersonMedicalRecords(firstName, lastName);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("successfully deleted");
        } catch (NoSuchElementException e) {
            String errorMessage = "error while deleting MedicalRecordsDto because of non existing person with firstName=" + firstName + " & lastName=" + lastName;
            log.error(errorMessage);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorMessage);
        }
    }

    private List<MedicalRecordsDto> mapPersonsToMedicalRecordsDto(List<Person> personList) {
        JMapper<MedicalRecordsDto, Person> personToMedicalRecordsDtoMapper = new JMapper<>(MedicalRecordsDto.class, Person.class);
        return personList.stream()
                .map(personToMedicalRecordsDtoMapper::getDestination)
                .collect(Collectors.toList());
    }

}
