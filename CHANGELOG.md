# Changelog — TEA Descomplica

## [1.0.0] — MVP 1.0 (2026-11-10)

### Primeira versão pública

Este é o lançamento inicial do TEA Descomplica, incorporando todas as funcionalidades desenvolvidas nas Etapas 01 a 04:

#### Etapa 01 — Fundação técnica e arquitetura
- Estrutura do projeto com Gradle Kotlin DSL e Version Catalog
- Namespace e applicationId: `br.com.teadescomplica`
- Min SDK 26, Target SDK 34, Java 17 toolchain
- Tela Início, Meus cenários, Configurações
- Persistência via DataStore Preferences (tema, animações)
- Tema claro e escuro
- Documentação completa (README, LICENSE, CHANGELOG, PRIVACY, FDROID_READINESS)

#### Etapa 02 — Preparação de experiências e planos alternativos
- Banco Room versão 2 com 5 entidades (scenarios, scenario_preparations, possible_situations, alternative_plans, personal_phrases)
- 5 DAOs com Flow reativo
- Migração explícita v1→v2 preservando dados existentes
- Telas de detalhes do cenário (4 seções) e consulta simplificada
- Navegação com sobreposição de telas

#### Etapa 03 — Minha Jornada, experiências e estratégias
- Banco Room versão 3 com 8 entidades (adicionadas: personal_experiences, personal_strategies, experience_strategy_cross_ref)
- Migração explícita v2→v3 preservando dados existentes
- Modelo de domínio: PersonalExperience, PersonalStrategy, ExperienceStrategyCrossRef
- Relacionamento opcional experiência-cenário (ON DELETE SET NULL)
- Relacionamento muitos-para-muitos experiência-estratégia (ON DELETE CASCADE)
- Telas: Minha Jornada, Registro de experiência, Detalhes da experiência, Minhas Estratégias
- Pesquisa offline por palavras-chave

#### Etapa 04 — Backup criptografado, restauração segura e privacidade
- Banco Room versão 3 mantida com todas as 8 entidades
- Modelo de domínio: BackupSnapshot
- Interface de repositório: BackupRepository
- Implementação: BackupRepositoryImpl com Storage Access Framework (SAF)
- Criptografia: CryptoManager com AES-256-GCM + PBKDF2-HMAC-SHA256 (600.000 iterações)
- Formato de arquivo: BackupFormat (.teadbackup, versão 1)
- Serialização: BackupSnapshotSerializer (JSON sem bibliotecas externas)
- ViewModel: BackupViewModel
- Tela: BackupRestoreScreen (criar/restaurar backup)
- Seção "Backup e restauração" na tela Configurações
- Navegação para tela de backup a partir de Configurações
- Métodos `deleteAll*` em todos os DAOs para restauração completa
- Testes unitários: criptografia, formato de backup, serialização, backup/restauração

### Alterado
- versionCode: 1, versionName: 1.0.0 (primeira versão pública)
- `DiModule.kt`: injetado `BackupRepository`
- `TeaNavigation.kt`: adicionada sobreposição de backup/restauração
- `SettingsScreen.kt`: adicionada seção de backup e restauração
- `AndroidManifest.xml`: sem permissão INTERNET, allowBackup=false, dataExtractionRules

### Documentação
- `BACKUP_FORMAT.md`: documentação detalhada do formato de arquivo de backup
- `SECURITY_AUDIT.md`: auditoria de segurança
- `ACCESSIBILITY_AUDIT.md`: auditoria de acessibilidade
- `TEST_MATRIX.md`: matriz de testes
- `RELEASE_CHECKLIST.md`: checklist de release
- `ETAPA_01_RELATORIO.md` a `ETAPA_05_RELATORIO.md`: relatórios técnicos de cada etapa

