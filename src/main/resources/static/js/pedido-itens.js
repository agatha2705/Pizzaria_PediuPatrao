document.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector("[data-order-items]");
    const template = document.querySelector("#item-template");
    const addButton = document.querySelector("[data-add-item]");

    if (!container || !template || !addButton) return;

    const reindex = () => {
        const rows = [...container.querySelectorAll("[data-item-row]")];
        rows.forEach((row, index) => {
            row.querySelectorAll("[data-name-template]").forEach((field) => {
                field.name = field.dataset.nameTemplate.replace("__INDEX__", index);
            });
            const removeButton = row.querySelector("[data-remove-item]");
            removeButton.disabled = rows.length === 1;
            removeButton.hidden = rows.length === 1;
        });
    };

    addButton.addEventListener("click", () => {
        container.append(template.content.cloneNode(true));
        reindex();
        container.querySelector("[data-item-row]:last-child select")?.focus();
    });

    container.addEventListener("click", (event) => {
        const button = event.target.closest("[data-remove-item]");
        if (!button || container.querySelectorAll("[data-item-row]").length === 1) return;
        button.closest("[data-item-row]").remove();
        reindex();
    });

    reindex();
});
