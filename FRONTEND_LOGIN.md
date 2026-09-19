# Frontend - Login e Controle de Acesso

## Mudanças Implementadas

### 1. Serviço de Sessão (SessaoUtil)
- Gerencia dados do usuário logado em localStorage
- Métodos disponíveis:
  - `definirUsuarioLogado(usuario)` - Armazena usuário após login
  - `obterUsuarioLogado()` - Recupera usuário armazenado
  - `obterIdUsuarioLogado()` - Retorna ID do usuário
  - `obterPerfilUsuarioLogado()` - Retorna perfil (ADM/FUNCIONARIO)
  - `ehAdministrador()` - Verifica se é ADM
  - `ehFuncionario()` - Verifica se é FUNCIONARIO
  - `podeEditarOs(usuarioCriadorId)` - Valida se pode editar uma OS
  - `limparSessao()` - Remove dados após logout

### 2. Tela de Login (LoginPanel)
**Mudanças:**
- Endpoint alterado de `/usuarios/login` → `/auth/login`
- Armazena usuário autenticado via SessaoUtil
- Resposta esperada:
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "chave": "FUNCIONARIO"
}
```

### 3. Cadastro de OS (CadastroOsWindow)
**Mudanças:**
- Usuário ID é obtido automaticamente do usuário logado
- Não precisa mais enviar `usuarioId` manualmente
- Valida se usuário está autenticado antes de criar

### 4. Atualizar OS (AtualizarOsWindow)
**Mudanças:**
- Valida se usuário tem permissão de editar ao carregar
- Se sem permissão:
  - Desabilita todos os campos
  - Desabilita botão "Salvar"
  - Muda título para "Visualizar OS (Sem permissão de edição)"
  - Mostra mensagem de erro se tentar editar
- Armazena ID do criador para validação

### 5. Logout (Main)
**Mudanças:**
- Botão "Logout" adicionado no header
- Faz requisição para `/auth/logout`
- Limpa sessão do usuário (localStorage)
- Redireciona para home

## Fluxo de Uso

### 1. Login
```
Usuário acessa /
↓
Tela de login carrega LoginPanel
↓
Usuário digita email e senha
↓
Click "Enviar" → POST /auth/login
↓
Resposta com dados do usuário
↓
SessaoUtil.definirUsuarioLogado(usuario)
↓
Redireciona para Main (dashboard)
```

### 2. Criar OS
```
Usuário em Main → Click "Cadastro de OS"
↓
Click "Adicionar"
↓
Abre CadastroOsWindow
↓
Preenche dados
↓
Click "Cadastar"
↓
usuarioId = SessaoUtil.obterIdUsuarioLogado()
↓
POST /os/cadastrar (com usuarioId do usuário logado)
↓
OS criada com sucesso
```

### 3. Editar OS
```
Usuário em Main → Click "Cadastro de OS"
↓
Seleciona uma OS
↓
Click "Editar"
↓
Abre AtualizarOsWindow
↓
GET /os/{id} carrega dados da OS
↓
Valida: SessaoUtil.podeEditarOs(usuarioCriador)
  ✅ Se SIM: Campos ativados, pode editar
  ❌ Se NÃO: Campos desativados, apenas visualizar
↓
Se editar: PUT /os/atualizar/{id}
```

### 4. Logout
```
Click "Logout" no header
↓
POST /auth/logout
↓
SessaoUtil.limparSessao()
↓
Redireciona para home (tela de login)
```

## Comportamento por Perfil

### FUNCIONÁRIO
- ✅ Pode criar OS
- ✅ Pode editar suas próprias OS
- ❌ Não pode editar OS de outros FUNCIONÁRIO
- ✅ Pode visualizar todas as OS
- ❌ Não tem acesso a "Usuários"

### ADM
- ✅ Pode fazer tudo
- ✅ Pode editar qualquer OS
- ✅ Tem acesso a "Usuários"

## Validação do Lado do Cliente (Frontend)

O SessaoUtil valida antes de tentar editar:
```javascript
// No AtualizarOsWindow
if (!SessaoUtil.podeEditarOs(os.usuario.id)) {
    me.desabilitarEdicao(view);
}
```

Isso melhora a UX desabilitando botões/campos antes de fazer a requisição.

## Validação do Lado do Servidor (Backend)

O OsService também valida para segurança:
```java
// No atualizarOs()
validarAcessoEditar(os);  // Lança AcessoNegadoException se sem permissão
```

Nunca confie apenas no cliente!

## LocalStorage

Dados armazenados em `localStorage`:
```json
{
  "usuarioLogado": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "chave": "FUNCIONARIO"
  }
}
```

Limpo ao fazer logout via `SessaoUtil.limparSessao()`

## Tratamento de Erros

### 401 Unauthorized
- Usuário não autenticado
- Redireciona para login

### 403 Forbidden
- Usuário não tem permissão
- Mostra mensagem: "Você não tem permissão para editar esta OS."

### 400 Bad Request
- Validação falhou
- Exibe erro específico

## Arquivos Modificados

### Criados:
- `SessaoUtil.js` - Serviço de gerenciamento de sessão

### Modificados:
- `LoginPanel.js` - Login atualizado
- `CadastroOsWindow.js` - Usa usuário autenticado
- `AtualizarOsWindow.js` - Validação de acesso
- `Main.js` - Botão de logout
- `MainController.js` - Método logout

## Testes no Frontend

### Teste 1: Login com sucesso
```javascript
// Email: funcionario@email.com
// Senha: senha123
// Esperado: localStorage com usuário e redirecionamento para Main
```

### Teste 2: Verificar SessaoUtil
```javascript
// No console:
SessaoUtil.obterUsuarioLogado()  // {id: 1, nome: "...", ...}
SessaoUtil.obterPerfilUsuarioLogado()  // "FUNCIONARIO"
SessaoUtil.ehAdministrador()  // false
```

### Teste 3: Criar OS
```javascript
// Abre CadastroOsWindow
// Preenche formulário
// Verifica que usuarioId é enviado corretamente (sem hardcode)
```

### Teste 4: Editar OS própria
```javascript
// Login como FUNCIONARIO1
// Cria uma OS
// Tenta editar → ✅ Sucesso
```

### Teste 5: Tentar editar OS de outro
```javascript
// Login como FUNCIONARIO1, nota ID da OS criada
// Logout
// Login como FUNCIONARIO2
// Tenta editar OS do FUNCIONARIO1
// → Campos desativados + mensagem de erro
```

### Teste 6: ADM edita qualquer OS
```javascript
// Login como ADM
// Tenta editar OS de qualquer FUNCIONARIO
// → ✅ Sucesso
```

### Teste 7: Logout
```javascript
// Click "Logout"
// Verifica localStorage vazio
// Página redireciona para login
```

## Notas Importantes

1. **localStorage não é seguro** - Não armazene dados sensíveis como senhas
2. **Validação do servidor é obrigatória** - O frontend apenas melhora UX
3. **Sessão HTTP** - O backend usa sessão HTTP, cookies são enviados automaticamente
4. **Persist Data** - Se usuário recarrega página, sessão está guardada em localStorage
5. **Logout completo** - Tanto frontend (localStorage) quanto backend (sessão) são limpos

## Próximas Melhorias Sugeridas

1. Adicionar foto/avatar do usuário no header
2. Mostrar nome do usuário logado
3. Confirmar logout antes de deletar sessão
4. Adicionar timeout de sessão (logout automático após inatividade)
5. Integração com JWT ao invés de sessão HTTP
