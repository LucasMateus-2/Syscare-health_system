export type Role = "PATIENT" | "DOCTOR" | "STAFF";
export type AppointmentStatus = "AGENDADO" | "CONCLUIDO" | "CANCELADO";

export type User = {
  id: number;
  email: string;
  full_name: string;
  role: Role;
  is_active: boolean;
  created_at: string;
};

export type Specialty = {
  id: number;
  name: string;
  description: string;
};

export type Doctor = {
  id: number;
  user: User;
  crm: string;
  especialidade: Specialty | null;
};

export type Patient = {
  id: number;
  user: User;
  cpf: string;
  data_nascimento: string;
  tipo_sanguineo: string;
  alergias: string;
};

export type Appointment = {
  id: number;
  medico: Doctor;
  paciente: Patient;
  data_hora: string;
  status: AppointmentStatus;
  observacoes: string;
  created_at: string;
  updated_at: string;
};

export type MedicalRecord = {
  id: number;
  paciente: Patient;
  medico: Doctor;
  consulta: number | null;
  diagnostico: string;
  prescricao: string;
  evolucao_clinica: string;
  data_registro: string;
};

export type ApiList<T> = T[] | { results: T[]; count: number };

const basePath = "/api/backend";

function readList<T>(payload: ApiList<T>): T[] {
  return Array.isArray(payload) ? payload : payload.results;
}

async function request<T>(
  endpoint: string,
  init: RequestInit = {},
): Promise<T> {
  const response = await fetch(`${basePath}${endpoint}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {}),
    },
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const api = {
  async list<T>(resource: string, query = "") {
    const suffix = query ? `?${query}` : "";
    return readList(await request<ApiList<T>>(`/${resource}/${suffix}`));
  },
  create<T>(resource: string, data: unknown) {
    return request<T>(`/${resource}/`, {
      method: "POST",
      body: JSON.stringify(data),
    });
  },
  update<T>(resource: string, id: number, data: unknown) {
    return request<T>(`/${resource}/${id}/`, {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  },
  remove(resource: string, id: number) {
    return request<void>(`/${resource}/${id}/`, { method: "DELETE" });
  },
  appointmentAction(id: number, action: "concluir" | "cancelar") {
    return request<Appointment>(`/consultas/${id}/${action}/`, {
      method: "PATCH",
      body: JSON.stringify({}),
    });
  },
};

export function formatDateTime(value: string) {
  if (!value) return "-";
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value));
}

export function fullDoctorName(doctor?: Doctor | null) {
  if (!doctor) return "-";
  const specialty = doctor.especialidade?.name ? `, ${doctor.especialidade.name}` : "";
  return `${doctor.user.full_name} (${doctor.crm}${specialty})`;
}

export function fullPatientName(patient?: Patient | null) {
  if (!patient) return "-";
  return `${patient.user.full_name} (${patient.cpf})`;
}
