# RELEASE_CHECKLIST.md — Checklist de Release — TEA Descomplica MVP 1.0

## Pré-Release

### Código e Arquitetura
- [ ] Código compila sem erros
- [ ] Não há dependências desnecessárias
- [ ] Não há código morto
- [ ] Não há funções vazias
- [ ] Arquitetura MVVM + Repository respeitada
- [ ] Não há acesso a DAOs diretamente de telas Compose
- [ ] Não há operações de banco na thread principal
- [ ] Versão: 1.0.0 / versionCode: 1

### Funcionalidades Obrigatórias

#### Meus Cenários
- [ ] Criar cenário
- [ ] Consultar cenário
- [ ] Editar cenário
- [ ] Excluir cenário com confirmação
- [ ] Persistir dados
- [ ] Exibir estado vazio
- [ ] Ordenar registros corretamente

#### Minha Preparação
- [ ] Informações antecipadas
- [ ] Data opcional
- [ ] Local informado manualmente
- [ ] Informações importantes
- [ ] Observações
- [ ] Persistência correta

#### Situações Possíveis
- [ ] Criar
- [ ] Editar
- [ ] Excluir
- [ ] Associar ao cenário correto

#### Planos Alternativos
- [ ] Criar
- [ ] Editar
- [ ] Excluir
- [ ] Associar opcionalmente a situações
- [ ] Preservar planos quando situação excluída

#### Frases Pessoais
- [ ] Criar
- [ ] Editar
- [ ] Excluir
- [ ] Copiar texto

### Minha Jornada
- [ ] Registrar experiência
- [ ] Editar experiência
- [ ] Excluir experiência
- [ ] Consultar histórico
- [ ] Pesquisar
- [ ] Relacionar com cenário
- [ ] Preservar experiências quando cenário excluído

### Minhas Estratégias
- [ ] Criar
- [ ] Editar
- [ ] Excluir
- [ ] Pesquisar
- [ ] Associar a experiências
- [ ] Reutilizar em várias experiências

### Configurações
- [ ] Tema claro
- [ ] Tema escuro
- [ ] Tema do sistema
- [ ] Preferência de animações reduzidas
- [ ] Persistência das preferências
- [ ] Informações de privacidade

### Backup e Restauração
- [ ] Criar backup
- [ ] Criptografar backup
- [ ] Selecionar destino (SAF)
- [ ] Restaurar backup
- [ ] Validar senha
- [ ] Rejeitar arquivos adulterados
- [ ] Preservar relacionamentos
- [ ] Tratar falhas sem perda silenciosa

## Privacidade e Segurança

- [ ] Sem permissão INTERNET no AndroidManifest
- [ ] android:allowBackup="false"
- [ ] dataExtractionRules configurado
- [ ] android:exported corretamente (apenas MainActivity)
- [ ] Nenhuma conta ou cadastro
- [ ] Nenhum servidor ou backend
- [ ] Nenhum Firebase
- [ ] Nenhum analytics ou telemetria
- [ ] Nenhuma publicidade
- [ ] Nenhum SDK de rastreamento
- [ ] Senha não armazenada em Room/DataStore
- [ ] Senha não registrada em logs
- [ ] Backup iniciado exclusivamente pelo usuário
- [ ] Uso do Storage Access Framework (SAF)
- [ ] Não depende diretamente do Google Drive

## Código e Qualidade

- [ ] Código Kotlin idiomático
- [ ] Imutabilidade sempre que possível
- [ ] Tratamento explícito de erros
- [ ] Coroutines estruturadas
- [ ] Sem avisos críticos ignorados
- [ ] Decisões arquiteturais documentadas

## Testes

- [ ] Testes de persistência Room implementados
- [ ] Testes de DataStore implementados
- [ ] Testes de ViewModels implementados
- [ ] Testes de interface Compose implementados
- [ ] Testes de criptografia implementados
- [ ] Testes de backup/restauração implementados
- [ ] Testes de migração implementados
- [ ] Testes de integridade referencial implementados

## Documentação

- [ ] README.md atualizado
- [ ] CHANGELOG.md atualizado
- [ ] PRIVACY.md atualizado
- [ ] FDROID_READINESS.md atualizado
- [ ] BACKUP_FORMAT.md criado
- [ ] SECURITY_AUDIT.md criado
- [ ] ACCESSIBILITY_AUDIT.md criado
- [ ] TEST_MATRIX.md criado
- [ ] RELEASE_CHECKLIST.md criado
- [ ] ETAPA_05_RELATORIO.md criado

## Metadados F-Drift (opcional para submissão futura)

- [ ] fastlane/metadata/android/pt-BR/title.txt
- [ ] fastlane/metadata/android/pt-BR/short_description.txt
- [ ] fastlane/metadata/android/pt-BR/full_description.txt
- [ ] fastlane/metadata/android/pt-BR/changelogs/1.txt
- [ ] Screenshots (quando disponíveis)

## Aprovação para Release

| Critério | Status |
|----------|--------|
| Aplicativo compila | PENDENTE (ambiente sem dl.google.com) |
| Testes executáveis passam | PENDENTE (ambiente sem dl.google.com) |
| Lint passa | PENDENTE (ambiente sem dl.google.com) |
| Não há falhas críticas | APROVADO (código revisado) |
| Não há permissão INTERNET | APROVADO |
| Não há cadastro obrigatório | APROVADO |
| Não há rastreamento | APROVADO |
| Interface acessível | APROVADO (com ressalvas menores) |
| Documentação consistente | APROVADO |

**Veredito: PENDENTE (aguardando compilação e testes em ambiente adequado)**
