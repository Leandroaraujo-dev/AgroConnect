const urlLogin = "http://localhost:8080/login";
const urlUsuarios = "http://localhost:8080/usuario";
const urlPedidos = "http://localhost:8080/pedido";
const urlColheitas = "http://localhost:8080/colheita";
const urlCompradores = "http://localhost:8080/comprador";
const urlEmpresas = "http://localhost:8080/empresa";

/**
 * Centraliza as chamadas ao backend para manter o envio do JWT e o tratamento
 * de sessões expiradas consistentes em todos os módulos autenticados.
 *
 * @param {string} url Endereço do recurso da API.
 * @param {RequestInit} [options] Opções nativas da requisição fetch.
 * @returns {Promise<Response>} Resposta original do backend.
 */
async function apiFetch(url, options = {}) {
  const token = localStorage.getItem("token");
  const headers = new Headers(options.headers);

  if (token) {
    // O token é incluído em um único ponto para proteger todas as rotas que usam esta função.
    headers.set("Authorization", `Bearer ${token}`);
  }

  const resposta = await fetch(url, { ...options, headers });

  if (resposta.status === 401 || resposta.status === 403) {
    // Uma credencial rejeitada não deve permanecer disponível para chamadas futuras.
    localStorage.removeItem("token");

    if (document.body.dataset.page !== "login") {
      window.location.href = "index.html";
    }
  }

  return resposta;
}

/**
 * Impede a abertura de páginas internas sem uma credencial local.
 * A validade real do JWT continua sendo confirmada pelo backend em cada requisição.
 *
 * @returns {boolean} Se há token armazenado para iniciar a navegação autenticada.
 */
function verificarAutenticacao() {
  const token = localStorage.getItem("token");

  if (!token || !token.trim()) {
    window.location.href = "index.html";
    return false;
  }

  return true;
}

/**
 * Normaliza valores nulos da API, inclusive em objetos e listas aninhados,
 * para evitar exibir marcadores de ausência diretamente na interface.
 *
 * @param {*} valor Valor recebido da API.
 * @returns {*} Valor normalizado, preservando a estrutura original.
 */
function limparDadosApi(valor) {
  if (valor === null || valor === undefined || valor === "null") {
    return "-";
  }

  if (Array.isArray(valor)) {
    return valor.map(limparDadosApi);
  }

  if (typeof valor === "object") {
    return Object.fromEntries(
      Object.entries(valor).map(([chave, dado]) => [chave, limparDadosApi(dado)])
    );
  }

  return valor;
}

/** Considera inativo somente o status de exclusão lógica definido pelo backend. */
function registroEstaAtivo(registro) {
  return String(registro.status ?? "").trim().toUpperCase() !== "D";
}

/** Aplica a seleção atual do filtro de status a uma lista recebida da API. */
function filtrarListaPorStatus(registros) {
  const filtroStatus = document.querySelector("#filtroStatus")?.value ?? "ativos";

  if (filtroStatus === "todos") {
    return registros;
  }

  return registros.filter((registro) => {
    const ativo = registroEstaAtivo(registro);
    return filtroStatus === "inativos" ? !ativo : ativo;
  });
}

/** Cria a célula e o badge de status padronizados para as tabelas de cadastro. */
function criarCelulaStatus(registro) {
  const ativo = registroEstaAtivo(registro);
  const celula = document.createElement("td");
  const badge = document.createElement("span");
  badge.className = `status-badge ${ativo ? "status-badge-active" : "status-badge-inactive"}`;
  badge.textContent = ativo ? "Ativo" : "Inativo";
  celula.append(badge);
  return celula;
}

/** Remove qualquer caractere que não seja numérico do campo informado. */
function manterSomenteNumeros(campo) {
  campo.value = campo.value.replace(/\D/g, "");
}

/**
 * Apresenta datas ISO ou locais em formato brasileiro, sem depender de parsing
 * implícito do navegador que poderia deslocar o dia por causa do fuso horário.
 *
 * @param {string} data Data recebida da API.
 * @returns {string} Data em DD/MM/AAAA ou o conteúdo original se não for reconhecido.
 */
function formatarData(data) {
  if (typeof data !== "string" || data === "-") {
    return data;
  }

  const partesIso = data.match(/^(\d{4})[-/](\d{1,2})[-/](\d{1,2})$/);
  const partesLocais = data.match(/^(\d{1,2})[-/](\d{1,2})[-/](\d{1,4})$/);
  let ano;
  let mes;
  let dia;

  if (partesIso) {
    [, ano, mes, dia] = partesIso;
  } else if (partesLocais) {
    [, dia, mes, ano] = partesLocais;
  } else {
    return data;
  }

  const anoNumerico = ano.length === 2
    ? Number(ano) + (Number(ano) < 50 ? 2000 : 1900)
    : Number(ano);

  const mesNumerico = Number(mes);
  const diaNumerico = Number(dia);
  const bissexto = anoNumerico % 4 === 0 && (anoNumerico % 100 !== 0 || anoNumerico % 400 === 0);
  const diasNoMes = [31, bissexto ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][mesNumerico - 1];

  if (mesNumerico < 1 || mesNumerico > 12 || diaNumerico < 1 || diaNumerico > diasNoMes) {
    return data;
  }

  return `${String(diaNumerico).padStart(2, "0")}/${String(mesNumerico).padStart(2, "0")}/${String(anoNumerico).padStart(4, "0")}`;
}

/**
 * Autentica o usuário, valida o token retornado e conduz ao painel.
 * A resposta textual também é aceita para exibir mensagens de backends que não
 * retornam JSON em caso de erro.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário de login.
 * @returns {Promise<void>}
 */
async function enviarLogin(evento) {
  evento.preventDefault();

  const campoLogin = document.querySelector("#login");
  const campoSenha = document.querySelector("#senha");
  const botaoLogin = document.querySelector("#login-button");
  const mensagemLogin = document.querySelector("#login-message");
  const dadosLogin = {
    senha: campoSenha.value,
    login: campoLogin.value.trim()
  };

  botaoLogin.disabled = true;
  botaoLogin.textContent = "Entrando...";
  mensagemLogin.textContent = "";
  mensagemLogin.className = "login-message";

  try {
    const resposta = await apiFetch(urlLogin, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosLogin)
    });

    const textoResposta = await resposta.text();
    let dadosResposta = {};

    if (textoResposta) {
      try {
        dadosResposta = JSON.parse(textoResposta);
      } catch {
        dadosResposta = { mensagem: textoResposta };
      }
    }

    if (!resposta.ok) {
      mensagemLogin.textContent = dadosResposta.mensagem || `Não foi possível entrar (HTTP ${resposta.status}).`;
      mensagemLogin.className = "login-message visible error";
      return;
    }

    if (typeof dadosResposta.token !== "string" || !dadosResposta.token.trim()) {
      throw new Error("A resposta do backend não contém um token de autenticação válido.");
    }

    localStorage.setItem("token", dadosResposta.token);
    mensagemLogin.textContent = dadosResposta.mensagem || "Login realizado com sucesso.";
    mensagemLogin.className = "login-message visible success";

    window.setTimeout(() => {
      window.location.href = "dashboard.html";
    }, 700);
  } catch (erro) {
    mensagemLogin.textContent = `Não foi possível conectar ao backend. Confira se ele está ativo e permite CORS. Detalhe: ${erro.message}`;
    mensagemLogin.className = "login-message visible error";
  } finally {
    botaoLogin.disabled = false;
    botaoLogin.textContent = "Entrar";
  }
}

/**
 * Busca uma coleção autenticada e valida seu formato antes de repassá-la às telas.
 * A validação centralizada evita que cada módulo renderize respostas incompatíveis.
 *
 * @param {string} url Endpoint da coleção.
 * @param {string} tipo Nome da coleção, usado na mensagem de erro.
 * @returns {Promise<Array>} Registros normalizados.
 */
async function buscarLista(url, tipo) {
  const resposta = await apiFetch(url, {
    method: "GET",
    headers: {
      Accept: "application/json"
    }
  });

  if (!resposta.ok) {
    throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
  }

  const dados = await resposta.json();

  if (!Array.isArray(dados)) {
    throw new Error(`A resposta da API não está no formato esperado: era necessário um array de ${tipo}.`);
  }

  return dados.map(limparDadosApi);
}

/**
 * Lê usuários da API e constrói a tabela com ações de edição e exclusão.
 * Os dados são atribuídos via textContent para apresentá-los como texto, não como HTML.
 *
 * @returns {Promise<void>}
 */
