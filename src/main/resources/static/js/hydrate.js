import { openPanel }                  from "./panels.js";
import { toggleTheme }                from "./theme.js";
import { sendBotMessage, handleBotSend, BOT_PROMPTS } from "./bot.js";

export function hydrate(root) {
    bindOnce(root, "[data-open-panel]", "boundOpenPanel", (button) => {
        button.addEventListener("click", () => openPanel(button.dataset.openPanel));
    });

    bindOnce(root, "[data-form-toggle]", "boundFormToggle", (button) => {
        button.addEventListener("click", () => {
            const form = button.closest(".transacao-form");
            if (!form) return;

            const expanded = !form.classList.contains("is-expanded");
            form.classList.toggle("is-expanded",  expanded);
            form.classList.toggle("is-collapsed", !expanded);
            button.setAttribute("aria-expanded", expanded ? "true" : "false");
        });
    });

    bindOnce(root, "[data-menu-toggle]", "boundMenuToggle", (button) => {
        button.addEventListener("click", () => document.body.classList.toggle("menu-open"));
    });

    bindOnce(root, "[data-theme-toggle]", "boundThemeToggle", (button) => {
        button.addEventListener("click", toggleTheme);
    });

    bindOnce(root, "[data-bot-action]", "boundBotAction", (button) => {
        button.addEventListener("click", () => {
            const prompt = BOT_PROMPTS[button.dataset.botAction] || button.textContent.trim();
            sendBotMessage(prompt, button.dataset.botAction);
        });
    });

    bindOnce(root, "[data-bot-send]", "boundBotSend", (button) => {
        button.addEventListener("click", handleBotSend);
    });

    bindOnce(root, "[data-bot-input]", "boundBotInput", (input) => {
        input.addEventListener("keydown", (event) => {
            if (event.key === "Enter" && !event.shiftKey) {
                event.preventDefault();
                handleBotSend();
            }
        });
    });

    bindOnce(root, "[data-goal-toggle]", "boundGoalToggle", (button) => {
        button.addEventListener("click", () => {
            const form = button.closest(".insight-card")?.querySelector("[data-goal-form]");
            if (!form) return;
            const hidden = form.classList.toggle("is-hidden");
            button.textContent = hidden ? "Alterar meta" : "Fechar";
        });
    });

    bindOnce(root, "[data-pie-percent]", "boundPiePercent", (element) => {
        window.setTimeout(() => {
            element.style.setProperty("--meta-percent", element.dataset.piePercent);
        }, 50);
    });
}

function bindOnce(root, selector, flag, handler) {
    root.querySelectorAll(selector).forEach((element) => {
        if (element.dataset[flag]) return;
        element.dataset[flag] = "true";
        handler(element);
    });
}