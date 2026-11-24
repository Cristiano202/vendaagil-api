# 🚀 VendaÁgil API

API RESTful desenvolvida para gestão comercial, controle de estoque e registro de vendas multi-empresa (multi-tenant). Projeto focado em boas práticas de arquitetura back-end, segurança, testes unitários e integração contínua.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3 (Web, Data JPA, Security, Validation)
* **Banco de Dados & Migrações:** PostgreSQL & Flyway
* **Testes:** JUnit 5 & Mockito
* **Documentação:** SpringDoc OpenAPI (Swagger UI)
* **DevOps & CI/CD:** Docker & GitHub Actions

---

## ⚙️ Funcionalidades Principais

* **Autenticação e Autorização:** Controle de acesso seguro via tokens JWT.
* **Isolamento Multi-Empresa:** Produtos, usuários e vendas vinculados à respectiva empresa, bloqueando acessos ou operações cruzadas.
* **Gestão de Produtos e Estoque:** Cadastro de itens com validação e baixa automática de estoque no momento da venda.
* **Registro de Vendas:** Processamento transacional de pedidos com cálculo automático de totais e histórico de movimentações.

---

## 🚀 Como Executar Localmente

### Pré-requisitos
* Java 21 (JDK)
* Maven 3.9+
* PostgreSQL (ou Docker)

### Passo a passo

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/Cristiano202/vendaagil-api.git](https://github.com/Cristiano202/vendaagil-api.git)
   cd vendaagil-api
