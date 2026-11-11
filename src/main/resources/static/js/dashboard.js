document.addEventListener("DOMContentLoaded", () => {
    const inicio = document.querySelector("#inicio");
    const fim = document.querySelector("#fim");
    const formulario = document.querySelector("#dashboard-filtros");
    const feedback = document.querySelector("#dashboard-feedback");
    const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
    const numero = new Intl.NumberFormat("pt-BR");

    if (!inicio || !fim || !formulario) return;

    const dataIso = (data) => {
        const ano = data.getFullYear();
        const mes = String(data.getMonth() + 1).padStart(2, "0");
        const dia = String(data.getDate()).padStart(2, "0");
        return `${ano}-${mes}-${dia}`;
    };

    const definirPeriodo = (tipo) => {
        const hoje = new Date();
        let primeiraData = new Date(hoje);
        if (tipo === "7") primeiraData.setDate(hoje.getDate() - 6);
        if (tipo === "mes") primeiraData = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
        inicio.value = dataIso(primeiraData);
        fim.value = dataIso(hoje);
    };

    const parametros = () => new URLSearchParams({ inicio: inicio.value, fim: fim.value });
    const buscar = async (recurso) => {
        const resposta = await fetch(`/api/dashboard/${recurso}?${parametros()}`, {
            headers: { Accept: "application/json" }
        });
        if (!resposta.ok) {
            const erro = await resposta.json().catch(() => null);
            throw new Error(erro?.mensagem || "Não foi possível atualizar o dashboard.");
        }
        return resposta.json();
    };

    const dataCurta = (iso) => {
        const [ano, mes, dia] = iso.split("-").map(Number);
        return new Intl.DateTimeFormat("pt-BR", { day: "2-digit", month: "2-digit" })
            .format(new Date(ano, mes - 1, dia));
    };

    const vazio = (texto) => `<p class="dashboard-empty">${texto}</p>`;

    const preencherResumo = (resumo) => {
        document.querySelector("#metrica-faturamento").textContent = moeda.format(resumo.faturamento);
        document.querySelector("#metrica-ticket").textContent = moeda.format(resumo.ticketMedio);
        document.querySelector("#metrica-pedidos").textContent = numero.format(resumo.pedidosRecebidos);
        document.querySelector("#metrica-descontos").textContent = moeda.format(resumo.descontosConcedidos);
        document.querySelector("#metrica-cancelamentos").textContent = numero.format(resumo.cancelamentos);
    };

    const preencherVendas = (vendas) => {
        const grafico = document.querySelector("#grafico-vendas");
        const total = vendas.reduce((soma, item) => soma + Number(item.faturamento), 0);
        document.querySelector("#grafico-total").textContent = moeda.format(total);
        if (!vendas.some((item) => Number(item.faturamento) > 0)) {
            grafico.innerHTML = vazio("Nenhuma venda finalizada neste período.");
            return;
        }
        const maior = Math.max(...vendas.map((item) => Number(item.faturamento)), 1);
        grafico.innerHTML = vendas.map((item) => {
            const percentual = Math.max((Number(item.faturamento) / maior) * 100, 3);
            return `<div class="sales-column" title="${dataCurta(item.data)}: ${moeda.format(item.faturamento)}">
                <div class="sales-bar-area">
                    <span class="sales-value" style="bottom:calc(${percentual}% + 6px)">${Number(item.faturamento) > 0 ? moeda.format(item.faturamento) : ""}</span>
                    <span class="sales-bar" style="height:${percentual}%"></span>
                </div>
                <small>${dataCurta(item.data)}</small>
            </div>`;
        }).join("");
    };

    const preencherStatus = (status) => {
        const destino = document.querySelector("#grafico-status");
        const maior = Math.max(...status.map((item) => item.quantidade), 1);
        if (!status.some((item) => item.quantidade > 0)) {
            destino.innerHTML = vazio("Não há pedidos no período.");
            return;
        }
        destino.innerHTML = status.filter((item) => item.quantidade > 0).map((item) => `
            <div class="status-row"><div><span>${item.rotulo}</span><strong>${numero.format(item.quantidade)}</strong></div>
            <span class="status-track"><span style="width:${(item.quantidade / maior) * 100}%"></span></span></div>`).join("");
    };

    const preencherRanking = (produtos) => {
        const destino = document.querySelector("#ranking-produtos");
        if (produtos.length === 0) {
            destino.innerHTML = vazio("Nenhum produto vendido no período.");
            return;
        }
        destino.innerHTML = produtos.map((produto, indice) => `<li><span class="ranking-position">${indice + 1}</span>
            <div><strong>${produto.produto}</strong><small>${numero.format(produto.quantidadeVendida)} unidades</small></div>
            <b>${moeda.format(produto.faturamento)}</b></li>`).join("");
    };

    const atualizar = async () => {
        if (!inicio.value || !fim.value || inicio.value > fim.value) {
            feedback.textContent = "Informe um período válido.";
            feedback.className = "dashboard-feedback error";
            return;
        }
        feedback.textContent = "Atualizando dados…";
        feedback.className = "dashboard-feedback";
        try {
            const [resumo, vendas, status, produtos] = await Promise.all([
                buscar("resumo"), buscar("vendas-por-dia"), buscar("pedidos-por-status"), buscar("produtos-mais-vendidos")
            ]);
            preencherResumo(resumo);
            preencherVendas(vendas);
            preencherStatus(status);
            preencherRanking(produtos);
            feedback.textContent = `Dados atualizados para ${dataCurta(inicio.value)} a ${dataCurta(fim.value)}.`;
            feedback.className = "dashboard-feedback success";
        } catch (erro) {
            feedback.textContent = erro.message;
            feedback.className = "dashboard-feedback error";
        }
    };

    formulario.addEventListener("submit", (evento) => {
        evento.preventDefault();
        atualizar();
    });
    document.querySelectorAll("[data-periodo]").forEach((botao) => botao.addEventListener("click", () => {
        definirPeriodo(botao.dataset.periodo);
        atualizar();
    }));

    definirPeriodo("7");
    atualizar();
});