async function carregarUsuarios() {
  const corpoTabela = document.querySelector("#users-table-body");
  const mensagemUsuarios = document.querySelector("#users-message");
  mensagemUsuarios.textContent = "Carregando usuários...";
  mensagemUsuarios.className = "users-message";
  corpoTabela.replaceChildren();

  try {
    const usuariosRecebidos = await buscarLista(urlUsuarios, "usuários");
    const usuarios = filtrarListaPorStatus(usuariosRecebidos);

    usuarios.forEach((usuario) => {
      const linha = document.createElement("tr");
      const valores = [
        usuario.nome || "Sem nome",
        usuario.cpf || "Não informado",
        formatarData(usuario.dataNascimento || "Não informada"),
        usuario.razaoSocialEmpresa || "Sem empresa"
      ];

      valores.forEach((valor) => {
        const celula = document.createElement("td");
        celula.textContent = valor;
        linha.append(celula);
      });

      linha.append(criarCelulaStatus(usuario));

      const celulaAcoes = document.createElement("td");
      const acoes = document.createElement("div");
      acoes.className = "user-actions";

      const botaoEditar = document.createElement("button");
      botaoEditar.className = "user-action-button user-action-edit";
      botaoEditar.type = "button";
      botaoEditar.setAttribute("aria-label", `Editar ${usuario.nome || "usuário"}`);
      botaoEditar.title = "Editar";
      botaoEditar.innerHTML = '<i class="bx bx-edit" aria-hidden="true"></i>';
      botaoEditar.addEventListener("click", () => abrirModalUsuario(usuario, botaoEditar));

      const botaoExcluir = document.createElement("button");
      botaoExcluir.className = "user-action-button user-action-delete";
      botaoExcluir.type = "button";
      botaoExcluir.setAttribute("aria-label", `Excluir ${usuario.nome || "usuário"}`);
      botaoExcluir.title = "Excluir";
      botaoExcluir.innerHTML = '<i class="bx bx-trash" aria-hidden="true"></i>';
      botaoExcluir.addEventListener("click", () => excluirUsuario(usuario, botaoExcluir));

      acoes.append(botaoEditar, botaoExcluir);
      celulaAcoes.append(acoes);
      linha.append(celulaAcoes);
      corpoTabela.append(linha);
    });

    mensagemUsuarios.textContent = usuarios.length
      ? `${usuarios.length} usuário(s) encontrado(s).`
      : usuariosRecebidos.length
        ? "Nenhum usuário corresponde ao filtro selecionado."
        : "Nenhum usuário cadastrado.";
  } catch (erro) {
    mensagemUsuarios.textContent = `Não foi possível carregar os usuários. ${erro.message}`;
    mensagemUsuarios.className = "users-message error";
  }
}

/**
 * Converte datas da API para o formato YYYY-MM-DD exigido por input[type=date].
 *
 * @param {string} data Data recebida da API.
 * @returns {string} Data compatível com o campo ou string vazia quando inválida.
 */
function converterDataParaInput(data) {
  if (typeof data !== "string") {
    return "";
  }

  const partesIso = data.match(/^(\d{4})[-/](\d{1,2})[-/](\d{1,2})$/);
  const partesLocais = data.match(/^(\d{1,2})[-/](\d{1,2})[-/](\d{4})$/);

  if (partesIso) {
    const [, ano, mes, dia] = partesIso;
    return `${ano}-${mes.padStart(2, "0")}-${dia.padStart(2, "0")}`;
  }

  if (partesLocais) {
    const [, dia, mes, ano] = partesLocais;
    return `${ano}-${mes.padStart(2, "0")}-${dia.padStart(2, "0")}`;
  }

  return "";
}

/**
 * Preenche seletores usados para relacionar entidades, descartando chaves ausentes
 * e preservando o vínculo atual durante uma edição quando ele ainda está disponível.
 *
 * @param {HTMLSelectElement} select Campo a preencher.
 * @param {Array} registros Registros de origem do relacionamento.
 * @param {Function} obterValor Extrai a chave que será enviada à API.
 * @param {Function} obterTexto Extrai o rótulo legível para a pessoa usuária.
 * @param {string|number} [valorSelecionado] Chave que deve permanecer selecionada.
 * @param {Function|null} [configurarOpcao] Adiciona dados específicos à opção.
 * @returns {boolean} Se a seleção solicitada está disponível.
 */
function preencherSelectRelacionamento(
  select,
  registros,
  obterValor,
  obterTexto,
  valorSelecionado = "",
  configurarOpcao = null
) {
  const opcaoPadrao = document.createElement("option");
  opcaoPadrao.value = "";
  opcaoPadrao.disabled = true;
  opcaoPadrao.selected = true;
  opcaoPadrao.textContent = "Selecione uma opção...";
  select.replaceChildren(opcaoPadrao);

  let quantidadeOpcoes = 0;
  registros.forEach((registro) => {
    const valor = obterValor(registro);

    if (valor === null || valor === undefined || valor === "" || valor === "-") {
      return;
    }

    const opcao = document.createElement("option");
    const texto = obterTexto(registro);
    opcao.value = String(valor);
    opcao.textContent = typeof texto === "string" &&
      texto.trim() &&
      texto.trim() !== "-" &&
      texto.trim().toLowerCase() !== "string"
      ? texto
      : String(valor);
    configurarOpcao?.(opcao, registro);
    select.append(opcao);
    quantidadeOpcoes += 1;
  });

  if (!quantidadeOpcoes) {
    const opcaoVazia = document.createElement("option");
    opcaoVazia.disabled = true;
    opcaoVazia.textContent = "Nenhuma opção disponível";
    select.append(opcaoVazia);
    select.disabled = true;
    return false;
  }

  select.disabled = false;

  if (valorSelecionado !== null && valorSelecionado !== undefined && valorSelecionado !== "") {
    select.value = String(valorSelecionado);
  }

  return !valorSelecionado || select.value === String(valorSelecionado);
}

/** Carrega as empresas para manter o vínculo obrigatório de um usuário. */
async function carregarSelectEmpresas(valorSelecionado = "") {
  const select = document.querySelector("#empresa_id");
  select.disabled = true;
  const empresas = await buscarLista(urlEmpresas, "empresas");

  return preencherSelectRelacionamento(
    select,
    empresas,
    (empresa) => empresa.id,
    (empresa) => empresa.razaoSocial && empresa.razaoSocial !== "-"
      ? empresa.razaoSocial
      : empresa.nomeFantasia,
    valorSelecionado
  );
}

/** Carrega produtores cadastrados para vincular uma colheita ao usuário responsável. */
async function carregarSelectUsuarios(cpfSelecionado = "") {
  const select = document.querySelector("#cpfProdutor");
  select.disabled = true;
  const usuarios = await buscarLista(urlUsuarios, "usuários");

  return preencherSelectRelacionamento(
    select,
    usuarios,
    (usuario) => usuario.cpf,
    (usuario) => usuario.nome && usuario.nome !== "-"
      ? `${usuario.nome} (${usuario.cpf})`
      : usuario.cpf,
    cpfSelecionado
  );
}

/** Carrega colheitas disponíveis para compor um pedido. */
async function carregarSelectColheitas(nomeSelecionado = "") {
  const select = document.querySelector("#nomeColheita");
  select.disabled = true;
  const colheitas = await buscarLista(urlColheitas, "colheitas");
  const colheitaSelecionada = colheitas.find(
    (colheita) => colheita.nomeProduto === nomeSelecionado
  );
  const idSelecionado = colheitaSelecionada?.id ?? "";

  const colheitaDisponivel = preencherSelectRelacionamento(
    select,
    colheitas,
    (colheita) => colheita.id,
    (colheita) => colheita.nomeProduto,
    idSelecionado,
    (opcao, colheita) => {
      opcao.dataset.valor = colheita.valorProduto ?? "";
      opcao.dataset.estoque = colheita.quantidadeEstoque ?? "";
    }
  );

  return (!nomeSelecionado || Boolean(colheitaSelecionada)) && colheitaDisponivel;
}

/** Atualiza os valores calculados do pedido conforme a colheita e a quantidade. */
function atualizarValoresPedido() {
  const campoColheita = document.querySelector("#nomeColheita");
  const campoQuantidade = document.querySelector("#order-quantity");
  const opcaoColheita = campoColheita.selectedOptions[0];
  const valorUnitario = Number(opcaoColheita?.dataset.valor);
  const quantidade = Number(campoQuantidade.value);
  const produtoSelecionado = Boolean(campoColheita.value);
  const valorValido = produtoSelecionado && Number.isFinite(valorUnitario)
    ? valorUnitario
    : 0;
  const valorTotal = produtoSelecionado &&
    campoQuantidade.value !== "" &&
    Number.isFinite(quantidade)
    ? valorValido * quantidade
    : 0;
  const formatarMoeda = (valor) => valor.toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL"
  });

  document.querySelector("#order-unit-price").value = formatarMoeda(valorValido);
  document.querySelector("#order-total-price").value = formatarMoeda(valorTotal);
}

