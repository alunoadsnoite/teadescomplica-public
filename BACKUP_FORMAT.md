# BACKUP_FORMAT.md — Formato de Arquivo de Backup do TEA Descomplica

## Visão Geral

O TEA Descomplica utiliza um formato de arquivo próprio e versionado para backups
criptografados. A extensão do arquivo é `.teadbackup`.

O formato é binário e contém um cabeçalho autenticado (AAD) não criptografado e um
payload criptografado com AES-256-GCM. Nenhum dado pessoal é armazenado em texto
puro no cabeçalho.

## Estrutura do Arquivo

Todos os inteiros são armazenados em big-endian (ordem de rede).

| Offset | Tamanho | Campo | Descrição |
|--------|---------|-------|-----------|
| 0 | 4 bytes | Magic number | `0x54454144` ("TEAD") |
| 4 | 4 bytes | Formato versão | Inteiro, atualmente 1 |
| 8 | 4 bytes | KDF versão | Inteiro, atualmente 1 (PBKDF2-HMAC-SHA256) |
| 12 | 4 bytes | Cripto versão | Inteiro, atualmente 1 (AES-256-GCM) |
| 16 | 4 bytes | Tamanho do salt | Inteiro, atualmente 16 |
| 20 | 16 bytes | Salt | Aleatório criptograficamente seguro, único por backup |
| 36 | 4 bytes | Tamanho do nonce | Inteiro, atualmente 12 |
| 40 | 12 bytes | Nonce | IV de 96 bits, único por operação de criptografia |
| 52 | 4 bytes | Tamanho do AAD | Inteiro, tamanho do cabeçalho autenticado |
| 56 | AAD bytes | AAD | Cabeçalho JSON autenticado, não criptografado |
| 56+AAD | 4 bytes | Tamanho do criptograma | Inteiro, tamanho do payload criptografado + tag |
| 60+AAD | Variável | Criptograma | Dados do backup criptografados com AES-256-GCM + tag de 16 bytes |

## Cabeçalho Autenticado (AAD)

O AAD é um objeto JSON não criptografado, mas autenticado pela tag GCM. Qualquer
alteração no AAD invalida a descriptografia.

```json
{"formatVersion":1,"kdfVersion":1,"cryptoVersion":1}
```

O AAD contém apenas metadados de formato. Nenhum dado pessoal (títulos, descrições,
senhas, chaves) é armazenado no AAD.

## Payload Criptografado

O payload é a serialização JSON do `BackupSnapshot` criptografada com AES-256-GCM
usando uma chave derivada da senha do usuário via PBKDF2-HMAC-SHA256.

O JSON do snapshot contém todos os dados do aplicativo:
- Cenários (título, descrição, objetivo, datas)
- Preparações (local, data, o que esperar, o que levar, informações importantes, observações)
- Situações possíveis (descrição, classificação opcional, data)
- Planos alternativos (título, descrição, posição, associação opcional)
- Frases pessoais (texto, posição, data)
- Experiências (título, data, descrição, o que ajudou, dificuldades, observações, cenário relacionado)
- Estratégias (título, descrição, datas)
- Associações experiência-estratégia (muitos-para-muitos)
- Preferências (tema, animações reduzidas)

## Algoritmos Criptográficos

| Componente | Algoritmo | Parâmetros |
|------------|-----------|------------|
| Cifragem | AES-256-GCM | Chave de 256 bits, nonce de 96 bits, tag de 128 bits |
| Derivação de chave | PBKDF2-HMAC-SHA256 | 600.000 iterações, salt de 128 bits |
| Gerador aleatório | SecureRandom | Criptograficamente seguro |
| Hash (KDF) | SHA-256 | Usado pelo PBKDF2 |

## Requisitos de Senha

- Mínimo de 8 caracteres
- Não é armazenada em nenhum local (Room, DataStore, logs)
- Não é transmitida pela rede
- Deve ser fornecida pelo usuário a cada operação de backup/restauração
- Se esquecida, o backup não pode ser recuperado

## Versionamento

| Versão | Descrição |
|--------|-----------|
| 1 | Versão inicial do formato de backup |

O campo `formatVersion` no cabeçalho permite futuras evoluções do formato sem
quebrar a compatibilidade com versões anteriores.

## Limitações

- Tamanho máximo do arquivo de backup: 50 MB
- Não há compressão dos dados antes da criptografia
- Não há suporte a restauração parcial (é tudo ou nada)
- Não há mesclagem automática com dados existentes

## Política de Restauração (MVP 1.0)

A restauração substitui completamente todos os dados atuais pelos dados do backup.
Antes de executar, o aplicativo apresenta um aviso claro e exige confirmação
explícita do usuário.

A restauração utiliza transações Room para substituir os dados de forma atômica.
As preferências do DataStore são restauradas separadamente após a transação Room.

## Recuperação após Falha

Se a restauração falhar durante a aplicação:
- Os dados Room são restaurados dentro de uma transação (rollback automático)
- As preferências do DataStore podem estar em estado misto se a falha ocorrer
  após a transação Room mas antes da conclusão do DataStore
- O usuário pode tentar a restauração novamente com o mesmo arquivo de backup

## Auditoria de Privacidade

- Nenhuma permissão de internet no AndroidManifest
- `android:allowBackup="false"`
- Regras de extração de dados excluem banco de dados e shared preferences
- O backup é iniciado exclusivamente pelo usuário
- O seletor de documentos Android (SAF) permite escolha do provedor de armazenamento
- O aplicativo não depende do Google Drive nem de qualquer serviço de nuvem
- Nenhum dado pessoal é transmitido pela rede
