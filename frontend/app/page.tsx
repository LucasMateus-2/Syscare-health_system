"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import {
  api,
  Appointment,
  AppointmentStatus,
  Doctor,
  formatDateTime,
  fullDoctorName,
  fullPatientName,
  MedicalRecord,
  Patient,
  Role,
  Specialty,
  User,
} from "@/lib/api";

type ResourceKey =
  | "users"
  | "especialidades"
  | "medicos"
  | "pacientes"
  | "consultas"
  | "prontuarios";

type EditState = { resource: ResourceKey; id: number } | null;

const roles: Role[] = ["PATIENT", "DOCTOR", "STAFF"];
const bloodTypes = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];
const statuses: AppointmentStatus[] = ["AGENDADO", "CONCLUIDO", "CANCELADO"];

const emptyUser = {
  email: "",
  password: "",
  full_name: "",
  role: "PATIENT" as Role,
  is_active: true,
};

const emptySpecialty = { name: "", description: "" };
const emptyDoctor = { user_id: "", crm: "", especialidade_id: "" };
const emptyPatient = {
  user_id: "",
  cpf: "",
  data_nascimento: "",
  tipo_sanguineo: "O+",
  alergias: "",
};
const emptyAppointment = {
  medico_id: "",
  paciente_id: "",
  data_hora: "",
  status: "AGENDADO" as AppointmentStatus,
  observacoes: "",
};
const emptyRecord = {
  paciente_id: "",
  medico_id: "",
  consulta_id: "",
  diagnostico: "",
  prescricao: "",
  evolucao_clinica: "",
};

