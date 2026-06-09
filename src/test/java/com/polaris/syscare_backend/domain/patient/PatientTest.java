package com.polaris.syscare_backend.domain.patient;

import com.polaris.syscare_backend.domain.patient.vo.CPF;
import com.polaris.syscare_backend.domain.patient.vo.Email;
import com.polaris.syscare_backend.domain.patient.vo.FullName;
import com.polaris.syscare_backend.domain.patient.vo.Phone;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PatientTest
{
    /*------------Testes de CPF-------------------*/
    @Test
    void shouldNotAllowRegistrationWithInvalidCpf()
    {
        assertThatThrownBy(() -> new CPF("123"))
                .isInstanceOf(DomainException.class)
                .hasMessage("CPF inválido");
        assertThatThrownBy(() -> new CPF("123"))
                .isInstanceOf(DomainException.class)
                .hasMessage("CPF inválido");
    }

    @Test
    void shouldNotAllowCpfWithInvalidCheckDigits()
    {
        assertThatThrownBy(() -> new CPF("11111111111"))
                .isInstanceOf(DomainException.class);
    }
    /*-------------------------------------------------------------*/

    /*-------------------Testes Email------------------------------*/
    @Test
    void shouldNotAllowEmailWithoutAtSign()
    {
        assertThatThrownBy(() -> new Email("anaemail.com"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldNotAllowEmailWithoutDomain()
    {
        assertThatThrownBy(() -> new Email("ana@"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldNotAllowBlankEmail()
    {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(DomainException.class);
    }
    /*---------------------------------------------------*/

    /*---------------FullName Test-----------------------*/

    @Test
    void shouldNotAllowSingleWordName()
    {
        assertThatThrownBy(() -> new FullName("Ana"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Nome completo obrigatório");
    }

    @Test
    void shouldNotAllowNameWithNumbers()
    {
        assertThatThrownBy(() -> new FullName("Ana 123"))
                .isInstanceOf(DomainException.class);
    }
    /*--------------------------------------------------*/

    /*-----------------Enitity Patient------------------*/
    @Test
    void shouldNotAllowInactivatingAlreadyInactivePatient()
    {
        // Cria um paciente ativo
        Patient patient = new Patient(
                new FullName("Ana Souza"),
                new CPF("529.982.247-25"),
                new Email("ana@email.com"),
                new Phone("(15)99999999")
        );

        // Inativa pela primeira vez (deve funcionar)
        patient.inactivate();

        // Tentar inativar novamente deve lançar exceção
        assertThatThrownBy(() -> patient.inactivate())
                .isInstanceOf(DomainException.class)
                .hasMessage("Paciente já está inativo");
    }
    /*--------------------------------------------------*/
}