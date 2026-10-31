# AgroConnect

O AgroConnect é uma aplicação web para apoiar a gestão agrícola em um único
painel. A interface reúne o acesso autenticado, indicadores operacionais e o
cadastro de usuários, pedidos, compradores, colheitas e empresas, integrando-se
a uma API REST executada localmente.

## Tecnologias utilizadas

- **HTML5** para a estrutura das páginas, formulários e tabelas.
- **CSS3** para o layout responsivo e a identidade visual com efeito
  **Glassmorphism**, usando transparências e desfoque de fundo.
- **Vanilla JavaScript (ES6+)** para interações, validações, chamadas HTTP e
  atualização dinâmica das telas, sem framework JavaScript.
- **Boxicons** para os ícones da interface.
- **ViaCEP** para consulta e preenchimento automático de endereços pelo CEP.

## Funcionalidades

- **Login com JWT:** envia as credenciais à API, armazena o token de sessão no
  navegador e encaminha para o dashboard. As páginas internas verificam a
  presença do token; respostas HTTP 401 ou 403 removem a sessão e redirecionam
  para o login.
- **Dashboard:** exibe totais de pedidos e colheitas e um resumo dos produtos
  cadastrados.
- **CRUD de usuários:** consulta, cadastro, edição e exclusão, com vínculo a uma
  empresa.
- **CRUD de pedidos:** consulta, cadastro, edição da quantidade e exclusão, com
  vínculos a uma colheita e a um comprador.
- **CRUD de compradores:** consulta, cadastro, edição e exclusão, incluindo
  documento e endereço de entrega.
- **CRUD de colheitas:** consulta, cadastro, edição e exclusão de produtos,
  estoque, valor e produtor responsável.
- **CRUD de empresas:** consulta, cadastro, edição e exclusão de empresas.
- **Consulta de CEP:** ao sair do campo CEP do formulário de comprador, consulta
  o ViaCEP e preenche logradouro, bairro, cidade e UF quando houver dados.
- **Navegação acessível:** mensagens de estado, confirmação para exclusões,
  formulários em modais e suporte à tecla Escape para fechá-los.

## Estrutura do front-end

```text
Projeto samuel/
├── README.md
└── app/
    ├── index.html
    ├── dashboard.html
    ├── usuarios.html
    ├── pedidos.html
    ├── comprador.html
    ├── colheita.html
    ├── empresa.html
    ├── css/
    │   └── style.css
    ├── js/
    │   └── script.js
    ├── fundo-login.jpg
    └── fundo-painel.jpg
```

O arquivo `app/js/script.js` é compartilhado pelas páginas e identifica a tela
atual pelo atributo `data-page` do elemento `<body>`.

## Requisitos

- Navegador moderno.
- Visual Studio Code com a extensão **Live Server** (ou outro servidor HTTP
  estático local).
- Backend AgroConnect ativo em `http://localhost:8080`, com CORS permitindo a
  origem usada pelo servidor local do front-end.

O front-end consome os endpoints `/login`, `/usuario`, `/pedido`, `/colheita`,
`/comprador` e `/empresa` do backend. Sem o backend em execução, será possível
abrir a interface, mas o login e o carregamento ou salvamento dos dados não
funcionarão. A consulta de CEP depende também de acesso à internet para alcançar
o ViaCEP.

## Como executar localmente

1. Abra a pasta `Projeto samuel` no Visual Studio Code.
2. No Explorer, abra `app/index.html`.
3. Clique com o botão direito no arquivo e selecione **Open with Live Server**.
4. Acesse a URL local aberta pelo Live Server e entre com uma conta cadastrada
   no backend.

> Não é necessário instalar dependências de front-end nem compilar o projeto.
> Para usar as páginas internas, inicie também o backend e confirme que ele
> aceita requisições CORS da origem local do Live Server.