/** Carrega compradores disponíveis para compor um pedido. */
async function carregarSelectCompradores(documentoSelecionado = "") {
  const select = document.querySelector("#documentoComprador");
  select.disabled = true;
  const compradores = await buscarLista(urlCompradores, "compradores");

  return preencherSelectRelacionamento(
    select,
    compradores,
    (comprador) => comprador.cnpjOuCpf,
    (comprador) => comprador.nome && comprador.nome !== "-"
      ? `${comprador.nome} (${comprador.cnpjOuCpf})`
      : comprador.cnpjOuCpf,
    documentoSelecionado
  );
}

/**
 * Prepara o formulário de usuário para criação ou edição e carrega suas empresas.
 * O vínculo empresarial é preenchido antes da gravação para evitar referências inválidas.
 *
 * @param {Object|null} [usuario] Registro existente; null inicia um cadastro.
 * @param {HTMLElement|null} [elementoOrigem] Controle que abriu o modal, para restaurar o foco.
 * @returns {Promise<void>}
 */
async function abrirModalUsuario(usuario = null, elementoOrigem = null) {
  const modal = document.querySelector("#user-modal");
  const formulario = document.querySelector("#user-form");

  formulario.reset();
  formulario.dataset.usuarioId = usuario?.id ?? "";
  document.querySelector("#user-modal-title").textContent = usuario
    ? "Editar Usuário"
    : "Cadastrar Usuário";
  document.querySelector("#user-form-message").textContent = "";
  document.querySelector("#user-name").value = usuario?.nome ?? "";
  document.querySelector("#user-cpf").value = usuario?.cpf ?? "";
  document.querySelector("#user-birth-date").value = converterDataParaInput(usuario?.dataNascimento);
  modal.returnFocusElement = elementoOrigem;
  modal.hidden = false;

  try {
    const empresaSelecionada = await carregarSelectEmpresas(usuario?.empresa_id ?? "");

    if (!empresaSelecionada && usuario?.empresa_id) {
      document.querySelector("#user-form-message").textContent =
        "A empresa atualmente vinculada não está disponível. Selecione outra opção.";
    }
  } catch (erro) {
    document.querySelector("#user-form-message").textContent =
      `Não foi possível carregar as empresas. ${erro.message}`;
  }

  document.querySelector("#user-name").focus();
}

/** Fecha o formulário e devolve o foco ao controle que o abriu. */
function fecharModalUsuario() {
  const modal = document.querySelector("#user-modal");
  const elementoOrigem = modal.returnFocusElement;
  modal.hidden = true;
  modal.returnFocusElement = null;

  if (elementoOrigem?.isConnected) {
    elementoOrigem.focus();
  } else {
    document.querySelector("#add-user-button").focus();
  }
}

/**
 * Cria ou atualiza um usuário; valida a empresa relacionada antes de enviar o payload.
 * O recarregamento após a resposta confirma na tabela o estado persistido pelo backend.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário.
 * @returns {Promise<void>}
 */
async function salvarUsuario(evento) {
  evento.preventDefault();

  const formulario = evento.currentTarget;
  const mensagem = document.querySelector("#user-form-message");
  const botaoSalvar = document.querySelector("#user-save-button");
  const id = formulario.dataset.usuarioId;
  const empresaIdSelecionada = document.querySelector("#empresa_id").value;
  const empresaId = Number(empresaIdSelecionada);
  const dadosUsuario = {
    nome: document.querySelector("#user-name").value.trim(),
    cpf: document.querySelector("#user-cpf").value.trim(),
    dataNascimento: document.querySelector("#user-birth-date").value,
    empresa_id: empresaId,
    senha: document.querySelector("#user-password").value
  };

  if (!empresaIdSelecionada || !Number.isSafeInteger(empresaId) || empresaId < 1) {
    mensagem.textContent = "Selecione uma empresa válida.";
    return;
  }

  mensagem.textContent = "";
  botaoSalvar.disabled = true;
  botaoSalvar.textContent = "Salvando...";

  try {
    const resposta = await apiFetch(id ? `${urlUsuarios}/${encodeURIComponent(id)}` : urlUsuarios, {
      method: id ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosUsuario)
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    fecharModalUsuario();
    await carregarUsuarios();
  } catch (erro) {
    mensagem.textContent = `Não foi possível salvar o usuário. ${erro.message}`;
  } finally {
    botaoSalvar.disabled = false;
    botaoSalvar.textContent = "Salvar";
  }
}

/**
 * Solicita confirmação antes de excluir e recarrega a lista após sucesso da API.
 *
 * @param {Object} usuario Registro que será excluído.
 * @param {HTMLButtonElement} botaoExcluir Controle que iniciou a ação.
 * @returns {Promise<void>}
 */
async function excluirUsuario(usuario, botaoExcluir) {
  if (!window.confirm("Tem certeza que deseja excluir?")) {
    return;
  }

  if (usuario.id === null || usuario.id === undefined || usuario.id === "-") {
    const mensagem = document.querySelector("#users-message");
    mensagem.textContent = "Não foi possível excluir o usuário: ID não informado.";
    mensagem.className = "users-message error";
    return;
  }

  botaoExcluir.disabled = true;

  try {
    const resposta = await apiFetch(`${urlUsuarios}/${encodeURIComponent(usuario.id)}`, {
      method: "DELETE"
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    window.alert("Usuário excluído com sucesso.");
    await carregarUsuarios();
  } catch (erro) {
    const mensagem = document.querySelector("#users-message");
    mensagem.textContent = `Não foi possível excluir o usuário. ${erro.message}`;
    mensagem.className = "users-message error";
    botaoExcluir.disabled = false;
  }
}

/**
 * Lê os pedidos e apresenta seus vínculos e ações na tabela.
 * A consulta e a apresentação de erros ficam encapsuladas para manter a tela utilizável.
 *
 * @returns {Promise<void>}
 */
async function carregarPedidos() {
  const corpoTabela = document.querySelector("#orders-table-body");
  const mensagemPedidos = document.querySelector("#orders-message");
  mensagemPedidos.textContent = "Carregando pedidos...";
  mensagemPedidos.className = "users-message";
  corpoTabela.replaceChildren();

  try {
    const pedidosRecebidos = await buscarLista(urlPedidos, "pedidos");
    const pedidos = filtrarListaPorStatus(pedidosRecebidos);

    pedidos.forEach((pedido) => {
      const linha = document.createElement("tr");
      const valores = [
        pedido.id,
        pedido.nomeColheita ?? "Não informado",
        pedido.documentoComprador ?? "Não informado",
        pedido.quantidadeComprada,
        pedido.statusPagamento ?? "Não informado"
      ];

      valores.forEach((valor) => {
        const celula = document.createElement("td");
        celula.textContent = valor;
        linha.append(celula);
      });

      linha.append(criarCelulaStatus(pedido));

      const celulaAcoes = document.createElement("td");
      const acoes = document.createElement("div");
      acoes.className = "user-actions";

      const botaoEditar = document.createElement("button");
      botaoEditar.className = "user-action-button user-action-edit";
      botaoEditar.type = "button";
      botaoEditar.setAttribute("aria-label", `Editar pedido ${pedido.id ?? ""}`.trim());
      botaoEditar.title = "Editar";
      botaoEditar.innerHTML = '<i class="bx bx-edit" aria-hidden="true"></i>';
      botaoEditar.addEventListener("click", () => abrirModalPedido(pedido, botaoEditar));

      if (pedido.statusPagamento?.toLowerCase() !== "pagamento confirmado") {
        const botaoConfirmarPagamento = document.createElement("button");
        botaoConfirmarPagamento.className = "user-action-button user-action-confirm";
        botaoConfirmarPagamento.type = "button";
        botaoConfirmarPagamento.setAttribute(
          "aria-label",
          `Confirmar pagamento do pedido ${pedido.id ?? ""}`.trim()
        );
        botaoConfirmarPagamento.title = "Confirmar pagamento";
        botaoConfirmarPagamento.innerHTML = '<i class="bx bx-check-circle" aria-hidden="true"></i>';
        botaoConfirmarPagamento.addEventListener("click", () =>
          confirmarPagamentoPedido(pedido, botaoConfirmarPagamento)
        );

        acoes.append(botaoConfirmarPagamento);
      }

      const botaoExcluir = document.createElement("button");
      botaoExcluir.className = "user-action-button user-action-delete";
      botaoExcluir.type = "button";
      botaoExcluir.setAttribute("aria-label", `Excluir pedido ${pedido.id ?? ""}`.trim());
      botaoExcluir.title = "Excluir";
      botaoExcluir.innerHTML = '<i class="bx bx-trash" aria-hidden="true"></i>';
      botaoExcluir.addEventListener("click", () => excluirPedido(pedido, botaoExcluir));

      acoes.append(botaoEditar, botaoExcluir);
      celulaAcoes.append(acoes);
      linha.append(celulaAcoes);
      corpoTabela.append(linha);
    });

    mensagemPedidos.textContent = pedidos.length
      ? `${pedidos.length} pedido(s) encontrado(s).`
      : pedidosRecebidos.length
        ? "Nenhum pedido corresponde ao filtro selecionado."
        : "Nenhum pedido cadastrado.";
  } catch (erro) {
    mensagemPedidos.textContent = `Não foi possível carregar os pedidos. ${erro.message}`;
    mensagemPedidos.className = "users-message error";
  }
}

/**
 * Confirma o pagamento do pedido e solicita a baixa de estoque ao backend.
 *
 * @param {Object} pedido Pedido cujo pagamento será confirmado.
 * @param {HTMLButtonElement} botao Botão que iniciou a ação.
 * @returns {Promise<void>}
 */
async function confirmarPagamentoPedido(pedido, botao) {
  if (!window.confirm("Confirmar o pagamento deste pedido? O estoque do produto será reduzido automaticamente.")) {
    return;
  }

  const mensagem = document.querySelector("#orders-message");
  botao.disabled = true;

  try {
    const resposta = await apiFetch(
      `${urlPedidos}/${encodeURIComponent(pedido.id)}/status`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ statusPagamento: "Pagamento Confirmado" })
      }
    );

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    await carregarPedidos();
  } catch (erro) {
    mensagem.textContent = `Não foi possível confirmar o pagamento. ${erro.message}`;
    mensagem.className = "users-message error";
    botao.disabled = false;
  }
}

