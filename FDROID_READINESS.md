# FDROID_READINESS.md — Checklist de Preparação para F-Droid

O TEA Descomplica está sendo desenvolvido com o objetivo de ser distribuído via F-Droid. Este documento contém uma checklist de preparação. **O aplicativo ainda não está aprovado pelo F-Droid.**

## Versão Atual

Versão: 1.0.0 (versionCode: 1)

## Requisitos do F-Droid

| Requisito | Status | Observações |
|-----------|--------|-------------|
| Código-fonte aberto | ✅ | Licença GPL-3.0-or-later |
| Dependências livres e auditáveis | ⚠️ | AndroidX Compose, Room, KSP requerem acesso ao repositório do Google para build |
| Sem Firebase | ✅ | Não utilizado |
| Sem Google Play Services obrigatórios | ✅ | Não utilizado |
| Sem bibliotecas proprietárias | ✅ | Apenas AndroidX, Kotlin stdlib, Room |
| Sem SDKs de publicidade | ✅ | Não utilizado |
| Sem downloads de código executável em runtime | ✅ | Não utilizado |
| Sem serviços remotos necessários | ✅ | Funcionamento totalmente offline |
| Gradle Wrapper versionado | ✅ | Gradle 8.10.2 |
| Instruções de compilação no README | ✅ | Veja README.md |
| Sem permissão INTERNET | ✅ | Não declarada no manifest |
| `allowBackup="false"` | ✅ | Declarado no manifest |
| `android:exported` correto | ✅ | Apenas MainActivity exportado |
| Banco de dados local privado | ✅ | Room SQLite no armazenamento privado do app |
| Backup criptografado | ✅ | AES-256-GCM + PBKDF2, senha do usuário |
| Sem dependência Google Drive | ✅ | SAF permite qualquer provedor compatível |

## Pendências para Submissão ao F-Droid

- [ ] Verificar se todas as dependências são construíveis a partir de código-fonte livre.
- [ ] Gerar APK assinado para submissão.
- [ ] Preparar metadados do F-Droid (descrição, categorias, licença).
- [ ] Verificar conformidade com as políticas de build do F-Droid (reproducible builds).
- [ ] Executar compilação completa em ambiente com acesso ao repositório do Google.
- [ ] Executar testes automatizados e corrigir falhas.
- [ ] Executar Android Lint e corrigir avisos.
- [ ] Testar em emulador/dispositivo real.

## Limitações Conhecidas

Este ambiente não possui acesso ao repositório Maven do Google (`dl.google.com`), o que impediu a execução da compilação completa e dos testes automatizados. A compilação e os testes devem ser executados em um ambiente com acesso aos repositórios padrão do Android.

## Criptografia e F-Droid

O TEA Descomplica utiliza criptografia padrão (AES-256-GCM, PBKDF2-HMAC-SHA256) implementada com bibliotecas Java Cryptography Architecture (JCA) do próprio Android. Não há dependências proprietárias ou de terceiros para criptografia.
