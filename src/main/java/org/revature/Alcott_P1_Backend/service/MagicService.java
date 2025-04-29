package org.revature.Alcott_P1_Backend.service;

import org.revature.Alcott_P1_Backend.entity.Magic;
import org.revature.Alcott_P1_Backend.exception.DuplicateEntryException;
import org.revature.Alcott_P1_Backend.exception.EmptyFieldException;
import org.revature.Alcott_P1_Backend.repository.MagicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        // null or empty check
        if(category == null || category.isEmpty()){
            throw new EmptyFieldException("category cannot be empty");
        }
        return magicRepository.findAllByCategory(category);
    }

    public Magic getMagicByName(String name){
        return magicRepository.findByName(name);
    }

    public Magic getMagicById(int id){
        return magicRepository.findById(id).orElse(null);
    }

    public String addNewMagic(Magic newMagic) throws EmptyFieldException, DuplicateEntryException {

        if(newMagic.getName() == null || newMagic.getName().isEmpty()){
            throw new EmptyFieldException("Name cannot be empty");
        }
        if(newMagic.getDescription() == null || newMagic.getDescription().isEmpty()){
            newMagic.setDescription("No description provided");
        }
        if(newMagic.getCategory() == null || newMagic.getCategory().isEmpty()){
            throw new EmptyFieldException("Category cannot be empty");
        }
        if(newMagic.getPrice() == 0){
            newMagic.setPrice(999);
        }

        // Check if there is one already in the database
        if(magicRepository.existsByName(newMagic.getName())){
            throw new DuplicateEntryException("A magic with this name already exists in the database");
        }

        return magicRepository.save(newMagic).getName();
    }

    public String deleteMagics(Magic[] selectedMagics){
        for(Magic m : selectedMagics){
            magicRepository.delete(m);
        }

        return "success";
    }
}
