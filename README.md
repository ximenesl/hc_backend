*Leia em: [Português](#senac-hc-gestao-de-horas-complementares) | [English](#senac-hc-complementary-hours-management)*

---

# 🏥 SENAC HC: Gestao de Horas Complementares

> Uma plataforma web para gerenciamento e validação de horas complementares de alunos de graduação, conectando discentes, coordenadores e administradores em tempo real. Desenvolvido como Projeto Integrador para o **Curso de Tecnologia em Análise e Desenvolvimento de Sistemas** do **Centro Universitário Senac**.

[![Licença](https://img.shields.io/badge/licenca-MIT-green)](LICENSE)
[![Senac](https://img.shields.io/badge/Instituicao-Senac-blue)](https://www.sp.senac.br/)
[![Conformidade LGPD](https://img.shields.io/badge/Conformidade-LGPD%20Ready-blueviolet)](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm)

---

## 📋 Visão Geral do Projeto

O **SENAC HC** é um ecossistema digital projetado para otimizar o fluxo de envio, validação e controle de horas complementares (atividades acadêmico-científico-culturais). A plataforma permite que os alunos enviem certificados digitalizados, escolhendo as categorias adequadas, e acompanhem seu progresso em tempo real em relação à carga horária exigida pelo seu curso. Coordenadores e administradores dispõem de um painel de validação centralizado para avaliar, deferir ou indeferir certificados, inserindo a carga horária final computada.

### Funcionalidades Principais

* **Processamento Automatizado com OCR:** Extração automática de dados de certificados em PDF ou imagem (carga horária via padrões regex e sugestão de título via palavras-chave de tecnologias), otimizando o fluxo de validação.
* **Mecanismo Flexível de Regras:** Cadastro e controle dinâmico de regras de validação por curso (limite máximo de aproveitamento, divisão por grupos de atividades e requisitos específicos).
* **Controle de Usuários e Perfis:** Níveis de acesso seguro baseados em perfis (Aluno, Coordenador e Administrador) gerenciados via autenticação robusta JWT.
* **Comunicação por E-mail:** Integração com serviços de e-mail (Resend e Brevo) para redefinição de senhas e alertas do sistema.

---

## 🔒 LGPD & Privacidade de Dados (Lei Geral de Proteção de Dados)

Por processar dados pessoais cadastrais (como nome, registro acadêmico/RA, e-mail e vínculo de curso) e documentos contendo dados de terceiros e dos próprios discentes (certificados), o desenvolvimento do SENAC HC pautou-se pelas diretrizes de privacidade por design, em conformidade com a Lei Federal nº 13.709/2018 (LGPD).

### Padrões de Privacidade Implementados:

* **Base Legal para Tratamento (Art. 7º, V & XI):** O tratamento dos dados cadastrais e o armazenamento de certificados são fundamentados na execução de contrato ou procedimentos preliminares (relação educacional entre o aluno e a instituição de ensino), sendo indispensáveis para a execução e comprovação acadêmica.
* **Minimização e Segurança dos Dados:** Apenas as informações estritamente necessárias para a validação da carga horária são solicitadas. Os arquivos de certificados enviados são armazenados de forma restrita e protegida, de modo que apenas os coordenadores e administradores autorizados possam acessá-los para auditoria e conferência.
* **Direitos do Titular (Art. 18):** O sistema dispõe de recursos que garantem ao estudante:
  * Confirmação da existência de tratamento e acesso aos seus dados de progresso e cadastro.
  * Correção de dados incompletos, inexatos ou desatualizados.
  * Portabilidade dos dados por meio de consultas e acompanhamento direto na interface.
* **Segurança da Informação (Art. 46):** Toda a comunicação com a API é protegida por tokens de autenticação JWT (JSON Web Tokens). As senhas dos usuários são criptografadas no banco de dados utilizando algoritmos de hash seguros (bcrypt), impedindo a visualização em plaintext mesmo por administradores do banco.

---

## 🛠️ Tecnologias Utilizadas (Backend)

* **Core Backend:** Java 17, Spring Boot 3.2.4 (Spring Web, Spring Security, Validation)
* **Banco de Dados:** PostgreSQL (produção), H2 Database (desenvolvimento e testes)
* **Mapeamento & Persistência:** Spring Data JPA, Hibernate ORM
* **OCR & Leitura de Documentos:** Tess4J (Tesseract OCR wrapper) v5.12.0, Apache PDFBox v2.0.31
* **Comunicação & Segurança:** JJWT (JSON Web Token) v0.12.5, Spring Boot Mail, Resend Java SDK v3.1.0
* **Documentação de API:** Springdoc OpenAPI / Swagger UI v2.5.0
* **Testes:** Spring Boot Test, JUnit 5, Mockito

---

## ⚙️ Configuração e Execução Local (Desenvolvimento)

Siga os passos abaixo para configurar e executar o ambiente do backend localmente.

### 1. Pré-requisitos
Certifique-se de possuir instalado em sua máquina:
* **Java JDK 17**
* **Maven** (ou uso do wrapper do Maven `mvnw` incluso no diretório)
* **Git**
* Banco de dados **PostgreSQL** instalado e ativo (opcional, caso não queira utilizar o H2 em memória padrão)

### 2. Configuração (`application.yml` ou variáveis de ambiente)
O backend pode ser configurado através do arquivo [application.yml](file:///c:/Users/Joelson/Desktop/hc/hc_backend/src/main/resources/application.yml). Por padrão, ele executa com banco H2 em memória na porta `8080`. Para customizar as conexões ou integrar com serviços externos, você pode configurar as seguintes variáveis de ambiente:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nome_do_banco
SPRING_DATASOURCE_USERNAME=usuario_postgres
SPRING_DATASOURCE_PASSWORD=senha_postgres
RESEND_API_KEY=sua_chave_resend_para_envio_de_emails
BREVO_API_KEY=sua_chave_brevo_para_envio_de_emails
```

### 3. Configuração e Inicialização
Abra o terminal no diretório raiz do backend (`hc_backend`):

```bash
# 1. Limpar e compilar o projeto com Maven
mvn clean install

# 2. Executar a aplicação
mvn spring-boot:run
```

O banco de dados será populado automaticamente pelo `AdminSeeder` com dados de teste no primeiro início.
* **Console do H2:** Disponível em `http://localhost:8080/h2-console`
  * **JDBC URL:** `jdbc:h2:mem:certificado_db`
  * **User:** `sa` | **Password:** (em branco)
* **Documentação Swagger/OpenAPI:** Acessível em `http://localhost:8080/swagger-ui.html`

#### Usuários de Teste Semeados:
* **Administrador:** `admin@admin.com` | senha: `admin123`
* **Coordenador:** `joelsonjose222@gmail.com` | senha: `joelson123` (Cursos: ADS e Jogos Digitais)
* **Alunos (Turma ADS-2024-1):** `aluno.ads0@teste.com` a `aluno.ads4@teste.com` | senha: `aluno123`
* **Alunos (Turma JOG-2024-1):** `aluno.jogos0@teste.com` a `aluno.jogos4@teste.com` | senha: `aluno123`

---

## 📊 Endpoints Principais da API

| Método | Endpoint | Descrição | Escopo / Regra de Acesso |
|---|---|---|---|
| POST | `/api/auth/login` | Realiza a autenticação de usuários e gera o token JWT | Público |
| POST | `/api/auth/forgot-password` | Envia e-mail com token para recuperação de senha | Público |
| POST | `/api/auth/change-password` | Efetua a alteração de senha do usuário logado | Autenticado |
| POST | `/api/certificates` | Realiza o envio de um novo certificado (multipart/form-data) | Aluno |
| POST | `/api/certificates/ocr` | Executa OCR em imagem/PDF para detectar título e horas | Aluno |
| GET | `/api/certificates` | Lista todos os certificados para coordenação/administração | Coordenador, Admin |
| GET | `/api/certificates/me/{alunoId}` | Retorna os certificados enviados por um aluno específico | Aluno, Coordenador, Admin |
| PUT | `/api/certificates/{id}/status` | Altera o status e valida a carga horária de um certificado | Coordenador, Admin |
| GET | `/api/certificates/{id}/file` | Realiza o download/visualização do arquivo do certificado | Autenticado |
| GET | `/api/users/me` | Retorna os dados do perfil do usuário logado | Autenticado |
| POST | `/api/users` | Cria um novo usuário no sistema | Coordenador, Admin |
| GET | `/api/regras/curso/{cursoId}` | Lista as regras e limites de aproveitamento de horas do curso | Público |

---

## 📝 Melhorias Futuras

* **Refinamento do OCR com NLP:** Implementação de técnicas de Processamento de Linguagem Natural (NLP) para leitura de entidades avançada e redução de falsos positivos na extração de dados.
* **Painel de Auditoria e Logs:** Histórico detalhado de ações efetuadas por coordenadores para maior transparência e controle operacional.
* **Assinatura Digital de Relatórios:** Geração automática da ficha consolidada assinada digitalmente com chaves criptográficas.
* **Integração com Armazenamento em Nuvem:** Armazenamento seguro de arquivos de certificados em serviços de nuvem como AWS S3 ou Azure Blob Storage.

---

## 👥 Autores & Equipe do Projeto

* **Homero Flávio**
* **Joelson José**
* **Kallyne Melo**
* **Lucas Ximenes**
* **Marcelly Arcanjo**
* **Nicollas Abrão**
* **Thayanne Rodrigues**

### Orientadores Acadêmicos
* **Professor Orientador / Coordenador:** Prof. ____________
* **Professor de Inglês Técnico:** Prof. Leonardo Trevas

---
---

# 🏥 SENAC HC: Complementary Hours Management

> A web platform for managing and validating complementary hours for undergraduate students, connecting students, coordinators, and administrators in real-time. Developed as a Capstone Project (*Projeto Integrador*) for the **Systems Analysis and Development Program** at **Senac College**.

[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Senac](https://img.shields.io/badge/Institution-Senac%20College-blue)](https://www.sp.senac.br/)
[![LGPD Compliance](https://img.shields.io/badge/Compliance-LGPD%20Ready-blueviolet)](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm)

---

## 📋 Project Overview

**SENAC HC** is a digital ecosystem designed to optimize the workflow of submitting, validating, and monitoring complementary hours (academic, scientific, and cultural activities). The platform allows students to upload digitalized certificates, choose the appropriate category, and track their progress in real-time against the total hours required by their curriculum. Coordinators and administrators are provided with a centralized validation dashboard to evaluate, approve, or reject certificates, entering the final validated hours.

### Key Features

* **OCR File Processing:** Automatic extraction of data from PDF or image certificates (such as workload extraction using regex patterns and title suggestions using technology keywords) to streamline validation.
* **Course-Specific Rules Engine:** Flexible creation of validation rules per course (such as percentage caps, activity groups, and specific requirements) ensuring academic compliance.
* **User Management & Secure Auth:** Role-based secure access profiles (Student, Coordinator, and Administrator) with JWT authentication and bcrypt password hashing.
* **Email Notification System:** Integration with email services (Resend and Brevo) for secure credential recovery and system notifications.

---

## 🔒 LGPD & Data Privacy Compliance (Lei Geral de Proteção de Dados)

Because this application processes personal registration data (such as name, academic record/RA, email, and course enrollment) and documents containing personal details of students and third parties (certificates), privacy by design was a core development guideline, in compliance with Brazilian Federal Law nº 13.709/2018 (LGPD).

### Implemented Privacy Standards:

* **Legal Basis for Processing (Art. 7, V & XI):** The processing of registration data and certificate storage is grounded on the performance of a contract or preliminary procedures (educational services agreement between the student and the educational institution), being indispensable for graduation auditing and academic validation.
* **Data Minimization & Security:** Only information strictly necessary for validation is requested. Uploaded certificate files are stored in a restricted and secure manner, ensuring only authorized coordinators and administrators can access them for audit and verification purposes.
* **User Rights Panel (Art. 18):** The system provides features that guarantee students:
  * Access and confirmation of the existence of data processing regarding their progress and profile.
  * Correction of incomplete, inaccurate, or outdated records.
  * Portability of personal records through direct tracking on the dashboard interface.
* **Security (Art. 46):** All API communication is secured using JWT (JSON Web Tokens). User passwords are encrypted in the database using secure hashing algorithms (bcrypt), preventing plaintext visibility even to database administrators.

---

## 🛠️ Tech Stack (Backend)

* **Core Backend:** Java 17, Spring Boot 3.2.4 (Spring Web, Spring Security, Validation)
* **Database:** PostgreSQL (production), H2 Database (local development and testing)
* **Mapping & Persistence:** Spring Data JPA, Hibernate ORM
* **OCR & Document Parsing:** Tess4J (Tesseract OCR wrapper) v5.12.0, Apache PDFBox v2.0.31
* **Communication & Security:** JJWT (JSON Web Token) v0.12.5, Spring Boot Mail, Resend Java SDK v3.1.0
* **API Documentation:** Springdoc OpenAPI / Swagger UI v2.5.0
* **Testing:** Spring Boot Test, JUnit 5, Mockito

---

## ⚙️ Getting Started (Local Development)

Follow the steps below to configure and run the backend environment locally.

### 1. Prerequisites
Ensure you have installed on your machine:
* **Java JDK 17**
* **Maven** (or use the Maven wrapper `mvnw` included in the backend directory)
* **Git**
* **PostgreSQL** instance running locally (optional, if you do not want to use H2 in-memory)

### 2. Configuration (`application.yml` or Environment Variables)
The backend can be configured via the [application.yml](file:///c:/Users/Joelson/Desktop/hc/hc_backend/src/main/resources/application.yml) file. By default, it runs with an H2 in-memory database on port `8080`. For custom setups, you can configure the following environment variables:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_name
SPRING_DATASOURCE_USERNAME=postgres_user
SPRING_DATASOURCE_PASSWORD=postgres_password
RESEND_API_KEY=your_resend_api_key_for_emails
BREVO_API_KEY=your_brevo_api_key_for_emails
```

### 3. Setup and Execution
Open a terminal in the backend directory (`hc_backend`):

```bash
# 1. Clean and package the application
mvn clean install

# 2. Run the application
mvn spring-boot:run
```

The database will be automatically seeded with test data on the first run by `AdminSeeder`.
* **H2 Console:** Accessible at `http://localhost:8080/h2-console`
  * **JDBC URL:** `jdbc:h2:mem:certificado_db`
  * **User:** `sa` | **Password:** (blank)
* **Swagger/OpenAPI Documentation:** Accessible at `http://localhost:8080/swagger-ui.html`

#### Seeded Test Users:
* **Administrator:** `admin@admin.com` | password: `admin123`
* **Coordinator:** `joelsonjose222@gmail.com` | password: `joelson123` (Courses: ADS and Digital Games)
* **Students (ADS-2024-1 Class):** `aluno.ads0@teste.com` to `aluno.ads4@teste.com` | password: `aluno123`
* **Students (JOG-2024-1 Class):** `aluno.jogos0@teste.com` to `aluno.jogos4@teste.com` | password: `aluno123`

---

## 📊 Core API Endpoints

| Method | Endpoint | Description | Scope / Access Rule |
|---|---|---|---|
| POST | `/api/auth/login` | Authenticates users and generates JWT token | Public |
| POST | `/api/auth/forgot-password` | Sends password reset email with token | Public |
| POST | `/api/auth/change-password` | Securely updates user password | Authenticated |
| POST | `/api/certificates` | Uploads a new certificate (multipart/form-data) | Student |
| POST | `/api/certificates/ocr` | Executes OCR on image/PDF to detect title and hours | Student |
| GET | `/api/certificates` | Lists all certificates for coordination/administration | Coordinator, Admin |
| GET | `/api/certificates/me/{alunoId}` | Retrieves all certificates uploaded by a specific student | Student, Coordinator, Admin |
| PUT | `/api/certificates/{id}/status` | Updates certificate status and sets validated hours | Coordinator, Admin |
| GET | `/api/certificates/{id}/file` | Downloads or views the certificate file | Authenticated |
| GET | `/api/users/me` | Returns profile data of the currently logged-in user | Authenticated |
| POST | `/api/users` | Creates a new user in the system | Coordinator, Admin |
| GET | `/api/regras/curso/{cursoId}` | Lists rule limits and hour validation types for a course | Public |

---

## 📝 Future Improvements

* **OCR Refinement with NLP:** Implement Natural Language Processing (NLP) techniques for advanced entity recognition and reduced false positives in workload extraction.
* **Audit Trail & Action Logs:** Maintain a detailed log history of certificate actions taken by coordinators for audit transparency.
* **Digital Signatures for Reports:** Automatically generate and digitally sign the complementary hours consolidation document.
* **Cloud Storage Integration:** Secure storage of uploaded certificate files in cloud environments such as AWS S3 or Azure Blob Storage.

---

## 👥 Authors & Project Team

* **Homero Flávio**
* **Joelson José**
* **Kallyne Melo**
* **Lucas Ximenes**
* **Marcelly Arcanjo**
* **Nicollas Abrão**
* **Thayanne Rodrigues**

### Academic Advisors
* **Academic Advisor / Professor:** Prof. ____________
* **Technical English Course Professor:** Prof. Leonardo Trevas