/**
 * Abre o formulário de pedido e carrega colheitas e compradores em paralelo.
 * Na edição, os vínculos existentes ficam bloqueados porque o backend só aceita
 * alteração de quantidade em pedidos já criados.
 *
 * @param {Object|null} [pedido] Registro existente; null inicia um cadastro.
 * @param {HTMLElement|null} [elementoOrigem] Controle que abriu o modal.
 * @returns {Promise<void>}
 */
async function abrirModalPedido(pedido = null, elementoOrigem = null) {
  const modal = document.querySelector("#order-modal");
  const formulario = document.querySelector("#order-form");

  formulario.reset();
  formulario.dataset.pedidoId = pedido?.id ?? "";
  document.querySelector("#order-modal-title").textContent = pedido
    ? "Editar Pedido"
    : "Cadastrar Pedido";
  document.querySelector("#order-form-message").textContent = "";
  document.querySelector("#order-edit-note").hidden = !pedido;
  document.querySelector("#order-quantity").value = pedido?.quantidadeComprada ?? "";
  modal.returnFocusElement = elementoOrigem;
  modal.hidden = false;

  const campoColheita = document.querySelector("#nomeColheita");
  const campoComprador = document.querySelector("#documentoComprador");
  campoColheita.disabled = true;
  campoComprador.disabled = true;

  try {
    const [colheitaSelecionada, compradorSelecionado] = await Promise.all([
      carregarSelectColheitas(pedido?.nomeColheita ?? ""),
      carregarSelectCompradores(pedido?.documentoComprador ?? "")
    ]);

    campoColheita.disabled = Boolean(pedido) || campoColheita.disabled;
    campoComprador.disabled = Boolean(pedido) || campoComprador.disabled;
    atualizarValoresPedido();

    if ((!colheitaSelecionada && pedido?.nomeColheita) ||
        (!compradorSelecionado && pedido?.documentoComprador)) {
      document.querySelector("#order-form-message").textContent =
        "Um relacionamento do pedido não está disponível na lista atual.";
    }
  } catch (erro) {
    document.querySelector("#order-form-message").textContent =
      `Não foi possível carregar colheitas e compradores. ${erro.message}`;
  }

  modal.returnFocusElement = elementoOrigem;
  document.querySelector("#nomeColheita").focus();
}

/** Fecha o formulário e devolve o foco ao controle que o abriu. */
function fecharModalPedido() {
  const modal = document.querySelector("#order-modal");
  const elementoOrigem = modal.returnFocusElement;
  modal.hidden = true;
  modal.returnFocusElement = null;

  if (elementoOrigem?.isConnected) {
    elementoOrigem.focus();
  } else {
    document.querySelector("#add-order-button").focus();
  }
}

/**
 * Cria um pedido com seus relacionamentos ou altera apenas sua quantidade.
 * As validações locais impedem envio de quantidades inválidas e vínculos vazios.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário.
 * @returns {Promise<void>}
 */
async function salvarPedido(evento) {
  evento.preventDefault();

  const formulario = evento.currentTarget;
  const mensagem = document.querySelector("#order-form-message");
  const botaoSalvar = document.querySelector("#order-save-button");
  const id = formulario.dataset.pedidoId;
  const quantidade = Number(document.querySelector("#order-quantity").value);
  const campoColheita = document.querySelector("#nomeColheita");
  const opcaoColheita = campoColheita.selectedOptions[0];
  const nomeColheita = opcaoColheita?.textContent ?? "";
  const documentoComprador = document.querySelector("#documentoComprador").value;
  const dadosPedido = {
    nomeColheita,
    documentoComprador,
    quantidadeComprada: quantidade
  };

  if (!id && (!nomeColheita || !documentoComprador)) {
    mensagem.textContent = "Selecione uma colheita e um comprador.";
    return;
  }

  if (!Number.isSafeInteger(quantidade) || quantidade < 1) {
    mensagem.textContent = "Informe uma quantidade inteira maior que zero.";
    return;
  }

  const estoque = Number(opcaoColheita?.dataset.estoque);
  if (
    opcaoColheita?.dataset.estoque !== "" &&
    Number.isFinite(estoque) &&
    quantidade > estoque
  ) {
    mensagem.textContent = `Quantidade indisponível. Estoque atual: ${estoque}`;
    return;
  }

  mensagem.textContent = "";
  botaoSalvar.disabled = true;
  botaoSalvar.textContent = "Salvando...";

  try {
    const resposta = await apiFetch(id ? `${urlPedidos}/${encodeURIComponent(id)}` : urlPedidos, {
      method: id ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosPedido)
    });

    if (!resposta.ok) {
      if (resposta.status === 400) {
        const erroApi = await resposta.text();
        mensagem.textContent = erroApi.trim()
          ? erroApi
          : `A API respondeu com o status HTTP ${resposta.status}.`;
      } else {
        mensagem.textContent = `Não foi possível salvar o pedido. A API respondeu com o status HTTP ${resposta.status}.`;
      }

      return;
    }

    fecharModalPedido();
    await carregarPedidos();
  } catch (erro) {
    mensagem.textContent = `Não foi possível salvar o pedido. ${erro.message}`;
  } finally {
    botaoSalvar.disabled = false;
    botaoSalvar.textContent = "Salvar";
  }
}

/**
 * Exclui o pedido após confirmação e atualiza o contador sem recarregar a página.
 *
 * @param {Object} pedido Registro que será excluído.
 * @param {HTMLButtonElement} botaoExcluir Controle que iniciou a ação.
 * @returns {Promise<void>}
 */
