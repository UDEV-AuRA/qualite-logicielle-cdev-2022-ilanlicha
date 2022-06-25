package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class EmployeServicetest {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    @Test
    public void testEmbaucheEmploye() throws EmployeException {
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("C00001")).thenReturn(null);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.embaucheEmploye("Cena", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        //Employe employe = employeRepository.findByMatricule("C00001");
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getNom()).isEqualTo("Cena");
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00001");
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getSalaire()).isEqualTo(2129.71);
    }

    @Test
    public void testEmbauchePlusieursEmploye() throws EmployeException {
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12345");
        Mockito.when(employeRepository.findByMatricule("C12346")).thenReturn(null);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.embaucheEmploye("Cena", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        //Employe employe = employeRepository.findByMatricule("C00001");
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getNom()).isEqualTo("Cena");
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C12346");
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getSalaire()).isEqualTo(2129.71);
    }

    @Test
    public void testEmbaucheLimiteMatricule(){
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When
        /*try {
            employeService.embaucheEmploye("Cena", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);
            Assertions.fail("Aurait du planter !");
        } catch (Exception e){
            //Then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e).hasMessage("Limite des 100000 matricules atteinte !");
        }*/

        //When
            Throwable e = Assertions.catchThrowable(() -> {
              employeService.embaucheEmploye("Cena", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);
            });

            //Then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class).hasMessage("Limite des 100000 matricules atteinte !");

    }

    @Test
    public void testEmbaucheEmployeExisteDeja(){
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("55555");
        Mockito.when(employeRepository.findByMatricule(Mockito.anyString())).thenReturn(
                new Employe("Cena", "John", "C55556", LocalDate.now(), 2500d, 1, 1.0)
        );

        //When
        Throwable e = Assertions.catchThrowable(() -> {
            employeService.embaucheEmploye("Cena", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);
        });

        //Then
        Assertions.assertThat(e)
                .isInstanceOf(EntityExistsException.class)
                .hasMessage("L'employé de matricule C55556 existe déjà en BDD");

    }

    @Test
    public void testEmbaucheEmployeTempsPariel() throws EmployeException {
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12345");
        Mockito.when(employeRepository.findByMatricule("C12346")).thenReturn(null);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class)))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        //When
        employeService.embaucheEmploye("Doe", "John",
                Poste.COMMERCIAL, NiveauEtude.MASTER, 0.5);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());

        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getNom()).isEqualTo("Doe");
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C12346");
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(0.5);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1064.85);
    }

    @BeforeEach
    @AfterEach
    public void purge(){
        employeRepository.deleteAll();
    }
}
