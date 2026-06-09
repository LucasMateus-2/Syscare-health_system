package com.polaris.syscare_backend.application.patient.gateway;

import com.polaris.syscare_backend.domain.patient.Patient;
import com.polaris.syscare_backend.domain.patient.vo.CPF;

public interface PatientGateway
{
    boolean existsByCpf(CPF cpf);

    Patient save(Patient patient);

}
