# TEST_MATRIX.md — Matriz de Testes — TEA Descomplica MVP 1.0

## Ambientes de Teste

| Dispositivo | Versão Android | Tema | Fonte | TalkBack | Status |
|-------------|---------------|------|-------|----------|--------|
| Emulador/Disp. | API 26 (Android 8.0) | Claro | Normal | Não | NÃO EXECUTADO |
| Emulador/Disp. | API 34 (Android 14) | Escuro | Ampliada | Sim | NÃO EXECUTADO |
| Emulador/Disp. | API 34 (Android 14) | Claro | Normal | Não | NÃO EXECUTADO |

*Nota: Não foi possível executar testes em dispositivo real ou emulador devido à
indisponibilidade de acesso ao repositório Maven do Google para compilação.*

## Matriz de Funcionalidades

### Tela Inicial

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Exibir nome "TEA Descomplica" | Texto visível e correto | NÃO EXECUTADO |
| Exibir slogan "Prepare-se do seu jeito." | Texto visível e correto | NÃO EXECUTADO |
| Exibir aviso médico | Texto visível e compreensível | NÃO EXECUTADO |
| Botão "Meus cenários" navega para lista | Navegação ocorre | NÃO EXECUTADO |
| Botão "Configurações" navega para configurações | Navegação ocorre | NÃO EXECUTADO |

### Meus Cenários

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Lista vazia exibe estado vazio | Mensagem amigável exibida | NÃO EXECUTADO |
| Criar novo cenário | Cenário salvo e exibido | NÃO EXECUTADO |
| Editar cenário existente | Alterações salvas | NÃO EXECUTADO |
| Excluir cenário com confirmação | Cenário removido | NÃO EXECUTADO |
| Cancelar exclusão | Cenário mantido | NÃO EXECUTADO |
| Título obrigatório vazio | Erro de validação exibido | NÃO EXECUTADO |
| Ordenação por updated_at | Mais recente primeiro | NÃO EXECUTADO |
| Navegar para detalhes | Tela de detalhes exibida | NÃO EXECUTADO |

### Detalhes do Cenário

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Exibir título do cenário | Título correto | NÃO EXECUTADO |
| Exibir preparação | Todos os campos exibidos | NÃO EXECUTADO |
| Adicionar situação possível | Situação salva e exibida | NÃO EXECUTADO |
| Editar situação | Alteração salva | NÃO EXECUTADO |
| Excluir situação com confirmação | Situação removida | NÃO EXECUTADO |
| Planos mantidos ao excluir situação | possibleSituationId = null | NÃO EXECUTADO |
| Adicionar plano alternativo | Plano salvo e exibido | NÃO EXECUTADO |
| Adicionar frase pessoal | Frase salva e exibida | NÃO EXECUTADO |
| Copiar frase para área de transferência | Texto copiado | NÃO EXECUTADO |

### Modo de Consulta

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Exibir título do cenário | Título correto | NÃO EXECUTADO |
| Exibir objetivo | Objetivo correto | NÃO EXECUTADO |
| Exibir situações possíveis | Lista correta | NÃO EXECUTADO |
| Exibir planos alternativos | Lista correta | NÃO EXECUTADO |
| Exibir frases pessoais | Lista correta | NÃO EXECUTADO |
| Sem modo de edição | Botões de edição não visíveis | NÃO EXECUTADO |

### Minha Jornada

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Lista vazia exibe estado vazio | Mensagem amigável exibida | NÃO EXECUTADO |
| Registrar nova experiência | Experiência salva | NÃO EXECUTADO |
| Experiência sem cenário | Salva com scenarioId = null | NÃO EXECUTADO |
| Pesquisar experiências | Resultados filtrados | NÃO EXECUTADO |
| Navegar para detalhes | Tela de detalhes exibida | NÃO EXECUTADO |

