# Política de Privacidade — TEA Descomplica

## Resumo

O TEA Descomplica **não coleta, não transmite e não compartilha** dados pessoais. Todos os dados são armazenados exclusivamente no seu dispositivo. O aplicativo não exibe anúncios, não exige conta, não usa internet e não possui servidores.

## Dados Armazenados Localmente

Os seguintes dados são salvos apenas no armazenamento interno do dispositivo:

- Cenários criados pelo usuário (título, descrição, objetivo, datas de criação e atualização).
- Preparações de cenário (local, data, o que esperar, o que levar, informações importantes, observações).
- Situações possíveis (descrições, classificações opcionais, datas).
- Planos alternativos (títulos, descrições, associações, ordem).
- Frases pessoais (textos, associações, ordem).
- Experiências vividas (títulos, datas, descrições, o que ajudou, dificuldades, observações).
- Estratégias pessoais (títulos, descrições).
- Associações entre experiências e estratégias.
- Preferências de tema (sistema, claro ou escuro).
- Preferência de animações reduzidas.

Nenhum desses dados sai do dispositivo, a menos que você escolha exportá-los manualmente por meio de um backup criptografado.

## Backup e Restauração

O TEA Descomplica oferece backup manual criptografado via Storage Access Framework (SAF) do Android:

- O backup é iniciado exclusivamente por você.
- Você define uma senha para proteger o backup.
- Você escolhe onde salvar o arquivo de backup (incluindo provedores externos como Google Drive, quando disponíveis pelo seletor do Android).
- O aplicativo não se conecta diretamente ao Google Drive nem a qualquer serviço de nuvem.
- O arquivo de backup é criptografado com AES-256-GCM usando uma chave derivada de sua senha.
- A senha não é armazenada pelo aplicativo. Se você esquecer a senha, não será possível recuperar o conteúdo do backup.
- A restauração substitui completamente todos os dados atuais pelos dados do backup.

## Permissões

O aplicativo **não solicita** a permissão `android.permission.INTERNET` nem quaisquer permissões de armazenamento externo amplas (MANAGE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE). O aplicativo usa o Storage Access Framework (SAF) do Android para acessar arquivos apenas quando você escolhe explicitamente um documento.

Não acessa contas do Google, não usa Firebase e não se comunica com servidores externos.

## Backup Automático

Não há backup automático nem sincronização. O backup é uma ação manual iniciada por você.

## Terceiros

Não há integração com serviços de inteligência artificial, redes de publicidade, SDKs de rastreamento, analytics ou telemetria.

## Criptografia

Os backups utilizam:
- AES-256-GCM (criptografia autenticada)
- PBKDF2-HMAC-SHA256 (derivação de chave a partir de senha)
- 600.000 iterações do KDF
- Salt aleatório de 128 bits por backup
- Nonce de 96 bits por operação de criptografia

## Contato

Valdenor — valdenor.sa@proton.me
