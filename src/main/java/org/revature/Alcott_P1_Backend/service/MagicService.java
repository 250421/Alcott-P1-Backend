package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Magic;
import org.revature.Alcott_P1_Backend.exception.DuplicateEntryException;
import org.revature.Alcott_P1_Backend.exception.EmptyFieldException;
import org.revature.Alcott_P1_Backend.repository.MagicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class MagicService {

    private final MagicRepository magicRepository;

    @Autowired
    public MagicService(MagicRepository magicRepository){
        this.magicRepository = magicRepository;
    }

    public List<Magic> getAllMagics() {
        return magicRepository.findAll();
    }

    public List<Magic> getMagicCategory(String category) throws EmptyFieldException {
        if (category == null || category.isEmpty()) {
            throw new EmptyFieldException("Category cannot be empty");
        }
        List<Magic> magics = magicRepository.findAllByCategory(category);
        if (magics.isEmpty()) {
            throw new IllegalArgumentException("No magics found for the given category");
        }
        return magics;
    }

    public Magic getMagicByName(String name){
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        Magic magic = magicRepository.findByName(name);
        if (magic == null) {
            throw new IllegalArgumentException("No magic found with the given name");
        }
        return magic;
    }

    public Magic getMagicById(int id){
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer");
        }
        return magicRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No magic found with the given ID"));    
    }

    @Transactional
    public String addNewMagic(Magic newMagic) throws EmptyFieldException, DuplicateEntryException {

        if (newMagic == null) {
            throw new IllegalArgumentException("Magic object cannot be null");
        }
        if (newMagic.getName() == null || newMagic.getName().isEmpty()) {
            throw new EmptyFieldException("Name cannot be empty");
        }
        if (newMagic.getDescription() == null || newMagic.getDescription().isEmpty()) {
            newMagic.setDescription("No description provided");
        }
        if (newMagic.getCategory() == null || newMagic.getCategory().isEmpty()) {
            throw new EmptyFieldException("Category cannot be empty");
        }
        if (newMagic.getPrice() <= 0) {
            newMagic.setPrice(999);
        }
    
        if (magicRepository.existsByName(newMagic.getName())) {
            throw new DuplicateEntryException("A magic with this name already exists in the database");
        }

        if(newMagic.getId() == -2){
            newMagic.setId(null);
        }
    
        return magicRepository.save(newMagic).getName();
    }

    @Transactional
    public String editMagic(Magic newMagic) throws EmptyFieldException, DuplicateEntryException {

        if (newMagic == null) {
            throw new IllegalArgumentException("Magic object cannot be null");
        }
        if (newMagic.getName() == null || newMagic.getName().isEmpty()) {
            throw new EmptyFieldException("Name cannot be empty");
        }
        if (newMagic.getDescription() == null || newMagic.getDescription().isEmpty()) {
            newMagic.setDescription("No description provided");
        }
        if (newMagic.getCategory() == null || newMagic.getCategory().isEmpty()) {
            throw new EmptyFieldException("Category cannot be empty");
        }
        if (newMagic.getPrice() <= 0) {
            newMagic.setPrice(999);
        }
    
        if (!magicRepository.existsByName(newMagic.getName())) {
            throw new DuplicateEntryException("There is no magic with this name in the database");
        }
    
        return magicRepository.save(newMagic).getName();
    }

    public String deleteMagics(Magic[] selectedMagics){
        if (selectedMagics == null || selectedMagics.length == 0) {
            throw new IllegalArgumentException("Selected magics cannot be null or empty");
        }
        for (Magic m : selectedMagics) {
            if (!magicRepository.existsById(m.getId().intValue())) {
                throw new IllegalArgumentException("Magic with ID " + m.getId() + " does not exist");
            }
            magicRepository.delete(m);
        }
        return "success";
    }
}
