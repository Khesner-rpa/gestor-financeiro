const MAX_LENGTH = 220;

export const BOT_PROMPTS = {
    resumo:    "Resumir meu mes",
    otimizar:  "Como reduzir gastos?",
    meta:      "Como esta minha meta?",
    categoria: "Onde gasto mais?"
};

function botContext() {
    const bot = document.querySelector("#finance-bot");
    return {
        saldo:     bot?.dataset.saldo     || "0,00",
        receitas:  bot?.dataset.receitas  || "0,00",
        despesas:  bot?.dataset.despesas  || "0,00",
        categoria: bot?.dataset.categoria || "nenhuma categoria",
        economia:  bot?.dataset.economia  || "0,00",
        insight:   bot?.dataset.insight   || ""
    };
}

function buildBotAnswer(text, topic) {
    const ctx        = botContext();
    const normalized = text.toLowerCase();

    if (topic === "resumo" || normalized.includes("resum")) {
        return `Seu saldo atual e R$ ${ctx.saldo}. Neste mes voce recebeu R$ ${ctx.receitas} e gastou R$ ${ctx.despesas}.`;
    }

    if (topic === "otimizar" || normalized.includes("reduz") || normalized.includes("econom")) {
        return `Comece revisando ${ctx.categoria}. Separar R$ ${ctx.economia} para a reserva ja melhora seu ritmo mensal.`;
    }

    if (topic === "meta" || normalized.includes("meta") || normalized.includes("reserva")) {
        return `Sua meta de reserva ainda precisa de R$ ${ctx.economia}. `
             + (ctx.insight || "Revise despesas variaveis para acelerar o progresso.");
    }

    if (topic === "categoria" || normalized.includes("categoria") || normalized.includes("gasto")) {
        return `A categoria com maior peso no mes e ${ctx.categoria}. `
             + "Priorize cortes pequenos nela antes de mexer em gastos fixos.";
    }

    if (normalized.includes("saldo")) {
        return `Seu saldo atual e R$ ${ctx.saldo}.`;
    }

    if (normalized.includes("receita") || normalized.includes("entrada")) {
        return `Suas receitas do mes somam R$ ${ctx.receitas}.`;
    }

    if (normalized.includes("despesa") || normalized.includes("gastei")) {
        return `Suas despesas do mes somam R$ ${ctx.despesas}.`;
    }

    return ctx.insight
        || `Com base nos seus dados: saldo R$ ${ctx.saldo}, receitas R$ ${ctx.receitas} e despesas R$ ${ctx.despesas}.`;
}

function appendBotMessage(text, className) {
    const feed = document.querySelector("#bot-feed");
    if (!feed) return;

    document.querySelector(".bot-welcome")?.classList.add("is-hidden");

    const message       = document.createElement("div");
    message.className   = className;
    message.textContent = text;
    feed.appendChild(message);
    feed.scrollTop = feed.scrollHeight;
}

export function sendBotMessage(text, topic) {
    const question = text.trim().slice(0, MAX_LENGTH);
    if (!question) return;

    appendBotMessage(question, "bot-message user-message");
    window.setTimeout(() => {
        appendBotMessage(buildBotAnswer(question, topic), "bot-message bot-message--assistant");
    }, 280);
}

export function handleBotSend() {
    const input = document.querySelector("#bot-input");
    if (!input) return;

    const text = input.value.trim();
    if (!text) return;

    sendBotMessage(text);
    input.value = "";
    input.focus();
}
