package org.revature.Alcott_P1_Backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.revature.Alcott_P1_Backend.entity.Magic;
import org.revature.Alcott_P1_Backend.exception.DuplicateEntryException;
import org.revature.Alcott_P1_Backend.exception.EmptyFieldException;
import org.revature.Alcott_P1_Backend.repository.MagicRepository;
import org.revature.Alcott_P1_Backend.service.MagicService;

class MagicServiceTests {

    private MagicRepository magicRepository;
    private MagicService magicService;

    @BeforeEach
    void setUp() {
        magicRepository = mock(MagicRepository.class);
        magicService = new MagicService(magicRepository);
    }

    @Test
    void getAllMagics_ShouldReturnListOfMagics() {
        List<Magic> magics = List.of(new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", ""));
        when(magicRepository.findAll()).thenReturn(magics);

        List<Magic> result = magicService.getAllMagics();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fireball", result.get(0).getName());
    }

    @Test
    void getMagicCategory_ShouldReturnMagics_WhenValidCategory() throws EmptyFieldException {
        List<Magic> magics = List.of(new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", ""));
        when(magicRepository.findAllByCategory("Offensive")).thenReturn(magics);

        List<Magic> result = magicService.getMagicCategory("Offensive");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fireball", result.get(0).getName());
    }

    @Test
    void getMagicCategory_ShouldThrowException_WhenCategoryIsNull() {
        assertThrows(EmptyFieldException.class, () -> magicService.getMagicCategory(null));
    }

    @Test
    void getMagicCategory_ShouldThrowException_WhenNoMagicsFound() {
        when(magicRepository.findAllByCategory("Nonexistent")).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> magicService.getMagicCategory("Nonexistent"));
    }

    @Test
    void getMagicByName_ShouldReturnMagic_WhenValidName() {
        Magic magic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        when(magicRepository.findByName("Fireball")).thenReturn(magic);

        Magic result = magicService.getMagicByName("Fireball");

        assertNotNull(result);
        assertEquals("Fireball", result.getName());
    }

    @Test
    void getMagicByName_ShouldThrowException_WhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> magicService.getMagicByName(null));
    }

    @Test
    void getMagicByName_ShouldThrowException_WhenMagicNotFound() {
        when(magicRepository.findByName("Nonexistent")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> magicService.getMagicByName("Nonexistent"));
    }

    @Test
    void getMagicById_ShouldReturnMagic_WhenValidId() {
        Magic magic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        when(magicRepository.findById(1)).thenReturn(java.util.Optional.of(magic));

        Magic result = magicService.getMagicById(1);

        assertNotNull(result);
        assertEquals("Fireball", result.getName());
    }

    @Test
    void getMagicById_ShouldThrowException_WhenIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> magicService.getMagicById(0));
    }

    @Test
    void getMagicById_ShouldThrowException_WhenMagicNotFound() {
        when(magicRepository.findById(999)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> magicService.getMagicById(999));
    }

    @Test
    void addNewMagic_ShouldAddMagic_WhenValidInput() throws EmptyFieldException, DuplicateEntryException {
        Magic newMagic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        when(magicRepository.existsByName("Fireball")).thenReturn(false);
        when(magicRepository.save(newMagic)).thenReturn(newMagic);

        String result = magicService.addNewMagic(newMagic);

        assertEquals("Fireball", result);
        verify(magicRepository, times(1)).save(newMagic);
    }

    @Test
    void addNewMagic_ShouldThrowException_WhenMagicIsNull() {
        assertThrows(IllegalArgumentException.class, () -> magicService.addNewMagic(null));
    }

    @Test
    void addNewMagic_ShouldThrowException_WhenNameIsEmpty() {
        Magic newMagic = new Magic(1L, "", "A large ball of fire", 1, 1, "Attack Magic", "");

        assertThrows(EmptyFieldException.class, () -> magicService.addNewMagic(newMagic));
    }

    @Test
    void addNewMagic_ShouldThrowException_WhenMagicAlreadyExists() {
        Magic newMagic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        when(magicRepository.existsByName("Fireball")).thenReturn(true);

        assertThrows(DuplicateEntryException.class, () -> magicService.addNewMagic(newMagic));
    }

    @Test
    void deleteMagics_ShouldDeleteMagics_WhenValidInput() {
        Magic magic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        Magic[] magics = { magic };
        when(magicRepository.existsById(1)).thenReturn(true);

        String result = magicService.deleteMagics(magics);

        assertEquals("success", result);
        verify(magicRepository, times(1)).delete(magic);
    }

    @Test
    void deleteMagics_ShouldThrowException_WhenSelectedMagicsIsNull() {
        assertThrows(IllegalArgumentException.class, () -> magicService.deleteMagics(null));
    }

    @Test
    void deleteMagics_ShouldThrowException_WhenMagicDoesNotExist() {
        Magic magic = new Magic(1L, "Fireball", "A large ball of fire", 1, 1, "Attack Magic", "");
        Magic[] magics = { magic };
        when(magicRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> magicService.deleteMagics(magics));
    }

    @Test
    void editMagic_ShouldEditMagic_WhenValidInput() throws EmptyFieldException, DuplicateEntryException {
        Magic updatedMagic = new Magic(1L, "Fireball", "An updated description", 1, 1, "Attack Magic", "");

        when(magicRepository.existsByName("Fireball")).thenReturn(true);
        when(magicRepository.save(updatedMagic)).thenReturn(updatedMagic);

        String result = magicService.editMagic(updatedMagic);

        assertEquals("Fireball", result);
        verify(magicRepository, times(1)).save(updatedMagic);
    }

    @Test
    void editMagic_ShouldThrowException_WhenMagicIsNull() {
        assertThrows(IllegalArgumentException.class, () -> magicService.editMagic(null));
    }

    @Test
    void editMagic_ShouldThrowException_WhenNameIsEmpty() {
        Magic updatedMagic = new Magic(1L, "", "An updated description", 1, 1, "Attack Magic", "");

        assertThrows(EmptyFieldException.class, () -> magicService.editMagic(updatedMagic));
    }

    @Test
    void editMagic_ShouldThrowException_WhenCategoryIsEmpty() {
        Magic updatedMagic = new Magic(1L, "Fireball", "An updated description", 1, 1, "", "");

        assertThrows(EmptyFieldException.class, () -> magicService.editMagic(updatedMagic));
    }

    @Test
    void editMagic_ShouldThrowException_WhenMagicDoesNotExist() {
        Magic updatedMagic = new Magic(1L, "Nonexistent", "An updated description", 1, 1, "Attack Magic", "");
        when(magicRepository.existsByName("Nonexistent")).thenReturn(false);

        assertThrows(DuplicateEntryException.class, () -> magicService.editMagic(updatedMagic));
    }

}