export default function Home() {
  const [users, setUsers] = useState<User[]>([]);
  const [specialties, setSpecialties] = useState<Specialty[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [records, setRecords] = useState<MedicalRecord[]>([]);
  const [active, setActive] = useState<ResourceKey>("consultas");
  const [loading, setLoading] = useState(false);
  const [notice, setNotice] = useState("");
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [edit, setEdit] = useState<EditState>(null);

  const [userForm, setUserForm] = useState(emptyUser);
  const [specialtyForm, setSpecialtyForm] = useState(emptySpecialty);
  const [doctorForm, setDoctorForm] = useState(emptyDoctor);
  const [patientForm, setPatientForm] = useState(emptyPatient);
  const [appointmentForm, setAppointmentForm] = useState(emptyAppointment);
  const [recordForm, setRecordForm] = useState(emptyRecord);

  const doctorUsers = useMemo(
    () => users.filter((user) => user.role === "DOCTOR"),
    [users],
  );
  const patientUsers = useMemo(
    () => users.filter((user) => user.role === "PATIENT"),
    [users],
  );

  async function loadAll(query = "") {
    setLoading(true);
    setError("");
    try {
      const searchQuery = query.trim()
        ? new URLSearchParams({ search: query.trim() }).toString()
        : "";
      const [
        nextUsers,
        nextSpecialties,
        nextDoctors,
        nextPatients,
        nextAppointments,
        nextRecords,
      ] = await Promise.all([
        api.list<User>("users", searchQuery),
        api.list<Specialty>("especialidades", searchQuery),
        api.list<Doctor>("medicos", searchQuery),
        api.list<Patient>("pacientes", searchQuery),
        api.list<Appointment>("consultas", searchQuery),
        api.list<MedicalRecord>("prontuarios", searchQuery),
      ]);
      setUsers(nextUsers);
      setSpecialties(nextSpecialties);
      setDoctors(nextDoctors);
      setPatients(nextPatients);
      setAppointments(nextAppointments);
      setRecords(nextRecords);
    } catch (caught) {
      setError(
        caught instanceof Error
          ? caught.message
          : "Nao foi possivel carregar os dados.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll();
  }, []);

  async function submit(
    event: FormEvent<HTMLFormElement>,
    resource: ResourceKey,
    payload: Record<string, unknown>,
    reset: () => void,
  ) {
    event.preventDefault();
    setError("");
    setNotice("");
    try {
      const cleaned = cleanPayload(payload);
      if (edit?.resource === resource) {
        await api.update(resource, edit.id, cleaned);
        setNotice("Registro atualizado.");
      } else {
        await api.create(resource, cleaned);
        setNotice("Registro criado.");
      }
      setEdit(null);
      reset();
      await loadAll(search);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : "Falha ao salvar.");
    }
  }

  async function remove(resource: ResourceKey, id: number) {
    setError("");
    setNotice("");
    try {
      await api.remove(resource, id);
      setNotice("Registro removido.");
      await loadAll(search);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : "Falha ao remover.");
    }
  }

  async function runAppointmentAction(id: number, action: "concluir" | "cancelar") {
    setError("");
    setNotice("");
    try {
      await api.appointmentAction(id, action);
      setNotice(action === "concluir" ? "Consulta concluida." : "Consulta cancelada.");
      await loadAll(search);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : "Falha na acao.");
    }
  }

  function startEdit(resource: ResourceKey, item: unknown) {
    setActive(resource);
    if (resource === "users") {
      const user = item as User;
      setUserForm({ ...user, password: "" });
      setEdit({ resource, id: user.id });
    }
    if (resource === "especialidades") {
      const specialty = item as Specialty;
      setSpecialtyForm(specialty);
      setEdit({ resource, id: specialty.id });
    }
    if (resource === "medicos") {
      const doctor = item as Doctor;
      setDoctorForm({
        user_id: String(doctor.user.id),
        crm: doctor.crm,
        especialidade_id: doctor.especialidade ? String(doctor.especialidade.id) : "",
      });
      setEdit({ resource, id: doctor.id });
    }
    if (resource === "pacientes") {
      const patient = item as Patient;
      setPatientForm({
        user_id: String(patient.user.id),
        cpf: patient.cpf,
        data_nascimento: patient.data_nascimento,
        tipo_sanguineo: patient.tipo_sanguineo,
        alergias: patient.alergias,
      });
      setEdit({ resource, id: patient.id });
    }
    if (resource === "consultas") {
      const appointment = item as Appointment;
      setAppointmentForm({
        medico_id: String(appointment.medico.id),
        paciente_id: String(appointment.paciente.id),
        data_hora: toDateTimeLocal(appointment.data_hora),
        status: appointment.status,
        observacoes: appointment.observacoes,
      });
      setEdit({ resource, id: appointment.id });
    }
    if (resource === "prontuarios") {
      const record = item as MedicalRecord;
      setRecordForm({
        paciente_id: String(record.paciente.id),
        medico_id: String(record.medico.id),
        consulta_id: record.consulta ? String(record.consulta) : "",
        diagnostico: record.diagnostico,
        prescricao: record.prescricao,
        evolucao_clinica: record.evolucao_clinica,
      });
      setEdit({ resource, id: record.id });
    }
  }

  const statCards = [
    ["Usuarios", users.length],
    ["Especialidades", specialties.length],
    ["Medicos", doctors.length],
    ["Pacientes", patients.length],
    ["Consultas", appointments.length],
    ["Prontuarios", records.length],
  ];

  return (
    <main>
      <header className="topbar">
        <div>
          <p className="eyebrow">SysCare</p>
          <h1>Gestao clinica</h1>
        </div>
        <form
          className="search"
          onSubmit={(event) => {
            event.preventDefault();
            loadAll(search);
          }}
        >
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Buscar em todos os recursos"
          />
          <button type="submit">Buscar</button>
        </form>
      </header>

      <section className="stats" aria-label="Resumo">
        {statCards.map(([label, value]) => (
          <article className="stat" key={label}>
            <span>{label}</span>
            <strong>{value}</strong>
          </article>
        ))}
      </section>

      <nav className="tabs" aria-label="Recursos da API">
        {[
          ["consultas", "Consultas"],
          ["prontuarios", "Prontuarios"],
          ["pacientes", "Pacientes"],
          ["medicos", "Medicos"],
          ["especialidades", "Especialidades"],
          ["users", "Usuarios"],
        ].map(([key, label]) => (
          <button
            key={key}
            className={active === key ? "active" : ""}
            onClick={() => setActive(key as ResourceKey)}
            type="button"
          >
            {label}
          </button>
        ))}
      </nav>

      <div className="feedback">
        {loading && <span>Carregando dados da API...</span>}
        {notice && <span className="success">{notice}</span>}
        {error && <pre className="error">{error}</pre>}
      </div>

      {active === "consultas" && (
        <section className="workspace">
          <Panel title={edit?.resource === "consultas" ? "Editar consulta" : "Nova consulta"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(
                  event,
                  "consultas",
                  {
                    ...appointmentForm,
                    data_hora: new Date(appointmentForm.data_hora).toISOString(),
                  },
                  () => setAppointmentForm(emptyAppointment),
                )
              }
            >
              <Select
                label="Medico"
                value={appointmentForm.medico_id}
                onChange={(value) =>
                  setAppointmentForm({ ...appointmentForm, medico_id: value })
                }
                options={doctors.map((doctor) => [doctor.id, fullDoctorName(doctor)])}
              />
              <Select
                label="Paciente"
                value={appointmentForm.paciente_id}
                onChange={(value) =>
                  setAppointmentForm({ ...appointmentForm, paciente_id: value })
                }
                options={patients.map((patient) => [patient.id, fullPatientName(patient)])}
              />
              <Field
                label="Data e hora"
                type="datetime-local"
                value={appointmentForm.data_hora}
                onChange={(value) =>
                  setAppointmentForm({ ...appointmentForm, data_hora: value })
                }
              />
              <Select
                label="Status"
                value={appointmentForm.status}
                onChange={(value) =>
                  setAppointmentForm({
                    ...appointmentForm,
                    status: value as AppointmentStatus,
                  })
                }
                options={statuses.map((status) => [status, status])}
              />
              <Textarea
                label="Observacoes"
                value={appointmentForm.observacoes}
                onChange={(value) =>
                  setAppointmentForm({ ...appointmentForm, observacoes: value })
                }
              />
              <FormActions editing={edit?.resource === "consultas"} onCancel={() => {
                setEdit(null);
                setAppointmentForm(emptyAppointment);
              }} />
            </form>
          </Panel>
          <Panel title="Consultas">
            <Table
              headers={["Paciente", "Medico", "Data", "Status", "Acoes"]}
              rows={appointments.map((appointment) => [
                fullPatientName(appointment.paciente),
                fullDoctorName(appointment.medico),
                formatDateTime(appointment.data_hora),
                appointment.status,
                <Actions
                  key={appointment.id}
                  onEdit={() => startEdit("consultas", appointment)}
                  onDelete={() => remove("consultas", appointment.id)}
                  extra={
                    <>
                      <button onClick={() => runAppointmentAction(appointment.id, "concluir")}>
                        Concluir
                      </button>
                      <button onClick={() => runAppointmentAction(appointment.id, "cancelar")}>
                        Cancelar
                      </button>
                    </>
                  }
                />,
              ])}
            />
          </Panel>
        </section>
      )}

      {active === "prontuarios" && (
        <section className="workspace">
          <Panel title={edit?.resource === "prontuarios" ? "Editar prontuario" : "Novo prontuario"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(event, "prontuarios", recordForm, () => setRecordForm(emptyRecord))
              }
            >
              <Select
                label="Paciente"
                value={recordForm.paciente_id}
                onChange={(value) => setRecordForm({ ...recordForm, paciente_id: value })}
                options={patients.map((patient) => [patient.id, fullPatientName(patient)])}
              />
              <Select
                label="Medico"
                value={recordForm.medico_id}
                onChange={(value) => setRecordForm({ ...recordForm, medico_id: value })}
                options={doctors.map((doctor) => [doctor.id, fullDoctorName(doctor)])}
              />
              <Select
                label="Consulta"
                value={recordForm.consulta_id}
                onChange={(value) => setRecordForm({ ...recordForm, consulta_id: value })}
                options={appointments.map((item) => [
                  item.id,
                  `${fullPatientName(item.paciente)} - ${formatDateTime(item.data_hora)}`,
                ])}
                optional
              />
              <Textarea
                label="Diagnostico"
                value={recordForm.diagnostico}
                onChange={(value) => setRecordForm({ ...recordForm, diagnostico: value })}
              />
              <Textarea
                label="Prescricao"
                value={recordForm.prescricao}
                onChange={(value) => setRecordForm({ ...recordForm, prescricao: value })}
              />
              <Textarea
                label="Evolucao clinica"
                value={recordForm.evolucao_clinica}
                onChange={(value) =>
                  setRecordForm({ ...recordForm, evolucao_clinica: value })
                }
              />
              <FormActions editing={edit?.resource === "prontuarios"} onCancel={() => {
                setEdit(null);
                setRecordForm(emptyRecord);
              }} />
            </form>
          </Panel>
          <Panel title="Prontuarios">
            <Table
              headers={["Paciente", "Medico", "Diagnostico", "Registro", "Acoes"]}
              rows={records.map((record) => [
                fullPatientName(record.paciente),
                fullDoctorName(record.medico),
                record.diagnostico,
                formatDateTime(record.data_registro),
                <Actions
                  key={record.id}
                  onEdit={() => startEdit("prontuarios", record)}
                  onDelete={() => remove("prontuarios", record.id)}
                />,
              ])}
            />
          </Panel>
        </section>
      )}

      {active === "pacientes" && (
        <section className="workspace">
          <Panel title={edit?.resource === "pacientes" ? "Editar paciente" : "Novo paciente"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(event, "pacientes", patientForm, () => setPatientForm(emptyPatient))
              }
            >
              <Select
                label="Usuario paciente"
                value={patientForm.user_id}
                onChange={(value) => setPatientForm({ ...patientForm, user_id: value })}
                options={patientUsers.map((user) => [user.id, user.full_name])}
              />
              <Field
                label="CPF"
                value={patientForm.cpf}
                onChange={(value) => setPatientForm({ ...patientForm, cpf: value })}
              />
              <Field
                label="Nascimento"
                type="date"
                value={patientForm.data_nascimento}
                onChange={(value) =>
                  setPatientForm({ ...patientForm, data_nascimento: value })
                }
              />
              <Select
                label="Tipo sanguineo"
                value={patientForm.tipo_sanguineo}
                onChange={(value) =>
                  setPatientForm({ ...patientForm, tipo_sanguineo: value })
                }
                options={bloodTypes.map((type) => [type, type])}
              />
              <Textarea
                label="Alergias"
                value={patientForm.alergias}
                onChange={(value) => setPatientForm({ ...patientForm, alergias: value })}
              />
              <FormActions editing={edit?.resource === "pacientes"} onCancel={() => {
                setEdit(null);
                setPatientForm(emptyPatient);
              }} />
            </form>
          </Panel>
          <Panel title="Pacientes">
            <Table
              headers={["Nome", "CPF", "Nascimento", "Sangue", "Acoes"]}
              rows={patients.map((patient) => [
                patient.user.full_name,
                patient.cpf,
                patient.data_nascimento,
                patient.tipo_sanguineo,
                <Actions
                  key={patient.id}
                  onEdit={() => startEdit("pacientes", patient)}
                  onDelete={() => remove("pacientes", patient.id)}
                />,
              ])}
            />
          </Panel>
        </section>
      )}

      {active === "medicos" && (
        <section className="workspace">
          <Panel title={edit?.resource === "medicos" ? "Editar medico" : "Novo medico"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(event, "medicos", doctorForm, () => setDoctorForm(emptyDoctor))
              }
            >
              <Select
                label="Usuario medico"
                value={doctorForm.user_id}
                onChange={(value) => setDoctorForm({ ...doctorForm, user_id: value })}
                options={doctorUsers.map((user) => [user.id, user.full_name])}
              />
              <Field
                label="CRM"
                value={doctorForm.crm}
                onChange={(value) => setDoctorForm({ ...doctorForm, crm: value })}
              />
              <Select
                label="Especialidade"
                value={doctorForm.especialidade_id}
                onChange={(value) =>
                  setDoctorForm({ ...doctorForm, especialidade_id: value })
                }
                options={specialties.map((specialty) => [specialty.id, specialty.name])}
                optional
              />
              <FormActions editing={edit?.resource === "medicos"} onCancel={() => {
                setEdit(null);
                setDoctorForm(emptyDoctor);
              }} />
            </form>
          </Panel>
          <Panel title="Medicos">
            <Table
              headers={["Nome", "CRM", "Especialidade", "Acoes"]}
              rows={doctors.map((doctor) => [
                doctor.user.full_name,
                doctor.crm,
                doctor.especialidade?.name ?? "-",
                <Actions
                  key={doctor.id}
                  onEdit={() => startEdit("medicos", doctor)}
                  onDelete={() => remove("medicos", doctor.id)}
                />,
              ])}
            />
          </Panel>
        </section>
      )}

      {active === "especialidades" && (
        <section className="workspace">
          <Panel title={edit?.resource === "especialidades" ? "Editar especialidade" : "Nova especialidade"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(event, "especialidades", specialtyForm, () =>
                  setSpecialtyForm(emptySpecialty),
                )
              }
            >
              <Field
                label="Nome"
                value={specialtyForm.name}
                onChange={(value) => setSpecialtyForm({ ...specialtyForm, name: value })}
              />
              <Textarea
                label="Descricao"
                value={specialtyForm.description}
                onChange={(value) =>
                  setSpecialtyForm({ ...specialtyForm, description: value })
                }
              />
              <FormActions editing={edit?.resource === "especialidades"} onCancel={() => {
                setEdit(null);
                setSpecialtyForm(emptySpecialty);
              }} />
            </form>
          </Panel>
          <Panel title="Especialidades">
            <Table
              headers={["Nome", "Descricao", "Acoes"]}
              rows={specialties.map((specialty) => [
                specialty.name,
                specialty.description || "-",
                <Actions
                  key={specialty.id}
                  onEdit={() => startEdit("especialidades", specialty)}
                  onDelete={() => remove("especialidades", specialty.id)}
                />,
              ])}
            />
          </Panel>
        </section>
      )}

      {active === "users" && (
        <section className="workspace">
          <Panel title={edit?.resource === "users" ? "Editar usuario" : "Novo usuario"}>
            <form
              className="form"
              onSubmit={(event) =>
                submit(event, "users", userForm, () => setUserForm(emptyUser))
              }
            >
              <Field
                label="Nome completo"
                value={userForm.full_name}
                onChange={(value) => setUserForm({ ...userForm, full_name: value })}
              />
              <Field
                label="Email"
                type="email"
                value={userForm.email}
                onChange={(value) => setUserForm({ ...userForm, email: value })}
              />
              <Field
                label="Senha"
                type="password"
                value={userForm.password}
                onChange={(value) => setUserForm({ ...userForm, password: value })}
                required={!edit}
              />
              <Select
                label="Perfil"
                value={userForm.role}
                onChange={(value) => setUserForm({ ...userForm, role: value as Role })}
                options={roles.map((role) => [role, role])}
              />
              <label className="checkbox">
                <input
                  checked={userForm.is_active}
                  onChange={(event) =>
                    setUserForm({ ...userForm, is_active: event.target.checked })
                  }
                  type="checkbox"
                />
                Ativo
              </label>
              <FormActions editing={edit?.resource === "users"} onCancel={() => {
                setEdit(null);
                setUserForm(emptyUser);
              }} />
            </form>
          </Panel>
          <Panel title="Usuarios">
            <Table
              headers={["Nome", "Email", "Perfil", "Ativo", "Acoes"]}
              rows={users.map((user) => [
                user.full_name,
                user.email,
                user.role,
                user.is_active ? "Sim" : "Nao",
                <Actions
                  key={user.id}
                  onEdit={() => startEdit("users", user)}
                  onDelete={() => remove("users", user.id)}
                />,
              ])}
            />
          </Panel>
        </section>
      )}
    </main>
  );
}

