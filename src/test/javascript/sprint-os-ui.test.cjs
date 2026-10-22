const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const root = path.resolve(__dirname, '../../main/resources/static');
const encode = value => String(value).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');

function carregar(relative, extras = {}) {
  let definition;
  const requests = [];
  const Ext = {
    define: (_name, config) => { definition = config; },
    decode: value => { try { return JSON.parse(value); } catch { return null; } },
    JSON: { decode: value => JSON.parse(value) },
    isArray: Array.isArray,
    isDate: value => value instanceof Date,
    isEmpty: value => value == null || value === '' || (Array.isArray(value) && !value.length),
    apply: Object.assign,
    String: { htmlEncode: encode },
    Array: { each: (values, fn) => (values || []).forEach(fn), map: (values, fn) => values.map(fn), some: (values, fn) => values.some(fn) },
    Date: {
      parse: value => new Date(value),
      format: (value, format) => {
        const date = value instanceof Date ? value : new Date(value);
        const pad = number => String(number).padStart(2, '0');
        if (format === 'Y-m-d') return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())}`;
        return `${pad(date.getDate())}/${pad(date.getMonth()+1)}/${date.getFullYear()} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
      }
    },
    Ajax: { request: options => { requests.push(options); } },
    Msg: { alert: () => {} }
  };
  vm.runInNewContext(fs.readFileSync(path.join(root, relative), 'utf8'), {
    Ext, Date, console, Intl, window: { location: { origin: 'http://laboratorio' } },
    sistemaOsLocal: { apiUrl: 'http://laboratorio' }, ...extras
  }, { filename: relative });
  return { definition, requests };
}

function laboratorioRelatorio() {
  const loaded = carregar('classic/src/view/os/RelatorioOsWindow.js');
  const c = { ...loaded.definition.controller, start: 0, limit: 25,
    filtrosAplicados: {}, filtrosEmExibicao: {}, dadosStatus: [], dadosPeriodo: [] };
  const refs = {};
  for (const name of ['graficoStatus', 'graficoPeriodo']) {
    refs[name] = { rendered: true, getWidth: () => 400, setHtml: html => { refs[name].html = html; } };
  }
  refs.rotuloPaginacao = { setHtml: html => { refs.rotuloPaginacao.html = html; } };
  refs.btnAnterior = { setDisabled: value => { refs.btnAnterior.disabled = value; } };
  refs.btnProxima = { setDisabled: value => { refs.btnProxima.disabled = value; } };
  refs.tabelaRelatorio = { getStore: () => ({ loadData: values => { refs.tabelaRelatorio.values = values; } }) };
  c.getView = () => ({ destroyed: false, setLoading: () => {} });
  c.lookupReference = name => refs[name];
  return { c, refs, requests: loaded.requests };
}

test('normaliza situações da API e da listagem e bloqueia encerradas', () => {
  const { definition: helper } = carregar('app/util/OsOperacional.js');
  assert.equal(helper.normalizarSituacao('Concluído'), 'CONCLUIDO');
  assert.equal(helper.normalizarSituacao('Em Andamento'), 'EM_ANDAMENTO');
  assert.equal(helper.estaEncerrada({ situacao: 'Cancelado' }), true);
  assert.equal(helper.estaEncerrada({ situacao: 'PENDENTE', dataFim: '2026-10-21' }), true);
  assert.equal(helper.estaEncerrada({ situacao: 'EM_ANDAMENTO' }), false);
});

test('autor edita seu comentário, outro comum não; administrador edita qualquer', () => {
  const Sessao = { usuarioId: 2, isAdm: () => false };
  const { definition } = carregar('classic/src/view/os/ComentariosOsWindow.js', { Sessao });
  const record = id => ({ get: name => name === 'autor' ? { id } : null });
  assert.equal(Boolean(definition.controller.podeEditarRegistro(record(2))), true);
  assert.equal(Boolean(definition.controller.podeEditarRegistro(record(3))), false);
  Sessao.isAdm = () => true;
  assert.equal(Boolean(definition.controller.podeEditarRegistro(record(3))), true);
});

