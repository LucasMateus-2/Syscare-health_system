🚀 Syscare - Backend - NestJS + TypeORM
Este é o repositório principal da nossa API. Siga os passos abaixo para configurar o ambiente e começar a desenvolver.

🛠️ Pré-requisitos
Antes de começar, você precisa ter instalado:

Node.js (Versão 18 ou superior)

PostgreSQL (Rodando localmente ou via Docker)

NPM (Já vem com o Node)

📥 Instalação
Clone o repositório:

Bash
git clone git@github.com:LucasMateus-2/Syscare-health_system.git
cd backend

Instale as dependências:

Bash
npm install
⚙️ Configuração do Banco de Dados
O projeto utiliza variáveis de ambiente para se conectar ao banco.

Crie um arquivo chamado .env na raiz do projeto.

Copie e cole o conteúdo abaixo, ajustando com as suas credenciais locais:

Snippet de código
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_aqui
DB_DATABASE=nome_do_banco
Nota: Certifique-se de criar o banco de dados no seu PostgreSQL manualmente antes de rodar o projeto.

🏃 Como Rodar
Bash
# Modo de desenvolvimento (com auto-reload)
npm run start:dev
A API estará disponível em: http://localhost:3000

📁 Estrutura do Projeto
src/ - Onde a mágica acontece.

*.entity.ts: Define as tabelas do banco de dados.

*.controller.ts: Define as rotas (URLs) da API.

*.service.ts: Onde fica a lógica de negócio.

*.dto.ts: Define o formato dos dados que recebemos nas requisições.

📜 Comandos Úteis
nest g resource <nome>: Cria um novo módulo completo (entidade, service, controller).

npm run build: Prepara o projeto para produção.
