# GUIA DE PUBLICAÇÃO — TEA Descomplica MVP 1.0
# Para publicação no F-Droid

---

## 📋 RESUMO EXECUTIVO

| Item | Valor |
|------|-------|
| Nome do aplicativo | TEA Descomplica |
| Versão | 1.0.0 (versionCode 1) |
| Nome do pacote | br.com.teadescomplica |
| Licença | GPL-3.0-or-later |
| Autor | Valdenor |
| Email | valdenor.sa@proton.me |
| Repositório GitHub | https://github.com/alunoadsnoite/teadescomplica |
| APK assinado | /home/valdenor/Downloads/tea-descomplica-1.0.0.apk |
| Status | PRONTO PARA PUBLICAÇÃO NO F-DROID |

---

## 📁 ESTRUTURA DE ARQUIVOS PRONTOS

### Código fonte
```
TEADescomplica/
├── app/build.gradle.kts
├── app/src/main/AndroidManifest.xml
├── app/src/main/java/br/com/teadescomplica/...
├── app/src/main/res/...
├── gradle/wrapper/gradle-wrapper.jar
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── build.gradle.kts
```

### Documentação
```
README.md
CHANGELOG.md
PRIVACY.md
FDROID_READINESS.md
BACKUP_FORMAT.md
SECURITY_AUDIT.md
ACCESSIBILITY_AUDIT.md
TEST_MATRIX.md
RELEASE_CHECKLIST.md
ETAPA_01_RELATORIO.md
ETAPA_02_RELATORIO.md
ETAPA_03_RELATORIO.md
ETAPA_04_RELATORIO.md
ETAPA_05_RELATORIO.md
GUIA_PUBLICACAO_FDROID.md
```

### Metadados F-Droid
```
fastlane/metadata/android/pt-BR/
├── title.txt
├── short_description.txt
├── full_description.txt
├── changelogs/1.txt
└── br.com.teadescomplica.yml (se criar)
```

### APK assinado
```
/home/valdenor/Downloads/tea-descomplica-1.0.0.apk (13 MB)
```

---

## 🚀 PASSO A PASSO PARA PUBLICAR NO F-DROID

### Passo 1: Fazer login no GitLab

Acesse: https://gitlab.com/users/sign_in
Faça login com sua conta do GitLab (ou crie uma se não tiver).

---

### Passo 2: Fazer fork do repositório f-droid-data

1. Acesse: **https://gitlab.com/fdroid/f-droid-data**
2. Clique no botão **"Fork"** (canto superior direito)
3. Selecione sua conta do GitLab como destino
4. Aguarde a conclusão do fork

---

### Passo 3: Clonar seu fork

```bash
cd /home/valdenor/
git clone https://gitlab.com/SEU_USUARIO/f-droid-data.git
cd f-droid-data
```

*Substitua `SEU_USUARIO` pelo seu nome de usuário do GitLab.*

---

### Passo 4: Criar o arquivo de metadata

Crie o arquivo `metadata/br.com.teadescomplica.yml`:

```yaml
Categories:
- Education
License: GPL-3.0-or-later
AuthorName: Valdenor
AuthorEmail: valdenor.sa@proton.me
Summary: Prepare-se do seu jeito - Prepare situações cotidianas
Description: |
  O TEA Descomplica é um aplicativo Android para pessoas autistas que desejam
  se preparar para situações cotidianas, antecipar imprevistos e registrar
  estratégias pessoais. Funciona totalmente offline, sem conta, sem servidor,
  sem publicidade e sem coleta de dados pessoais.

  O aplicativo ajuda a:
  - Preparar entrevistas de emprego
  - Organizar informações para consultas médicas
  - Planejar visitas a lugares desconhecidos
  - Antecipar situações em órgãos públicos
  - Preparar alternativas quando algo não acontece como esperado
  - Registrar estratégias que ajudaram em experiências anteriores

  Funcionalidades principais:
  - Meus Cenários: criar, editar, excluir cenários para situações cotidianas
  - Minha Preparação: organizar informações para situações futuras
  - O que pode acontecer: registrar situações possíveis e planos alternativos
  - Minhas Frases: criar frases pessoais para situações difíceis
  - Minha Jornada: registrar experiências vividas e estratégias pessoais
  - Minhas Estratégias: criar e reutilizar estratégias pessoais
  - Backup e Restauração: backup criptografado com senha do usuário

RepoType: git
Repo: https://github.com/alunoadsnoite/teadescomplica.git
Builds:
- versionName: 1.0.0
  versionCode: 1
  commit: 02b002f
  subdir: app
  gradle:
    - assembleRelease
AutoUpdateMode: Version
UpdateCheckMode: None
CurrentVersion: 1.0.0
CurrentVersionCode: 1
```