async function excluirPedido(pedido, botaoExcluir) {
  if (!window.confirm("Tem certeza que deseja excluir?")) {
    return;
  }

  if (pedido.id === null || pedido.id === undefined || pedido.id === "-") {
    const mensagem = document.querySelector("#orders-message");
    mensagem.textContent = "Não foi possível excluir o pedido: ID não informado.";
    mensagem.className = "users-message error";
    return;
  }

  botaoExcluir.disabled = true;

  try {
    const resposta = await apiFetch(`${urlPedidos}/${encodeURIComponent(pedido.id)}`, {
      method: "DELETE"
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    window.alert("Pedido excluído com sucesso.");
    await carregarPedidos();
  } catch (erro) {
    const mensagem = document.querySelector("#orders-message");
    mensagem.textContent = `Não foi possível excluir o pedido. ${erro.message}`;
    mensagem.className = "users-message error";
    botaoExcluir.disabled = false;
  }
}

/**
 * Lê os compradores e exibe seus documentos e endereços de entrega.
 *
 * @returns {Promise<void>}
 */
async function carregarCompradores() {
  const corpoTabela = document.querySelector("#buyers-table-body");
  const mensagemCompradores = document.querySelector("#buyers-message");
  mensagemCompradores.textContent = "Carregando compradores...";
  mensagemCompradores.className = "users-message";
  corpoTabela.replaceChildren();

  try {
    const compradoresRecebidos = await buscarLista(urlCompradores, "compradores");
    const compradores = filtrarListaPorStatus(compradoresRecebidos);

    compradores.forEach((comprador) => {
      const linha = document.createElement("tr");
      const valores = [
        comprador.id ?? "-",
        comprador.nome ?? "-",
        comprador.cnpjOuCpf,
        comprador.enderecoEntrega
      ].map((valor) => {
        if (valor === null || valor === undefined) {
          return "-";
        }

        if (typeof valor === "string" && valor.trim().toLowerCase() === "string") {
          return "-";
        }

        return valor;
      });

      valores.forEach((valor) => {
        const celula = document.createElement("td");
        celula.textContent = valor;
        linha.append(celula);
      });

      linha.append(criarCelulaStatus(comprador));

      const celulaAcoes = document.createElement("td");
      const acoes = document.createElement("div");
      acoes.className = "user-actions";

      const botaoEditar = document.createElement("button");
      botaoEditar.className = "user-action-button user-action-edit";
      botaoEditar.type = "button";
      botaoEditar.setAttribute("aria-label", `Editar ${comprador.nome || "comprador"}`);
      botaoEditar.title = "Editar";
      botaoEditar.innerHTML = '<i class="bx bx-edit" aria-hidden="true"></i>';
      botaoEditar.addEventListener("click", () => abrirModalComprador(comprador, botaoEditar));

      const botaoExcluir = document.createElement("button");
      botaoExcluir.className = "user-action-button user-action-delete";
      botaoExcluir.type = "button";
      botaoExcluir.setAttribute("aria-label", `Excluir ${comprador.nome || "comprador"}`);
      botaoExcluir.title = "Excluir";
      botaoExcluir.innerHTML = '<i class="bx bx-trash" aria-hidden="true"></i>';
      botaoExcluir.addEventListener("click", () => excluirComprador(comprador, botaoExcluir));

      acoes.append(botaoEditar, botaoExcluir);
      celulaAcoes.append(acoes);
      linha.append(celulaAcoes);
      corpoTabela.append(linha);
    });

    mensagemCompradores.textContent = compradores.length
      ? `${compradores.length} comprador(es) encontrado(s).`
      : compradoresRecebidos.length
        ? "Nenhum comprador corresponde ao filtro selecionado."
        : "Nenhum comprador cadastrado.";
  } catch (erro) {
    mensagemCompradores.textContent = `Não foi possível carregar os compradores. ${erro.message}`;
    mensagemCompradores.className = "users-message error";
  }
}

/**
 * Reidrata os campos de endereço a partir do texto salvo pela aplicação.
 * Endereços em formato legado ou inesperado são preservados no campo de logradouro,
 * evitando descartar informação durante uma edição.
 *
 * @param {string} endereco Endereço completo armazenado no comprador.
 */
function preencherCamposEndereco(endereco) {
  const campos = {
    cep: document.querySelector("#buyer-cep"),
    logradouro: document.querySelector("#buyer-street"),
    numero: document.querySelector("#buyer-number"),
    complemento: document.querySelector("#buyer-complement"),
    bairro: document.querySelector("#buyer-neighborhood"),
    cidade: document.querySelector("#buyer-city"),
    uf: document.querySelector("#buyer-state")
  };

  Object.values(campos).forEach((campo) => {
    campo.value = "";
  });

  if (typeof endereco !== "string" || !endereco.trim()) {
    return;
  }

  const partes = endereco.split(",").map((parte) => parte.trim());
  const numeroEComplemento = partes[1]?.match(/^(.+?)(?:\s+-\s+(.*))?$/);
  const cidadeUf = partes[3]?.match(/^(.*?)\s+-\s*([A-Za-z]{2})$/);
  const cep = partes[4]?.match(/^CEP:\s*(.+)$/i);

  if (partes.length === 5 && numeroEComplemento && cidadeUf && cep) {
    campos.logradouro.value = partes[0];
    campos.numero.value = numeroEComplemento[1];
    campos.complemento.value = numeroEComplemento[2] ?? "";
    campos.bairro.value = partes[2];
    campos.cidade.value = cidadeUf[1];
    campos.uf.value = cidadeUf[2].toUpperCase();
    campos.cep.value = cep[1];
    return;
  }

  campos.logradouro.value = endereco;
}

/**
 * Monta o endereço estruturado em uma única string no formato esperado pela API.
 *
 * @returns {string} Endereço completo de entrega.
 */
function formatarEnderecoEntrega() {
  const logradouro = document.querySelector("#buyer-street").value.trim();
  const numero = document.querySelector("#buyer-number").value.trim();
  const complemento = document.querySelector("#buyer-complement").value.trim();
  const bairro = document.querySelector("#buyer-neighborhood").value.trim();
  const cidade = document.querySelector("#buyer-city").value.trim();
  const uf = document.querySelector("#buyer-state").value.trim().toUpperCase();
  const cepNumerico = document.querySelector("#buyer-cep").value.replace(/\D/g, "");
  const cepFormatado = cepNumerico.length === 8
    ? `${cepNumerico.slice(0, 5)}-${cepNumerico.slice(5)}`
    : cepNumerico;
  const endereco = `${logradouro}, ${numero}${complemento ? ` - ${complemento}` : ""}, ${bairro}, ${cidade} - ${uf}, CEP: ${cepFormatado}`;

  return endereco;
}

/**
 * Consulta o ViaCEP após a saída do campo, valida o CEP e completa o endereço.
 * A chamada externa só ocorre com oito dígitos, evitando consultas inúteis e
 * deixando os campos restantes editáveis para CEPs sem logradouro.
 *
 * @returns {Promise<void>}
 */
async function buscarEnderecoPorCep() {
  const campoCep = document.querySelector("#buyer-cep");
  const mensagemCep = document.querySelector("#buyer-cep-message");
  const cep = campoCep.value.replace(/\D/g, "");
  mensagemCep.textContent = "";

  if (!cep) {
    return;
  }

  if (!/^\d{8}$/.test(cep)) {
    mensagemCep.textContent = "Informe um CEP válido com 8 dígitos.";
    return;
  }

  campoCep.value = `${cep.slice(0, 5)}-${cep.slice(5)}`;

  try {
    // O ViaCEP é público e não usa o JWT reservado às chamadas do backend AgroConnect.
    const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`, {
      headers: {
        Accept: "application/json"
      }
    });

    if (!resposta.ok) {
      throw new Error(`O ViaCEP respondeu com o status HTTP ${resposta.status}.`);
    }

    const endereco = await resposta.json();

    if (endereco.erro) {
      mensagemCep.textContent = "CEP não encontrado. Confira o número e tente novamente.";
      return;
    }

    document.querySelector("#buyer-street").value = endereco.logradouro ?? "";
    document.querySelector("#buyer-neighborhood").value = endereco.bairro ?? "";
    document.querySelector("#buyer-city").value = endereco.localidade ?? "";
    document.querySelector("#buyer-state").value = endereco.uf ?? "";
    document.querySelector("#buyer-number").focus();
  } catch (erro) {
    mensagemCep.textContent = `Não foi possível consultar o CEP. ${erro.message}`;
  }
}

/**
 * Prepara o cadastro ou edição de comprador, decompondo o endereço salvo em campos.
 *
 * @param {Object|null} [comprador] Registro existente; null inicia um cadastro.
 * @param {HTMLElement|null} [elementoOrigem] Controle que abriu o modal.
 */
function abrirModalComprador(comprador = null, elementoOrigem = null) {
  const modal = document.querySelector("#buyer-modal");
  const formulario = document.querySelector("#buyer-form");

  formulario.reset();
  formulario.dataset.compradorId = comprador?.id ?? "";
  document.querySelector("#buyer-modal-title").textContent = comprador
    ? "Editar Comprador"
    : "Cadastrar Comprador";
  document.querySelector("#buyer-form-message").textContent = "";
  document.querySelector("#buyer-cep-message").textContent = "";
  document.querySelector("#buyer-name").value = comprador?.nome ?? "";
  document.querySelector("#buyer-document").value = comprador?.cnpjOuCpf ?? "";
  preencherCamposEndereco(comprador?.enderecoEntrega ?? "");
  modal.returnFocusElement = elementoOrigem;
  modal.hidden = false;
  document.querySelector("#buyer-name").focus();
}

/** Fecha o formulário e devolve o foco ao controle que o abriu. */
function fecharModalComprador() {
  const modal = document.querySelector("#buyer-modal");
  const elementoOrigem = modal.returnFocusElement;
  modal.hidden = true;
  modal.returnFocusElement = null;

  if (elementoOrigem?.isConnected) {
    elementoOrigem.focus();
  } else {
    document.querySelector("#add-buyer-button").focus();
  }
}

/**
 * Cria ou atualiza um comprador, serializando seu endereço no formato da API.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário.
 * @returns {Promise<void>}
 */
async function salvarComprador(evento) {
  evento.preventDefault();

  const formulario = evento.currentTarget;
  const mensagem = document.querySelector("#buyer-form-message");
  const botaoSalvar = document.querySelector("#buyer-save-button");
  const id = formulario.dataset.compradorId;
  const enderecoEntrega = formatarEnderecoEntrega();
  const dadosComprador = {
    nome: document.querySelector("#buyer-name").value.trim(),
    cnpjOuCpf: document.querySelector("#buyer-document").value.trim(),
    enderecoEntrega
  };

  mensagem.textContent = "";
  botaoSalvar.disabled = true;
  botaoSalvar.textContent = "Salvando...";

  try {
    const resposta = await apiFetch(id ? `${urlCompradores}/${encodeURIComponent(id)}` : urlCompradores, {
      method: id ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosComprador)
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    fecharModalComprador();
    await carregarCompradores();
  } catch (erro) {
    mensagem.textContent = `Não foi possível salvar o comprador. ${erro.message}`;
  } finally {
    botaoSalvar.disabled = false;
    botaoSalvar.textContent = "Salvar";
  }
}

/**
 * Exclui o comprador após confirmação e recarrega a lista após sucesso do backend.
 *
 * @param {Object} comprador Registro que será excluído.
 * @param {HTMLButtonElement} botaoExcluir Controle que iniciou a ação.
 * @returns {Promise<void>}
 */
async function excluirComprador(comprador, botaoExcluir) {
  if (!window.confirm("Tem certeza que deseja excluir?")) {
    return;
  }

  if (comprador.id === null || comprador.id === undefined || comprador.id === "-") {
    const mensagem = document.querySelector("#buyers-message");
    mensagem.textContent = "Não foi possível excluir o comprador: ID não informado.";
    mensagem.className = "users-message error";
    return;
  }

  botaoExcluir.disabled = true;

  try {
    const resposta = await apiFetch(`${urlCompradores}/${encodeURIComponent(comprador.id)}`, {
      method: "DELETE"
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    window.alert("Comprador excluído com sucesso.");
    await carregarCompradores();
  } catch (erro) {
    const mensagem = document.querySelector("#buyers-message");
    mensagem.textContent = `Não foi possível excluir o comprador. ${erro.message}`;
    mensagem.className = "users-message error";
    botaoExcluir.disabled = false;
  }
}

/**
 * Lê as colheitas e formata os valores monetários para apresentação brasileira.
 *
 * @returns {Promise<void>}
 */
async function carregarColheitas() {
  const corpoTabela = document.querySelector("#harvests-table-body");
  const mensagemColheitas = document.querySelector("#harvests-message");
  mensagemColheitas.textContent = "Carregando colheitas...";
  mensagemColheitas.className = "users-message";
  corpoTabela.replaceChildren();

  try {
    const colheitasRecebidas = await buscarLista(urlColheitas, "colheitas");
    const colheitas = filtrarListaPorStatus(colheitasRecebidas);

    colheitas.forEach((colheita) => {
      const linha = document.createElement("tr");
      const valorProduto = Number(colheita.valorProduto);
      const valores = [
        colheita.id ?? "-",
        colheita.nomeProduto ?? "-",
        colheita.cpfProdutor ?? "Não informado",
        colheita.quantidadeEstoque ?? "-",
        colheita.valorProduto === null || colheita.valorProduto === undefined || !Number.isFinite(valorProduto)
          ? "-"
          : valorProduto.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
      ];

      valores.forEach((valor) => {
        const celula = document.createElement("td");
        celula.textContent = valor;
        linha.append(celula);
      });

      linha.append(criarCelulaStatus(colheita));

      const celulaAcoes = document.createElement("td");
      const acoes = document.createElement("div");
      acoes.className = "user-actions";

      const botaoEditar = document.createElement("button");
      botaoEditar.className = "user-action-button user-action-edit";
      botaoEditar.type = "button";
      botaoEditar.setAttribute("aria-label", `Editar ${colheita.nomeProduto || "colheita"}`);
      botaoEditar.title = "Editar";
      botaoEditar.innerHTML = '<i class="bx bx-edit" aria-hidden="true"></i>';
      botaoEditar.addEventListener("click", () => abrirModalColheita(colheita, botaoEditar));

      const botaoExcluir = document.createElement("button");
      botaoExcluir.className = "user-action-button user-action-delete";
      botaoExcluir.type = "button";
      botaoExcluir.setAttribute("aria-label", `Excluir ${colheita.nomeProduto || "colheita"}`);
      botaoExcluir.title = "Excluir";
      botaoExcluir.innerHTML = '<i class="bx bx-trash" aria-hidden="true"></i>';
      botaoExcluir.addEventListener("click", () => excluirColheita(colheita, botaoExcluir));

      acoes.append(botaoEditar, botaoExcluir);
      celulaAcoes.append(acoes);
      linha.append(celulaAcoes);
      corpoTabela.append(linha);
    });

    mensagemColheitas.textContent = colheitas.length
      ? `${colheitas.length} colheita(s) encontrada(s).`
      : colheitasRecebidas.length
        ? "Nenhuma colheita corresponde ao filtro selecionado."
        : "Nenhuma colheita cadastrada.";
  } catch (erro) {
    mensagemColheitas.textContent = `Não foi possível carregar as colheitas. ${erro.message}`;
    mensagemColheitas.className = "users-message error";
  }
}

/**
 * Prepara cadastro ou edição de colheita e carrega o produtor relacionado.
 *
 * @param {Object|null} [colheita] Registro existente; null inicia um cadastro.
 * @param {HTMLElement|null} [elementoOrigem] Controle que abriu o modal.
 * @returns {Promise<void>}
 */
async function abrirModalColheita(colheita = null, elementoOrigem = null) {
  const modal = document.querySelector("#harvest-modal");
  const formulario = document.querySelector("#harvest-form");

  formulario.reset();
  formulario.dataset.colheitaId = colheita?.id ?? "";
  document.querySelector("#harvest-modal-title").textContent = colheita
    ? "Editar Colheita"
    : "Cadastrar Colheita";
  document.querySelector("#harvest-form-message").textContent = "";
  document.querySelector("#harvest-product").value = colheita?.nomeProduto ?? "";
  document.querySelector("#harvest-quantity").value = colheita?.quantidadeEstoque ?? "";
  document.querySelector("#harvest-value").value = colheita?.valorProduto ?? "";
  modal.returnFocusElement = elementoOrigem;
  modal.hidden = false;

  try {
    const produtorSelecionado = await carregarSelectUsuarios(colheita?.cpfProdutor ?? "");

    if (!produtorSelecionado && colheita?.cpfProdutor) {
      document.querySelector("#harvest-form-message").textContent =
        "O produtor atualmente vinculado não está disponível. Selecione outra opção.";
    }
  } catch (erro) {
    document.querySelector("#harvest-form-message").textContent =
      `Não foi possível carregar os produtores. ${erro.message}`;
  }

  document.querySelector("#harvest-product").focus();
}

/** Fecha o formulário e devolve o foco ao controle que o abriu. */
function fecharModalColheita() {
  const modal = document.querySelector("#harvest-modal");
  const elementoOrigem = modal.returnFocusElement;
  modal.hidden = true;
  modal.returnFocusElement = null;

  if (elementoOrigem?.isConnected) {
    elementoOrigem.focus();
  } else {
    document.querySelector("#add-harvest-button").focus();
  }
}

/**
 * Cria ou atualiza uma colheita após validar estoque, valor e produtor relacionado.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário.
 * @returns {Promise<void>}
 */
async function salvarColheita(evento) {
  evento.preventDefault();

  const formulario = evento.currentTarget;
  const mensagem = document.querySelector("#harvest-form-message");
  const botaoSalvar = document.querySelector("#harvest-save-button");
  const id = formulario.dataset.colheitaId;
  const quantidade = Number(document.querySelector("#harvest-quantity").value);
  const valor = Number(document.querySelector("#harvest-value").value);
  const cpfProdutor = document.querySelector("#cpfProdutor").value;
  const dadosColheita = {
    nomeProduto: document.querySelector("#harvest-product").value.trim(),
    cpfProdutor,
    quantidadeEstoque: quantidade,
    valorProduto: valor
  };

  if (!id && !cpfProdutor) {
    mensagem.textContent = "Selecione um produtor.";
    return;
  }

  if (!Number.isSafeInteger(quantidade) || quantidade < 0) {
    mensagem.textContent = "Informe uma quantidade inteira válida.";
    return;
  }

  if (!Number.isFinite(valor) || valor < 0) {
    mensagem.textContent = "Informe um valor válido.";
    return;
  }

  mensagem.textContent = "";
  botaoSalvar.disabled = true;
  botaoSalvar.textContent = "Salvando...";

  try {
    const resposta = await apiFetch(id ? `${urlColheitas}/${encodeURIComponent(id)}` : urlColheitas, {
      method: id ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosColheita)
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    fecharModalColheita();
    await carregarColheitas();
  } catch (erro) {
    mensagem.textContent = `Não foi possível salvar a colheita. ${erro.message}`;
  } finally {
    botaoSalvar.disabled = false;
    botaoSalvar.textContent = "Salvar";
  }
}

/**
 * Exclui a colheita após confirmação e atualiza a tabela após sucesso.
 *
 * @param {Object} colheita Registro que será excluído.
 * @param {HTMLButtonElement} botaoExcluir Controle que iniciou a ação.
 * @returns {Promise<void>}
 */
async function excluirColheita(colheita, botaoExcluir) {
  if (!window.confirm("Tem certeza que deseja excluir?")) {
    return;
  }

  if (colheita.id === null || colheita.id === undefined || colheita.id === "-") {
    const mensagem = document.querySelector("#harvests-message");
    mensagem.textContent = "Não foi possível excluir a colheita: ID não informado.";
    mensagem.className = "users-message error";
    return;
  }

  botaoExcluir.disabled = true;

  try {
    const resposta = await apiFetch(`${urlColheitas}/${encodeURIComponent(colheita.id)}`, {
      method: "DELETE"
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    window.alert("Colheita excluída com sucesso.");
    await carregarColheitas();
  } catch (erro) {
    const mensagem = document.querySelector("#harvests-message");
    mensagem.textContent = `Não foi possível excluir a colheita. ${erro.message}`;
    mensagem.className = "users-message error";
    botaoExcluir.disabled = false;
  }
}

/**
 * Lê as empresas e apresenta seus dados e quantidade de usuários vinculados.
 *
 * @returns {Promise<void>}
 */
async function carregarEmpresas() {
  const corpoTabela = document.querySelector("#companies-table-body");
  const mensagemEmpresas = document.querySelector("#companies-message");
  mensagemEmpresas.textContent = "Carregando empresas...";
  mensagemEmpresas.className = "users-message";
  corpoTabela.replaceChildren();

  try {
    const empresasRecebidas = await buscarLista(urlEmpresas, "empresas");
    const empresas = filtrarListaPorStatus(empresasRecebidas);

    empresas.forEach((empresa) => {
      const linha = document.createElement("tr");
      const valores = [
        empresa.id ?? "-",
        empresa.razaoSocial,
        empresa.nomeFantasia,
        empresa.cnpj,
        empresa.inscricaoEstadual,
        empresa.quantidadeUsuario ?? "-"
      ].map((valor) => {
        if (valor === null || valor === undefined) {
          return "-";
        }

        if (typeof valor === "string" && valor.trim().toLowerCase() === "string") {
          return "-";
        }

        return valor;
      });

      valores.forEach((valor) => {
        const celula = document.createElement("td");
        celula.textContent = valor;
        linha.append(celula);
      });

      linha.append(criarCelulaStatus(empresa));

      const celulaAcoes = document.createElement("td");
      const acoes = document.createElement("div");
      acoes.className = "user-actions";

      const botaoEditar = document.createElement("button");
      botaoEditar.className = "user-action-button user-action-edit";
      botaoEditar.type = "button";
      botaoEditar.setAttribute("aria-label", `Editar ${empresa.razaoSocial || "empresa"}`);
      botaoEditar.title = "Editar";
      botaoEditar.innerHTML = '<i class="bx bx-edit" aria-hidden="true"></i>';
      botaoEditar.addEventListener("click", () => abrirModalEmpresa(empresa, botaoEditar));

      const botaoExcluir = document.createElement("button");
      botaoExcluir.className = "user-action-button user-action-delete";
      botaoExcluir.type = "button";
      botaoExcluir.setAttribute("aria-label", `Excluir ${empresa.razaoSocial || "empresa"}`);
      botaoExcluir.title = "Excluir";
      botaoExcluir.innerHTML = '<i class="bx bx-trash" aria-hidden="true"></i>';
      botaoExcluir.addEventListener("click", () => excluirEmpresa(empresa, botaoExcluir));

      acoes.append(botaoEditar, botaoExcluir);
      celulaAcoes.append(acoes);
      linha.append(celulaAcoes);
      corpoTabela.append(linha);
    });

    mensagemEmpresas.textContent = empresas.length
      ? `${empresas.length} empresa(s) encontrada(s).`
      : empresasRecebidas.length
        ? "Nenhuma empresa corresponde ao filtro selecionado."
        : "Nenhuma empresa cadastrada.";
  } catch (erro) {
    mensagemEmpresas.textContent = `Não foi possível carregar as empresas. ${erro.message}`;
    mensagemEmpresas.className = "users-message error";
  }
}

/**
 * Preenche o formulário de empresa para criação ou edição.
 *
 * @param {Object|null} [empresa] Registro existente; null inicia um cadastro.
 * @param {HTMLElement|null} [elementoOrigem] Controle que abriu o modal.
 */
function abrirModalEmpresa(empresa = null, elementoOrigem = null) {
  const modal = document.querySelector("#company-modal");
  const formulario = document.querySelector("#company-form");

  formulario.reset();
  formulario.dataset.empresaId = empresa?.id ?? "";
  document.querySelector("#company-modal-title").textContent = empresa
    ? "Editar Empresa"
    : "Cadastrar Empresa";
  document.querySelector("#company-form-message").textContent = "";
  document.querySelector("#company-legal-name").value = empresa?.razaoSocial ?? "";
  document.querySelector("#company-trade-name").value = empresa?.nomeFantasia ?? "";
  document.querySelector("#company-cnpj").value = empresa?.cnpj ?? "";
  document.querySelector("#company-state-registration").value = empresa?.inscricaoEstadual ?? "";
  modal.returnFocusElement = elementoOrigem;
  modal.hidden = false;
  document.querySelector("#company-legal-name").focus();
}

/** Fecha o formulário e devolve o foco ao controle que o abriu. */
function fecharModalEmpresa() {
  const modal = document.querySelector("#company-modal");
  const elementoOrigem = modal.returnFocusElement;
  modal.hidden = true;
  modal.returnFocusElement = null;

  if (elementoOrigem?.isConnected) {
    elementoOrigem.focus();
  } else {
    document.querySelector("#add-company-button").focus();
  }
}

/**
 * Cria ou atualiza uma empresa e recarrega a lista após a confirmação do backend.
 *
 * @param {SubmitEvent} evento Evento de envio do formulário.
 * @returns {Promise<void>}
 */
async function salvarEmpresa(evento) {
  evento.preventDefault();

  const formulario = evento.currentTarget;
  const mensagem = document.querySelector("#company-form-message");
  const botaoSalvar = document.querySelector("#company-save-button");
  const id = formulario.dataset.empresaId;
  const dadosEmpresa = {
    razaoSocial: document.querySelector("#company-legal-name").value.trim(),
    nomeFantasia: document.querySelector("#company-trade-name").value.trim(),
    cnpj: document.querySelector("#company-cnpj").value.trim(),
    inscricaoEstadual: document.querySelector("#company-state-registration").value.trim()
  };

  mensagem.textContent = "";
  botaoSalvar.disabled = true;
  botaoSalvar.textContent = "Salvando...";

  try {
    const resposta = await apiFetch(id ? `${urlEmpresas}/${encodeURIComponent(id)}` : urlEmpresas, {
      method: id ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(dadosEmpresa)
    });

    if (!resposta.ok) {
      throw new Error(`A API respondeu com o status HTTP ${resposta.status}.`);
    }

    fecharModalEmpresa();
    await carregarEmpresas();
  } catch (erro) {
    mensagem.textContent = `Não foi possível salvar a empresa. ${erro.message}`;
  } finally {
    botaoSalvar.disabled = false;
    botaoSalvar.textContent = "Salvar";
  }
}

/**
 * Exclui a empresa após confirmação, preservando a mensagem detalhada da API
 * quando a operação falha (por exemplo, por vínculos ainda existentes).
 *
 * @param {Object} empresa Registro que será excluído.
 * @param {HTMLButtonElement} botaoExcluir Controle que iniciou a ação.
 * @returns {Promise<void>}
 */
async function excluirEmpresa(empresa, botaoExcluir) {
  if (!window.confirm("Tem certeza que deseja excluir?")) {
    return;
  }

  if (empresa.id === null || empresa.id === undefined || empresa.id === "-") {
    const mensagem = document.querySelector("#companies-message");
    mensagem.textContent = "Não foi possível excluir a empresa: ID não informado.";
    mensagem.className = "users-message error";
    return;
  }

  botaoExcluir.disabled = true;

  try {
    const resposta = await apiFetch(`${urlEmpresas}/${encodeURIComponent(empresa.id)}`, {
      method: "DELETE"
    });

    if (!resposta.ok) {
      const textoResposta = await resposta.text();
      let detalheErro = textoResposta;

      if (textoResposta) {
        try {
          detalheErro = JSON.parse(textoResposta).mensagem || textoResposta;
        } catch {
          detalheErro = textoResposta;
        }
      }

      throw new Error(detalheErro || `A API respondeu com o status HTTP ${resposta.status}.`);
    }

    window.alert("Empresa excluída com sucesso.");
    await carregarEmpresas();
  } catch (erro) {
    const mensagem = document.querySelector("#companies-message");
    mensagem.textContent = `Não foi possível excluir a empresa. ${erro.message}`;
    mensagem.className = "users-message error";
    botaoExcluir.disabled = false;
  }
}

/**
 * Carrega os indicadores independentes em paralelo para que uma falha parcial
 * não impeça a apresentação do outro resumo do dashboard.
 *
 * @returns {Promise<void>}
 */
async function carregarDashboard() {
  const totalPedidos = document.querySelector("#dashboard-orders-total");
  const detalhesPedidos = document.querySelector("#dashboard-orders-detail");
  const totalColheitas = document.querySelector("#dashboard-harvests-total");
  const detalhesColheitas = document.querySelector("#dashboard-harvests-detail");

  const [resultadoPedidos, resultadoColheitas] = await Promise.allSettled([
    buscarLista(urlPedidos, "pedidos"),
    buscarLista(urlColheitas, "colheitas")
  ]);

  if (resultadoPedidos.status === "fulfilled") {
    totalPedidos.textContent = resultadoPedidos.value.length.toLocaleString("pt-BR");
    detalhesPedidos.textContent = resultadoPedidos.value.length
      ? "Pedidos registrados no sistema."
      : "Nenhum pedido cadastrado.";
  } else {
    totalPedidos.textContent = "—";
    detalhesPedidos.textContent = `Não foi possível carregar os pedidos. ${resultadoPedidos.reason.message}`;
    detalhesPedidos.classList.add("summary-card-error");
  }

  if (resultadoColheitas.status === "fulfilled") {
    const colheitas = resultadoColheitas.value;
    const nomesColheitas = colheitas
      .map((colheita) => colheita.nomeProduto)
      .filter((nome) => typeof nome === "string" && nome.trim() && nome !== "-")
      .map((nome) => nome.trim());
    const nomesExibidos = nomesColheitas.slice(0, 3);
    const quantidadeRestante = nomesColheitas.length - nomesExibidos.length;

    totalColheitas.textContent = colheitas.length.toLocaleString("pt-BR");
    detalhesColheitas.textContent = colheitas.length
      ? nomesExibidos.length
        ? `${nomesExibidos.join(" • ")}${quantidadeRestante > 0 ? ` e mais ${quantidadeRestante}` : ""}`
        : "Colheitas cadastradas sem nome informado."
      : "Nenhuma colheita cadastrada.";
  } else {
    totalColheitas.textContent = "—";
    detalhesColheitas.textContent = `Não foi possível carregar as colheitas. ${resultadoColheitas.reason.message}`;
    detalhesColheitas.classList.add("summary-card-error");
  }
}

/** Recolhe ou expande a navegação e mantém aria-expanded sincronizado. */
function alternarMenu() {
  const menuEstaAberto = document.body.classList.toggle("menu-recolhido") === false;
  document.querySelector("#menu-button").setAttribute("aria-expanded", String(menuEstaAberto));
}

const formularioLogin = document.querySelector("#login-form");
if (formularioLogin) {
  formularioLogin.addEventListener("submit", enviarLogin);
}

window.verificarAutenticacao = verificarAutenticacao;
window.apiFetch = apiFetch;
const paginaInterna = document.body.dataset.page !== "login";
// O gate evita até mesmo iniciar as consultas das páginas internas sem sessão local.
const autenticado = !paginaInterna || verificarAutenticacao();

const botaoSair = document.querySelector(".logout-button");
if (botaoSair) {
  botaoSair.addEventListener("click", () => {
    localStorage.removeItem("token");
  });
}

const botaoMenu = document.querySelector("#menu-button");
if (autenticado && botaoMenu) {
  botaoMenu.addEventListener("click", alternarMenu);
}

if (autenticado) {
  ["#user-cpf", "#company-cnpj", "#buyer-document"].forEach((seletor) => {
    const campo = document.querySelector(seletor);
    if (campo) {
      campo.addEventListener("input", () => manterSomenteNumeros(campo));
    }
  });

  const filtroStatus = document.querySelector("#filtroStatus");
  const carregarListaPorPagina = {
    usuarios: carregarUsuarios,
    pedidos: carregarPedidos,
    compradores: carregarCompradores,
    colheitas: carregarColheitas,
    empresas: carregarEmpresas
  }[document.body.dataset.page];

  if (filtroStatus && carregarListaPorPagina) {
    filtroStatus.addEventListener("change", carregarListaPorPagina);
  }
}

if (autenticado && document.body.dataset.page === "usuarios") {
  const modalUsuario = document.querySelector("#user-modal");
  document.querySelector("#add-user-button").addEventListener("click", () => abrirModalUsuario());
  document.querySelector("#user-cancel-button").addEventListener("click", fecharModalUsuario);
  document.querySelector("#user-form").addEventListener("submit", salvarUsuario);
  modalUsuario.addEventListener("click", (evento) => {
    if (evento.target === modalUsuario) {
      fecharModalUsuario();
    }
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && !modalUsuario.hidden) {
      fecharModalUsuario();
    }
  });
}

if (autenticado && document.body.dataset.page === "pedidos") {
  const modalPedido = document.querySelector("#order-modal");
  document.querySelector("#add-order-button").addEventListener("click", () => abrirModalPedido());
  document.querySelector("#order-cancel-button").addEventListener("click", fecharModalPedido);
  document.querySelector("#order-form").addEventListener("submit", salvarPedido);
  document.querySelector("#nomeColheita").addEventListener("change", atualizarValoresPedido);
  document.querySelector("#order-quantity").addEventListener("input", atualizarValoresPedido);
  modalPedido.addEventListener("click", (evento) => {
    if (evento.target === modalPedido) {
      fecharModalPedido();
    }
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && !modalPedido.hidden) {
      fecharModalPedido();
    }
  });
}

if (autenticado && document.body.dataset.page === "compradores") {
  const modalComprador = document.querySelector("#buyer-modal");
  document.querySelector("#add-buyer-button").addEventListener("click", () => abrirModalComprador());
  document.querySelector("#buyer-cancel-button").addEventListener("click", fecharModalComprador);
  document.querySelector("#buyer-form").addEventListener("submit", salvarComprador);
  document.querySelector("#buyer-cep").addEventListener("blur", buscarEnderecoPorCep);
  modalComprador.addEventListener("click", (evento) => {
    if (evento.target === modalComprador) {
      fecharModalComprador();
    }
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && !modalComprador.hidden) {
      fecharModalComprador();
    }
  });
}

if (autenticado && document.body.dataset.page === "colheitas") {
  const modalColheita = document.querySelector("#harvest-modal");
  document.querySelector("#add-harvest-button").addEventListener("click", () => abrirModalColheita());
  document.querySelector("#harvest-cancel-button").addEventListener("click", fecharModalColheita);
  document.querySelector("#harvest-form").addEventListener("submit", salvarColheita);
  modalColheita.addEventListener("click", (evento) => {
    if (evento.target === modalColheita) {
      fecharModalColheita();
    }
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && !modalColheita.hidden) {
      fecharModalColheita();
    }
  });
}

if (autenticado && document.body.dataset.page === "empresas") {
  const modalEmpresa = document.querySelector("#company-modal");
  document.querySelector("#add-company-button").addEventListener("click", () => abrirModalEmpresa());
  document.querySelector("#company-cancel-button").addEventListener("click", fecharModalEmpresa);
  document.querySelector("#company-form").addEventListener("submit", salvarEmpresa);
  modalEmpresa.addEventListener("click", (evento) => {
    if (evento.target === modalEmpresa) {
      fecharModalEmpresa();
    }
  });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && !modalEmpresa.hidden) {
      fecharModalEmpresa();
    }
  });
}

if (autenticado) switch (document.body.dataset.page) {
  case "dashboard":
    carregarDashboard();
    break;
  case "usuarios":
    carregarUsuarios();
    break;
  case "pedidos":
    carregarPedidos();
    break;
  case "compradores":
    carregarCompradores();
    break;
  case "colheitas":
    carregarColheitas();
    break;
  case "empresas":
    carregarEmpresas();
    break;
}
