package com.labs.service;

import com.labs.exception.EntityNotFoundException;
import com.labs.model.CompanyEntity;
import com.labs.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository repository;

    private CompanyService service;

    @BeforeEach
    void setUp() {
        service = new CompanyServiceImpl(repository);
    }

    @Nested
    @DisplayName("save()")
    class SaveTests {

        @Test
        @DisplayName("успешное сохранение возвращает сгенерированный id")
        void save_ShouldReturnGeneratedId() {
            when(repository.save(any(CompanyEntity.class))).thenReturn(42);

            int id = service.save("Yandex", 12000);

            assertEquals(42, id);
            verify(repository, times(1)).save(any(CompanyEntity.class));
        }

        @Test
        @DisplayName("пустое имя - выбрасывается IllegalArgumentException")
        void save_WithBlankName_ShouldThrowIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.save("  ", 1000));

            verifyNoInteractions(repository);
        }

        @Test
        @DisplayName("отрицательное число сотрудников - выбрасывается IllegalArgumentException")
        void save_WithNegativeCount_ShouldThrowIllegalArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.save("Yandex", -1));

            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("запись найдена - возвращается сущность")
        void findById_WhenExists_ShouldReturnEntity() {
            CompanyEntity entity = buildEntity(1, "Yandex", 12000);
            when(repository.findById(1)).thenReturn(Optional.of(entity));

            CompanyEntity result = service.findById(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("Yandex", result.getName());
            assertEquals(12000, result.getEmployeesCount());
        }

        @Test
        @DisplayName("запись не найдена - выбрасывается EntityNotFoundException")
        void findById_WhenNotExists_ShouldThrow() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.findById(99)
            );

            assertTrue(ex.getMessage().contains("99"));
        }
    }

    @Nested
    @DisplayName("findByName()")
    class FindByNameTests {

        @Test
        @DisplayName("запись найдена - возвращается сущность")
        void findByName_WhenExists_ShouldReturnEntity() {
            CompanyEntity entity = buildEntity(2, "Google", 150000);
            when(repository.findByName("Google")).thenReturn(Optional.of(entity));

            CompanyEntity result = service.findByName("Google");

            assertEquals(2, result.getId());
            assertEquals("Google", result.getName());
        }

        @Test
        @DisplayName("запись не найдена - выбрасывается EntityNotFoundException")
        void findByName_WhenNotExists_ShouldThrow() {
            when(repository.findByName("Unknown")).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.findByName("Unknown")
            );

            assertTrue(ex.getMessage().contains("Unknown"));
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("возвращает все записи")
        void findAll_ShouldReturnAllEntities() {
            List<CompanyEntity> companies = List.of(
                    buildEntity(1, "Yandex", 12000),
                    buildEntity(2, "Google", 150000),
                    buildEntity(3, "VK", 8000)
            );
            when(repository.findAll()).thenReturn(companies);

            List<CompanyEntity> result = service.findAll();

            assertEquals(3, result.size());
            assertEquals("Yandex", result.get(0).getName());
        }

        @Test
        @DisplayName("таблица пуста - возвращается пустой список")
        void findAll_WhenEmpty_ShouldReturnEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            List<CompanyEntity> result = service.findAll();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("обновление, репозиторий вызван")
        void update_WhenExists_ShouldCallRepository() {
            CompanyEntity entity = buildEntity(1, "Yandex", 15000);
            when(repository.findById(1)).thenReturn(Optional.of(entity));
            when(repository.update(entity)).thenReturn(true);

            assertDoesNotThrow(() -> service.update(entity));

            verify(repository, times(1)).findById(1);
            verify(repository, times(1)).update(entity);
        }

        @Test
        @DisplayName("запись не найдена, выбрасывается EntityNotFoundException, без update")
        void update_WhenNotExists_ShouldThrowWithoutCallingUpdate() {
            CompanyEntity entity = buildEntity(99, "Ghost", 0);
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> service.update(entity));

            verify(repository, never()).update(any());
        }

        @Test
        @DisplayName("null entity")
        void update_WithNull_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> service.update(null));
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("deleteById()")
    class DeleteByIdTests {

        @Test
        @DisplayName("deleteById делегируется репозиторию")
        void deleteById_ShouldDelegateToRepository() {
            doNothing().when(repository).deleteById(1);

            assertDoesNotThrow(() -> service.deleteById(1));

            verify(repository, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("удаление несуществующей записи")
        void deleteById_NonExistent_ShouldNotThrow() {
            doNothing().when(repository).deleteById(999);

            assertDoesNotThrow(() -> service.deleteById(999));

            verify(repository, times(1)).deleteById(999);
        }
    }

    private CompanyEntity buildEntity(int id, String name, int employeesCount) {
        CompanyEntity entity = new CompanyEntity(name, employeesCount);
        entity.setId(id);
        return entity;
    }
}
