package org.revature.Alcott_P1_Backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.revature.Alcott_P1_Backend.entity.Magic;
import org.revature.Alcott_P1_Backend.exception.DuplicateEntryException;
import org.revature.Alcott_P1_Backend.exception.EmptyFieldException;
import org.revature.Alcott_P1_Backend.service.MagicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "https://localhost:5173", allowCredentials = "true")
public class MagicController {

    @Autowired
    MagicService magicService;

    @GetMapping("/list-magics")
    public ResponseEntity<List<Magic>> getAllMagics(){
        return ResponseEntity.status(200).body(
                magicService.getAllMagics()
        );
    }

    @GetMapping("/list-magics/{category}")
    public ResponseEntity<List<Magic>> getMagicCategory(@PathVariable("category") String category){
        try {
            return ResponseEntity.status(200).body(
                    magicService.getMagicCategory(category)
            );
        }
        catch(EmptyFieldException e){
            return ResponseEntity.status(400).body(new ArrayList<>());
        }
    }

    @GetMapping("/view-magic/{name}")
    public ResponseEntity<Magic> getMagicByName(@PathVariable("name") String name){
        return ResponseEntity.status(200).body(
                magicService.getMagicByName(name)
        );
    }

    @GetMapping("/magic/{magicId}")
    public ResponseEntity<Magic> getMagicById(@PathVariable("magicId") int id){
        return ResponseEntity.status(200).body(
                magicService.getMagicById(id)
        );
    }

    @PostMapping("/admin/add-magic")
    public ResponseEntity<String> addMagic(@RequestBody Magic newMagic, HttpServletRequest request) {
        //TODO: add correct error responses
        try {
            return ResponseEntity.status(200).body(
                    magicService.addNewMagic(newMagic)
            );
        }
        catch(EmptyFieldException e){
            return ResponseEntity.status(400).body("Required fields cannot be empty");
        }
        catch(DuplicateEntryException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A magic with this name already exists");
        }
    }

    @PostMapping("/admin/delete-magics")
    public ResponseEntity<String> deleteMagics(@RequestBody Magic[] selectedMagics, HttpServletRequest request) {
        //TODO: add correct error responses
        try {
            return ResponseEntity.status(200).body(
                    magicService.deleteMagics(selectedMagics)
            );
        }
        catch(Exception e){
            return ResponseEntity.status(400).body("An unexpected error has occurred");
        }

    }
}