test('renderizadores apresentam data e editor com o controlador da janela, não da grade', () => {
  const { definition } = carregar('classic/src/view/os/ComentariosOsWindow.js');
  const grid = definition.items[0].items[0];
  const controller = { formatarData: value => value ? '21/10/2026 19:00' : '' };
  const scope = { up: () => ({ getController: () => controller }), getController: () => null };
  assert.match(grid.columns.find(c => c.dataIndex === 'dataCriacao').renderer.call(scope, '2026-10-21T19:00:00'), /21\/10\/2026/);
  const value = grid.columns.find(c => c.dataIndex === 'editadoPor').renderer.call(scope,
    { nome: '<Administrador>' }, {}, { get: () => '2026-10-21T19:00:00' });
  assert.match(value, /&lt;Administrador&gt;/);
  assert.match(value, /21\/10\/2026/);
});

test('comentário HTML é texto, não conteúdo executável', () => {
  const { definition } = carregar('classic/src/view/os/ComentariosOsWindow.js');
  const renderer = definition.items[0].items[0].columns.find(c => c.dataIndex === 'conteudo').renderer;
  const output = renderer('<script>teste</script>', {});
  assert.equal(output, '&lt;script&gt;teste&lt;/script&gt;');
});

test('relatório usa filtros aplicados na paginação e renderiza total, tabela e ambos os gráficos', () => {
  const { c, refs, requests } = laboratorioRelatorio();
  c.filtrosAplicados = { situacao: 'PENDENTE', dataInicio: '2026-10-01', dataFim: '2026-10-02' };
  c.start = 25;
  c.carregarRelatorio();
  assert.equal(requests[0].params.situacao, 'PENDENTE');
  assert.equal(requests[0].params.start, 25);
  requests[0].success({ responseText: JSON.stringify({ linhas: [{ id: 26 }], total: 26,
    porStatus: [{ situacao: 'PENDENTE', total: 26 }],
    porPeriodo: [{ periodo: '2026-10-01', total: 26 }], filtros: c.filtrosAplicados }) });
  assert.equal(refs.tabelaRelatorio.values.length, 1);
  assert.match(refs.rotuloPaginacao.html, /26–26 de 26/);
  assert.match(refs.graficoStatus.html, /<svg/);
  assert.match(refs.graficoPeriodo.html, /<svg/);
  assert.match(refs.graficoPeriodo.html, /2026-10-02/); // dia sem emissão preenchido com zero
});

test('resposta antiga não substitui resultado de um filtro mais recente', () => {
  const { c, refs, requests } = laboratorioRelatorio();
  c.carregarRelatorio();
  c.filtrosAplicados = { situacao: 'CANCELADO' };
  c.carregarRelatorio();
  requests[1].success({ responseText: JSON.stringify({ linhas: [{ id: 2 }], total: 1,
    porStatus: [{ situacao: 'CANCELADO', total: 1 }], porPeriodo: [], filtros: c.filtrosAplicados }) });
  requests[0].success({ responseText: JSON.stringify({ linhas: [{ id: 1 }], total: 8, porStatus: [], porPeriodo: [] }) });
  assert.equal(refs.tabelaRelatorio.values[0].id, 2);
  assert.equal(c.total, 1);
});

test('gráficos vazios e de um único dia não geram NaN ou Infinity', () => {
  const { c } = laboratorioRelatorio();
  const statusVazio = c.criarGraficoStatus([], 400);
  const periodoVazio = c.criarGraficoPeriodo([], 400);
  const umDia = c.criarGraficoPeriodo([{ periodo: '2026-10-21', total: 1 }], 400);
  assert.match(statusVazio, /Nenhuma OS/);
  assert.match(periodoVazio, /Nenhuma OS/);
  assert.doesNotMatch(umDia, /NaN|Infinity/);
  assert.match(umDia, /2026-10-21: 1/);
});