function cleanPayload(payload: Record<string, unknown>) {
  return Object.fromEntries(
    Object.entries(payload)
      .filter(([, value]) => value !== "")
      .map(([key, value]) => {
        if (key.endsWith("_id") && value !== null) {
          return [key, Number(value)];
        }
        return [key, value];
      }),
  );
}

function toDateTimeLocal(value: string) {
  if (!value) return "";
  const date = new Date(value);
  const offset = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - offset).toISOString().slice(0, 16);
}

function Panel({
  title,
  children,
}: Readonly<{ title: string; children: React.ReactNode }>) {
  return (
    <article className="panel">
      <h2>{title}</h2>
      {children}
    </article>
  );
}

function Field({
  label,
  value,
  onChange,
  type = "text",
  required = true,
}: Readonly<{
  label: string;
  value: string;
  onChange: (value: string) => void;
  type?: string;
  required?: boolean;
}>) {
  return (
    <label>
      <span>{label}</span>
      <input
        required={required}
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}

function Textarea({
  label,
  value,
  onChange,
}: Readonly<{
  label: string;
  value: string;
  onChange: (value: string) => void;
}>) {
  return (
    <label>
      <span>{label}</span>
      <textarea value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function Select({
  label,
  value,
  onChange,
  options,
  optional = false,
}: Readonly<{
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: Array<[number | string, string]>;
  optional?: boolean;
}>) {
  return (
    <label>
      <span>{label}</span>
      <select
        required={!optional}
        value={value}
        onChange={(event) => onChange(event.target.value)}
      >
        <option value="">{optional ? "Nenhum" : "Selecione"}</option>
        {options.map(([optionValue, labelText]) => (
          <option key={optionValue} value={optionValue}>
            {labelText}
          </option>
        ))}
      </select>
    </label>
  );
}

function FormActions({
  editing,
  onCancel,
}: Readonly<{ editing: boolean; onCancel: () => void }>) {
  return (
    <div className="formActions">
      <button type="submit">{editing ? "Atualizar" : "Criar"}</button>
      {editing && (
        <button className="secondary" onClick={onCancel} type="button">
          Cancelar edicao
        </button>
      )}
    </div>
  );
}

function Table({
  headers,
  rows,
}: Readonly<{ headers: string[]; rows: React.ReactNode[][] }>) {
  return (
    <div className="tableWrap">
      <table>
        <thead>
          <tr>
            {headers.map((header) => (
              <th key={header}>{header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td colSpan={headers.length}>Nenhum registro encontrado.</td>
            </tr>
          ) : (
            rows.map((row, index) => (
              <tr key={index}>
                {row.map((cell, cellIndex) => (
                  <td key={cellIndex}>{cell}</td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

function Actions({
  onEdit,
  onDelete,
  extra,
}: Readonly<{
  onEdit: () => void;
  onDelete: () => void;
  extra?: React.ReactNode;
}>) {
  return (
    <div className="actions">
      <button onClick={onEdit} type="button">
        Editar
      </button>
      {extra}
      <button className="danger" onClick={onDelete} type="button">
        Excluir
      </button>
    </div>
  );
}