### Minhas Estratégias

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Lista vazia exibe estado vazio | Mensagem amigável exibida | NÃO EXECUTADO |
| Criar estratégia | Estratégia salva | NÃO EXECUTADO |
| Editar estratégia | Alteração salva | NÃO EXECUTADO |
| Excluir estratégia | Estratégia removida | NÃO EXECUTADO |
| Associar estratégia a experiência | Associação salva | NÃO EXECUTADO |
| Pesquisar estratégias | Resultados filtrados | NÃO EXECUTADO |

### Configurações

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Alterar tema para claro | Tema claro aplicado | NÃO EXECUTADO |
| Alterar tema para escuro | Tema escuro aplicado | NÃO EXECUTADO |
| Alterar tema para sistema | Segue dispositivo | NÃO EXECUTADO |
| Ativar reduzir animações | Animações reduzidas | NÃO EXECUTADO |
| Preferências persistem após reiniciar | Valores restaurados | NÃO EXECUTADO |
| Exibir versão do aplicativo | "1.0.0" exibido | NÃO EXECUTADO |
| Exibir contato do desenvolvedor | E-mail exibido | NÃO EXECUTADO |

### Backup e Restauração

| Caso de Teste | Resultado Esperado | Status |
|---------------|-------------------|--------|
| Criar backup com banco vazio | Arquivo .teadbackup criado | NÃO EXECUTADO |
| Criar backup com dados | Todos os dados incluídos | NÃO EXECUTADO |
| Criar backup com senha | Arquivo criptografado | NÃO EXECUTADO |
| Restaurar backup válido | Dados restaurados | NÃO EXECUTADO |
| Restaurar com senha incorreta | Erro de autenticação | NÃO EXECUTADO |
| Restaurar arquivo corrompido | Erro de integridade | NÃO EXECUTADO |
| Restaurar arquivo truncado | Erro de leitura | NÃO EXECUTADO |
| Restaurar versão incompatível | Erro de versão | NÃO EXECUTADO |
| Preservar relacionamentos após restauração | Associações mantidas | NÃO EXECUTADO |
| Preservar experiência ao excluir cenário | scenarioId = null | NÃO EXECUTADO |
| Não há permissão INTERNET | Manifesto sem permissão | NÃO EXECUTADO |
| Sem conta ou cadastro | Nenhuma tela de login | NÃO EXECUTADO |

## Testes Automatizados Implementados

| Classe de Teste | Cobertura | Status |
|-----------------|-----------|--------|
| CryptoManagerTest | Criptografia, descriptografia, integridade GCM | Implementado |
| BackupFormatTest | Formato de arquivo, magic number, versão | Implementado |
| BackupSnapshotSerializerTest | Serialização, deserialização, caracteres especiais | Implementado |
| BackupRepositoryTest | Backup vazio, com dados, caracteres especiais | Implementado |
| BackupRestoreTest | Restauração, senha incorreta, arquivo corrompido | Implementado |
| ScenarioRoomDatabaseTest | CRUD, ordenação, Flow, migração, cascata, SET NULL | Implementado |
| ScenarioPreparationRepositoryTest | CRUD preparação, campos opcionais | Implementado |
| PossibleSituationRepositoryTest | CRUD situações, ordenação | Implementado |
| AlternativePlanRepositoryTest | CRUD planos, reordenação | Implementado |
| PersonalPhraseRepositoryTest | CRUD frases, ordenação | Implementado |
| PersonalExperienceRepositoryTest | CRUD experiências, consulta por texto | Implementado |
| PersonalStrategyRepositoryTest | CRUD estratégias, ordenação, associação | Implementado |
| PersonalJourneyViewModelTest | Estado inicial, carregar, pesquisar | Implementado |
| StrategyLibraryViewModelTest | Carregar estratégias, excluir | Implementado |
| ScenarioDetailViewModelTest | Carregar, salvar, adicionar/excluir | Implementado |
| PreparationQueryViewModelTest | Modo consulta, estado inicial | Implementado |

**Total de classes de teste: 16**

## Limitações

- Testes não executados devido à falta de acesso ao repositório Maven do Google
- Testes instrumentados (Compose UI, AndroidTest) não executados
- Testes em dispositivo real não executados
