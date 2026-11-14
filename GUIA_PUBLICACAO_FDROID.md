# GUIA DE PUBLICAÇÃO — TEA Descomplica MVP 1.0

Este guia mostra como compilar, assinar e preparar o aplicativo para publicação no F-Droid.

---

## 📋 PRÉ-REQUISITOS

Antes de começar, você precisa de:
- **Java 17** (já instalado em `/home/valdenor/development/jdks/temurin-17`)
- **Android SDK** com build-tools 34.0.0 (já instalado em `/home/valdenor/Android/Sdk`)
- **Acesso à internet** para baixar dependências do Gradle
- **Gradle Wrapper** já incluído no projeto (`./gradlew`)

---

## 🚀 PASSO 1: CLONAR O REPOSITÓRIO

```bash
cd /home/valdenor/
git clone https://github.com/alunoadsnoite/teadescomplica.git
cd teadescomplica
```

---

## 🔑 PASSO 2: GERAR O KEYSTORE DE ASSINATURA

O F-Droid exige um APK assinado. Gere uma chave de assinatura:

```bash
keytool -genkey -v -keystore /home/valdenor/Projetos/TEADescomplica/tea-descomplica-release.keystore \
  -alias teadescomplica-release \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Você será perguntado:
- Senha do keystore → **anote esta senha**
- Nome, unidade organizacional, etc. → pode deixar em branco ou preencher
- Senha da chave → **anote esta senha** (pode ser a mesma do keystore)

---

## ⚙️ PASSO 3: CRIAR ARQUIVO DE CONFIGURAÇÃO DE ASSINATURA

Crie o arquivo `/home/valdenor/Projetos/TEADescomplica/app/signing.properties`:

```properties
keyStoreFile=/home/valdenor/Projetos/TEADescomplica/tea-descomplica-release.keystore
keyStorePassword=SUA_SENHA_DO_KEYSTORE
keyAlias=teadescomplica-release
keyPassword=SUA_SENHA_DA_CHAVE
```

**Importante:** Substitua `SUA_SENHA_DO_KEYSTORE` e `SUA_SENHA_DA_CHAVE` pelas senhas que você criou no passo 2.

---

## 🔨 PASSO 4: COMPILAR O APK

```bash
cd /home/valdenor/Projetos/TEADescomplica
export JAVA_HOME=/home/valdenor/development/jdks/temurin-17
./gradlew :app:assembleRelease
```

O APK assinado será gerado em:
```
/home/valdenor/Projetos/TEADescomplica/app/build/outputs/apk/release/app-release.apk
```

---

## 📁 PASSO 5: COPIAR O APK PARA DOWNLOADS

```bash
cp /home/valdenor/Projetos/TEADescomplica/app/build/outputs/apk/release/app-release.apk /home/valdenor/Downloads/
```

---

## 📱 PASSO 6: PREPARAR METADADOS PARA O F-DROID

O F-Droid não aceita upload direto de APK. Você precisa fazer um **fork** do repositório [f-droid-data](https://gitlab.com/fdroid/f-droid-data) e adicionar o metadata do seu aplicativo.

### 6.1 Fazer fork do f-droid-data

Acesse: https://gitlab.com/fdroid/f-droid-data
Clique em "Fork" para criar sua própria cópia.

### 6.2 Criar o arquivo de metadata

No seu fork do f-droid-data, crie o arquivo:
```
metadata/br.com.teadescomplica.yml
```

Conteúdo do arquivo:
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
  commit: f5701dd
  subdir: app
  gradle:
    - assembleRelease
AutoUpdateMode: Version
UpdateCheckMode: None
CurrentVersion: 1.0.0
CurrentVersionCode: 1
```

**Observação:** O campo `commit` deve ser o hash do commit no seu repositório GitHub. Atualmente é `f5701dd`, mas pode mudar se você fizer novos commits.

---

## 📸 PASSO 7: ADICIONAR SCREENSHOTS (OPCIONAL MAS RECOMENDADO)

O F-Droid recomenda screenshots do aplicativo. Tire capturas de tela do app em execução e coloque em:

```
fastlane/metadata/android/pt-BR/screenshots/
```

Nomes sugeridos:
- `screenshot01.png` - Tela inicial
- `screenshot02.png` - Meus cenários
- `screenshot03.png` - Meus cenários com cenário criado
- `screenshot04.png` - Minha Jornada
- `screenshot05.png` - Configurações / Backup

---

## 📤 PASSO 8: ENVIAR PULL REQUEST PARA O F-DROID-DATA

1. No seu fork do f-droid-data, crie um branch:
   ```bash
   git checkout -b add-teadescomplica
   ```
2. Adicione o arquivo de metadata:
   ```bash
   git add metadata/br.com.teadescomplica.yml
   git commit -m "Add TEA Descomplica"
   git push origin add-teadescomplica
   ```
3. No GitLab, crie um Merge Request para o repositório oficial `fdroid/f-droid-data`

---

## 📊 PASSO 9: AGUARDAR REVISÃO DO F-DROID

Os mantenedores do F-Droid revisarão seu aplicativo. Eles podem:
- Aprovar imediatamente
- Solicitar alterações
- Rejeitar se não atender aos critérios

O processo pode levar de alguns dias a algumas semanas.

---

## 📊 CHECKLIST FINAL

| Item | Status |
|------|--------|
| Código fonte no GitHub | ✅ |
| Keystore gerado | ⬜ |
| signing.properties criado | ⬜ |
| APK assinado compilado | ⬜ |
| APK copiado para Downloads | ⬜ |
| Fork do f-droid-data | ⬜ |
| Metadata yaml criado | ⬜ |
| Screenshots adicionados | ⬜ |
| Pull Request enviado | ⬜ |
| Aprovação do F-Droid | ⬜ |

---

## 🆘 EM CASO DE ERRO

Se a compilação falhar, verifique:
1. Java 17 está configurado: `java -version`
2. Android SDK está instalado: `ls /home/valdenor/Android/Sdk/`
3. Gradle Wrapper funciona: `./gradlew --version`
4. Acesso à internet para baixar dependências

Se precisar de ajuda, relate o erro exato que aparecer no terminal.