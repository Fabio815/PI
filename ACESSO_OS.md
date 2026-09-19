# Sistema de Controle de Acesso para OS

## Resumo das Mudanças

Implementado um sistema completo de controle de acesso às Ordens de Serviço (OS) que respeita as seguintes regras:

- **Usuários FUNCIONÁRIO**: Podem editar apenas as OS que criaram
- **Usuários ADM**: Podem editar todas as OS

## Arquitetura

### 1. Autenticação
- **CustomUserDetails**: Implementa UserDetails do Spring Security
- **CustomUserDetailsService**: Carrega usuários do banco por email
- **AutenticacaoService**: Serviço centralizado para obter usuário autenticado

### 2. Autorização
- **AcessoNegadoException**: Exception lançada quando usuário não tem permissão
- **UsuarioNaoAutenticadoException**: Exception lançada quando usuário não está autenticado
- Validação implementada em `OsService.validarAcessoEditar()`

### 3. Segurança
- **SecurityConfig**: Configurado com autenticação por sessão
- Endpoints públicos: `/auth/**` e `/usuario/cadastrar`
- Todos os outros endpoints: Requerem autenticação

## Como Usar

### 1. Login
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@email.com",
  "senha": "senha123"
}

Resposta:
{
  "id": 1,
  "nome": "João",
  "email": "usuario@email.com",
  "chave": "FUNCIONARIO"
}
```

### 2. Criar OS
```bash
POST /os/cadastrar
Content-Type: application/json

{
  "clienteId": 1,
  "modelo": "Modelo X",
  "cor": "Azul",
  "situacao": "PENDENTE",
  "orcamento": {
    "valorServico": 100.00,
    "observacoes": "Observações",
    "itens": [
      {
        "pecaId": 1,
        "quantidade": 2
      }
    ]
  }
}

⚠️ IMPORTANTE: O usuarioId NÃO precisa ser enviado. 
O sistema automaticamente usa o usuário autenticado.
```

### 3. Editar OS
```bash
PUT /os/atualizar/{id}
Content-Type: application/json

{
  "clienteId": 1,
  "modelo": "Modelo Y",
  "cor": "Vermelho",
  "situacao": "CONCLUÍDO",
  "orcamento": {...}
}

✅ Permitido se:
  - Você é o criador da OS
  - Você é ADM

❌ Negado se:
  - Você é FUNCIONÁRIO e não criou a OS
```

### 4. Atualizar Status da OS
```bash
PUT /os/status/{id}

✅ Permitido se:
  - Você é o criador da OS
  - Você é ADM

❌ Negado se:
  - Você é FUNCIONÁRIO e não criou a OS
```

### 5. Logout
```bash
POST /auth/logout

Resposta:
"Logout realizado com sucesso!"
```

## Fluxo de Autenticação

```
1. Usuário faz login em /auth/login
   ↓
2. Spring Security autentica via CustomUserDetailsService
   ↓
3. Sessão HTTP criada
   ↓
4. Requisições subsequentes incluem o header de sessão
   ↓
5. AutenticacaoService recupera usuário do SecurityContext
   ↓
6. OsService valida acesso antes de editar
```

## Códigos de Erro

| Status | Erro | Motivo |
|--------|------|--------|
| 401 | `usuarioNaoAutenticado` | Não está logado |
| 403 | `acessoNegado` | Não tem permissão para editar |
| 404 | `usuarioNaoEncontrado` | Usuário não existe |
| 404 | `osNaoEncontrada` | OS não existe |

## Exemplo Completo (cURL)

```bash
# 1. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@email.com","senha":"senha123"}' \
  -c cookies.txt

# 2. Criar OS (usa cookies da sessão)
curl -X POST http://localhost:8080/os/cadastrar \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "clienteId": 1,
    "modelo": "Modelo X",
    "cor": "Azul",
    "situacao": "PENDENTE",
    "orcamento": {
      "valorServico": 100.00,
      "observacoes": "",
      "itens": [{"pecaId": 1, "quantidade": 1}]
    }
  }'

# 3. Editar OS
curl -X PUT http://localhost:8080/os/atualizar/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "clienteId": 1,
    "modelo": "Modelo Y",
    "cor": "Vermelho",
    "situacao": "CONCLUÍDO",
    "orcamento": {...}
  }'

# 4. Logout
curl -X POST http://localhost:8080/auth/logout \
  -b cookies.txt
```

## Testes Importantes

### ✅ Teste 1: Funcionário edita sua própria OS
1. Login como FUNCIONARIO
2. Criar OS → Sucesso
3. Editar a OS criada → ✅ Sucesso

### ✅ Teste 2: Funcionário tenta editar OS de outro
1. Login como FUNCIONARIO1
2. Criar OS → Sucesso, nota o ID
3. Logout
4. Login como FUNCIONARIO2
5. Tentar editar OS do FUNCIONARIO1 → ❌ 403 Forbidden

### ✅ Teste 3: ADM edita qualquer OS
1. Login como ADM
2. Editar qualquer OS → ✅ Sucesso

### ✅ Teste 4: Sem autenticação
1. Tentar criar/editar OS sem login → ❌ 401 Unauthorized

## Arquivos Modificados

### Criados:
- `AcessoNegadoException.java`
- `UsuarioNaoAutenticadoException.java`
- `AutenticacaoService.java`
- `CustomUserDetails.java`
- `CustomUserDetailsService.java`
- `LoginDTO.java`

### Modificados:
- `SecurityConfig.java` - Configuração de autenticação
- `OsService.java` - Validação de acesso
- `OsController.java` - Usa usuário autenticado
- `AuthController.java` - Endpoints de login/logout
- `UsuarioService.java` - Método carregarPorEmail
- `UsuarioNaoEncontradoException.java` - Novo construtor
- `AppExceptionHandler.java` - Handlers para novas exceptions

## Próximos Passos (Opcional)

1. **JWT Token**: Se precisar de API mobile, substitua sessão HTTP por JWT
2. **OAuth2**: Se precisar integrar com SSO (Google, GitHub, etc)
3. **Auditoria**: Adicionar logs de quem editou o quê e quando
4. **Validação Frontend**: Desabilitar botões de editar para usuários sem permissão