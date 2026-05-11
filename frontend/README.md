# SysCare Frontend

Next.js frontend for the Django REST API in `backend/Django`.

## Run

```bash
npm install
npm run dev
```

Open `http://127.0.0.1:3000`.

The app calls Django through the Next.js proxy route at `/api/backend/*`.
Set the Django API base URL with:

```bash
DJANGO_API_URL=http://127.0.0.1:8000/api/v1
```

The frontend covers:

- `users`
- `especialidades`
- `medicos`
- `pacientes`
- `consultas`
- `consultas/:id/concluir`
- `consultas/:id/cancelar`
- `prontuarios`