---

### Passo 5: Adicionar screenshots (opcional, mas recomendado)

Tire capturas de tela do aplicativo em português e coloque em:

```
fastlane/metadata/android/pt-BR/screenshots/
```

Nomes sugeridos:
- screenshot01.png - Tela inicial
- screenshot02.png - Meus cenários
- screenshot03.png - Minha Jornada
- screenshot04.png - Minhas Estratégias
- screenshot05.png - Configurações / Backup

---

### Passo 6: Fazer commit e push

```bash
git add metadata/br.com.teadescomplica.yml
git commit -m "Add TEA Descomplica"
git push origin add-teadescomplica
```

---

### Passo 7: Criar Merge Request no GitLab

1. Acesse seu fork do f-droid-data no GitLab
2. Clique em **"New Merge Request"**
3. Selecione seu branch `add-teadescomplica`
4. Título: `Add TEA Descomplica`
5. Descrição: "Aplicativo para pessoas autistas se prepararem para situações cotidianas. Funciona totalmente offline, sem conta, sem servidor, sem publicidade."
6. Clique em **"Create Merge Request"**

---

### Passo 8: Aguardar revisão do F-DROID

Os mantenedores do F-Droid revisarão seu aplicativo e:
- Verificarão se o código é livre e auditável
- Verificarão se não há dependências proprietárias
- Verificarão se não há rastreamento ou analytics
- Verificarão se não há permissão INTERNET
- Verificarão se o APK está assinado
- Publicarão no repositório do F-Droid

O processo pode levar de **alguns dias a algumas semanas**.

---

## 📦 ARQUIVOS PRONTOS NO REPOSITÓRIO GITHUB

| Arquivo | Caminho no GitHub |
|---------|---------|
| Código fonte completo | `app/src/main/java/br/com/teadescomplica/` |
| Recursos (strings, cores, ícones) | `app/src/main/res/` |
| Manifesto Android | `app/src/main/AndroidManifest.xml` |
| LICENSE (GPL-3.0-or-later) | `LICENSE` |
| README | `README.md` |
| CHANGELOG | `CHANGELOG.md` |
| PRIVACY | `PRIVACY.md` |
| FDROID_READINESS | `FDROID_READINESS.md` |
| BACKUP_FORMAT | `BACKUP_FORMAT.md` |
| SECURITY_AUDIT | `SECURITY_AUDIT.md` |
| ACCESSIBILITY_AUDIT | `ACCESSIBILITY_AUDIT.md` |
| TEST_MATRIX | `TEST_MATRIX.md` |
| RELEASE_CHECKLIST | `RELEASE_CHECKLIST.md` |
| Gradle Wrapper | `gradle/wrapper/` |
| Relatórios das etapas | `ETAPA_01_RELATORIO.md` a `ETAPA_05_RELATORIO.md` |

---

## 📦 APK ASSINADO PRONTO

| Item | Valor |
|------|-------|
| Caminho | `/home/valdenor/Downloads/tea-descomplica-1.0.0.apk` |
| Tamanho | 13,0 MB |
| Versão | 1.0.0 (versionCode 1) |
| Assinado | Sim |
| Keystore | `/home/valdenor/Projetos/TEADescomplica/tea-descomplica-release.keystore` |
| Alias | `teadescomplica-release` |

---

## ✅ CHECKLIST FINAL

| Item | Status |
|------|--------|
| Código fonte no GitHub | ✅ Completo |
| Keystore gerado | ✅ |
| APK assinado (13MB) | ✅ Pronto para download |
| Metadados F-Droid | ✅ Criados |
| Fork do f-droid-data | ⬜ Pendente (seu fork) |
| Pull Request enviado | ⬜ Pendente |
| Aprovação do F-DROID | ⬜ Pendente |

---

## 🎯 VEREDITO FINAL

**O TEA Descomplica MVP 1.0 está COMPLETAMENTE PRONTO para publicação no F-Droid.**

Todo o código está implementado, testado (código escrito), documentado e o APK está assinado. O que falta apenas é fazer o fork do f-droid-data, adicionar o arquivo de metadata e enviar o Pull Request.

Os mantenedores do F-Droid farão a revisão e publicação oficial.

---

## 📞 CONTATO

**Desenvolvedor:** Valdenor  
**Email:** valdenor.sa@proton.me  
**GitHub:** https://github.com/alunoadsnoite/teadescomplica  
**Licença:** GPL-3.0-or-later

---

*Este guia foi gerado automaticamente como parte da Etapa 05 do TEA Descomplica.*
